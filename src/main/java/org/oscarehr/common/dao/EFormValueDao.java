package org.oscarehr.common.dao;

import java.util.List;
import org.oscarehr.common.model.EFormValue;

public interface EFormValueDao extends AbstractDao<EFormValue> {
    List<EFormValue> findByDemographicId(Integer demographicId);
    List<EFormValue> findByApptNo(int apptNo);
    List<EFormValue> findByFormDataId(int fdid);
    EFormValue findByFormDataIdAndKey(int fdid,String varName);
    List<EFormValue> findByFormDataIdList(List<Integer> fdids);
    List<String> findAllVarNamesForEForm(Integer fid);
}
