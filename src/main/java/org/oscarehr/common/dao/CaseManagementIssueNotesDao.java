package org.oscarehr.common.dao;

import java.util.List;

public interface CaseManagementIssueNotesDao {
    List<CaseManagementIssue> getNoteIssues(Integer noteId);
    List<Integer> getNoteIdsWhichHaveIssues(String[] issueId);
}
