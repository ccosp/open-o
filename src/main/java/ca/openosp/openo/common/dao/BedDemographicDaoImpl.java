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

import java.util.Calendar;
import java.util.List;

import javax.persistence.Query;

import org.apache.commons.lang.time.DateUtils;
import org.apache.logging.log4j.Logger;
import ca.openosp.openo.common.model.BedDemographic;
import ca.openosp.openo.common.model.BedDemographicHistorical;
import ca.openosp.openo.common.model.BedDemographicPK;
import ca.openosp.openo.ehrutil.MiscUtils;
import org.springframework.stereotype.Repository;

@Repository
public class BedDemographicDaoImpl extends AbstractDaoImpl<BedDemographic> implements BedDemographicDao {

    private Logger log = MiscUtils.getLogger();

    public BedDemographicDaoImpl() {
        super(BedDemographic.class);
    }

    @Override
    public boolean demographicExists(int bedId) {
        Query query = entityManager.createQuery("select count(*) from BedDemographic b where b.id.bedId = ?1");
        query.setParameter(1, bedId);

        Long result = (Long) query.getSingleResult();

        return (result.intValue() == 1);
    }

    @Override
    public boolean bedExists(int demographicNo) {
        Query query = entityManager.createQuery("select count(*) from BedDemographic b where b.id.demographicNo = ?");
        query.setParameter(0, demographicNo);

        Long result = (Long) query.getSingleResult();

        return (result.intValue() == 1);
    }

    @Override
    public BedDemographic getBedDemographicByBed(int bedId) {
        Query query = entityManager.createQuery("select b from BedDemographic b where b.id.bedId = ?");
        query.setParameter(0, bedId);

        @SuppressWarnings("unchecked")
        List<BedDemographic> bedDemographics = query.getResultList();

        if (bedDemographics.size() > 1) {
            throw new IllegalStateException("Bed is assigned to more than one client");
        }

        BedDemographic bedDemographic = ((bedDemographics.size() == 1) ? bedDemographics.get(0) : null);

        log.debug("getBedDemographicByBed: " + bedId);

        return bedDemographic;
    }

    @Override
    public BedDemographic getBedDemographicByDemographic(int demographicNo) {
        Query query = entityManager.createQuery("select b from BedDemographic b where b.id.demographicNo = ?");
        query.setParameter(0, demographicNo);

        @SuppressWarnings("unchecked")
        List<BedDemographic> bedDemographics = query.getResultList();

        if (bedDemographics.size() > 1) {
            throw new IllegalStateException("Client is assigned to more than one bed");
        }

        BedDemographic bedDemographic = ((bedDemographics.size() == 1) ? bedDemographics.get(0) : null);

        log.debug("getBedDemographicByDemographic: " + demographicNo);

        return bedDemographic;
    }

    @Override
    public void saveBedDemographic(BedDemographic bedDemographic) {
        if (bedDemographic == null)
            return;

        updateHistory(bedDemographic);

        if (bedDemographic.getId().getBedId() == null || bedDemographic.getId().getBedId().intValue() == 0 ||
                bedDemographic.getId().getDemographicNo() == null
                || bedDemographic.getId().getDemographicNo().intValue() == 0)
            persist(bedDemographic);
        else
            merge(bedDemographic);

        log.debug("saveBedDemographic: " + bedDemographic);
    }

    @Override
    public void deleteBedDemographic(BedDemographic bedDemographic) {
        // save historical
        if (!DateUtils.isSameDay(bedDemographic.getReservationStart(), Calendar.getInstance().getTime())) {
            BedDemographicHistorical historical = BedDemographicHistorical.create(bedDemographic);
            persist(historical);
        }

        // delete
        remove(bedDemographic.getId());

    }

    @Override
    public boolean bedDemographicExists(BedDemographicPK id) {
        Query query = entityManager
                .createQuery("select count(*) from BedDemographic b where  b.id.bedId = ? and b.id.demographicNo = ?");
        query.setParameter(0, id.getBedId());
        query.setParameter(1, id.getDemographicNo());

        Long result = (Long) query.getSingleResult();

        return (result.intValue() == 1);
    }

    @Override
    public void updateHistory(BedDemographic bedDemographic) {
        BedDemographic existing = null;

        BedDemographicPK id = bedDemographic.getId();

        if (!bedDemographicExists(id)) {
            Integer demographicNo = id.getDemographicNo();
            Integer bedId = id.getBedId();

            if (bedExists(demographicNo)) {
                existing = getBedDemographicByDemographic(demographicNo);
            } else if (demographicExists(bedId)) {
                existing = getBedDemographicByBed(bedId);
            }
        }

        if (existing != null) {
            deleteBedDemographic(existing);
        }
    }
}
