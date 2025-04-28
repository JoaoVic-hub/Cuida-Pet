package com.clinica.view.ViewEmpresa; // Mantendo o pacote original

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// Imports dos DAOs e Models necessários
import com.clinica.DAO.ConsultaDAO;
import com.clinica.DAO.VeterinarioDAO;
import com.clinica.DAO.ClienteDAO; // Necessário para instanciar ConsultaDAO
import com.clinica.DAO.AnimalDAO;  // Necessário para instanciar ConsultaDAO
import com.clinica.model.Consulta;
import com.clinica.model.Veterinario;

public class PainelRelatorios extends JPanel {

    private JTextField campoMes;
    private JTextField campoAno;
    private JButton btnGerar;
    private JTextArea areaRelatorio;

    // --- DAOs necessários ---
    private final ConsultaDAO consultaDAO;
    private final VeterinarioDAO veterinarioDAO;
    // Não precisa de ClienteDAO e AnimalDAO aqui se já foram usados para criar ConsultaDAO

    public PainelRelatorios() {
        // --- Inicialização dos DAOs ---
        // É CRUCIAL que esta instanciação seja feita corretamente,
        // passando as dependências necessárias.
        // Idealmente, os DAOs seriam injetados de fora, mas para simplificar:
        ClienteDAO clienteDAO = new ClienteDAO();
        AnimalDAO animalDAO = new AnimalDAO(/* talvez precise do clienteDAO? */);
        this.veterinarioDAO = new VeterinarioDAO();
        this.consultaDAO = new ConsultaDAO(clienteDAO, animalDAO, this.veterinarioDAO);
        // -----------------------------

        setLayout(new BorderLayout(10, 10));

        // Título
        JLabel lblTitulo = new JLabel("Relatórios de Consultas por Veterinário", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(lblTitulo, BorderLayout.NORTH);

        // Painel de Filtros
        JPanel panelFiltros = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panelFiltros.add(new JLabel("Mês (1-12):"));
        campoMes = new JTextField(2);
        panelFiltros.add(campoMes);
        panelFiltros.add(new JLabel("Ano (AAAA):"));
        campoAno = new JTextField(4);
        panelFiltros.add(campoAno);
        btnGerar = new JButton("Gerar Relatório");
        panelFiltros.add(btnGerar);
        add(panelFiltros, BorderLayout.CENTER);

        // Área de Texto para o Relatório
        areaRelatorio = new JTextArea();
        areaRelatorio.setEditable(false);
        areaRelatorio.setFont(new Font("Monospaced", Font.PLAIN, 12)); // Fonte monoespaçada para alinhar
        JScrollPane scrollPane = new JScrollPane(areaRelatorio);
        scrollPane.setPreferredSize(new Dimension(600, 400)); // Tamanho preferencial
        add(scrollPane, BorderLayout.SOUTH);

        // Ação do Botão
        btnGerar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gerarRelatorio();
            }
        });
    }

    private void gerarRelatorio() {
        int mes;
        int ano;

        // Validação básica da entrada
        try {
            mes = Integer.parseInt(campoMes.getText());
            ano = Integer.parseInt(campoAno.getText());
            if (mes < 1 || mes > 12 || ano < 1900 || ano > YearMonth.now().getYear() + 5) { // Validação de ano razoável
                JOptionPane.showMessageDialog(this, "Por favor, insira um mês (1-12) e um ano válidos.", "Entrada Inválida", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Por favor, insira valores numéricos para mês e ano.", "Entrada Inválida", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Lógica para buscar dados com DAOs e gerar o relatório
        try {
            // 1. Buscar todas as consultas (o ConsultaDAO deve carregar os relacionamentos)
            List<Consulta> todasConsultas = consultaDAO.listarTodos();

            // 2. Filtrar as consultas pelo mês e ano desejados
            List<Consulta> consultasFiltradas = todasConsultas.stream()
                .filter(c -> c != null && c.getDataHora() != null && c.getVeterinario() != null) // Garante dados essenciais
                .filter(c -> c.getDataHora().getMonthValue() == mes && c.getDataHora().getYear() == ano)
                .collect(Collectors.toList());

            if (consultasFiltradas.isEmpty()) {
                areaRelatorio.setText(String.format("Nenhuma consulta encontrada para %02d/%04d.", mes, ano));
                return;
            }

            // 3. Agrupar as consultas filtradas por ID do veterinário
            Map<Integer, List<Consulta>> consultasPorVeterinario = consultasFiltradas.stream()
                .collect(Collectors.groupingBy(c -> c.getVeterinario().getId()));

            // 4. Construir o relatório
            StringBuilder relatorio = new StringBuilder();
            relatorio.append(String.format("Relatório de Consultas - Mês: %02d / Ano: %04d\n", mes, ano));
            relatorio.append("======================================================\n");

            // Itera sobre cada veterinário que teve consultas no período
            for (Map.Entry<Integer, List<Consulta>> entry : consultasPorVeterinario.entrySet()) {
                int vetId = entry.getKey();
                List<Consulta> consultasDoVet = entry.getValue();

                // Busca o nome do veterinário
                Veterinario vet = veterinarioDAO.exibir(vetId);
                String nomeVet = (vet != null) ? vet.getNome() : "Veterinário ID: " + vetId + " (Não encontrado)";

                // Conta os status
                long total = consultasDoVet.size();
                long concluidas = consultasDoVet.stream().filter(c -> "Concluída".equalsIgnoreCase(c.getStatus())).count();
                long canceladas = consultasDoVet.stream().filter(c -> "Cancelada".equalsIgnoreCase(c.getStatus())).count();
                // Poderia contar outros status também (Agendada, Em Andamento, etc.)

                relatorio.append(String.format("Veterinário: %s\n", nomeVet));
                relatorio.append(String.format("  Total de Consultas no Período: %d\n", total));
                relatorio.append(String.format("  - Concluídas: %d\n", concluidas));
                relatorio.append(String.format("  - Canceladas: %d\n", canceladas));
                // Adicionar contagem de outros status se necessário
                relatorio.append("------------------------------------------------------\n");
            }

            areaRelatorio.setText(relatorio.toString());

        } catch (Exception e) {
            // Captura exceções gerais que podem ocorrer nos DAOs ou processamento
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao gerar o relatório: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            areaRelatorio.setText("Erro ao gerar relatório.\nConsulte o console para mais detalhes.");
        }
    }
}