package org.oscarehr.email.helpers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.oscarehr.common.model.EmailAttachment;
import org.oscarehr.common.model.EmailConfig;
import org.oscarehr.managers.SecurityInfoManager;
import org.oscarehr.util.EmailSendingException;
import org.oscarehr.util.LoggedInInfo;
import org.oscarehr.util.SpringUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class APISendGridEmailSender {
    private LoggedInInfo loggedInInfo;
    private SecurityInfoManager securityInfoManager = SpringUtils.getBean(SecurityInfoManager.class);

    private EmailConfig emailConfig;
    private String[] recipients = new String[0];
    private String subject;
    private String body;
    private List<EmailAttachment> attachments;
    private String endpoint;

    private APISendGridEmailSender() { }

    public APISendGridEmailSender(LoggedInInfo loggedInInfo, EmailConfig emailConfig, String[] recipients, String subject, String body, List<EmailAttachment> attachments) {
        this.loggedInInfo = loggedInInfo;
        this.emailConfig = emailConfig;
        this.recipients = recipients;
        this.subject = subject;
        this.body = body;
        this.attachments = attachments;
        this.endpoint = "https://api.sendgrid.com/v3/mail/send";
    }

    public void send() throws EmailSendingException {
        if (!securityInfoManager.hasPrivilege(loggedInInfo, "_email", SecurityInfoManager.WRITE, null)) {
			throw new RuntimeException("missing required security object (_email)");
		}

        HttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(endpoint);
        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setHeader("Authorization", "Bearer " + getAPIKey());
        try {
            StringEntity entity = new StringEntity(createEmailJSON());
            httpPost.setEntity(entity);
            HttpResponse response = httpClient.execute(httpPost);
            if (response.getStatusLine().getStatusCode() >= 400) { throw new EmailSendingException(response.getStatusLine() + "\n" + EntityUtils.toString(response.getEntity())); }
        } catch (Exception e) {
            throw new EmailSendingException(e.getMessage(), e);
        }
    }

    private String createEmailJSON() throws EmailSendingException {
        JSONObject emailJson = new JSONObject();
        addTo(emailJson);
        addFrom(emailJson);
        addSubject(emailJson);
        addBody(emailJson);
        addAttachments(emailJson);
        return emailJson.toString();
    }

    private void addTo(JSONObject emailJson) {
        JSONArray personalizations = new JSONArray();
        JSONObject personalization = new JSONObject();

        JSONArray toList = new JSONArray();
        for (String recipient : recipients) {
            JSONObject to = new JSONObject();
            to.put("email", recipient);
            toList.add(to);
        }

        personalization.put("to", toList);
        personalizations.add(personalization);

        emailJson.put("personalizations", personalizations);
    }

    private void addFrom(JSONObject emailJson) {
        JSONObject from = new JSONObject();
        from.put("email", emailConfig.getSenderEmail());
        from.put("name", emailConfig.getSenderFullName());
        emailJson.put("from", from);
    }

    private void addSubject(JSONObject emailJson) {
        emailJson.put("subject", subject);
    }

    private void addBody(JSONObject emailJson) {
        JSONArray content = new JSONArray();
        JSONObject contentObj = new JSONObject();
        contentObj.put("type", "text/plain");
        contentObj.put("value", body);
        content.add(contentObj);
        emailJson.put("content", content);
    }

    private void addAttachments(JSONObject emailJson) throws EmailSendingException {
        JSONArray jsonAttachments = new JSONArray();
        for (EmailAttachment emailAttachment : attachments) {
            try {
                JSONObject jsonAttachment = new JSONObject();
                Path path = Paths.get(emailAttachment.getFilePath());
                jsonAttachment.put("content", Base64.encodeBase64String(Files.readAllBytes(path)));
                jsonAttachment.put("filename", emailAttachment.getFileName());
                jsonAttachment.put("type", "application/pdf");
                jsonAttachment.put("disposition", "attachment");
                jsonAttachments.add(jsonAttachment);
            } catch (Exception e) {
                throw new EmailSendingException("Failed to attach " + emailAttachment.getFileName() + " while sending email using SendGrid.");
            }
        }
        emailJson.put("attachments", jsonAttachments);
    }

    private String getAPIKey() throws EmailSendingException {
        String apiKey;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(emailConfig.getConfigDetailsJson());
            apiKey = jsonNode.get("api_key").asText();
        } catch (IOException e) {
            throw new EmailSendingException("Invalid credentials configured for " + emailConfig.getSenderEmail());
        }
        return apiKey;
    }
}
