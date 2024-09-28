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
package org.oscarehr.labs.alberta;

import static org.junit.Assert.assertTrue;

import java.security.PrivateKey;
import java.security.PublicKey;

import ca.openosp.openo.ehrutil.MiscUtils;
import org.apache.logging.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;
import ca.openosp.openo.common.dao.DaoTestFixtures;
import ca.openosp.openo.common.dao.utils.SchemaUtils;
import ca.openosp.openo.common.hl7.v2.oscar_to_oscar.SendingUtils;

public class UploadingTest extends DaoTestFixtures {

    private static final boolean ENABLED = false;

    /**
     * Public OSCAR key
     */
    public static final String OSCAR_KEY = "";

    /**
     * Public client key
     */
    public static final String CLIENT_KEY = "";

    private static Logger logger = MiscUtils.getLogger();

    @BeforeClass
    public static void init() throws Exception {
        SchemaUtils.restoreAllTables();
        logger.info("Initialized successfully");
    }

    @Test
    public void test() throws Exception {
        if (!ENABLED) {
            return;
        }

        String url = "http://localhost:8080/oscar/lab/newLabUpload.do";
        String publicOscarKeyString = OSCAR_KEY;
        String publicServiceKeyString = CLIENT_KEY;

        PublicKey publicOscarKey = SendingUtils.getPublicOscarKey(publicOscarKeyString);
        PrivateKey publicServiceKey = SendingUtils.getPublicServiceKey(publicServiceKeyString);

        boolean isSuccess = true;
        for (int i = 0; i < TestLabs.ALL_LABS.length; i++) {
            String messageText = TestLabs.ALL_LABS[i];
            byte[] bytes = messageText.getBytes();
            int statusCode = SendingUtils.send(null, bytes, url, publicOscarKey, publicServiceKey, "CLS");
            logger.info("Completed upload for " + TestLabs.LAB_NAMES[i] + " with status " + statusCode);
            isSuccess &= statusCode == 200;

            if (i == 0)
                break;
        }
        assertTrue(isSuccess);
    }

}
