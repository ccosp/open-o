//CHECKSTYLE:OFF
/**
 * Copyright (c) 2024. Magenta Health. All Rights Reserved.
 * Copyright (c) 2013-2015. Department of Computer Science, University of Victoria. All Rights Reserved.
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
 * Department of Computer Science
 * LeadLab
 * University of Victoria
 * Victoria, Canada
 * <p>
 * Modifications made by Magenta Health in 2024.
 */

package org.oscarehr.managers;

import org.apache.logging.log4j.Logger;
import org.oscarehr.common.dao.DrugDao;
import org.oscarehr.common.dao.FavoriteDao;
import org.oscarehr.common.exception.AccessDeniedException;
import org.oscarehr.common.model.Drug;
import org.oscarehr.common.model.Favorite;
import org.oscarehr.common.model.Prescription;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.ws.rest.to.model.RxStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ca.openosp.openo.log.LogAction;
import ca.openosp.openo.oscarDemographic.data.RxInformation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Manager class to access data regarding prescriptions.
 */
@Service
public class RxManagerImpl implements RxManager {

    private static Logger logger = MiscUtils.getLogger();

    @Autowired
    protected SecurityInfoManager securityInfoManager;

    @Autowired
    protected PrescriptionManager prescriptionManager;

    @Autowired
    protected DrugDao drugDao;

    @Autowired
    protected FavoriteDao favoriteDao;

    /**
     * Gets drugs for the given demographic that are marked as current.
     *
     * @param info          details regarding the current user
     * @param demographicNo the demographic to get current drugs for.
     * @param status        the status to filter on the meds, one of {ALL, CURRENT, ARCHIVED}
     * @return a list of the drugs that are marked as current in the demographics record.
     * @throws UnsupportedOperationException when a status that is not supported is requested.
     */
    @Override
    public List<Drug> getDrugs(LoggedInInfo info, int demographicNo, String status)
            throws UnsupportedOperationException {

        RxStatus rxStatus;
        List<Drug> drugs = Collections.emptyList();

        try {
            String drugstatus = status.trim().toUpperCase();
            rxStatus = Enum.valueOf(RxStatus.class, drugstatus);
            drugs = getDrugs(info, demographicNo, rxStatus);
        } catch (Exception e) {
            throw new UnsupportedOperationException("Unknown drug status: " + status);
        }

        return drugs;
    }

    @Override
    public List<Drug> getDrugs(LoggedInInfo info, int demographicNo, RxStatus status) {

        LogAction.addLogSynchronous(info, "RxManager.getDrugs", "demographicNo=" + demographicNo + " status=" + status);

        // Access control check.
        readCheck(info, demographicNo);

        List<Drug> drugs;

        // find drugs from the DAO based on status.
        switch (status) {
            case ALL:
                drugs = drugDao.findByDemographicId(demographicNo);
                break;
            case ARCHIVED:
                drugs = drugDao.findByDemographicId(demographicNo, true);
                break;
            case CURRENT:
                drugs = drugDao.findByDemographicId(demographicNo, false);
                break;
            case LONGTERM:
                drugs = drugDao.findLongTermDrugsByDemographic(demographicNo);
                break;
            default:
                drugs = Collections.emptyList();
                break;
        }

        return drugs;
    }


    /**
     * Get drug by id,  User is checked to make sure they have permissions to view drug by checking the patient it was prescribed too.
     *
     * @param info   details regarding the current user
     * @param drugId id of the drug to retreive
     * @return drug
     * @throws UnsupportedOperationException when a drug is not found.
     */
    @Override
    public Drug getDrug(LoggedInInfo info, int drugId) throws UnsupportedOperationException {

        Drug drug = drugDao.find(drugId);

        if (drug == null) {
            throw new UnsupportedOperationException("drug not found: " + drugId);
        }
        //(LoggedInInfo loggedInInfo, String action, String content, String contentId, String demographicNo, String data)
        LogAction.addLog(info, "RxManager.getDrug", "drugs", "" + drugId, "" + drug.getDemographicId(), drug.toString());

        // Access control check.
        readCheck(info, drug.getDemographicId());


        return drug;
    }

