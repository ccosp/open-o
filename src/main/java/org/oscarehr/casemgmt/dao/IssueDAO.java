package org.oscarehr.casemgmt.dao;

import java.util.List;
import com.quatro.model.security.Secrole;
import org.oscarehr.casemgmt.model.Issue;

public interface IssueDAO {
    Issue getIssue(Long id);
    List<Issue> getIssues();
    List<Issue> findIssueByCode(String[] codes);
    Issue findIssueByCode(String code);
    Issue findIssueByTypeAndCode(String type, String code);
    void saveIssue(Issue issue);
    void delete(Long issueId);
    List<Issue> findIssueBySearch(String search);
    List<Long> getIssueCodeListByRoles(List<Secrole> roles);
    List<Issue> search(String search, List<Secrole> roles, int startIndex, int numToReturn);
    Integer searchCount(String search, List<Secrole> roles);
    List searchNoRolesConcerned(String search);
    List<String> getLocalCodesByCommunityType(String type);
}