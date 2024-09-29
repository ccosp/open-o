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

package ca.openosp.openo.oscarLab;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import ca.openosp.openo.common.dao.FileUploadCheckDao;
import ca.openosp.openo.ehrutil.MiscUtils;
import ca.openosp.openo.ehrutil.SpringUtils;

import ca.openosp.openo.util.ConversionUtils;

/**
 * @author Jay Gallagher
 */
public final class FileUploadCheck {

    private FileUploadCheck() {
        // no instantiation allowed
    }

    private static boolean hasFileBeenUploaded(String md5sum) {
        FileUploadCheckDao dao = SpringUtils.getBean(FileUploadCheckDao.class);
        List<ca.openosp.openo.common.model.FileUploadCheck> checks = dao.findByMd5Sum(md5sum);
        return !checks.isEmpty();
    }

    public static boolean hasFileBeenUploadedByFileLocation(String fileLocation) throws IOException {
        InputStream is = null;

        try {
            is = new FileInputStream(fileLocation);
            String md5sum = DigestUtils.md5Hex(IOUtils.toByteArray(is));
            return hasFileBeenUploaded(md5sum);
        } finally {
            IOUtils.closeQuietly(is);
        }

    }

    public static Map<String, String> getFileInfo(Integer id) {
        Map<String, String> fileInfo = new HashMap<String, String>();
        FileUploadCheckDao dao = SpringUtils.getBean(FileUploadCheckDao.class);
        ca.openosp.openo.common.model.FileUploadCheck c = dao.find(id);
        if (c != null) {
            toMap(fileInfo, c);
        }
        return fileInfo;
    }

    private static void toMap(Map<String, String> fileInfo, ca.openosp.openo.common.model.FileUploadCheck c) {
        fileInfo.put("providerNo", c.getProviderNo());
        fileInfo.put("filename", c.getFilename());
        fileInfo.put("md5sum", c.getMd5sum());
        fileInfo.put("dateTime", ConversionUtils.toTimestampString(c.getDateTime()));
    }

    public static Hashtable<String, String> getFileInfo(String md5sum) {
        Hashtable<String, String> fileInfo = new Hashtable<String, String>();
        FileUploadCheckDao dao = SpringUtils.getBean(FileUploadCheckDao.class);
        List<ca.openosp.openo.common.model.FileUploadCheck> checks = dao.findByMd5Sum(md5sum);

        if (!checks.isEmpty()) {
            ca.openosp.openo.common.model.FileUploadCheck c = checks.get(0);
            toMap(fileInfo, c);
        }

        return fileInfo;
    }

    public static final int UNSUCCESSFUL_SAVE = -1;

    /**
     * Used to add a new file to the database, checks to see if it already has been added
     */
    public static synchronized int addFile(String name, InputStream is, String provider) {
        int fileUploaded = UNSUCCESSFUL_SAVE;
        try {
            String md5sum = DigestUtils.md5Hex(IOUtils.toByteArray(is));
            if (!hasFileBeenUploaded(md5sum)) {

                ca.openosp.openo.common.model.FileUploadCheck f = new ca.openosp.openo.common.model.FileUploadCheck();
                f.setProviderNo(provider);
                f.setFilename(name);
                f.setMd5sum(md5sum);
                f.setDateTime(new Date());

                FileUploadCheckDao dao = SpringUtils.getBean(FileUploadCheckDao.class);
                dao.persist(f);

                fileUploaded = f.getId();
            }
        } catch (Exception e) {
            MiscUtils.getLogger().error("Error", e);
        }
        MiscUtils.getLogger().debug("returning " + fileUploaded);
        return fileUploaded;
    }

}
