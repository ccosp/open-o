package org.oscarehr.common.dao;

import java.util.Date;
import java.util.Map;
import java.util.SortedSet;

import org.oscarehr.common.model.Provider;
import org.oscarehr.util.EncounterUtil.EncounterType;

public interface PopulationReportDao {
    public static final int LOW = 0;
    public static final int MEDIUM = 1;
    public static final int HIGH = 2;

    int getCurrentPopulationSize();

    int getCurrentAndHistoricalPopulationSize(int numYears);

    int[] getUsages(int numYears);

    int getMortalities(int numYears);

    int getPrevalence(SortedSet<String> icd10Codes);

    int getIncidence(SortedSet<String> icd10Codes);

    Map<Integer, Integer> getCaseManagementNoteCountGroupedByIssueGroup(int programId, Integer roleId, EncounterType encounterType, Date startDate, Date endDate);

    Map<Integer, Integer> getCaseManagementNoteCountGroupedByIssueGroup(int programId, Provider provider, EncounterType encounterType, Date startDate, Date endDate);

    Integer getCaseManagementNoteTotalUniqueEncounterCountInIssueGroups(int programId, Integer roleId, EncounterType encounterType, Date startDate, Date endDate);

    Integer getCaseManagementNoteTotalUniqueEncounterCountInIssueGroups(int programId, Provider provider, EncounterType encounterType, Date startDate, Date endDate);

    Integer getCaseManagementNoteTotalUniqueClientCountInIssueGroups(int programId, Integer roleId, EncounterType encounterType, Date startDate, Date endDate);

    Integer getCaseManagementNoteTotalUniqueClientCountInIssueGroups(int programId, Provider provider, EncounterType encounterType, Date startDate, Date endDate);

    Integer getCaseManagementNoteCountByIssueGroup(int programId, Integer issueGroupId, Integer roleId, EncounterType encounterType, Date startDate, Date endDate);
}
