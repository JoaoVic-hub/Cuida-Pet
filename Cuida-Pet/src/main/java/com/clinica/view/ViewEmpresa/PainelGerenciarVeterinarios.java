package com.clinica.view.ViewEmpresa;

import com.clinica.controller.VeterinarioController;
import com.clinica.model.Veterinario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PainelGerenciarVeterinarios extends JPanel {
    private JTable tabela;
    private DefaultTableModel modelo;
    private JScrollPane scrollPane;
    private VeterinarioController controller = new VeterinarioController();

    public PainelGerenciarVeterinarios() {
        setLayout(new BorderLayout());

        JLabel titulo = new JLabel("Gerenciamento de Veterinários", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titulo.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(titulo, BorderLayout.NORTH);

        modelo = new DefaultTableModel(
                new String[]{"ID", "Nome", "Email", "Telefone", "CPF", "CRMV", "Especialidade"}, 
                0
        );
        tabela = new JTable(modelo);
        tabela.setRowHeight(24);
        tabela.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabela.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        scrollPane = new JScrollPane(tabela);
        add(scrollPane, BorderLayout.CENTER);

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


        btnAdicionar.addActionListener(e -> {
            VeterinarioFormDialog dialog = new VeterinarioFormDialog((JFrame) SwingUtilities.getWindowAncestor(this), null);
            dialog.setVisible(true);
            if (dialog.foiSalvo()) {
                Veterinario novo = dialog.getVeterinario();
                controller.adicionarVeterinario(
                        novo.getNome(),
                        novo.getEmail(),
                        novo.getTelefone(),
                        novo.getCpf(),
                        novo.getSenha(),
                        novo.getCrmv(),
                        novo.getEspecialidade()
                );
                carregarVeterinarios();
            }
        });

        btnEditar.addActionListener(e -> {
            int linha = tabela.getSelectedRow();
            if (linha >= 0) {
                int id = (int) modelo.getValueAt(linha, 0);
                Veterinario existente = controller.buscarVeterinarioPorId(id);
                VeterinarioFormDialog dialog = new VeterinarioFormDialog((JFrame) SwingUtilities.getWindowAncestor(this), existente);
                dialog.setVisible(true);
                if (dialog.foiSalvo()) {
                    Veterinario atualizado = dialog.getVeterinario();
                    controller.atualizarVeterinario(
                            id,
                            atualizado.getNome(),
                            atualizado.getEmail(),
                            atualizado.getTelefone(),
                            atualizado.getCpf(),
                            atualizado.getSenha(),
                            atualizado.getCrmv(),
                            atualizado.getEspecialidade()
                    );
                    carregarVeterinarios();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Selecione um veterinário para editar.");
            }
        });

        btnExcluir.addActionListener(e -> {
            int linha = tabela.getSelectedRow();
            if (linha >= 0) {
                int id = (int) modelo.getValueAt(linha, 0);
                int confirm = JOptionPane.showConfirmDialog(this, "Deseja excluir este veterinário?", "Confirmação", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    controller.removerVeterinario(id);
                    carregarVeterinarios();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Selecione um veterinário para excluir.");
            }
        });

        btnAtualizar.addActionListener(e -> carregarVeterinarios());

        carregarVeterinarios();
    }

    private void carregarVeterinarios() {
        List<Veterinario> vets = controller.listarTodosVeterinarios();
        atualizarTabela(vets);
    }

    private void atualizarTabela(List<Veterinario> lista) {
        modelo.setRowCount(0);
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
}
