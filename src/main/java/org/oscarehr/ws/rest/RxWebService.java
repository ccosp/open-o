//CHECKSTYLE:OFF
/**
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
 */

package org.oscarehr.ws.rest;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionJavaScript;
import org.oscarehr.common.exception.AccessDeniedException;
import org.oscarehr.common.model.Demographic;
import org.oscarehr.common.model.Drug;
import org.oscarehr.common.model.Favorite;
import org.oscarehr.common.model.Prescription;
import org.oscarehr.managers.DemographicManager;
import org.oscarehr.managers.PrescriptionManager;
import org.oscarehr.managers.RxManager;
import org.oscarehr.managers.RxManagerImpl;
import org.oscarehr.managers.SecurityInfoManager;
import org.oscarehr.rx.util.RxUtil;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.MiscUtils;
import org.oscarehr.ws.rest.conversion.ConversionException;
import org.oscarehr.ws.rest.conversion.DrugConverter;
import org.oscarehr.ws.rest.conversion.FavoriteConverter;
import org.oscarehr.ws.rest.conversion.PrescriptionConverter;
import org.oscarehr.ws.rest.to.*;
import org.oscarehr.ws.rest.to.model.DrugTo1;
import org.oscarehr.ws.rest.to.model.FavoriteTo1;
import org.oscarehr.ws.rest.to.model.PrescriptionTo1;
import org.oscarehr.ws.rest.to.model.PrintPointTo1;
import org.oscarehr.ws.rest.to.model.PrintRxTo1;
import org.oscarehr.ws.rest.to.model.RxStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import openo.log.LogAction;

import javax.imageio.ImageIO;
import javax.naming.OperationNotSupportedException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.StreamingOutput;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Path("/rx")
@Component("rxWebService")
@Produces("application/xml")
public class RxWebService extends AbstractServiceImpl {

    private static Logger logger = MiscUtils.getLogger();

    @Autowired
    protected SecurityInfoManager securityInfoManager;

    @Autowired
    protected RxManager rxManager;

    @Autowired
    protected DrugConverter drugConverter;

    @Autowired
    protected PrescriptionConverter prescriptionConverter;

    @Autowired
    protected FavoriteConverter favoriteConverter;

    @Autowired
    protected DemographicManager demographicManager;

    @Autowired
    protected PrescriptionManager prescriptionManager;

    @GET
    @Path("/drugs")
    @Produces("application/json")
    public DrugSearchResponse drugs(@QueryParam("demographicNo") int demographicNo)
            throws OperationNotSupportedException {
        return drugs(demographicNo, null);

    }

    /**
     * Gets drugs for the demographic and filter based on their status.
     *
     * @param demographicNo the demographic identifier to look up drugs for.
     * @param status        the status to use to filter the results on {"", current, archived,}
     * @return a response containing a list of drugs that meet the status criteria.
     * @throws AccessDeniedException          if the current user does not have permission to access this data.
     * @throws OperationNotSupportedException if the requested status is unknown.
     */
    @GET
    @Path("/drugs{status}")
    @Produces("application/json")
    public DrugSearchResponse drugs(@QueryParam("demographicNo") int demographicNo, @PathParam("status") String status)
            throws OperationNotSupportedException {

        // determine if the user has privileges to view this data.
        if (!securityInfoManager.hasPrivilege(getLoggedInInfo(), "_rx", "r", demographicNo)) {
            throw new AccessDeniedException("_rx", "r", demographicNo);
        }

        DrugSearchResponse response = new DrugSearchResponse();

        List<Drug> drugList;

        if (status == null) {

            drugList = rxManager.getDrugs(getLoggedInInfo(), demographicNo, RxManager.ALL);

        } else if (status.equals(RxManager.CURRENT) || status.equals(RxManager.ARCHIVED)) {

            drugList = rxManager.getDrugs(getLoggedInInfo(), demographicNo, status);

        } else {

            // Throw an exception because we do not know what status they are requesting.
            throw new OperationNotSupportedException();

        }

        response.setContent(this.drugConverter.getAllAsTransferObjects(getLoggedInInfo(), drugList));

        return response;
    }

    @GET
    @Path("rxStatus")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDocumentCategories() {
        return Response.status(Status.OK).entity(RxStatus.values()).build();
    }

