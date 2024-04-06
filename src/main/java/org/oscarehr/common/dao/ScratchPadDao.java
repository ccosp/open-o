package org.oscarehr.common.dao;

import java.util.List;
import org.oscarehr.common.model.ScratchPad;

public interface ScratchPadDao extends AbstractDao<ScratchPad> {
    boolean isScratchFilled(String providerNo);
    ScratchPad findByProviderNo(String providerNo);
    List<Object[]> findAllDatesByProviderNo(String providerNo);
}
