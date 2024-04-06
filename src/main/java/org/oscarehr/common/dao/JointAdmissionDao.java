package org.oscarehr.common.dao;

import java.util.List;
import org.oscarehr.common.model.JointAdmission;

public interface JointAdmissionDao {
    List<JointAdmission> getSpouseAndDependents(Integer clientId);
    JointAdmission getJointAdmission(Integer clientId);
    void removeJointAdmission(Integer clientId,String providerNo);
    void removeJointAdmission(JointAdmission admission);
}
