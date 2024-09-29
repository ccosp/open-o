//CHECKSTYLE:OFF
/**
 * Copyright (c) 2008-2012 Indivica Inc.
 * <p>
 * This software is made available under the terms of the
 * GNU General Public License, Version 2, 1991 (GPLv2).
 * License details are available via "indivica.ca/gplv2"
 * and "gnu.org/licenses/gpl-2.0.html".
 */

package ca.openosp.openo.documentManager.actions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import ca.openosp.openo.OscarProperties;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;
import org.apache.struts.upload.FormFile;
import ca.openosp.openo.PMmodule.model.ProgramProvider;
import ca.openosp.openo.common.dao.ProviderInboxRoutingDao;
import ca.openosp.openo.common.dao.QueueDocumentLinkDao;
import ca.openosp.openo.common.dao.UserPropertyDAO;
import ca.openosp.openo.common.model.UserProperty;
import ca.openosp.openo.documentManager.EDoc;
import ca.openosp.openo.documentManager.EDocUtil;
import ca.openosp.openo.documentManager.IncomingDocUtil;
import ca.openosp.openo.managers.ProgramManager2;
import ca.openosp.openo.managers.SecurityInfoManager;
import ca.openosp.openo.ehrutil.LoggedInInfo;
import ca.openosp.openo.ehrutil.MiscUtils;
import ca.openosp.openo.ehrutil.SpringUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import ca.openosp.openo.documentManager.data.DocumentUploadForm;
import ca.openosp.openo.log.LogAction;
import ca.openosp.openo.log.LogConst;

public class DocumentUploadAction extends DispatchAction {

    private static Logger logger = MiscUtils.getLogger();
    private SecurityInfoManager securityInfoManager = SpringUtils.getBean(SecurityInfoManager.class);

    public ActionForward executeUpload(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                       HttpServletResponse response) throws Exception {
        DocumentUploadForm fm = (DocumentUploadForm) form;

        if (!securityInfoManager.hasPrivilege(LoggedInInfo.getLoggedInInfoFromSession(request), "_edoc", "w", null)) {
            throw new SecurityException("missing required security object (_edoc)");
        }

        HashMap<String, Object> map = new HashMap<String, Object>();
        FormFile docFile = fm.getFiledata();
        String destination = request.getParameter("destination");
        java.util.Locale locale = (java.util.Locale) request.getSession().getAttribute(org.apache.struts.Globals.LOCALE_KEY);
        ResourceBundle props = ResourceBundle.getBundle("oscarResources", locale);
        if (docFile == null) {
            map.put("error", 4);
        } else if (destination != null && destination.equals("incomingDocs")) {
            String fileName = docFile.getFileName();
            if (!fileName.toLowerCase().endsWith(".pdf")) {
                map.put("error", props.getString("dms.documentUpload.onlyPdf"));
            } else if (docFile.getFileSize() == 0) {
                map.put("error", 4);
                throw new FileNotFoundException();
            } else {

                String queueId = request.getParameter("queue");
                String destFolder = request.getParameter("destFolder");

                File f = new File(IncomingDocUtil.getAndCreateIncomingDocumentFilePathName(queueId, destFolder, fileName));
                if (f.exists()) {
                    map.put("error", fileName + " " + props.getString("dms.documentUpload.alreadyExists"));

                } else {
                    boolean success = writeToIncomingDocs(docFile, queueId, destFolder, fileName);
                    if (!success) {
                        map.put("error", "Failed to write file. Please contact administrator");
                        MiscUtils.getLogger().error("Failed to write file to " + destFolder);
                    } else {
                        map.put("name", docFile.getFileName());
                        map.put("size", docFile.getFileSize());
                    }
                }
                request.getSession().setAttribute("preferredQueue", queueId);
                if (docFile != null) {
                    docFile.destroy();
                    docFile = null;
                }

            }
        } else {
            int numberOfPages = 0;
            String fileName = docFile.getFileName();
            String user = (String) request.getSession().getAttribute("user");
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            EDoc newDoc = new EDoc("", "", fileName, "", user, user, fm.getSource(), 'A',
                    simpleDateFormat.format(Calendar.getInstance().getTime()),
                    "", "", "demographic", "-1", 0);
            newDoc.setDocPublic("0");

            // if the document was added in the context of a program
            ProgramManager2 programManager = SpringUtils.getBean(ProgramManager2.class);
            LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(request);
            ProgramProvider pp = programManager.getCurrentProgramInDomain(loggedInInfo, loggedInInfo.getLoggedInProviderNo());
            if (pp != null && pp.getProgramId() != null) {
                newDoc.setProgramId(pp.getProgramId().intValue());
            }

            fileName = newDoc.getFileName();
            // save local file;
            if (docFile.getFileSize() == 0) {
                map.put("error", 4);
                throw new FileNotFoundException();
            }

            // write file to local dir
            writeLocalFile(docFile, fileName);
            newDoc.setContentType(docFile.getContentType());
            if (fileName.endsWith(".PDF") || fileName.endsWith(".pdf")) {
                newDoc.setContentType("application/pdf");
                // get number of pages when document is a PDF
                numberOfPages = countNumOfPages(fileName);
            }
            newDoc.setNumberOfPages(numberOfPages);
            String doc_no = EDocUtil.addDocumentSQL(newDoc);
            LogAction.addLog((String) request.getSession().getAttribute("user"), LogConst.ADD, LogConst.CON_DOCUMENT, doc_no, request.getRemoteAddr());

            String providerId = request.getParameter("provider");
            if (providerId != null) {
                WebApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(request.getSession().getServletContext());
                ProviderInboxRoutingDao providerInboxRoutingDao = (ProviderInboxRoutingDao) ctx.getBean(ProviderInboxRoutingDao.class);
                providerInboxRoutingDao.addToProviderInbox(providerId, Integer.parseInt(doc_no), "DOC");
            }

            String queueId = request.getParameter("queue");
            if (queueId != null && !queueId.equals("-1")) {
                WebApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(request.getSession().getServletContext());
                QueueDocumentLinkDao queueDocumentLinkDAO = (QueueDocumentLinkDao) ctx.getBean(QueueDocumentLinkDao.class);
                Integer qid = Integer.parseInt(queueId.trim());
                Integer did = Integer.parseInt(doc_no.trim());
                queueDocumentLinkDAO.addActiveQueueDocumentLink(qid, did);
                request.getSession().setAttribute("preferredQueue", queueId);
            }

            map.put("name", docFile.getFileName());
            map.put("size", docFile.getFileSize());

            if (docFile != null) {
                docFile.destroy();
                docFile = null;
            }
        }
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = JSONObject.fromObject(map);
        jsonArray.add(jsonObject);
        response.getOutputStream().write(jsonArray.toString().getBytes());
        return null;
    }

