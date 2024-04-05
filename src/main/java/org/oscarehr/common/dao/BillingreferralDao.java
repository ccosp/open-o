package org.oscarehr.common.dao; 
import java.util.List;
import org.oscarehr.common.model.Billingreferral;

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