    /**
     * Get a list of formatted medications the demographic has prescribed and are active. The returned list contains the drug's special field, or if one doesn't exist, the medication's name.
     *
     * @param loggedInInfo  Current user's logged in info
     * @param demographicNo The demographic number to get the medications for
     * @return List of medication information from the medication's special field
     */
    public List<String> getCurrentSingleLineMedications(LoggedInInfo loggedInInfo, int demographicNo) {
        List<String> singleLineMedications = new ArrayList<>();
        // List<Drug> medications = getDrugs(loggedInInfo, demographicNo, CURRENT);

        // for (Drug medication : medications) {
        //     if(medication.isCurrent() || (medication.isLongTerm() && !medication.isArchived())) {
        //         String prescription = medication.getSpecial();

        //         if (StringUtils.isNotEmpty(prescription)) {
        //             prescription = prescription.replace("\n", " ").replace("\r", " ");
        //             singleLineMedications.add(WordUtils.capitalizeFully(prescription));
        //         } else {
        //             singleLineMedications.add(medication.getDrugName());
        //         }
        //     }
        // }

        RxInformation rxInformation = new RxInformation();
        String[] medications = rxInformation.getCurrentMedication(String.valueOf(demographicNo)).split("\\n");
        for (String medication : medications) {
            singleLineMedications.add(medication);
        }

        return singleLineMedications;
    }

    /**
     * Adds a new drug to the database.
     *
     * @param info information regarding the logged in user.
     * @param d    the drug object to add to the database.
     * @return the drug object that was added.
     */
    @Override
    public Drug addDrug(LoggedInInfo info, Drug d) {

        LogAction.addLogSynchronous(info, "RxManager.addDrug", "providerNo=" +
                info.getLoggedInProviderNo()
                + " drug.brandName=" + d.getBrandName()
                + " demographicNo=" + d.getDemographicId());

        // Will throw an exception if access is denied.
        this.writeCheck(info, d.getDemographicId());

        // Have to set ID to null so that database
        // can auto-generate one for this drug.
        d.setId(null);

        if (this.drugDao.addNewDrug(d)) {

            // If the addNewDrug(d) call succeeds d will
            // contain the ID that was auto-generated by the
            // database.

            return d;

        } else {

            return null;

        }

    }

    /**
     * Updates the a drug, d, that is passed in the database.
     * The update is completed by making a "new" entry for the updated
     * version of the drug and then setting the "old" version to archived.
     * This is done so that:
     * a) audit trail is maintained;
     * b) database state will remain workable for legacy versions.
     *
     * @param info information regarding the user making the request.
     * @param d    the drug to replace the old version with.
     * @return a drug object representing the new version, null otherwise.
     */
    @Override
    public Drug updateDrug(LoggedInInfo info, Drug d) {

        // Will throw an exception if access is denied.
        this.writeCheck(info, d.getDemographicId());

        if (d.getId() == null) {
            return null;
        }

        Drug old = this.drugDao.find(d.getId());

        if (old == null) {
            return null;
        }

        // Attempt to add the new drug first, if this fails
        // the don't try to update the old drug to archived.

        Drug temp = this.addDrug(info, d);

        if (temp == null) {
            return null;
        }

        // Update fields in old drug.
        old.setArchived(true);
        old.setArchivedDate(new Date());

        // Have to use the Drug.REPRESCRIBED identifier
        //  for archived_reason so that the database
        //  is remains in a state that is usable
        //  by the legacy (Rx2) UI.
        // TODO: Rework legacy Rx UI to use a new field called "updated"
        old.setArchivedReason(Drug.REPRESCRIBED);

        // Push changes on old drug into database.
        this.drugDao.merge(old);

        LogAction.addLogSynchronous(info, "RxManager.updateDrug", "providerNo=" +
                info.getLoggedInProviderNo()
                + " drug.brandName=" + d.getBrandName()
                + " demographicNo=" + d.getDemographicId());

        return temp;
    }

