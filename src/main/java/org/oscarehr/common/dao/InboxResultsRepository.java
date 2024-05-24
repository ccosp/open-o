package org.oscarehr.common.dao;

import org.apache.logging.log4j.Logger;
import org.oscarehr.common.model.Provider;
import org.oscarehr.common.model.inbox.InboxItem;
import org.oscarehr.common.model.inbox.InboxItemDemographicCount;
import org.oscarehr.common.model.inbox.InboxQueryParameters;
import org.oscarehr.common.model.inbox.InboxResponse;
import org.oscarehr.util.MiscUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public interface InboxResultsRepository {
    //String getLabsSql(String loggedInProviderNo, String providerNumber, String firstName, String lastName, String hin, String startDate, String endDate, String status, String abnormalStatus, InboxQueryParameters.MatchedStatus matchedStatus, Boolean getCounts, Boolean getDemographicCounts, Map<String, String> whereValues);
    //String getLabsWhereSql(String loggedInProviderNo, String providerNumber, String firstName, String lastName, String hin, String startDate, String endDate, String status, String abnormalStatus, InboxQueryParameters.MatchedStatus matchedStatus, Map<String, String> whereValues);
    //String getLabsGroupedWhereSql(String providerNumber, String firstName, String lastName, String hin, String status, InboxQueryParameters.MatchedStatus matchedStatus, Map<String, String> whereValues);
    //String getHRMReportsSql(String loggedInProviderNo, String providerNumber, String firstName, String lastName, String hin, String startDate, String endDate, String status, InboxQueryParameters.MatchedStatus matchedStatus, Boolean getCounts, Boolean getDemographicCounts, Map<String, String> whereValues);
    //String getHRMReportsWhereSql(String loggedInProviderNo, String providerNumber, String firstName, String lastName, String hin, String startDate, String endDate, String status, InboxQueryParameters.MatchedStatus matchedStatus, Map<String, String> whereValues);
}
