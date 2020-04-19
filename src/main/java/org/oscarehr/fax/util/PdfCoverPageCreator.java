/**
 * Copyright (c) 2001-2002. Department of Family Medicine, McMaster University. All Rights Reserved.
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
 * Department of Family Medicine
 * McMaster University
 * Hamilton
 * Ontario, Canada
 */
package org.oscarehr.fax.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.oscarehr.util.MiscUtils;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.BaseFont;

import oscar.oscarClinic.ClinicData;

public class PdfCoverPageCreator {
	
	private String note;
	private static BaseFont basefont;
	private static Font body = new Font(basefont, 12, Font.NORMAL);
	private static Font heading = new Font(basefont, 13, Font.NORMAL);
	private static Font heading_bold = new Font(basefont, 14, Font.BOLD);
	private ClinicData clinic;
	
	public PdfCoverPageCreator(String note) {
		this.note = note;
		try {
			basefont = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED );
		} catch (IOException e) {
			MiscUtils.getLogger().error("PDF COVER PAGE ERROR",e);
		} catch (DocumentException e) {
			MiscUtils.getLogger().error("PDF COVER PAGE ERROR",e);
		}
	}
	
	public PdfCoverPageCreator(String note, ClinicData clinic) {
		this(note);
		this.clinic = clinic;
	}
	
	public byte[] createCoverPage() {
	
		byte[] bytearray = new byte[] {};
		
		try(ByteArrayOutputStream os = new ByteArrayOutputStream()) {
			Document document = new Document();
			
			PdfWriter.getInstance(document, os);

			document.setPageSize( PageSize.LETTER );
			document.addTitle( "Fax Cover Page" );
			document.addCreator( "OSCAR" );
			document.addAuthor("OSCAR EMR");
			document.open();
						
			PdfPTable maintable = new PdfPTable(1);
			maintable.setWidthPercentage(100f);
			
			// clinic address
			PdfPCell cell = new PdfPCell(createClinicInfoHeader());		
			cell.setBorder(0);			
			maintable.addCell(cell);
			
			// date line
			cell = new PdfPCell(createDateLine());		
			cell.setBorder(0);	
			maintable.addCell(cell);
			
			// memo
			cell = new PdfPCell(new Phrase(note, body));
			cell.setPaddingTop(30f);
			cell.setBorder(0);
			cell.setColspan(1);
		
			maintable.addCell(cell);

			document.add(maintable);
			document.close();
			
			bytearray = os.toByteArray();
			
			
        } catch (DocumentException e) {
	        
        	MiscUtils.getLogger().error("PDF COVER PAGE ERROR",e);
	        
        }catch( IOException e ) {
        	
        	MiscUtils.getLogger().error("PDF COVER PAGE ERROR",e);
	        
        } 
		
		return bytearray;
	}

	/**
	 * Creates a table and populates it with the clinic information for the header.
	 * @return the table produced
	 */
	private PdfPTable createClinicInfoHeader() {

		if(clinic == null)
		{
			clinic = new ClinicData();
		} 
		
		String letterheadName = clinic.getClinicName();

		PdfPTable infoTable = new PdfPTable(1);

		PdfPCell cell = new PdfPCell( new Phrase(letterheadName, heading_bold) );
		cell.setBorder(0);
		cell.setPadding(0);
		infoTable.addCell(cell);
	
		// add the address details
		Phrase addressPhrase = new Phrase("", heading);

		addressPhrase.add( String.format("%s",clinic.getClinicAddress() ) );
		
		cell.setPhrase( addressPhrase );
		infoTable.addCell(cell);
		
		Phrase cityphrase = new Phrase("", heading);
		
		cityphrase.add(String.format("%s, %s. %s", clinic.getClinicCity(),
				clinic.getClinicProvince(), 
				clinic.getClinicPostal() ) );
	
		cell.setPhrase( cityphrase );
		infoTable.addCell(cell);
		
		// add the telecom info
		Phrase telecomPhrase = new Phrase("", heading);

		telecomPhrase.add( String.format("Phone: %s", clinic.getClinicPhone() ) );
		
		cell.setPhrase( telecomPhrase );
		infoTable.addCell(cell);
		
		telecomPhrase = new Phrase("", heading);
		
		telecomPhrase.add( String.format("Fax: %s", clinic.getClinicFax() ) );
		
		cell.setPhrase( telecomPhrase );
		infoTable.addCell(cell);

		return infoTable;
	}
	
	protected PdfPTable createDateLine() {
		
		PdfPTable datelineborder = new PdfPTable(1);
		datelineborder.setWidthPercentage(100f);
		PdfPCell datecell = new PdfPCell();
		Date today = new Date();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		datecell.setPhrase( new Phrase( simpleDateFormat.format(today), body) ); 
		datecell.setBorder(0);
		datecell.setColspan(1);
		datecell.setPaddingTop(50f);
		datecell.setPaddingBottom(5f);
		datecell.enableBorderSide(PdfPCell.BOTTOM );

		datecell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);

		datelineborder.addCell(datecell);
		
		return datelineborder;		
	}
}
