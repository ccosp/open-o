/**
 * Copyright (c) 2001-2002. Department of Family Medicine, McMaster University. All Rights Reserved.
 * This software is published under the GPL GNU General Public License.
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 *
 * This software was written for the
 * Department of Family Medicine
 * McMaster University
 * Hamilton
 * Ontario, Canada
 */
package org.oscarehr.managers;

import com.onelogin.saml2.settings.Saml2Settings;
import com.onelogin.saml2.settings.SettingsBuilder;
import org.apache.logging.log4j.Logger;
//import org.oscarehr.PMmodule.dao.ProviderDao;
//import org.oscarehr.common.dao.FacilityDao;
//import org.oscarehr.common.dao.ProviderPreferenceDao;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SSOUtility;
//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Service
public class SsoAuthenticationManager {

    private static final Logger logger = MiscUtils.getLogger();

//    @Autowired
//    private ProviderDao providerDao;
//    @Autowired
//    private ProviderPreferenceDao providerPreferenceDao;
//    @Autowired
//    private FacilityDao facilityDao;

    private final String samlPropertiesFile = "/onelogin.saml.properties";

    /**
     * Manager method that builds a settings object for an IDP authentication request.
     * This method is specific for use with OneLogin Tools
     *
     * @return Saml2Settings object for use with OneLogin tools.
     * @throws IOException
     */
    public Saml2Settings buildAuthenticationRequestSettings(String user_email, String context) throws IOException, URISyntaxException {

        Properties samlData = new Properties();
        try(InputStream inputStream = getClass().getResourceAsStream(samlPropertiesFile)) {
            samlData.load(inputStream);

            /*
             * Set service provider data:
             * Data about OSCAR's identifiers and links for SSO
             * See onelogin.saml.properties file in resources for details.
             */
            samlData.put("onelogin.saml2.sp.assertion_consumer_service.url",
                    new URL(SSOUtility.getLoginRedirectUrl(context)));
            samlData.put("onelogin.saml2.sp.single_logout_service.url",
                    new URL(SSOUtility.getLogoutRedirectUrl(context)));
        }

        // do something with user email here.

        /*
         * Set identity provider data:
         * Data about the IDP OSCAR will use for SSO authentication
         */
        Map<String, String> sso_presets = SSOUtility.getSSOPresetsFromOscarProperties();
        samlData.put("onelogin.saml2.sp.entityid", sso_presets.get("sso.sp.entity.id"));
        samlData.put("onelogin.saml2.idp.entityid", sso_presets.get("sso.entity.id"));
        samlData.put("onelogin.saml2.idp.single_sign_on_service.url", sso_presets.get("sso.url.login"));
        samlData.put("onelogin.saml2.idp.single_logout_service.url", sso_presets.get("sso.url.logout"));
        samlData.put("onelogin.saml2.idp.x509cert", sso_presets.get("sso.idp.x509cert"));

        SettingsBuilder builder = new SettingsBuilder();
        Saml2Settings saml2Settings = builder.fromValues(typeCast(samlData)).build();

        return saml2Settings;
    }

    private static HashMap<String, Object> typeCast(Properties properties) {
        Map propertiesMap = properties;
        Map<String, Object> propertiesMapCast = (Map<String, Object>) propertiesMap;
        return new HashMap<>(propertiesMapCast);
    }

}
