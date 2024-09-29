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
package ca.openosp.openo.ws.transfer_objects;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import ca.openosp.openo.common.model.Property;
import org.springframework.beans.BeanUtils;

public final class ProviderPropertyTransfer {

    private Integer id;
    private String name;
    private String value;

    public Integer getId() {
        return (id);
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return (name);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return (value);
    }

    public void setValue(String value) {
        this.value = value;
    }

    public static ProviderPropertyTransfer toTransfer(Property property) {
        if (property == null) return (null);

        ProviderPropertyTransfer providerPropertyTransfer = new ProviderPropertyTransfer();

        BeanUtils.copyProperties(property, providerPropertyTransfer);

        return (providerPropertyTransfer);
    }

    public static ProviderPropertyTransfer[] toTransfers(List<Property> properties) {
        ArrayList<ProviderPropertyTransfer> results = new ArrayList<ProviderPropertyTransfer>();

        for (Property property : properties) {
            results.add(toTransfer(property));
        }

        return (results.toArray(new ProviderPropertyTransfer[0]));
    }

    @Override
    public String toString() {
        return (ReflectionToStringBuilder.toString(this));
    }
}
