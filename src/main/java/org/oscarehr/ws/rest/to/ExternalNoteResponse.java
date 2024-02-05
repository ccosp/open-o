package org.oscarehr.ws.rest.to;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ExternalNoteResponse extends GenericRESTResponse {
    private Long noteId;

    public ExternalNoteResponse(boolean success, String message, Long noteId) {
        setSuccess(success);
        setMessage(message);
        setNoteId(noteId);
    }

    public void setNoteId(Long noteId) {
        this.noteId = noteId;
    }

    public Long getNoteId() {
        return noteId;
    }
}
