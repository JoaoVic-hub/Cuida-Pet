package com.clinica.view.ViewEmpresa;

import com.clinica.controller.ClienteController;
import com.clinica.model.Cliente;
import com.clinica.report.ClientReportPDFGenerator;
import com.clinica.report.PDFReportGenerator;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class PainelGerenciarClientes extends JPanel {

    private JTable tabela;
    private DefaultTableModel modelo;
    private JScrollPane scrollPane;
    private ClienteController controller = new ClienteController();

    public PainelGerenciarClientes() {
        setLayout(new BorderLayout());

        JLabel titulo = new JLabel("👥 Gerenciamento de Clientes", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titulo.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(titulo, BorderLayout.NORTH);

        // Tabela para exibir os clientes
        modelo = new DefaultTableModel(new String[]{"ID", "Nome", "Endereço", "Email", "Telefone", "CPF"}, 0);
        tabela = new JTable(modelo);
        tabela.setRowHeight(24);
        tabela.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabela.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

        scrollPane = new JScrollPane(tabela);
        scrollPane.setVisible(false);
        add(scrollPane, BorderLayout.CENTER);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton btnAdicionar = new JButton("➕ Adicionar");
        JButton btnEditar = new JButton("✏️ Editar");
        JButton btnExcluir = new JButton("🗑️ Excluir");
        JButton btnAtualizar = new JButton("🔄 Atualizar");
        JButton btnRelatorio = new JButton("📊 Gerar Relatório");

        painelBotoes.add(btnAdicionar);
        painelBotoes.add(btnEditar);
        painelBotoes.add(btnExcluir);
        painelBotoes.add(btnAtualizar);
        painelBotoes.add(btnRelatorio);
        add(painelBotoes, BorderLayout.SOUTH);

        // Painel de busca
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

  
        btnAdicionar.addActionListener(e -> {

            ClienteFormDialog dialog = new ClienteFormDialog((JFrame) SwingUtilities.getWindowAncestor(this), null);
            dialog.setVisible(true);

            if (dialog.foiSalvo()) {
                Cliente novo = dialog.getCliente();

                if (dialog.getCep() != null && !dialog.getCep().isEmpty()) {
 
                    controller.adicionarClienteComCep(
                            novo.getNome(),
                            dialog.getCep(),
                            novo.getEmail(),
                            novo.getTelefone(),
                            novo.getCpf(),
                            novo.getSenha()
                    );
                } else {
                    controller.adicionarCliente(
                            novo.getNome(),
                            novo.getEndereco(),
                            novo.getEmail(),
                            novo.getTelefone(),
                            novo.getCpf(),
                            novo.getSenha() 
                    );
                }

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
                    controller.atualizarCliente(
                            id,
                            atualizado.getNome(),
                            atualizado.getEndereco(),
                            atualizado.getEmail(),
                            atualizado.getTelefone(),
                            atualizado.getCpf(),
                            atualizado.getSenha() 
                    );
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
                int confirm = JOptionPane.showConfirmDialog(this,
                        "Deseja excluir este cliente?", "Confirmação", JOptionPane.YES_NO_OPTION);
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

  
        btnRelatorio.addActionListener(e -> {

            StringBuilder reportContent = new StringBuilder();
            List<Cliente> clientes = controller.listarTodosClientes();
            for (Cliente c : clientes) {
                reportContent.append("ID: ").append(c.getId())
                             .append(" - Nome: ").append(c.getNome())
                             .append(" - Endereço: ").append(c.getEndereco())
                             .append(" - Email: ").append(c.getEmail())
                             .append(" - Telefone: ").append(c.getTelefone())
                             .append(" - CPF: ").append(c.getCpf())
                             .append("\n");
            }

            String outputPath = "relatorio_clientes.pdf";

            PDFReportGenerator report = new ClientReportPDFGenerator(outputPath, reportContent.toString());
            report.generateReport(); 

            JOptionPane.showMessageDialog(this, "Relatório gerado com sucesso em:\n" + outputPath);
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
                c.getId(),
                c.getNome(),
                c.getEndereco(),
                c.getEmail(),
                c.getTelefone(),
                c.getCpf()
            });
        }
    }

    private void gerarRelatorioClientes() {
        List<Cliente> clientes = controller.listarTodosClientes();
        int totalClientes = clientes.size();

        long clientesComEmail = clientes.stream().filter(c -> c.getEmail() != null && !c.getEmail().isEmpty()).count();
        long clientesComTelefone = clientes.stream().filter(c -> c.getTelefone() != null && !c.getTelefone().isEmpty()).count();

        String dataRelatorio = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date());

        String relatorio =
            "📋 RELATÓRIO DE CLIENTES - CLÍNICA VETERINÁRIA\n" +
            "Data: " + dataRelatorio + "\n\n" +
            "📊 RESUMO ESTATÍSTICO:\n" +
            "----------------------------------------\n" +
            "• Total de clientes cadastrados: " + totalClientes + "\n" +
            "• Clientes com e-mail cadastrado: " + clientesComEmail + " (" +
                (totalClientes > 0 ? (clientesComEmail * 100 / totalClientes) : 0) + "%)\n" +
            "• Clientes com telefone cadastrado: " + clientesComTelefone + " (" +
                (totalClientes > 0 ? (clientesComTelefone * 100 / totalClientes) : 0) + "%)\n\n" +
            "🔄 ÚLTIMA ATUALIZAÇÃO:\n" +
            "----------------------------------------\n" +
            "Relatório gerado automaticamente pelo sistema.\n";

        JTextArea textArea = new JTextArea(relatorio);
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 400));

        JOptionPane.showMessageDialog(this, scrollPane, "Relatório de Clientes", JOptionPane.INFORMATION_MESSAGE);
    }
}
