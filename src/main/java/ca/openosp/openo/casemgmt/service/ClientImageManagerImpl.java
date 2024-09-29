//CHECKSTYLE:OFF
/**
 * Copyright (c) 2024. Magenta Health. All Rights Reserved.
 * <p>
 * Copyright (c) 2005-2012. Centre for Research on Inner City Health, St. Michael's Hospital, Toronto. All Rights Reserved.
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
 * This software was written for
 * Centre for Research on Inner City Health, St. Michael's Hospital,
 * Toronto, Ontario, Canada
 * <p>
 * Modifications made by Magenta Health in 2024.
 */
package ca.openosp.openo.casemgmt.service;

import ca.openosp.openo.casemgmt.dao.ClientImageDAO;
import ca.openosp.openo.casemgmt.model.ClientImage;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class ClientImageManagerImpl implements ClientImageManager {
    protected ClientImageDAO clientImageDAO;

    @Override
    public void setClientImageDAO(ClientImageDAO dao) {
        this.clientImageDAO = dao;
    }

    @Override
    public void saveClientImage(String id, byte[] image_data, String image_type) {
        ClientImage clientImage = new ClientImage();
        clientImage.setDemographic_no(Integer.parseInt(id));
        clientImage.setImage_data(image_data);
        clientImage.setImage_type(image_type);
        clientImageDAO.saveClientImage(clientImage);
    }

    @Override
    public ClientImage getClientImage(Integer clientId) {
        return clientImageDAO.getClientImage(clientId);
    }

    @Override
    public void saveClientImage(ClientImage img) {
        clientImageDAO.saveClientImage(img);
    }
}