    @GET
    @Path("/drugs/{status}/{demographicNo}")
    @Produces(MediaType.APPLICATION_JSON)
    public DrugSearchResponse drugs(@PathParam("status") String status, @PathParam("demographicNo") int demographicNo) {
        DrugSearchResponse drugResponse;

        switch (RxStatus.valueOf(status.trim().toUpperCase())) {
            case ALL:
                drugResponse = getAllDrugs(demographicNo);
                break;
            case ARCHIVED:
                drugResponse = getCurrentDrugs(demographicNo);
                break;
            case CURRENT:
                drugResponse = getLongtermDrugs(demographicNo);
                break;
            case LONGTERM:
                drugResponse = getArchivedDrugs(demographicNo);
                break;
            default:
                drugResponse = null;
                break;
        }

        return drugResponse;
    }

    @GET
    @Path("/drugs/all/{demographicNo}")
    @Produces(MediaType.APPLICATION_JSON)
    public DrugSearchResponse getAllDrugs(@PathParam("demographicNo") int demographicNo) {
        List<Drug> drugList = rxManager.getDrugs(getLoggedInInfo(), demographicNo, RxStatus.ALL);
        return new DrugSearchResponse(this.drugConverter.getAllAsTransferObjects(getLoggedInInfo(), drugList));
    }

    @GET
    @Path("/drugs/current/{demographicNo}")
    @Produces(MediaType.APPLICATION_JSON)
    public DrugSearchResponse getCurrentDrugs(@PathParam("demographicNo") int demographicNo) {
        List<Drug> drugList = rxManager.getDrugs(getLoggedInInfo(), demographicNo, RxStatus.CURRENT);
        return new DrugSearchResponse(this.drugConverter.getAllAsTransferObjects(getLoggedInInfo(), drugList));
    }

    @GET
    @Path("/drugs/longterm/{demographicNo}")
    @Produces(MediaType.APPLICATION_JSON)
    public DrugSearchResponse getLongtermDrugs(@PathParam("demographicNo") int demographicNo) {
        List<Drug> drugList = rxManager.getLongTermDrugs(getLoggedInInfo(), demographicNo);
        return new DrugSearchResponse(this.drugConverter.getAllAsTransferObjects(getLoggedInInfo(), drugList));
    }

    @GET
    @Path("/drugs/archived/{demographicNo}")
    @Produces(MediaType.APPLICATION_JSON)
    public DrugSearchResponse getArchivedDrugs(@PathParam("demographicNo") int demographicNo) {
        List<Drug> drugList = rxManager.getDrugs(getLoggedInInfo(), demographicNo, RxStatus.ARCHIVED);
        return new DrugSearchResponse(this.drugConverter.getAllAsTransferObjects(getLoggedInInfo(), drugList));
    }

    /**
     * Adds a new drug to the drugs table.
     *
     * @param transferObject the drug information
     * @param demographicNo  the identifier for the demographic this drug is for.
     * @return a drug transfer object that reflects the new drug in the database.
     */
    @POST
    @Path("/new")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public DrugResponse addDrug(DrugTo1 transferObject, @QueryParam("demographicNo") int demographicNo) {

        LoggedInInfo info = getLoggedInInfo();

        // determine if the user has privileges to view this data.
        if (!securityInfoManager.hasPrivilege(info, "_rx", "w", demographicNo)) {
            throw new AccessDeniedException("_rx", "w", demographicNo);
        }

        DrugResponse resp = new DrugResponse();

        Drug d;
        Drug outgoingDrug;

        try {

            d = this.drugConverter.getAsDomainObject(info, transferObject);
            outgoingDrug = this.rxManager.addDrug(info, d);

            if (outgoingDrug != null) {

                resp.setSuccess(true);
                resp.setDrug(this.drugConverter.getAsTransferObject(info, outgoingDrug));

            } else {

                resp.setMessage("Could not add the drug, request rejected.");
                resp.setSuccess(false);

            }

        } catch (ConversionException ce) {

            logger.error(ce);
            resp.setMessage("Could not convert provided JSON to Drug domain object, addDrug request rejected.");
            resp.setSuccess(false);

        }

        return resp;

    }

