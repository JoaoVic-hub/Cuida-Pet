package com.clinica.view.ViewEmpresa;

import com.clinica.facade.ClinicaFacade; // Importar Facade
import com.clinica.model.Consulta;
import com.clinica.model.Veterinario;
import java.awt.*;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Map; // Para nome do mês
import java.util.stream.Collectors;
import javax.swing.*; // Para nome do mês

public class PainelRelatorios extends JPanel {

    private JTextField campoMes;
    private JTextField campoAno;
    private JButton btnGerar;
    private JTextArea areaRelatorio;

    // --- USAR A FACADE ---
    private ClinicaFacade facade = ClinicaFacade.getInstance();
    // ---------------------

    // REMOVER DAOs específicos
    // private final ConsultaDAO consultaDAO;
    // private final VeterinarioDAO veterinarioDAO;

    public PainelRelatorios() {
        // REMOVER inicialização de DAOs aqui
        // ClienteDAO clienteDAO = new ClienteDAO();
        // AnimalDAO animalDAO = new AnimalDAO();
        // this.veterinarioDAO = new VeterinarioDAO();
        // this.consultaDAO = new ConsultaDAO(clienteDAO, animalDAO, this.veterinarioDAO);

        setLayout(new BorderLayout(10, 10));

        // --- UI (Título, Filtros, Área de Texto - sem mudanças) ---
        JLabel lblTitulo = new JLabel("Relatórios de Consultas por Veterinário", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(lblTitulo, BorderLayout.NORTH);

        JPanel panelFiltros = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panelFiltros.add(new JLabel("Mês (1-12):"));
        campoMes = new JTextField(3); // Um pouco mais largo
        panelFiltros.add(campoMes);
        panelFiltros.add(new JLabel("Ano (AAAA):"));
        campoAno = new JTextField(5); // Um pouco mais largo
        panelFiltros.add(campoAno);
        btnGerar = new JButton("📊 Gerar Relatório");
        panelFiltros.add(btnGerar);
        add(panelFiltros, BorderLayout.CENTER); // Adiciona ao centro, não sul

        areaRelatorio = new JTextArea();
        areaRelatorio.setEditable(false);
        areaRelatorio.setFont(new Font("Monospaced", Font.PLAIN, 13)); // Fonte monoespaçada
        JScrollPane scrollPane = new JScrollPane(areaRelatorio);
        // Define um tamanho preferencial para o scrollpane no sul
        scrollPane.setPreferredSize(new Dimension(600, 350));
        add(scrollPane, BorderLayout.SOUTH); // Adiciona ao sul

        // --- Ação do Botão (AJUSTAR CHAMADAS) ---
        btnGerar.addActionListener(e -> gerarRelatorio()); // Chama método que usa facade

        // Define valores padrão para mês/ano atual (opcional)
         YearMonth currentYearMonth = YearMonth.now();
         campoMes.setText(String.valueOf(currentYearMonth.getMonthValue()));
         campoAno.setText(String.valueOf(currentYearMonth.getYear()));
    }

    // AJUSTADO: Usa Facade para obter dados
    private void gerarRelatorio() {
        int mes;
        int ano;

        try {
            mes = Integer.parseInt(campoMes.getText().trim());
            ano = Integer.parseInt(campoAno.getText().trim());
            if (mes < 1 || mes > 12 || ano < 1900 || ano > YearMonth.now().getYear() + 10) { // Range de ano
                JOptionPane.showMessageDialog(this, "Mês (1-12) ou Ano inválido.", "Entrada Inválida", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Mês e Ano devem ser números.", "Entrada Inválida", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // 1. Buscar consultas filtradas usando a Facade
            // (O método buscarConsultasParaRelatorio na Facade já faz o filtro por mês/ano)
            List<Consulta> consultasFiltradas = facade.buscarConsultasParaRelatorio(mes, ano);

            if (consultasFiltradas.isEmpty()) {
                areaRelatorio.setText(String.format("Nenhuma consulta encontrada para %02d/%04d.", mes, ano));
                return;
            }

            // 2. Agrupar por Veterinário (ID)
            // Garante que só agrupa se o veterinário não for nulo
             Map<Integer, List<Consulta>> consultasPorVeterinario = consultasFiltradas.stream()
                .filter(c -> c.getVeterinario() != null) // Filtra consultas sem veterinário associado
                .collect(Collectors.groupingBy(c -> c.getVeterinario().getId()));

            if (consultasPorVeterinario.isEmpty() && !consultasFiltradas.isEmpty()) {
                areaRelatorio.setText(String.format("Consultas encontradas para %02d/%04d, mas nenhuma com veterinário associado.", mes, ano));
                return;
             }

            // 3. Construir o Relatório
            StringBuilder relatorio = new StringBuilder();
            // Locale para Português para nome do mês
            Locale brasil = new Locale("pt", "BR");
            String nomeMes = Month.of(mes).getDisplayName(TextStyle.FULL_STANDALONE, brasil);

            relatorio.append(String.format("Relatório de Consultas - %s / %04d\n", nomeMes.toUpperCase(), ano));
            relatorio.append("======================================================\n\n");

            // Itera sobre os veterinários que tiveram consultas
            for (Map.Entry<Integer, List<Consulta>> entry : consultasPorVeterinario.entrySet()) {
                int vetId = entry.getKey();
                List<Consulta> consultasDoVet = entry.getValue();

                // USA A FACADE para buscar o nome do veterinário
                Veterinario vet = facade.buscarVeterinarioPorId(vetId);
                String nomeVet = (vet != null && vet.getNome() != null) ? vet.getNome() : "Veterinário ID: " + vetId;

                // Contagem de status
                long total = consultasDoVet.size();
                long concluidas = consultasDoVet.stream().filter(c -> "Concluída".equalsIgnoreCase(c.getStatus())).count();
                long canceladas = consultasDoVet.stream().filter(c -> "Cancelada".equalsIgnoreCase(c.getStatus())).count();
                long agendadas = consultasDoVet.stream().filter(c -> "Agendada".equalsIgnoreCase(c.getStatus())).count();
                long emAndamento = consultasDoVet.stream().filter(c -> "Em Andamento".equalsIgnoreCase(c.getStatus())).count();


                relatorio.append(String.format("Veterinário: %s (ID: %d)\n", nomeVet, vetId));
                relatorio.append(String.format("  Total de Consultas no Período: %d\n", total));
                relatorio.append(String.format("    - Agendadas: .... %d\n", agendadas));
                relatorio.append(String.format("    - Em Andamento: . %d\n", emAndamento));
                relatorio.append(String.format("    - Concluídas: ... %d\n", concluidas));
                relatorio.append(String.format("    - Canceladas: ... %d\n", canceladas));
                relatorio.append("------------------------------------------------------\n");
            }

            areaRelatorio.setText(relatorio.toString());
            areaRelatorio.setCaretPosition(0); // Rola para o topo

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao gerar o relatório: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            areaRelatorio.setText("Erro ao gerar relatório.\nConsulte o console (logs) para mais detalhes.");
        }
    }
}