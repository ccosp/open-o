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


package oscar.oscarEncounter.oscarConsultationRequest.pageUtil;

import java.util.ArrayList;
import java.util.List;
import org.oscarehr.common.model.ConsultDocs;
import org.oscarehr.util.LoggedInInfo;
import oscar.oscarLab.ca.on.CommonLabResultData;
import oscar.oscarLab.ca.on.LabResultData;

/**
 *
 * @author rjonasz
 */
public class ConsultationAttachLabs extends ConsultationAttach{
    private final String DOCTYPE = ConsultDocs.DOCTYPE_LAB;

    /** Creates a new instance of ConsultationAttachLabs */
    public ConsultationAttachLabs(String provNo, String demo, String req, String[] d) {
        super(provNo, demo, req, d);
    }

    public void attach(LoggedInInfo loggedInInfo) {

        //first we get a list of currently attached labs
        CommonLabResultData labResData = new CommonLabResultData();
        List<LabResultData> oldlist = labResData.populateLabResultsData(loggedInInfo, getDemoNo(), getReqId(), CommonLabResultData.ATTACHED);
        List<String> newlist = new ArrayList<>();
        List<LabResultData> keeplist = new ArrayList<>();
        List<String> currentList = super.getDocs();

        boolean alreadyAttached;
        //add new documents to list and get ids of docs to keep attached
        for(int i = 0; i < currentList.size(); ++i) {
            alreadyAttached = false;
            for(int j = 0; j < oldlist.size(); ++j) {
                if( (oldlist.get(j)).labPatientId.equals(currentList.get(i)) ) {
                    alreadyAttached = true;
                    keeplist.add(oldlist.get(j));
                    break;
                }
            }
            if( !alreadyAttached ) {
                newlist.add(currentList.get(i));
            }
        }

        //now compare what we need to keep with what we have and remove association
        for (LabResultData old : oldlist) {
            if (keeplist.contains(old)) {
                continue;
            }
            super.detach(loggedInInfo, old.labPatientId, DOCTYPE);
        }

        super.attach(loggedInInfo, newlist, DOCTYPE);
    }

}
