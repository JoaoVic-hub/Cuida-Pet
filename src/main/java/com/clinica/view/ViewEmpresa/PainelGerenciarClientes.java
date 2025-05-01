package com.clinica.view.ViewEmpresa;

import com.clinica.facade.ClinicaFacade;
import com.clinica.model.Animal;
import com.clinica.model.Cliente;
import com.clinica.observer.DataObserver;
import com.clinica.observer.DataType;
import com.clinica.report.ClientReportPDFGenerator; 
import java.awt.*; 
import java.text.SimpleDateFormat; 
import java.time.format.DateTimeFormatter;
import java.util.Collections; 
import java.util.Date;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class PainelGerenciarClientes extends JPanel implements DataObserver {

    // --- Componentes UI e Variáveis (sem mudanças) ---
    private JTable tabelaClientes;
    private DefaultTableModel modeloClientes;
    private JScrollPane scrollPaneClientes;
    private JTable tabelaAnimais;
    private DefaultTableModel modeloAnimais;
    private JScrollPane scrollPaneAnimais;
    private JButton btnAdicionarAnimal, btnEditarAnimal, btnExcluirAnimal;
    private JLabel lblAnimaisTitulo;
    private JButton btnAdicionar, btnEditar, btnExcluir, btnAtualizar, btnRelatorio; 
    private JTextField txtBuscaNome, txtBuscaId;
    private JButton btnBuscarNome, btnBuscarId, btnListarTodos;
    private Cliente clienteSelecionado = null;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private ClinicaFacade facade = ClinicaFacade.getInstance();

    public PainelGerenciarClientes() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // === Painel Superior ===
        JPanel panelSuperior = new JPanel(new BorderLayout(0, 5));
        JLabel titulo = new JLabel("👥 Gerenciamento de Clientes", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        panelSuperior.add(titulo, BorderLayout.NORTH);
        JPanel painelBusca = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        txtBuscaNome = new JTextField(15);
        btnBuscarNome = new JButton("🔍 Buscar por Nome");
        txtBuscaId = new JTextField(5);
        btnBuscarId = new JButton("🔎 Buscar por ID");
        btnListarTodos = new JButton("📋 Listar Todos");
        painelBusca.add(new JLabel("Nome:"));
        painelBusca.add(txtBuscaNome);
        painelBusca.add(btnBuscarNome);
        painelBusca.add(new JLabel("ID:"));
        painelBusca.add(txtBuscaId);
        painelBusca.add(btnBuscarId);
        painelBusca.add(btnListarTodos);
        panelSuperior.add(painelBusca, BorderLayout.CENTER);
        add(panelSuperior, BorderLayout.NORTH);

        // === Painel Central Dividido ===
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.6);
        splitPane.setBorder(null);
        JPanel panelTabelaClientes = new JPanel(new BorderLayout());
        panelTabelaClientes.setBorder(BorderFactory.createTitledBorder("Clientes Cadastrados"));
        modeloClientes = new DefaultTableModel(new String[]{"ID", "Nome", "Endereço", "Email", "Telefone", "CPF"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabelaClientes = new JTable(modeloClientes);
        tabelaClientes.setRowHeight(24);
        tabelaClientes.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabelaClientes.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabelaClientes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        scrollPaneClientes = new JScrollPane(tabelaClientes);
        panelTabelaClientes.add(scrollPaneClientes, BorderLayout.CENTER);
        splitPane.setTopComponent(panelTabelaClientes);
        JPanel panelAnimais = new JPanel(new BorderLayout(5, 5));
        panelAnimais.setBorder(BorderFactory.createTitledBorder("Animais do Cliente"));
        lblAnimaisTitulo = new JLabel("Selecione um cliente para ver os animais", SwingConstants.LEFT);
        lblAnimaisTitulo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblAnimaisTitulo.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 0));
        panelAnimais.add(lblAnimaisTitulo, BorderLayout.NORTH);
        modeloAnimais = new DefaultTableModel(new String[]{"ID", "Nome", "Espécie", "Raça", "Nascimento"}, 0){
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabelaAnimais = new JTable(modeloAnimais);
        tabelaAnimais.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabelaAnimais.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        scrollPaneAnimais = new JScrollPane(tabelaAnimais);
        panelAnimais.add(scrollPaneAnimais, BorderLayout.CENTER);
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

        // === Painel de Botões Inferior ===
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnAdicionar = new JButton("➕ Adicionar Cliente");
        btnEditar = new JButton("✏️ Editar Cliente");
        btnExcluir = new JButton("🗑️ Excluir Cliente");
        btnRelatorio = new JButton("📊 Gerar Relatório PDF");
        painelBotoes.add(btnAdicionar);
        painelBotoes.add(btnEditar);
        painelBotoes.add(btnExcluir);
        painelBotoes.add(btnRelatorio);
        add(painelBotoes, BorderLayout.SOUTH);

        // === Listeners ===
        tabelaClientes.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) { selecionarCliente(); }
        });
        btnAdicionar.addActionListener(e -> adicionarCliente());
        btnEditar.addActionListener(e -> editarCliente());
        btnExcluir.addActionListener(e -> excluirCliente());
        btnRelatorio.addActionListener(e -> gerarRelatorioClientes());
        btnBuscarNome.addActionListener(e -> buscarPorNome());
        btnBuscarId.addActionListener(e -> buscarPorId());
        btnListarTodos.addActionListener(e -> carregarClientes());
        btnAdicionarAnimal.addActionListener(e -> adicionarAnimal());
        btnEditarAnimal.addActionListener(e -> editarAnimal());
        btnExcluirAnimal.addActionListener(e -> excluirAnimal());

        // --- Inicialização ---
        carregarClientes(); // Carrega dados iniciais
        habilitarBotoesAnimal(false);

        // --- GERENCIAMENTO DO OBSERVER ---
        facade.removeObserver(this);
        facade.addObserver(this);    // Adiciona a instância atual
        // ---------------------------------

    }

    // --- Implementação do Método update() da Interface DataObserver ---
    @Override
    public void update(DataType typeChanged) {
        // Adiciona log para ver se o update está sendo chamado
        System.out.println(">>> UPDATE CHAMADO em " + this.getClass().getSimpleName() + " para: " + typeChanged + " | Instância: " + this.hashCode());

        // Verifica se a mudança afeta os dados exibidos neste painel
        if (typeChanged == DataType.CLIENTE) {
            System.out.println("-> Recarregando clientes...");
            carregarClientes();
        } else if (typeChanged == DataType.ANIMAL && clienteSelecionado != null) {
            // Se um animal mudou E um cliente está selecionado, recarrega a lista de animais desse cliente
            System.out.println("-> Recarregando animais do cliente ID: " + clienteSelecionado.getId());
            // Verifica se o cliente selecionado ainda existe antes de recarregar animais
            try {
                if (facade.buscarClientePorId(clienteSelecionado.getId()) != null) {
                    carregarAnimaisDoCliente(clienteSelecionado.getId());
                } else {
                    System.out.println("-> Cliente selecionado não existe mais, limpando seleção.");
                    tabelaClientes.clearSelection(); 
                }
            } catch (Exception e) {
                 System.err.println("Erro ao verificar cliente selecionado durante update de ANIMAL: " + e.getMessage());
                 e.printStackTrace();
            }
        }
    }

    private void carregarClientes() {
        System.out.println(">>> CARREGAR CLIENTES INICIO em " + this.hashCode()); // Log com hashcode
        int selectedRow = tabelaClientes.getSelectedRow(); // Salva linha selecionada
        Object selectedId = null;
        if (selectedRow >= 0) {
            try {
                selectedId = modeloClientes.getValueAt(selectedRow, 0);
            } catch (ArrayIndexOutOfBoundsException e) {
                selectedRow = -1;
            }
        }

        modeloClientes.setRowCount(0);
        try {
            List<Cliente> clientes = facade.listarTodosClientes();
            if (clientes != null) {
                clientes.forEach(c ->
                    modeloClientes.addRow(new Object[]{
                        c.getId(), c.getNome(), c.getEndereco(), c.getEmail(), c.getTelefone(), c.getCpf()
                    })
                );
            }
        } catch (Exception e) {
            System.err.println("!!! ERRO em carregarClientes: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao carregar clientes: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
        modeloClientes.fireTableDataChanged();

        // Tenta restaurar seleção
        if (selectedId != null) {
            for (int i = 0; i < modeloClientes.getRowCount(); i++) {
                if (selectedId.equals(modeloClientes.getValueAt(i, 0))) {
                    tabelaClientes.setRowSelectionInterval(i, i);
                    // Chama selecionarCliente explicitamente se a seleção não disparar o listener
                    if (tabelaClientes.getSelectedRow() == i) {
                         selecionarCliente();
                    }
                    break;
                }
            }
             // Se não encontrou o ID antigo, limpa a seleção e os animais
             if (tabelaClientes.getSelectedRow() == -1) {
                 tabelaClientes.clearSelection();
                 atualizarTabelaAnimais(null);
             }
        } else {
            tabelaClientes.clearSelection();
            atualizarTabelaAnimais(null);
        }
        System.out.println("<<< CARREGAR CLIENTES FIM em " + this.hashCode());
    }

    private void adicionarCliente() {
        ClienteFormDialog dialog = new ClienteFormDialog((JFrame) SwingUtilities.getWindowAncestor(this), null);
        dialog.setVisible(true);
        if (dialog.foiSalvo()) {
            Cliente novo = dialog.getCliente();
            String cepDigitado = dialog.getCep(); // Pega o CEP que o usuário digitou

            try {
                // Se o usuário digitou algo no campo CEP (mesmo que só a máscara)
                if (cepDigitado != null && !cepDigitado.replaceAll("[^0-9]", "").isEmpty()) {
                    System.out.println("Painel: Chamando facade.adicionarClienteComCep com 7 args");
                    // Chama a versão com 7 argumentos, passando o endereço digitado
                    facade.adicionarClienteComCep(
                        novo.getNome(),
                        cepDigitado,          // O CEP digitado
                        novo.getEndereco(),   // O Endereço digitado
                        novo.getEmail(),
                        novo.getTelefone(),
                        novo.getCpf(),
                        novo.getSenha()
                    );
                } else {
                    System.out.println("Painel: Chamando facade.adicionarCliente com 6 args");
                    // Chama a versão com 6 argumentos (sem CEP)
                    facade.adicionarCliente(
                        novo.getNome(),
                        novo.getEndereco(),
                        novo.getEmail(),
                        novo.getTelefone(),
                        novo.getCpf(),
                        novo.getSenha()
                    );
                }
                JOptionPane.showMessageDialog(this, "Cliente adicionado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, "Erro de validação: " + ex.getMessage(), "Erro", JOptionPane.WARNING_MESSAGE);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Erro ao adicionar cliente:\n" + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editarCliente() {
        int linha = tabelaClientes.getSelectedRow();
        if (linha >= 0) {
            int id = (int) modeloClientes.getValueAt(linha, 0);
            Cliente existente = null;
             try { existente = facade.buscarClientePorId(id); } catch (Exception e) { e.printStackTrace(); JOptionPane.showMessageDialog(this, "Erro ao buscar cliente:\n" + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE); return; }
            if (existente == null) { JOptionPane.showMessageDialog(this, "Cliente (ID: " + id + ") não encontrado.", "Erro", JOptionPane.ERROR_MESSAGE); return; }
            ClienteFormDialog dialog = new ClienteFormDialog((JFrame) SwingUtilities.getWindowAncestor(this), existente);
            dialog.setVisible(true);
            if (dialog.foiSalvo()) {
                Cliente atualizado = dialog.getCliente();
                try {
                    facade.atualizarCliente( id, atualizado.getNome(), atualizado.getEndereco(), atualizado.getEmail(), atualizado.getTelefone(), atualizado.getCpf(), atualizado.getSenha() );
                    // carregarClientes(); // Observer cuidará disso
                    JOptionPane.showMessageDialog(this, "Cliente atualizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception e) { e.printStackTrace(); JOptionPane.showMessageDialog(this, "Erro ao atualizar cliente:\n" + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE); }
            }
        } else { JOptionPane.showMessageDialog(this, "Selecione um cliente.", "Aviso", JOptionPane.WARNING_MESSAGE); }
    }

    private void excluirCliente() {
        int linha = tabelaClientes.getSelectedRow();
        if (linha >= 0) {
            int id = (int) modeloClientes.getValueAt(linha, 0);
            Cliente existente = null;
            try { existente = facade.buscarClientePorId(id); } catch (Exception e) { e.printStackTrace(); JOptionPane.showMessageDialog(this, "Erro ao buscar cliente:\n" + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE); return; }
            if (existente == null) { JOptionPane.showMessageDialog(this, "Cliente (ID: " + id + ") não encontrado.", "Erro", JOptionPane.ERROR_MESSAGE); return; }
            boolean podeExcluir = true; String msgRestricao = "";
            try { if (!facade.listarAnimaisPorCliente(id).isEmpty()) { podeExcluir = false; msgRestricao += "- Possui animais.\n"; } } catch (Exception e) { e.printStackTrace(); JOptionPane.showMessageDialog(this, "Erro ao verificar deps:\n" + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE); return; }
            if (!podeExcluir) { JOptionPane.showMessageDialog(this, "Não excluir '" + existente.getNome() + "':\n" + msgRestricao, "Bloqueado", JOptionPane.WARNING_MESSAGE); return; }
            int confirm = JOptionPane.showConfirmDialog(this, "Excluir '" + existente.getNome() + "'?", "Confirmar", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                 try {
                    facade.removerCliente(id); 
                    JOptionPane.showMessageDialog(this, "Cliente excluído!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                 } catch (Exception e) { e.printStackTrace(); JOptionPane.showMessageDialog(this, "Erro ao excluir cliente:\n" + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE); }
            }
        } else { JOptionPane.showMessageDialog(this, "Selecione um cliente.", "Aviso", JOptionPane.WARNING_MESSAGE); }
    }

    private void selecionarCliente() {
        int linha = tabelaClientes.getSelectedRow();
        if (linha >= 0) {
            try {
                 int clienteId = (int) modeloClientes.getValueAt(linha, 0);
                 clienteSelecionado = facade.buscarClientePorId(clienteId);
                 if (clienteSelecionado != null) {
                     lblAnimaisTitulo.setText("🐶 Animais de: " + clienteSelecionado.getNome());
                     atualizarTabelaAnimais(facade.listarAnimaisPorCliente(clienteId));
                 } else {
                     lblAnimaisTitulo.setText("🐶 Cliente (ID: " + clienteId + ") não encontrado.");
                     clienteSelecionado = null;
                     atualizarTabelaAnimais(null);
                 }
            } catch (Exception e) {
                 e.printStackTrace(); JOptionPane.showMessageDialog(this, "Erro selecionar/carregar:\n" + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                 lblAnimaisTitulo.setText("🐶 Erro ao carregar animais.");
                 clienteSelecionado = null;
                 atualizarTabelaAnimais(null);
            }
        } else {
            lblAnimaisTitulo.setText("🐶 Animais do Cliente Selecionado:");
            clienteSelecionado = null;
            atualizarTabelaAnimais(null);
        }
    }

    private void atualizarTabelaAnimais(List<Animal> animais) {
        modeloAnimais.setRowCount(0);
        if (animais != null) {
            animais.forEach(a -> modeloAnimais.addRow(new Object[]{ a.getId(), a.getNome(), a.getEspecie(), a.getRaca(), a.getDataNascimento() != null ? a.getDataNascimento().format(dateFormatter) : "N/A" }) );
        }
        modeloAnimais.fireTableDataChanged();
        habilitarBotoesAnimal(clienteSelecionado != null);
    }

    private void carregarAnimaisDoCliente(int clienteId) {
         try { atualizarTabelaAnimais(facade.listarAnimaisPorCliente(clienteId)); } catch (Exception e) { e.printStackTrace(); JOptionPane.showMessageDialog(this, "Erro recarregar animais: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE); atualizarTabelaAnimais(null); }
    }

    private void adicionarAnimal() {
        if (clienteSelecionado != null) {
            AnimalDialog dialog = new AnimalDialog((JFrame) SwingUtilities.getWindowAncestor(this), null, clienteSelecionado.getId(), facade);
            dialog.setVisible(true);
        } else { JOptionPane.showMessageDialog(this, "Selecione um cliente.", "Aviso", JOptionPane.WARNING_MESSAGE); }
    }

    private void editarAnimal() {
        int linhaAnimal = tabelaAnimais.getSelectedRow();
        if (linhaAnimal >= 0 && clienteSelecionado != null) {
            int animalId = (int) modeloAnimais.getValueAt(linhaAnimal, 0);
            Animal animalExistente = null;
             try { animalExistente = facade.buscarAnimalPorId(animalId); } catch (Exception e) { e.printStackTrace(); JOptionPane.showMessageDialog(this, "Erro buscar animal:\n" + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE); return; }
             if (animalExistente == null) { JOptionPane.showMessageDialog(this, "Animal (ID: " + animalId + ") não encontrado.", "Erro", JOptionPane.ERROR_MESSAGE); return; }
            AnimalDialog dialog = new AnimalDialog((JFrame) SwingUtilities.getWindowAncestor(this), animalExistente, clienteSelecionado.getId(), facade);
             dialog.setVisible(true);
             // Observer cuidará da atualização
        } else {
             if (clienteSelecionado == null) JOptionPane.showMessageDialog(this, "Selecione um cliente.", "Aviso", JOptionPane.WARNING_MESSAGE);
             else JOptionPane.showMessageDialog(this, "Selecione um animal.", "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void excluirAnimal() {
        int linhaAnimal = tabelaAnimais.getSelectedRow();
        if (linhaAnimal >= 0 && clienteSelecionado != null) {
            int animalId = (int) modeloAnimais.getValueAt(linhaAnimal, 0);
            Animal existente = null;
            try { existente = facade.buscarAnimalPorId(animalId); } catch (Exception e) { e.printStackTrace(); JOptionPane.showMessageDialog(this, "Erro buscar animal:\n" + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE); return; }
            if (existente == null) { JOptionPane.showMessageDialog(this, "Animal (ID: " + animalId + ") não encontrado.", "Erro", JOptionPane.ERROR_MESSAGE); return; }
            int confirm = JOptionPane.showConfirmDialog(this, "Excluir '" + existente.getNome() + "'?", "Confirmar", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                 try {
                    facade.removerAnimal(animalId); // Observer cuidará
                    JOptionPane.showMessageDialog(this, "Animal excluído!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                 } catch (Exception e) { e.printStackTrace(); JOptionPane.showMessageDialog(this, "Erro excluir animal:\n" + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE); }
            }
        } else {
             if (clienteSelecionado == null) JOptionPane.showMessageDialog(this, "Selecione um cliente.", "Aviso", JOptionPane.WARNING_MESSAGE);
             else JOptionPane.showMessageDialog(this, "Selecione um animal.", "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void habilitarBotoesAnimal(boolean clienteSelecionadoValido) {
        btnAdicionarAnimal.setEnabled(clienteSelecionadoValido);
        boolean temAnimaisNaTabela = modeloAnimais.getRowCount() > 0;
        btnEditarAnimal.setEnabled(clienteSelecionadoValido && temAnimaisNaTabela);
        btnExcluirAnimal.setEnabled(clienteSelecionadoValido && temAnimaisNaTabela);
        if (!clienteSelecionadoValido || !temAnimaisNaTabela) { tabelaAnimais.clearSelection(); }
    }

    private void buscarPorNome() {
        String nome = txtBuscaNome.getText().trim();
        try {
            if (!nome.isEmpty()) { atualizarTabelaClientes(facade.buscarClientesPorNome(nome)); }
            else { carregarClientes(); }
        } catch (Exception e) { e.printStackTrace(); JOptionPane.showMessageDialog(this, "Erro buscar nome:\n" + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE); }
    }

    private void buscarPorId() {
        String idStr = txtBuscaId.getText().trim();
         if (idStr.isEmpty()){ JOptionPane.showMessageDialog(this, "Digite um ID.", "Aviso", JOptionPane.WARNING_MESSAGE); return; }
        try {
            int id = Integer.parseInt(idStr);
            Cliente c = facade.buscarClientePorId(id);
            List<Cliente> resultado = (c != null) ? Collections.singletonList(c) : Collections.emptyList();
            atualizarTabelaClientes(resultado);
            if (c == null) { JOptionPane.showMessageDialog(this, "Nenhum cliente com ID: " + id, "Não Encontrado", JOptionPane.INFORMATION_MESSAGE); }
        } catch (NumberFormatException ex) { JOptionPane.showMessageDialog(this, "ID inválido!", "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) { ex.printStackTrace(); JOptionPane.showMessageDialog(this, "Erro buscar ID:\n" + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE); }
    }

    private void atualizarTabelaClientes(List<Cliente> clientes) {
        modeloClientes.setRowCount(0);
        if (clientes != null) {
             clientes.forEach(c -> modeloClientes.addRow(new Object[]{ c.getId(), c.getNome(), c.getEndereco(), c.getEmail(), c.getTelefone(), c.getCpf() }));
        }
        modeloClientes.fireTableDataChanged();
        tabelaClientes.clearSelection();
        atualizarTabelaAnimais(null);
    }

    private void gerarRelatorioClientes() {
        try {
             List<Cliente> clientes = facade.listarTodosClientes();
             if (clientes == null || clientes.isEmpty()) { JOptionPane.showMessageDialog(this, "Não há clientes.", "Aviso", JOptionPane.INFORMATION_MESSAGE); return; }
             StringBuilder content = new StringBuilder("RELATÓRIO DE CLIENTES\nGerado em: ");
             content.append(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date())).append("\n================================================================================\n\n");
             clientes.forEach(c -> content.append(String.format( "ID: %-5d | Nome: %-30s | CPF: %-15s\n          | Endereço: %-40s\n          | Email: %-30s | Telefone: %s\n", c.getId(), c.getNome(), c.getCpf(), c.getEndereco(), c.getEmail(), c.getTelefone())).append("--------------------------------------------------------------------------------\n") );
             String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
             String outputPath = "relatorio_clientes_" + timestamp + ".pdf";
             new ClientReportPDFGenerator(outputPath, content.toString()).generateReport();
             JOptionPane.showMessageDialog(this, "Relatório gerado!\nSalvo como: " + outputPath, "Sucesso", JOptionPane.INFORMATION_MESSAGE);
         } catch (Exception e) { JOptionPane.showMessageDialog(this, "Erro gerar relatório:\n" + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE); e.printStackTrace(); }
    }

} 
