package org.oscarehr.common.dao;

import java.util.List;

import org.oscarehr.common.NativeSql;
import org.oscarehr.common.model.DxAssociation;

public interface DxDao extends AbstractDao<DxAssociation> {

	List<DxAssociation> findAllAssociations();

	int removeAssociations();

	DxAssociation findAssociation(String codeType, String code);

	List<Object[]> findCodingSystemDescription(String codingSystem, String code);

	List<Object[]> findCodingSystemDescription(String codingSystem, String[] keywords);

	String getCodeDescription(String codingSystem, String code);
}
