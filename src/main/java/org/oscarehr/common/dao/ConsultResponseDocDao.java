package org.oscarehr.common.dao;

import java.util.List;
import org.oscarehr.common.model.ConsultResponseDoc;

public interface ConsultResponseDocDao {
    ConsultResponseDoc findByResponseIdDocNoDocType(Integer responseId, Integer documentNo, String docType);
    List<ConsultResponseDoc> findByResponseId(Integer responseId);
    List<Object[]> findLabs(Integer consultResponseId);
}
