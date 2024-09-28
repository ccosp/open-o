//CHECKSTYLE:OFF
/**
 * Copyright (c) 2001-2002. Department of Family Medicine, McMaster University. All Rights Reserved.
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
 * Department of Family Medicine
 * McMaster University
 * Hamilton
 * Ontario, Canada
 */


package ca.openosp.openo.oscarEncounter.pageUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

import org.apache.struts.util.MessageResources;
import ca.openosp.openo.common.model.MessageTbl;
import ca.openosp.openo.common.model.OscarMsgType;
import ca.openosp.openo.managers.MessagingManager;
import ca.openosp.openo.ehrutil.LoggedInInfo;
import ca.openosp.openo.ehrutil.SpringUtils;
import ca.openosp.openo.util.StringUtils;

public class EctDisplayMsgAction extends EctDisplayAction {

    private static final String cmd = "msgs";
    private static MessagingManager messagingManager = SpringUtils.getBean(MessagingManager.class);

    public boolean getInfo(EctSessionBean bean, HttpServletRequest request, NavBarDisplayDAO Dao, MessageResources messages) {
        LoggedInInfo loggedInInfo = LoggedInInfo.getLoggedInInfoFromSession(request);
        if (!securityInfoManager.hasPrivilege(loggedInInfo, "_msg", "r", null)) {
            return true; //Oscar message link won't show up on new CME screen.
        } else {
            //set text for lefthand module title
            Dao.setLeftHeading(messages.getMessage(request.getLocale(), "oscarEncounter.LeftNavBar.Messages"));

            //set link for lefthand module title
            String winName = "ViewMsg" + bean.demographicNo;
            String url = "popupPage(600,900,'" + winName + "','" + request.getContextPath() + "/oscarMessenger/DisplayDemographicMessages.do?orderby=date&boxType=3&demographic_no=" + bean.demographicNo + "&providerNo=" + bean.providerNo + "&userName=" + bean.userName + "')";
            Dao.setLeftURL(url);

            //set the right hand heading link
            winName = "SendMsg" + bean.demographicNo;
            url = "popupPage(700,960,'" + winName + "','" + request.getContextPath() + "/oscarMessenger/SendDemoMessage.do?demographic_no=" + bean.demographicNo + "'); return false;";
            Dao.setRightURL(url);
            Dao.setRightHeadingID(cmd);  //no menu so set div id to unique id for this action

            // set lefthand data link
            List<MessageTbl> messageTblList = messagingManager.getMessagesLinkedToDemographic(loggedInInfo, Integer.parseInt(bean.demographicNo));
            int msgId;
            String msgSubject;
            String msgDate;
            int hash;
            int messageType;

            for (MessageTbl message : messageTblList) {
                msgId = message.getId();
                msgSubject = message.getSubject();
                messageType = message.getType();
                Date date = message.getDate();
                NavBarDisplayDAO.Item item = NavBarDisplayDAO.Item();
                item.setDate(date);
                hash = winName.hashCode();
                hash = hash < 0 ? hash * -1 : hash;

                msgDate = new SimpleDateFormat("dd-M-yyyy", request.getLocale()).format(date);

                url = "popupPage(600,900,'" + hash + "','" + request.getContextPath() + "/oscarMessenger/ViewMessage.do?from=encounter&boxType=0&messageID=" + msgId + "'); return false;";
                item.setURL(url);
                item.setTitle(StringUtils.maxLenString(msgSubject, MAX_LEN_TITLE, CROP_LEN_TITLE, ELLIPSES));
                item.setLinkTitle(msgSubject + " " + msgDate);
                if (messageType == OscarMsgType.INTEGRATOR_TYPE) {
                    item.setBgColour("#FFCCCC");
                }
                Dao.addItem(item);
            }

            return true;
        }
    }

    public String getCmd() {
        return cmd;
    }
}
