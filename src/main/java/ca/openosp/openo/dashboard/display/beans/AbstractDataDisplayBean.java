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
package ca.openosp.openo.dashboard.display.beans;

import java.util.ArrayList;
import java.util.List;

import ca.openosp.openo.dashboard.query.DrillDownAction;
import ca.openosp.openo.dashboard.query.Parameter;
import ca.openosp.openo.dashboard.query.RangeInterface;

public abstract class AbstractDataDisplayBean {

    private Integer id;
    private Integer dashboardId;
    private String queryVersion;
    private String name;
    private String category;
    private String subCategory;
    private String metricSetName;
    private String metricLabel;
    private String framework;
    private String frameworkVersion;
    private String definition;
    private String notes;
    private boolean active;
    private boolean locked;
    private String xmlTemplate;
    private String dxUpdateICD9Code = null;

    private List<Parameter> parameters;
    private List<DrillDownAction> actions;
    private List<String> actionIds;
    private List<RangeInterface> ranges;
    private String rangeString;
    private String queryString;
    private List<?> queryResult;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getDashboardId() {
        return dashboardId;
    }

    public void setDashboardId(Integer dashboardId) {
        this.dashboardId = dashboardId;
    }

    public String getQueryVersion() {
        return queryVersion;
    }

    public void setQueryVersion(String queryVersion) {
        this.queryVersion = queryVersion;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(String subCategory) {
        this.subCategory = subCategory;
    }

    public String getMetricSetName() {
        return metricSetName;
    }

    public void setMetricSetName(String metricSetName) {
        this.metricSetName = metricSetName;
    }

    public String getMetricLabel() {
        return metricLabel;
    }

    public void setMetricLabel(String metricLabel) {
        this.metricLabel = metricLabel;
    }

    public String getFramework() {
        return framework;
    }

    public void setFramework(String framework) {
        this.framework = framework;
    }

    public String getFrameworkVersion() {
        return frameworkVersion;
    }

    public void setFrameworkVersion(String frameworkVersion) {
        this.frameworkVersion = frameworkVersion;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public String getXmlTemplate() {
        return xmlTemplate;
    }

    public void setXmlTemplate(String xmlTemplate) {
        this.xmlTemplate = xmlTemplate;
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<Parameter> parameters) {
        this.parameters = parameters;
    }

    public List<DrillDownAction> getActions() {
        return actions;
    }

    public void setActions(List<DrillDownAction> actions) {
        this.actions = actions;
        this.actionIds = new ArrayList<String>();

        if (actions != null && !actions.isEmpty()) {
            for (DrillDownAction action : actions) {
                actionIds.add(action.getId());
                if ("dxUpdate".equals(action.getId())) {
                    setDxUpdateICD9Code(action.getValue());
                }
            }
        }
    }

    public List<String> getActionIds() {
        return actionIds;
    }

    public void setDxUpdateICD9Code(String dxUpdateICD9Code) {
        this.dxUpdateICD9Code = dxUpdateICD9Code;
    }

    public String getDxUpdateICD9Code() {
        return dxUpdateICD9Code;
    }

    public String getRangeString() {
        return rangeString;
    }

    public void setRangeString(String rangeString) {
        this.rangeString = rangeString;
    }

    public String getQueryString() {
        return queryString;
    }

    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }

    public List<?> getQueryResult() {
        return queryResult;
    }

    public void setQueryResult(List<?> queryResult) {
        this.queryResult = queryResult;
    }

    public List<RangeInterface> getRanges() {
        return ranges;
    }

    public void setRanges(List<RangeInterface> ranges) {
        this.ranges = ranges;
    }

}
