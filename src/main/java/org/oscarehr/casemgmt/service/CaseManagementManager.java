/**
 * Copyright (c) 2024. Magenta Health. All Rights Reserved.
 *
 * Copyright (c) 2005-2012. Centre for Research on Inner City Health, St. Michael's Hospital, Toronto. All Rights Reserved.
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
 * This software was written for
 * Centre for Research on Inner City Health, St. Michael's Hospital,
 * Toronto, Ontario, Canada
 *
 * Modifications made by Magenta Health in 2024.
 */

package org.oscarehr.casemgmt.service;

import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Logger;
import org.apache.struts.util.LabelValueBean;
import org.oscarehr.PMmodule.caisi_integrator.CaisiIntegratorManager;
import org.oscarehr.PMmodule.caisi_integrator.IntegratorFallBackManager;
import org.oscarehr.PMmodule.dao.ProgramAccessDAO;
import org.oscarehr.PMmodule.dao.ProgramProviderDAO;
import org.oscarehr.PMmodule.dao.ProgramQueueDao;
import org.oscarehr.PMmodule.model.AccessType;
import org.oscarehr.PMmodule.model.DefaultRoleAccess;
import org.oscarehr.PMmodule.model.ProgramAccess;
import org.oscarehr.PMmodule.model.ProgramProvider;
import org.oscarehr.PMmodule.model.ProgramQueue;
import org.oscarehr.PMmodule.service.AdmissionManager;
import org.oscarehr.PMmodule.service.ProgramManager;
import org.oscarehr.PMmodule.utility.ProgramAccessCache;
import org.oscarehr.PMmodule.utility.RoleCache;
import org.oscarehr.caisi_integrator.ws.CachedDemographicDrug;
import org.oscarehr.caisi_integrator.ws.CachedDemographicNote;
import org.oscarehr.caisi_integrator.ws.CachedFacility;
import org.oscarehr.casemgmt.common.EChartNoteEntry;
import org.oscarehr.casemgmt.dao.CaseManagementCPPDAO;
import org.oscarehr.casemgmt.dao.CaseManagementIssueDAO;
import org.oscarehr.casemgmt.dao.CaseManagementNoteDAO;
import org.oscarehr.casemgmt.dao.CaseManagementNoteExtDAO;
import org.oscarehr.casemgmt.dao.CaseManagementNoteLinkDAO;
import org.oscarehr.casemgmt.dao.IssueDAO;
import org.oscarehr.casemgmt.dao.RoleProgramAccessDAO;
import org.oscarehr.casemgmt.model.CaseManagementCPP;
import org.oscarehr.casemgmt.model.CaseManagementIssue;
import org.oscarehr.casemgmt.model.CaseManagementNote;
import org.oscarehr.casemgmt.model.CaseManagementNoteExt;
import org.oscarehr.casemgmt.model.CaseManagementNoteLink;
import org.oscarehr.casemgmt.model.CaseManagementSearchBean;
import org.oscarehr.casemgmt.model.Issue;
import org.oscarehr.casemgmt.model.ProviderExt;
import org.oscarehr.common.dao.AbstractDao;
import org.oscarehr.common.dao.AllergyDao;
import org.oscarehr.common.dao.AppointmentArchiveDao;
import org.oscarehr.common.dao.CaseManagementTmpSaveDao;
import org.oscarehr.common.dao.DemographicDao;
import org.oscarehr.common.dao.DrugDao;
import org.oscarehr.common.dao.DxDao;
import org.oscarehr.common.dao.DxresearchDAO;
import org.oscarehr.common.dao.EChartDao;
import org.oscarehr.common.dao.EncounterWindowDao;
import org.oscarehr.common.dao.HashAuditDao;
import org.oscarehr.common.dao.MessageTblDao;
import org.oscarehr.common.dao.MsgDemoMapDao;
import org.oscarehr.common.dao.OscarAppointmentDao;
import org.oscarehr.common.dao.ProviderExtDao;
import org.oscarehr.common.dao.SecRoleDao;
import org.oscarehr.common.dao.UserPropertyDAO;
import org.oscarehr.common.model.Admission;
import org.oscarehr.common.model.Allergy;
import org.oscarehr.common.model.Appointment;
import org.oscarehr.common.model.CaseManagementTmpSave;
import org.oscarehr.common.model.Demographic;
import org.oscarehr.common.model.Drug;
import org.oscarehr.common.model.DxAssociation;
import org.oscarehr.common.model.Dxresearch;
import org.oscarehr.common.model.EncounterWindow;
import org.oscarehr.common.model.HashAudit;
import org.oscarehr.common.model.MessageTbl;
import org.oscarehr.common.model.MsgDemoMap;
import org.oscarehr.common.model.Provider;
import org.oscarehr.common.model.UserProperty;
import org.oscarehr.managers.SecurityInfoManager;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import oscar.OscarProperties;
import org.oscarehr.documentManager.EDocUtil;
import oscar.log.LogAction;
import oscar.log.LogConst;
//import oscar.oscarEncounter.pageUtil.EctSessionBean;
import oscar.util.ConversionUtils;
import oscar.util.DateUtils;

