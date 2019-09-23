package org.oscarehr.ws.rest.conversion;

import org.apache.commons.lang3.StringEscapeUtils;
import org.oscarehr.common.model.Document;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.ws.rest.to.model.OtnEconsult;
import org.springframework.stereotype.Component;

@Component
public class OtnEconsultConverter extends AbstractConverter<Document, OtnEconsult> {

	@Override
	public Document getAsDomainObject(LoggedInInfo loggedInInfo, OtnEconsult t) throws ConversionException {
		Document document = new Document();
		
		String description = StringEscapeUtils.escapeHtml4(t.getDocDescription());
		document.setDocdesc(description);		
		document.setContentdatetime(t.getImportDate());
		document.setContenttype(t.getContentType());
		document.setDoccreator(loggedInInfo.getLoggedInProviderNo());
		document.setDocfilename(t.getFileName());
		document.setDoctype(OtnEconsult.getDoctype().getName());
		document.setStatus('A');
		document.setUpdatedatetime(t.getImportDate());
		document.setBase64Binary(t.getContents());
		document.setResponsible(loggedInInfo.getLoggedInProviderNo());
		document.setPublic1(0);
		
		return document;
	}

	@Override
	public OtnEconsult getAsTransferObject(LoggedInInfo loggedInInfo, Document d) throws ConversionException {
		// Endpoint is not 2-way. Import only.
		return null;
	}

}
