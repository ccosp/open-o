package org.oscarehr.casemgmt.service;

import org.oscarehr.casemgmt.dao.ClientImageDAO;
import org.oscarehr.casemgmt.model.ClientImage;
import org.springframework.transaction.annotation.Transactional;

public interface ClientImageManager {
    void setClientImageDAO(ClientImageDAO dao);
    void saveClientImage(String id, byte[] image_data, String image_type);
    ClientImage getClientImage(Integer clientId);
    void saveClientImage(ClientImage img);
}
