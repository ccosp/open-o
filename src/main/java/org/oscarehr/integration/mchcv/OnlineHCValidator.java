//CHECKSTYLE:OFF
/**
 * Copyright (c) 2001-2002. Department of Family Medicine, McMaster University. All Rights Reserved.
 * This software is published under the GPL GNU General Public License.
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 * <p>
 * This software was written for the
 * Department of Family Medicine
 * McMaster University
 * Hamilton
 * Ontario, Canada
 */
package org.oscarehr.integration.mchcv;

import ca.ontario.health.ebs.EbsFault;
import ca.ontario.health.hcv.Faultexception;
import ca.ontario.health.hcv.HCValidation;
import ca.ontario.health.hcv.HcvRequest;
import ca.ontario.health.hcv.HcvResults;
import ca.ontario.health.hcv.Requests;

import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.xml.soap.SOAPFault;
import javax.xml.ws.soap.SOAPFaultException;

import org.apache.logging.log4j.Logger;
import org.oscarehr.integration.ebs.client.EdtClientBuilder;
import org.oscarehr.integration.ebs.client.EdtClientBuilderConfig;
import org.oscarehr.util.MiscUtils;

import ca.openosp.openo.OscarProperties;

public class OnlineHCValidator implements HCValidator {
    private static Logger logger = MiscUtils.getLogger();

    private HCValidation validation;
    private EdtClientBuilder builder;

    public OnlineHCValidator() {
        OscarProperties properties = OscarProperties.getInstance();
        EdtClientBuilderConfig config = new EdtClientBuilderConfig();
        config.setLoggingRequired(!Boolean.valueOf(properties.getProperty("hcv.logging.skip")));
        config.setKeystoreUser(properties.getProperty("hcv.keystore.user"));
        config.setKeystorePassword(properties.getProperty("hcv.keystore.pass"));
        config.setUserNameTokenUser(properties.getProperty("hcv.service.user"));
        config.setUserNameTokenPassword(properties.getProperty("hcv.service.pass"));
        config.setServiceUrl(properties.getProperty("hcv.service.url"));
        config.setConformanceKey(properties.getProperty("hcv.service.conformanceKey"));
        config.setServiceId(properties.getProperty("hcv.service.id"));

        setBuilder(new EdtClientBuilder(config));
        setExternalClientKeystoreFilename(properties.getProperty("mcedt.service.clientKeystore.properties"));
        validation = builder.build(HCValidation.class);
    }

    @Override
    public HCValidationResult validate(String healthCardNumber, String versionCode) {
        return validate(healthCardNumber, versionCode, null);
    }

    @Override
    public HCValidationResult validate(String healthCardNumber, String versionCode, String serviceCode) {
        Requests requests = new Requests();
        HcvRequest request = new HcvRequest();
        request.setHealthNumber(healthCardNumber);
        request.setVersionCode(versionCode);
        if (serviceCode != null && !serviceCode.isEmpty()) {
            request.getFeeServiceCodes().add(serviceCode);
        }
        requests.getHcvRequest().add(request);
        HcvResults results = null;
        HCValidationResult result = null;
        try {
            results = validate(requests, "en");
            if (results != null) {
                result = HCValidator.createSingleResult(results, 0);
            }
        } catch (Faultexception e) {
            result = new HCValidationResult();
            result.setEbsFault(e.getFaultInfo());
            result.setResponseCode(NOT_VALID_RESPONSE_CODE);
        }

        return result;
    }

    @Override
    public HcvResults validate(Requests requests, String local) throws Faultexception {
        HcvResults results;
        try {
            results = validation.validate(requests, local);
        } catch (SOAPFaultException sfx) {
            SOAPFault soapFault = sfx.getFault();
            EbsFault ebsFault = new EbsFault();
            ebsFault.setCode(soapFault.getFaultCode());
            ebsFault.setMessage(soapFault.getFaultString());
            throw new Faultexception("", ebsFault);
        }
        return results;
    }

    public EdtClientBuilder getBuilder() {
        return builder;
    }

    private void setBuilder(EdtClientBuilder builder) {
        this.builder = builder;
    }

    /*
     * Set an external `clientKeystore.properties` by providing the path to the file.
     * If the path is not provided, it will default to `src/main/resources/clientKeystore.properties`.
     */
    private static void setExternalClientKeystoreFilename(String clientKeystorePropertiesPath) {
        if (clientKeystorePropertiesPath == null) {
            return;
        }
        Path signaturePropFile = Paths.get(clientKeystorePropertiesPath);
        if (Files.exists(signaturePropFile)) {
            File file = new File(clientKeystorePropertiesPath);
            try {
                EdtClientBuilder.setClientKeystoreFilename(file.toURI().toURL().toString());
            } catch (MalformedURLException e) {
                logger.error("Malformed URL: " + clientKeystorePropertiesPath, e);
            }
        }
    }

}
