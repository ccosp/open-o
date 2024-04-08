package org.oscarehr.common.dao;

import java.util.List;
import org.oscarehr.common.model.AbstractCodeSystemModel;

public interface AbstractCodeSystemDao<T extends AbstractCodeSystemModel<?>> extends AbstractDao<T> {

    //public static enum codingSystem {icd9,icd10,ichppccode,msp,SnomedCore}

   // public static String getDaoName(codingSystem codeSystem);

    public List<T> searchCode(String term);

    public T findByCode(String code);

    public AbstractCodeSystemModel<?> findByCodingSystem(String codingSystem);

}
