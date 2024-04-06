package org.oscarehr.common.dao;

import java.util.Date;
import java.util.List;
import org.oscarehr.common.model.GroupNoteLink;

public interface GroupNoteDao extends AbstractDao<GroupNoteLink> {
    List<GroupNoteLink> findLinksByDemographic(Integer demographicNo);
    List<GroupNoteLink> findLinksByDemographicSince(Integer demographicNo, Date lastDateUpdated);
    List<GroupNoteLink> findLinksByNoteId(Integer noteId);
    int getNumberOfLinksByNoteId(Integer noteId);
}
