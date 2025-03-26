package com.clinica.view.ViewEmpresa;

import com.clinica.controller.ClienteController;
import com.clinica.model.Cliente;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PainelGerenciarClientes extends JPanel {

    private JTable tabela;
    private DefaultTableModel modelo;
    private JScrollPane scrollPane;
    private ClienteController controller = new ClienteController();

    public PainelGerenciarClientes() {
        setLayout(new BorderLayout());

        // Título
        JLabel titulo = new JLabel("👥 Gerenciamento de Clientes", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titulo.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(titulo, BorderLayout.NORTH);

        // Modelo da tabela
        modelo = new DefaultTableModel(new String[]{"ID", "Nome", "Endereço", "Email", "Telefone", "CPF"}, 0);
        tabela = new JTable(modelo);
        tabela.setRowHeight(24);
        tabela.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabela.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

        scrollPane = new JScrollPane(tabela);
        scrollPane.setVisible(false); // 👈 invisível por padrão
        add(scrollPane, BorderLayout.CENTER);

        // Painel de botões (baixo)
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

        // Painel de busca (topo)
        JPanel painelBusca = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        JTextField txtBuscaNome = new JTextField(15);
        JButton btnBuscarNome = new JButton("🔍 Buscar por Nome");

        JTextField txtBuscaId = new JTextField(5);
        JButton btnBuscarId = new JButton("🔎 Buscar por ID");

        JButton btnListarTodos = new JButton("📋 Listar Todos");

        painelBusca.add(new JLabel("Nome:"));
        painelBusca.add(txtBuscaNome);
        painelBusca.add(btnBuscarNome);

        painelBusca.add(new JLabel("ID:"));
        painelBusca.add(txtBuscaId);
        painelBusca.add(btnBuscarId);

        painelBusca.add(btnListarTodos);

        add(painelBusca, BorderLayout.NORTH);

        // Botões com ações

        btnAdicionar.addActionListener(e -> {
            ClienteFormDialog dialog = new ClienteFormDialog((JFrame) SwingUtilities.getWindowAncestor(this), null);
            dialog.setVisible(true);
            if (dialog.foiSalvo()) {
                Cliente novo = dialog.getCliente();
                controller.adicionarCliente(novo.getNome(), novo.getEndereco(), novo.getEmail(), novo.getTelefone(), novo.getCpf());
                carregarClientes();
                scrollPane.setVisible(true);
                revalidate();
            }
        });

        btnEditar.addActionListener(e -> {
            int linha = tabela.getSelectedRow();
            if (linha >= 0) {
                int id = (int) modelo.getValueAt(linha, 0);
                Cliente existente = controller.buscarClientePorId(id);
                ClienteFormDialog dialog = new ClienteFormDialog((JFrame) SwingUtilities.getWindowAncestor(this), existente);
                dialog.setVisible(true);
                if (dialog.foiSalvo()) {
                    Cliente atualizado = dialog.getCliente();
                    controller.atualizarCliente(id, atualizado.getNome(), atualizado.getEndereco(), atualizado.getEmail(), atualizado.getTelefone(), atualizado.getCpf());
                    carregarClientes();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Selecione um cliente para editar.");
            }
        });

        btnExcluir.addActionListener(e -> {
            int linha = tabela.getSelectedRow();
            if (linha >= 0) {
                int id = (int) modelo.getValueAt(linha, 0);
                int confirm = JOptionPane.showConfirmDialog(this, "Deseja excluir este cliente?", "Confirmação", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    controller.removerCliente(id);
                    carregarClientes();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Selecione um cliente para excluir.");
            }
        });

        btnAtualizar.addActionListener(e -> {
            carregarClientes();
            scrollPane.setVisible(true);
            revalidate();
        });

        btnBuscarNome.addActionListener(e -> {
            String nome = txtBuscaNome.getText().trim();
            if (!nome.isEmpty()) {
                List<Cliente> encontrados = controller.buscarClientesPorNome(nome);
                atualizarTabela(encontrados);
                scrollPane.setVisible(true);
                revalidate();
            }
        });

        btnBuscarId.addActionListener(e -> {
            try {
                int id = Integer.parseInt(txtBuscaId.getText().trim());
                Cliente c = controller.buscarClientePorId(id);
                if (c != null) {
                    atualizarTabela(List.of(c));
                    scrollPane.setVisible(true);
                    revalidate();
                } else {
                    JOptionPane.showMessageDialog(this, "Nenhum cliente encontrado com esse ID.");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Digite um ID válido.");
            }
        });

        btnListarTodos.addActionListener(e -> {
            carregarClientes();
            scrollPane.setVisible(true);
            revalidate();
        });
    }

    private void carregarClientes() {
        List<Cliente> clientes = controller.listarTodosClientes();
        atualizarTabela(clientes);
    }

    private void atualizarTabela(List<Cliente> lista) {
        modelo.setRowCount(0);
        for (Cliente c : lista) {
            modelo.addRow(new Object[]{
                c.getId(), c.getNome(), c.getEndereco(),
                c.getEmail(), c.getTelefone(), c.getCpf()
            });
        }
    }
}
