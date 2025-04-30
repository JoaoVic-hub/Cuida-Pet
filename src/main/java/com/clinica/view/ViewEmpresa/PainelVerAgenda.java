package com.clinica.view.ViewEmpresa;

import com.clinica.facade.ClinicaFacade; // Importar Facade
import com.clinica.model.Agenda;
import com.clinica.model.Veterinario;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class PainelVerAgenda extends JPanel {

    private JTable tabela;
    private DefaultTableModel modelo;
    private JScrollPane scrollPane;
    // REMOVER: private AgendaDAO agendaDAO;
    // REMOVER: private VeterinarioController vetController;
    private JComboBox<VeterinarioComboItem> comboVeterinarios;
    private DateTimeFormatter dtf;

    // --- USAR A FACADE ---
    private ClinicaFacade facade = ClinicaFacade.getInstance();
    // ---------------------

    public PainelVerAgenda() {
        // REMOVER inicialização de DAOs/Controllers aqui
        // ClienteDAO clienteDAO = new ClienteDAO();
        // AnimalDAO animalDAO = new AnimalDAO();
        // VeterinarioDAO veterinarioDAO = new VeterinarioDAO();
        // ConsultaDAO consultaDAO = new ConsultaDAO(clienteDAO, animalDAO, veterinarioDAO);
        // agendaDAO = new AgendaDAO(consultaDAO);
        // vetController = new VeterinarioController();

        dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        setLayout(new BorderLayout(10, 10));

        // --- Painel superior (sem mudanças na UI) ---
        JPanel panelTopo = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JLabel lblVet = new JLabel("Selecione o Veterinário:");
        lblVet.setFont(new Font("Segoe UI", Font.BOLD, 14)); // Melhorar fonte
        comboVeterinarios = new JComboBox<>();
        comboVeterinarios.setPreferredSize(new Dimension(300, 28)); // Aumentar um pouco
        comboVeterinarios.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panelTopo.add(lblVet);
        panelTopo.add(comboVeterinarios);
        add(panelTopo, BorderLayout.NORTH);


        // --- Configuração da tabela (sem mudanças na UI) ---
         modelo = new DefaultTableModel(new String[]{
            "ID Consulta", "Data/Hora", "Status", "Animal", "Cliente" // Removido Endereço, não parece estar sendo populado corretamente antes
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabela = new JTable(modelo);
        tabela.setRowHeight(25);
        tabela.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tabela.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        scrollPane = new JScrollPane(tabela);
        add(scrollPane, BorderLayout.CENTER);


        // --- Painel inferior (sem mudanças na UI) ---
        JPanel panelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnAtualizar = new JButton("🔄 Atualizar Agenda");
        btnAtualizar.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panelBotoes.add(btnAtualizar);
        add(panelBotoes, BorderLayout.SOUTH);


        // --- Listeners (AJUSTAR CHAMADAS) ---
         carregarVeterinariosCombo(); // Carrega o combo usando a facade

        // Listener do ComboBox para filtrar
        comboVeterinarios.addActionListener(e -> {
            VeterinarioComboItem item = (VeterinarioComboItem) comboVeterinarios.getSelectedItem();
            if (item != null && item.getId() > 0) { // Verifica se não é item default ou nulo
                carregarAgendaPorVeterinario(item.getId()); // Carrega agenda usando a facade
            } else {
                 // Se selecionou "Todos" ou item inválido, pode limpar a tabela ou carregar tudo
                 modelo.setRowCount(0); // Limpa a tabela
                 // Ou carregar a agenda completa: carregarAgendaCompleta(); (implementar se necessário)
            }
        });

        // Listener do botão Atualizar
        btnAtualizar.addActionListener(e -> {
            VeterinarioComboItem item = (VeterinarioComboItem) comboVeterinarios.getSelectedItem();
             if (item != null && item.getId() > 0) {
                carregarAgendaPorVeterinario(item.getId()); // Recarrega agenda do selecionado
            } else {
                 modelo.setRowCount(0); // Limpa se nenhum vet selecionado
                 // Ou recarrega a agenda completa
            }
        });

        // Carrega inicialmente a agenda do primeiro veterinário (se houver)
        if (comboVeterinarios.getItemCount() > 1) { // Maior que 1 para ignorar o "Todos"
            comboVeterinarios.setSelectedIndex(1); // Seleciona o primeiro real
            // A ação do ComboBox já vai chamar carregarAgendaPorVeterinario
        } else if (comboVeterinarios.getItemCount() == 1 && ((VeterinarioComboItem)comboVeterinarios.getItemAt(0)).getId() > 0) {
             // Caso especial: só tem 1 veterinário e não tem a opção "Todos"
             comboVeterinarios.setSelectedIndex(0);
        }
    }

    // AJUSTADO: Carrega veterinários no combo usando a Facade
    private void carregarVeterinariosCombo() {
        comboVeterinarios.removeAllItems();
        // Adicionar opção para ver todos? (Opcional)
        // comboVeterinarios.addItem(new VeterinarioComboItem(0, "-- Todos --"));
        try {
            // USA A FACADE
            List<Veterinario> vets = facade.listarTodosVeterinarios();
            if (vets != null) {
                for (Veterinario v : vets) {
                    comboVeterinarios.addItem(new VeterinarioComboItem(v.getId(), v.getNome()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
             JOptionPane.showMessageDialog(this, "Erro ao carregar lista de veterinários: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    // AJUSTADO: Carrega agenda por veterinário usando a Facade
    private void carregarAgendaPorVeterinario(int vetId) {
         if (vetId <= 0) { // Não carrega para ID inválido (ex: "-- Todos --")
             modelo.setRowCount(0);
             return;
         }
        try {
            // USA A FACADE
            List<Agenda> lista = facade.listarAgendaPorVeterinario(vetId);
            atualizarTabela(lista); // Método auxiliar não muda
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao carregar agenda do veterinário: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            modelo.setRowCount(0); // Limpa em caso de erro
        }
    }

    // Método auxiliar para atualizar a tabela (não precisa mudar)
    private void atualizarTabela(List<Agenda> lista) {
        modelo.setRowCount(0);
        if (dtf == null) {
            dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        }

        if (lista != null) {
             for (Agenda agenda : lista) {
                 if (agenda == null) continue;

                 String dataFormatada = (agenda.getDataHora() != null) ? dtf.format(agenda.getDataHora()) : "N/A";
                 String nomeAnimal = agenda.getNomeAnimal() != null ? agenda.getNomeAnimal() : "-";
                 String nomeCliente = agenda.getNomeCliente() != null ? agenda.getNomeCliente() : "N/A";
                 String status = agenda.getStatus() != null ? agenda.getStatus() : "N/A";

                 modelo.addRow(new Object[]{
                     agenda.getConsultaId(),
                     dataFormatada,
                     status,
                     nomeAnimal,
                     nomeCliente
                     // agenda.getEnderecoCliente() // Removido - não populado consistentemente
                 });
            }
        }
        // Notifica a tabela (Boa prática)
        modelo.fireTableDataChanged();
    }
}