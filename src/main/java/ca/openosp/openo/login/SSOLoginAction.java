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

package ca.openosp.openo.login;

import java.io.IOException;
import java.util.*;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import ca.openosp.openo.ehrutil.LoggedInInfo;
import ca.openosp.openo.ehrutil.MiscUtils;
import ca.openosp.openo.ehrutil.SpringUtils;
import com.onelogin.saml2.Auth;
import com.onelogin.saml2.exception.Error;
import com.onelogin.saml2.exception.SettingsException;
import com.onelogin.saml2.settings.Saml2Settings;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionRedirect;
import org.apache.struts.actions.DispatchAction;
import ca.openosp.openo.PMmodule.dao.ProviderDao;
import ca.openosp.openo.PMmodule.service.ProviderManager;
import ca.openosp.openo.common.dao.FacilityDao;
import ca.openosp.openo.common.dao.ProviderPreferenceDao;
import ca.openosp.openo.common.dao.SecurityDao;
import ca.openosp.openo.common.dao.UserPropertyDAO;
import ca.openosp.openo.common.model.Security;
import ca.openosp.openo.managers.AppManager;
import ca.openosp.openo.managers.SsoAuthenticationManager;
import ca.openosp.openo.ehrutil.*;

import net.sf.json.JSONObject;
import ca.openosp.openo.OscarProperties;
import ca.openosp.openo.log.LogAction;
import ca.openosp.openo.log.LogConst;
import ca.openosp.openo.util.ParameterActionForward;

public final class SSOLoginAction extends DispatchAction {

    /**
     * This variable is only intended to be used by this class and the jsp which sets the selected facility.
     * This variable represents the queryString key used to pass the facility ID to this class.
     */
    public static final String SELECTED_FACILITY_ID = "selectedFacilityId";

    private static final Logger logger = MiscUtils.getLogger();
    private static final String LOG_PRE = "Login!@#$: ";

    private SsoAuthenticationManager ssoAuthenticationManager = SpringUtils.getBean(SsoAuthenticationManager.class);

    private ProviderManager providerManager = (ProviderManager) SpringUtils.getBean(ProviderManager.class);
    private AppManager appManager = SpringUtils.getBean(AppManager.class);
    private FacilityDao facilityDao = (FacilityDao) SpringUtils.getBean(FacilityDao.class);
    private ProviderPreferenceDao providerPreferenceDao = (ProviderPreferenceDao) SpringUtils.getBean(ProviderPreferenceDao.class);
    private ProviderDao providerDao = SpringUtils.getBean(ProviderDao.class);
    private UserPropertyDAO propDao = (UserPropertyDAO) SpringUtils.getBean(UserPropertyDAO.class);
    private SecurityDao securityDao = (SecurityDao) SpringUtils.getBean(SecurityDao.class);

    //Declares ssoKey String and actionForward String 
    private String oneIdKey = "";
    //Declares email string for the sso email
    private String oneIdEmail = "";
    private String requestStartTime;
    //Creates a new LoginCheckLogin object
    LoginCheckLogin loginCheck = new LoginCheckLogin();
    //Declares providerInformation String Array
    String[] providerInformation;
    String providerNumber = "";

