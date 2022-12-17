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

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.itextpdf.text.Document;
import org.oscarehr.util.MiscUtils;


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
    public static void concat(List<Object> alist,OutputStream out) {
        Document document = null;
        PdfReader reader = null;
        PdfCopy writer = null;
        try {
            int pageOffset = 0;
            ArrayList master = new ArrayList();
            int f =0;

            boolean fileAsStream = false;
			String name = "";

            MiscUtils.getLogger().debug("Size of list = "+alist.size());

            while (f < alist.size()) {
            	// we create a reader for a certain document
    			Object o = alist.get(f);

    			if (o instanceof InputStream) {
    				name = "";
    				fileAsStream = true;
    			} else {
    				name = (String) alist.get(f);
    				fileAsStream = false;
    			}

    			if (fileAsStream) {
    				reader = new PdfReader((InputStream) alist.get(f));
    			} else {
    				reader = new PdfReader(name);
    			}

                if (reader.isEncrypted()){
                    try {
                        Field encryptionField = reader.getClass().getDeclaredField("encrypted");
                        encryptionField.setAccessible(true);
                        encryptionField.set(reader, false);
                    } catch (Exception e) {
                    	MiscUtils.getLogger().error("Error",e);
                    }
    			}

                reader.consolidateNamedDestinations();
                // we retrieve the total number of pages
                int n = reader.getNumberOfPages();
                List bookmarks = SimpleBookmark.getBookmark(reader);
                if (bookmarks != null) {
                    if (pageOffset != 0)
                        SimpleBookmark.shiftPageNumbers(bookmarks, pageOffset, null);
                    master.addAll(bookmarks);
                }
                pageOffset += n;
                MiscUtils.getLogger().debug("There are " + n + " pages in " + name);

                if (f == 0) {
                    // step 1: creation of a document-object
                    document = new Document(reader.getPageSizeWithRotation(1));
                    // step 2: we create a writer that listens to the document
                    writer = new PdfCopy(document, out);
                    // step 3: we open the document
                    document.open();
                }
                // step 4: we add content
                PdfImportedPage page;
                for (int i = 0; i < n; ) {
                    ++i;
                    page = writer.getImportedPage(reader, i);
                    writer.addPage(page);
                    MiscUtils.getLogger().debug("Processed page " + i);
                }
                PRAcroForm form = reader.getAcroForm();
                if (form != null)
                    writer.copyDocumentFields(reader);
                f++;
            }
            if (master.size() > 0)
                writer.setOutlines(master);
        }
        catch(Exception e) {
            MiscUtils.getLogger().error("Error", e);
        }
        finally {
            if (document != null) {
                document.close();
            }

            if (reader != null) {
                reader.close();
            }

            if (writer != null) {
                writer.flush();
                writer.close();
            }
        }
    }

}
