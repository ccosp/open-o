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


package oscar.dms.actions;

import java.util.*;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;
import org.oscarehr.PMmodule.dao.SecUserRoleDao;
import org.oscarehr.PMmodule.model.SecUserRole;
import org.oscarehr.common.dao.DocumentDao;
import org.oscarehr.common.dao.InboxResultsRepository;
import org.oscarehr.common.dao.ProviderInboxRoutingDao;
import org.oscarehr.common.dao.QueueDao;
import org.oscarehr.common.dao.QueueDocumentLinkDao;
import org.oscarehr.common.dao.SystemPreferencesDao;
import org.oscarehr.common.model.Demographic;
import org.oscarehr.common.model.ProviderInboxItem;
import org.oscarehr.common.model.Queue;
import org.oscarehr.common.model.QueueDocumentLink;
import org.oscarehr.common.model.inbox.InboxQueryParameters;
import org.oscarehr.common.model.inbox.InboxResponse;
import org.oscarehr.common.model.inbox.OscarInboxQueryParameters;
import org.oscarehr.managers.DemographicManager;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;

import org.owasp.encoder.Encode;
import oscar.dms.EDoc;
import oscar.dms.EDocUtil;
import oscar.oscarLab.ca.all.Hl7textResultsData;
import oscar.oscarLab.ca.on.CommonLabResultData;
import oscar.oscarLab.ca.on.LabResultData;
import oscar.util.OscarRoleObjectPrivilege;

import com.quatro.dao.security.SecObjectNameDao;
import com.quatro.model.security.Secobjectname;

public class DmsInboxManageAction extends DispatchAction {
	
	private final Logger logger=MiscUtils.getLogger();

	private final String PROVIDER_PATTERN = "[a-zA-Z0-9]{1,11}";
	private final String STATUS_PATTERN = "[a-zA-Z]{1}";
	private final String NAME_PATTERN = "[a-zA-Z]";
	private final String PHN_PATTERN = "[0-9]{1,50}";
	private final String ISO_DATE_PATTERN = "^([0-9]{4})(-?)(1[0-2]|0[1-9])\\2(3[01]|0[1-9]|[12][0-9])$";

	private ProviderInboxRoutingDao providerInboxRoutingDAO = null;
	private QueueDocumentLinkDao queueDocumentLinkDAO = null;
	private SecObjectNameDao secObjectNameDao = null;
	private final SecUserRoleDao secUserRoleDao = SpringUtils.getBean(SecUserRoleDao.class);
	private final QueueDao queueDAO = SpringUtils.getBean(QueueDao.class);

	public void setProviderInboxRoutingDAO(ProviderInboxRoutingDao providerInboxRoutingDAO) {
		this.providerInboxRoutingDAO = providerInboxRoutingDAO;
	}

	public void setQueueDocumentLinkDAO(QueueDocumentLinkDao queueDocumentLinkDAO) {
		this.queueDocumentLinkDAO = queueDocumentLinkDAO;
	}

	public void setSecObjectNameDao(SecObjectNameDao secObjectNameDao) {
		this.secObjectNameDao = secObjectNameDao;
	}

	public ActionForward unspecified(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		return null;
	}


	private void addQueueSecObjectName(String queuename, String queueid) {
		String q = "_queue.";
		if (queuename != null && queueid != null) {
			q += queueid;
			Secobjectname sbn = new Secobjectname();
			sbn.setObjectname(q);
			sbn.setDescription(queuename);
			sbn.setOrgapplicable(0);
			secObjectNameDao.saveOrUpdate(sbn);
		}
	}

//	private boolean isSegmentIDUnique(ArrayList<LabResultData> doclabs, LabResultData data) {
//		boolean unique = true;
//		String sID = (data.segmentID).trim();
//		for (int i = 0; i < doclabs.size(); i++) {
//			LabResultData lrd = doclabs.get(i);
//			if (sID.equals((lrd.segmentID).trim())) {
//				unique = false;
//				break;
//			}
//		}
//		return unique;
//	}

