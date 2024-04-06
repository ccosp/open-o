package org.oscarehr.common.dao;

import org.oscarehr.billing.CA.ON.model.Billing3rdPartyAddress;
import org.springframework.stereotype.Repository;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

@Repository
@SuppressWarnings("unchecked")
public class Billing3rdPartyAddressDaoImpl extends AbstractDaoImpl<Billing3rdPartyAddress> implements Billing3rdPartyAddressDao {

    public Billing3rdPartyAddressDaoImpl() {
        super(Billing3rdPartyAddress.class);
    }

    public List<Billing3rdPartyAddress> findAll() {
        Query q = entityManager.createQuery("select b from Billing3rdPartyAddress b");
        List<Billing3rdPartyAddress> results = q.getResultList();
        return results;
    }

    public List<Billing3rdPartyAddress> findByCompanyName(String companyName) {
        Query q = entityManager.createQuery("select b from Billing3rdPartyAddress b where b.companyName = ?");
        q.setParameter(1, companyName);
        List<Billing3rdPartyAddress> results = q.getResultList();
        return results;
    }

    @NativeSql("billing_on_3rdPartyAddress")
    public List<Billing3rdPartyAddress> findAddresses(String searchModeParam, String orderByParam, String keyword, String limit1, String limit2) {
        // ... rest of the method implementation
    }
}
