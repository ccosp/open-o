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

package ca.openosp.openo.PMmodule.common;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import ca.openosp.openo.PMmodule.model.Intake;
import ca.openosp.openo.PMmodule.model.IntakeNode;
import ca.openosp.openo.PMmodule.web.adapter.HtmlAdapterFactory;
import ca.openosp.openo.PMmodule.web.adapter.IntakeNodeHtmlAdapter;

public class IntakeTag extends TagSupport {

    private int base;
    private Intake intake;
    private HtmlAdapterFactory adapterFactory;

    public IntakeTag() {
        adapterFactory = new HtmlAdapterFactory();
    }

    public void setBase(int base) {
        this.base = base;
    }

    public void setIntake(Intake intake) {
        this.intake = intake;
    }


    public void doTag() throws JspException, IOException {
        JspWriter out = super.pageContext.getOut();
        out.write(toHtml());
    }

    String toHtml() {
        StringBuilder html = new StringBuilder();
        toHtml(html, base, intake.getNode());

        return html.toString();
    }

    void toHtml(StringBuilder builder, int indent, IntakeNode node) {
        if (node == null)
            return;
        IntakeNodeHtmlAdapter htmlAdapter = adapterFactory.getHtmlAdapter(indent, node, intake);

        builder.append(htmlAdapter.getPreBuilder());

        for (IntakeNode child : node.getChildren()) {
            toHtml(builder, htmlAdapter.getIndent(), child);
        }

        builder.append(htmlAdapter.getPostBuilder());
    }

}
