/**
 * Copyright (c) 2001-2002. Department of Family Medicine, McMaster University. All Rights Reserved.
 * This software is published under the GPL GNU General Public License.
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version. 
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 *
 * This software was written for the
 * Department of Family Medicine
 * McMaster University
 * Hamilton
 * Ontario, Canada
 */
package org.oscarehr.common.model;

import javax.persistence.*;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Entity
@Table(name="SystemPreferences")
public class SystemPreferences extends AbstractModel<Integer>
{

    public static final List<String> RX_PREFERENCE_KEYS = Arrays.asList("rx_paste_provider_to_echart", "rx_show_end_dates","rx_show_start_dates", "rx_show_refill_duration", "rx_show_refill_quantity", 
            "rx_methadone_end_date_calc", "save_rx_signature");
    public static final List<String> SCHEDULE_PREFERENCE_KEYS = Arrays.asList("schedule_display_type", "schedule_display_custom_roster_status");
    public static final List<String> ECHART_PREFERENCE_KEYS = Arrays.asList("echart_hide_timer");
    public static final List<String> MASTER_FILE_PREFERENCE_KEYS = Arrays.asList("display_former_name", "redirect_for_contact");
    public static final List<String> GENERAL_SETTINGS_KEYS = Arrays.asList("replace_demographic_name_with_preferred", "msg_use_create_date");
    public static final String LAB_DISPLAY_PREFERENCE_KEYS = "code_show_hide_column";
    public static final List<String> EFORM_SETTINGS = Arrays.asList("rtl_template_document_type", "patient_intake_eform", "patient_intake_letter_eform");
    public static final List<String> REFERRAL_SOURCE_PREFERENCE_KEYS = Arrays.asList("enable_referral_source");
    public static final String KIOSK_DISPLAY_PREFERENCE_KEYS = "check_in_all_appointments";
    public static final List<String> DOCUMENT_SETTINGS_KEYS = Arrays.asList("document_description_typeahead", "inbox_use_fax_dropdown");
    public static final List<String> RTL_TEMPLATE_SETTINGS = Arrays.asList("rtl_template_document_type");
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name="name")
    private String name;

    @Column(name="value")
    private String value;

    @Column(name="updateDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateDate;

    public SystemPreferences() {}
    public SystemPreferences(String name, String value) {
        this.name = name;
        this.value = value;
        this.updateDate = new Date();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value != null ? value : "";
    }

    /**
     * Gets the system preference as a boolean
     * @return true if value is "true", false otherwise
     */
    public Boolean getValueAsBoolean() {
        return "true".equals(value);
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }
}
