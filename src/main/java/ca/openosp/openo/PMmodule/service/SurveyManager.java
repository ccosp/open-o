//CHECKSTYLE:OFF
/**
 * Copyright (c) 2005-2012. Centre for Research on Inner City Health, St. Michael's Hospital, Toronto. All Rights Reserved.
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
 * This software was written for
 * Centre for Research on Inner City Health, St. Michael's Hospital,
 * Toronto, Ontario, Canada
 */

package ca.openosp.openo.PMmodule.service;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.apache.struts.util.LabelValueBean;
import ca.openosp.openo.PMmodule.dao.SurveySecurityDao;
import ca.openosp.openo.PMmodule.dao.SurveySecurityDaoImpl;
import ca.openosp.openo.PMmodule.web.reports.custom.CustomReportDataSource;
import ca.openosp.openo.PMmodule.web.reports.custom.Item;
import ca.openosp.openo.common.dao.CaisiFormDao;
import ca.openosp.openo.common.dao.CaisiFormDataDao;
import ca.openosp.openo.common.dao.CaisiFormDataTmpSaveDao;
import ca.openosp.openo.common.dao.CaisiFormInstanceDao;
import ca.openosp.openo.common.dao.CaisiFormInstanceTmpSaveDao;
import ca.openosp.openo.common.model.CaisiForm;
import ca.openosp.openo.common.model.CaisiFormData;
import ca.openosp.openo.common.model.CaisiFormDataTmpSave;
import ca.openosp.openo.common.model.CaisiFormInstance;
import ca.openosp.openo.common.model.CaisiFormInstanceTmpSave;
import ca.openosp.openo.surveymodel.Page;
import ca.openosp.openo.surveymodel.Question;
import ca.openosp.openo.surveymodel.Section;
import ca.openosp.openo.surveymodel.SurveyDocument;
import ca.openosp.openo.ehrutil.LoggedInInfo;
import ca.openosp.openo.ehrutil.MiscUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value = "surveyManager2")
public class SurveyManager implements CustomReportDataSource {

    Logger log = MiscUtils.getLogger();

    @Autowired
    private CaisiFormDao caisiFormDao;
    @Autowired
    private CaisiFormDataDao caisiFormDataDao;
    @Autowired
    private CaisiFormInstanceDao caisiFormInstanceDao;
    @Autowired
    private CaisiFormDataTmpSaveDao caisiFormDataTmpSaveDao;
    @Autowired
    private CaisiFormInstanceTmpSaveDao caisiFormInstanceTmpSaveDao;

    public List getAllFormsForCurrentProviderAndCurrentFacility(LoggedInInfo loggedInInfo) {
        List<CaisiForm> allForms = caisiFormDao.findActiveByFacilityIdOrNull(loggedInInfo.getCurrentFacility().getId());
        List<CaisiForm> results = new ArrayList<CaisiForm>();
        SurveySecurityDao securityDao = new SurveySecurityDaoImpl();
        // filter out the ones due to security
        for (CaisiForm form : allForms) {
            // String name = form.getDescription().toLowerCase().replaceAll(" ","_");
            String name = form.getDescription();

            if (securityDao.checkPrivilege(name, loggedInInfo.getLoggedInProviderNo())) {
                results.add(form);
            }

        }
        return results;
    }

    public CaisiForm getForm(String formId) {
        return caisiFormDao.find(Integer.valueOf(formId));
    }

    public void saveFormInstance(CaisiFormInstance instance) {
        caisiFormInstanceDao.persist(instance);

        /*
         * for(Iterator<CaisiFormData> iter=dataList.iterator();iter.hasNext();) {
         * CaisiFormData data = iter.next();
         * data.setInstanceId(instance.getId());
         * this.caisiFormDataDao.persist(data);
         * }
         */

    }

    public void deleteTmpsave(String instanceId, String formId, String clientId, String providerId) {
        List tmpInstances = getTmpForms(instanceId, formId, clientId, providerId);
        if (tmpInstances.size() > 0 && tmpInstances != null) {
            CaisiFormInstanceTmpSave tmpInstance = (CaisiFormInstanceTmpSave) tmpInstances.get(0);
            /*
             * for(Iterator iter=tmpInstance.getData().iterator();iter.hasNext();) {
             * CaisiFormDataTmpSave data = (CaisiFormDataTmpSave)iter.next();
             * this.caisiFormDataTmpSaveDao.remove(data);
             * }
             */
            caisiFormInstanceTmpSaveDao.remove(tmpInstance.getId());
        }
    }

    public void saveFormInstanceTmpsave(CaisiFormInstanceTmpSave instance) {
        /*
         * for(Iterator iter=instance.getData().iterator();iter.hasNext();) {
         * CaisiFormDataTmpSave data = (CaisiFormDataTmpSave)iter.next();
         * this.caisiFormDataTmpSaveDao.persist(data);
         * }
         */
        caisiFormInstanceTmpSaveDao.persist(instance);
    }

    public CaisiFormInstance getLatestForm(String formId, String clientId) {
        return caisiFormInstanceDao.getLatestForm(Integer.valueOf(formId), Integer.valueOf(clientId));
    }

