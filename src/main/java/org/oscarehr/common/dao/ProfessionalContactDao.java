package org.oscarehr.common.dao;

import java.util.List;
import org.oscarehr.common.model.ProfessionalContact;

public interface ProfessionalContactDao extends AbstractDao<ProfessionalContact>{
    List<ProfessionalContact> findAll();
    List<ProfessionalContact> search(String searchMode, String orderBy, String keyword);
}
