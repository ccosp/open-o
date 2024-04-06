package org.oscarehr.common.dao;

import java.util.List;

import javax.persistence.Query;

import org.apache.logging.log4j.Logger;
import org.oscarehr.common.model.BedType;
import org.oscarehr.util.MiscUtils;
import org.springframework.stereotype.Repository;

@Repository
public class BedTypeDaoImpl extends AbstractDaoImpl<BedType> implements BedTypeDao {

    private Logger log = MiscUtils.getLogger();

    public BedTypeDaoImpl() {
        super(BedType.class);
    }

    public boolean bedTypeExists(Integer bedTypeId) {
        Query query = entityManager.createQuery("select count(*) from BedType b where b.id = ?");
        query.setParameter(1, bedTypeId);

        Long result = (Long)query.getSingleResult();

        return (result.intValue() == 1);
    }

    public BedType getBedType(Integer bedTypeId) {
        return find(bedTypeId);
    }

    public BedType[] getBedTypes() {
        Query query = entityManager.createQuery("select b from BedType b");

        List<BedType> bedTypes = query.getResultList();

        log.debug("getRooms: size: " + bedTypes.size());

        return bedTypes.toArray(new BedType[bedTypes.size()]);
    }
}
