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

import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.Properties;
import java.util.Vector;

import org.apache.commons.lang3.StringUtils;
import org.oscarehr.common.model.Security;
import org.oscarehr.util.MiscUtils;

import oscar.OscarProperties;


public final class LoginCheckLogin {
	boolean bWAN = true;

	LoginCheckLoginBean lb = null;

	LoginInfoBean linfo = null;

	LoginList llist = null;

	public LoginCheckLogin() {
	}

	static boolean ipFound(String IPToCheck) {
		String prop = OscarProperties.getInstance().getProperty("login_local_ip");
		if(!StringUtils.isEmpty(prop)) {
			String[] props = prop.split(",");
			for(String p:props) {
				if(IPToCheck.startsWith(p)) {
					return true;
				}
			}
		} 
		return false;
	}
	public boolean isBlock(String ip) {
		boolean bBlock = false;

		// judge the local network
	//	Properties p = OscarProperties.getInstance();
		if(ipFound(ip)) bWAN = false;
		//if (ip.startsWith(p.getProperty("login_local_ip"))) bWAN = false;

		GregorianCalendar now = new GregorianCalendar();
		while (llist == null) {
			llist = LoginList.getLoginListInstance(); // LoginInfoBean info =
			// null;
		}
		String sTemp = null;

		// delete the old entry in the loginlist if time out
		if (bWAN && !llist.isEmpty()) {
			for (Enumeration e = llist.keys(); e.hasMoreElements();) {
				sTemp = (String) e.nextElement();
				linfo = (LoginInfoBean) llist.get(sTemp);
				if (linfo.getTimeOutStatus(now)) llist.remove(sTemp);
			}

			// check if it is blocked
			if (llist.get(ip) != null && ((LoginInfoBean) llist.get(ip)).getStatus() == 0) bBlock = true;
		}

		return bBlock;
	}

	// lock username and ip
	public boolean isBlock(String ip, String userName) {
		Properties p = OscarProperties.getInstance();
		if (!p.getProperty("login_lock", "").trim().equals("true")) {
			return isBlock(ip);
		}

		// the following meets the requirment of epp
		boolean bBlock = false;
		// judge the local network
	//	if (ip.startsWith(p.getProperty("login_local_ip"))) bWAN = false;
		if(ipFound(ip)) bWAN = false;
		
		while (llist == null) {
			llist = LoginList.getLoginListInstance();
		}

		// check if it is blocked
		if (llist.get(userName) != null && ((LoginInfoBean) llist.get(userName)).getStatus() == 0) bBlock = true;

		return bBlock;
	}

	/**
	 * Authenticates based on an SSO Key
	 * @param ssoKey - The ssoKey to be matched to a provider
	 * @return String array of the provider information
	 */
	public String[] ssoAuth(String ssoKey) {
		lb = new LoginCheckLoginBean();
		lb.ssoIni(ssoKey);
		return lb.ssoAuthenticate();
	}
	
	// authenticate is used to check password
	public String[] auth(String user_name, String password, String pin, String ip) {
		lb = new LoginCheckLoginBean();
		lb.ini(user_name, password, pin, ip);
		return lb.authenticate();
	}

	/**
	 * Overload method for authentication without PIN code.
	 * It does not matter if the PIN code is set as required in
	 * the provider profile.
	 *
	 * @param user_name
	 * @param password
	 * @param ip
	 * @return
	 */
	public String[] auth(String user_name, String password, String ip) {
		return auth(user_name, password, null, ip);
	}

	/**
	 * only works after you call auth successfully
	 * 
	 * @return the Security object
	 */
	public Security getSecurity() {
		return (lb.getSecurity());
	}

	public synchronized void updateLoginList(String ip, String userName) {
		Properties p = OscarProperties.getInstance();
		if (!p.getProperty("login_lock", "").trim().equals("true")) {
			updateLoginList(ip);
		} else {
			updateLockList(userName);
		}
	}

	// update login list if login failed
	public synchronized void updateLoginList(String ip) {
		Properties p = OscarProperties.getInstance();
		if (bWAN) {
			GregorianCalendar now = new GregorianCalendar();
			if (llist.get(ip) == null) {
				linfo = new LoginInfoBean(now, Integer.parseInt(p.getProperty("login_max_failed_times")), Integer.parseInt(p.getProperty("login_max_duration")));
			} else {
				linfo = (LoginInfoBean) llist.get(ip);
				linfo.updateLoginInfoBean(now, 1);
			}
			llist.put(ip, linfo);
			MiscUtils.getLogger().debug(ip + "  status: " + ((LoginInfoBean) llist.get(ip)).getStatus() + " times: " + linfo.getTimes() + " time: ");
		}
	}

	// lock update login list if login failed
	public synchronized void updateLockList(String userName) {
		Properties p = OscarProperties.getInstance();
		if (bWAN) {
			GregorianCalendar now = new GregorianCalendar();
			if (llist.get(userName) == null) {
				linfo = new LoginInfoBean(now, Integer.parseInt(p.getProperty("login_max_failed_times")), Integer.parseInt(p.getProperty("login_max_duration")));
			} else {
				linfo = (LoginInfoBean) llist.get(userName);
				linfo.updateLoginInfoBean(now, 1);
			}
			llist.put(userName, linfo);
			MiscUtils.getLogger().debug(userName + "  status: " + ((LoginInfoBean) llist.get(userName)).getStatus() + " times: " + linfo.getTimes() + " time: ");
		}
	}

	public boolean unlock(String userName) {
		boolean bBlock = false;

		while (llist == null) {
			llist = LoginList.getLoginListInstance();
		}
		String sTemp = null;

		// unlocl the entry in the loginlist
		if (!llist.isEmpty()) {
			for (Enumeration e = llist.keys(); e.hasMoreElements();) {
				sTemp = (String) e.nextElement();
				if (sTemp.equals(userName)) {
					llist.remove(sTemp);
					bBlock = true;
				}
			}
		}

		return bBlock;
	}

	public Vector findLockList() {
		Vector ret = new Vector();

		while (llist == null) {
			llist = LoginList.getLoginListInstance();
		}
		String sTemp = null;

		// unlocl the entry in the loginlist
		if (!llist.isEmpty()) {
			for (Enumeration e = llist.keys(); e.hasMoreElements();) {
				sTemp = (String) e.nextElement();
				ret.add(sTemp);
			}
		}

		return ret;
	}
}
