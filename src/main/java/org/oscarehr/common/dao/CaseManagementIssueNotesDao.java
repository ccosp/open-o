package org.oscarehr.common.dao;

import java.util.List;
import org.oscarehr.casemgmt.model.CaseManagementIssue;

public interface CaseManagementIssueNotesDao {
    List<CaseManagementIssue> getNoteIssues(Integer noteId);
    List<Integer> getNoteIdsWhichHaveIssues(String[] issueId);
}
