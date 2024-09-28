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
package ca.openosp.openo.dashboard.handler;


import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.logging.log4j.Logger;
import ca.openosp.openo.dashboard.query.Column;
import ca.openosp.openo.dashboard.query.Parameter;
import ca.openosp.openo.dashboard.query.RangeInterface;
import ca.openosp.openo.dashboard.query.RangeLowerLimit;
import ca.openosp.openo.dashboard.query.RangeUpperLimit;
import ca.openosp.openo.dashboard.query.DrillDownAction;
import ca.openosp.openo.ehrutil.LoggedInInfo;
import ca.openosp.openo.ehrutil.MiscUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class IndicatorTemplateXML {

    private static Logger logger = MiscUtils.getLogger();

    private enum Root {heading, author, indicatorQuery, drillDownQuery, shared}

    private enum Heading {category, subCategory, framework, frameworkVersion, name, definition, notes, metricSetName, metricLabel}

    private enum Indicator {version, params, parameter, range, query}

    private enum Drilldown {version, params, parameter, range, displayColumns, column, exportColumns, drillDownActions, action, query}

    private enum ParameterAttribute {id, name, value}

    private enum ColumnAttribute {id, name, title, primary}

    private enum RangeAttribute {id, label, name, value}

    private enum ActionAttribute {id, name, value}

    private static enum ManditoryParameter {provider}

    private static enum ProviderValueAlias {all, loggedinprovider}

    private static enum ExcludedPatientAlias {none, excludedpatient}

    public static enum RangeType {upperLimit, lowerLimit}

    private Integer id;
    private Document xmlDocument;
    private Node headingNode;
    private Node indicatorQueryNode;
    private Node drillDownQueryNode;
    private String template;
    private String author;
    private String shared;
    private LoggedInInfo loggedInInfo;

    private String providerNo = null;
    private String sharedMetricLabel = null;

    private ExcludeDemographicHandler excludeDemographicHandler = new ExcludeDemographicHandler();

    public IndicatorTemplateXML(LoggedInInfo loggedInInfo, Document xmlDocument) {
        this(xmlDocument);
        this.loggedInInfo = loggedInInfo;
    }

    public IndicatorTemplateXML(Document xmlDocument) {
        xmlDocument.getDocumentElement().normalize();
        setXmlDocument(xmlDocument);
        setRootChildren();
    }

    /**
     * This is set when this XML parser is created from a existing IndicatorTemplate entity
     * for tracking. It is set with the IndicatorTemplate.id field.
     * Otherwise if this parser is being used to create a new IndicatorTemplate
     * this property will be null.
     */
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    private void setRootChildren() {
        // Author element is optional
        NodeList authorNode = getXmlDocument().getElementsByTagName(Root.author.name());
        String author = "";
        if (authorNode != null && authorNode.getLength() > 0) {
            author = authorNode.item(0).getTextContent();
        }
        setAuthor(author);

        //Shared element is optional
        NodeList sharedNode = getXmlDocument().getElementsByTagName(Root.shared.name());
        String shared = "";
        if (sharedNode != null && sharedNode.getLength() > 0) {
            shared = sharedNode.item(0).getTextContent();
        }
        setShared(shared);

        // Required nodes - instantiation will fail if these nodes are missing.
        setHeadingNode(getXmlDocument().getElementsByTagName(Root.heading.name()).item(0));
        setIndicatorQueryNode(getXmlDocument().getElementsByTagName(Root.indicatorQuery.name()).item(0));
        setDrillDownQueryNode(getXmlDocument().getElementsByTagName(Root.drillDownQuery.name()).item(0));
    }

    private Element getHeadingNode() {
        if (headingNode.getNodeType() == Node.ELEMENT_NODE) {
            return (Element) headingNode;
        }
        return null;
    }

    private void setHeadingNode(Node headingNode) {
        this.headingNode = headingNode;
    }

    private Element getIndicatorQueryNode() {
        if (indicatorQueryNode.getNodeType() == Node.ELEMENT_NODE) {
            return (Element) indicatorQueryNode;
        }
        return null;
    }

    private void setIndicatorQueryNode(Node indicatorQueryNode) {
        this.indicatorQueryNode = indicatorQueryNode;
    }

    private Element getDrillDownQueryNode() {
        if (drillDownQueryNode.getNodeType() == Node.ELEMENT_NODE) {
            return (Element) drillDownQueryNode;
        }
        return null;
    }

    private void setDrillDownQueryNode(Node drillDownQueryNode) {
        this.drillDownQueryNode = drillDownQueryNode;
    }

    public Document getXmlDocument() {
        return xmlDocument;
    }

    public void setXmlDocument(Document xmlDocument) {
        this.xmlDocument = xmlDocument;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }


    public String getShared() {
        return shared;
    }

    public void setShared(String shared) {
        this.shared = shared;
    }

    // Heading elements


    /**
     * Required Element - runtime error will be thrown if the node or element is missing
     */
    public String getCategory() {
        return getHeadingNode().getElementsByTagName(Heading.category.name()).item(0).getTextContent();
    }

    /**
     * Required Element - runtime error will be thrown if the node or element is missing
     */
    public String getSubCategory() {
        return getHeadingNode().getElementsByTagName(Heading.subCategory.name()).item(0).getTextContent();
    }

    public String getMetricSetName() {
        NodeList nl = getHeadingNode().getElementsByTagName(Heading.metricSetName.name());
        if (nl != null && nl.getLength() > 0) {
            return nl.item(0).getTextContent();
        }
        return null;
    }

    public String getMetricLabel() {
        NodeList nl = getHeadingNode().getElementsByTagName(Heading.metricLabel.name());
        if (nl != null && nl.getLength() > 0) {
            return nl.item(0).getTextContent();
        }
        return null;
    }

    /**
     * Required Element - runtime error will be thrown if the node or element is missing
     */
    public String getFramework() {
        return getHeadingNode().getElementsByTagName(Heading.framework.name()).item(0).getTextContent();
    }

    /**
     * Required Element - runtime error will be thrown if the node or element is missing
     */
    public String getFrameworkVersion() {
        return getHeadingNode().getElementsByTagName(Heading.frameworkVersion.name()).item(0).getTextContent();
    }

    /**
     * Required Element - runtime error will be thrown if the node or element is missing
     */
    public String getName() {
        return getHeadingNode().getElementsByTagName(Heading.name.name()).item(0).getTextContent();
    }

    /**
     * Required Element - runtime error will be thrown if the node or element is missing
     */
    public String getDefinition() {
        return getHeadingNode().getElementsByTagName(Heading.definition.name()).item(0).getTextContent();
    }

    /**
     * Required Element - runtime error will be thrown if the node or element is missing
     */
    public String getNotes() {
        return getHeadingNode().getElementsByTagName(Heading.notes.name()).item(0).getTextContent();
    }

    // Query elements.

    /**
     * Required Element - runtime error will be thrown if the node or element is missing
     */
    public String getIndicatorQueryVersion() {
        return getIndicatorQueryNode().getElementsByTagName(Indicator.version.name()).item(0).getTextContent();
    }

    /**
     * Required Element - runtime error will be thrown if the node or element is missing
     */
    public String getDrilldownQueryVersion() {
        return getDrillDownQueryNode().getElementsByTagName(Drilldown.version.name()).item(0).getTextContent();
    }

    /**
     * Optional Element - returns null if the node or element is missing.
     */
    public String getIndicatorQuery() {
        NodeList nodeList = getIndicatorQueryNode().getElementsByTagName(Indicator.query.name());
        if (nodeList == null || nodeList.getLength() < 1) {
            return "";
        }
        return nodeList.item(0).getTextContent();
    }

    /**
     * Optional Element - returns null if the node or element is missing.
     */
    public String getDrilldownQuery() {
        NodeList nodeList = getDrillDownQueryNode().getElementsByTagName(Drilldown.query.name());
        if (nodeList == null) {
            return "";
        }
        return nodeList.item(0).getTextContent();
    }

    /**
     * Optional Element - returns null if the node or element is missing.
     */
    public List<Parameter> getIndicatorParameters() {
        NodeList paramsNodeList = getIndicatorQueryNode().getElementsByTagName(Indicator.params.name());
        Element paramsElement = null;
        NodeList parameters = null;

        if (paramsNodeList != null) {
            paramsElement = (Element) paramsNodeList.item(0);
        }

        if (paramsElement != null) {
            parameters = paramsElement.getElementsByTagName(Indicator.parameter.name());
        }

        return createParameterList(parameters, "null");
    }

    /**
     * Optional Element - returns null if the node or element is missing.
     */
    public List<Parameter> getDrilldownParameters(String metricLabel) {
        NodeList paramsNodeList = getDrillDownQueryNode().getElementsByTagName(Drilldown.params.name());
        Element paramsElement = null;
        NodeList parameters = null;

        if (paramsNodeList != null) {
            paramsElement = (Element) paramsNodeList.item(0);
        }

        if (paramsElement != null) {
            parameters = paramsElement.getElementsByTagName(Drilldown.parameter.name());
        }

        return createParameterList(parameters, metricLabel);
    }

    /**
     * Optional Element - returns null if the node or element is missing.
     */
    public List<DrillDownAction> getDrilldownActions() {
        NodeList actionsNodeList = getDrillDownQueryNode().getElementsByTagName(Drilldown.drillDownActions.name());
        Element drillDownActionsElement = null;
        NodeList actions = null;
        if (actionsNodeList != null) {
            drillDownActionsElement = (Element) actionsNodeList.item(0);
        }
        if (drillDownActionsElement != null) {
            actions = drillDownActionsElement.getElementsByTagName(Drilldown.action.name());
        }
        return createActionList(actions);
    }

    /**
     * Optional Element - returns null if the node or element is missing.
     */
    public List<Column> getDrilldownDisplayColumns() {
        NodeList columnNodeList = getDrillDownQueryNode().getElementsByTagName(Drilldown.displayColumns.name());
        return createColumnList(columnNodeList);
    }

    /**
     * Optional Element - returns null if the node or element is missing.
     */
    public List<Column> getDrilldownExportColumns() {
        NodeList columnNodeList = getDrillDownQueryNode().getElementsByTagName(Drilldown.exportColumns.name());
        return createColumnList(columnNodeList);
    }

    /**
     * Optional Element - returns null if the node or element is missing.
     */
    public List<RangeInterface> getIndicatorRanges() {
        NodeList rangeNodeList = getIndicatorQueryNode().getElementsByTagName(Indicator.range.name());
        return getRanges(rangeNodeList);
    }

    /**
     * Optional Element - returns null if the node or element is missing.
     */
    public List<RangeInterface> getDrilldownRanges() {
        NodeList rangeNodeList = getDrillDownQueryNode().getElementsByTagName(Drilldown.range.name());
        return getRanges(rangeNodeList);
    }

    private List<RangeInterface> getRanges(NodeList rangeNodeList) {

        List<RangeInterface> rangeList = null;
        Element rangesNode = null;
        NodeList upperLimits = null;
        NodeList lowerLimits = null;

        if (rangeNodeList != null && rangeNodeList.getLength() > 0) {
            rangesNode = (Element) rangeNodeList.item(0);
        }

        if (rangesNode != null) {
            upperLimits = rangesNode.getElementsByTagName(RangeType.upperLimit.name());
            lowerLimits = rangesNode.getElementsByTagName(RangeType.lowerLimit.name());
        }

        if (upperLimits != null) {
            rangeList = createRangeList(upperLimits, RangeUpperLimit.class);
        }

        if (lowerLimits != null) {

            if (rangeList != null) {
                rangeList.addAll(createRangeList(lowerLimits, RangeLowerLimit.class));
            } else {
                rangeList = createRangeList(lowerLimits, RangeLowerLimit.class);
            }
        }

        return rangeList;
    }

    // helpers
    private static List<RangeInterface> createRangeList(NodeList ranges, Class<?> clazz) {

        List<RangeInterface> rangeList = null;

        for (int i = 0; i < ranges.getLength(); i++) {

            if (rangeList == null) {
                rangeList = new ArrayList<RangeInterface>();
            }

            Node rangeNode = ranges.item(i);
            NamedNodeMap rangeAttributes = rangeNode.getAttributes();
            String id = rangeAttributes.getNamedItem(RangeAttribute.id.name()).getTextContent();
            String name = rangeAttributes.getNamedItem(RangeAttribute.name.name()).getTextContent();
            String value = rangeAttributes.getNamedItem(RangeAttribute.value.name()).getTextContent();
            String label = rangeAttributes.getNamedItem(RangeAttribute.label.name()).getTextContent();
            RangeInterface range = null;

            try {
                range = (RangeInterface) clazz.newInstance();
            } catch (Exception e) {
                logger.error("Failed to instantiate class " + clazz.getName(), e);
            }

            if (range != null) {

                range.setId(id);
                range.setName(name);
                range.setValue(value);
                range.setLabel(label);
                rangeList.add(range);

            }
        }

        return rangeList;
    }

    private static List<Column> createColumnList(NodeList columnNodeList) {

        List<Column> columnList = null;
        Element columnsNode = null;
        NodeList columns = null;

        if (columnNodeList == null) {
            return columnList;
        }

        columnsNode = (Element) columnNodeList.item(0);

        if (columnsNode == null) {
            return columnList;
        }

        columns = columnsNode.getElementsByTagName(Drilldown.column.name());

        if (columns == null) {
            return columnList;
        }

        for (int i = 0; i < columns.getLength(); i++) {

            if (columnList == null) {
                columnList = new ArrayList<Column>();
            }

            Node columnNode = columns.item(i);
            NamedNodeMap columnAttributes = columnNode.getAttributes();
            Column column = new Column();

            String id = columnAttributes.getNamedItem(ColumnAttribute.id.name()).getTextContent();
            String name = columnAttributes.getNamedItem(ColumnAttribute.name.name()).getTextContent();
            String title = columnAttributes.getNamedItem(ColumnAttribute.title.name()).getTextContent();
            Node primaryNode = columnAttributes.getNamedItem(ColumnAttribute.primary.name());
            String primary = "";

            if (primaryNode != null) {
                primary = primaryNode.getTextContent();
            }

            if (primary != null) {
                column.setPrimary(Boolean.parseBoolean(primary));
            }

            column.setId(id);
            column.setName(name);
            column.setTitle(title);

            columnList.add(column);
        }

        return columnList;
    }

    private List<Parameter> createParameterList(NodeList parameters, String metricLabel) {

        List<Parameter> parameterList = null;

        if (parameters == null) {
            return parameterList;
        }

        for (int i = 0; i < parameters.getLength(); i++) {

            if (parameterList == null) {
                parameterList = new ArrayList<Parameter>();
            }

            Node parameterNode = parameters.item(i);
            NamedNodeMap parameterAttributes = parameterNode.getAttributes();
            Parameter parameter = new Parameter();
            String[] values;

            String id = parameterAttributes.getNamedItem(ParameterAttribute.id.name()).getTextContent();
            String name = parameterAttributes.getNamedItem(ParameterAttribute.name.name()).getTextContent();
            String value = parameterAttributes.getNamedItem(ParameterAttribute.value.name()).getTextContent();

            if (value.contains(",")) {
                values = value.split(",");
            } else {
                values = new String[]{value};
            }

            values = setParameterAliasWithValue(id, values);

            if (id.equals("sharedMetricLabel")) {
                values[0] = metricLabel;
            }
            parameter.setId(id);
            parameter.setName(name);
            parameter.setValue(values);

            parameterList.add(parameter);
        }

        return parameterList;
    }

    private List<DrillDownAction> createActionList(NodeList actions) {
        List<DrillDownAction> actionList = null;
        if (actions == null) {
            return actionList;
        }
        for (int i = 0; i < actions.getLength(); i++) {

            if (actionList == null) {
                actionList = new ArrayList<DrillDownAction>();
            }
            Node actionNode = actions.item(i);
            NamedNodeMap actionAttributes = actionNode.getAttributes();
            DrillDownAction action = new DrillDownAction();
            String id = actionAttributes.getNamedItem(ActionAttribute.id.name()).getTextContent();
            String name = actionAttributes.getNamedItem(ActionAttribute.name.name()).getTextContent();
            String value = null;
            if ("dxUpdate".equals(id)) {
                value = actionAttributes.getNamedItem(ActionAttribute.value.name()).getTextContent();
            }
            action.setId(id);
            action.setName(name);
            action.setValue(value);

            actionList.add(action);
        }
        return actionList;
    }

    private String[] setParameterAliasWithValue(String parameterId, String[] parameterValues) {

        String[] newParameterValues = new String[parameterValues.length];

        for (int i = 0; i < parameterValues.length; i++) {
            newParameterValues[i] = setParameterAliasWithValue(parameterId, parameterValues[i]);
        }

        return newParameterValues;
    }

    /**
     * This is set to replace a Provider Number alias with the logged in Provider Number.
     * Others can be added as needed by editing the Enum values and Switch in this method.
     */
    private String setParameterAliasWithValue(String parameterId, String parameterValue) {

        parameterValue = parameterValue.trim();

        //TODO for now only captures one required parameter value
        // A switch will be required here to handle more values.
        if (!(ManditoryParameter.provider.name()).equalsIgnoreCase(parameterId)) {
            if ((ExcludedPatientAlias.excludedpatient.name().equalsIgnoreCase(parameterId))) {
                switch (ExcludedPatientAlias.valueOf(parameterValue.toLowerCase())) {
                    case excludedpatient:
                        excludeDemographicHandler.setLoggedinInfo(loggedInInfo);
                        String indicatorName = excludeDemographicHandler.getDrilldownIdentifier(this.getName(), this.getSubCategory(), this.getCategory());
                        List<Integer> demoNos = excludeDemographicHandler.getDemoIds(indicatorName);
                        if (demoNos != null && !demoNos.isEmpty()) {
                            logger.info("excluding patients for " + indicatorName);
                            String result = "(";
                            for (Integer i : demoNos) {
                                result += i + ",";
                            }
                            parameterValue = result.substring(0, result.length() - 1) + ")";
                        } else {
                            parameterValue = "(-1)";
                        }
                        break;
                    case none:
                        parameterValue = "(-1)";
                        break;
                }
            }
            return parameterValue;
        }

        if (providerNo != null) {
            return providerNo;
        }

        if (parameterValue.equals("%")) {
            return parameterValue;
        }

        // alias values should not have punctuation.
        parameterValue = parameterValue.replaceAll("[^a-zA-Z ]", "").toLowerCase();

        if (parameterValue.isEmpty()) {
            return parameterValue;
        }

        switch (ProviderValueAlias.valueOf(parameterValue)) {
            case loggedinprovider:
                parameterValue = getLoggedInProvider().trim();
                break;

            case all:
                parameterValue = "%";
                break;

        }

        return parameterValue;
    }

    public String getLoggedInProvider() {
        String providerNo = "";

        if (this.loggedInInfo != null) {
            providerNo = this.loggedInInfo.getLoggedInProviderNo();
        }

        return providerNo;
    }

    public void setProviderNo(String providerNo) {
        this.providerNo = providerNo;
    }


    public void setSharedMetricLabel(String sharedMetricLabel) {
        this.sharedMetricLabel = sharedMetricLabel;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this);
    }

}