import com.quatro.model.security.Secrole;
import com.quatro.service.security.RolesManager;

public interface CaseManagementManager {

    public enum IssueType {
        OMEDS, SOCHISTORY, MEDHISTORY, CONCERNS, REMINDERS, FAMHISTORY, RISKFACTORS
    }

    public final int SIGNATURE_SIGNED = 1;
    public final int SIGNATURE_VERIFY = 2;

    public void setAppointmentArchiveDao(AppointmentArchiveDao appointmentArchiveDao);

    public void setDxDao(DxDao dxDao);

    public CaseManagementIssue getIssueByIssueCode(String demo, String issue_code);

    public CaseManagementIssue getIssueById(String demo, String issue_id);

    public void setProgramManager(ProgramManager programManager);

    public void getEditors(CaseManagementNote note);

    public void getEditors(Collection<CaseManagementNote> notes);

    public List<Provider> getAllEditors(String demographicNo);

    public UserProperty getUserProperty(String provider_no, String name);

    public void saveUserProperty(UserProperty prop);

    public void saveEctWin(EncounterWindow ectWin);

    public EncounterWindow getEctWin(String provider);

    public void saveNoteExt(CaseManagementNoteExt cExt);

    public void updateNoteExt(CaseManagementNoteExt cExt);

    public void saveNoteLink(CaseManagementNoteLink cLink);

    public void updateNoteLink(CaseManagementNoteLink cLink);

    public String saveNote(CaseManagementCPP cpp, CaseManagementNote note, String cproviderNo, String userName,
            String lastStr, String roleName);

    public List getNotes(String demographic_no, UserProperty prop);

    public List<CaseManagementNote> getCPP(String demoNo, long issueId, UserProperty prop);

    public List<CaseManagementNote> getNotes(String demographic_no, String[] issues, UserProperty prop);

    public List<CaseManagementNote> getNotes(LoggedInInfo loggedInInfo, String demographic_no, String[] issues,
            UserProperty prop);

    public List<CaseManagementNote> getNotes(String demographic_no);

    public List<CaseManagementNote> getNotes(String demographic_no, Integer maxNotes);

    public List<CaseManagementNote> getNotes(String demographic_no, String[] issues);

    public List<CaseManagementNote> getNotes(LoggedInInfo loggedInInfo, String demographic_no, String[] issues);

    public List<CaseManagementNote> getNotes(String demographic_no, String[] issues, Integer maxNotes);

    public List<CaseManagementNote> getNotesWithLimit(String demographic_no, Integer offset, Integer numToReturn);

    public List<CaseManagementNote> getNotesInDateRange(String demographic_no, Date startDate, Date endDate);

    public List<CaseManagementNote> getActiveNotes(String demographic_no, String[] issues);

    public List<CaseManagementNote> getActiveNotes(LoggedInInfo loggedInInfo, String demographic_no, String[] issues);

    public List<CaseManagementIssue> getIssues(int demographic_no);

    public Issue getIssueIByCmnIssueId(int cmnIssueId);

    public List<CaseManagementIssue> getIssuesByNote(int noteId);

    public List<CaseManagementIssue> getIssues(int demographic_no, Boolean resolved);

    public List<CaseManagementIssue> getIssues(String demographic_no, List accessRight);

    public boolean inAccessRight(String right, String issueAccessType, List accessRight);

    public Issue getIssue(String issue_id);

    public Issue getIssueByCode(IssueType issueCode);

    public Issue getIssueByCode(String issueCode);

    public CaseManagementNote getNote(String note_id);

    public List<CaseManagementNote> getNotesByUUID(String uuid);

    public CaseManagementNote getMostRecentNote(String uuid);

    public CaseManagementNoteExt getNoteExt(Long id);

    public List<CaseManagementNoteExt> getExtByNote(Long noteId);

