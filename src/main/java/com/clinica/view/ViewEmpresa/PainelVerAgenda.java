package com.clinica.view.ViewEmpresa;

import com.clinica.facade.ClinicaFacade;
import com.clinica.model.Agenda;
import com.clinica.model.Veterinario;
import com.clinica.observer.DataObserver;
import com.clinica.observer.DataType; 
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class PainelVerAgenda extends JPanel implements DataObserver {

    private JTable tabela;
    private DefaultTableModel modelo;
    private JScrollPane scrollPane;
    private JComboBox<VeterinarioComboItem> comboVeterinarios;
    private DateTimeFormatter dtf;
    private ClinicaFacade facade = ClinicaFacade.getInstance();

    public PainelVerAgenda() {
        dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- UI ---
        JPanel panelTopo = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JLabel lblVet = new JLabel("Selecione o Veterinário:");
        lblVet.setFont(new Font("Segoe UI", Font.BOLD, 14));
        comboVeterinarios = new JComboBox<>();
        comboVeterinarios.setPreferredSize(new Dimension(300, 28));
        comboVeterinarios.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panelTopo.add(lblVet);
        panelTopo.add(comboVeterinarios);
        add(panelTopo, BorderLayout.NORTH);

        modelo = new DefaultTableModel(new String[]{
            "ID Consulta", "Data/Hora", "Status", "Animal", "Cliente"
        }, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabela = new JTable(modelo);
        tabela.setRowHeight(25);
        tabela.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tabela.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        scrollPane = new JScrollPane(tabela);
        add(scrollPane, BorderLayout.CENTER);

        // --- Listeners ---
        comboVeterinarios.addActionListener(e -> carregarAgendaDoVeterinarioSelecionado());

        // --- Inicialização ---
        carregarVeterinariosCombo(); // Carrega o combo
        // Seleciona o primeiro e dispara o carregamento da agenda se houver vets
        if (comboVeterinarios.getItemCount() > 0) {
             comboVeterinarios.setSelectedIndex(0); // Dispara o ActionListener
        } else {
             modelo.setRowCount(0); // Limpa tabela se não houver vets
        }


        // --- REGISTRAR COMO OBSERVER ---
        facade.removeObserver(this);
        facade.addObserver(this);
        // -----------------------------
    }

    // --- Implementação do Método update() ---
    @Override
    public void update(DataType typeChanged) {
        System.out.println("PainelVerAgenda notificado sobre: " + typeChanged); // Log
        // Recarrega se Consultas, Veterinários, Clientes ou Animais mudarem
        if (typeChanged == DataType.CONSULTA ||
            typeChanged == DataType.VETERINARIO ||
            typeChanged == DataType.CLIENTE ||
            typeChanged == DataType.ANIMAL ||
            typeChanged == DataType.AGENDA) 
        {
            System.out.println("-> Recarregando agenda...");
            // Recarrega a lista de veterinários no combo (caso um tenha sido add/removido)
            // Mantém a seleção atual se possível
             Object selectedItem = comboVeterinarios.getSelectedItem();
             carregarVeterinariosCombo();
             if (selectedItem != null) {
                 comboVeterinarios.setSelectedItem(selectedItem); // Tenta manter a seleção
             }
             // Recarrega a agenda para o veterinário atualmente selecionado
            carregarAgendaDoVeterinarioSelecionado();
        }
    }

    // --- Métodos Auxiliares ---

    private void carregarVeterinariosCombo() {
        // Guarda o ID selecionado antes de limpar
        int selectedId = -1;
        if (comboVeterinarios.getSelectedItem() instanceof VeterinarioComboItem) {
            selectedId = ((VeterinarioComboItem) comboVeterinarios.getSelectedItem()).getId();
        }

        comboVeterinarios.removeAllItems();
        try {
            List<Veterinario> vets = facade.listarTodosVeterinarios();
            if (vets != null) {
                for (Veterinario v : vets) {
                    comboVeterinarios.addItem(new VeterinarioComboItem(v.getId(), v.getNome()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace(); JOptionPane.showMessageDialog(this, "Erro ao carregar veterinários: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }

        // Tenta reselecionar o item que estava selecionado
        if (selectedId > 0) {
            for (int i = 0; i < comboVeterinarios.getItemCount(); i++) {
                if (comboVeterinarios.getItemAt(i).getId() == selectedId) {
                    comboVeterinarios.setSelectedIndex(i);
                    break;
                }
            }
        } else if (comboVeterinarios.getItemCount() > 0) {
            // Se nada estava selecionado (ou a seleção anterior sumiu), seleciona o primeiro
            comboVeterinarios.setSelectedIndex(0);
        }
    }

    // Carrega a agenda baseado no item selecionado no ComboBox
    private void carregarAgendaDoVeterinarioSelecionado() {
         Object selectedItem = comboVeterinarios.getSelectedItem();
         if (selectedItem instanceof VeterinarioComboItem) {
             int vetId = ((VeterinarioComboItem) selectedItem).getId();
             if (vetId > 0) { // Carrega apenas se for um ID válido
                 carregarAgendaPorVeterinario(vetId);
             } else {
                 modelo.setRowCount(0); // Limpa a tabela se for "-- Selecione --" ou similar
             }
         } else {
              modelo.setRowCount(0); // Limpa se nada estiver selecionado
         }
    }

    // Carrega a agenda de um ID de veterinário específico
    private void carregarAgendaPorVeterinario(int vetId) {
        modelo.setRowCount(0); // Limpa antes de carregar
        try {
            List<Agenda> lista = facade.listarAgendaPorVeterinario(vetId); // Usa Facade
            atualizarTabela(lista); // Atualiza a UI
        } catch (Exception e) {
            e.printStackTrace(); JOptionPane.showMessageDialog(this, "Erro ao carregar agenda: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Atualiza o modelo da tabela com os dados da agenda
    private void atualizarTabela(List<Agenda> lista) {
        // modelo.setRowCount(0); // Limpeza já feita em carregarAgendaPorVeterinario
        if (dtf == null) { dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"); }

        if (lista != null) {
             for (Agenda agenda : lista) {
                 if (agenda == null) continue;
                 String dataFormatada = (agenda.getDataHora() != null) ? dtf.format(agenda.getDataHora()) : "N/A";
                 String nomeAnimal = agenda.getNomeAnimal() != null ? agenda.getNomeAnimal() : "-";
                 String nomeCliente = agenda.getNomeCliente() != null ? agenda.getNomeCliente() : "N/A";
                 String status = agenda.getStatus() != null ? agenda.getStatus() : "N/A";
                 modelo.addRow(new Object[]{
                     agenda.getConsultaId(), dataFormatada, status, nomeAnimal, nomeCliente
                 });
            }
        }
        modelo.fireTableDataChanged(); // Notifica
    }
}
