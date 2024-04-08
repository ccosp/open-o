package org.oscarehr.common.dao;

import java.util.List;
import org.oscarehr.common.model.AbstractCodeSystemModel;

public abstract class AbstractCodeSystemDaoImpl<T extends AbstractCodeSystemModel<?>> extends AbstractDaoImpl<T> implements AbstractCodeSystemDao<T> {

    public AbstractCodeSystemDaoImpl(Class<T> modelClass) {
        super(modelClass);
    }

    public static enum codingSystem {icd9,icd10,ichppccode,msp,SnomedCore}

    public static String getDaoName(codingSystem codeSystem) {
        String object;
        switch(codeSystem) {
        case SnomedCore: object = "snomedCoreDao";
            break;
        case icd10: object = "icd10Dao";
            break;
        case icd9: object = "icd9Dao";
            break;
        case ichppccode: object = "ichppccodeDao";
            break;
        case msp: object = "diagnosticCodeDao";
            break;
        default: throw new IllegalArgumentException("Unsupported code system: " + codeSystem + ". Please use one of icd9, ichppccode, snomedcore");                
        }       
        return object;
    }

    public abstract List<T> searchCode(String term);

    public abstract T findByCode(String code);

    public abstract AbstractCodeSystemModel<?> findByCodingSystem(String codingSystem);

}