    public List getExtByKeyVal(String keyVal);

    public List getExtByValue(String keyVal, String value);

    public List getExtBeforeDate(String keyVal, Date dateValue);

    public List getExtAfterDate(String keyVal, Date dateValue);

    public CaseManagementNoteLink getNoteLink(Long id);

    public List getLinkByNote(Long noteId);

    public CaseManagementNoteLink getLatestLinkByNote(Long noteId);

    public List getLinkByTableId(Integer tableName, Long tableId);

    public List<CaseManagementNoteLink> getLinkByTableId(Integer tableName, Long tableId, String otherId);

    public List<CaseManagementNoteLink> getLinkByTableIdDesc(Integer tableName, Long tableId);

    public List<CaseManagementNoteLink> getLinkByTableIdDesc(Integer tableName, Long tableId, String otherId);

    public CaseManagementNoteLink getLatestLinkByTableId(Integer tableName, Long tableId, String otherId);

    public CaseManagementNoteLink getLatestLinkByTableId(Integer tableName, Long tableId);

    public Integer getTableNameByDisplay(String disp);

    public CaseManagementCPP getCPP(String demographic_no);

    public List<Allergy> getAllergies(String demographic_no);

    public List<Drug> getPrescriptions(String demographic_no, boolean all);

    public List<Drug> getCurrentPrescriptions(int demographic_no);

    public List<Drug> getPrescriptions(LoggedInInfo loggedInInfo, int demographicId, boolean all);

    public List<LabelValueBean> getMsgBeans(Integer demographicNo);

    public void deleteIssueById(CaseManagementIssue issue);

    public void saveAndUpdateCaseIssues(List issuelist);

    public void saveCaseIssue(CaseManagementIssue issue);

    public Issue getIssueInfo(Long l);

    public List getAllIssueInfo();

    public void saveCPP(CaseManagementCPP cpp, String providerNo);

    public List<Issue> getIssueInfoByCode(String providerNo, String[] codes);

    public List<Issue> getIssueInfoByCode(String providerNo, String code);

    public Issue getIssueInfoByCode(String code);

    public Issue getIssueInfoByTypeAndCode(String type, String code);

    public List<Issue> getIssueInfoBySearch(String providerNo, String search, List accessRight);

    public void addNewIssueToConcern(String demoNo, String issueName);

    public void removeIssueFromCPP(String demoNo, CaseManagementIssue issue);

    public void changeIssueInCPP(String demoNo, String origIssueDesc, String newIssueDesc);

    public void updateCurrentIssueToCPP(String demoNo, List issueList);

    public List<CaseManagementIssue> getFilteredNotes(String providerNo, String demographic_no);

    public boolean haveIssue(Long issid, Long noteId, String demoNo);

    public boolean haveIssue(Long issid, String demoNo);

    public boolean greaterEqualLevel(int level, String providerNo);

    public List<AccessType> getAccessRight(String providerNo, String demoNo, String programId);

    public boolean roleInAccess(Long roleId, ProgramAccess pa);

    public void addrt(List<AccessType> rt, AccessType at);

    public boolean hasAccessRight(String accessName, String accessType, String providerNo, String demoNo, String pId);

    public String getRoleName(String providerNo, String program_id);

    public String getDemoName(String demoNo);

    public String getDemoGender(String demoNo);

    public String getDemoAge(String demoNo);

    public String getDemoDOB(String demoNo);

    public String getCaisiRoleById(String id);

    public List<CaseManagementNote> search(CaseManagementSearchBean searchBean);

    public List<CaseManagementNote> filterNotesByAccess(List<CaseManagementNote> notes, String providerNo);

    public void tmpSave(String providerNo, String demographicNo, String programId, String noteId, String note);

    public void deleteTmpSave(String providerNo, String demographicNo, String programId);

    public CaseManagementTmpSave getTmpSave(String providerNo, String demographicNo, String programId);

    public CaseManagementTmpSave restoreTmpSave(String providerNo, String demographicNo, String programId);

    public CaseManagementTmpSave restoreTmpSave(String providerNo, String demographicNo, String programId, Date date);

    public List getHistory(String note_id);

    public List<CaseManagementNote> getIssueHistory(String issueIds, String demoNo);

    public List<CaseManagementNote> filterNotes(LoggedInInfo loggedInInfo, String providerNo,
            Collection<CaseManagementNote> notes, String programId);

    public List<EChartNoteEntry> filterNotes1(String providerNo, Collection<EChartNoteEntry> notes, String programId);