	public ActionForward previewPatientDocLab(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		String demographicNo = request.getParameter("demog");
		String docs = request.getParameter("docs");
		String labs = request.getParameter("labs");
		String providerNo = request.getParameter("providerNo");
		String searchProviderNo = request.getParameter("searchProviderNo");
		String ackStatus = request.getParameter("ackStatus");
		ArrayList<EDoc> docPreview = new ArrayList<EDoc>();
		ArrayList<LabResultData> labPreview = new ArrayList<LabResultData>();

		if (docs != null && docs.length() > 0) {
			String[] did = new String[]{docs};
			if(docs.contains(",")) {
				did = docs.split(",");
			}
			List<String> didList = new ArrayList<String>();
			for (String s : did) {
				if (s.length() > 0) {
					didList.add(s);
				}
			}
			if (didList.size() > 0) {
				docPreview = EDocUtil.listDocsPreviewInbox(didList);
			}
		}

		if (labs != null && labs.length() > 0) {
			String[] labids = new String[]{labs};
			if(labs.contains(",")) {
				labids = labs.split(",");
			}
			List<String> ls = new ArrayList<String>();
			Collections.addAll(ls, labids);

			if (ls.size() > 0) {
				labPreview = Hl7textResultsData.getNotAckLabsFromLabNos(ls);
			}
		}

		request.setAttribute("docPreview", docPreview);
		request.setAttribute("labPreview", labPreview);
		request.setAttribute("providerNo", providerNo);
		request.setAttribute("searchProviderNo", searchProviderNo);
		request.setAttribute("ackStatus", ackStatus);
		DemographicManager demographicManager = SpringUtils.getBean(DemographicManager.class);
		Demographic demographic = demographicManager.getDemographic(LoggedInInfo.getLoggedInInfoFromSession(request), demographicNo);
		String demoName = "Not,Assigned";
		if (demographic != null) {
			demoName = demographic.getFirstName() + "," + demographic.getLastName();
		}
		request.setAttribute("demoName", demoName);
		return mapping.findForward("doclabPreview");
	}

