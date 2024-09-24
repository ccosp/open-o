/**
 * Copyright (c) 2024. Magenta Health. All Rights Reserved.
 * Copyright (c) 2001-2002. Department of Family Medicine, McMaster University. All Rights Reserved.
 * This software is published under the GPL GNU General Public License.
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 * <p>
 * This software was written for the
 * Department of Family Medicine
 * McMaster University
 * Hamilton
 * Ontario, Canada
 * <p>
 * Modifications made by Magenta Health in 2024.
 */


package org.oscarehr.common.dao;

import java.util.ArrayList;
import java.util.List;

import org.oscarehr.common.model.Document;

import oscar.oscarLab.ca.on.LabResultData;

public interface DocumentResultsDao {
    public boolean isSentToValidProvider(String docNo);

    public boolean isSentToProvider(String docNo, String providerNo);

    public ArrayList<LabResultData> populateDocumentResultsDataOfAllProviders(String providerNo, String demographicNo,
                                                                              String status);

    //retrieve documents belonging to a provider
    public ArrayList<LabResultData> populateDocumentResultsDataLinkToProvider(String providerNo, String demographicNo,
                                                                              String status);

    //retrieve all documents from database
    public ArrayList<LabResultData> populateDocumentResultsData(String providerNo, String demographicNo, String status);

    public List<Document> getPhotosByAppointmentNo(int appointmentNo);
}
 