package org.oscarehr.common.dao;

import java.util.List;
import javax.persistence.Query;
import org.oscarehr.common.model.PreventionsLotNrs;
import org.springframework.stereotype.Repository;

@Repository
public class PreventionsLotNrsDaoImpl extends AbstractDaoImpl<PreventionsLotNrs> implements PreventionsLotNrsDao {

    public PreventionsLotNrsDaoImpl() {
        super(PreventionsLotNrs.class);
    }
    
    @Override
    public List<PreventionsLotNrs> findLotNrData(Boolean bDeleted) {
        // ... existing implementation ...
    }

    @Override
    public PreventionsLotNrs findByName(String prevention, String lotNr, Boolean bDeleted) {
        // ... existing implementation ...
    }
    
    @Override
    public List<String> findLotNrs(String prevention, Boolean bDeleted) {
        // ... existing implementation ...
    }
    
    @Override
    public List<PreventionsLotNrs> findPagedData(String prevention, Boolean bDeleted, Integer offset, Integer limit) {
        // ... existing implementation ...
    }
}