    /**
     * Updates a drug in the database identified by the drugId to reflect
     * the data provided in the incoming transferObject. In the database this
     * has the effect of:
     * a) setting the drug with transferObject.drugId to archived;
     * b) making a new entry that contains data from the transferObject.
     *
     * @param transferObject the data to make the update based on.
     * @param demographicNo  the demographic this drug is for.
     * @return a response object containing a drug transfer object
     * that reflects updated version in the database.
     * @throws AccessDeniedException if the current user is not allowed to write
     *                               prescription information to this demographic.
     */
    @Path("/update")
    @POST
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public DrugResponse updateDrug(DrugTo1 transferObject, @QueryParam("demographicNo") int demographicNo) {

        LoggedInInfo info = getLoggedInInfo();

        DrugResponse resp = new DrugResponse();

        // determine if the user has privileges to view this data.
        if (!securityInfoManager.hasPrivilege(info, "_rx", "w", demographicNo)) {
            throw new AccessDeniedException("_rx", "w", demographicNo);
        }

        Drug incomingDrug;
        Drug outgoingDrug;

        try {

            incomingDrug = this.drugConverter.getAsDomainObject(info, transferObject);
            outgoingDrug = this.rxManager.updateDrug(info, incomingDrug);

            if (outgoingDrug != null) {

                resp.setSuccess(true);
                resp.setDrug(this.drugConverter.getAsTransferObject(info, outgoingDrug));

            } else {

                resp.setSuccess(false);
                resp.setDrug(null);
                resp.setMessage("Unable to update drug, request to updateDrug() failed.");

            }

        } catch (ConversionException ce) {
            logger.info("Failed to convert from transfer object to domain object: " + ce.getMessage());
            logger.error(ce);
            resp.setMessage("Could not convert provided JSON to Drug domain object, updateDrug request rejected.");
            resp.setDrug(null);
            resp.setSuccess(false);

        }

        return resp;

    }

    /**
     * Marks a drug in the database as discontinued. Requires a reason for
     * discontinuing be provided.
     *
     * @param drugId        the id of the drug to discontinue
     * @param reason        the reason for discontinuing the drug, one of:
     *                      {"adverseReaction","allergy","discontinuedByAnotherPhysician",
     *                      "increasedRiskBenefitRatio", "newScientificEvidence", "noLongerNecessary"
     *                      "ineffectiveTreatment", "other", "cost", "drugInteraction",
     *                      "patientRequest", "unknown", "deleted", "simplifyingTreatment"}
     * @param demographicNo the demographic the drug is associated with.
     * @return a generic response indicating success or failure.
     */
    @Path("/discontinue")
    @POST
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public GenericRESTResponse discontinueDrug(@QueryParam("drugId") int drugId,
                                               @QueryParam("reason") String reason,
                                               @QueryParam("demographicNo") int demographicNo
    ) {

        LoggedInInfo info = getLoggedInInfo();

        // Determine if the user has privileges to view this data.
        if (!securityInfoManager.hasPrivilege(info, "_rx", "w", demographicNo)) {
            throw new AccessDeniedException("_rx", "w", demographicNo);
        }

        GenericRESTResponse resp = new GenericRESTResponse();

        if (rxManager.discontinue(info, drugId, demographicNo, reason)) {

            resp.setSuccess(true);
            resp.setMessage("Successfully discontinued drug.");

        } else {

            resp.setSuccess(false);
            resp.setMessage("Failed to discontinue drug.");
        }

        return resp;

    }

    @Path("/drug/{drugId}")
    @POST
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public DrugResponse represcribe(@PathParam("drugId") Integer drugId) {
        LoggedInInfo info = getLoggedInInfo();
        Drug drug = rxManager.getDrug(info, drugId);
        DrugResponse resp = new DrugResponse();
        String special = RxUtil.trimSpecial(drug);
        drug.setSpecial(special);
        resp.setSuccess(true);
        resp.setDrug(this.drugConverter.getAsTransferObject(info, drug));

        return resp;
    }


    /**
     * Creates a prescription for the drugs that are provided.
     *
     * @param drugTransferObjects a non-empty list of drugs to include on the prescription.
     * @param demographicNo       the demographic this prescription is for.
     * @return the completed prescription or an indication of failure.
     */
    @Path("/prescribe")
    @POST
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public PrescriptionResponse prescribe(
            List<DrugTo1> drugTransferObjects,
            @QueryParam("demographicNo") int demographicNo
    ) {


        LoggedInInfo info = getLoggedInInfo();

        // Determine if the user has privileges to view this data.
        if (!securityInfoManager.hasPrivilege(info, "_rx", "w", demographicNo)) {
            throw new AccessDeniedException("_rx", "w", demographicNo);
        }

        PrescriptionResponse resp = new PrescriptionResponse();

        // sanity check for input parameters
        if (drugTransferObjects == null || demographicNo < 0 || drugTransferObjects.size() < 1) {
            resp.setSuccess(false);
            resp.setMessage("Invalid parameters passed to prescribe");
            return resp;
        }

        List<Drug> drugs = new ArrayList<Drug>();

        RxManagerImpl.PrescriptionDrugs pd;

        try {

            //attempt to convert to domain object so that we can
            // work with the objects.

            for (DrugTo1 to : drugTransferObjects) {
                drugs.add(this.drugConverter.getAsDomainObject(info, to));
            }

        } catch (ConversionException ce) {

            logger.info("Failed to convert from transfer object to domain object: " + ce.getMessage());
            logger.error("ERROR", ce);
            resp.setMessage("Could not convert provided drugs to domain object, prescribe failed.");
            resp.setSuccess(false);

            return resp;
        }

        // attempt to prescribe the drugs.
        pd = rxManager.prescribe(info, drugs, demographicNo);

        if (pd != null) {

            // prescribe was success. We can now prepare the response.
            resp.setDrugs(this.drugConverter.getAllAsTransferObjects(info, pd.drugs));
            resp.setPrescription(this.prescriptionConverter.getAsTransferObject(info, pd.prescription));
            resp.setSuccess(true);

            return resp;

        } else {

            logger.error("Failed to prescribe drugs: " + drugs.toString());
            resp.setMessage("Failed to prescribe drugs");
            resp.setSuccess(false);
            return resp;

        }

    }

