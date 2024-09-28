//CHECKSTYLE:OFF
/**
 * Copyright (c) 2024. Magenta Health. All Rights Reserved.
 * Copyright (c) 2006-. OSCARservice, OpenSoft System. All Rights Reserved.
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
 * Modifications made by Magenta Health in 2024.
 */
package ca.openosp.openo.common.dao;

import java.util.List;

import ca.openosp.openo.common.model.Billingreferral;

public interface BillingreferralDao extends AbstractDao<Billingreferral> {
    public Billingreferral getByReferralNo(String referral_no);

    public Billingreferral getById(int id);

    public List<Billingreferral> getBillingreferrals();

    public List<Billingreferral> getBillingreferral(String referral_no);

    public List<Billingreferral> getBillingreferral(String last_name, String first_name);

    public List<Billingreferral> getBillingreferralByLastName(String last_name);

    public List<Billingreferral> getBillingreferralBySpecialty(String specialty);

    public List<Billingreferral> searchReferralCode(String codeName, String codeName1, String codeName2, String desc, String fDesc, String desc1, String fDesc1, String desc2, String fDesc2);

    public void updateBillingreferral(Billingreferral obj);

    public String getReferralDocName(String referral_no);
}
