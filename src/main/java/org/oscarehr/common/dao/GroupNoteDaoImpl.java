package org.oscarehr.common.dao;

import java.util.Date;
import java.util.List;
import javax.persistence.Query;
import org.oscarehr.common.model.GroupNoteLink;
import org.springframework.stereotype.Repository;

@Repository
public class GroupNoteDaoImpl extends AbstractDaoImpl<GroupNoteLink> implements GroupNoteDao {

    public GroupNoteDaoImpl() {
        super(GroupNoteLink.class);
    }

    public List<GroupNoteLink> findLinksByDemographic(Integer demographicNo) {
        String sqlCommand = "select * from GroupNoteLink where demographicNo=?1 and active=true";

        Query query = entityManager.createNativeQuery(sqlCommand, modelClass);
        query.setParameter(1, demographicNo);
        
        @SuppressWarnings("unchecked")
        List<GroupNoteLink> results=query.getResultList();
    
        return (results);
    }
    
    public List<GroupNoteLink> findLinksByDemographicSince(Integer demographicNo, Date lastDateUpdated) {
        String sqlCommand = "select * from GroupNoteLink where demographicNo=?1 and active=true and created > ?2";

        Query query = entityManager.createNativeQuery(sqlCommand, modelClass);
        query.setParameter(1, demographicNo);
        query.setParameter(2,lastDateUpdated);
        
        @SuppressWarnings("unchecked")
        List<GroupNoteLink> results=query.getResultList();
    
        return (results);
    }
    
    public List<GroupNoteLink> findLinksByNoteId(Integer noteId) {

        String sqlCommand = "select * from GroupNoteLink where noteId=?1 and active=true";

        Query query = entityManager.createNativeQuery(sqlCommand, modelClass);
        query.setParameter(1, noteId);
        
        @SuppressWarnings("unchecked")
        List<GroupNoteLink> results=query.getResultList();
    
        return (results);
    }
    
    public int getNumberOfLinksByNoteId(Integer noteId) {
        return this.findLinksByNoteId(noteId).size();
    }
}