    /**
     * Counts the number of pages in a local pdf file.
     *
     * @param fileName the name of the file
     * @return the number of pages in the file
     */
    public int countNumOfPages(String fileName) {
        return EDocUtil.getPDFPageCount(fileName);
    }

    /**
     * Writes an uploaded file to disk
     *
     * @param docFile  the uploaded file
     * @param fileName the name for the file on disk
     * @throws Exception when an error occurs
     */
    private void writeLocalFile(FormFile docFile, String fileName) throws Exception {
        InputStream fis = null;
        FileOutputStream fos = null;
        try {
            fis = docFile.getInputStream();
            String savePath = OscarProperties.getInstance().getProperty("DOCUMENT_DIR") + "/" + fileName;
            fos = new FileOutputStream(savePath);
            byte[] buf = new byte[128 * 1024];
            int i = 0;
            while ((i = fis.read(buf)) != -1) {
                fos.write(buf, 0, i);
            }
        } catch (Exception e) {
            logger.debug(e.toString());
        } finally {
            if (fis != null)
                fis.close();
            if (fos != null)
                fos.close();
        }
    }

    private boolean writeToIncomingDocs(FormFile docFile, String queueId, String PdfDir, String fileName) {

        String parentPath = IncomingDocUtil.getIncomingDocumentFilePath(queueId, PdfDir);
        if (!new File(parentPath).exists()) {
            return false;
        }

        String savePath = IncomingDocUtil.getAndCreateIncomingDocumentFilePathName(queueId, PdfDir, fileName);
        try (InputStream fis = docFile.getInputStream();
             FileOutputStream fos = new FileOutputStream(savePath)) {
            IOUtils.copy(fis, fos);
        } catch (Exception e) {
            logger.debug(e.toString());
            return false;
        }

        return true;
    }

    public ActionForward setUploadDestination(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                              HttpServletResponse response) {

        String user_no = (String) request.getSession().getAttribute("user");
        String destination = request.getParameter("destination");
        UserPropertyDAO pref = (UserPropertyDAO) SpringUtils.getBean(UserPropertyDAO.class);
        UserProperty up = pref.getProp(user_no, UserProperty.UPLOAD_DOCUMENT_DESTINATION);

        if (up == null) {
            up = new UserProperty();
            up.setName(UserProperty.UPLOAD_DOCUMENT_DESTINATION);
            up.setProviderNo(user_no);
        }

        if (up.getValue() == null || !(up.getValue().equals(destination))) {
            up.setValue(destination);
            pref.saveProp(up);
        }
        return null;
    }

    public ActionForward setUploadIncomingDocumentFolder(ActionMapping mapping, ActionForm form, HttpServletRequest request,
                                                         HttpServletResponse response) {


        String user_no = (String) request.getSession().getAttribute("user");
        String destFolder = request.getParameter("destFolder");
        UserPropertyDAO pref = (UserPropertyDAO) SpringUtils.getBean(UserPropertyDAO.class);
        UserProperty up = pref.getProp(user_no, UserProperty.UPLOAD_INCOMING_DOCUMENT_FOLDER);

        if (up == null) {
            up = new UserProperty();
            up.setName(UserProperty.UPLOAD_INCOMING_DOCUMENT_FOLDER);
            up.setProviderNo(user_no);
        }

        if (up.getValue() == null || !(up.getValue().equals(destFolder))) {
            up.setValue(destFolder);
            pref.saveProp(up);
        }
        return null;
    }
}