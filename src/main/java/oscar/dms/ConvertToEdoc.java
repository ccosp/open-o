/**
 * Copyright (c) 2015-2019. The Pharmacists Clinic, Faculty of Pharmaceutical Sciences, University of British Columbia. All Rights Reserved.
 * This software is published under the GPL GNU General Public License.
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 *
 * This software was written for the
 * The Pharmacists Clinic
 * Faculty of Pharmaceutical Sciences
 * University of British Columbia
 * Vancouver, British Columbia, Canada
 */

package oscar.dms;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.oscarehr.common.model.EFormData;
import org.oscarehr.managers.NioFileManager;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.util.SpringUtils;
//import org.w3c.dom.Document;
//import org.w3c.dom.Element;
//import org.w3c.dom.Node;
//import org.w3c.dom.NodeList;
import org.w3c.tidy.Tidy;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
//import org.w3c.tidy.Tidy;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.lowagie.text.DocumentException;

//import com.lowagie.text.DocumentException;
import oscar.OscarProperties;
import oscar.form.util.FormTransportContainer;

/**
 * A utility that converts HTML into a PDF and returns an Oscar eDoc object.
 * 
 * This is useful for converting Forms and eForms with well structured HTML into 
 * PDF documents that can be attached to Consultation Requests, Faxes or transferred 
 * to other file systems.
 * 
 * NOT ALL DOCUMENTS ARE CONVERTABLE. USE AT OWN RISK.
 */
public class ConvertToEdoc {
	
	private static final Logger logger = MiscUtils.getLogger();
	
	public static enum DocumentType { eForm, form }
	private static enum PathAttribute { src, href }
	private static enum FileType { pdf, css, jpeg, png, gif }

	public static final String CUSTOM_STYLESHEET_ID = "pdfMediaStylesheet";
	private static final String DEFAULT_IMAGE_DIRECTORY = String.format( "%1$s", OscarProperties.getInstance().getProperty( "eform_image" ) );
	private static final String DEFAULT_FILENAME = "temporaryPDF";
	public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
	private static final String DEFAULT_CONTENT_TYPE = "application/pdf";
	private static final String SYSTEM_ID = "-1";
	private static String contextPath;
	private static String realPath;
	
	private static NioFileManager nioFileManager = SpringUtils.getBean( NioFileManager.class );
	
	/**
	 * Convert EForm to EDoc
	 * 
	 * Returns an EDoc Object with a filename that is saved in this class' 
	 * temporary DEFAULT_FILE_PATH. This file can be persisted by moving from the 
	 * temporary path to a file storage path prior to persisting this Object to the 
	 * database. 
	 */
	public synchronized static final EDoc from( EFormData eform ) {
		
		String eformString = eform.getFormData();	
		String demographicNo = eform.getDemographicId() + "";
		String filename = buildFilename( eform.getFormName(), demographicNo );
		EDoc edoc = null;		
		Path path = execute( eformString, filename );
		
		if(Files.isReadable(path)) {
			edoc = buildEDoc( path.getFileName().toString(), 
					eform.getSubject(), 
					null, 
					eform.getProviderNo(), 
					demographicNo, 
					DocumentType.eForm,
					path.getParent().toString());
		} else {
			logger.error("Could not read temporary PDF file " + filename);
		}		
		
		return edoc;	
	}

