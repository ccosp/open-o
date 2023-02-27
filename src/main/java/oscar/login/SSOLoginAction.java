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

package oscar.login;

import java.io.IOException;
import java.util.*;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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
import org.oscarehr.PMmodule.dao.ProviderDao;
import org.oscarehr.PMmodule.service.ProviderManager;
import org.oscarehr.common.dao.FacilityDao;
import org.oscarehr.common.dao.ProviderPreferenceDao;
import org.oscarehr.common.dao.SecurityDao;
import org.oscarehr.common.dao.UserPropertyDAO;
import org.oscarehr.common.model.Security;
import org.oscarehr.managers.AppManager;
import org.oscarehr.managers.SsoAuthenticationManager;
import org.oscarehr.util.*;

import net.sf.json.JSONObject;
import oscar.OscarProperties;
import oscar.log.LogAction;
import oscar.log.LogConst;
import oscar.util.ParameterActionForward;

public final class SSOLoginAction extends DispatchAction {
	
	/**
	 * This variable is only intended to be used by this class and the jsp which sets the selected facility.
	 * This variable represents the queryString key used to pass the facility ID to this class.
	 */
    public static final String SELECTED_FACILITY_ID="selectedFacilityId";

    private static final Logger logger = MiscUtils.getLogger();
    private static final String LOG_PRE = "Login!@#$: ";

	private SsoAuthenticationManager ssoAuthenticationManager = SpringUtils.getBean(SsoAuthenticationManager.class);
    
    private ProviderManager providerManager = (ProviderManager) SpringUtils.getBean("providerManager");
    private AppManager appManager = SpringUtils.getBean(AppManager.class);
    private FacilityDao facilityDao = (FacilityDao) SpringUtils.getBean("facilityDao");
    private ProviderPreferenceDao providerPreferenceDao = (ProviderPreferenceDao) SpringUtils.getBean("providerPreferenceDao");
    private ProviderDao providerDao = SpringUtils.getBean(ProviderDao.class);
    private UserPropertyDAO propDao =(UserPropertyDAO)SpringUtils.getBean("UserPropertyDAO");
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
	
