package com.clinica;

import com.clinica.view.ViewEmpresa.TelaEmpresa;
import com.formdev.flatlaf.FlatLightLaf; // ou FlatDarkLaf
import javax.swing.*;

public class App {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
            // UIManager.setLookAndFeel(new FlatDarkLaf()); // se quiser tema escuro
        } catch (Exception e) {
            System.err.println("Erro ao aplicar tema FlatLaf");
        }

        SwingUtilities.invokeLater(() -> new TelaEmpresa());
    }
}
