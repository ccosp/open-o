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

package ca.openosp.openo.PMmodule.web.adapter.ocan;

import ca.openosp.openo.PMmodule.web.adapter.IntakeNodeHtmlAdapter;
import ca.openosp.openo.PMmodule.model.IntakeNode;
import ca.openosp.openo.PMmodule.web.adapter.AbstractHtmlAdapter;

public class PageOcanStaffXmlAdapter extends AbstractHtmlAdapter {

    public PageOcanStaffXmlAdapter(int indent, IntakeNode node) {
        super(indent, node);
    }

    /**
     * @see IntakeNodeHtmlAdapter#getPreBuilder()
     */
    public StringBuilder getPreBuilder() {
        StringBuilder preBuilder = super.getPreBuilder();

        preBuilder.
                append("<").
                append(getLabelOcanXML()).
                append(">").
                append(EOL);

/*
		indent(preBuilder).append("<div dojoType=\"ContentPane\" label=\"").append(getLabel()).append("\" class=\"intakePage\" >").append(EOL);
		beginTag();
		
		if (!hasSections()) {
			indent(preBuilder).append("<table class=\"intakeTable\">").append(EOL);
			beginTag();
		}
*/
        return preBuilder;
    }

    /**
     * @see IntakeNodeHtmlAdapter#getPostBuilder()
     */
    public StringBuilder getPostBuilder() {
        StringBuilder postBuilder = super.getPostBuilder();

        postBuilder.
                append("</").
                append(getLabelOcanXML()).
                append(">").
                append(EOL);

/*
		if (!hasSections()) {
			endTag();
			indent(postBuilder).append("</table> <!-- End Question Table -->").append(EOL);
		}

		endTag();
		indent(postBuilder).append("</div> <!-- End Page -->").append(EOL);
*/
        return postBuilder;
    }

}
