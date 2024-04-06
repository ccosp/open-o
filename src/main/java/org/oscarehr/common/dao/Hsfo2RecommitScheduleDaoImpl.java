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
    
    // ... rest of the methods implementation ...
}