    public boolean hasRole(String providerNo, CachedDemographicNote cachedDemographicNote, String programId);

    public boolean isRoleIncludedInAccess(ProgramAccess pa, Secrole role);

    public Map<String, ProgramAccess> convertProgramAccessListToMap(List<ProgramAccess> paList);

    public Integer searchIssuesCount(String providerNo, String programId, String search);

    public List<Issue> searchIssues(String providerNo, String programId, String search);

    public List<Issue> searchIssues(String providerNo, String programId, String search, int startIndex,
            int numToReturn);

    public List searchIssuesNoRolesConcerned(String providerNo, String programId, String search);

    public List<CaseManagementIssue> filterIssues(LoggedInInfo loggedInInfo, String providerNo,
            List<CaseManagementIssue> issues, String programId);

    public void updateNote(CaseManagementNote note);

    public void saveNoteSimple(CaseManagementNote note);

    public Long saveNoteSimpleReturnID(CaseManagementNote note);

    public boolean isClientInProgramDomain(String providerNo, String demographicNo);

    public List<ProgramProvider> getProgramProviders(String providerNo);

    public List<Admission> getAdmission(Integer demographicNo);

    public boolean isClientInProgramDomain(List<ProgramProvider> providerPrograms, List<Admission> allAdmissions);

    public boolean isClientReferredInProgramDomain(String providerNo, String demographicNo);

    public boolean unlockNote(int noteId, String password);

    public void updateIssue(String demographicNo, Long originalIssueId, Long newIssueId);

    public boolean getEnabled();

    public void setEnabled(boolean enabled);

    public void setHashAuditDao(HashAuditDao dao);

    public void setCaseManagementNoteDAO(CaseManagementNoteDAO dao);

    public void setCaseManagementNoteExtDAO(CaseManagementNoteExtDAO dao);

    public void setCaseManagementNoteLinkDAO(CaseManagementNoteLinkDAO dao);

    public void setCaseManagementIssueDAO(CaseManagementIssueDAO dao);

    public void setProgramAccessDAO(ProgramAccessDAO programAccessDAO);

    public void setIssueDAO(IssueDAO dao);

    public void setCaseManagementCPPDAO(CaseManagementCPPDAO dao);

    public void setProgramProviderDao(ProgramProviderDAO programProviderDao);

    public void setProgramQueueDao(ProgramQueueDao programQueueDao);

    public void setRolesManager(RolesManager mgr);

    public void setProviderExtDao(ProviderExtDao providerExtDao);

    public void setRoleProgramAccessDAO(RoleProgramAccessDAO roleProgramAccessDAO);

    public void setDemographicDao(DemographicDao demographicDao);

    public void setCaseManagementTmpSaveDao(CaseManagementTmpSaveDao dao);

    public void setAdmissionManager(AdmissionManager mgr);

    public void setUserPropertyDAO(UserPropertyDAO dao);

    public void setDxresearchDAO(DxresearchDAO dao);

    public void setSecRoleDao(SecRoleDao secRoleDao);

    public void saveToDx(LoggedInInfo loggedInInfo, String demographicNo, String code, String codingSystem,
            boolean association);

    public void saveToDx(LoggedInInfo loggedInInfo, String demographicNo, String code);

    public List<Dxresearch> getDxByDemographicNo(String demographicNo);

    public String getTemplateSignature(String template, ResourceBundle rc, Map<String, String> map);

    public String getSignature(String cproviderNo, String userName, String roleName, Locale locale, int type);

    public void seteChartDao(EChartDao eChartDao);

    public void setEncounterWindowDao(EncounterWindowDao encounterWindowDao);

    public CaseManagementNote getLastSaved(String programId, String demono, String providerNo, Map unlockedNotesMap);

    public CaseManagementNote makeNewNote(String providerNo, String demographicNo, String encType, String appointmentNo,
            Locale locale);

    public void addNewNoteLink(Long noteId);

    public CaseManagementNote saveCaseManagementNote(LoggedInInfo loggedInInfo, CaseManagementNote note,
            List<CaseManagementIssue> issuelist, CaseManagementCPP cpp, String ongoing, boolean verify, Locale locale,
            Date now, CaseManagementNote annotationNote, String userName, String user, String remoteAddr,
            String lastSavedNoteString) throws Exception;

    public void setCPPMedicalHistory(CaseManagementCPP cpp, String providerNo, List accessRight);

    public String listNotes(String code, String providerNo, String demoNo);

}