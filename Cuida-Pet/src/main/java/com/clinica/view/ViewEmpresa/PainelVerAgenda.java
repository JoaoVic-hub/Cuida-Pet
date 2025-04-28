package com.clinica.view.ViewEmpresa;

import com.clinica.DAO.AgendaDAO;
import com.clinica.DAO.AnimalDAO;
import com.clinica.DAO.ClienteDAO;
import com.clinica.DAO.ConsultaDAO;
import com.clinica.DAO.VeterinarioDAO;
import com.clinica.controller.VeterinarioController;
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
    private AgendaDAO agendaDAO;
    private VeterinarioController vetController;
    private JComboBox<VeterinarioComboItem> comboVeterinarios;
    private DateTimeFormatter dtf; // Para formatar a data/hora

    public PainelVerAgenda() {
        // Inicializa o DAO e o formatter de data/hora
// --- DEPOIS (Exemplo de como DEVE ser) ---
        ClienteDAO clienteDAO = new ClienteDAO();
        AnimalDAO animalDAO = new AnimalDAO();
        VeterinarioDAO veterinarioDAO = new VeterinarioDAO();
        // Passe as instâncias necessárias para o construtor:
        ConsultaDAO consultaDAO = new ConsultaDAO(clienteDAO, animalDAO, veterinarioDAO);

        agendaDAO = new AgendaDAO(consultaDAO);
        vetController = new VeterinarioController();
        dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        setLayout(new BorderLayout(10, 10));

        // Painel superior para selecionar o veterinário
        JPanel panelTopo = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JLabel lblVet = new JLabel("Selecione o Veterinário: ");
        comboVeterinarios = new JComboBox<>();
        comboVeterinarios.setPreferredSize(new Dimension(250, 25));
        panelTopo.add(lblVet);
        panelTopo.add(comboVeterinarios);

        // Carrega a lista de veterinários no combo
        carregarVeterinariosCombo();

        // Adiciona um listener para filtrar a agenda conforme a seleção
        comboVeterinarios.addActionListener(e -> {
            VeterinarioComboItem item = (VeterinarioComboItem) comboVeterinarios.getSelectedItem();
            if (item != null) {
                carregarAgendaPorVeterinario(item.getId());
            }
        });

        add(panelTopo, BorderLayout.NORTH);

        // Configuração do modelo da tabela
        modelo = new DefaultTableModel(new String[]{
            "Consulta ID", "Data/Hora", "Status", "Animal", "Cliente", "Endereço"
        }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Torna a tabela somente leitura
            }
        };

        tabela = new JTable(modelo);
        tabela.setRowHeight(25);
        tabela.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tabela.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        scrollPane = new JScrollPane(tabela);
        add(scrollPane, BorderLayout.CENTER);

        // Painel inferior com botão de Atualizar
        JPanel panelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnAtualizar = new JButton("Atualizar");
        btnAtualizar.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnAtualizar.addActionListener(e -> {
            VeterinarioComboItem item = (VeterinarioComboItem) comboVeterinarios.getSelectedItem();
            if (item != null) {
                carregarAgendaPorVeterinario(item.getId());
            }
        });
        panelBotoes.add(btnAtualizar);
        add(panelBotoes, BorderLayout.SOUTH);

        // Carrega inicialmente a agenda do veterinário selecionado
        if (comboVeterinarios.getItemCount() > 0) {
            VeterinarioComboItem item = (VeterinarioComboItem) comboVeterinarios.getSelectedItem();
            carregarAgendaPorVeterinario(item.getId());
        }
    }

    // Carrega todos os veterinários no comboBox
    private void carregarVeterinariosCombo() {
        comboVeterinarios.removeAllItems();
        List<Veterinario> vets = vetController.listarTodosVeterinarios();
        for (Veterinario v : vets) {
            // Cria um item que armazena o id e o nome para exibição
            VeterinarioComboItem item = new VeterinarioComboItem(v.getId(), v.getNome());
            comboVeterinarios.addItem(item);
        }
    }

    // Carrega a agenda filtrada pelo id do veterinário selecionado
    private void carregarAgendaPorVeterinario(int vetId) {
        List<Agenda> lista = agendaDAO.listarPorVeterinario(vetId);
        atualizarTabela(lista);
    }

    // Atualiza o modelo da tabela com os dados da lista de Agenda.
    private void atualizarTabela(List<Agenda> lista) {
        modelo.setRowCount(0);
        for (Agenda agenda : lista) {
            modelo.addRow(new Object[]{
                agenda.getConsultaId(),
                agenda.getDataHora() != null ? dtf.format(agenda.getDataHora()) : "N/A",
                agenda.getStatus(),
                agenda.getNomeAnimal(),         // Obtido da view
                agenda.getNomeCliente(),          // Obtido da view
                agenda.getEnderecoCliente()       // Obtido da view
            });
        }
    }
}
