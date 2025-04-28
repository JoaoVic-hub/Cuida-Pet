package com.clinica.view.ViewEmpresa;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import com.google.gson.Gson; // Caso use algum parser, mas aqui não é necessário
import com.clinica.DAO.ConexaoMySQL;
import java.time.format.DateTimeFormatter;

public class PainelRelatorios extends JPanel {

    private JTextField campoMes;
    private JTextField campoAno;
    private JButton btnGerar;
    private JTextArea areaRelatorio;

    public PainelRelatorios() {
        setLayout(new BorderLayout(10, 10));

        // Título do painel
        JLabel lblTitulo = new JLabel("Relatórios de Consultas", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(lblTitulo, BorderLayout.NORTH);

        // Painel de Filtros (Mês e Ano)
        JPanel panelFiltros = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panelFiltros.add(new JLabel("Mês:"));
        campoMes = new JTextField(2);
        panelFiltros.add(campoMes);
        panelFiltros.add(new JLabel("Ano:"));
        campoAno = new JTextField(4);
        panelFiltros.add(campoAno);
        btnGerar = new JButton("Gerar Relatório");
        panelFiltros.add(btnGerar);
        add(panelFiltros, BorderLayout.CENTER);

        // Área de exibição do relatório
        areaRelatorio = new JTextArea();
        areaRelatorio.setFont(new Font("Monospaced", Font.PLAIN, 12));
        areaRelatorio.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(areaRelatorio);
        scrollPane.setPreferredSize(new Dimension(800, 400));
        add(scrollPane, BorderLayout.SOUTH);

        // Ação do botão de gerar relatório
        btnGerar.addActionListener(e -> gerarRelatorio());
    }

    private void gerarRelatorio() {
        String mesStr = campoMes.getText().trim();
        String anoStr = campoAno.getText().trim();

        if (mesStr.isEmpty() || anoStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, informe o mês e o ano para gerar o relatório.");
            return;
        }

        int mes, ano;
        try {
            mes = Integer.parseInt(mesStr);
            ano = Integer.parseInt(anoStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Mês e Ano devem ser valores numéricos.");
            return;
        }

        String sql = "{CALL sp_relatorio_consultas_veterinario(?, ?)}";
        StringBuilder relatorio = new StringBuilder();

        try (Connection conexao = com.clinica.DAO.ConexaoMySQL.getConexao();
             CallableStatement stmt = conexao.prepareCall(sql)) {

            stmt.setInt(1, mes);
            stmt.setInt(2, ano);

            try (ResultSet rs = stmt.executeQuery()) {
                relatorio.append(String.format("Relatório de Consultas - Mês: %02d / Ano: %04d\n", mes, ano));
                relatorio.append("------------------------------------------------------\n");
                while (rs.next()) {
                    String nomeVet = rs.getString("Veterinario");
                    int total = rs.getInt("total_consultas");
                    int concluidas = rs.getInt("consultas_concluidas");
                    int canceladas = rs.getInt("consultas_canceladas");
                    relatorio.append(String.format("Veterinário: %s\n", nomeVet));
                    relatorio.append(String.format("  Total de Consultas: %d\n", total));
                    relatorio.append(String.format("  Concluídas: %d  |  Canceladas: %d\n", concluidas, canceladas));
                    relatorio.append("------------------------------------------------------\n");
                }
            }
            areaRelatorio.setText(relatorio.toString());
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao gerar o relatório.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}