    /**
     * Updates a drug entry in the database to be "discontinued", this entails:
     * a) marking the entry as archived;
     * b) setting a reason for archiving;
     * c) setting the archive date.
     *
     * @param info          information for the logged in user
     * @param drugId        the id of the drug to discontinue
     * @param demographicId the demographic for the drug to discontinue
     * @param reason        a word/phrase that describes why the drug is being discontinued, one of:
     *                      {"adverseReaction","allergy","discontinuedByAnotherPhysician",
     *                      "increasedRiskBenefitRatio", "newScientificEvidence", "noLongerNecessary"
     *                      "ineffectiveTreatment", "other", "cost", "drugInteraction",
     *                      "patientRequest", "unknown", "deleted", "simplifyingTreatment"}
     * @return true if the drug was successfully discontinued, false otherwise.
     * @throws UnsupportedOperationException if the reason for discontinuing the drug is not recognized.
     */
    @Override
    public boolean discontinue(LoggedInInfo info, int drugId, int demographicId, String reason) {

        // Check that this user is allowed to write to the demographic's
        // prescribing information.
        this.writeCheck(info, demographicId);

        Drug d = this.drugDao.find(drugId);

        if (d == null) { //make sure we have a drug to operate on.

            logger.info("No drug with drugId: " + drugId + " found, failed to discontinue.");

            return false;

        } else if (d.getDemographicId() != demographicId) { //check to make sure demographic matches drug.

            logger.info("Drug demographic (" + d.getDemographicId() + ") does not match input " +
                    "demographic (" + demographicId + "), failed to discontinue.");
            return false;

        } else {

            // TODO: Refactor Drug to use an Enum to keep track of reasons for discontinuing a drug.

            // set the reason for discontinuation.

            if (reason.equals(Drug.ADVERSE_REACTION)) {

                d.setArchivedReason(Drug.ADVERSE_REACTION);

            } else if (reason.equals(Drug.ALLERGY)) {

                d.setArchivedReason(Drug.ALLERGY);

            } else if (reason.equals(Drug.DISCONTINUED_BY_ANOTHER_PHYSICIAN)) {

                d.setArchivedReason(Drug.DISCONTINUED_BY_ANOTHER_PHYSICIAN);

            } else if (reason.equals(Drug.DRUG_INTERACTION)) {

                d.setArchivedReason(Drug.DRUG_INTERACTION);

            } else if (reason.equals(Drug.COST)) {

                d.setArchivedReason(Drug.COST);

            } else if (reason.equals(Drug.DELETED)) {

                d.setArchivedReason(Drug.DELETED);

            } else if (reason.equals(Drug.INCREASED_RISK_BENEFIT_RATIO)) {

                d.setArchivedReason(Drug.INCREASED_RISK_BENEFIT_RATIO);

            } else if (reason.equals(Drug.NEW_SCIENTIFIC_EVIDENCE)) {

                d.setArchivedReason(Drug.NEW_SCIENTIFIC_EVIDENCE);

            } else if (reason.equals(Drug.INEFFECTIVE_TREATMENT)) {

                d.setArchivedReason(Drug.INEFFECTIVE_TREATMENT);

            } else if (reason.equals(Drug.NO_LONGER_NECESSARY)) {

                d.setArchivedReason(Drug.NO_LONGER_NECESSARY);

            } else if (reason.equals(Drug.SIMPLIFYING_TREATMENT)) {

                d.setArchivedReason(Drug.SIMPLIFYING_TREATMENT);

            } else if (reason.equals(Drug.PATIENT_REQUEST)) {

                d.setArchivedReason(Drug.PATIENT_REQUEST);

            } else if (reason.equals(Drug.PRESCRIBING_ERROR)) {

                d.setArchivedReason(Drug.PRESCRIBING_ERROR);

            } else if (reason.equals(Drug.UNKNOWN)) {

                d.setArchivedReason(Drug.UNKNOWN);

            } else if (reason.equals(Drug.OTHER)) {

                d.setArchivedReason(Drug.OTHER);

            } else {

                throw new UnsupportedOperationException("Unsupported discontinue reason: " + reason);

            }

            d.setArchived(true);
            d.setArchivedDate(new Date());

            // persist the change to the database.
            this.drugDao.merge(d);

            LogAction.addLogSynchronous(info, "RxManager.discontinue", "providerNo=" +
                    info.getLoggedInProviderNo()
                    + " drug.brandName=" + d.getBrandName()
                    + " demographicNo=" + d.getDemographicId());

            return true;
        }

    }

