//CHECKSTYLE:OFF

package ca.openosp.openo;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.*;
import ca.openosp.openo.ehrutil.MiscUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;

public class OscarDocumentCreator {
    public static final String PDF = "pdf";
    public static final String CSV = "csv";
    public static final String EXCEL = "excel";

    public OscarDocumentCreator() {

    }

    public InputStream getDocumentStream(String path) {
        InputStream reportInstream = null;
        reportInstream = getClass().getClassLoader().getResourceAsStream(path);
        return reportInstream;
    }

    @SuppressWarnings("rawtypes")
    public void fillDocumentStream(HashMap parameters, OutputStream sos,
                                   String docType, InputStream xmlDesign,
                                   Object dataSrc) {
        fillDocumentStream(parameters, sos, docType, xmlDesign, dataSrc, null);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public void fillDocumentStream(HashMap parameters, OutputStream sos,
                                   String docType, InputStream xmlDesign,
                                   Object dataSrc, String exportPdfJavascript) {
        try {
            JasperReport jasperReport = null;
            JasperPrint print = null;
            jasperReport = getJasperReport(xmlDesign);
            if (docType.equals(OscarDocumentCreator.PDF) && exportPdfJavascript != null) {
                jasperReport.setProperty("net.sf.jasperreports.export.pdf.javascript", exportPdfJavascript);
            }
            if (dataSrc == null) {
                print = JasperFillManager.fillReport(jasperReport, parameters, new JREmptyDataSource());
            } else if (dataSrc instanceof List) {
                JRDataSource ds = new JRBeanCollectionDataSource((List<?>) dataSrc);
                print = JasperFillManager.fillReport(jasperReport, parameters,
                        ds);
            } else if (dataSrc instanceof java.sql.Connection) {
                print = JasperFillManager.fillReport(jasperReport, parameters,
                        (Connection) dataSrc);
            } else if (dataSrc instanceof ResultSet) {
                JRDataSource ds = new JRResultSetDataSource((ResultSet) dataSrc);
                print = JasperFillManager.fillReport(jasperReport, parameters,
                        ds);
            } else {
                JRDataSource ds = (JRDataSource) dataSrc;
                print = JasperFillManager.fillReport(jasperReport, parameters, ds);
            }
            if (docType.equals(OscarDocumentCreator.PDF)) {
                JasperExportManager.exportReportToPdfStream(print, sos);
            } else if (docType.equals(OscarDocumentCreator.CSV)) {
                this.exportReportToCSVStream(print, sos);

            } else if (docType.equals(OscarDocumentCreator.EXCEL)) {
                this.exportReportToExcelStream(print, sos);
            }

        } catch (JRException ex) {
            MiscUtils.getLogger().error("Error", ex);
        }
    }

    /**
     * Returns a JasperReport instance reprepesenting the supplied InputStream
     *
     * @param xmlDesign InputStream
     * @return JasperReport
     */
    public JasperReport getJasperReport(InputStream xmlDesign) {
        JasperReport jasperReport = null;
        try {
            jasperReport = JasperCompileManager.compileReport(
                    xmlDesign);
        } catch (JRException ex) {
            MiscUtils.getLogger().error("Error", ex);
        }
        return jasperReport;
    }

    public JasperReport getJasperReport(byte[] xmlDesign) {
        JasperReport jasperReport = null;
        try {
            jasperReport = JasperCompileManager.compileReport(
                    new ByteArrayInputStream(xmlDesign));
        } catch (JRException ex) {
            MiscUtils.getLogger().error("Error", ex);
        }
        return jasperReport;
    }

    /**
     * Fills a servletoutout stream with data from a JasperReport
     *
     * @param jasperPrint JasperPrint
     * @param sos         ServletOutputStream
     * @throws JRException
     */
    private void exportReportToCSVStream(JasperPrint jasperPrint, OutputStream sos) throws JRException {
        JRCsvExporter exp = new JRCsvExporter();
        exp.setExporterInput(new SimpleExporterInput(jasperPrint));
        exp.setExporterOutput(new SimpleWriterExporterOutput(sos));
        SimpleCsvReportConfiguration configuration = new SimpleCsvReportConfiguration();
        exp.setConfiguration(configuration);
        exp.exportReport();
    }

    private void exportReportToExcelStream(JasperPrint jasperPrint, OutputStream os)
            throws JRException {
        JRXlsxExporter exp = new JRXlsxExporter();
        exp.setExporterInput(new SimpleExporterInput(jasperPrint));
        exp.setExporterOutput(new SimpleOutputStreamExporterOutput(os));

        SimpleXlsxReportConfiguration reportConfiguration = new SimpleXlsxReportConfiguration();
        reportConfiguration.setIgnorePageMargins(true);
        reportConfiguration.setOffsetX(0);
        reportConfiguration.setIgnoreCellBorder(false);
        reportConfiguration.setDetectCellType(true);
        reportConfiguration.setWhitePageBackground(false);
        reportConfiguration.setOnePagePerSheet(false);
        reportConfiguration.setMaxRowsPerSheet(65000);
        exp.setConfiguration(reportConfiguration);

        exp.exportReport();
    }

}