    /**
     * Looks up the history of a particular drug.
     *
     * @param id            the drug identifier to get history for.
     * @param demographicNo the id of the demographic associated with the drug.
     * @return a response containing the drugs in the history.
     */
    @Path("/history")
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public DrugSearchResponse history(
            @QueryParam("id") int id,
            @QueryParam("demographicNo") int demographicNo
    ) {

        LoggedInInfo info = getLoggedInInfo();

        // Determine if the user has privileges to view this data.
        if (!securityInfoManager.hasPrivilege(info, "_rx", "r", demographicNo)) {
            throw new AccessDeniedException("_rx", "r", demographicNo);
        }

        DrugSearchResponse resp = new DrugSearchResponse();

        List<Drug> drugs = rxManager.getHistory(id, info, demographicNo);
        resp.setContent(drugConverter.getAllAsTransferObjects(info, drugs));

        return resp;
    }


    @Path("/prescriptions")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<PrescriptionTo1> getPrescriptions(@QueryParam("demographicNo") int demographicNo) {

        LoggedInInfo info = getLoggedInInfo();

        // Determine if the user has privileges to view this data.
        if (!securityInfoManager.hasPrivilege(info, "_rx", "r", demographicNo)) {
            throw new AccessDeniedException("_rx", "r", demographicNo);
        }

        List<Prescription> prescriptions = prescriptionManager.getPrescriptions(info, demographicNo);
        List<PrescriptionTo1> retPrescriptions = prescriptionConverter.getAllAsTransferObjects(info, prescriptions);

        return retPrescriptions;
    }

    @Path("/recordPrescriptionPrint/{scriptNo}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public PrescriptionTo1 recordPrescriptionPrint(@PathParam("scriptNo") int scriptNo) {

        LoggedInInfo info = getLoggedInInfo();

        // Determine if the user has privileges to view this data.
        //if (!securityInfoManager.hasPrivilege(info, "_rx", "r", demographicNo)) {
        //    throw new AccessDeniedException("_rx", "r", demographicNo);
        //}


        prescriptionManager.print(info, scriptNo);

        Prescription prescription = prescriptionManager.getPrescription(info, scriptNo);


        PrescriptionTo1 retPrescriptions = prescriptionConverter.getAsTransferObject(info, prescription);

        return retPrescriptions;
    }

    @Path("/favorites")
    @GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public FavoriteResponse favourites() {

        // No access control check required, we are not accessing a patient record.
        // TODO: Revise access control policies and re-evalute this to see if it requires access control check.

        LoggedInInfo info = getLoggedInInfo();

        FavoriteResponse resp = new FavoriteResponse();

        List<Favorite> favs = this.rxManager.getFavorites(info.getLoggedInProviderNo());

        resp.setDrugs(this.favoriteConverter.getAllAsTransferObjects(info, favs));

        return resp;

    }

    @Path("/favorites")
    @POST
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public GenericRESTResponse addFavorite(FavoriteTo1 newFavorite) {

        // No access control check required, we are not accessing a patient record.
        // TODO: Revise access control policies and re-evalute this to see if it requires access control check.

        LoggedInInfo info = getLoggedInInfo();

        GenericRESTResponse resp = new GenericRESTResponse();

        try {

            Favorite f = this.favoriteConverter.getAsDomainObject(info, newFavorite);

            if (this.rxManager.addFavorite(f)) {
                resp.setSuccess(true);
                resp.setMessage("added favorite");
            } else {
                resp.setSuccess(false);
                resp.setMessage("failed to add new favorite");
            }

        } catch (ConversionException e) {
            logger.error(e.getStackTrace());
            resp.setSuccess(false);
            resp.setMessage("Failed to add favorite.");
        }

        return resp;

    }


