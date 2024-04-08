package org.oscarehr.common.dao;

import java.util.List;
import org.oscarehr.common.model.ConsultDocs;

public interface ConsultDocsDao {
    List<ConsultDocs> findByRequestIdDocNoDocType(Integer requestId, Integer documentNo, String docType);
    List<ConsultDocs> findByRequestIdDocType(Integer requestId, String docType);
    List<ConsultDocs> findByRequestId(Integer requestId);
    List<Object[]> findLabs(Integer consultationId);
}