    /**
     * Creates one new prescription which references all of the listed drugs. Requires the user
     * have write privileges. Marks old drugs as archived ("represcribed") and creates new versions
     * of the listed drugs to associate with the prescription.
     *
     * @param info   the current user's logged in information.
     * @param drugs  a non-empty list of the drugs to prescribe
     * @param demoNo the demographic to prescribe for.
     * @return a record of the prescription and drugs, null if the prescription could not be created.
     */
    @Override
    public PrescriptionDrugs prescribe(LoggedInInfo info, List<Drug> drugs, Integer demoNo) {

        // sanity check on inputs
        if (info == null || drugs == null || drugs.size() < 1 || demoNo < 0) {
            logger.error("Sanity Check Failed");
            return null;
        }

        // Check if we are allowed to write to this patient's chart.
        this.writeCheck(info, demoNo);

        // Check to ensure that the drugs can be prescribed
        for (Drug d : drugs) {
            if (!canPrescribe(d)) {
                logger.error("Can't prescribe drug returning null");
                return null;
            }
        }

        // p will contain the ID of the prescription in the DB.
        // use this ID and associate with drugs before doing updates.
        Prescription p = this.prescriptionManager.createNewPrescription(info, drugs, demoNo);

        // update drugs in the DB. This should be done
        // after all of the drugs are checked to see if they
        // can be prescribed.
        Drug temp;
        List<Drug> newDrugs = new ArrayList<Drug>();

        for (Drug d : drugs) {

            // set the scriptNo field the prescription we just created.
            d.setScriptNo(p.getId());

            temp = this.updateDrug(info, d);

            if (temp == null) {

                // if the update attempt returned
                // null then it means the drug was not
                // previously in the DB.
                // Try adding it now.
                temp = this.addDrug(info, d);

                if (temp == null) {

                    // failed to add drug.
                    return null;

                }
            }

            newDrugs.add(temp);

        }

        return new PrescriptionDrugs(p, newDrugs);

    }

    /**
     * Finds the history of a drug using its ATC code.
     *
     * @param id            the drug id to find the history of
     * @param info          the current users info
     * @param demographicNo the identifier for the demographic.
     * @return a list of previously documented drugs that make up the history,
     * empty list if no history could be found.
     */
    @Override
    public List<Drug> getHistory(Integer id, LoggedInInfo info, Integer demographicNo) {

        readCheck(info, demographicNo);

        List<Drug> potentialDrugs = drugDao.findByDemographicIdAndDrugId(demographicNo, id);

        List<Drug> historyDrugs;

        if (potentialDrugs.size() == 1 && potentialDrugs.get(0).getAtc() != null && !potentialDrugs.get(0).getAtc().trim().isEmpty()) {
            historyDrugs = drugDao.findByDemographicIdAndAtc(demographicNo, potentialDrugs.get(0).getAtc());
            if (historyDrugs.isEmpty()) { // not all drugs have ATC codes ie custom drugs.
                return potentialDrugs;
            }
        } else {
            historyDrugs = new ArrayList<Drug>();
        }

        return historyDrugs;
    }