	/**
	 * Convert Form to EDoc
	 * 
	 * Returns an EDoc Object with a filename that is saved in this class' 
	 * temporary DEFAULT_FILE_PATH. This file can be persisted by moving from the 
	 * temporary path to a file storage path prior to persisting this Object to the 
	 * database. 
	 * 
	 * Example use:
	 * 
	 * public ActionForward eDocument(ActionMapping mapping, ActionForm form, 
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
			FormBean bpmh = (FormBean) form;

			FormTransportContainer formTransportContainer = new FormTransportContainer( 
					response, request, mapping.findForward("success").getPath() );

			formTransportContainer.setDemographicNo( bpmh.getDemographicNo() );
			formTransportContainer.setProviderNo( bpmh.getProvider().getProviderNo() );
			formTransportContainer.setSubject( "BPMH Form ID " + bpmh.getFormId() );
			formTransportContainer.setFormName( "bpmh" );
			formTransportContainer.setRealPath( getServlet().getServletContext().getRealPath( File.separator ) );
			
			FormsManager#saveFormDataAsEDoc( LoggedInInfo.getLoggedInInfoFromSession(request), formTransportContainer );
			
			return ActionForward;
		}
	 * 
	 */
	public synchronized static final EDoc from( FormTransportContainer formTransportContainer ) {
	
		String htmlString = formTransportContainer.getHTML();
		String demographicNo = formTransportContainer.getDemographicNo();
		String filename = buildFilename( formTransportContainer.getFormName(), demographicNo );
		String subject = formTransportContainer.getSubject();
		String providerNo = formTransportContainer.getProviderNo();
		if( providerNo == null ) {
			providerNo = formTransportContainer.getLoggedInInfo().getLoggedInProviderNo();
		}
		// this should be the same for every thread.
		ConvertToEdoc.contextPath = formTransportContainer.getContextPath();
		ConvertToEdoc.realPath = formTransportContainer.getRealPath();
		
		EDoc edoc = null;		
		Path path = execute( htmlString, filename );
		
		if(Files.isReadable(path)) {
			edoc = buildEDoc( filename, 
					subject, 
					null, 
					providerNo, 
					demographicNo, 
					formTransportContainer.getDocumentType(), 
					path.toString());			
		} else {
			logger.error("Could not read temporary PDF file " + filename);
		}
		
		return edoc;
	}
	
	/**
	 * 
	 * Save the document as a temporary PDF. Does not save or return an eDoc entity. 
	 * This is a temporary file location. Ensure that the file is deleted after it's used. 
	 * 
	 * @return temporary path to the produced PDF.. 
	 */
	public synchronized static final Path saveAsTempPDF( EFormData eform ) {
		String eformString = eform.getFormData();
		String filename = buildFilename( eform.getFormName(), eform.getDemographicId()+"" );
		Path filePath = execute( eformString, filename );

		return filePath;
	}
	
	/**
	 * 
	 * Save the document as a temporary PDF. Does not save or return an eDoc entity.
	 * This is a temporary file location. Ensure that the file is deleted after it's used.  
	 * 
	 * @return temporary path to the produced PDF. 
	 */
	public synchronized static final Path saveAsTempPDF(FormTransportContainer formTransportContainer) {
		String htmlString = formTransportContainer.getHTML();
		String filename = buildFilename( formTransportContainer.getFormName(), formTransportContainer.getDemographicNo() );
		Path filePath = execute( htmlString, filename );

		return filePath;
	}
	
	/**
	 * Execute building and saving PDF to temp directory.
	 */
	private static Path execute( final String eformString, final String filename ) {

		Document document = buildDocument( eformString );
		Path path = null;

		logger.debug("Final HTML " + document.outerHtml());
		
		try(ByteArrayOutputStream os = new ByteArrayOutputStream()) {
			renderPDF( document, os );
			path = nioFileManager.saveTempFile( filename, os );
		} catch (DocumentException e1) {
			logger.error( "Exception parsing file to PDF. File not saved. ", e1 );
		} catch (IOException e) {
			logger.error("Problem while writing PDF file to filesystem. " + filename, e);
		} 
		
		return path;
	}

	/**
	 * creates a filename
	 */
	private static final String buildFilename( String filename, String demographicNo ) {
		
		if( filename == null || filename.isEmpty() ) {
			filename = DEFAULT_FILENAME;
		}
		
		filename = filename.trim();
		filename = filename.replaceAll(" ", "_");
		filename = String.format("%1$s_%2$s", filename, demographicNo );
		filename = String.format("%1$s_%2$s", filename, new Date().getTime() );

		return filename;
	}
	
	/**
	 * Builds an EDoc instance.
	 */
	public static EDoc buildEDoc( final String filename, final String subject, final String sourceHtml, 
			final String providerNo, final String demographicNo, final DocumentType documentType, final String filePath) {

		final SimpleDateFormat simpleDateFormat = new SimpleDateFormat( DEFAULT_DATE_FORMAT ); 		
		final String todayDate = simpleDateFormat.format( new Date() );
		
		EDoc eDoc = new EDoc(
				( subject == null ) ? "" : subject,
				documentType.name(), 
				filename, 
				sourceHtml, 
				SYSTEM_ID, 
				providerNo, 
				"",
				EDocFactory.Status.ACTIVE.getStatusCharacter(), 
				todayDate,
				"", 
				new Date().toString(),
				EDocFactory.Module.demographic.name(), 
				demographicNo,
				Boolean.FALSE );
		
		eDoc.setContentType( DEFAULT_CONTENT_TYPE );
		eDoc.setContentDateTime( new Date() );
		eDoc.setNumberOfPages(0);
		eDoc.setFilePath(filePath);

		return eDoc;
	}

