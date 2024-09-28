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
package ca.openosp.openo.managers;

import ca.openosp.openo.common.model.Demographic;
import ca.openosp.openo.common.model.Drug;
import ca.openosp.openo.common.model.Dxresearch;
import ca.openosp.openo.common.model.FormeCARES;
import ca.openosp.openo.common.model.Measurement;
import ca.openosp.openo.common.model.Prevention;
import ca.openosp.openo.common.model.Provider;
import ca.openosp.openo.common.model.Tickler;
import ca.openosp.openo.common.model.TicklerCategory;
import ca.openosp.openo.common.model.TicklerTextSuggest;
import ca.openosp.openo.ehrutil.EncounterUtil;
import ca.openosp.openo.ehrutil.JsonUtil;
import ca.openosp.openo.ehrutil.LoggedInInfo;
import ca.openosp.openo.ehrutil.MiscUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import org.apache.logging.log4j.Logger;
import org.caisi.service.IssueAdminManager;
import ca.openosp.openo.PMmodule.dao.ProgramProviderDAO;
import ca.openosp.openo.PMmodule.model.ProgramProvider;
import ca.openosp.openo.casemgmt.dao.CaseManagementNoteLinkDAO;
import ca.openosp.openo.casemgmt.dao.IssueDAO;
import ca.openosp.openo.casemgmt.model.CaseManagementIssue;
import ca.openosp.openo.casemgmt.model.CaseManagementNote;
import ca.openosp.openo.casemgmt.model.CaseManagementNoteLink;
import ca.openosp.openo.casemgmt.model.Issue;
import ca.openosp.openo.casemgmt.service.CaseManagementManager;
import ca.openosp.openo.common.dao.DrugDao;
import ca.openosp.openo.common.dao.DxresearchDAO;
import ca.openosp.openo.common.dao.FormeCARESDao;
import ca.openosp.openo.managers.constants.Constants;
import org.oscarehr.ehrutil.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ca.openosp.openo.oscarEncounter.data.EctProgram;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class FormeCARESManager {

    @Autowired
    private FormeCARESDao formeCARESDao;
    @Autowired
    private SecurityInfoManager securityInfoManager;
    @Autowired
    private DemographicManager demographicManager;
    @Autowired
    private MeasurementManager measurementManager;
    @Autowired
    private PreventionManager preventionManager;
    @Autowired
    private TicklerManager ticklerManager;
    @Autowired
    private ProviderManager2 providerManager;
    @Autowired
    private ProgramProviderDAO programProviderDao;
    @Autowired
    private CaseManagementNoteLinkDAO caseManagementNoteLinkDao;
    @Autowired
    private IssueDAO issueDao;

    private final IssueAdminManager issueAdminManager = new IssueAdminManager();

    /*
     * Ideally a manager should be created for the DrugsDao and the DxResearchDao.
     * A new manager was not created here because this project is
     * designed to be as compatible as possible with
     * all the different OSCAR forks.
     * Adding a manager may cause awkward merge conflicts.
     */
    @Autowired
    private DrugDao drugDao;
    @Autowired
    private DxresearchDAO dxresearchDAO;
    @Autowired
    private CaseManagementManager caseManagementManager;

    private final Logger logger = MiscUtils.getLogger();

    public final JSONObject save(LoggedInInfo loggedInInfo, String formData) {

        JSONObject formJsonData = (JSONObject) JsonUtil.jsonToPojo(formData, JSONObject.class);
        String demographicNo = (String) formJsonData.get(Constants.Cares.FormField.demographicNo.name());
        JSONObject responseMessage = new JSONObject();
        responseMessage.put(Constants.Cares.FormField.saved.name(), false);
        responseMessage.put(Constants.Cares.FormField.demographicNo.name(), demographicNo);

        if (!securityInfoManager.hasPrivilege(loggedInInfo, "_form", SecurityInfoManager.WRITE, demographicNo)) {
            throw new SecurityException("missing required security object (_form)");
        }

        String formId = (String) formJsonData.get(Constants.Cares.FormField.formId.name());
        int formIdInteger = 0;
        if (formId != null && !formId.isEmpty()) {
            formIdInteger = Integer.parseInt(formId);
        }

        // intercept to save the current frailty score into OSCAR measurements
        saveFrailtyScore(loggedInInfo, formJsonData);

        // create a new formeCARES object for every save.
        FormeCARES formeCARES = new FormeCARES();
        boolean completed = formJsonData.getBoolean(Constants.Cares.FormField.completed.name());
        formeCARES.setDemographicNo(Integer.parseInt(demographicNo));
        formeCARES.setProviderNo(loggedInInfo.getLoggedInProviderNo());
        formeCARES.setFormEdited(new Date());
        formeCARES.setFormData(formJsonData.toString());
        formeCARES.setCompleted(completed);

        if (formIdInteger == 0) {
            formeCARES.setFormCreated(new Date());
        } else {
            FormeCARES previousFormeCARES = formeCARESDao.find(formIdInteger);
            if (previousFormeCARES != null) {
                formeCARES.setFormCreated(previousFormeCARES.getFormCreated());

                /*
                 * This form will be rendered inactive if the completed
                 * value is set to true during a form save.
                 */
                Date completedDate = completeForm(completed, previousFormeCARES.getFormCreated(), Integer.parseInt(demographicNo));
                formeCARES.setCompletedDate(completedDate);
            }
        }

        formeCARESDao.persist(formeCARES);

        logger.debug("eCARES form id " + formeCARES.getId() + " saved.");

        if (formeCARES.getId() != null && formeCARES.getId() > 0) {
            responseMessage.put(Constants.Cares.FormField.saved.name(), true);
        }
        responseMessage.put(Constants.Cares.FormField.formId.name(), formeCARES.getId());

        return responseMessage;
    }

    public final JSONObject getData(LoggedInInfo loggedInInfo, int demographicNo, int formId) {
        if (!securityInfoManager.hasPrivilege(loggedInInfo, "_form", SecurityInfoManager.READ, demographicNo)) {
            throw new SecurityException("missing required security object (_form)");
        }

        logger.debug("Fetching eCARES form id " + formId + " for demographic number " + demographicNo);

        FormeCARES formeCARES = null;
        JSONObject jsonDataObject = null;

        // get previously saved data if the form id > 0
        if (formId > 0) {
            formeCARES = formeCARESDao.find(formId);
        }

        // get the pre-existing form data to populate into the open form
        if (formeCARES != null) {
            String jsonData = formeCARES.getFormData();
            jsonDataObject = (JSONObject) JSONSerializer.toJSON(jsonData);

            // set completion status
            jsonDataObject.put(Constants.Cares.FormField.completed.name(), formeCARES.isCompleted());
        }

        // create a new data object if no data pre-exists
        if (jsonDataObject == null) {
            jsonDataObject = new JSONObject();

            /* This is a new form. Check for existing incomplete forms.
             * to warn the user with incompleteFormExists = true/false
             */
            jsonDataObject.put(Constants.Cares.FormField.incompleteFormExists.name(), incompleteFormExists(demographicNo));
        }

        // set the current form id and demographic number
        jsonDataObject.put(Constants.Cares.FormField.demographicNo.name(), demographicNo);
        jsonDataObject.put(Constants.Cares.FormField.formId.name(), formId);

        // set demographic information. Chart information always overrides form information
        setDemographic(loggedInInfo, jsonDataObject);

        // set user information. User information is always the user that is logged in
        setProvider(loggedInInfo, jsonDataObject);

        // set measurements like the most recent Creatinine Clearance (eGFR)
        setMeasurements(loggedInInfo, jsonDataObject);

        // set the patient's current medications
        setMedications(loggedInInfo, jsonDataObject);

        // set the patient's problems. AKA: disease registry
        setProblems(loggedInInfo, jsonDataObject);

        // prepopulate immunization data.
        setPreventions(loggedInInfo, jsonDataObject);

        return jsonDataObject;
    }

    public final JSONObject createTickler(LoggedInInfo loggedInInfo, String tickerString) {
        if (!securityInfoManager.hasPrivilege(loggedInInfo, "_form", SecurityInfoManager.WRITE, null)) {
            throw new SecurityException("missing required security object (_form)");
        }

        JSONObject responseMessage = new JSONObject();
        boolean success = Boolean.FALSE;
        Tickler tickler = null;
        JSONObject formJsonData = null;

        if (tickerString != null & !tickerString.isEmpty()) {
            /* Would rather do this: tickler = (Tickler) JsonUtil.jsonToPojo(tickerString, Tickler.class);
             * but then a refactor of OSCAR's Tickler model is needed to handle the ENUMs correctly
             */
            formJsonData = (JSONObject) JsonUtil.jsonToPojo(tickerString, JSONObject.class);
            Provider assignedTo = providerManager.getProvider(loggedInInfo, formJsonData.getString(Constants.Cares.Tickler.taskAssignedTo.name()));
            tickler = new Tickler();

            try {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date serviceDate = simpleDateFormat.parse(formJsonData.getString(Constants.Cares.Tickler.serviceDate.name()));

                tickler.setServiceDate(serviceDate);
                tickler.setServiceTime(formJsonData.getString(Constants.Cares.Tickler.serviceTime.name()));
                tickler.setCreator(loggedInInfo.getLoggedInProviderNo());
                tickler.setAssignee(loggedInInfo.getLoggedInProvider());
                tickler.setTaskAssignedTo(assignedTo.getProviderNo());
                tickler.setTaskAssignedToName(assignedTo.getFullName());
                tickler.setCategoryId(formJsonData.getInt(Constants.Cares.Tickler.categoryId.name()));
                tickler.setMessage(formJsonData.getString(Constants.Cares.Tickler.message.name()));
                tickler.setPriorityAsString(formJsonData.getString(Constants.Cares.Tickler.priority.name()));
                tickler.setDemographicNo(formJsonData.getInt(Constants.Cares.Tickler.demographicNo.name()));
                tickler.setStatus(Tickler.STATUS.A);
                tickler.setUpdateDate(new Date());

            } catch (Exception e) {
                tickler = null;
                logger.error("Error with parsing date in Tickler for eCARES form", e);
            }
        }

        if (tickler != null) {
            success = ticklerManager.addTickler(loggedInInfo, tickler);
            try {
                long noteId = saveTicklerEncounterNote(loggedInInfo, formJsonData.getString(Constants.Cares.Tickler.comments.name()),
                        formJsonData.getInt(Constants.Cares.Tickler.demographicNo.name()));

                if (noteId > 0) {
                    //save link, so we know what tickler this note is linked to
                    CaseManagementNoteLink caseManagementNoteLink = new CaseManagementNoteLink();
                    caseManagementNoteLink.setNoteId(noteId);
                    caseManagementNoteLink.setTableId((long) tickler.getId());
                    caseManagementNoteLink.setTableName(CaseManagementNoteLink.TICKLER);

                    caseManagementNoteLinkDao.save(caseManagementNoteLink);

                    responseMessage.put(Constants.Cares.Tickler.id.name(), tickler.getId());
                }
            } catch (Exception e) {
                /* do nothing; to ensure that at least the tickler still saves. A note can
                 * be added later by the user.
                 */
                logger.error("Error saving encounter note for tickler id " + tickler.getId(), e);
            }
        }

        if (!success) {
            responseMessage.put("error", "Missing valid input data.");
        }

        responseMessage.put(Constants.Cares.Tickler.saved.name(), success);
        return responseMessage;
    }

    /**
     * Returns a blank tickler interface if no existing tickler is specified.
     *
     * @param loggedInInfo
     * @param tickerString
     * @return
     */
    public final Map<String, Object> getTickler(LoggedInInfo loggedInInfo, String tickerString) {
        if (!securityInfoManager.hasPrivilege(loggedInInfo, "_tickler", SecurityInfoManager.READ, null)) {
            throw new RuntimeException("missing required security object (_tickler)");
        }

        Map<String, Object> attributes = new HashMap<String, Object>();
        Tickler.PRIORITY[] priorities = Tickler.PRIORITY.values();
        List<TicklerTextSuggest> textSuggestions = ticklerManager.getActiveTextSuggestions(loggedInInfo);
        List<Provider> providers = providerManager.getProviders(loggedInInfo, Boolean.TRUE);
        List<TicklerCategory> ticklerCategories = ticklerManager.getActiveTicklerCategories(loggedInInfo);

        attributes.put(Constants.Cares.Tickler.priorities.name(), priorities);
        attributes.put(Constants.Cares.Tickler.textSuggestions.name(), textSuggestions);
        attributes.put(Constants.Cares.Tickler.providers.name(), providers);
        attributes.put(Constants.Cares.Tickler.ticklerCategories.name(), ticklerCategories);

        return attributes;
    }

    public final long saveTicklerEncounterNote(LoggedInInfo loggedInInfo, String note, int demographicNo) throws Exception {
        if (!securityInfoManager.isAllowedAccessToPatientRecord(loggedInInfo, demographicNo)) {
            throw new RuntimeException("missing required security object");
        }

        String programNumber = new EctProgram(loggedInInfo.getSession()).getProgram(loggedInInfo.getLoggedInProviderNo());
        ProgramProvider programProvider = programProviderDao.getProgramProvider(loggedInInfo.getLoggedInProviderNo(), Long.valueOf(programNumber));

        CaseManagementNote caseManagementNote = new CaseManagementNote();
        caseManagementNote.setNote(note);
        caseManagementNote.setHistory(note);
        caseManagementNote.setProviderNo(loggedInInfo.getLoggedInProviderNo());
        caseManagementNote.setDemographic_no(demographicNo + "");
        caseManagementNote.setAppointmentNo(0);
        caseManagementNote.setEncounter_type(EncounterUtil.EncounterType.FACE_TO_FACE_WITH_CLIENT.getOldDbValue());
        caseManagementNote.setSigned(Boolean.TRUE);
        caseManagementNote.setIncludeissue(Boolean.TRUE);
        caseManagementNote.setRevision("1");
        caseManagementNote.setSigned(true);
        caseManagementNote.setSigning_provider_no(loggedInInfo.getLoggedInProviderNo());
        caseManagementNote.setUpdate_date(new Date());
        caseManagementNote.setReporter_program_team("null");
        caseManagementNote.setUuid(null);
        caseManagementNote.setProgram_no(programNumber);
        caseManagementNote.setReporter_caisi_role(String.valueOf(programProvider.getRoleId()));
        caseManagementNote.setObservation_date(new Date());
        caseManagementNote.setCreate_date(new Date());

        caseManagementManager.saveNoteSimple(caseManagementNote);

        Issue issue = issueDao.findIssueByTypeAndCode(Issue.SYSTEM, Constants.Cares.Tickler.TicklerNote.name());
        CaseManagementIssue caseManagementIssue = caseManagementManager.getIssueById(demographicNo + "", issue.getId() + "");

        if (caseManagementIssue == null) {
            caseManagementIssue = new CaseManagementIssue();
            caseManagementIssue.setAcute(false);
            caseManagementIssue.setCertain(false);
            caseManagementIssue.setDemographic_no(demographicNo);
            caseManagementIssue.setIssue_id(issue.getId());
            caseManagementIssue.setMajor(false);
            caseManagementIssue.setProgram_id(Integer.parseInt(caseManagementNote.getProgram_no()));
            caseManagementIssue.setResolved(false);
            caseManagementIssue.setType(issue.getRole());
            caseManagementIssue.setUpdate_date(new Date());

            caseManagementManager.saveCaseIssue(caseManagementIssue);
        }

        caseManagementNote.getIssues().add(caseManagementIssue);

        caseManagementManager.updateNote(caseManagementNote);

        return caseManagementNote.getId();
    }

    // HELPER METHODS

    private boolean incompleteFormExists(int demographicNo) {
        List<FormeCARES> formeCARESList = formeCARESDao.findAllIncompleteByDemographicNumber(demographicNo);
        for (FormeCARES formeCARES : formeCARESList) {
            if (!formeCARES.isCompleted()) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }

    /**
     * Changes the form status to complete for all revisions of a specific form by the
     * creation date of the form.
     *
     * @param completed
     * @return Date of the completed date
     */
    private Date completeForm(boolean completed, Date formDate, int demographicNo) {
        Date completedDate = null;
        if (completed) {
            List<FormeCARES> formeCARESList = formeCARESDao.findAllByFormCreatedDateDemographicNo(formDate, demographicNo);
            completedDate = new Date();
            for (FormeCARES formeCARES : formeCARESList) {
                formeCARES.setCompletedDate(completedDate);
                formeCARES.setCompleted(completed);
                formeCARESDao.merge(formeCARES);
            }
        }
        return completedDate;
    }

    private void setDemographic(LoggedInInfo loggedInInfo, JSONObject jsonDataObject) {
        Demographic demographic = demographicManager.getDemographic(loggedInInfo, jsonDataObject.getInt(Constants.Cares.FormField.demographicNo.name()));
        jsonDataObject.put(Constants.Cares.FormField.patientFirstName.name(), demographic.getFirstName());
        jsonDataObject.put(Constants.Cares.FormField.patientLastName.name(), demographic.getLastName());
        jsonDataObject.put(Constants.Cares.FormField.patientPHN.name(), demographic.getHin());
        jsonDataObject.put(Constants.Cares.FormField.patientDOB.name(), demographic.getBirthDayAsString());
        jsonDataObject.put(Constants.Cares.FormField.patientGender.name(), demographic.getSex());
        jsonDataObject.put(Constants.Cares.FormField.patientAge, demographic.getAge());
    }

    private void setProvider(LoggedInInfo loggedInInfo, JSONObject jsonDataObject) {
        Provider provider = loggedInInfo.getLoggedInProvider();
        jsonDataObject.put(Constants.Cares.FormField.userFirstName.name(), provider.getFirstName());
        jsonDataObject.put(Constants.Cares.FormField.userLastName.name(), provider.getLastName());
        jsonDataObject.put(Constants.Cares.FormField.userFullName.name(), provider.getFullName());
        jsonDataObject.put(Constants.Cares.FormField.userSignature.name(), provider.getFormattedName());
    }

    /**
     * most recent Creatinine Clearance (eGFR)
     *
     * @param loggedInInfo   Authentication information for the current user
     * @param jsonDataObject Current JSON Object with form data
     */
    private void setMeasurements(LoggedInInfo loggedInInfo, JSONObject jsonDataObject) {
        List<String> typeList = new ArrayList<String>();
        typeList.add(Constants.Cares.Measurement.EGFR.name());
        typeList.add(Constants.Cares.Measurement.CRCL.name());

        /*
         * get the most recent creatine clearance (EGFR) from the patient chart.
         */
        Measurement recentMeasurement = getRecentMeasurement(loggedInInfo, typeList, jsonDataObject.getInt(Constants.Cares.FormField.demographicNo.name()));
        if (recentMeasurement != null) {
            jsonDataObject.put(Constants.Cares.FormField.crcl.name(), recentMeasurement.getDataField());
        }

        /*
         * get the entire history of the patient frailty index for display of
         * trending data in the form
         */
        typeList = new ArrayList<String>();
        typeList.add(Constants.Cares.Measurement.EFI.name());

        List<Measurement> measurementList = getRecentMeasurementList(loggedInInfo, typeList, jsonDataObject.getInt(Constants.Cares.FormField.demographicNo.name()));
        JSONArray jsonArray = new JSONArray();
        for (Measurement measurement : measurementList) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(Constants.Cares.Measurement.id.name(), measurement.getId());
            jsonObject.put(Constants.Cares.Measurement.date.name(), measurement.getCreateDate());
            jsonObject.put(Constants.Cares.Measurement.score.name(), measurement.getDataField());
            jsonArray.add(jsonObject);
        }

        jsonDataObject.put(Constants.Cares.FormField.efi_scores.name(), jsonArray);
    }

    private void setMedications(LoggedInInfo loggedInInfo, JSONObject jsonDataObject) {

        List<Drug> drugList = null;
        int demographicNo = jsonDataObject.getInt(Constants.Cares.FormField.demographicNo.name());

        /* add authorization for fetching Medications here
         * since a Drug manager does not exist in this version of ca.openosp.openo.
         */
        if (!securityInfoManager.hasPrivilege(loggedInInfo, "_newCasemgmt.prescriptions", SecurityInfoManager.READ, demographicNo)) {
            jsonDataObject.element(Constants.Cares.Medication.medications.name(), new String[]{"User not authorized to view medications for this patient. " +
                    "Missing required security object (_newCasemgmt.prescriptions)"});
        } else {
            drugList = drugDao.findByDemographicId(demographicNo, Boolean.FALSE);
        }

        // add only the prescription details about the medication into a JSON string.
        if (drugList != null && !drugList.isEmpty()) {
            JSONArray jsonArray = new JSONArray();

            outer:
            for (Drug drug : drugList) {
                JSONObject medicationObject;

                /* update and merge saved list. A saved medication can be
                 * "archived" when the user sets the active variable in the
                 * eCARES form.  This is only a "symbolic" delete.
                 */
                if (jsonDataObject.containsKey(Constants.Cares.Medication.medications.name())) {
                    Object savedMedications = jsonDataObject.get(Constants.Cares.Medication.medications.name());
                    if (savedMedications instanceof JSONArray) {
                        for (Object savedMedication : (JSONArray) savedMedications) {
                            if (savedMedication instanceof JSONObject) {
                                medicationObject = (JSONObject) savedMedication;
                                if (medicationObject.get(Constants.Cares.Medication.id.name()).equals(drug.getId())) {
                                    jsonArray.add(medicationObject);
                                    continue outer;
                                }
                            }
                        }
                    }
                }

                // create a new medication object if the process gets this far.
                medicationObject = new JSONObject();
                medicationObject.put(Constants.Cares.Medication.id.name(), drug.getId());
                medicationObject.put(Constants.Cares.Medication.rxDate.name(), drug.getRxDate() != null ? drug.getRxDate().toString() : "");
                medicationObject.put(Constants.Cares.Medication.prescription.name(), drug.getSpecial().replaceAll("\n", ""));
                medicationObject.put(Constants.Cares.Medication.active.name(), true);
                jsonArray.add(medicationObject);
            }

            jsonDataObject.put(Constants.Cares.Medication.medications.name(), jsonArray);
        }
    }

    private void setProblems(LoggedInInfo loggedInInfo, JSONObject jsonDataObject) {
        List<Dxresearch> dxresearchList = null;
        /* add authorization for fetching disease registry here
         * since a dxregistry manager not exist in this version of ca.openosp.openo.
         */
        int demographicNo = jsonDataObject.getInt(Constants.Cares.FormField.demographicNo.name());
        if (!securityInfoManager.hasPrivilege(loggedInInfo, "_newCasemgmt.DxRegistry", SecurityInfoManager.READ, demographicNo)) {
            jsonDataObject.element("problems", new String[]{"User not authorized to view diseases for this patient. " +
                    "Missing required security object (_newCasemgmt.DxRegistry)"});
        } else {
            dxresearchList = dxresearchDAO.getByDemographicNo(demographicNo);
        }

        if (dxresearchList != null && !dxresearchList.isEmpty()) {

            JSONArray jsonArray = new JSONArray();

            outer:
            for (Dxresearch dxresearch : dxresearchList) {
                JSONObject dxresearchObject;

                if ('A' == dxresearch.getStatus()) {

                    /* update and merge saved list. A saved disease can be
                     * "archived" when the user sets the active variable in the
                     * eCARES form.  This is only a "symbolic" delete.
                     */
                    if (jsonDataObject.containsKey(Constants.Cares.Problem.problems.name())) {
                        Object savedProblems = jsonDataObject.get(Constants.Cares.Problem.problems.name());
                        if (savedProblems instanceof JSONArray) {
                            for (Object savedProblem : (JSONArray) savedProblems) {
                                if (savedProblem instanceof JSONObject) {
                                    dxresearchObject = (JSONObject) savedProblem;
                                    if (dxresearchObject.get(Constants.Cares.Problem.id.name()).equals(dxresearch.getId())) {
                                        jsonArray.add(dxresearchObject);
                                        continue outer;
                                    }
                                }
                            }
                        }
                    }

                    dxresearchObject = new JSONObject();
                    dxresearchObject.put(Constants.Cares.Problem.id.name(), dxresearch.getId());
                    dxresearchObject.put(Constants.Cares.Problem.startdate.name(), dxresearch.getStartDate().toString());
                    dxresearchObject.put(Constants.Cares.Problem.code.name(), dxresearch.getCodingSystem() + "(" + dxresearch.getDxresearchCode() + ")");
                    String description = dxresearchDAO.getDescription(dxresearch.getCodingSystem(), dxresearch.getDxresearchCode());
                    dxresearchObject.put(Constants.Cares.Problem.description.name(), description);
                    dxresearchObject.put(Constants.Cares.Problem.active.name(), true);

                    jsonArray.add(dxresearchObject);
                }
            }

            jsonDataObject.put("problems", jsonArray);
        }

    }

    private void setPreventions(LoggedInInfo loggedInInfo, JSONObject jsonDataObject) {
        int demographicNo = jsonDataObject.getInt(Constants.Cares.FormField.demographicNo.name());
        List<Prevention> preventionList = preventionManager.getPreventionsByDemographicNo(loggedInInfo, demographicNo);
        if (preventionList != null && !preventionList.isEmpty()) {
            /*
             * Evidence of any Zoster, Pneu, Td, HepA or HepB
             * Any influenza done within the last year.
             */
            for (Prevention prevention : preventionList) {

                // this should never be null. Remove punctuation because enums dont like it.
                String preventionType = prevention.getPreventionType().replaceAll("[^a-zA-Z ]", "").trim();
                Date preventionDate = prevention.getPreventionDate();
                Calendar ayearago = Calendar.getInstance();
                ayearago.add(Calendar.YEAR, -1);

                if (!contains(preventionType, Constants.Cares.Prevention.values())) {
                    continue;
                }
                ;

                if (prevention.isComplete()) {
                    switch (Constants.Cares.Prevention.valueOf(preventionType)) {
                        case HepAB:
                            jsonDataObject.put(Constants.Cares.FormField.hep_a, "1");
                            jsonDataObject.put(Constants.Cares.FormField.hep_b, "1");
                            break;
                        case HepA:
                            jsonDataObject.put(Constants.Cares.FormField.hep_a, "1");
                            break;
                        case HepB:
                            jsonDataObject.put(Constants.Cares.FormField.hep_b, "1");
                            break;
                        case Td:
                            jsonDataObject.put(Constants.Cares.FormField.tetanus_and_diphtheria, "1");
                            break;
                        case Pneumovax:
                        case PneuC:
                            jsonDataObject.put(Constants.Cares.FormField.pneumococcal, "1");
                            break;
                        case Smoking:
                            jsonDataObject.put(Constants.Cares.FormField.smoke, "1");
                            break;
                        case Flu:
                            if (preventionDate.after(ayearago.getTime())) {
                                jsonDataObject.put(Constants.Cares.FormField.influenza, "1");
                            }
                            break;
                        case RZV:
                        case Zoster:
                        case HZV:
                            jsonDataObject.put(Constants.Cares.FormField.zoster, "1");
                            break;
                        default:
                            // do nothing
                            break;
                    }
                }
            }
        }
    }

    /**
     * check to see if an Enum value is contained in a
     * given list
     */
    private static boolean contains(String value, Enum[] list) {
        for (Enum item : list) {
            if (item.name().equals(value)) {
                return true;
            }
        }
        return false;
    }

    private void saveFrailtyScore(LoggedInInfo loggedInInfo, JSONObject jsonDataObject) {
        String currentFrailtyScore = null;
        if (jsonDataObject.containsKey(Constants.Cares.FormField.deficit_based_frailty_score.name())) {
            currentFrailtyScore = jsonDataObject.getString(Constants.Cares.FormField.deficit_based_frailty_score.name());
        }

        int demographicNo = jsonDataObject.getInt(Constants.Cares.FormField.demographicNo.name());

        if (currentFrailtyScore != null && !currentFrailtyScore.isEmpty()) {
            Measurement frailtyMeasurement = new Measurement();
            frailtyMeasurement.setProviderNo(loggedInInfo.getLoggedInProviderNo());
            frailtyMeasurement.setComments("EFI calculated from the eCARES form");
            frailtyMeasurement.setCreateDate(new Date());
            frailtyMeasurement.setDataField(currentFrailtyScore);
            frailtyMeasurement.setType(Constants.Cares.Measurement.EFI.name());
            frailtyMeasurement.setDemographicId(demographicNo);
            frailtyMeasurement.setDateObserved(new Date());
            measurementManager.addMeasurement(loggedInInfo, frailtyMeasurement);
        }
    }

    private Measurement getRecentMeasurement(LoggedInInfo loggedInInfo, List<String> typeList, int demographicNo) {
        List<Measurement> measurementList = getRecentMeasurementList(loggedInInfo, typeList, demographicNo);
        Measurement measurement = null;

        if (!measurementList.isEmpty()) {
            measurement = measurementList.get(0);
        }

        return measurement;
    }

    private List<Measurement> getRecentMeasurementList(LoggedInInfo loggedInInfo, List<String> typeList, int demographicNo) {
        List<Measurement> measurementList = measurementManager.getMeasurementByType(loggedInInfo, demographicNo, typeList);

        if (measurementList != null && !measurementList.isEmpty()) {
            Collections.sort(measurementList, DateCreatedComparator);
        }

        if (measurementList == null) {
            measurementList = Collections.emptyList();
        }

        return measurementList;
    }

    /**
     * This should be added to the Measurement model class.  It was inserted here
     * so that forked versions of OSCAR will not have issues merging this project.
     */
    private final Comparator<Measurement> DateCreatedComparator = new Comparator<Measurement>() {
        public int compare(Measurement o1, Measurement o2) {
            if (o1.getId() != null && o2.getId() != null) {
                return o2.getCreateDate().compareTo(o1.getCreateDate());
            }
            return 0;
        }
    };

}
