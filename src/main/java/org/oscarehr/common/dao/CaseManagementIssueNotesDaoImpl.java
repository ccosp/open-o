package org.oscarehr.common.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.oscarehr.casemgmt.model.CaseManagementIssue;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class CaseManagementIssueNotesDaoImpl implements CaseManagementIssueNotesDao {
    
    @PersistenceContext
    protected EntityManager entityManager = null;
    
    public List<CaseManagementIssue> getNoteIssues(Integer noteId)
    {
    	Query query=entityManager.createNativeQuery("select casemgmt_issue.* from casemgmt_issue_notes, casemgmt_issue where note_id=?1 and casemgmt_issue_notes.id=casemgmt_issue.id", CaseManagementIssue.class);
    	query.setParameter(1, noteId);
    	
        @SuppressWarnings("unchecked")
    	List<CaseManagementIssue> results=query.getResultList();
        return(results);
    }
    
    public List<Integer> getNoteIdsWhichHaveIssues(String[] issueId)
    {
    	if(issueId == null || issueId.length==0)
    		return null;
    	if(issueId.length==1 && issueId[0].equals(""))
    		return null;
    	
    	StringBuilder issueIdList = new StringBuilder();
    	for(String i:issueId) {
    		if(issueIdList.length()>0)
    			issueIdList.append(",");
    		issueIdList.append(i);
    	}
    	String sql = "select note_id  from casemgmt_issue_notes where id in ("+issueIdList.toString() + ")";
    	
    	Query query=entityManager.createNativeQuery(sql);
    	
        @SuppressWarnings("unchecked")
    	List<Integer> results=query.getResultList();
        return(results);
    }
    
}
