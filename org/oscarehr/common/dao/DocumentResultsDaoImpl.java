package org.oscarehr.common.dao;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Query;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Logger;
import org.oscarehr.common.model.Demographic;
import org.oscarehr.common.model.Document;
import org.oscarehr.common.model.ProviderInboxItem;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;
import oscar.oscarLab.ca.on.CommonLabResultData;
import oscar.oscarLab.ca.on.LabResultData;

public class DocumentResultsDaoImpl extends AbstractDaoImpl<Document> implements DocumentResultsDao {

    Logger logger = org.oscarehr.util.MiscUtils.getLogger();

    public DocumentResultsDaoImpl() {
        super(Document.class);
    }

    // ... rest of the methods implementation ...
}
