package org.oscarehr.common.dao;

import java.util.List;
import org.oscarehr.common.model.PreventionsLotNrs;

public interface PreventionsLotNrsDao {
    List<PreventionsLotNrs> findLotNrData(Boolean bDeleted);
    PreventionsLotNrs findByName(String prevention, String lotNr, Boolean bDeleted);
    List<String> findLotNrs(String prevention, Boolean bDeleted);
    List<PreventionsLotNrs> findPagedData(String prevention, Boolean bDeleted, Integer offset, Integer limit);
}
