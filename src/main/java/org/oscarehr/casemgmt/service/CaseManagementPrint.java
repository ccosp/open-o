//CHECKSTYLE:OFF
package org.oscarehr.casemgmt.service;

import com.lowagie.text.DocumentException;
import ca.openosp.openo.OscarProperties;
import ca.openosp.openo.oscarEncounter.data.EctProviderData;
import ca.openosp.openo.oscarEncounter.pageUtil.EctSessionBean;
import org.apache.logging.log4j.Logger;
import org.oscarehr.PMmodule.caisi_integrator.CaisiIntegratorManager;
import org.oscarehr.PMmodule.model.ProgramProvider;
import org.oscarehr.PMmodule.service.ProgramManager;
import org.oscarehr.caisi_integrator.ws.CachedDemographicNote;
import org.oscarehr.caisi_integrator.ws.DemographicWs;
import org.oscarehr.casemgmt.model.CaseManagementNote;
import org.oscarehr.casemgmt.model.CaseManagementNoteExt;
import org.oscarehr.casemgmt.model.Issue;
import org.oscarehr.casemgmt.util.ExtPrint;
import org.oscarehr.casemgmt.web.NoteDisplay;
import org.oscarehr.casemgmt.web.NoteDisplayLocal;
import org.oscarehr.common.model.Prevention;
import org.oscarehr.managers.PreventionManager;
import org.oscarehr.managers.ProgramManager2;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;
import ca.openosp.openo.oscarLab.ca.all.pageUtil.LabPDFCreator;
import ca.openosp.openo.oscarLab.ca.all.pageUtil.OLISLabPDFCreator;
import ca.openosp.openo.oscarLab.ca.all.parsers.Factory;
import ca.openosp.openo.oscarLab.ca.all.parsers.MessageHandler;
import ca.openosp.openo.oscarLab.ca.all.parsers.OLISHL7Handler;
import ca.openosp.openo.oscarLab.ca.on.CommonLabResultData;
import ca.openosp.openo.oscarLab.ca.on.LabResultData;
import ca.openosp.openo.util.ConcatPDF;
import ca.openosp.openo.util.ConversionUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class CaseManagementPrint {

    private static Logger logger = MiscUtils.getLogger();


    CaseManagementManager caseManagementMgr = SpringUtils.getBean(CaseManagementManager.class);

    private NoteService noteService = SpringUtils.getBean(NoteService.class);


    private ProgramManager2 programManager2 = SpringUtils.getBean(ProgramManager2.class);

    private ProgramManager programMgr = SpringUtils.getBean(ProgramManager.class);

    private PreventionManager preventionManager = SpringUtils.getBean(PreventionManager.class);

    /*
     *This method was in CaseManagementEntryAction but has been moved out so that both the classic Echart and the flat echart can use the same printing method.
     *
     */
    public void doPrint(LoggedInInfo loggedInInfo, Integer demographicNo, boolean printAllNotes, String[] noteIds, boolean printCPP, boolean printRx, boolean printLabs, boolean printPreventions, boolean useDateRange, Calendar startDate, Calendar endDate, HttpServletRequest request, OutputStream os) throws IOException, com.lowagie.text.DocumentException, com.itextpdf.text.DocumentException {

        String providerNo = loggedInInfo.getLoggedInProviderNo();


        if (printAllNotes) {
            noteIds = getAllNoteIds(loggedInInfo, request, "" + demographicNo);
        }

        if (useDateRange) {
            noteIds = getAllNoteIdsWithinDateRange(loggedInInfo, request, "" + demographicNo, startDate.getTime(), endDate.getTime());
        }
        logger.debug("NOTES2PRINT: " + noteIds);

        String demono = "" + demographicNo;
        request.setAttribute("demoName", getDemoName(demono));
        request.setAttribute("demoSex", getDemoSex(demono));
        request.setAttribute("demoAge", getDemoAge(demono));
        request.setAttribute("demoPhn", getDemoPhn(demono));
        request.setAttribute("mrp", getMRP(request, demono));
        String dob = getDemoDOB(demono);
        dob = convertDateFmt(dob, request);
        request.setAttribute("demoDOB", dob);


        List<CaseManagementNote> notes = new ArrayList<CaseManagementNote>();
        List<String> remoteNoteUUIDs = new ArrayList<String>();
        String uuid;
        for (int idx = 0; idx < noteIds.length; ++idx) {
            if (noteIds[idx].startsWith("UUID")) {
                uuid = noteIds[idx].substring(4);
                remoteNoteUUIDs.add(uuid);
            } else {
                Long noteId = ConversionUtils.fromLongString(noteIds[idx]);
                if (noteId > 0) {
                    CaseManagementNote note = this.caseManagementMgr.getNote(noteId.toString());
                    if (note != null && note.getProviderNo() != null
                            && (note.getProviderNo().isEmpty() || Integer.parseInt(note.getProviderNo()) != -1)) {
                        notes.add(note);
                    }
                }
            }
        }

        if (loggedInInfo.getCurrentFacility().isIntegratorEnabled() && remoteNoteUUIDs.size() > 0) {
            DemographicWs demographicWs = CaisiIntegratorManager.getDemographicWs(loggedInInfo, loggedInInfo.getCurrentFacility());
            List<CachedDemographicNote> remoteNotes = demographicWs.getLinkedCachedDemographicNotes(Integer.parseInt(demono));
            for (CachedDemographicNote remoteNote : remoteNotes) {
                for (String remoteUUID : remoteNoteUUIDs) {
                    if (remoteUUID.equals(remoteNote.getCachedDemographicNoteCompositePk().getUuid())) {
                        CaseManagementNote fakeNote = getFakedNote(remoteNote);
                        notes.add(fakeNote);
                        break;
                    }
                }
            }
        }

        // we're not guaranteed any ordering of notes given to us, so sort by observation date
        OscarProperties p = OscarProperties.getInstance();
        String noteSort = p.getProperty("CMESort", "");
        if (noteSort.trim().equalsIgnoreCase("UP")) {
            Collections.sort(notes, CaseManagementNote.noteObservationDateComparator);
            Collections.reverse(notes);
        } else {
            Collections.sort(notes, CaseManagementNote.noteObservationDateComparator);
        }

        //How should i filter out observation dates?
        if (!printAllNotes && (startDate != null && endDate != null)) {
            List<CaseManagementNote> dateFilteredList = new ArrayList<CaseManagementNote>();
            logger.debug("start date " + startDate);
            logger.debug("end date " + endDate);

            for (CaseManagementNote cmn : notes) {
                logger.debug("cmn " + cmn.getId() + "  -- " + cmn.getObservation_date() + " ? start date " + startDate.getTime().before(cmn.getObservation_date()) + " end date " + endDate.getTime().after(cmn.getObservation_date()));
                if (startDate.getTime().before(cmn.getObservation_date()) && endDate.getTime().after(cmn.getObservation_date())) {
                    dateFilteredList.add(cmn);
                }
            }
            notes = dateFilteredList;
        }

        List<CaseManagementNote> issueNotes;
        List<CaseManagementNote> tmpNotes;
        HashMap<String, List<CaseManagementNote>> cpp = null;
        if (printCPP) {
            cpp = new HashMap<String, List<CaseManagementNote>>();
            String[] issueCodes = {"OMeds", "SocHistory", "MedHistory", "Concerns", "Reminders", "FamHistory", "RiskFactors"};
            for (int j = 0; j < issueCodes.length; ++j) {
                List<Issue> issues = caseManagementMgr.getIssueInfoByCode(providerNo, issueCodes[j]);
                String[] issueIds = getIssueIds(issues);// = new String[issues.size()];
                tmpNotes = caseManagementMgr.getNotes(demono, issueIds);
                issueNotes = new ArrayList<CaseManagementNote>();
                for (int k = 0; k < tmpNotes.size(); ++k) {
                    if (!tmpNotes.get(k).isLocked() && !tmpNotes.get(k).isArchived()) {
                        List<CaseManagementNoteExt> exts = caseManagementMgr.getExtByNote(tmpNotes.get(k).getId());
                        boolean exclude = false;
                        for (CaseManagementNoteExt ext : exts) {
                            if (ext.getKeyVal().equals("Hide Cpp")) {
                                if (ext.getValue().equals("1")) {
                                    exclude = true;
                                }
                            }
                        }
                        if (!exclude) {
                            issueNotes.add(tmpNotes.get(k));
                        }
                    }
                }
                cpp.put(issueCodes[j], issueNotes);
            }
        }
        String demoNo = null;
        List<CaseManagementNote> othermeds = null;
        if (printRx) {
            demoNo = demono;
            if (cpp == null) {
                List<Issue> issues = caseManagementMgr.getIssueInfoByCode(providerNo, "OMeds");
                String[] issueIds = getIssueIds(issues);// new String[issues.size()];
                othermeds = caseManagementMgr.getNotes(demono, issueIds);
            } else {
                othermeds = cpp.get("OMeds");
            }
        }

        List<Prevention> preventions = null;
        if (printPreventions) {
            preventions = preventionManager.getPreventionsByDemographicNo(loggedInInfo, Integer.parseInt(demono));
        }

        SimpleDateFormat headerFormat = new SimpleDateFormat("yyyy-MM-dd.hh.mm.ss");
        Date now = new Date();
        String headerDate = headerFormat.format(now);

        // Create new file to save form to
        String path = OscarProperties.getInstance().getProperty("DOCUMENT_DIR");
        String fileName = path + "EncounterForm-" + headerDate + ".pdf";
        File file = null;
        FileOutputStream out = null;
        File file2 = null;
        FileOutputStream os2 = null;

        FileOutputStream fos = null;
        List<Object> pdfDocs = new ArrayList<Object>();


        try {

            file = new File(fileName);
            out = new FileOutputStream(file);

            CaseManagementPrintPdf printer = new CaseManagementPrintPdf(request, out);
            printer.printDocHeaderFooter();
            printer.printCPP(cpp);
            printer.printRx(demoNo, othermeds);
            printer.printPreventions(preventions);
            printer.printNotes(notes);

            /* check extensions */
            Enumeration<String> e = request.getParameterNames();
            while (e.hasMoreElements()) {
                String name = e.nextElement();
                if (name.startsWith("extPrint")) {
                    if (request.getParameter(name).equals("true")) {
                        ExtPrint printBean = (ExtPrint) SpringUtils.getBean(name);
                        if (printBean != null) {
                            printBean.printExt(printer, request);
                        }
                    }
                }
            }
            printer.finish();

            pdfDocs.add(fileName);

            if (printLabs) {
                // get the labs which fall into the date range which are attached to this patient
                CommonLabResultData comLab = new CommonLabResultData();
                ArrayList<LabResultData> labs = comLab.populateLabResultsData(loggedInInfo, "", demono, "", "", "", "U");

                Collections.sort(labs);

                LinkedHashMap<String, LabResultData> accessionMap = new LinkedHashMap<String, LabResultData>();
                for (int i = 0; i < labs.size(); i++) {
                    LabResultData result = labs.get(i);
                    if (result.isHL7TEXT()) {
                        if (result.accessionNumber == null || result.accessionNumber.equals("")) {
                            accessionMap.put("noAccessionNum" + i + result.labType, result);
                        } else {
                            if (!accessionMap.containsKey(result.accessionNumber + result.labType))
                                accessionMap.put(result.accessionNumber + result.labType, result);
                        }
                    }
                }

                for (LabResultData result : accessionMap.values()) {
                    //Date d = result.getDateObj();
                    // TODO:filter out the ones which aren't in our date range if there's a date range????
                    String segmentId = result.segmentID;
                    MessageHandler handler = Factory.getHandler(segmentId);
                    String fileName2 = OscarProperties.getInstance().getProperty("DOCUMENT_DIR") + "//" + handler.getPatientName().replaceAll("\\s", "_") + "_" + handler.getMsgDate() + "_LabReport.pdf";
                    file2 = new File(fileName2);
                    os2 = new FileOutputStream(file2);

                    if (handler instanceof OLISHL7Handler) {
                        OLISLabPDFCreator olisLabPdfCreator = new OLISLabPDFCreator(os2, request, segmentId);
                        olisLabPdfCreator.printPdf();
                        os2.close();
                        pdfDocs.add(fileName2);
                    } else {

                        LabPDFCreator pdfCreator = new LabPDFCreator(os2, segmentId, loggedInInfo.getLoggedInProviderNo());
                        try {
                            pdfCreator.printPdf();
                        } catch (DocumentException documentException) {
                            throw new DocumentException(documentException);
                        }
                        os2.close();

                        String fileName3 = OscarProperties.getInstance().getProperty("DOCUMENT_DIR") + "//" + handler.getPatientName().replaceAll("\\s", "_") + "_" + handler.getMsgDate() + "_LabReport.1.pdf";
                        File file3 = new File(fileName3);
                        fos = new FileOutputStream(file3);
                        pdfCreator.addEmbeddedDocuments(file2, fos);
                        pdfDocs.add(fileName3);

                    }
                }

            }
            ConcatPDF.concat(pdfDocs, os);
        } catch (IOException e) {
            logger.error("Error ", e);

        } finally {
            if (out != null) {
                out.close();
            }
            if (os2 != null) {
                os2.close();
            }
            if (fos != null) {
                fos.close();
            }
            if (file != null) {
                file.delete();
            }
            if (file2 != null) {
                file2.delete();
            }
            for (Object o : pdfDocs) {
                new File((String) o).delete();
            }
        }

    }

    public String[] getIssueIds(List<Issue> issues) {
        String[] issueIds = new String[issues.size()];
        int idx = 0;
        for (Issue i : issues) {
            issueIds[idx] = String.valueOf(i.getId());
            ++idx;
        }
        return issueIds;
    }

    private CaseManagementNote getFakedNote(CachedDemographicNote remoteNote) {
        CaseManagementNote note = new CaseManagementNote();

        if (remoteNote.getObservationDate() != null)
            note.setObservation_date(remoteNote.getObservationDate().getTime());
        note.setNote(remoteNote.getNote());

        return (note);
    }


    @SuppressWarnings("unchecked")
    private String[] getAllNoteIdsWithinDateRange(LoggedInInfo loggedInInfo, HttpServletRequest request, String demoNo, Date startDate, Date endDate) {

        HttpSession se = loggedInInfo.getSession();

        ProgramProvider pp = programManager2.getCurrentProgramInDomain(loggedInInfo, loggedInInfo.getLoggedInProviderNo());
        String programId = null;

        if (pp != null && pp.getProgramId() != null) {
            programId = "" + pp.getProgramId();
        } else {
            programId = String.valueOf(programMgr.getProgramIdByProgramName("OSCAR")); //Default to the oscar program if provider hasn't been assigned to a program
        }

        NoteSelectionCriteria criteria = new NoteSelectionCriteria();
        criteria.setStartDate(startDate);
        criteria.setEndDate(endDate);
        criteria.setMaxResults(Integer.MAX_VALUE);
        criteria.setDemographicId(ConversionUtils.fromIntString(demoNo));
        criteria.setUserRole((String) request.getSession().getAttribute("userrole"));
        criteria.setUserName((String) request.getSession().getAttribute("user"));
        if (request.getParameter("note_sort") != null && request.getParameter("note_sort").length() > 0) {
            criteria.setNoteSort(request.getParameter("note_sort"));
        }
        if (programId != null && !programId.trim().isEmpty()) {
            criteria.setProgramId(programId);
        }


        if (se.getAttribute("CaseManagementViewAction_filter_roles") != null) {
            criteria.getRoles().addAll((List<String>) se.getAttribute("CaseManagementViewAction_filter_roles"));
        }

        if (se.getAttribute("CaseManagementViewAction_filter_providers") != null) {
            criteria.getProviders().addAll((List<String>) se.getAttribute("CaseManagementViewAction_filter_providers"));
        }

        if (se.getAttribute("CaseManagementViewAction_filter_providers") != null) {
            criteria.getIssues().addAll((List<String>) se.getAttribute("CaseManagementViewAction_filter_issues"));
        }


        if (logger.isDebugEnabled()) {
            logger.debug("SEARCHING FOR NOTES WITH CRITERIA: " + criteria);
        }

        NoteSelectionResult result = noteService.findNotes(loggedInInfo, criteria);


        List<String> buf = new ArrayList<String>();
        for (NoteDisplay nd : result.getNotes()) {
            if (!(nd instanceof NoteDisplayLocal)) {
                continue;
            }
            buf.add(nd.getNoteId().toString());
        }


        return buf.toArray(new String[0]);
    }

    @SuppressWarnings("unchecked")
    private String[] getAllNoteIds(LoggedInInfo loggedInInfo, HttpServletRequest request, String demoNo) {

        HttpSession se = loggedInInfo.getSession();

        ProgramProvider pp = programManager2.getCurrentProgramInDomain(loggedInInfo, loggedInInfo.getLoggedInProviderNo());
        String programId = null;

        if (pp != null && pp.getProgramId() != null) {
            programId = "" + pp.getProgramId();
        } else {
            programId = String.valueOf(programMgr.getProgramIdByProgramName("OSCAR")); //Default to the oscar program if provider hasn't been assigned to a program
        }

        NoteSelectionCriteria criteria = new NoteSelectionCriteria();
        criteria.setMaxResults(Integer.MAX_VALUE);
        criteria.setDemographicId(ConversionUtils.fromIntString(demoNo));
        criteria.setUserRole((String) request.getSession().getAttribute("userrole"));
        criteria.setUserName((String) request.getSession().getAttribute("user"));
        if (request.getParameter("note_sort") != null && request.getParameter("note_sort").length() > 0) {
            criteria.setNoteSort(request.getParameter("note_sort"));
        }
        if (programId != null && !programId.trim().isEmpty()) {
            criteria.setProgramId(programId);
        }


        if (se.getAttribute("CaseManagementViewAction_filter_roles") != null) {
            criteria.getRoles().addAll((List<String>) se.getAttribute("CaseManagementViewAction_filter_roles"));
        }

        if (se.getAttribute("CaseManagementViewAction_filter_providers") != null) {
            criteria.getProviders().addAll((List<String>) se.getAttribute("CaseManagementViewAction_filter_providers"));
        }

        if (se.getAttribute("CaseManagementViewAction_filter_issues") != null) {
            criteria.getIssues().addAll((List<String>) se.getAttribute("CaseManagementViewAction_filter_issues"));
        }

        if (logger.isDebugEnabled()) {
            logger.debug("SEARCHING FOR NOTES WITH CRITERIA: " + criteria);
        }

        NoteSelectionResult result = noteService.findNotes(loggedInInfo, criteria);


        List<String> buf = new ArrayList<String>();
        for (NoteDisplay nd : result.getNotes()) {
            if (!(nd instanceof NoteDisplayLocal)) {
                continue;
            }
            buf.add(nd.getNoteId().toString());
        }


        return buf.toArray(new String[0]);
    }


    protected String getDemoName(String demoNo) {
        if (demoNo == null) {
            return "";
        }
        return caseManagementMgr.getDemoName(demoNo);
    }

    protected String getDemoSex(String demoNo) {
        if (demoNo == null) {
            return "";
        }
        return caseManagementMgr.getDemoGender(demoNo);
    }

    protected String getDemoAge(String demoNo) {
        if (demoNo == null) return "";
        return caseManagementMgr.getDemoAge(demoNo);
    }

    protected String getDemoDOB(String demoNo) {
        if (demoNo == null) return "";
        return caseManagementMgr.getDemoDOB(demoNo);
    }

    protected String getDemoPhn(String demoNo) {
        if (demoNo == null) return "";
        return caseManagementMgr.getDemoPhn(demoNo);
    }

    protected String getMRP(HttpServletRequest request, String demographicNo) {
        String strBeanName = "casemgmt_oscar_bean" + demographicNo;
        EctSessionBean bean = (EctSessionBean) request.getSession().getAttribute(strBeanName);
        if (bean == null) return new String("");
        if (bean.familyDoctorNo == null) return new String("");
        if (bean.familyDoctorNo.isEmpty()) return new String("");

        EctProviderData.Provider prov = new EctProviderData().getProvider(bean.familyDoctorNo);
        String name = prov.getFirstName() + " " + prov.getSurname();
        return name;
    }

    protected String convertDateFmt(String strOldDate, HttpServletRequest request) {
        String strNewDate = new String();
        if (strOldDate != null && strOldDate.length() > 0) {
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd", request.getLocale());
            try {

                Date tempDate = fmt.parse(strOldDate);
                strNewDate = new SimpleDateFormat("dd-MMM-yyyy", request.getLocale()).format(tempDate);

            } catch (ParseException ex) {
                MiscUtils.getLogger().error("Error", ex);
            }
        }

        return strNewDate;
    }

}
