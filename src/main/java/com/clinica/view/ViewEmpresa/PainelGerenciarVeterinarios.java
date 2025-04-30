package com.clinica.view.ViewEmpresa;

import com.clinica.facade.ClinicaFacade; // Importar Facade
import com.clinica.model.Veterinario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PainelGerenciarVeterinarios extends JPanel {
    private JTable tabela;
    private DefaultTableModel modelo;
    private JScrollPane scrollPane;
    // REMOVER: private VeterinarioController controller = new VeterinarioController();

    // --- USAR A FACADE ---
    private ClinicaFacade facade = ClinicaFacade.getInstance();
    // ---------------------

    public PainelGerenciarVeterinarios() {
        setLayout(new BorderLayout(10, 10)); // Adicionado espaçamento

        JLabel titulo = new JLabel("Gerenciamento de Veterinários", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titulo.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(titulo, BorderLayout.NORTH);

        // Configuração da tabela
        modelo = new DefaultTableModel(
                new String[]{"ID", "Nome", "Email", "Telefone", "CPF", "CRMV", "Especialidade"},
                0
        ){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Tabela não editável
            }
        };
        tabela = new JTable(modelo);
        tabela.setRowHeight(24);
        tabela.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabela.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        scrollPane = new JScrollPane(tabela);
        add(scrollPane, BorderLayout.CENTER);

        // Painel de botões
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton btnAdicionar = new JButton("➕ Adicionar");
        JButton btnEditar = new JButton("✏️ Editar");
        JButton btnExcluir = new JButton("🗑️ Excluir");
        JButton btnAtualizar = new JButton("🔄 Atualizar");
        painelBotoes.add(btnAdicionar);
        painelBotoes.add(btnEditar);
        painelBotoes.add(btnExcluir);
        painelBotoes.add(btnAtualizar);
        add(painelBotoes, BorderLayout.SOUTH);

        // --- Listeners (AJUSTAR CHAMADAS) ---
        btnAdicionar.addActionListener(e -> {
            // O VeterinarioFormDialog não precisa da facade diretamente
            VeterinarioFormDialog dialog = new VeterinarioFormDialog((JFrame) SwingUtilities.getWindowAncestor(this), null);
            dialog.setVisible(true);
            if (dialog.foiSalvo()) {
                Veterinario novo = dialog.getVeterinario();
                // USA A FACADE para adicionar
                facade.adicionarVeterinario(
                        novo.getNome(),
                        novo.getEmail(),
                        novo.getTelefone(),
                        novo.getCpf(),
                        novo.getSenha(), // Cuidado com senha em texto plano
                        novo.getCrmv(),
                        novo.getEspecialidade()
                );
                carregarVeterinarios(); // Recarrega usando a facade
            }
        });

        btnEditar.addActionListener(e -> {
            int linha = tabela.getSelectedRow();
            if (linha >= 0) {
                int id = (int) modelo.getValueAt(linha, 0);
                // USA A FACADE para buscar
                Veterinario existente = facade.buscarVeterinarioPorId(id);
                 if (existente == null) {
                     JOptionPane.showMessageDialog(this, "Veterinário não encontrado para edição (ID: " + id + "). Atualize a lista.", "Erro", JOptionPane.ERROR_MESSAGE);
                     return;
                 }
                VeterinarioFormDialog dialog = new VeterinarioFormDialog((JFrame) SwingUtilities.getWindowAncestor(this), existente);
                dialog.setVisible(true);
                if (dialog.foiSalvo()) {
                    Veterinario atualizado = dialog.getVeterinario();
                    // USA A FACADE para atualizar
                    facade.atualizarVeterinario(
                            id, // Passa o ID original
                            atualizado.getNome(),
                            atualizado.getEmail(),
                            atualizado.getTelefone(),
                            atualizado.getCpf(),
                            atualizado.getSenha(), // Cuidado
                            atualizado.getCrmv(),
                            atualizado.getEspecialidade()
                    );
                    carregarVeterinarios(); // Recarrega usando a facade
                }
            } else {
                JOptionPane.showMessageDialog(this, "Selecione um veterinário para editar.");
            }
        });

        btnExcluir.addActionListener(e -> {
            int linha = tabela.getSelectedRow();
            if (linha >= 0) {
                int id = (int) modelo.getValueAt(linha, 0);
                 // Verifica se existe antes
                 Veterinario existente = facade.buscarVeterinarioPorId(id);
                  if (existente == null) {
                     JOptionPane.showMessageDialog(this, "Veterinário não encontrado para exclusão (ID: " + id + "). Atualize a lista.", "Erro", JOptionPane.ERROR_MESSAGE);
                     return;
                 }
                int confirm = JOptionPane.showConfirmDialog(this,
                        "Deseja excluir o veterinário '" + existente.getNome() + "'?",
                        "Confirmar Exclusão", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (confirm == JOptionPane.YES_OPTION) {
                    // USA A FACADE para remover
                    facade.removerVeterinario(id);
                    carregarVeterinarios(); // Recarrega usando a facade
                }
            } else {
                JOptionPane.showMessageDialog(this, "Selecione um veterinário para excluir.");
            }
        });

        btnAtualizar.addActionListener(e -> carregarVeterinarios()); // Chama método que usa facade

        carregarVeterinarios(); // Carrega dados iniciais usando a facade
    }

    // AJUSTADO: Usa Facade
    private void carregarVeterinarios() {
        try {
            // USA A FACADE
            List<Veterinario> vets = facade.listarTodosVeterinarios();
            atualizarTabela(vets); // Método auxiliar não muda, só recebe dados da facade
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao carregar veterinários: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Método auxiliar para preencher a tabela (não precisa mudar)
    private void atualizarTabela(List<Veterinario> lista) {
        modelo.setRowCount(0); // Limpa a tabela
        if (lista != null) {
            for (Veterinario vet : lista) {
                modelo.addRow(new Object[]{
                        vet.getId(),
                        vet.getNome(),
                        vet.getEmail(),
                        vet.getTelefone(),
                        vet.getCpf(),
                        vet.getCrmv(),
                        vet.getEspecialidade()
                });
            }
        }
        // Notifica a tabela explicitamente (Boa prática, embora DefaultTableModel costume fazer isso)
        modelo.fireTableDataChanged();
    }
}