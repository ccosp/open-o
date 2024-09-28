//CHECKSTYLE:OFF
/**
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
 */

package ca.openosp.openo.PMmodule.model;

import java.io.Serializable;

/**
 * This is the object class that relates to the functional_user_type table. Any customizations belong here.
 */
public class FunctionalUserType implements Serializable {

    private int hashCode = Integer.MIN_VALUE;// primary key
    private Long _id;// fields
    private String _name;

    // constructors
    public FunctionalUserType() {
        initialize();
    }

    /**
     * Constructor for primary key
     */
    public FunctionalUserType(Long _id) {
        this.setId(_id);
        initialize();
    }


    /* [CONSTRUCTOR MARKER END] */
    protected void initialize() {
        //no code in here right now
    }

    /**
     * Return the unique identifier of this class
     * <p>
     * generator-class="native"
     * column="id"
     */
    public Long getId() {
        return _id;
    }

    /**
     * Set the unique identifier of this class
     *
     * @param _id the new ID
     */
    public void setId(Long _id) {
        this._id = _id;
        this.hashCode = Integer.MIN_VALUE;
    }

    /**
     * Return the value associated with the column: name
     */
    public String getName() {
        return _name;
    }

    /**
     * Set the value related to the column: name
     *
     * @param _name the name value
     */
    public void setName(String _name) {
        this._name = _name;
    }

    public boolean equals(Object obj) {
        if (null == obj) return false;
        if (!(obj instanceof FunctionalUserType)) return false;
        else {
            FunctionalUserType mObj = (FunctionalUserType) obj;
            if (null == this.getId() || null == mObj.getId()) return false;
            else return (this.getId().equals(mObj.getId()));
        }
    }

    public int hashCode() {
        if (Integer.MIN_VALUE == this.hashCode) {
            if (null == this.getId()) return super.hashCode();
            else {
                String hashStr = this.getClass().getName() + ":" + this.getId().hashCode();
                this.hashCode = hashStr.hashCode();
            }
        }
        return this.hashCode;
    }

    public String toString() {
        return super.toString();
    }
}
