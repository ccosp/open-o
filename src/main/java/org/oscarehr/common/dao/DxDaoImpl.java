package org.oscarehr.common.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import org.oscarehr.common.NativeSql;
import org.oscarehr.common.model.DxAssociation;
import org.oscarehr.util.MiscUtils;
import org.springframework.stereotype.Repository;

@Repository
public class DxDaoImpl extends AbstractDaoImpl<DxAssociation> implements DxDao {

	public DxDaoImpl() {
		super(DxAssociation.class);
	}

	@Override
	public List<DxAssociation> findAllAssociations() {
		Query query = entityManager.createQuery("select x from DxAssociation x order by x.dxCodeType,x.dxCode");

		@SuppressWarnings("unchecked")
		List<DxAssociation> results = query.getResultList();

		return results;
	}

	@Override
	public int removeAssociations() {
		Query query = entityManager.createQuery("DELETE from DxAssociation");
		return query.executeUpdate();
	}

	@Override
	public DxAssociation findAssociation(String codeType, String code) {
		Query query = entityManager.createQuery("SELECT x from DxAssociation x where x.codeType = ?1 and x.code = ?2");
		query.setParameter(1, codeType);
		query.setParameter(2, code);

		@SuppressWarnings("unchecked")
		List<DxAssociation> results = query.getResultList();
		if (!results.isEmpty()) {
			return results.get(0);
		}
		return null;
	}

	@NativeSql
	@Override
	@SuppressWarnings("unchecked")
	public List<Object[]> findCodingSystemDescription(String codingSystem, String code) {
		try {
			String sql = "SELECT " + codingSystem + ", description FROM " + codingSystem + " WHERE " + codingSystem
					+ " = :code";
			Query query = entityManager.createNativeQuery(sql);
			query.setParameter("code", code);
			return query.getResultList();
		} catch (Exception e) {
			// TODO Add exclude to the test instead when it's merged
			return new ArrayList<Object[]>();
		}
	}

	@NativeSql
	@Override
	@SuppressWarnings("unchecked")
	public List<Object[]> findCodingSystemDescription(String codingSystem, String[] keywords) {
		try {
			boolean flag = false;
			StringBuilder buf = new StringBuilder("select " + codingSystem + ", description from " + codingSystem);

			for (String keyword : keywords) {
				if (keyword == null || keyword.trim().equals("")) {
					continue;
				}
				if (!flag) {
					buf.append(" where ");
				}
				if (flag) {
					buf.append(" or ");
				}
				buf.append(" " + codingSystem + " like '%" + keyword + "%' or description like '%" + keyword + "%' ");
				flag = true;
			}

			Query query = entityManager.createNativeQuery(buf.toString());
			return query.getResultList();
		} catch (Exception e) {
			MiscUtils.getLogger().error("error", e);
			return new ArrayList<Object[]>();
		}

	}

	@NativeSql
	@Override
	public String getCodeDescription(String codingSystem, String code) {
		String desc = "";
		StringBuilder buf = new StringBuilder("select description from " + codingSystem + " where " + codingSystem + "='"
				+ code + "'");
		try {
			Query query = entityManager.createNativeQuery(buf.toString());
			desc = (String) query.getSingleResult();
		} catch (Exception e) {
			MiscUtils.getLogger().error("error " + buf, e);
		}
		return desc;
	}

}