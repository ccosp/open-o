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


package org.oscarehr.measurements.web;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;
import org.oscarehr.common.dao.MeasurementDao;
import org.oscarehr.common.dao.OscarAppointmentDao;
import org.oscarehr.common.model.Appointment;
import org.oscarehr.common.model.Measurement;
import org.oscarehr.managers.SecurityInfoManager;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;
import oscar.util.StringUtils;

public class MeasurementDataAction extends DispatchAction {

	private static MeasurementDao measurementDao = SpringUtils.getBean(MeasurementDao.class);
	OscarAppointmentDao appointmentDao = (OscarAppointmentDao)SpringUtils.getBean("oscarAppointmentDao");
	private SecurityInfoManager securityInfoManager = SpringUtils.getBean(SecurityInfoManager.class);
	 
	public ActionForward getLatestValues(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws IOException {
		String demographicNo = request.getParameter("demographicNo");
		String typeStr = request.getParameter("types");
		String appointmentNo = request.getParameter("appointmentNo");
		int apptNo = 0;
		if(appointmentNo != null && appointmentNo.length()>0 && !appointmentNo.equalsIgnoreCase("null")) {
			apptNo = Integer.parseInt(appointmentNo);
		}

		if(!securityInfoManager.hasPrivilege(LoggedInInfo.getLoggedInInfoFromSession(request), "_demographic", "r", demographicNo)) {
        	throw new SecurityException("missing required security object (_demographic)");
        }
		
		int prevApptNo = 0;
		if(apptNo > 0) {
			List<Appointment> appts = appointmentDao.getAppointmentHistory(Integer.parseInt(demographicNo));
			if(appts.size() > 1) {
				for(int x=0;x<appts.size();x++) {
					Appointment appt = appts.get(x);
					if(appt.getId().intValue() == apptNo && x < appts.size()-1) {
						prevApptNo = appts.get(x+1).getId();
					}
				}
			}
		}


		String fresh =request.getParameter("fresh");
		HashMap<String,Boolean> freshMap = new HashMap<String,Boolean>();
		if(fresh!=null) {
			String tmp[] = fresh.split(",");
			for(int x=0;x<tmp.length;x++) {
				freshMap.put(tmp[x],true);
			}
		}
		if(typeStr == null || typeStr.length() == 0) {
			//error
		}
		String[] types = typeStr.split(",");

		Map<String,Measurement> measurementMap = measurementDao.getMeasurements(Integer.parseInt(demographicNo),types);

		Date nctTs = null;
		Date applanationTs=null;

		StringBuilder script = new StringBuilder();
		for(String key:measurementMap.keySet()) {
			Measurement value = measurementMap.get(key);
			if((freshMap.get(key)==null) ||(freshMap.get(key) != null && value.getAppointmentNo() == Integer.parseInt(appointmentNo))) {
				//script.append("jQuery(\"[measurement='"+key+"']\").val(\""+value.getDataField().replace("\n", "\\n")+"\").attr({itemtime: \"" + value.getCreateDate().getTime() + "\", appointment_no: \"" + value.getAppointmentNo() + "\"});\n");
				script.append("jQuery(\"[measurement='"+key+"']\").val(\""+StringEscapeUtils.escapeJavaScript(value.getDataField())+"\").attr({itemtime: \"" + value.getCreateDate().getTime() + "\", appointment_no: \"" + value.getAppointmentNo() + "\"});\n");
				if(apptNo>0 && apptNo == value.getAppointmentNo()) {
					script.append("jQuery(\"[measurement='"+key+"']\").addClass('examfieldwhite');\n");
				}
				if(prevApptNo>0 && value.getAppointmentNo() == prevApptNo) {
					script.append("jQuery(\"[measurement='"+key+"']\").attr('prev_appt','true');\n");
				}
				if(apptNo>0 && value.getAppointmentNo() == apptNo) {
					script.append("jQuery(\"[measurement='"+key+"']\").attr('current_appt','true');\n");
				}
				if(key.equals("os_iop_applanation") || key.equals("od_iop_applanation")) {
					if(applanationTs == null) {
						applanationTs = value.getDateObserved();
					} else if(value.getDateObserved().after(applanationTs)) {
						applanationTs = value.getDateObserved();
					}
				}
				if(key.equals("os_iop_nct") || key.equals("od_iop_nct")) {
					if(nctTs == null) {
						nctTs = value.getDateObserved();
					} else if(value.getDateObserved().after(nctTs)) {
						nctTs = value.getDateObserved();
					}
				}

				if(key.equals("iop_ra") || key.equals("iop_la")) {
					if(applanationTs == null) {
						applanationTs = value.getDateObserved();
					} else if(value.getDateObserved().after(applanationTs)) {
						applanationTs = value.getDateObserved();
					}
				}
				if(key.equals("iop_rn") || key.equals("iop_ln")) {
					if(nctTs == null) {
						nctTs = value.getDateObserved();
					} else if(value.getDateObserved().after(nctTs)) {
						nctTs = value.getDateObserved();
					}
				}
			}
		}

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		if(applanationTs!=null)
			script.append("jQuery(\"#applanation_ts\").html('"+sdf.format(applanationTs)+"');\n");
		if(nctTs != null)
			script.append("jQuery(\"#nct_ts\").html('"+sdf.format(nctTs)+"');\n");

		response.getWriter().print(script);
		return null;
	}

	public ActionForward getMeasurementsGroupByDate(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		String demographicNo = request.getParameter("demographicNo");
		String[] types = (request.getParameter("types") != null ? request.getParameter("types") : "").split(",");

		if(!securityInfoManager.hasPrivilege(LoggedInInfo.getLoggedInInfoFromSession(request), "_demographic", "r", demographicNo)) {
        	throw new SecurityException("missing required security object (_demographic)");
        }
		
		List<Date> measurementDates = measurementDao.getDatesForMeasurements(Integer.parseInt(demographicNo), types);
		HashMap<String, HashMap<String, Measurement>> measurementsMap = new HashMap<String, HashMap<String, Measurement>>();

		for (Date d : measurementDates) {
			Calendar c = Calendar.getInstance();
			c.setTime(d);

			Date outDate = c.getTime();

			if (!measurementsMap.keySet().contains(outDate.getTime() + ""))
				measurementsMap.put(outDate.getTime() + "", measurementDao.getMeasurementsPriorToDate(Integer.parseInt(demographicNo), d));
		}

		boolean isJsonRequest = request.getParameter("json") != null && request.getParameter("json").equalsIgnoreCase("true");

		if (isJsonRequest) {
			JSONObject json = JSONObject.fromObject(measurementsMap);
			response.getOutputStream().write(json.toString().getBytes());
		}
		return null;
	}

	/**
	 * This function will be used to save a new measurement object to the database
	 *
	 * @param request Request object containing the demographic number, appointment number, measurement type, value & instruction.
	 * @param response Response object to return the JSON data
	 * @return JSON object containing boolean to let the user know if the save was successful or not
	 */
	public ActionForward saveMeasurement(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws NumberFormatException, IOException {
		LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(request);
		String providerNo = loggedInInfo.getLoggedInProviderNo();

		String demographicNo = request.getParameter("demographicNo");
		String appointmentNoString = request.getParameter("appointmentNo");
		String measurementType = request.getParameter("type");
		String measurementValue = request.getParameter("value");
		String measurementInstruction = request.getParameter("instruction");
		String dateObservedString = request.getParameter("dateObserved");
		HashMap<String, Object> hashMap = new HashMap<String, Object>();
		int appointmentNo = 0;
		try {
			appointmentNo = Integer.parseInt(appointmentNoString);
		} catch (NumberFormatException e) {
			MiscUtils.getLogger().error("Failed to save measurement - invalid appointment ID", e);
		}
		Date dateObserved = new Date();
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			dateObserved = sdf.parse(dateObservedString);
		} catch (ParseException e) {
			MiscUtils.getLogger().error("Failed to save measurement - invalid date observed", e);
		}

		if(!securityInfoManager.hasPrivilege(LoggedInInfo.getLoggedInInfoFromSession(request), "_demographic", "w", demographicNo)) {
			throw new SecurityException("missing required security object (_demographic)");
		}

		if (!StringUtils.isNullOrEmpty(measurementType) && !StringUtils.isNullOrEmpty(measurementValue) && !StringUtils.isNullOrEmpty(demographicNo)) {
			Measurement measurement = new Measurement();
			measurement.setDataField(measurementValue);
			measurement.setType(measurementType);
			measurement.setDateObserved(dateObserved);
			measurement.setDemographicId(Integer.parseInt(demographicNo));
			measurement.setMeasuringInstruction(measurementInstruction);
			measurement.setProviderNo(providerNo);
			measurement.setAppointmentNo(appointmentNo);
			measurementDao.persist(measurement);

			hashMap.put("success", true);
			JSONObject json = JSONObject.fromObject(hashMap);
			response.getOutputStream().write(json.toString().getBytes());
		} else {
			hashMap.put("success", false);
			JSONObject json = JSONObject.fromObject(hashMap);
			response.getOutputStream().write(json.toString().getBytes());
		}

		return null;
	}


	public ActionForward saveValues(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		LoggedInInfo loggedInInfo=LoggedInInfo.getLoggedInInfoFromSession(request);
		String providerNo=loggedInInfo.getLoggedInProviderNo();
		
		String demographicNo = request.getParameter("demographicNo");
		String strAppointmentNo = request.getParameter("appointmentNo");
		int appointmentNo = Integer.parseInt(strAppointmentNo);

		boolean isJsonRequest = request.getParameter("json") != null && request.getParameter("json").equalsIgnoreCase("true");

		if(!securityInfoManager.hasPrivilege(LoggedInInfo.getLoggedInInfoFromSession(request), "_demographic", "w", demographicNo)) {
        	throw new SecurityException("missing required security object (_demographic)");
        }
		
		try {

			Enumeration e = request.getParameterNames();
			Map<String,String> measurements = new HashMap<String,String>();

			while(e.hasMoreElements()) {
				String key = (String)e.nextElement();
				String values[] = request.getParameterValues(key);
				if(key.equals("action") || key.equals("demographicNo") || key.equals("appointmentNo"))
					continue;
				//if(values.length>0 && values[0]!=null && values[0].length()>0) {
				if(values.length>0){
					measurements.put(key,values[0]);
					Measurement m = new Measurement();
					m.setComments("");
					m.setDataField(values[0]);
					m.setDateObserved(new Date());
					m.setDemographicId(Integer.parseInt(demographicNo));
					m.setMeasuringInstruction("");
					m.setProviderNo(providerNo);
					m.setType(key);
					m.setAppointmentNo(appointmentNo);
					measurementDao.persist(m);
				}
			}

			if (isJsonRequest) {
				HashMap<String, Object> hashMap = new HashMap<String, Object>();
				hashMap.put("success", true);
				JSONObject json = JSONObject.fromObject(hashMap);
				response.getOutputStream().write(json.toString().getBytes());
			}

		} catch (Exception e) {
			HashMap<String, Object> hashMap = new HashMap<String, Object>();
			hashMap.put("success", false);
			MiscUtils.getLogger().error("Couldn't save measurements", e);
			JSONObject json = JSONObject.fromObject(hashMap);
			response.getOutputStream().write(json.toString().getBytes());
		}

		return null;
	}

	public ActionForward getDataByType(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		String demoNo = request.getParameter("demoNo");
		String typeList = request.getParameter("typeList");
		String searchDate = request.getParameter("searchDate");
		String[] type_list = typeList.split(",");
		JSONObject json = new JSONObject();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		Date start = null;
		Date end = null;
		if(null != searchDate && searchDate.length() > 0){
			if(searchDate.indexOf("-") > -1){
				start = sdf.parse(searchDate + " 00:00:00");
				end = sdf.parse(searchDate + " 23:59:59");
			}
			if(searchDate.indexOf("/") > -1){
				start = sdf2.parse(searchDate + " 00:00:00");
				end = sdf2.parse(searchDate + " 23:59:59");
			}
		}
		for(int i = 0;i < type_list.length;i ++){
			List<Measurement> m = measurementDao.findMeasurementByTypeAndDate(Integer.parseInt(demoNo), type_list[i], start, end);
			if(m.size() > 0){
				json.put(type_list[i], m.get(0).getDataField());
			}else{
				json.put(type_list[i], "");
			}
		}

		response.getOutputStream().write(json.toString().getBytes());
		return null;
	}

	/**
	 * This function will be used to retrieve all measurement data of a specific type for a demographic
	 *
	 * @param request Request object containing the demographic number and measurement type to be searched
	 * @param response Response object to return the JSON data containing measurement information
	 * @return JSON object containing measurement data
	 */
	public ActionForward getMeasurementsByType(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws NumberFormatException, IOException {
		String demographicNo = request.getParameter("demographicNo");
		String measurementType = request.getParameter("measurementType");

		JSONObject json = new JSONObject();
		List<Measurement> measurements = measurementDao.findByType(Integer.parseInt(demographicNo), measurementType);
		if (measurements.isEmpty()) {
			json.put("-1", "No Results Found");
		} else {
			for (Measurement measurement : measurements) {
				json.put(measurement.getId(), measurement);
			}
		}

		response.getOutputStream().write(json.toString().getBytes());
		return null;
	}
}