    public ActionForward econsultLogin(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)  throws IOException {
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
        
        if(!StringUtils.isEmpty(signature)) {
        	logger.info("Found signature " + signature);
        	try {
        		Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        		SecretKeySpec secret_key = new SecretKeySpec(OscarProperties.getInstance().getProperty("oneid.encryptionKey").getBytes("UTF-8"), "HmacSHA256");
        		sha256_HMAC.init(secret_key);
        		String ourSig = Hex.encodeHexString(sha256_HMAC.doFinal((oneIdKey + oneIdEmail + encryptedOneIdToken + ts).getBytes("UTF-8")));
        		if(!ourSig.equals(signature)) {
        			logger.warn("SSO Login: invalid HMAC signature");
                	ActionRedirect redirect = new ActionRedirect(mapping.findForward("ssoLoginError"));
                    redirect.addParameter("errorMessage", "Invalid signature found");
                    return redirect;
        		}
        	}catch(Exception e) {
        		MiscUtils.getLogger().error("Error",e);
        	}
        	
        } else {
        	logger.warn("SSO Login: expected HMAC signature");
        	ActionRedirect redirect = new ActionRedirect(mapping.findForward("ssoLoginError"));
            redirect.addParameter("errorMessage", "No signature found");
            return redirect;
        }
        
        String oneIdToken = null;
        if(!StringUtils.isEmpty(encryptedOneIdToken)) {
        	oneIdToken = decrypt(OscarProperties.getInstance().getProperty("oneid.encryptionKey"),encryptedOneIdToken);
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
        }
        catch (Exception e) {
        	logger.error("An exception has occurred while trying to login with ONE ID", e);
        	String newURL = mapping.findForward("error").getPath();
        	if (e.getMessage() != null && e.getMessage().startsWith("java.lang.ClassNotFoundException")) {
        		newURL = newURL + "?errorMessage=Database driver " + e.getMessage().substring(e.getMessage().indexOf(':') + 2) + " not found";
        	}
        	else {
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
        	
        	return(new ActionForward(newURL));
        }
        
        
        if (providerInformation != null && providerInformation.length != 1) {
        	logger.info("providerInformation : " + Arrays.toString(providerInformation));
        	providerNumber = providerInformation[0];
        	
        	//Compares the logged in provider number with the provider number that matched the oneIdKey 
        	if (loggedInProviderNumber.equals(providerNumber)){
        		//Sets the oneIdEmail session attribute
    			session.setAttribute("oneIdEmail", oneIdEmail);
    			session.setAttribute("oneid_token", oneIdToken );
    			 
    			if (providerInformation[6] != null && !providerInformation[6].equals("")) {
                    session.setAttribute("delegateOneIdEmail", providerInformation[6]);
                }
    			
    			String operation = request.getParameter("operation");
    			if(operation != null && "launch".equals(operation)) {
    				ActionForward af = new ActionForward();
    				af.setPath("/clinicalConnectEHRViewer.do");
    				actionForward = new ParameterActionForward(af);
    				actionForward.addParameter("method", "launchNonPatientContext");
    			} else {
    				actionForward = new ParameterActionForward(mapping.findForward("success"));
    			}
        	}
        	else {
        		actionForward = new ParameterActionForward(mapping.findForward("error"));
        		actionForward.addParameter("errorMessage", "A different OSCAR account is linked to the ONE ID account");
        	}
        }
        else {
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
	        		}
	        		else {
	        			logger.error("The account for provider number " + providerNumber + " already has a ONE ID account associated with it");
	        			//Routes failure and sets a message parameter to alert the user.
	        			actionForward = new ParameterActionForward(mapping.findForward("error"));
	        			actionForward.addParameter("errorMessage", "The account for provider number " + providerNumber + " already has a ONE ID account associated with it");
	        		}
        		}        		
        	}
        	else {
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
				for(String error : errors) {
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

		if(auth != null ) {

			errors = auth.getErrors();

			if (! errors.isEmpty()) {
				logger.error("Errors found while attempting to authenticate SSO response: " + StringUtils.join(errors, ", "));

				String errorReason = auth.getLastErrorReason();
				if (errorReason != null && !errorReason.isEmpty()) {
					logger.error("Possible reason for last error " + auth.getLastErrorReason());
				}

			} else if( auth.isAuthenticated() ) {

				Map<String, List<String>> attributes = auth.getAttributes();
				String nameId = auth.getNameId();
				String nameIdFormat = auth.getNameIdFormat();
				String sessionIndex = auth.getSessionIndex();
				String nameidNameQualifier = auth.getNameIdNameQualifier();
				String nameidSPNameQualifier = auth.getNameIdSPNameQualifier();

				//			String email = auth.getAttribute("email");
				//			String oneIdToken = auth.getAttribute("encryptedOneIdToken");

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

				String[] providerInformation = ssoAuthenticationManager.checkLogin(nameId);

				if (providerInformation != null && providerInformation.length < 0) {

					HttpSession session = request.getSession(false);
					if (session != null) {
						if (request.getParameter("invalidate_session") != null && request.getParameter("invalidate_session").equals("false")) {
							//don't invalidate in this case, it messes up authenticity of OAUTH
						} else {
							session.invalidate();
						}
					}

					// Create a new session for this user
					session = request.getSession();

					/* add session attributes;
					 * NOTE: these are the ONLY attributes we add to a session.
					 * NO provider preferences here.
					 */
					session.setAttribute("attributes", attributes);
					session.setAttribute("nameId", nameId);
					session.setAttribute("nameIdFormat", nameIdFormat);
					session.setAttribute("sessionIndex", sessionIndex);
					session.setAttribute("nameidNameQualifier", nameidNameQualifier);
					session.setAttribute("nameidSPNameQualifier", nameidSPNameQualifier);

					session.setAttribute("user", providerInformation[0]);
					session.setAttribute("userfirstname", providerInformation[1]);
					session.setAttribute("userlastname", providerInformation[2]);
					session.setAttribute("userrole", providerInformation[4]);
					session.setAttribute("oscar_context_path", ssoAuthenticationManager.validateContextPath(request.getContextPath()));
					session.setAttribute("expired_days", providerInformation[5]);

					//				session.setAttribute("oneIdEmail", email);
					//				session.setAttribute("oneid_token", oneIdToken);

					// this will set ONLY if the user login checks out from ssoAuthenticationManager.checkLogin(nameId)
					session.setAttribute(SessionConstants.LOGGED_IN_SECURITY, ssoAuthenticationManager.getSecurity());

					// only the provider class info here.  Nothing more.
					session.setAttribute(SessionConstants.LOGGED_IN_PROVIDER, ssoAuthenticationManager.getProvider(providerInformation[0]));

					if (providerInformation[6] != null && !providerInformation[6].equals("")) {
						session.setAttribute("delegateOneIdEmail", providerInformation[6]);
					}

					destination = "provider";

					LogAction.addLog(providerInformation[0], LogConst.LOGIN, LogConst.CON_LOGIN, "", request.getRemoteAddr());
				}
			} else {
				logger.error("SSO authentication failed: " + StringUtils.join(errors, ", "));
				logger.debug("SSO authentication settings: " + auth.getSettings());
			}
		} // end if Auth != null. An exception will be thrown before this point.

		ActionRedirect redirect = new ActionRedirect(mapping.findForward(destination));
		// additional error message parameters here.
		return redirect;
	}

//
//    	boolean ajaxResponse = request.getParameter("ajaxResponse") != null ? Boolean.valueOf(request.getParameter("ajaxResponse")) : false;
//    	//Gets the IP address
//    	String ip = request.getRemoteAddr();
//    	//Gets the isMobileOptimized attribute
//    	Boolean isMobileOptimized = request.getSession().getAttribute("mobileOptimized") != null;
//    	String destination = "failure";
//    	//Declares providerInformation String Array
//    	String[] providerInformation;
//    	String providerNumber = "";
//
//    	//Gets the ssoKey parameter
//    	oneIdKey = request.getParameter("nameId");
//    	oneIdEmail = request.getParameter("email");
//
//		// are these optional parameters demanded by Ontario OneId
//        requestStartTime = request.getParameter("ts");
//	    String ts = request.getParameter("ts");
//        String encryptedOneIdToken = request.getParameter("encryptedOneIdToken");
//
//		// azure AD user apn is required. "user email"
//
//	    String signature = request.getParameter("signature");
//
//        if(!StringUtils.isEmpty(signature)) {
//        	logger.info("Found signature " + signature);
//        	try {
//        		Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
//        		SecretKeySpec secret_key = new SecretKeySpec(OscarProperties.getInstance().getProperty("oneid.encryptionKey").getBytes("UTF-8"), "HmacSHA256");
//        		sha256_HMAC.init(secret_key);
//        		String ourSig = Hex.encodeHexString(sha256_HMAC.doFinal((oneIdKey + oneIdEmail + encryptedOneIdToken + ts).getBytes("UTF-8")));
//        		if(!ourSig.equals(signature)) {
//        			logger.warn("SSO Login: invalid HMAC signature");
//                	ActionRedirect redirect = new ActionRedirect(mapping.findForward("ssoLoginError"));
//                    redirect.addParameter("errorMessage", "Invalid signature found");
//                    return redirect;
//        		}
//        	}catch(Exception e) {
//        		MiscUtils.getLogger().error("Error",e);
//        	}
//
//        } else {
//        	logger.warn("SSO Login: expected HMAC signature");
//        	ActionRedirect redirect = new ActionRedirect(mapping.findForward("ssoLoginError"));
//            redirect.addParameter("errorMessage", "No signature found");
//            return redirect;
//        }
//
//        String oneIdToken = null;
//        if(!StringUtils.isEmpty(encryptedOneIdToken)) {
//        	oneIdToken = decrypt(OscarProperties.getInstance().getProperty("oneid.encryptionKey"),encryptedOneIdToken);
//        	logger.info("token from encryption is " + oneIdToken);
//        } else {
//        	logger.warn("SSO Login: expected an encrypted token");
//        	ActionRedirect redirect = new ActionRedirect(mapping.findForward("ssoLoginError"));
//            redirect.addParameter("errorMessage", "No token found");
//            return redirect;
//        }
//
//        Boolean valid = isSessionValid();
//
//        if (!valid) {
//            ActionRedirect redirect = new ActionRedirect(mapping.findForward("ssoLoginError"));
//            redirect.addParameter("errorMessage", "The session has timed out");
//            return redirect;
//        }
//
//        if(oneIdToken == null) {
//        	ActionRedirect redirect = new ActionRedirect(mapping.findForward("ssoLoginError"));
//            redirect.addParameter("errorMessage", "No valid token found");
//            return redirect;
//        }
//
//    	//Creates a new LoginCheckLogin object
//        LoginCheckLogin loginCheck = new LoginCheckLogin();
//
//        try {
//        	providerInformation = loginCheck.ssoAuth(oneIdKey);
//        }
//        catch (Exception e) {
//        	logger.error("An exception has occurred while trying to login with SSO", e);
//        	String newURL = mapping.findForward("error").getPath();
//        	if (e.getMessage() != null && e.getMessage().startsWith("java.lang.ClassNotFoundException")) {
//        		newURL = newURL + "?errormsg=Database driver " + e.getMessage().substring(e.getMessage().indexOf(':') + 2) + " not found";
//        	}
//        	else {
//        		newURL = newURL + "?errormsg=Database connection error: " + e.getMessage() + ".";
//        	}
//
//        	if (ajaxResponse) {
//        		JSONObject json = new JSONObject();
//        		json.put("success", false);
//        		json.put("error", "Database connect error" + e.getMessage() + ".");
//        		response.setContentType("text/x-json");
//        		json.write(response.getWriter());
//        		return null;
//        	}
//
//        	return(new ActionForward(newURL));
//        }
//
//        logger.error("providerInformation : " + Arrays.toString(providerInformation));
//        if (providerInformation != null && providerInformation.length != 1) {
//        	providerNumber = providerInformation[0];
//        	//Checks if the provider is inactive
//        	ProviderDao providerDao = SpringUtils.getBean(ProviderDao.class);
//        	Provider p = providerDao.getProvider(providerNumber);
//
//        	if(p == null || (p.getStatus() != null && p.getStatus().equals("0"))) {
//            	logger.info(LOG_PRE + " Inactive: " + providerNumber);
//            	LogAction.addLog(providerNumber, "login", "failed", "inactive");
//
//                String newURL = mapping.findForward("error").getPath();
//                newURL = newURL + "?errormsg=Your account is inactive. Please contact your administrator to activate.";
//                return(new ActionForward(newURL));
//            }
//
//        	// invalidate the existing session
//            HttpSession session = request.getSession(false);
//            if (session != null) {
//            	if(request.getParameter("invalidate_session") != null && request.getParameter("invalidate_session").equals("false")) {
//            		//don't invalidate in this case..messes up authenticity of OAUTH
//            	} else {
//            		session.invalidate();
//            	}
//            }
//            session = request.getSession(); // Create a new session for this user
//
//            logger.debug("Assigned new session for: " + providerInformation[0] + " : " + providerInformation[3] + " : " + providerInformation[4]);
//            LogAction.addLog(providerInformation[0], LogConst.LOGIN, LogConst.CON_LOGIN, "", ip);
//
//            // initial db setting
//            Properties pvar = OscarProperties.getInstance();
//
//
//            String providerNo = providerInformation[0];
//            session.setAttribute("user", providerInformation[0]);
//            session.setAttribute("userfirstname", providerInformation[1]);
//            session.setAttribute("userlastname", providerInformation[2]);
//            session.setAttribute("userrole", providerInformation[4]);
//            session.setAttribute("oscar_context_path", request.getContextPath());
//            session.setAttribute("expired_days", providerInformation[5]);
//            session.setAttribute("oneIdEmail", oneIdEmail);
//            session.setAttribute("oneid_token", oneIdToken );
//            if (providerInformation[6] != null && !providerInformation[6].equals("")) {
//                session.setAttribute("delegateOneIdEmail", providerInformation[6]);
//            }
//
//            // If a new session has been created, we must set the mobile attribute again
//            if (isMobileOptimized) session.setAttribute("mobileOptimized","true");
//            // initiate security manager
//            String default_pmm = null;
//
//         // get preferences from preference table
//        	ProviderPreference providerPreference=providerPreferenceDao.find(providerNo);
//
//        	if (providerPreference==null) providerPreference=new ProviderPreference();
//
//        	session.setAttribute(SessionConstants.LOGGED_IN_PROVIDER_PREFERENCE, providerPreference);
//
//            if (org.oscarehr.common.IsPropertiesOn.isCaisiEnable()) {
//            	String tklerProviderNo = null;
//            	UserProperty prop = propDao.getProp(providerNo, UserProperty.PROVIDER_FOR_TICKLER_WARNING);
//        		if (prop == null) {
//        			tklerProviderNo = providerNo;
//        		} else {
//        			tklerProviderNo = prop.getValue();
//        		}
//            	session.setAttribute("tklerProviderNo",tklerProviderNo);
//
//                session.setAttribute("newticklerwarningwindow", providerPreference.getNewTicklerWarningWindow());
//                session.setAttribute("default_pmm", providerPreference.getDefaultCaisiPmm());
//                session.setAttribute("caisiBillingPreferenceNotDelete", String.valueOf(providerPreference.getDefaultDoNotDeleteBilling()));
//
//                default_pmm = providerPreference.getDefaultCaisiPmm();
//                @SuppressWarnings("unchecked")
//                ArrayList<String> newDocArr = (ArrayList<String>)request.getSession().getServletContext().getAttribute("CaseMgmtUsers");
//                if("enabled".equals(providerPreference.getDefaultNewOscarCme())) {
//                	newDocArr.add(providerNo);
//                	session.setAttribute("CaseMgmtUsers", newDocArr);
//                }
//            }
//            session.setAttribute("starthour", providerPreference.getStartHour().toString());
//            session.setAttribute("endhour", providerPreference.getEndHour().toString());
//            session.setAttribute("everymin", providerPreference.getEveryMin().toString());
//            session.setAttribute("groupno", providerPreference.getMyGroupNo());
//
//            destination = "provider";
//
//            if (destination.equals("provider") && default_pmm != null && "enabled".equals(default_pmm)) {
//            	destination = "caisiPMM";
//            }
//
//            if (destination.equals("provider") && OscarProperties.getInstance().getProperty("useProgramLocation", "false").equals("true") ) {
//            	destination = "programLocation";
//            }
//
//            String quatroShelter = OscarProperties.getInstance().getProperty("QUATRO_SHELTER");
//            if(quatroShelter!= null && quatroShelter.equals("on")) {
//            	destination = "shelterSelection";
//            }
//
//            //Lazy Loads AlertTimer instance only once, will run as daemon for duration of server runtime
//            if (pvar.getProperty("billregion").equals("BC")) {
//                String alertFreq = pvar.getProperty("ALERT_POLL_FREQUENCY");
//                if (alertFreq != null) {
//                    Long longFreq = new Long(alertFreq);
//                    String[] alertCodes = OscarProperties.getInstance().getProperty("CDM_ALERTS").split(",");
//                    AlertTimer.getInstance(alertCodes, longFreq.longValue());
//                }
//            }
//
//            String username = (String) session.getAttribute("user");
//            Provider provider = providerManager.getProvider(username);
//            session.setAttribute(SessionConstants.LOGGED_IN_PROVIDER, provider);
//            session.setAttribute(SessionConstants.LOGGED_IN_SECURITY, loginCheck.getSecurity());
//
//            LoggedInInfo loggedInInfo = LoggedInUserFilter.generateLoggedInInfoFromSession(request);
//
//            if (destination.equals("provider")) {
//                UserProperty drugrefProperty = propDao.getProp(UserProperty.MYDRUGREF_ID);
//                if (drugrefProperty != null || appManager.isK2AUser(loggedInInfo)) {
//                    DSService service =   SpringUtils.getBean(DSService.class);
//                    service.fetchGuidelinesFromServiceInBackground(loggedInInfo);
//                }
//            }
//
//		    MyOscarUtils.attemptMyOscarAutoLoginIfNotAlreadyLoggedIn(loggedInInfo, true);
//
//            List<Integer> facilityIds = providerDao.getFacilityIds(provider.getProviderNo());
//            if (facilityIds.size() > 1) {
//                return(new ActionForward("/select_facility.jsp?nextPage=" + destination));
//            }
//            else if (facilityIds.size() == 1) {
//                // set current facility
//                Facility facility=facilityDao.find(facilityIds.get(0));
//                request.getSession().setAttribute("currentFacility", facility);
//                LogAction.addLog(providerInformation[0], LogConst.LOGIN, LogConst.CON_LOGIN, "facilityId="+facilityIds.get(0), ip);
//                if(facility.isEnableOcanForms()) {
//                	request.getSession().setAttribute("ocanWarningWindow", OcanForm.getOcanWarningMessage(facility.getId()));
//                }
//                if(facility.isEnableCbiForm()) {
//                	request.getSession().setAttribute("cbiReminderWindow", CBIUtil.getCbiSubmissionFailureWarningMessage(facility.getId(),provider.getProviderNo() ));
//                }
//            }
//            else {
//        		List<Facility> facilities = facilityDao.findAll(true);
//        		if(facilities!=null && facilities.size()>=1) {
//        			Facility fac = facilities.get(0);
//        			int first_id = fac.getId();
//        			ProviderDao.addProviderToFacility(providerNo, first_id);
//        			Facility facility=facilityDao.find(first_id);
//        			request.getSession().setAttribute("currentFacility", facility);
//        			LogAction.addLog(providerInformation[0], LogConst.LOGIN, LogConst.CON_LOGIN, "facilityId="+first_id, ip);
//            	}
//            }
//
//        }
//        else {
//        	//If the key does not match a user, return the key asking the user to input the OSCAR credentials of the account to tie it to.
//        	logger.info("Key does not match to an account, prompting for OSCAR credentials.");
//
//            if(ajaxResponse) {
//            	JSONObject json = new JSONObject();
//            	json.put("success", false);
//            	response.setContentType("text/x-json");
//            	json.put("oneIdKey", oneIdKey);
//            	json.write(response.getWriter());
//            	return null;
//            }
//            destination = "ssoLoginError";
//            ParameterActionForward forward = new ParameterActionForward(mapping.findForward(destination));
//            forward.addParameter("nameId", oneIdKey);
//            forward.addParameter("email", oneIdEmail);
//            return forward;
//        }
//
//    	logger.debug("checking oauth_token");
//        if(request.getParameter("oauth_token") != null) {
//    		String proNo = (String)request.getSession().getAttribute("user");
//    		ServiceRequestTokenDao serviceRequestTokenDao = SpringUtils.getBean(ServiceRequestTokenDao.class);
//    		ServiceRequestToken srt = serviceRequestTokenDao.findByTokenId(request.getParameter("oauth_token"));
//    		if(srt != null) {
//    			srt.setProviderNo(proNo);
//    			serviceRequestTokenDao.merge(srt);
//    		}
//    	}
//
//        if(ajaxResponse) {
//        	logger.debug("rendering ajax response");
//        	Provider prov = providerDao.getProvider((String)request.getSession().getAttribute("user"));
//        	JSONObject json = new JSONObject();
//        	json.put("success", true);
//        	json.put("providerName", prov.getFormattedName());
//        	json.put("providerNo", prov.getProviderNo());
//        	response.setContentType("text/x-json");
//        	json.write(response.getWriter());
//        	return null;
//        }
//
//    	logger.debug("rendering standard response : "+ destination);
//
//        //Returns the destination ActionForward
//    	return mapping.findForward(destination);
//    }

    /**
     * A basic Action method to redirect a user login to the SSO Identity Provider when
     * SSO authentication is enabled in the oscar.properties file.
     * This method sends an authentication request to the IDP and then handles the
     * reply.
     */
    public ActionForward authenticationRedirect(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response){
		String user_email = request.getParameter("user_email");
		String context = request.getRequestURL().toString();
	    String relayState = request.getParameter("RelayState");

		if(relayState != null) {
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
        Integer timeout =  Integer.parseInt(OscarProperties.getInstance().getProperty("econsultLoginTimeout"));

        try {
            Long startTime = Long.parseLong(requestStartTime);
            Long currentTime = new Date().getTime() / 1000;

            isSessionValid = (currentTime - startTime) < timeout;
        }
        catch(NumberFormatException e) {
            logger.error("The property econsultLoginTimeout or the requestStartTime query string does not exist or is not a valid integer", e);
        }

        return isSessionValid;
    }

    public static String decrypt(String key, String data) {

	    try {
	      String[] parts = data.split(":");

	      IvParameterSpec iv = new IvParameterSpec( Base64.decodeBase64(parts[1]) );
	  //    IvParameterSpec iv = new IvParameterSpec(java.util.Base64.getDecoder().decode(parts[1]));
	      SecretKeySpec secretKey = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

	      Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
	      cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);

	      byte[] decodedEncryptedData = java.util.Base64.getDecoder().decode(parts[0]);

	      byte[] original = cipher.doFinal(decodedEncryptedData);

	      return new String(original);
	    } catch (Exception ex) {
	    	logger.error("Error",ex);
	    	return null;
	    }
	  }
    
   
}