	/**
	 * Use the Flying Saucer tools to render a PDF from a 
	 * well formed w3c XHTML document
	 * @throws DocumentException 
	 */
	private static final void renderPDF( final Document document, ByteArrayOutputStream os ) throws DocumentException {		
		ITextRenderer renderer = new ITextRenderer();
		renderer.setDocumentFromString(document.outerHtml());
		renderer.layout();
		renderer.createPDF( os );
	}
	
	/**
	 * Build this HTML document. 
	 * - adds translated image paths
	 * - inserts custom stylesheets.
	 */
	private static final Document buildDocument( final String documentString ) {
		
		Document document = getDocument( documentString );

		if( document != null ) {
			translateResourcePaths( document );
			// setHeadElement( document );
			addCss( document );
		}

		return document;
	}
	
	/**
	 * Get a W3C XML document from well formed XHTML using JSoup
	 */
	private static Document getDocument( final String documentString ) {
		String tidyDocumentString = tidyDocument(documentString); 

	    Document document = Jsoup.parse(tidyDocumentString);
	    document.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
	    return document;
	}
	
	/**
	 * Adds custom CSS templates to the Document.
	 * 
	 * Normally the stylesheets should be included with the HTML being converted. This method may be
	 * required to alter the current style for better print to PDF. Or if the original stylesheet gets 
	 * stripped out of the HTML like with the Rich Text Letter Editor
	 * 
	 * A stylesheet reference can be set into the origin HTML document with a hidden input tag: 
	 * <input type="hidden" id="customStylesheet" name="customStylesheet" value="<stylesheet filename>" />
	 * This tag would be inserted between the section tag of a Rich Text Letter Template. 
	 * The custom stylesheet will be retrieved from Oscar's images directory. Only the filename needs to be 
	 * given by the input tag. This method will build the filepath.

	 * - Adds a head element to the document if one does not exist.
	 *   
	 */
	private static void addCss( Document document ) {

//		XPath xpath = XPathFactory.newInstance().newXPath();
		Elements styleSheetElement = document.getElementsByAttributeValue("id", CUSTOM_STYLESHEET_ID);

//		try {
//			styleSheetElement = (Element) xpath.evaluate("//*[@id='" + CUSTOM_STYLESHEET_ID + "']", document, XPathConstants.NODE);
//		} catch (XPathExpressionException e) {
//			logger.error("Error", e);
//		}

		if( styleSheetElement != null && styleSheetElement.size() > 0) {
			setParameterInjectedCss(document, styleSheetElement.get(0).val());
		}
	}
	
	/**
	 * It is critical that a head element is present for this given document. 
	 */
//	private static void setHeadElement( Document document ) {
//		
//		Node headNode = null;
//		Node htmlNode = document.getDocumentElement();
//		NodeList nodeList = htmlNode.getChildNodes();
//		
//		for( int i = 0; i < nodeList.getLength(); i++ ) {
//			Node node = nodeList.item(i);
//
//			if( "head".equals( node.getNodeName() )) {
//				headNode = node;
//			}
//		}
//		
//		if( headNode == null ) {              
//	      	htmlNode.appendChild( document.createElement("head") );
//		}
//	}
	
	/**
	 * Returns the head element directly from the given Document 
	 */
	private static Element getHeadElement( Document document ) {
		return document.getElementsByTag("head").get(0);
	}
	
	/**
	 * Create a Link element from the CSS filename that was inserted into the 
	 * origin HTML as a hidden input element. 
	 */
	private static void setParameterInjectedCss( Document document, String filename ) {
		filename = buildImageDirectoryPath( filename );
		filename = validateLink( filename );

		// add a stylesheet ie: link <link rel="stylesheet" href=".." />
		if( filename != null ) {
			Element linkElement = document.createElement("link");
			linkElement.attr("rel", "stylesheet");
			linkElement.attr("href", filename);
			getHeadElement( document ).appendChild( linkElement );
		}		
	}

