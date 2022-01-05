package org.oscarehr.ws.rest.conversion;

import org.oscarehr.common.model.DocumentReview;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.ws.rest.to.model.DocumentReviewTo1;

public class DocumentReviewConverter  extends AbstractConverter<DocumentReview, DocumentReviewTo1>  {
    @Override
    public DocumentReview getAsDomainObject(LoggedInInfo loggedInInfo, DocumentReviewTo1 t) throws ConversionException {
        DocumentReview d = new DocumentReview();

        d.setId(t.getId());
        d.setDocumentNo(t.getDocumentNo());
        d.setProviderNo(t.getProviderNo());
        d.setDateTimeReviewed(t.getDateTimeReviewed());
        
        if(t.getReviewer() != null){
            d.setReviewer(new ProviderConverter().getAsDomainObject(loggedInInfo, t.getReviewer()));
        }
        
        return d;
    }

    @Override
    public DocumentReviewTo1 getAsTransferObject(LoggedInInfo loggedInInfo, DocumentReview d) throws ConversionException {
        DocumentReviewTo1 t = new DocumentReviewTo1();
        
        t.setId(d.getId());
        t.setDocumentNo(d.getDocumentNo());
        t.setProviderNo(d.getProviderNo());
        t.setDateTimeReviewed(d.getDateTimeReviewed());

        if(d.getReviewer() != null){
            t.setReviewer(new ProviderConverter().getAsTransferObject(loggedInInfo, d.getReviewer()));
        }

        return t;
    }
}
