package com.clinica.report;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;

/**
 * Classe concreta que implementa o template para gerar um relatório de clientes.
 * Ela implementa as etapas específicas: cabeçalho, conteúdo e rodapé.
 */
public class ClientReportPDFGenerator extends PDFReportGenerator {
    private String clientReportData; // Conteúdo do relatório, por exemplo, uma String com os dados dos clientes.

    public ClientReportPDFGenerator(String outputPath, String clientReportData) {
        super(outputPath);
        this.clientReportData = clientReportData;
    }

    @Override
    protected void addHeader() throws DocumentException {
        // Adiciona o cabeçalho do relatório
        document.add(new Paragraph("=== Relatório de Clientes ===\n\n"));
    }

    @Override
    protected void addContent() throws DocumentException {
        // Adiciona o conteúdo principal do relatório
        document.add(new Paragraph(clientReportData));
    }

    @Override
    protected void addFooter() throws DocumentException {
        // Adiciona o rodapé do relatório
        document.add(new Paragraph("\n\nRelatório gerado automaticamente pelo sistema."));
    }
}
