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

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.oscarehr.common.dao.PreventionDao;
import org.oscarehr.common.dao.PreventionExtDao;
import org.oscarehr.common.dao.PropertyDao;
import org.oscarehr.common.model.Prevention;
import org.oscarehr.common.model.PreventionExt;
import org.oscarehr.common.model.Property;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import oscar.log.LogAction;
import oscar.oscarPrevention.PreventionDS;
import oscar.oscarPrevention.PreventionData;
import oscar.oscarPrevention.PreventionDisplayConfig;
import oscar.util.StringUtils;

@Service
public class PreventionManager {
	@Autowired
	private PreventionDao preventionDao;
	@Autowired
	private PreventionExtDao preventionExtDao;
	@Autowired
	private PropertyDao propertyDao;
	@Autowired
	private PreventionDS preventionDS;
		
	private static final String HIDE_PREVENTION_ITEM = "hide_prevention_item";

	private ArrayList<String> preventionTypeList = new ArrayList<String>();

	public List<Prevention> getUpdatedAfterDate(LoggedInInfo loggedInInfo, Date updatedAfterThisDateExclusive, int itemsToReturn) {
		List<Prevention> results = preventionDao.findByUpdateDate(updatedAfterThisDateExclusive, itemsToReturn);

		LogAction.addLogSynchronous(loggedInInfo, "PreventionManager.getUpdatedAfterDate", "updatedAfterThisDateExclusive=" + updatedAfterThisDateExclusive);

		return (results);
	}

	public Prevention getPrevention(LoggedInInfo loggedInInfo, Integer id) {
		Prevention result = preventionDao.find(id);

		//--- log action ---
		if (result != null) {
			LogAction.addLogSynchronous(loggedInInfo, "PreventionManager.getPrevention", "id=" + id);
		}

		return (result);
	}

	public List<PreventionExt> getPreventionExtByPrevention(LoggedInInfo loggedInInfo, Integer preventionId) {
		List<PreventionExt> results = preventionExtDao.findByPreventionId(preventionId);

		LogAction.addLogSynchronous(loggedInInfo, "PreventionManager.getPreventionExtByPrevention", "preventionId=" + preventionId);

		return (results);
	}

	public ArrayList<String> getPreventionTypeList() {
		if (preventionTypeList.isEmpty()) {
			PreventionDisplayConfig pdc = PreventionDisplayConfig.getInstance();
			for (HashMap<String, String> prevTypeHash : pdc.getPreventions()) {
				if (prevTypeHash != null && StringUtils.filled(prevTypeHash.get("name"))) {
					preventionTypeList.add(prevTypeHash.get("name").trim());
				}
			}
		}
		return preventionTypeList;
	}
	
	public ArrayList<HashMap<String,String>> getPreventionTypeDescList() {
		PreventionDisplayConfig pdc = PreventionDisplayConfig.getInstance();
		ArrayList<HashMap<String,String>> preventionTypeDescList = pdc.getPreventions();
		
		return preventionTypeDescList;
	}
	
	public boolean isHidePrevItemExist() {
		List<Property> props = propertyDao.findByName(HIDE_PREVENTION_ITEM);
		if(props.size()>0){
			return true;
		}		
		return false;
	}
	
	public boolean hideItem(String item) {
		String itemsToRemove = null;
		Property p = propertyDao.checkByName(HIDE_PREVENTION_ITEM);
		
		if(p!=null && p.getValue()!=null){
			itemsToRemove = p.getValue();
			List<String> items = Arrays.asList(itemsToRemove.split("\\s*,\\s*"));
			for(String i:items){
				if(i.equals(item)){
					return true;
				}
			}
		}
		return false;
	}
	
	public static String getCustomPreventionItems() {
		String itemsToRemove = "";
		PropertyDao propertyDao = (PropertyDao)SpringUtils.getBean("propertyDao");
		Property p = propertyDao.checkByName(HIDE_PREVENTION_ITEM);
		if(p!=null && p.getValue()!=null){
		itemsToRemove = p.getValue();
		}
		return itemsToRemove;
	}
	
	public void addCustomPreventionItems(String items){
		boolean propertyExists = isHidePrevItemExist();
		if(propertyExists){
			Property p = propertyDao.checkByName(HIDE_PREVENTION_ITEM);
			p.setValue(items);
			propertyDao.merge(p);
		}else{
			Property x = new Property();
			x.setName("hide_prevention_item");
			x.setValue(items);
			propertyDao.persist(x);
		}
	}	

	public void addPreventionWithExts(Prevention prevention, HashMap<String, String> exts) {
		if (prevention == null) return;

		preventionDao.persist(prevention);
		if (exts != null) {
			for (String keyval : exts.keySet()) {
				if (StringUtils.filled(keyval) && StringUtils.filled(exts.get(keyval))) {
					PreventionExt preventionExt = new PreventionExt();
					preventionExt.setPreventionId(prevention.getId());
					preventionExt.setKeyval(keyval);
					preventionExt.setVal(exts.get(keyval));
					preventionExtDao.persist(preventionExt);
				}
			}
		}
	}

