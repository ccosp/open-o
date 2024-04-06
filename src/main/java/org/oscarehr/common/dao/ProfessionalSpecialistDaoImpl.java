// The content of this file will be the same as the original ProfessionalSpecialistDao.java file
// but with the class name changed to ProfessionalSpecialistDaoImpl and implementing the ProfessionalSpecialistDao interface.

package org.oscarehr.common.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;
import org.apache.commons.lang.StringUtils;
import org.oscarehr.common.model.ProfessionalSpecialist;
import org.springframework.stereotype.Repository;

@Repository
public class ProfessionalSpecialistDaoImpl extends AbstractDaoImpl<ProfessionalSpecialist> implements ProfessionalSpecialistDao {

    public ProfessionalSpecialistDaoImpl() {
        super(ProfessionalSpecialist.class);
    }

    // ... rest of the methods implementation
}