    @GET
    @Path("/{demographicNo}/watermark/{rxNo}")
    @Produces("image/png")
    public StreamingOutput watermark(@PathParam("demographicNo") Integer demographicNo, @PathParam("rxNo") Integer rxNo, @Context HttpServletRequest request, @Context HttpServletResponse response) {
        LoggedInInfo loggedInInfo = getLoggedInInfo();
        response.setContentType("image/png");
        List<Drug> list = prescriptionManager.getDrugsByScriptNo(loggedInInfo, rxNo, null);
        StringBuilder sb = new StringBuilder();
        for (Drug drug : list) {
            sb.append(drug.getSpecial());
            sb.append("\n\n");
        }

        final String text = sb.toString();
        return new StreamingOutput() {
            @Override
            public void write(java.io.OutputStream os)
                    throws IOException, WebApplicationException {
                try {

                    BufferedImage img = RxUtil.getWaterMarkImage(text);
                    ImageIO.write(img, "PNG", os);

                } catch (Exception e) {
                    logger.error("error writing image", e);
                }
            }

        };

    }

    @POST
    @Path("/{demographicNo}/print/{rxNo}")
    @Produces("application/pdf")
    @Consumes(MediaType.APPLICATION_JSON)
    public StreamingOutput print(@PathParam("demographicNo") Integer demographicNo, @PathParam("rxNo") Integer rxNo, @Context HttpServletRequest request, PrintRxTo1 transferObject) {

        LoggedInInfo loggedInInfo = getLoggedInInfo();
        Demographic demographic = demographicManager.getDemographic(getLoggedInInfo(), demographicNo);

        LogAction.addLog(loggedInInfo, "PRINT", "drug", "" + transferObject.getDrugId(), "" + demographicNo, transferObject.toString());

        for (PrintPointTo1 point : transferObject.getPrintPoints()) {

            if (point.getText().startsWith("@@")) {
                switch (point.getText()) {
                    case "@@HIN":
                        point.setText(demographic.getHin());
                        break;
                    case "@@DEMO.FIRSTNAME":
                        point.setText(demographic.getFirstName());
                        break;
                    case "@@DEMO.LASTNAME":
                        point.setText(demographic.getLastName());
                        break;
                    case "@@ADDRESS.STREET":
                        point.setText(demographic.getAddress());
                        break;
                    case "@@ADDRESS.CITY":
                        point.setText(demographic.getCity());
                        break;
                    case "@@ADDRESS.PROV":
                        point.setText(demographic.getProvince());
                        break;

                    case "@@DEMO.DOB.DAY":
                        point.setText(demographic.getDateOfBirth());
                        break;
                    case "@@DEMO.DOB.MONTH":
                        point.setText(demographic.getMonthOfBirth());
                        break;
                    case "@@DEMO.DOB.YEAR":
                        point.setText(demographic.getYearOfBirth());
                        break;
                }


            }


        }

        final PrintRxTo1 rxToPrint = transferObject;

        return new StreamingOutput() {
            @Override
            public void write(java.io.OutputStream os)
                    throws IOException, WebApplicationException {
                try {
                    PDDocument document = new PDDocument();

                    //Embedding javascript to print dialog
                    if (rxToPrint.isAutoPrint()) {
                        PDActionJavaScript PDAjavascript = new PDActionJavaScript("this.print();");
                        document.getDocumentCatalog().setOpenAction(PDAjavascript);
                    }

                    PDRectangle rect = new PDRectangle(rxToPrint.getWidth(), rxToPrint.getHeight());
                    PDPage page = new PDPage(rect);// PDPage.PAGE_SIZE_A5);
                    document.addPage(page);

                    // Create a new font object selecting one of the PDF base fonts
                    PDFont font = PDType1Font.HELVETICA_BOLD;

                    // Start a new content stream which will "hold" the to be created content
                    PDPageContentStream contentStream = new PDPageContentStream(document, page);

                    for (PrintPointTo1 point : rxToPrint.getPrintPoints()) {
                        contentStream.beginText();
                        contentStream.setFont(font, point.getFontSize());
                        contentStream.moveTextPositionByAmount(point.getX(), point.getY());
                        contentStream.drawString(point.getText());
                        contentStream.endText();

                    }

                    float[] x = rxToPrint.getxPolygonCoords();
                    float[] y = rxToPrint.getyPolygonCoords();
                    if (x != null && y != null && x.length > 1 && y.length > 1) {
                        contentStream.drawPolygon(x, y);
                    }

                    contentStream.close();


                    document.save(os);
                    document.close();
                } catch (Exception e) {
                    logger.error("error streaming", e);
                } finally {
                    IOUtils.closeQuietly(os);
                }

            }
        };
    }


}