    public ActionForward unspecified(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {

        String relayState = request.getParameter("RelayState");
        if (relayState != null) {
            logger.error("Relay state parameter sent to unspecified. Is the relay missing method parameters? " + relayState);
        }

        return authenticationRedirect(mapping, form, request, response);
    }

    public ActionForward econsultLogin(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws IOException {
        boolean ajaxResponse = request.getParameter("ajaxResponse") != null ? Boolean.valueOf(request.getParameter("ajaxResponse")) : false;
        ParameterActionForward actionForward = null;

        HttpSession session = request.getSession();
        LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(session);
        //Gets the loggedInProviderNumber
        String loggedInProviderNumber = loggedInInfo.getLoggedInProviderNo();
        //Sets the oneIdKey and email
        oneIdKey = request.getParameter("nameId");
        oneIdEmail = request.getParameter("email");
        requestStartTime = request.getParameter("ts");
        String encryptedOneIdToken = request.getParameter("encryptedOneIdToken");

        String signature = request.getParameter("signature");
        String ts = request.getParameter("ts");

        if (!StringUtils.isEmpty(signature)) {
            logger.info("Found signature " + signature);
            try {
                Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
                SecretKeySpec secret_key = new SecretKeySpec(OscarProperties.getInstance().getProperty("oneid.encryptionKey").getBytes("UTF-8"), "HmacSHA256");
                sha256_HMAC.init(secret_key);
                String ourSig = Hex.encodeHexString(sha256_HMAC.doFinal((oneIdKey + oneIdEmail + encryptedOneIdToken + ts).getBytes("UTF-8")));
                if (!ourSig.equals(signature)) {
                    logger.warn("SSO Login: invalid HMAC signature");
                    ActionRedirect redirect = new ActionRedirect(mapping.findForward("ssoLoginError"));
                    redirect.addParameter("errorMessage", "Invalid signature found");
                    return redirect;
                }
            } catch (Exception e) {
                MiscUtils.getLogger().error("Error", e);
            }

        } else {
            logger.warn("SSO Login: expected HMAC signature");
            ActionRedirect redirect = new ActionRedirect(mapping.findForward("ssoLoginError"));
            redirect.addParameter("errorMessage", "No signature found");
            return redirect;
        }

        String oneIdToken = null;
        if (!StringUtils.isEmpty(encryptedOneIdToken)) {
            oneIdToken = decrypt(OscarProperties.getInstance().getProperty("oneid.encryptionKey"), encryptedOneIdToken);
            logger.info("token from encryption is " + oneIdToken);
        } else {
            logger.warn("SSO Login: expected an encrypted token");
            ActionRedirect redirect = new ActionRedirect(mapping.findForward("ssoLoginError"));
            redirect.addParameter("errorMessage", "No token found");
            return redirect;
        }

        Boolean valid = isSessionValid();

        if (!valid) {
            ActionRedirect redirect = new ActionRedirect(mapping.findForward("ssoLoginError"));
            redirect.addParameter("errorMessage", "The session has timed out");
            return redirect;
        }

        try {
            providerInformation = loginCheck.ssoAuth(oneIdKey);
        } catch (Exception e) {
            logger.error("An exception has occurred while trying to login with ONE ID", e);
            String newURL = mapping.findForward("error").getPath();
            if (e.getMessage() != null && e.getMessage().startsWith("java.lang.ClassNotFoundException")) {
                newURL = newURL + "?errorMessage=Database driver " + e.getMessage().substring(e.getMessage().indexOf(':') + 2) + " not found";
            } else {
                newURL = newURL + "?errorMessage=Database connection error: " + e.getMessage() + ".";
            }

            if (ajaxResponse) {
                JSONObject json = new JSONObject();
                json.put("success", false);
                json.put("error", "Database connect error" + e.getMessage() + ".");
                response.setContentType("text/x-json");
                json.write(response.getWriter());
                return null;
            }

            return (new ActionForward(newURL));
        }


        if (providerInformation != null && providerInformation.length != 1) {
            logger.info("providerInformation : " + Arrays.toString(providerInformation));
            providerNumber = providerInformation[0];

            //Compares the logged in provider number with the provider number that matched the oneIdKey
            if (loggedInProviderNumber.equals(providerNumber)) {
                //Sets the oneIdEmail session attribute
                session.setAttribute("oneIdEmail", oneIdEmail);
                session.setAttribute("oneid_token", oneIdToken);

                if (providerInformation[6] != null && !providerInformation[6].equals("")) {
                    session.setAttribute("delegateOneIdEmail", providerInformation[6]);
                }

                String operation = request.getParameter("operation");
                if (operation != null && "launch".equals(operation)) {
                    ActionForward af = new ActionForward();
                    af.setPath("/clinicalConnectEHRViewer.do");
                    actionForward = new ParameterActionForward(af);
                    actionForward.addParameter("method", "launchNonPatientContext");
                } else {
                    actionForward = new ParameterActionForward(mapping.findForward("success"));
                }
            } else {
                actionForward = new ParameterActionForward(mapping.findForward("error"));
                actionForward.addParameter("errorMessage", "A different OSCAR account is linked to the ONE ID account");
            }
        } else {
            //If the key does not match a user, link the key to the ONE ID account

            //If the ondIdKey parameter is not null and is not an empty string
            if (oneIdKey != null && !oneIdKey.equals("")) {
                //Gets the logged in provider's number to link the ONE ID key
                Security securityRecord = securityDao.getByProviderNo(loggedInProviderNumber);
                //If a provider was found
                if (securityRecord != null) {
                    //Checks if the oneIdKey linked to the security record doesn't exist
                    if (securityRecord.getOneIdKey() == null || securityRecord.getOneIdKey().equals("")) {
                        //Updates the ONE ID Key for the security record
                        securityRecord.setOneIdKey(oneIdKey);
                        securityRecord.setOneIdEmail(oneIdEmail);

                        securityDao.updateOneIdKey(securityRecord);

                        //Logs the linking of the key
                        logger.info("Linked ONE ID Key " + oneIdKey + " to provider " + loggedInProviderNumber);
                        //Sets the oneIdEmail session attribute
                        session.setAttribute("oneIdEmail", oneIdEmail);

                        if (securityRecord.getDelagateOneIdEmail() != null && !securityRecord.getDelagateOneIdEmail().equals("")) {
                            session.setAttribute("delegateOneIdEmail", securityRecord.getDelagateOneIdEmail());
                        }

                        //Sets the actionForward to success
                        actionForward = new ParameterActionForward(mapping.findForward("success"));
                    } else {
                        logger.error("The account for provider number " + providerNumber + " already has a ONE ID account associated with it");
                        //Routes failure and sets a message parameter to alert the user.
                        actionForward = new ParameterActionForward(mapping.findForward("error"));
                        actionForward.addParameter("errorMessage", "The account for provider number " + providerNumber + " already has a ONE ID account associated with it");
                    }
                }
            } else {
                logger.error("oneIdKey is null");
                actionForward = new ParameterActionForward(mapping.findForward("error"));
                actionForward.addParameter("errorMessage", "The OneIDKey that was returned is invalid.");
            }
        }
        return actionForward;
    }

    public ActionForward ssoLogout(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        List<String> errors = Collections.emptyList();
        try {
            Auth auth = new Auth(request, response);
            auth.processSLO();
            errors = auth.getErrors();
            if (errors.isEmpty()) {
                logger.info("Successfully logged out");
            } else {
                for (String error : errors) {
                    logger.error(error);
                }
            }
        } catch (Exception e) {
            logger.error("Exception during logout. Errors:  " + errors, e);
        }
        return mapping.findForward("logout");
    }

    public ActionForward ssoLogin(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {

//		String relayState = request.getParameter("RelayState");

        String user_email = request.getParameter("user_email");
        String context = request.getRequestURL().toString();
        String destination = "ssoLoginError";
        List<String> errors = Collections.emptyList();
        Auth auth = null;
        String contextPath = request.getContextPath();
        String ipAddress = request.getRemoteAddr();

        logger.debug("Context path validated: " + contextPath);

        try {
            Saml2Settings saml2Settings = ssoAuthenticationManager.buildAuthenticationRequestSettings(user_email, context);
            auth = new Auth(saml2Settings, request, response);
            auth.processResponse();
        } catch (IOException e) {
            logger.error("Found IOException ", e);
        } catch (SettingsException e) {
            logger.error("Found exception error in settings: " + auth == null ? "" : auth.getSettings(), e);
        } catch (Error e) {
            logger.error("Found exception error in SAML response ", e);
        } catch (Exception e) {
            logger.error("Found unexpected exception", e);
        }

        if (auth != null) {

            errors = auth.getErrors();

            if (!errors.isEmpty()) {
                logger.error("Errors found while attempting to authenticate SSO response: " + StringUtils.join(errors, ", "));

                String errorReason = auth.getLastErrorReason();
                if (errorReason != null && !errorReason.isEmpty()) {
                    logger.error("Possible reason for last error " + auth.getLastErrorReason());
                }

            } else if (auth.isAuthenticated()) {

//				if (relayState != null) {
//					//					&& relayState != ServletUtils.getSelfRoutedURLNoQuery(request)) {
//
//					// usually means redirect to auth request if this is not set??
//					logger.error("SSO relay state is set: " + relayState);
//
//					if (attributes.isEmpty()) {
//						logger.error("No authentication response attributes found: " + StringUtils.join(attributes, ", "));
//						destination = "ssoLoginError";
//					} else {
//						Collection<String> keys = attributes.keySet();
//						for (String name : keys) {
//							logger.info(name);
//							List<String> values = attributes.get(name);
//							for (String value : values) {
//								logger.info(" - " + value);
//							}
//						}
//
//
//					}
//				}

                Map<String, Object> sessionData = ssoAuthenticationManager.checkSSOLogin(auth);

                if (sessionData != null && !sessionData.isEmpty()) {

                    HttpSession session = request.getSession(false);
                    if (session != null) {
                        if (request.getParameter("invalidate_session") != null && request.getParameter("invalidate_session").equals("false")) {
                            //don't invalidate in this case, it messes up authenticity of OAUTH
                            logger.debug("Session NOT invalidated: " + session.getId());
                        } else {
                            session.invalidate();
                            logger.debug("Session invalidated: " + session.getId());
                        }
                    }

                    // Create a new session for this user
                    HttpSession newSession = request.getSession(true);

                    logger.debug("New session created: " + newSession.getId());

                    newSession.setMaxInactiveInterval(7200);
                    newSession.setAttribute("oscar_context_path", contextPath);

                    // full site or mobile
                    newSession.setAttribute("fullSite", "true");

                    for (String key : sessionData.keySet()) {
                        newSession.setAttribute(key, sessionData.get(key));
                    }

                    if (logger.isDebugEnabled()) {
                        logger.debug("New session validated: ");
                        Enumeration<String> sessionAttributeNames = newSession.getAttributeNames();
                        while (sessionAttributeNames.hasMoreElements()) {
                            String attributeName = sessionAttributeNames.nextElement();
                            logger.debug("=> " + attributeName + ":" + newSession.getAttribute(attributeName));
                        }
                        logger.debug("SSO login updated forwarding to provider schedule.");
                    }

                    destination = "provider";

                    LogAction.addLog((String) newSession.getAttribute("user"), LogConst.LOGIN, LogConst.CON_LOGIN, "", ipAddress);
                } else {
                    logger.error("OSCAR SSO login failed");
                    // find out why.
                }
            } else {
                logger.error("SSO authentication failed: " + StringUtils.join(errors, ", "));
                logger.debug("SSO authentication settings: " + auth.getSettings());
            }
        } // end if Auth != null. An exception will be thrown before this point.

        //TODO add something about ajax here.

        ActionRedirect redirect = new ActionRedirect(mapping.findForward(destination));
        // additional error message parameters here.
        return redirect;
    }

    /**
     * A basic Action method to redirect a user login to the SSO Identity Provider when
     * SSO authentication is enabled in the openo.properties file.
     * This method sends an authentication request to the IDP and then handles the
     * reply.
     */
    public ActionForward authenticationRedirect(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
        String user_email = request.getParameter("user_email");
        String context = request.getRequestURL().toString();
        String relayState = request.getParameter("RelayState");

        if (relayState != null) {
            logger.error("Should the relay state parameter be present while redirecting for SSO authentication? " + relayState);
        }

        try {
            Saml2Settings saml2Settings = ssoAuthenticationManager.buildAuthenticationRequestSettings(user_email, context);
            Auth auth = new Auth(saml2Settings, request, response);
            auth.login();
        } catch (Exception e) {
            // handle error
            logger.error("Error during authentication redirect ", e);
        }

        // return to meaningfull error page.
        return null;
    }

    private Boolean isSessionValid() {
        Boolean isSessionValid = true;
        Integer timeout = Integer.parseInt(OscarProperties.getInstance().getProperty("econsultLoginTimeout"));

        try {
            Long startTime = Long.parseLong(requestStartTime);
            Long currentTime = new Date().getTime() / 1000;

            isSessionValid = (currentTime - startTime) < timeout;
        } catch (NumberFormatException e) {
            logger.error("The property econsultLoginTimeout or the requestStartTime query string does not exist or is not a valid integer", e);
        }

        return isSessionValid;
    }

    public static String decrypt(String key, String data) {

        try {
            String[] parts = data.split(":");

            IvParameterSpec iv = new IvParameterSpec(Base64.decodeBase64(parts[1]));
            //    IvParameterSpec iv = new IvParameterSpec(java.ehrutil.Base64.getDecoder().decode(parts[1]));
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);

            byte[] decodedEncryptedData = java.util.Base64.getDecoder().decode(parts[0]);

            byte[] original = cipher.doFinal(decodedEncryptedData);

            return new String(original);
        } catch (Exception ex) {
            logger.error("Error", ex);
            return null;
        }
    }


}
