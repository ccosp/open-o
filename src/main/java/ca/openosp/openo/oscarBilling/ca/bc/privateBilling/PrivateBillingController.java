//CHECKSTYLE:OFF
package ca.openosp.openo.oscarBilling.ca.bc.privateBilling;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.HashMap;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import ca.openosp.openo.common.model.Demographic;
import ca.openosp.openo.common.model.Provider;
import ca.openosp.openo.PMmodule.dao.ProviderDao;
import ca.openosp.openo.oscarDemographic.data.DemographicData;
import ca.openosp.openo.ehrutil.LoggedInInfo;
import ca.openosp.openo.ehrutil.SpringUtils;
import ca.openosp.openo.oscarClinic.ClinicData;

/*
 * Author: Charles Liu <charles.liu@nondfa.com>
 * Company: WELL Health Technologies Corp.
 * Date: December 6, 2018
 */
public class PrivateBillingController extends HttpServlet {
    private static String LIST_PRIVATE_BILLS = "billing/CA/BC/privateBilling/viewStatement.jsp";
    private static String PRINT_PREVIEW_BILLS = "billing/CA/BC/privateBilling/printPreview.jsp";
    private PrivateBillingDAO dao;
    private ProviderDao providerDao;

    public PrivateBillingController() {
        super();
        dao = new PrivateBillingDAO();
        providerDao = SpringUtils.getBean(ProviderDao.class);
    }

    private void listPrivateBills(HttpServletRequest request, HttpServletResponse response, String forward) throws ServletException, IOException {
        try {
            List<Provider> providers = providerDao.getProviders();
            String providerId = request.getParameter("providerId");
            if (providerId == null || providerId.isEmpty()) {
                providerId = "%";
            }
            forward = LIST_PRIVATE_BILLS;
            request.setAttribute("providers", providers);
            request.setAttribute("providerId", providerId);
            request.setAttribute("bills", dao.listPrivateBills(providerId));

            RequestDispatcher view = request.getRequestDispatcher(forward);
            view.forward(request, response);
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void printPreviewBills(HttpServletRequest request, HttpServletResponse response, String forward) throws ServletException, IOException {
        try {
            DemographicData demoData = new DemographicData();
            List<HashMap> patientBills = new ArrayList<HashMap>();

            String[] paramValues = request.getParameterValues("billIds");
            if (paramValues.length == 1) {
                JsonArray jsonArr = new JsonParser().parse(paramValues[0]).getAsJsonArray();
                for (int i = 0; i < jsonArr.size(); i++) {
                    HashMap<String, Object> map = new HashMap<String, Object>();
                    JsonElement jsonElem = jsonArr.get(i);
                    JsonElement demographicNumber = jsonElem.getAsJsonObject().get("demographicNumber");
                    String strDemographicNumber = demographicNumber.getAsString();
                    Demographic patient = demoData.getDemographic(LoggedInInfo.getLoggedInInfoFromSession(request), strDemographicNumber);
                    map.put("patientFirstName", patient.getFirstName());
                    map.put("patientLastName", patient.getLastName());
                    map.put("patientAddress", patient.getAddress());
                    map.put("patientCity", patient.getCity());
                    map.put("patientProvince", patient.getProvince());
                    map.put("patientPostal", patient.getPostal());
                    map.put("patientMonthOfBirth", patient.getMonthOfBirth());
                    map.put("patientDateOfBirth", patient.getDateOfBirth());
                    map.put("patientYearOfBirth", patient.getYearOfBirth());

                    // get recipient info (note: a null attribute means the recipient is the patient)
                    JsonElement recipientId = jsonElem.getAsJsonObject().get("recipientId");
                    String strRecipientId = recipientId.getAsString();
                    if (strRecipientId.isEmpty() || strRecipientId == "") {
                        map.put("recipientName", patient.getFirstName() + " " + patient.getLastName());
                        map.put("recipientAddress", patient.getAddress());
                        map.put("recipientCity", patient.getCity());
                        map.put("recipientProvince", patient.getProvince());
                        map.put("recipientPostal", patient.getPostal());
                    } else {
                        HashMap<String, String> recipient = dao.getRecipientById(strRecipientId);
                        map.put("recipientName", recipient.get("name"));
                        map.put("recipientAddress", recipient.get("address"));
                        map.put("recipientCity", recipient.get("city"));
                        map.put("recipientProvince", recipient.get("province"));
                        map.put("recipientPostal", recipient.get("postal"));
                    }

                    // get current clinic info
                    ClinicData clinic = new ClinicData();
                    map.put("clinicName", clinic.getClinicName());
                    map.put("clinicAddress", clinic.getClinicAddress());
                    map.put("clinicCity", clinic.getClinicCity());
                    map.put("clinicProvince", clinic.getClinicProvince());
                    map.put("clinicPostal", clinic.getClinicPostal());
                    map.put("clinicPhone", clinic.getClinicPhone());
                    map.put("clinicFax", clinic.getClinicFax());

                    // get patient invoice items
                    String strRecipientName = (strRecipientId.isEmpty() || strRecipientId == "") ? "" : map.get("recipientName").toString();
                    List<HashMap<String, String>> invoiceItems = dao.listPrivateBillItems(strDemographicNumber, strRecipientName);
                    map.put("invoiceItems", invoiceItems);

                    // queue patient bill data
                    patientBills.add(map);
                }
            }

            String billToClinic = request.getParameter("billToClinic");
            String billIds = request.getParameter("billIds");

            forward = PRINT_PREVIEW_BILLS;
            request.setAttribute("date", new Date().toString());
            request.setAttribute("billToClinic", billToClinic);
            request.setAttribute("billIds", billIds);
            request.setAttribute("patientBills", patientBills);

            RequestDispatcher view = request.getRequestDispatcher(forward);
            view.forward(request, response);
        } catch (ServletException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, NullPointerException {
        String forward = "";
        String action = request.getParameter("action");
        try {
            if (action.equalsIgnoreCase("listPrivateBills")) {
                listPrivateBills(request, response, forward);
            } else if (action.equalsIgnoreCase("printPreviewBills")) {
                printPreviewBills(request, response, forward);
            } else {
                // missing 'billIds' parameters, go back to default action 'LIST_PRIVATE_BILLS'
                listPrivateBills(request, response, forward);
            }
        } catch (NullPointerException e) {
            // action is not provided, by default forward to LIST_PRIVATE_BILLS
            listPrivateBills(request, response, forward);
        } catch (ServletException e) {
            e.printStackTrace();
        }
    }

}