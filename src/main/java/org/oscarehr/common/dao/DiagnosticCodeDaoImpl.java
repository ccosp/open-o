package org.oscarehr.common.dao;

import java.util.Collections;
import java.util.List;
import javax.persistence.Query;
import org.oscarehr.common.model.AbstractCodeSystemModel;
import org.oscarehr.common.model.DiagnosticCode;
import org.springframework.stereotype.Repository;

@Repository
@SuppressWarnings("unchecked")
public class DiagnosticCodeDaoImpl extends AbstractDaoImpl<DiagnosticCode> implements DiagnosticCodeDao {

    public DiagnosticCodeDaoImpl() {
        super(DiagnosticCode.class);
    }

    // ... rest of the methods implementation from the original DiagnosticCodeDao class ...
}