	public ActionForward prepareForIndexPage(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		HttpSession session = request.getSession();
		try {
			if (session.getAttribute("userrole") == null) {
				response.sendRedirect("../logout.jsp");
			}
		} catch (Exception e) {
			MiscUtils.getLogger().error("error",e);
		}

		String providerNo = (String) session.getAttribute("user");
		String searchProviderNo = request.getParameter("searchProviderNo");
		boolean searchAll = "-1".equals(request.getParameter("searchProviderAll"));
		boolean searchUnclaimed = "0".equals(request.getParameter("searchProviderAll"));
		String status = request.getParameter("status");
		List<String> abnormalStatusValues = Arrays.asList("all", "abnormalOnly", "normalOnly");
		String abnormalStatus = request.getParameter("abnormalStatus");
		String searchPage = request.getParameter("isSearchPage")==null?"":request.getParameter("isSearchPage");

		if (status == null) {
			status = "N";
		} // default to new labs only
		else if ("-1".equals(status)) {
			status = "";
		}
		if (abnormalStatus == null || !abnormalStatusValues.contains(abnormalStatus)) {
			abnormalStatus = "all";
		}
		if (providerNo == null) {
			providerNo = "";
		}
		
		if(searchAll) {
			searchProviderNo = "";
		} else if (searchUnclaimed) {
			searchProviderNo = "0";
		} else if (searchProviderNo == null) {
			searchProviderNo = providerNo;
		} // default to current provider

		// boolean providerSearch = !"-1".equals(searchProviderNo);

		logger.debug("SEARCH " + searchProviderNo);

		String patientFirstName = request.getParameter("fname");
		String patientLastName = request.getParameter("lname");
		String patientHealthNumber = request.getParameter("hnum");
		String startDate = request.getParameter("startDate");
		String endDate = request.getParameter("endDate");

		if (patientFirstName == null || ! Pattern.matches(NAME_PATTERN, patientFirstName)) {
			patientFirstName = "";
		}
		if (patientLastName == null || ! Pattern.matches(NAME_PATTERN, patientLastName)) {
			patientLastName = "";
		}
		if (patientHealthNumber == null || ! Pattern.matches(PHN_PATTERN, patientHealthNumber)) {
			patientHealthNumber = "";
		}
		// boolean patientSearch = !"".equals(patientFirstName) || !"".equals(patientLastName)|| !"".equals(patientHealthNumber);

		patientFirstName = Encode.forJava(patientFirstName);
		patientLastName = Encode.forJava(patientLastName);
		patientHealthNumber = Encode.forJava(patientHealthNumber);

		if(! Pattern.matches(PROVIDER_PATTERN, searchProviderNo)) {
			searchProviderNo = providerNo;
		} else {
			searchProviderNo = Encode.forJava(searchProviderNo);
		}

		if(startDate == null || ! Pattern.matches(ISO_DATE_PATTERN, startDate)) {
			startDate = "";
		}

		if(endDate == null || ! Pattern.matches(ISO_DATE_PATTERN, endDate)) {
			endDate = "";
		}


		if (! "true".equals(searchPage))
		{
			InboxResultsRepository inboxResultsRepository = SpringUtils.getBean(InboxResultsRepository.class);
			LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(request);
            SystemPreferencesDao systemPreferencesDao = SpringUtils.getBean(SystemPreferencesDao.class);
            boolean documentShowDescription = systemPreferencesDao.isPreferenceValueEquals("document_discipline_column_display", "document_description");

			OscarInboxQueryParameters inboxQueryParameters = new OscarInboxQueryParameters(loggedInInfo.getLoggedInProvider());
			inboxQueryParameters.whereProviderNumber(searchProviderNo)
					.whereFirstName(patientFirstName).whereLastName(patientLastName).whereHin(patientHealthNumber)
					.whereStartDate(startDate).whereEndDate(endDate)
					.whereStatus(status).whereAbnormalStatus(abnormalStatus).whereMatchedStatus(InboxQueryParameters.MatchedStatus.ALL)
					.whereSortBy("dateTime").whereSortOrder("descending")
					.wherePage(0).whereResultsPerPage(0)
					.whereShowDocuments(true).whereShowLabs(true).whereShowHrm(true).whereGetCounts(true).whereGetDemographicCounts(true).whereDocumentShowDescription(documentShowDescription);

			InboxResponse inboxResponseResults = inboxResultsRepository.getInboxItems(inboxQueryParameters);
			request.setAttribute("inboxResponse", inboxResponseResults);

			Long categoryHash = Long.parseLong("" + (int)'A' + inboxResponseResults.getDocumentCount() + inboxResponseResults.getLabCount() + inboxResponseResults.getHrmCount())
					+ Long.parseLong("" + (int)'D' + inboxResponseResults.getDocumentCount())
					+ Long.parseLong("" + (int)'L' + inboxResponseResults.getLabCount())
					+ Long.parseLong("" + (int)'H' + inboxResponseResults.getHrmCount());

			request.setAttribute("patientFirstName", Encode.forHtmlContent(patientFirstName));
			request.setAttribute("patientLastName", Encode.forHtmlContent(patientLastName));
			request.setAttribute("patientHealthNumber", Encode.forHtmlContent(patientHealthNumber));
			request.setAttribute("providerNo", Encode.forHtmlContent(providerNo));
			request.setAttribute("searchProviderNo", Encode.forHtmlContent(searchProviderNo));
			request.setAttribute("searchUnclaimed", Boolean.toString(searchUnclaimed));
			request.setAttribute("ackStatus", Encode.forHtmlContent(status));
			request.setAttribute("abnormalStatus", Encode.forHtmlContent(abnormalStatus));
			request.setAttribute("categoryHash", categoryHash);
			request.setAttribute("startDate", startDate);
			request.setAttribute("endDate", endDate);
			return mapping.findForward("dms_index");
		}

		request.setAttribute("patientFirstName", Encode.forHtmlContent(patientFirstName));
		request.setAttribute("patientLastName", Encode.forHtmlContent(patientLastName));
		request.setAttribute("patientHealthNumber", Encode.forHtmlContent(patientHealthNumber));
		request.setAttribute("providerNo", Encode.forHtmlContent(providerNo));
		request.setAttribute("searchProviderNo", Encode.forHtmlContent(searchProviderNo));
		request.setAttribute("searchUnclaimed", Boolean.toString(searchUnclaimed));
		request.setAttribute("abnormalStatus", Encode.forHtmlContent(abnormalStatus));
		request.setAttribute("ackStatus", Encode.forHtmlContent(status));
		request.setAttribute("startDate", startDate);
		request.setAttribute("endDate", endDate);
		return mapping.findForward("dms_index");
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
    public ActionForward prepareForContentPage(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		LoggedInInfo loggedInInfo=LoggedInInfo.getLoggedInInfoFromSession(request);
		HttpSession session = request.getSession();
		try {
			if (session.getAttribute("userrole") == null) response.sendRedirect("../logout.jsp");
		} catch (Exception e) {
			logger.error("Error", e);
		}

		// can't use userrole from session, because it changes if provider A search for provider B's documents

		// oscar.oscarMDS.data.MDSResultsData mDSData = new oscar.oscarMDS.data.MDSResultsData();
		// CommonLabResultData comLab = new CommonLabResultData();
		// String providerNo = request.getParameter("providerNo");
		String providerNo = (String) session.getAttribute("user");
		String searchProviderNo = request.getParameter("searchProviderNo");
		boolean searchUnclaimed = Boolean.parseBoolean(request.getParameter("searchUnclaimed"));
		String ackStatus = request.getParameter("status");
		String demographicNo = request.getParameter("demographicNo"); // used when searching for labs by patient instead of provider
		// String scannedDocStatus = StringEscapeUtils.escapeSql(request.getParameter("scannedDocument"));
		String startDateStr = request.getParameter("startDate");
		String endDateStr = request.getParameter("endDate");
		String abnormalStatus = request.getParameter("abnormalStatus");
		String patientFirstName = request.getParameter("fname");
		String patientLastName = request.getParameter("lname");
		String patientHealthNumber = request.getParameter("hnum");

		int page = 0;
		try {
			page = Integer.parseInt(request.getParameter("page"));
			if (page > 0) {
				page--;
			}
		} catch (NumberFormatException nfe) {
			page = 0;
		}

		int pageSize = 20;
		try {
			String tmp = request.getParameter("pageSize");
			pageSize = Integer.parseInt(tmp);
		} catch (NumberFormatException nfe) {
			pageSize = 20;
		}
		// scannedDocStatus = "I";

		String view = request.getParameter("view");
		if (view == null || "".equals(view)) {
			view = "all";
		}

//		boolean mixLabsAndDocs = "normal".equals(view) || "all".equals(view);

		if(endDateStr == null || ! Pattern.matches(ISO_DATE_PATTERN, endDateStr) ) {
			endDateStr = "";
		}

		if(startDateStr == null || ! Pattern.matches(ISO_DATE_PATTERN, startDateStr) ) {
			startDateStr = "";
		}

		if (patientFirstName == null || ! Pattern.matches(NAME_PATTERN, patientFirstName)) {
			patientFirstName = "";
		}
		if (patientLastName == null || ! Pattern.matches(NAME_PATTERN, patientLastName)) {
			patientLastName = "";
		}
		if (patientHealthNumber == null || ! Pattern.matches(PHN_PATTERN, patientHealthNumber)) {
			patientHealthNumber = "";
		}
		// boolean patientSearch = !"".equals(patientFirstName) || !"".equals(patientLastName)|| !"".equals(patientHealthNumber);

		patientFirstName = Encode.forJava(patientFirstName);
		patientLastName = Encode.forJava(patientLastName);
		patientHealthNumber = Encode.forJava(patientHealthNumber);

		if (abnormalStatus == null || ! Pattern.matches(STATUS_PATTERN, abnormalStatus)) {
			abnormalStatus = "L";
		}

		if (ackStatus == null || ! Pattern.matches(STATUS_PATTERN, ackStatus)) {
			ackStatus = "N";
		} // default to new labs only

		if (providerNo == null || ! Pattern.matches(PROVIDER_PATTERN, providerNo)) {
			providerNo = "";
		}

		if (searchUnclaimed) {
			searchProviderNo = "0";
		}

		if (searchProviderNo == null || ! Pattern.matches(PROVIDER_PATTERN, searchProviderNo)) {
			searchProviderNo = providerNo;
		}

		InboxResultsRepository inboxResultsRepository = SpringUtils.getBean(InboxResultsRepository.class);
		DemographicManager demographicManager = SpringUtils.getBean(DemographicManager.class);
		Demographic demographic = demographicManager.getDemographic(LoggedInInfo.getLoggedInInfoFromSession(request), demographicNo);
		if (demographic != null) {
			patientFirstName = demographic.getFirstName();
			patientLastName = demographic.getLastName();
			patientHealthNumber = demographic.getHin();
		}

        SystemPreferencesDao systemPreferencesDao = SpringUtils.getBean(SystemPreferencesDao.class);
        boolean documentShowDescription = systemPreferencesDao.isPreferenceValueEquals("document_discipline_column_display", "document_description");

		InboxQueryParameters.MatchedStatus matchedStatusSearch = ("0".equals(demographicNo)) ? InboxQueryParameters.MatchedStatus.NOT_MATCHED : InboxQueryParameters.MatchedStatus.ALL;
		OscarInboxQueryParameters inboxQueryParameters = new OscarInboxQueryParameters(loggedInInfo.getLoggedInProvider());
		inboxQueryParameters.whereProviderNumber(searchProviderNo)
				.whereFirstName(patientFirstName).whereLastName(patientLastName).whereHin(patientHealthNumber)
				.whereStartDate(startDateStr).whereEndDate(endDateStr)
				.whereStatus(ackStatus).whereAbnormalStatus(abnormalStatus).whereMatchedStatus(matchedStatusSearch)
				.whereSortBy("dateTime").whereSortOrder("descending")
				.wherePage(page).whereResultsPerPage(pageSize)
				.whereShowDocuments("all".equals(view) || "documents".equals(view)).whereShowLabs("all".equals(view) || "labs".equals(view)).whereShowHrm("all".equals(view) || "hrms".equals(view))
				.whereGetCounts(false).whereGetDemographicCounts(false).whereDocumentShowDescription(documentShowDescription);

		InboxResponse results = inboxResultsRepository.getInboxItems(inboxQueryParameters);
		List<LabResultData> labdocs = results.getLabResultData(loggedInInfo);

		/* find all data for the index.jsp page */
		Hashtable patientDocs = new Hashtable();
		Hashtable patientIdNames = new Hashtable();
		String patientIdNamesStr = "";
		Hashtable docStatus = new Hashtable();
		Hashtable docType = new Hashtable();
		Hashtable<String, List<String>> ab_NormalDoc = new Hashtable();

		for (int i = 0; i < labdocs.size(); i++) {
			LabResultData data = labdocs.get(i);

			List<String> segIDs = new ArrayList<String>();
			String labPatientId = data.getLabPatientId();
			if (labPatientId == null || labPatientId.equals("-1")) labPatientId = "-1";

			if (data.isAbnormal()) {
				List<String> abns = ab_NormalDoc.get("abnormal");
				if (abns == null) {
					abns = new ArrayList<String>();
					abns.add(data.getSegmentID());
				} else {
					abns.add(data.getSegmentID());
				}
				ab_NormalDoc.put("abnormal", abns);
			} else {
				List<String> ns = ab_NormalDoc.get("normal");
				if (ns == null) {
					ns = new ArrayList<String>();
					ns.add(data.getSegmentID());
				} else {
					ns.add(data.getSegmentID());
				}
				ab_NormalDoc.put("normal", ns);
			}
			if (patientDocs.containsKey(labPatientId)) {

				segIDs = (List) patientDocs.get(labPatientId);
				segIDs.add(data.getSegmentID());
				patientDocs.put(labPatientId, segIDs);
			} else {
				segIDs.add(data.getSegmentID());
				patientDocs.put(labPatientId, segIDs);
				patientIdNames.put(labPatientId, Encode.forHtmlContent(data.patientName));
				patientIdNamesStr += ";" + labPatientId + "=" + Encode.forHtmlContent(data.patientName);
			}
			docStatus.put(data.getSegmentID(), data.getAcknowledgedStatus());
			docType.put(data.getSegmentID(), data.labType);
		}

		Integer totalDocs = 0;
		Integer totalHL7 = 0;
		Hashtable<String, List<String>> typeDocLab = new Hashtable();
		Enumeration keys = docType.keys();
		while (keys.hasMoreElements()) {
			String keyDocLabId = ((String) keys.nextElement());
			String valType = (String) docType.get(keyDocLabId);

			if (valType.equalsIgnoreCase("DOC")) {
				if (typeDocLab.containsKey("DOC")) {
					List<String> docids = typeDocLab.get("DOC");
					docids.add(keyDocLabId);// add doc id to list
					typeDocLab.put("DOC", docids);
				} else {
					List<String> docids = new ArrayList<String>();
					docids.add(keyDocLabId);
					typeDocLab.put("DOC", docids);
				}
				totalDocs++;
			} else if (valType.equalsIgnoreCase("HL7")) {
				if (typeDocLab.containsKey("HL7")) {
					List<String> hl7ids = typeDocLab.get("HL7");
					hl7ids.add(keyDocLabId);
					typeDocLab.put("HL7", hl7ids);
				} else {
					List<String> hl7ids = new ArrayList<String>();
					hl7ids.add(keyDocLabId);
					typeDocLab.put("HL7", hl7ids);
				}
				totalHL7++;
			}
		}

		Hashtable patientNumDoc = new Hashtable();
		Enumeration patientIds = patientDocs.keys();
		String patientIdStr = "";
		int totalNumDocs = 0;
		while (patientIds.hasMoreElements()) {
			String key = (String) patientIds.nextElement();
			patientIdStr += key;
			patientIdStr += ",";
			List<String> val = (List<String>) patientDocs.get(key);
			int numDoc = val.size();
			patientNumDoc.put(key, numDoc);
			totalNumDocs += numDoc;
		}

		List<String> normals = ab_NormalDoc.get("normal");
		List<String> abnormals = ab_NormalDoc.get("abnormal");

		logger.debug("labdocs.size()="+labdocs.size());

		// set attributes
		request.setAttribute("pageNum", page);
		request.setAttribute("docType", docType);
		request.setAttribute("patientDocs", patientDocs);
		request.setAttribute("providerNo", Encode.forHtmlContent(providerNo));
		request.setAttribute("searchProviderNo", Encode.forHtmlContent(searchProviderNo));
		request.setAttribute("patientIdNames", patientIdNames);
		request.setAttribute("docStatus", docStatus);
		request.setAttribute("patientIdStr", patientIdStr);
		request.setAttribute("typeDocLab", typeDocLab);
		request.setAttribute("demographicNo", demographicNo);
		request.setAttribute("ackStatus", ackStatus);
		request.setAttribute("labdocs", labdocs);
		request.setAttribute("patientNumDoc", patientNumDoc);
		request.setAttribute("totalDocs", totalDocs);
		request.setAttribute("totalHL7", totalHL7);
		request.setAttribute("normals", normals);
		request.setAttribute("abnormals", abnormals);
		request.setAttribute("totalNumDocs", totalNumDocs);
		request.setAttribute("patientIdNamesStr", patientIdNamesStr);

		return mapping.findForward("dms_page");
	}

	public ActionForward addNewQueue(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		boolean success = false;
		try {
			String qn = request.getParameter("newQueueName");
			qn = qn.trim();
			if (qn != null && qn.length() > 0) {
				QueueDao queueDao = (QueueDao) SpringUtils.getBean("queueDao");
				success = queueDao.addNewQueue(qn);
				addQueueSecObjectName(qn, queueDao.getLastId());
			}
		} catch (Exception e) {
			logger.error("Error", e);
		}

		HashMap<String, Boolean> hm = new HashMap<String, Boolean>();
		hm.put("addNewQueue", success);
		JSONObject jsonObject = JSONObject.fromObject(hm);
		try {
			response.getOutputStream().write(jsonObject.toString().getBytes());
		} catch (java.io.IOException ioe) {
			logger.error("Error", ioe);
		}
		return null;
	}

//         public ActionForward isDocumentLinkedToDemographic(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
//                boolean success = false;
//                String demoId = null;
//                try {
//                       String docId = request.getParameter("docId");
//                       logger.debug("DocId:"+docId);
//                       if (docId != null) {
//                            docId = docId.trim();
//                            if (docId.length() > 0) {
//                                EDoc doc = EDocUtil.getDoc(docId);
//                                demoId = doc.getModuleId();
//
//                                if (demoId != null) {
//                                    logger.debug("DemoId:"+demoId);
//                                    Integer demographicId = Integer.parseInt(demoId);
//                                    if (demographicId > 0) {
//                                        logger.debug("Success true");
//                                        success = true;
//                                    }
//                                }
//                            }
//                        }
//                } catch (Exception e) {
//                    logger.error("Error", e);
//                }
//
//                HashMap<String, Object> hm = new HashMap<String, Object>();
//                hm.put("isLinkedToDemographic", success);
//                hm.put("demoId", demoId);
//                JSONObject jsonObject = JSONObject.fromObject(hm);
//                try {
//                    response.getOutputStream().write(jsonObject.toString().getBytes());
//                } catch (java.io.IOException ioe) {
//                    logger.error("Error", ioe);
//                }
//
//                return null;
//	}
         
	public ActionForward isLabLinkedToDemographic(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		boolean success = false;
		String demoId = null;
		try {
			String qn = request.getParameter("labid");
			if (qn != null) {
				qn = qn.trim();
				if (qn.length() > 0) {
					CommonLabResultData c = new CommonLabResultData();
					demoId = c.getDemographicNo(qn, "HL7");
					if (demoId != null && !demoId.equals("0")) success = true;
				}
			}
		} catch (Exception e) {
			logger.error("Error", e);
		}

		HashMap<String, Object> hm = new HashMap<String, Object>();
		hm.put("isLinkedToDemographic", success);
		hm.put("demoId", demoId);
		JSONObject jsonObject = JSONObject.fromObject(hm);
		try {
			response.getOutputStream().write(jsonObject.toString().getBytes());
		} catch (java.io.IOException ioe) {
			logger.error("Error", ioe);
		}
		return null;
	}

	public ActionForward updateDocStatusInQueue(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		String docid = request.getParameter("docid");
		if (docid != null && ! docid.isEmpty()) {
			queueDocumentLinkDAO.setStatusInactive(Integer.parseInt(docid));
		}
		return null;
	}

	// return a hastable containing queue id to queue name, a hashtable of queue id and a list of document nos.
	// forward to documentInQueus.jsp
	@SuppressWarnings({ "unchecked", "rawtypes" })
    public ActionForward getDocumentsInQueues(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession();
		try {
			if (session.getAttribute("userrole") == null) response.sendRedirect("../logout.jsp");
		} catch (Exception e) {
			logger.error("Error", e);
		}
		String providerNo = (String) session.getAttribute("user");
		String searchProviderNo = request.getParameter("searchProviderNo");
		String ackStatus = request.getParameter("status");

		if (ackStatus == null) {
			ackStatus = "N";
		} // default to new labs only
		if (providerNo == null) {
			providerNo = "";
		}
		if (searchProviderNo == null) {
			searchProviderNo = providerNo;
		}

		StringBuilder roleName = new StringBuilder();
		List<SecUserRole> roles = secUserRoleDao.getUserRoles(searchProviderNo);
		for (SecUserRole r : roles) {
			if (roleName.length() != 0) {
				roleName.append(',');
			}
			roleName.append(r.getRoleName());
		}
		roleName.append("," + searchProviderNo);

		String patientIdNamesStr = "";
		List<QueueDocumentLink> qs = queueDocumentLinkDAO.getActiveQueueDocLink();
		HashMap<Integer, List<Integer>> queueDocNos = new HashMap<Integer, List<Integer>>();
		HashMap<Integer, String> docType = new HashMap<Integer, String>();
		HashMap<Integer, List<Integer>> patientDocs = new HashMap<Integer, List<Integer>>();
		DocumentDao documentDao = (DocumentDao) SpringUtils.getBean("documentDao");
		Demographic demo = new Demographic();
		List<Integer> docsWithPatient = new ArrayList<Integer>();
		HashMap<Integer, String> patientIdNames = new HashMap<Integer, String>();// lbData.patientName = demo.getLastName()+ ", "+demo.getFirstName();
		List<Integer> patientIds = new ArrayList<Integer>();
		Integer demoNo;
		HashMap<Integer, String> docStatus = new HashMap<Integer, String>();
		String patientIdStr = "";
		StringBuilder patientIdBud = new StringBuilder();
		HashMap<String, List<Integer>> typeDocLab = new HashMap<String, List<Integer>>();
		List<Integer> ListDocIds = new ArrayList<Integer>();
		for (QueueDocumentLink q : qs) {
			int qid = q.getQueueId();
			List<Object> vec = OscarRoleObjectPrivilege.getPrivilegeProp("_queue." + qid);
			// if queue is not default and provider doesn't have access to it,continue
			if (qid != Queue.DEFAULT_QUEUE_ID && !OscarRoleObjectPrivilege.checkPrivilege(roleName.toString(), (Properties) vec.get(0), (List) vec.get(1))) {
				continue;
			}
			int docid = q.getDocId();
			ListDocIds.add(docid);
			docType.put(docid, "DOC");
			demo = documentDao.getDemoFromDocNo(Integer.toString(docid));
			if (demo == null) demoNo = -1;
			else demoNo = demo.getDemographicNo();
			if (!patientIds.contains(demoNo)) patientIds.add(demoNo);
			if (!patientIdNames.containsKey(demoNo)) {
				if (demoNo == -1) {
					patientIdNames.put(demoNo, "Not, Assigned");
					patientIdNamesStr += ";" + demoNo + "=" + "Not, Assigned";
				} else {
					patientIdNames.put(demoNo, Encode.forHtmlContent(demo.getLastName()) + ", " + Encode.forHtmlContent(demo.getFirstName()));
					patientIdNamesStr += ";" + demoNo + "=" + Encode.forHtmlContent(demo.getLastName()) + ", " + Encode.forHtmlContent(demo.getFirstName());
				}

			}
			List<ProviderInboxItem> providers = providerInboxRoutingDAO.getProvidersWithRoutingForDocument("DOC", docid);
			if (providers.size() > 0) {
				ProviderInboxItem pii = providers.get(0);
				docStatus.put(docid, pii.getStatus());
			}
			if (patientDocs.containsKey(demoNo)) {
				docsWithPatient = patientDocs.get(demoNo);
				docsWithPatient.add(docid);
				patientDocs.put(demoNo, docsWithPatient);
			} else {
				docsWithPatient = new ArrayList<Integer>();
				docsWithPatient.add(docid);
				patientDocs.put(demoNo, docsWithPatient);
			}
			if (queueDocNos.containsKey(qid)) {

				List<Integer> ds = queueDocNos.get(qid);
				ds.add(docid);
				queueDocNos.put(qid, ds);

			} else {
				List<Integer> ds = new ArrayList<Integer>();
				ds.add(docid);
				queueDocNos.put(qid, ds);
			}
		}
		Integer dn = 0;
		for (int i = 0; i < patientIds.size(); i++) {
			dn = patientIds.get(i);
			patientIdBud.append(dn);
			if (i != patientIds.size() - 1) patientIdBud.append(",");
		}
		patientIdStr = patientIdBud.toString();
		typeDocLab.put("DOC", ListDocIds);
		List<Integer> normals = ListDocIds;// assume all documents are normal
		List<Integer> abnormals = new ArrayList<Integer>();
		request.setAttribute("typeDocLab", typeDocLab);
		request.setAttribute("docStatus", docStatus);
		request.setAttribute("patientDocs", patientDocs);
		request.setAttribute("patientIdNames", patientIdNames);
		request.setAttribute("docType", docType);
		request.setAttribute("patientIds", patientIds);
		request.setAttribute("patientIdStr", patientIdStr);
		request.setAttribute("normals", normals);
		request.setAttribute("abnormals", abnormals);
		request.setAttribute("queueDocNos", queueDocNos);
		request.setAttribute("patientIdNamesStr", patientIdNamesStr);
		request.setAttribute("queueIdNames", queueDAO.getHashMapOfQueues());
		request.setAttribute("providerNo", providerNo);
		request.setAttribute("searchProviderNo", searchProviderNo);
		return mapping.findForward("document_in_queues");

	}
}
