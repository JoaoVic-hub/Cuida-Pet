package com.clinica;

import com.clinica.view.ClinicaView;
import javax.swing.SwingUtilities;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ClinicaView view = new ClinicaView();
            view.setVisible(true);
        });
    }
}
