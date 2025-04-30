package com.clinica.view.ViewEmpresa;

import com.clinica.controller.AnimalController;
import com.clinica.controller.ClienteController;
import com.clinica.model.Animal;
import com.clinica.model.Cliente;
import com.clinica.report.ClientReportPDFGenerator;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class PainelGerenciarClientes extends JPanel {

    // Componentes para Clientes
    private JTable tabelaClientes;
    private DefaultTableModel modeloClientes;
    private JScrollPane scrollPaneClientes;
    private ClienteController clienteController = new ClienteController();
    
    // Componentes para Animais
    private JTable tabelaAnimais;
    private DefaultTableModel modeloAnimais;
    private JScrollPane scrollPaneAnimais;
    private JButton btnAdicionarAnimal, btnEditarAnimal, btnExcluirAnimal;
    private JLabel lblAnimaisTitulo;
    private AnimalController animalController = new AnimalController();
    private Cliente clienteSelecionado = null;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // Botões e campos de busca
    private JButton btnAdicionar, btnEditar, btnExcluir, btnAtualizar, btnRelatorio;
    private JTextField txtBuscaNome, txtBuscaId;

    public PainelGerenciarClientes() {
        setLayout(new BorderLayout(10, 10));

        // Painel Superior (Título + Busca)
        JPanel panelSuperior = new JPanel(new BorderLayout());
        
        // Título
        JLabel titulo = new JLabel("👥 Gerenciamento de Clientes", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titulo.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));
        panelSuperior.add(titulo, BorderLayout.NORTH);

        // Painel de Busca
        JPanel painelBusca = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        txtBuscaNome = new JTextField(15);
        JButton btnBuscarNome = new JButton("🔍 Buscar por Nome");
        txtBuscaId = new JTextField(5);
        JButton btnBuscarId = new JButton("🔎 Buscar por ID");
        JButton btnListarTodos = new JButton("📋 Listar Todos");

        painelBusca.add(new JLabel("Nome:"));
        painelBusca.add(txtBuscaNome);
        painelBusca.add(btnBuscarNome);
        painelBusca.add(new JLabel("ID:"));
        painelBusca.add(txtBuscaId);
        painelBusca.add(btnBuscarId);
        painelBusca.add(btnListarTodos);
        panelSuperior.add(painelBusca, BorderLayout.CENTER);

        add(panelSuperior, BorderLayout.NORTH);

        // Painel Central Dividido
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.6);

        // Tabela Clientes
        modeloClientes = new DefaultTableModel(new String[]{"ID", "Nome", "Endereço", "Email", "Telefone", "CPF"}, 0);
        tabelaClientes = new JTable(modeloClientes);
        tabelaClientes.setRowHeight(24);
        scrollPaneClientes = new JScrollPane(tabelaClientes);
        splitPane.setTopComponent(scrollPaneClientes);

        // Painel Animais
        JPanel panelAnimais = new JPanel(new BorderLayout());
        lblAnimaisTitulo = new JLabel("🐶 Animais do Cliente Selecionado:", SwingConstants.LEFT);
        lblAnimaisTitulo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panelAnimais.add(lblAnimaisTitulo, BorderLayout.NORTH);

        modeloAnimais = new DefaultTableModel(new String[]{"ID", "Nome", "Espécie", "Raça", "Nascimento"}, 0);
        tabelaAnimais = new JTable(modeloAnimais);
        scrollPaneAnimais = new JScrollPane(tabelaAnimais);
        panelAnimais.add(scrollPaneAnimais, BorderLayout.CENTER);

        // Botões Animais
        JPanel painelBotoesAnimais = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        btnAdicionarAnimal = new JButton("➕ Adicionar Animal");
        btnEditarAnimal = new JButton("✏️ Editar Animal");
        btnExcluirAnimal = new JButton("🗑️ Excluir Animal");
        painelBotoesAnimais.add(btnAdicionarAnimal);
        painelBotoesAnimais.add(btnEditarAnimal);
        painelBotoesAnimais.add(btnExcluirAnimal);
        panelAnimais.add(painelBotoesAnimais, BorderLayout.SOUTH);
        splitPane.setBottomComponent(panelAnimais);

        add(splitPane, BorderLayout.CENTER);

        // Painel de Botões Clientes
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnAdicionar = new JButton("➕ Adicionar");
        btnEditar = new JButton("✏️ Editar");
        btnExcluir = new JButton("🗑️ Excluir");
        btnAtualizar = new JButton("🔄 Atualizar");
        btnRelatorio = new JButton("📊 Gerar Relatório");

        painelBotoes.add(btnAdicionar);
        painelBotoes.add(btnEditar);
        painelBotoes.add(btnExcluir);
        painelBotoes.add(btnAtualizar);
        painelBotoes.add(btnRelatorio);
        add(painelBotoes, BorderLayout.SOUTH);

        // Listeners
        tabelaClientes.getSelectionModel().addListSelectionListener(e -> selecionarCliente());
        btnAdicionar.addActionListener(e -> adicionarCliente());
        btnEditar.addActionListener(e -> editarCliente());
        btnExcluir.addActionListener(e -> excluirCliente());
        btnAtualizar.addActionListener(e -> carregarClientes());
        btnRelatorio.addActionListener(e -> gerarRelatorioClientes());
        btnBuscarNome.addActionListener(e -> buscarPorNome());
        btnBuscarId.addActionListener(e -> buscarPorId());
        btnListarTodos.addActionListener(e -> carregarClientes());
        btnAdicionarAnimal.addActionListener(e -> adicionarAnimal());
        btnEditarAnimal.addActionListener(e -> editarAnimal());
        btnExcluirAnimal.addActionListener(e -> excluirAnimal());
        
        carregarClientes();
        habilitarBotoesAnimal(false);
    }

    // Métodos de Clientes
    private void carregarClientes() {
        modeloClientes.setRowCount(0);
        clienteController.listarTodosClientes().forEach(c -> 
            modeloClientes.addRow(new Object[]{
                c.getId(),
                c.getNome(),
                c.getEndereco(),
                c.getEmail(),
                c.getTelefone(),
                c.getCpf()
            })
        );
    }

    private void adicionarCliente() {
        ClienteFormDialog dialog = new ClienteFormDialog((JFrame) SwingUtilities.getWindowAncestor(this), null);
        dialog.setVisible(true);
        if (dialog.foiSalvo()) {
            Cliente novo = dialog.getCliente();
            if (dialog.getCep() != null && !dialog.getCep().isEmpty()) {
                clienteController.adicionarClienteComCep(
                    novo.getNome(), dialog.getCep(), novo.getEmail(),
                    novo.getTelefone(), novo.getCpf(), novo.getSenha()
                );
            } else {
                clienteController.adicionarCliente(
                    novo.getNome(), novo.getEndereco(), novo.getEmail(),
                    novo.getTelefone(), novo.getCpf(), novo.getSenha()
                );
            }
            carregarClientes();
        }
    }

    private void editarCliente() {
        int linha = tabelaClientes.getSelectedRow();
        if (linha >= 0) {
            int id = (int) modeloClientes.getValueAt(linha, 0);
            Cliente existente = clienteController.buscarClientePorId(id);
            ClienteFormDialog dialog = new ClienteFormDialog((JFrame) SwingUtilities.getWindowAncestor(this), existente);
            dialog.setVisible(true);
            if (dialog.foiSalvo()) {
                Cliente atualizado = dialog.getCliente();
                clienteController.atualizarCliente(
                    id, atualizado.getNome(), atualizado.getEndereco(),
                    atualizado.getEmail(), atualizado.getTelefone(),
                    atualizado.getCpf(), atualizado.getSenha()
                );
                carregarClientes();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Selecione um cliente para editar.");
        }
    }

    private void excluirCliente() {
        int linha = tabelaClientes.getSelectedRow();
        if (linha >= 0) {
            int id = (int) modeloClientes.getValueAt(linha, 0);
            int confirm = JOptionPane.showConfirmDialog(this,
                "Deseja excluir este cliente?", "Confirmação", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                clienteController.removerCliente(id);
                carregarClientes();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Selecione um cliente para excluir.");
        }
    }

    // Métodos de Animais
    private void selecionarCliente() {
        int linha = tabelaClientes.getSelectedRow();
        if (linha >= 0) {
            int clienteId = (int) modeloClientes.getValueAt(linha, 0);
            clienteSelecionado = clienteController.buscarClientePorId(clienteId);
            carregarAnimaisDoCliente(clienteId);
            habilitarBotoesAnimal(true);
            lblAnimaisTitulo.setText("🐶 Animais de: " + clienteSelecionado.getNome());
        } else {
            clienteSelecionado = null;
            modeloAnimais.setRowCount(0);
            habilitarBotoesAnimal(false);
            lblAnimaisTitulo.setText("🐶 Animais do Cliente Selecionado:");
        }
    }

    private void carregarAnimaisDoCliente(int clienteId) {
        modeloAnimais.setRowCount(0);
        animalController.listarAnimaisPorCliente(clienteId).forEach(a ->
            modeloAnimais.addRow(new Object[]{
                a.getId(),
                a.getNome(),
                a.getEspecie(),
                a.getRaca(),
                a.getDataNascimento() != null ? a.getDataNascimento().format(dateFormatter) : "N/A"
            })
        );
    }

    private void adicionarAnimal() {
        if (clienteSelecionado != null) {
            abrirDialogAnimal(null);
        }
    }

    private void editarAnimal() {
        int linha = tabelaAnimais.getSelectedRow();
        if (linha >= 0 && clienteSelecionado != null) {
            int animalId = (int) modeloAnimais.getValueAt(linha, 0);
            abrirDialogAnimal(animalController.buscarAnimalPorId(animalId));
        }
    }

    private void excluirAnimal() {
        int linha = tabelaAnimais.getSelectedRow();
        if (linha >= 0) {
            int animalId = (int) modeloAnimais.getValueAt(linha, 0);
            int confirm = JOptionPane.showConfirmDialog(this,
                "Deseja excluir este animal?", "Confirmação", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                animalController.removerAnimal(animalId);
                carregarAnimaisDoCliente(clienteSelecionado.getId());
            }
        }
    }

    private void abrirDialogAnimal(Animal animal) {
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), true);
        JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));
        JTextField txtNome = new JTextField(animal != null ? animal.getNome() : "");
        JTextField txtEspecie = new JTextField(animal != null ? animal.getEspecie() : "");
        JTextField txtRaca = new JTextField(animal != null ? animal.getRaca() : "");
        JTextField txtNascimento = new JTextField(animal != null && animal.getDataNascimento() != null ?
            animal.getDataNascimento().format(dateFormatter) : "");

        panel.add(new JLabel("Nome:"));
        panel.add(txtNome);
        panel.add(new JLabel("Espécie:"));
        panel.add(txtEspecie);
        panel.add(new JLabel("Raça:"));
        panel.add(txtRaca);
        panel.add(new JLabel("Nascimento (dd/MM/yyyy):"));
        panel.add(txtNascimento);

        JButton btnSalvar = new JButton("Salvar");
        btnSalvar.addActionListener(e -> {
            try {
                LocalDate nascimento = !txtNascimento.getText().isEmpty() ?
                    LocalDate.parse(txtNascimento.getText(), dateFormatter) : null;
                
                Animal novoAnimal = new Animal(
                    txtNome.getText(),
                    txtEspecie.getText(),
                    txtRaca.getText(),
                    nascimento,
                    clienteSelecionado.getId()
                );

                if (animal != null) {
                    novoAnimal.setId(animal.getId());
                    animalController.atualizarAnimalObj(novoAnimal);
                } else {
                    animalController.adicionarAnimalObj(novoAnimal);
                }
                carregarAnimaisDoCliente(clienteSelecionado.getId());
                dialog.dispose();
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(dialog, "Formato de data inválido!");
            }
        });

        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(btnSalvar, BorderLayout.SOUTH);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    // Utilitários
    private void habilitarBotoesAnimal(boolean habilitar) {
        btnAdicionarAnimal.setEnabled(habilitar);
        btnEditarAnimal.setEnabled(habilitar && tabelaAnimais.getRowCount() > 0);
        btnExcluirAnimal.setEnabled(habilitar && tabelaAnimais.getRowCount() > 0);
    }

    private void buscarPorNome() {
        String nome = txtBuscaNome.getText().trim();
        if (!nome.isEmpty()) {
            atualizarTabelaClientes(clienteController.buscarClientesPorNome(nome));
        }
    }

    private void buscarPorId() {
        try {
            int id = Integer.parseInt(txtBuscaId.getText().trim());
            Cliente c = clienteController.buscarClientePorId(id);
            if (c != null) atualizarTabelaClientes(List.of(c));
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "ID inválido!");
        }
    }

    private void atualizarTabelaClientes(List<Cliente> clientes) {
        modeloClientes.setRowCount(0);
        clientes.forEach(c -> modeloClientes.addRow(new Object[]{
            c.getId(), c.getNome(), c.getEndereco(), c.getEmail(), c.getTelefone(), c.getCpf()
        }));
    }

    private void gerarRelatorioClientes() {
        StringBuilder content = new StringBuilder();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        
        clienteController.listarTodosClientes().forEach(c -> 
            content.append(String.format(
                "ID: %d | Nome: %s | Endereço: %s | Email: %s | Telefone: %s | CPF: %s\n",
                c.getId(), c.getNome(), c.getEndereco(), c.getEmail(), c.getTelefone(), c.getCpf()
            ))
        );

        new ClientReportPDFGenerator(
            "relatorio_clientes_" + sdf.format(new Date()) + ".pdf",
            content.toString()
        ).generateReport();

        JOptionPane.showMessageDialog(this, "Relatório gerado com sucesso!");
    }
}