    public List getFormsForCurrentProviderAndCurrentFacility(LoggedInInfo loggedInInfo, String clientId) {
        List<CaisiFormInstance> forms = caisiFormInstanceDao.getForms(Integer.valueOf(clientId),
                loggedInInfo.getCurrentFacility().getId());
        List<CaisiFormInstance> results = new ArrayList<CaisiFormInstance>();
        SurveySecurityDao securityDao = new SurveySecurityDaoImpl();

        for (CaisiFormInstance form : forms) {
            String name = form.getDescription();

            if (securityDao.checkPrivilege(name, loggedInInfo.getLoggedInProviderNo())) {
                results.add(form);
            }
        }
        return results;
    }

    public List getForms(String clientId) {
        return caisiFormInstanceDao.getForms(Long.valueOf(clientId));
    }

    public List getForms(String formId, String clientId) {
        return caisiFormInstanceDao.getForms(Integer.valueOf(formId), Integer.valueOf(clientId));
    }

    public List getFormsByFacility(String clientId, Integer facilityId) {
        return caisiFormInstanceDao.getForms(Integer.valueOf(clientId), facilityId);
    }

    public CaisiFormInstance getCurrentFormById(String formInstanceId) {
        return caisiFormInstanceDao.find(Integer.valueOf(formInstanceId));
    }

    public List getTmpForms(String instanceId, String formId, String clientId, String providerId) {
        return caisiFormInstanceTmpSaveDao.getTmpForms(Integer.valueOf(instanceId), Integer.valueOf(formId),
                Integer.valueOf(clientId), Integer.valueOf(providerId));
    }

    public List getTmpFormData(String tmpInstanceId) {
        return caisiFormDataTmpSaveDao.getTmpFormData(Long.valueOf(tmpInstanceId));
    }

    public void saveFormData(CaisiFormData data) {
        caisiFormDataDao.persist(data);
    }

    public void saveFormDataTmpsave(CaisiFormDataTmpSave data) {
        caisiFormDataTmpSaveDao.persist(data);
    }

    public SurveyDocument.Survey getFormModel(String formId) {
        CaisiForm survey = getForm(formId);
        if (survey != null) {
            try {
                String xml = survey.getSurveyData();
                SurveyDocument model = SurveyDocument.Factory.parse(new StringReader(xml));
                return model.getSurvey();
            } catch (Exception e) {
                MiscUtils.getLogger().error("Error", e);
            }
        }
        return null;
    }

    public List getCurrentForms(String formId, List clients) {
        return caisiFormInstanceDao.getCurrentForms(formId, clients);
    }

    public LabelValueBean[] getFormNames() {
        List<CaisiForm> forms = caisiFormDao.getCaisiForms();
        LabelValueBean[] results = new LabelValueBean[forms.size()];
        int x = 0;
        for (CaisiForm form : forms) {
            results[x] = new LabelValueBean(form.getDescription(), String.valueOf(form.getId()));
            x++;
        }
        return results;
    }

    public LabelValueBean[] getItems(String formId) {
        List<LabelValueBean> results = new ArrayList<LabelValueBean>();
        SurveyDocument.Survey survey = getFormModel(formId);
        if (survey == null) {
            return results.toArray(new LabelValueBean[results.size()]);
        }

        int pageId = 1;
        int sectionId = 0;
        for (Page page : survey.getBody().getPageArray()) {
            for (Page.QContainer qcontainer : page.getQContainerArray()) {
                sectionId = 0;
                if (qcontainer.isSetQuestion()) {
                    Question question = qcontainer.getQuestion();
                    String id = pageId + "_" + sectionId + "_" + question.getId();
                    results.add(new LabelValueBean(question.getDescription(), id));
                } else {
                    Section section = qcontainer.getSection();
                    sectionId = section.getId();
                    for (Question question : section.getQuestionArray()) {
                        String id = pageId + "_" + sectionId + "_" + question.getId();
                        results.add(new LabelValueBean(question.getDescription(), id));
                    }
                }
            }
        }
        return results.toArray(new LabelValueBean[results.size()]);
    }

    public Item getItem(String formId, String id) {
        SurveyDocument.Survey survey = getFormModel(formId);
        if (survey == null) {
            return null;
        }

        int pageId = 1;
        int sectionId = 0;
        for (Page page : survey.getBody().getPageArray()) {
            for (Page.QContainer qcontainer : page.getQContainerArray()) {
                sectionId = 0;
                if (qcontainer.isSetQuestion()) {
                    Question question = qcontainer.getQuestion();
                    String tmpId = pageId + "_" + sectionId + "_" + question.getId();
                    if (id.equals(tmpId)) {
                        return createItem(question, id);
                    }
                } else {
                    Section section = qcontainer.getSection();
                    sectionId = section.getId();
                    for (Question question : section.getQuestionArray()) {
                        String tmpId = pageId + "_" + sectionId + "_" + question.getId();
                        if (id.equals(tmpId)) {
                            return createItem(question, id);
                        }
                    }
                }
            }
        }
        return null;
    }

    private Item createItem(Question question, String id) {
        Item item = new Item();
        item.setId(id);
        item.setName(question.getDescription());

        return item;
    }
}
