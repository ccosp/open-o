package org.oscarehr.common.dao;

import java.util.List;
import javax.persistence.Query;
import org.oscarehr.common.model.Favorite;
import org.springframework.stereotype.Repository;

@Repository
public class FavoriteDaoImpl extends AbstractDaoImpl<Favorite> implements FavoriteDao {

	public FavoriteDaoImpl() {
		super(Favorite.class);
	}

	@SuppressWarnings("unchecked")
	public List<Favorite> findByProviderNo(String providerNo) {
		Query query = entityManager.createQuery("FROM " + modelClass.getSimpleName() + " f WHERE f.providerNo = :providerNo ORDER BY f.name");
		query.setParameter("providerNo", providerNo);
		return query.getResultList();
	}

	public Favorite findByEverything(String providerNo, String favoriteName, String bn, int gcn_SEQNO, String customName, float takeMin, float takeMax, String frequencyCode, String duration, String durationUnit, String quantity, int repeat, boolean nosubsInt, boolean prnInt, String parsedSpecial, String gn, String unitName, boolean customInstr) {
		Query query = entityManager.createQuery("FROM " + modelClass.getSimpleName() + " f WHERE f.providerNo = :providerNo AND f.name = :favoritename AND f.bn = :brandName AND f.gcnSeqno = :gcnSeqNo AND f.customName = :customName AND f.takeMin = :takemin AND f.takeMax = :takemax AND f.frequencyCode = :freqcode AND f.duration = :duration "
		        + "AND f.durationUnit = :durunit AND f.quantity = :quantity AND f.repeat = :repeat AND f.nosubs = :nosubs AND f.prn = :prn AND f.special = :special AND f.gn = :gn AND f.unitName = :unitName AND " + "f.customInstructions = :customInstructions");

		query.setParameter("providerNo", providerNo);
		query.setParameter("favoritename", favoriteName);
		query.setParameter("brandName", bn);
		query.setParameter("gcnSeqNo", (double) gcn_SEQNO);
		query.setParameter("customName", customName);
		query.setParameter("takemin", takeMin);
		query.setParameter("takemax", takeMax);
		query.setParameter("freqcode", frequencyCode);
		query.setParameter("duration", duration);
		query.setParameter("durunit", durationUnit);
		query.setParameter("quantity", quantity);
		query.setParameter("repeat", repeat);
		query.setParameter("nosubs", nosubsInt);
		query.setParameter("prn", prnInt);
		query.setParameter("special", parsedSpecial);
		query.setParameter("gn", gn);
		query.setParameter("unitName", unitName);
		query.setParameter("customInstructions", customInstr);

		return getSingleResultOrNull(query);
	}
}
