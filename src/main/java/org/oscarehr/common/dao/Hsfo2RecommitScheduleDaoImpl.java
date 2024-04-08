package org.oscarehr.common.dao;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.persistence.Query;

import org.oscarehr.common.model.Demographic;
import org.oscarehr.common.model.Hsfo2Patient;
import org.oscarehr.common.model.Hsfo2RecommitSchedule;
import org.oscarehr.util.LoggedInInfo;
import org.springframework.stereotype.Repository;

import oscar.form.study.hsfo2.HSFODAO;
import oscar.oscarDemographic.data.DemographicData;

@Repository
public class Hsfo2RecommitScheduleDaoImpl extends AbstractDaoImpl<Hsfo2RecommitSchedule> implements Hsfo2RecommitScheduleDao {
    public Hsfo2RecommitScheduleDaoImpl() {
        super(Hsfo2RecommitSchedule.class);
    }
    public Hsfo2RecommitSchedule getLastSchedule(boolean statusFlag) {
        String sqlCommand = "select * from hsfo_recommit_schedule  ";		 
        if (statusFlag) sqlCommand += "where status='D' ";
        sqlCommand += "order by id desc";
        
        Query query = entityManager.createNativeQuery(sqlCommand, modelClass);
        return getSingleResultOrNull(query);
   }
        
   public void updateLastSchedule(Hsfo2RecommitSchedule rd) {
       this.merge(rd);
   }

   public void insertchedule(Hsfo2RecommitSchedule rd) {
       this.persist(rd);
   }
   
   
   public boolean isLastActivExpire() {
        boolean exp=false;
        Hsfo2RecommitSchedule rd=getLastSchedule(false);
        if (rd!=null && !"D".equalsIgnoreCase(rd.getStatus())){
            if (rd.getSchedule_time().before(new Date())) exp=true;
        }
        return exp;
    }
    
    public void deActiveLast() {
        Hsfo2RecommitSchedule rd=getLastSchedule(false);
        if (rd!=null && !"D".equalsIgnoreCase(rd.getStatus())){
            rd.setStatus("D");
            updateLastSchedule(rd);
        }
    }
    
    public String SynchronizeDemoInfo(LoggedInInfo loggedInInfo) {
        HSFODAO hsfoDao=new HSFODAO();
        List idList=hsfoDao.getAllPatientId();
        Iterator itr=idList.iterator();
        DemographicData demoData = new DemographicData();
        while (itr.hasNext()){
            String pid=(String)itr.next();
            Hsfo2Patient pd=hsfoDao.retrievePatientRecord(pid);
            Demographic demo=demoData.getDemographic(loggedInInfo, pid);
            if(demo!=null) {
                String internalId=demo.getProviderNo();
                if(internalId==null || internalId.length()==0){
                    return demo.getLastName()+","+demo.getFirstName();
                }
                pd.setBirthDate(oscar.util.DateUtils.toDate(demo.getFormattedDob()));
                pd.setSex(demo.getSex().toLowerCase());
                if (demo.getLastName()!=null 
                        && demo.getLastName().trim().length()>0)
                    pd.setLName(demo.getLastName());
                if (demo.getFirstName()!=null 
                        && demo.getFirstName().trim().length()>0) 
                    pd.setFName(demo.getFirstName());
                
                String pcode=demo.getPostal().trim();
                if (pcode!=null && pcode.length()>=3)
                    pd.setPostalCode(pcode.substring(0, 3));
                hsfoDao.updatePatient(pd);
            }
        }
        return null;
    }
    
    public String checkProvider(LoggedInInfo loggedInInfo) {
        HSFODAO hsfoDao=new HSFODAO();
        List idList=hsfoDao.getAllPatientId();
        Iterator itr=idList.iterator();
        DemographicData demoData = new DemographicData();
        while (itr.hasNext()){
            String pid=(String)itr.next();
            Demographic demo=demoData.getDemographic(loggedInInfo, pid);
            String internalId=demo.getProviderNo();
            if(internalId==null || internalId.length()==0){
                return demo.getLastName()+","+demo.getFirstName();
            }
            
        }
        return null;
    }
}