	/**
	 * HTTP request paths routed through Struts need to be 
	 * translated into a relative path to the global images directory.
	 */
	private static void translateResourcePaths( Document document ) {
		translateLinkPaths( document );
		translateImagePaths( document );
		translateJavascriptPaths(document);
	}
	
	/**
	 * This method handles paths set into a link element
	 */
	private static void translateLinkPaths( Document document ) {
		Elements linkNodeList = document.select("link");	
		if( linkNodeList != null ) {
			translatePaths( linkNodeList, PathAttribute.href );
		}
	}
	
	/**
	 * This method handles paths set into an image element.
	 */
	private static void translateImagePaths( Document document ) {		
		Elements imageNodeList = document.select("img");		
		if( imageNodeList != null ) {
			translatePaths( imageNodeList, PathAttribute.src );
		}
	}
	
	/**
	 * This method handles paths to Javascript resources. 
	 * Usually in the images directory
	 */
	private static void translateJavascriptPaths( Document document ) {
		Elements linkNodeList = document.select("script");	
		if( linkNodeList != null ) {
			linkNodeList.remove();
			// translatePaths( linkNodeList, PathAttribute.src );
		}
	}
	
	/**
	 * Translate any given Link or Image element resource path from 
	 * a Struts HTTP request parameter or HTTP relative context path.
	 * 
	 * All resource links in the document must be absolute for the PDF 
	 * creator to work.
	 */
	private static void translatePaths( Elements nodeList, PathAttribute pathAttribute ) {

		for( int i = 0; i < nodeList.size(); i++ ) {
			
			Element element = nodeList.get(i);			
			String path = element.attr( pathAttribute.name() );
			String validLink = null;
			String parameters = null;
			String[] parameterList = null;		
			List<String> potentialFilePaths = new ArrayList<String>();

			if( path.contains("?") ) {
				// image or link paths with parameters
				parameters = path.split("\\?")[1];
			} else if(! path.isEmpty()) {
				// these are most likely relative context paths
				path = getRealPath( path );
				potentialFilePaths.add( path );
			}
			
			// parse the parameters and test if any are links to the eForm 
			// images library. Otherwise these resources are no good.
			if( parameters != null && parameters.contains("&") ) {
				parameterList = parameters.split("&");
			}
			
			if( parameterList != null ) {
				for( String parameter : parameterList ) {
					if( parameter.contains("=") ) {
						// these are file names that need a path.
						path = buildImageDirectoryPath( parameter.split("=")[1] );
						potentialFilePaths.add( path );
					}	
				}
			} else if( parameters != null && parameters.contains("=") ) {
				path = buildImageDirectoryPath( parameters.split("=")[1] );
				potentialFilePaths.add( path );
			} 
			
			
			// there really should be only one valid path.
			// Only use the one that validates
			validLink = validateLink( potentialFilePaths );
			
			// change the element resource link to something absolute  
			// that can be used by the PDF creator. 
			if( validLink != null ) {
				element.attr(pathAttribute.name(), validLink );
			} 

		}
	}
	
	/**
	 * Feed this method a filename and it will return a full path to the Oscar images directory. 
	 */
	private static String buildImageDirectoryPath( String filename ) {
		Path path = Paths.get(getImageDirectory(), filename);
		return path.toAbsolutePath().toString();
	}
	
	/**
	 * Convert a given context path into a file system absolute path.
	 * @return
	 */
	private static String getRealPath( String uri ) {
		String contextRealPath = "";
		
		logger.debug( "Context path set to " + contextPath );
		
		if( ConvertToEdoc.contextPath != null && ConvertToEdoc.realPath != null ) {
			
			logger.debug( "Relative file path " + uri );

			String filePath = uri.substring( contextPath.length(), uri.length() );
			filePath = filePath.replaceFirst( File.separator , "" );
			contextRealPath = ConvertToEdoc.realPath;
			
			if( ! contextRealPath.endsWith( File.separator )) {
				contextRealPath = contextRealPath + File.separator;
			}
			
			contextRealPath = String.format( "%1$s%2$s", contextRealPath, filePath );

			logger.debug( "Absolute file path " + contextRealPath );
	
		}
		
		/*
		 * catch external links to resources. 
		 */
		else if(uri.toLowerCase().startsWith("http")) {
			contextRealPath = uri;
		}
		
		/*
		 * For some strange reason eForm developers will use just the 
		 * filename as the uri and then add javascript to alter it
		 * This catches these potential situations
		 */
		else if(! uri.contains(File.separator)) {
			 contextRealPath = buildImageDirectoryPath( uri );
		}
		
		return contextRealPath;
	}
	
