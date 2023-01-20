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
package oscar.util;

/*
 *
 * This code is free software. It may only be copied or modified
 * if you include the following copyright notice:
 *
 * This class by Mark Thompson. Copyright (c) 2002 Mark Thompson.
 *
 * This code is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * itext@lowagie.com
 */


/**
 * This class demonstrates copying a PDF file using iText.
 * @author Mark Thompson
 */

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.oscarehr.util.MiscUtils;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PRAcroForm;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.SimpleBookmark;

public class ConcatPDF {


    public static void concat (ArrayList<Object> alist,String filename){
        try(OutputStream os = new FileOutputStream(filename);) {        	
        	concat(alist,os);
        }catch(Exception e){
            MiscUtils.getLogger().error("Error", e);
        }
    }

    /**
     * This class can be used to concatenate existing PDF files.
     * (This was an example known as PdfCopy.java)
     */
    public static void concat(List<Object> fileOrInputStreamPdfList, OutputStream outputStream) {
        PDFMergerUtility pdfMerger = new PDFMergerUtility();

        try {
            for (Object o : fileOrInputStreamPdfList) {
                PDDocument documentReader;

                //load pdf file
                if (o instanceof InputStream) {
                    documentReader = PDDocument.load((InputStream) o);
                } else {
                    String fileName = (String) o;
                    documentReader = PDDocument.load(new File(fileName));
                }

                // check if encrypted, if so de-encrypt
                if (documentReader.isEncrypted()){
                    try {
                        documentReader.setAllSecurityToBeRemoved(true);
                    }
                    catch (Exception e) {
                        MiscUtils.getLogger().error("The document is encrypted and can't be decrypted", e);
                    }
                }

                // save document to output stream and add resulting data to merger
                try(ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    InputStream inputStream = new ByteArrayInputStream(baos.toByteArray())) {
                    documentReader.save(baos);
                    pdfMerger.addSource(inputStream);
                    documentReader.close();
                } catch (IOException e) {
                    MiscUtils.getLogger().error("Document could not be written to disk", e);
                }
            }

            pdfMerger.setDestinationStream(outputStream);
            pdfMerger.mergeDocuments(MemoryUsageSetting.setupMainMemoryOnly());
        } catch (IOException e) {
            MiscUtils.getLogger().error("Error", e);
        }
    }

}
