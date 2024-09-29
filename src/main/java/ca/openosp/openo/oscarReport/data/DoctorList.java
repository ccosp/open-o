//CHECKSTYLE:OFF
/*
 * DoctorList.java
 *
 * Created on August 27, 2007, 4:23 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package ca.openosp.openo.oscarReport.data;

import java.util.ArrayList;
import java.util.List;

import ca.openosp.openo.PMmodule.dao.ProviderDao;
import ca.openosp.openo.common.model.Provider;
import ca.openosp.openo.ehrutil.SpringUtils;

import ca.openosp.openo.oscarProvider.bean.ProviderNameBean;

public class DoctorList {

    public ArrayList<ProviderNameBean> getDoctorNameList() {

        ArrayList<ProviderNameBean> dnl = new ArrayList<ProviderNameBean>();

        ProviderDao dao = SpringUtils.getBean(ProviderDao.class);
        List<Provider> docs = dao.getProvidersByType("doctor");

        for (Provider doc : docs) {
            ProviderNameBean pb = new ProviderNameBean();
            pb.setProviderID(doc.getProviderNo());
            pb.setProviderName(doc.getFullName());
            dnl.add(pb);
        }
        return dnl;
    }
}
