package com.clinica.report;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * Classe abstrata que define o Template Method para gerar relatórios em PDF.
 * O método generateReport() é o Template Method, que define a sequência de passos
 * para a criação do PDF. As etapas específicas (cabeçalho, conteúdo e rodapé)
 * são implementadas pelas subclasses.
 */
public abstract class PDFReportGenerator {
    protected Document document;
    protected String outputPath;

    public PDFReportGenerator(String outputPath) {
        this.outputPath = outputPath;
    }

    // Template Method: define a sequência de passos para gerar o relatório em PDF.
    public final void generateReport() {
        try {
            openDocument();
            addHeader();   // etapa customizável (definida pela subclasse)
            addContent();  // etapa customizável (definida pela subclasse)
            addFooter();   // etapa customizável (definida pela subclasse)
            closeDocument();
        } catch (DocumentException | FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    // Método para abrir e preparar o documento PDF
    private void openDocument() throws DocumentException, FileNotFoundException {
        document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(outputPath));
        document.open();
    }

    // Métodos abstratos a serem implementados pelas subclasses (etapas customizáveis)
    protected abstract void addHeader() throws DocumentException;
    protected abstract void addContent() throws DocumentException;
    protected abstract void addFooter() throws DocumentException;

    // Método para fechar o documento PDF
    private void closeDocument() {
        document.close();
    }
}
