//CHECKSTYLE:OFF
/**
 * Copyright (c) 2024. Magenta Health. All Rights Reserved.
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
 * <p>
 * Modifications made by Magenta Health in 2024.
 */
package ca.openosp.openo.common.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.apache.logging.log4j.Logger;
import ca.openosp.openo.PMmodule.utility.DateTimeFormatUtils;
import ca.openosp.openo.common.model.BedDemographicHistorical;
import ca.openosp.openo.ehrutil.MiscUtils;
import org.springframework.stereotype.Repository;

@Repository
public class BedDemographicHistoricalDaoImpl extends AbstractDaoImpl<BedDemographicHistorical>
        implements BedDemographicHistoricalDao {

    private Logger log = MiscUtils.getLogger();

    public BedDemographicHistoricalDaoImpl() {
        super(BedDemographicHistorical.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public BedDemographicHistorical[] getBedDemographicHistoricals(Date since) {
        Query query = entityManager.createQuery("select bdh from BedDemographicHistorical bdh where bdh.usageEnd >= ?");
        query.setParameter(0, DateTimeFormatUtils.getDateFromDate(since));

        List<BedDemographicHistorical> bedDemographicHistoricals = query.getResultList();

        log.debug("getBedDemographicHistoricals: size: " + bedDemographicHistoricals.size());

        return bedDemographicHistoricals.toArray(new BedDemographicHistorical[bedDemographicHistoricals.size()]);
    }
}