    /**
     * Gets a list of the provider's favorite drugs.
     *
     * @param pid the provider id.
     * @return a list of favorite drugs, empty list if no favorites.
     */
    @Override
    public List<Favorite> getFavorites(String pid) {

        if (pid != null && !pid.isEmpty()) {
            return this.favoriteDao.findByProviderNo(pid);
        }

        return new ArrayList<Favorite>();
    }

    /**
     * Adds a new favorite prescription to the providers record.
     *
     * @param f the favorite to add.
     * @return true if added, false otherwise
     */
    @Override
    public Boolean addFavorite(Favorite f) {

        // Consider adding write check here...but we are not accessing a patient
        // record. This may change if access control policy changes.

        if (f == null && !this.validFavorite(f)) {

            return false;

        } else {

            this.favoriteDao.persist(f);
            return true;

        }
    }

    /**
     * Determines if a Favorite object contains the minimum information.
     *
     * @param f the Favorite objec to check.
     * @return true if the objec is valid, false otherwise.
     */
    protected Boolean validFavorite(Favorite f) {

        if (f.getProviderNo() == null || f.getProviderNo().isEmpty()) return false;
        else if (f.getBn() == null || f.getBn().isEmpty()) return false;
        else if (f.getGn() == null || f.getGn().isEmpty()) return false;
        else if (f.getSpecial() == null || f.getSpecial().isEmpty()) return false;
        else if (f.getDuration() == null || f.getDuration().isEmpty()) return false;
        else if (f.getDurationUnit() == null || f.getDurationUnit().isEmpty()) return false;
        else return true;
    }

    /**
     * Determines if all of the information to create a valid prescription is
     * present in the drug object. Only check the bare minimum fields.
     *
     * @param d the drug to check.
     * @return true if the drug contains the required fields, false otherwise.
     */
    protected Boolean canPrescribe(Drug d) {

        if (d == null) {
            logger.debug("drug was null returning false");
            return false;
        }

        if (d.getProviderNo() == null || d.getProviderNo().equals("")) {
            logger.debug("provider was null or blank returning false");
            return false;
        }

        if (d.getDemographicId() == null || d.getDemographicId() < 0) {
            logger.debug("demographic was null returning false");
            return false;
        }

        if (d.getRxDate() == null) {
            logger.debug("rx date was null returning false");
            return false;
        }

        if (d.getEndDate() == null || d.getRxDate().after(d.getEndDate())) {
            logger.debug("drug endDate was null");
            return false;
        }

        if (d.getSpecial() == null || d.getSpecial().equals("")) {
            logger.debug("drug special instructions was null returning false");
            return false;
        }

        return true;
    }

    /**
     * Access control check:
     * determines current user is allow to READ prescription information
     * for the current demographic.
     *
     * @param info          information regarding the user making the request.
     * @param demographicNo the identifier for the patient being accessed.
     * @throws AccessDeniedException if access is denied.
     */

    protected void readCheck(LoggedInInfo info, int demographicNo) {
        if (!securityInfoManager.hasPrivilege(info, "_rx", "r", demographicNo)) {
            throw new AccessDeniedException("_rx", "r", demographicNo);
        }
    }

    /**
     * Access control check:
     * Determines if the current user is allowed to WRITE prescription information
     * to the chart of the current demographic.
     *
     * @param info          information regarding the user making the request.
     * @param demographicNo the identifier for the patient being accessed.
     * @throws AccessDeniedException if access is denied.
     */

    protected void writeCheck(LoggedInInfo info, int demographicNo) {
        if (!securityInfoManager.hasPrivilege(info, "_rx", "w", demographicNo)) {
            throw new AccessDeniedException("_rx", "w", demographicNo);
        }
    }

    @Override
    public List<Drug> getLongTermDrugs(LoggedInInfo info, int demographicNo) {
        LogAction.addLogSynchronous(info, "RxManager.getLongTermDrugs", "demographicNo=" + demographicNo);

        // Access control check.
        readCheck(info, demographicNo);

        return drugDao.findLongTermDrugsByDemographic(demographicNo);

    }


    // statuses for drugs

}
 