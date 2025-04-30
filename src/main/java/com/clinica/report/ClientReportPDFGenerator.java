package com.clinica.report;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;

//template method
public class ClientReportPDFGenerator extends PDFReportGenerator {
    private String clientReportData; 

    public ClientReportPDFGenerator(String outputPath, String clientReportData) {
        super(outputPath);
        this.clientReportData = clientReportData;
    }

    @Override
    protected void addHeader() throws DocumentException {
        document.add(new Paragraph("=== Relatório de Clientes ===\n\n"));
    }

    @Override
    protected void addContent() throws DocumentException {
        document.add(new Paragraph(clientReportData));
    }

    @Override
    protected void addFooter() throws DocumentException {

        document.add(new Paragraph("\n\nRelatório gerado automaticamente pelo sistema."));
    }
}