	/**
	 * Returns a List of valid file links from a list of potential valid links.
	 */
	private static List<String> validateLinks( List<String> potentialLinks ) {

		List<String> finalLinks = null;
		String validLink = null;
		
		for( String potentialLink : potentialLinks ) {
			if( potentialLink.isEmpty() ) {
				continue;
			}
			
			validLink = validateLink( potentialLink );
			
			if( finalLinks == null && validLink != null ) {
				finalLinks = new ArrayList<String>();
			}

			if( validLink != null ) {
				finalLinks.add( validLink );
			}
		}
		
		return finalLinks;
	}
	
	/**
	 * Returns the first valid file link from a list of potential valid links.
	 */
	private static String validateLink( List<String> potentialLinks ) {
		
		logger.debug( "Validating potential file paths " + potentialLinks );
		
		List<String> validLinks = validateLinks( potentialLinks );
		
		if( validLinks != null ) {
			return validLinks.get(0);
		}
		
		return null;
	}
	
	/**
	 * Returns only 1 valid file link.
	 */
	private static String validateLink( String potentialLink ) {
		
		// short circuit for external resources. 
		if(potentialLink.toLowerCase().startsWith("http")) {
			return potentialLink;
		}
		
		File file = null;
		String absolutePath = null;
				
		for( FileType fileType : FileType.values() ) {
			if( ( potentialLink.endsWith( fileType.name().toLowerCase() ) )) {
				file = new File( potentialLink );
			}			
		}

		if( file != null && file.isFile() ) {
			absolutePath = file.getAbsolutePath(); 
			
			logger.debug( "Validated path " + absolutePath );
		}

		return absolutePath;
	}

	/**
	 * Clean up any artifacts or poorly formed XML
	 */
	private static String tidyDocument( final String documentString ) {

		Tidy tidy = getTidy();
		String correctedDocument = null;
		
		try(StringReader reader = new StringReader( documentString );
				StringWriter writer = new StringWriter())
		{
			tidy.parse( reader, writer );
			correctedDocument = new String( writer.toString() );
		} catch (IOException e) {
			logger.warn("Error during document tidy ", e);
		}

		return correctedDocument;
	}
	
	/**
	 * Instantiate the Tidy HTML validator
	 */
	private static final Tidy getTidy() {
		Tidy tidy = new Tidy();
		Properties properties = new Properties(); 
		
		// these can be overriden with the properties file.
		tidy.setForceOutput( Boolean.TRUE ); 	// output the XHTML even if it fails the validator.
		tidy.setXHTML( Boolean.TRUE ); // only reading XHTML here
		tidy.setDropEmptyParas(Boolean.TRUE);
		tidy.setDocType( "auto" ); // everything will fail horribly if doctype is strict.
		tidy.setMakeClean( Boolean.TRUE );
		tidy.setLogicalEmphasis( Boolean.TRUE ); // replace the b and em tags with proper <strong> tags
		tidy.setHideComments(Boolean.TRUE);
		tidy.setQuiet( Boolean.TRUE );

		// logging
		if( logger.isDebugEnabled() ) {
			tidy.setHideComments( Boolean.FALSE );
			tidy.setQuiet( Boolean.FALSE );
		} 
		
		try (InputStream is = ConvertToEdoc.class.getClassLoader().getResourceAsStream( "/oscar/dms/ConvertToEdoc.properties" )) {			
			if( is != null ) {
				properties.load( is );
			}
		} catch (IOException e) {
			logger.warn("Could not load Tidy properties ", e);
		} 
		
		tidy.getConfiguration().addProps( properties );
		
		logger.debug( printTidyConfig( tidy ) );
		
		return tidy;
	}

	private static final String getImageDirectory() {
		return DEFAULT_IMAGE_DIRECTORY;
	}

	public static String printTidyConfig( Tidy tidy ) {
		
		String log = "";
		
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
				OutputStreamWriter osw = new OutputStreamWriter( baos );) {
			tidy.getConfiguration().printConfigOptions( osw, true );
			log = new String( baos.toString() );
		} catch (IOException e) {
			logger.error("Error", e);
		} 
		
		return log;
	}

}
