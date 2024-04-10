package com.quatro.service;

import java.sql.SQLException;
import java.util.List;
import com.quatro.model.LookupCodeValue;
import com.quatro.model.LookupTableDefValue;

public interface LookupManager {
    List LoadCodeList(String tableId, boolean activeOnly, String code, String codeDesc);
    List LoadCodeList(String tableId, boolean activeOnly, String parentCode, String code, String codeDesc);
    LookupTableDefValue GetLookupTableDef(String tableId);
    LookupCodeValue GetLookupCode(String tableId, String code);
    List LoadFieldDefList(String tableId);
    List GetCodeFieldValues(LookupTableDefValue tableDef, String code);
    List GetCodeFieldValues(LookupTableDefValue tableDef);
    String SaveCodeValue(boolean isNew, LookupTableDefValue tableDef, List fieldDefList) throws SQLException;
    int getCountOfActiveClient(String orgCd) throws SQLException;
}
