package org.oscarehr.PMmodule.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.oscarehr.PMmodule.dao.ProgramClientRestrictionDAO;
import org.oscarehr.PMmodule.exception.ClientAlreadyRestrictedException;
import org.oscarehr.PMmodule.model.ProgramClientRestriction;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class ClientRestrictionManagerImpl implements ClientRestrictionManager {

    private ProgramClientRestrictionDAO programClientRestrictionDAO;

    @Override
    public List<ProgramClientRestriction> getActiveRestrictionsForProgram(int programId, Date asOfDate) {
        // check dao for restriction
        Collection<ProgramClientRestriction> pcrs = programClientRestrictionDAO.findForProgram(programId);
        List<ProgramClientRestriction> returnPcrs = new ArrayList<ProgramClientRestriction>();
        if (pcrs != null && !pcrs.isEmpty()) {
            for (ProgramClientRestriction pcr : pcrs) {
                if (pcr.getStartDate().getTime() <= asOfDate.getTime() && pcr.getEndDate().getTime() <= pcr.getEndDate().getTime()) returnPcrs.add(pcr);
            }
        }
        return returnPcrs;
    }

    // ... rest of the methods implemented similarly ...

    @Required
    public void setProgramClientRestrictionDAO(ProgramClientRestrictionDAO programClientRestrictionDAO) {
        this.programClientRestrictionDAO = programClientRestrictionDAO;
    }
}
