/**
 * Copyright (c) 2024. Magenta Health. All Rights Reserved.
 * Copyright (c) 2005, 2009 IBM Corporation and others.
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
 * Contributors:
 * <Quatro Group Software Systems inc.>  <OSCAR Team>
 * <p>
 * Modifications made by Magenta Health in 2024.
 */

package com.quatro.dao;

import com.quatro.model.LookupCodeValue;
import com.quatro.model.LookupTableDefValue;
import org.oscarehr.PMmodule.dao.ProviderDao;
import org.oscarehr.PMmodule.model.Program;
import org.oscarehr.common.model.Facility;

import java.sql.SQLException;
import java.util.List;

public interface LookupDao {

    public List LoadCodeList(String tableId, boolean activeOnly, String code, String codeDesc);

    public LookupCodeValue GetCode(String tableId, String code);

    public List LoadCodeList(String tableId, boolean activeOnly, String parentCode, String code, String codeDesc);

    public LookupTableDefValue GetLookupTableDef(String tableId);

    public List LoadFieldDefList(String tableId);

    public List GetCodeFieldValues(LookupTableDefValue tableDef, String code);

    public List<List> GetCodeFieldValues(LookupTableDefValue tableDef);

    public String SaveCodeValue(boolean isNew, LookupTableDefValue tableDef, List fieldDefList) throws SQLException;

    public String SaveCodeValue(boolean isNew, LookupCodeValue codeValue) throws SQLException;

    public void SaveAsOrgCode(Program program) throws SQLException;

    public boolean inOrg(String org1, String org2);

    public void SaveAsOrgCode(Facility facility) throws SQLException;

    ;

    public void SaveAsOrgCode(LookupCodeValue orgVal, String tableId) throws SQLException;

    public void runProcedure(String procName, String[] params) throws SQLException;

    public int getCountOfActiveClient(String orgCd) throws SQLException;

    public void setProviderDao(ProviderDao providerDao);

}
