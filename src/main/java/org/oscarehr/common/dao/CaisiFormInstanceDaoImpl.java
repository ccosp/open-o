package org.oscarehr.common.dao;

import java.util.Date;
import java.util.List;
import org.oscarehr.common.model.CaisiFormData;
import org.oscarehr.common.model.CaisiFormInstance;
import org.oscarehr.common.model.Demographic;
import org.springframework.stereotype.Repository;

@Repository
public class CaisiFormInstanceDaoImpl extends AbstractDaoImpl<CaisiFormInstance> implements CaisiFormInstanceDao {
    public CaisiFormInstanceDaoImpl() {
        super(CaisiFormInstance.class);
    }
    
    // Implement all methods from the interface here
    // ...
}
