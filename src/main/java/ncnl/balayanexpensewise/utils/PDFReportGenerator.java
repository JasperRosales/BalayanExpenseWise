package ncnl.balayanexpensewise.utils;

import ncnl.balayanexpensewise.beans.Transaction;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import java.util.List;
import java.util.Map;

public class PDFReportGenerator {

    public void generateReport(List<Transaction> transactions, Map<String, Object> parameters, String templatePath, String outputPath) {
        try {
            JasperReport jasperReport = JasperCompileManager.compileReport(templatePath);

            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(transactions);

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

            JasperExportManager.exportReportToPdfFile(jasperPrint, outputPath);

            System.out.println("PDF Report generated successfully: " + outputPath);

        } catch (JRException e) {
            e.printStackTrace();
            System.err.println("Error generating PDF report: " + e.getMessage());
        }
    }
}
