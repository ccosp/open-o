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
package ca.openosp.openo.login.jaas;

import java.security.Principal;

import ca.openosp.openo.common.model.Provider;

/**
 * Adapter class for bridging OSCAR provider to the JAAS principal.
 */
public class OscarPrincipal extends Provider implements Principal {

    private String name;

    public OscarPrincipal() {
        super();
    }

    public OscarPrincipal(Provider provider) {
        super(provider);
    }

    public OscarPrincipal(String providerNo, String lastName, String providerType, String sex, String specialty, String firstName) {
        super(providerNo, lastName, providerType, sex, specialty, firstName);
    }

    public OscarPrincipal(String providerNo) {
        super(providerNo);
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Creates a new instance deep copy of this instance.
     *
     * @return Returns a new copy of the provider with all matching fields populated.
     */
    public Provider asProvider() {
        return new Provider(this);
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = prime + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!super.equals(obj)) return false;
        if (getClass() != obj.getClass()) return false;
        OscarPrincipal other = (OscarPrincipal) obj;
        if (name == null) {
            if (other.name != null) return false;
        } else if (!name.equals(other.name)) return false;
        return true;
    }


}
