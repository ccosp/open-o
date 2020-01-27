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
package org.oscarehr.ws.rest.to.model;

public class DocumentTo1 implements Comparable<DocumentTo1> {
	
	private int id;
	private String name;
	private String category;
	private String documentDate;
	private String documentTable;
	private String documentDescription;
	private String contentType;
	
	public DocumentTo1( int id, String name, String documentDate, String category, String contentType) {
		setId(id);
		setName(name);
		setDocumentDate(documentDate);
		setCategory(category);
		setDocumentTable("");
		setContentType(contentType);
	}
	
	public DocumentTo1( int id, String name, String documentDate, String category, String contentType, String documentTable) {
		this(id, name, documentDate, category, contentType);
		setDocumentTable(documentTable);
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}

	public String getDocumentDate() {
		return documentDate;
	}

	public void setDocumentDate(String documentDate) {
		this.documentDate = documentDate;
	}

	public String getDocumentTable() {
		return documentTable;
	}

	public void setDocumentTable(String documentTable) {
		this.documentTable = documentTable;
	}

	public String getDocumentDescription() {
		return documentDescription;
	}

	public void setDocumentDescription(String documentDescription) {
		this.documentDescription = documentDescription;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	@Override
	public int compareTo(DocumentTo1 arg0) {
		if (arg0.getDocumentDate() == null || arg0.getDocumentDate() == null) {
			return 0;
		}
		return getDocumentDate().compareTo(arg0.getDocumentDate());
	}

}
