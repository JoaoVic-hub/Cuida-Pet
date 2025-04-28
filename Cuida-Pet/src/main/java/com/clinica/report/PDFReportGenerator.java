package com.clinica.report;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public abstract class PDFReportGenerator {
    protected Document document;
    protected String outputPath;

    public PDFReportGenerator(String outputPath) {
        this.outputPath = outputPath;
    }

    public final void generateReport() {
        try {
            openDocument();
            addHeader();   
            addContent();  
            addFooter();   
            closeDocument();
        } catch (DocumentException | FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void openDocument() throws DocumentException, FileNotFoundException {
        document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(outputPath));
        document.open();
    }

    protected abstract void addHeader() throws DocumentException;
    protected abstract void addContent() throws DocumentException;
    protected abstract void addFooter() throws DocumentException;

    private void closeDocument() {
        document.close();
    }
}
