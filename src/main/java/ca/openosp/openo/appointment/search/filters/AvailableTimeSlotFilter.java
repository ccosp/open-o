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
package ca.openosp.openo.appointment.search.filters;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import ca.openosp.openo.appointment.search.SearchConfig;
import ca.openosp.openo.appointment.search.TimeSlot;
import ca.openosp.openo.managers.DayWorkSchedule;


public interface AvailableTimeSlotFilter {
    /**
     * @return a list of available time slots, i.e. make a new array and copy the qualifying time slots into it. Probably best not to alter the passed in list contents.
     * @
     */
    public List<TimeSlot> filterAvailableTimeSlots(SearchConfig clinic, String mrp, String providerId, Long appointmentTypeId, DayWorkSchedule dayWorkScheduleTransfer, List<TimeSlot> currentlyAllowedTimeSlots, Calendar date, Map<String, String> params);
}
