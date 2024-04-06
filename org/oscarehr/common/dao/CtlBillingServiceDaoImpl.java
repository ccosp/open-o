package org.oscarehr.common.dao;

import java.util.List;
import javax.persistence.Query;
import org.oscarehr.common.model.CtlBillingService;
import org.springframework.stereotype.Repository;

@Repository
@SuppressWarnings("unchecked")
public class CtlBillingServiceDaoImpl extends AbstractDaoImpl<CtlBillingService> implements CtlBillingServiceDao {

    public static final String DEFAULT_STATUS = "A";

    public CtlBillingServiceDaoImpl() {
        super(CtlBillingService.class);
    }

    // ... rest of the methods implementation goes here
}
