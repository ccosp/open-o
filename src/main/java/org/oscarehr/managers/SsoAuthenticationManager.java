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
import org.oscarehr.PMmodule.dao.ProviderDao;
import org.oscarehr.common.model.Provider;
import org.oscarehr.common.model.Security;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SSOUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import oscar.login.LoginCheckLogin;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import oscar.log.LogAction;

@Service
public class SsoAuthenticationManager implements Serializable {

    private static final Logger logger = MiscUtils.getLogger();

    private LoginCheckLogin loginCheck;

    @Autowired
    private ProviderDao providerDao;

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

        // do something with user_email here.

        /*
         * Set identity provider data:
         * Data about the IDP OSCAR will use for SSO authentication
         */
        Map<SSOUtility.SSO_SETTING, String> sso_presets = SSOUtility.getSSOPresetsFromOscarProperties();
        samlData.put("onelogin.saml2.sp.entityid", sso_presets.get(SSOUtility.SSO_SETTING.sso_sp_entity_id));
        samlData.put("onelogin.saml2.idp.entityid", sso_presets.get(SSOUtility.SSO_SETTING.sso_entity_id));
        samlData.put("onelogin.saml2.idp.single_sign_on_service.url", sso_presets.get(SSOUtility.SSO_SETTING.sso_url_login));
        samlData.put("onelogin.saml2.idp.single_logout_service.url", sso_presets.get(SSOUtility.SSO_SETTING.sso_url_logout));
        samlData.put("onelogin.saml2.idp.x509cert", sso_presets.get(SSOUtility.SSO_SETTING.sso_idp_x509cert));

        SettingsBuilder builder = new SettingsBuilder();
        Saml2Settings saml2Settings = builder.fromValues(typeCast(samlData)).build();

        return saml2Settings;
    }

    public String validateContextPath(String contextPath) {
        try {
            return SSOUtility.getRedirectUrl(contextPath).toString();
        } catch (URISyntaxException e) {
            logger.error("Context path is invalid " + contextPath, e);
        }
        return "";
    }

    public String[] checkLogin(String nameId) {
        loginCheck = new LoginCheckLogin();
        String[] providerInformation = loginCheck.ssoAuth(nameId);
        String providerNumber = providerInformation[0];
        Provider provider = getProvider(providerNumber);
        if(provider == null || (provider.getStatus() != null && provider.getStatus().equals("0"))) {
            logger.error("Provider account is missing or inactive. Provider number: " + providerNumber);
            LogAction.addLog(providerNumber, "login", "failed", "inactive");
            providerInformation = new String[0];
        }
        return providerInformation;
    }

    public Provider getProvider(String providerNumber) {
        return providerDao.getProvider(providerNumber);
    }

    public Security getSecurity() {
        return loginCheck.getSecurity();
    }

    private static HashMap<String, Object> typeCast(Properties properties) {
        Map propertiesMap = properties;
        Map<String, Object> propertiesMapCast = (Map<String, Object>) propertiesMap;
        return new HashMap<>(propertiesMapCast);
    }

}