	/**
	 * programId is ignored for now as oscar doesn't support it yet.
	 */
	public List<Prevention> getPreventionsByProgramProviderDemographicDate(LoggedInInfo loggedInInfo, Integer programId, String providerNo, Integer demographicId, Calendar updatedAfterThisDateExclusive, int itemsToReturn) {
		List<Prevention> results = preventionDao.findByProviderDemographicLastUpdateDate(providerNo, demographicId, updatedAfterThisDateExclusive.getTime(), itemsToReturn);

		LogAction.addLogSynchronous(loggedInInfo, "PreventionManager.getUpdatedAfterDate", "programId=" + programId + ", providerNo=" + providerNo + ", demographicId=" + demographicId + ", updatedAfterThisDateExclusive=" + updatedAfterThisDateExclusive.getTime());

		return (results);
	}
	
	public List<Prevention> getPreventionsByDemographicNo(LoggedInInfo loggedInInfo, Integer demographicNo) {
		List<Prevention> results = preventionDao.findUniqueByDemographicId(demographicNo);
		
		LogAction.addLogSynchronous(loggedInInfo, "PreventionManager.getPreventionsByDemographicNo", "demographicNo=" + demographicNo);

		return (results);
	}

	public String getWarnings(LoggedInInfo loggedInInfo, String demo) {

		oscar.oscarPrevention.Prevention prev = PreventionData.getLocalandRemotePreventions(loggedInInfo, Integer.parseInt(demo));
		String message = "";

		try {
			preventionDS.getMessages(prev);

			Map<String,Object> warningMsgs = prev.getWarningMsgs();
			Set<String> keySet = warningMsgs.keySet();

			String value;
			for(String key : keySet) {
				value = (String) warningMsgs.get(key);
				if(! org.oscarehr.provider.model.PreventionManager.isPrevDisabled(value)) {
					message += "["+key+"="+value+"]";
				}
			}
		} catch (Exception e) {
			MiscUtils.getLogger().error("Error", e);
		}

		return message;

	}


	public static String checkNames(String k){
		String rebuilt="";
		Pattern pattern = Pattern.compile("(\\[)(.*?)(\\])");
		Matcher matcher = pattern.matcher(k);

		while(matcher.find()){
			String[] key = matcher.group(2).split("=");
			boolean prevCheck = org.oscarehr.provider.model.PreventionManager.isPrevDisabled(key[0]);

			if(prevCheck==false){
				rebuilt=rebuilt+"["+key[1]+"]";
			}
		}

		return rebuilt;
	}


	public boolean isDisabled(){
		List<Property> pList = propertyDao.findByName("hide_prevention_stop_signs");

		if (pList.size() > 0 && pList.get(0).getValue().equals("master")) {
			return true;
		} else {
			return false;
		}
	}


	public boolean isCreated(){
		List<Property> pList = propertyDao.findByName("hide_prevention_stop_signs");
		return (pList.size() > 0);
	}

	public boolean isPrevDisabled(String name){
		return getDisabledPreventions().contains(name);
	}

	public List<String> getDisabledPreventions(){

		List<Property> pList = propertyDao.findByName("hide_prevention_stop_signs");

		String disabledNames = "";
		for (Property prop : pList) {
			disabledNames += prop.getValue();
		}
		//remove '[' since it always precedes a name
		disabledNames = disabledNames.replace("[", "");
		//split on ']' since it always follows a name
		return Arrays.asList(disabledNames.split("]"));
	}

	public boolean setDisabledPreventions(List<String> newDisabledPreventions){

		if (newDisabledPreventions == null) {
			return false;
		}

		propertyDao.removeByName("hide_prevention_stop_signs");

		if (newDisabledPreventions.get(0).equals("master") || newDisabledPreventions.get(0).equals("false")) {
			Property newProp = new Property();
			newProp.setName("hide_prevention_stop_signs");
			newProp.setValue(newDisabledPreventions.get(0));
			propertyDao.persist(newProp);
			return true;
		}

		String newDisabled = "";
		for(String preventionName : newDisabledPreventions){


			if ((newDisabled + "["+ preventionName +"]").length() > 255) { //a value in the property table holds a max of 255 characters

				Property newProp = new Property();
				newProp.setName("hide_prevention_stop_signs");
				newProp.setValue(newDisabled);
				propertyDao.persist(newProp);

				newDisabled = "[" + preventionName + "]";
			} else {
				newDisabled += "[" + preventionName + "]";
			}
		}
		if (!newDisabled.isEmpty()) {
			Property newProp = new Property();
			newProp.setName("hide_prevention_stop_signs");
			newProp.setValue(newDisabled);
			propertyDao.persist(newProp);
		}
		return true;
	}

}
