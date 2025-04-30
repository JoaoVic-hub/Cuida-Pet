package com.clinica.view.ViewEmpresa; // << 1. PACOTE CORRETO

import com.clinica.facade.ClinicaFacade;
import com.clinica.model.Animal;
import com.clinica.model.Cliente;
import com.clinica.report.ClientReportPDFGenerator; // Para gerar relatório
import com.clinica.view.ViewEmpresa.ClienteFormDialog; // Importa o diálogo de cliente

import com.clinica.view.ViewEmpresa.AnimalDialog; // << ADICIONAR ESTE IMPORT

import java.awt.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;
import java.util.Date;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.event.ListSelectionListener; // Import para listener
import javax.swing.event.ListSelectionEvent; // Import para listener

public class PainelGerenciarClientes extends JPanel {

    // --- Componentes UI ---
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
    private JButton btnBuscarNome, btnBuscarId, btnListarTodos; // << 2. DECLARAÇÃO DOS BOTÕES DE BUSCA

    // --- Outros ---
    private Cliente clienteSelecionado = null;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private ClinicaFacade facade = ClinicaFacade.getInstance();

    public PainelGerenciarClientes() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Adiciona margem geral

        // === Painel Superior (Título + Busca) ===
        JPanel panelSuperior = new JPanel(new BorderLayout(0, 5)); // Espaçamento vertical
        JLabel titulo = new JLabel("👥 Gerenciamento de Clientes", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        panelSuperior.add(titulo, BorderLayout.NORTH);

        JPanel painelBusca = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        txtBuscaNome = new JTextField(15);
        btnBuscarNome = new JButton("🔍 Buscar por Nome"); // << 3. INICIALIZAÇÃO
        txtBuscaId = new JTextField(5);
        btnBuscarId = new JButton("🔎 Buscar por ID");     // << 3. INICIALIZAÇÃO
        btnListarTodos = new JButton("📋 Listar Todos"); // << 3. INICIALIZAÇÃO

        painelBusca.add(new JLabel("Nome:"));
        painelBusca.add(txtBuscaNome);
        painelBusca.add(btnBuscarNome);
        painelBusca.add(new JLabel("ID:"));
        painelBusca.add(txtBuscaId);
        painelBusca.add(btnBuscarId);
        painelBusca.add(btnListarTodos);
        panelSuperior.add(painelBusca, BorderLayout.CENTER);
        add(panelSuperior, BorderLayout.NORTH);

        // === Painel Central Dividido (Clientes e Animais) ===
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.6); // Mais espaço para clientes inicialmente
        splitPane.setBorder(null); // Remove borda do split pane

        // -- Tabela Clientes --
        JPanel panelTabelaClientes = new JPanel(new BorderLayout()); // Painel para Tabela Clientes
        panelTabelaClientes.setBorder(BorderFactory.createTitledBorder("Clientes Cadastrados")); // Título para clareza
        modeloClientes = new DefaultTableModel(new String[]{"ID", "Nome", "Endereço", "Email", "Telefone", "CPF"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tabelaClientes = new JTable(modeloClientes);
        tabelaClientes.setRowHeight(24);
        tabelaClientes.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabelaClientes.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabelaClientes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        scrollPaneClientes = new JScrollPane(tabelaClientes);
        panelTabelaClientes.add(scrollPaneClientes, BorderLayout.CENTER);
        splitPane.setTopComponent(panelTabelaClientes);


        // -- Painel Animais --
        JPanel panelAnimais = new JPanel(new BorderLayout(5, 5));
         panelAnimais.setBorder(BorderFactory.createTitledBorder("Animais do Cliente")); // Título
        lblAnimaisTitulo = new JLabel("Selecione um cliente para ver os animais", SwingConstants.LEFT); // Texto inicial
        lblAnimaisTitulo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblAnimaisTitulo.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 0)); // Padding
        panelAnimais.add(lblAnimaisTitulo, BorderLayout.NORTH);

        modeloAnimais = new DefaultTableModel(new String[]{"ID", "Nome", "Espécie", "Raça", "Nascimento"}, 0){
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tabelaAnimais = new JTable(modeloAnimais);
        tabelaAnimais.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabelaAnimais.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
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

        // === Painel de Botões Inferior (Ações Cliente) ===
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnAdicionar = new JButton("➕ Adicionar Cliente");
        btnEditar = new JButton("✏️ Editar Cliente");
        btnExcluir = new JButton("🗑️ Excluir Cliente");
        btnAtualizar = new JButton("🔄 Atualizar Lista");
        btnRelatorio = new JButton("📊 Gerar Relatório PDF");
        painelBotoes.add(btnAdicionar);
        painelBotoes.add(btnEditar);
        painelBotoes.add(btnExcluir);
        painelBotoes.add(btnAtualizar);
        painelBotoes.add(btnRelatorio);
        add(painelBotoes, BorderLayout.SOUTH);

        // === Listeners ===
        tabelaClientes.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
             @Override
             public void valueChanged(ListSelectionEvent e) {
                 if (!e.getValueIsAdjusting()) {
                     selecionarCliente(); // Método definido abaixo
                 }
             }
        });

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

        // --- Inicialização ---
        carregarClientes();
        habilitarBotoesAnimal(false);
    } // <<-- FIM DO CONSTRUTOR

    // --- DEFINIÇÕES COMPLETAS DOS MÉTODOS ---

    private void carregarClientes() {
        modeloClientes.setRowCount(0); // Limpa tabela
        try {
            List<Cliente> clientes = facade.listarTodosClientes();
            if (clientes != null) {
                clientes.forEach(c ->
                    modeloClientes.addRow(new Object[]{
                        c.getId(),
                        c.getNome() != null ? c.getNome() : "", // Evita null pointer
                        c.getEndereco() != null ? c.getEndereco() : "",
                        c.getEmail() != null ? c.getEmail() : "",
                        c.getTelefone() != null ? c.getTelefone() : "",
                        c.getCpf() != null ? c.getCpf() : ""
                    })
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao carregar clientes: " + e.getMessage(), "Erro de Carregamento", JOptionPane.ERROR_MESSAGE);
        }
        modeloClientes.fireTableDataChanged(); // Notifica a tabela
        tabelaClientes.clearSelection(); // Limpa seleção
        atualizarTabelaAnimais(null); // Limpa tabela de animais e ajusta botões
    }

    private void adicionarCliente() {
        ClienteFormDialog dialog = new ClienteFormDialog((JFrame) SwingUtilities.getWindowAncestor(this), null);
        dialog.setVisible(true);
        if (dialog.foiSalvo()) {
            Cliente novo = dialog.getCliente();
            try {
                if (dialog.getCep() != null && !dialog.getCep().isEmpty()) {
                    facade.adicionarClienteComCep(
                        novo.getNome(), dialog.getCep(), novo.getEmail(),
                        novo.getTelefone(), novo.getCpf(), novo.getSenha()
                    );
                } else {
                    facade.adicionarCliente(
                        novo.getNome(), novo.getEndereco(), novo.getEmail(),
                        novo.getTelefone(), novo.getCpf(), novo.getSenha()
                    );
                }
                carregarClientes(); // Recarrega a lista
                JOptionPane.showMessageDialog(this, "Cliente adicionado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
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
             try {
                 existente = facade.buscarClientePorId(id);
             } catch (Exception e) {
                 e.printStackTrace();
                 JOptionPane.showMessageDialog(this, "Erro ao buscar cliente para edição:\n" + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                 return;
             }

            if (existente == null) {
                 JOptionPane.showMessageDialog(this, "Cliente (ID: " + id + ") não encontrado. Atualize a lista.", "Erro", JOptionPane.ERROR_MESSAGE);
                 return;
            }

            ClienteFormDialog dialog = new ClienteFormDialog((JFrame) SwingUtilities.getWindowAncestor(this), existente);
            dialog.setVisible(true);
            if (dialog.foiSalvo()) {
                Cliente atualizado = dialog.getCliente();
                try {
                    facade.atualizarCliente(
                        id, atualizado.getNome(), atualizado.getEndereco(),
                        atualizado.getEmail(), atualizado.getTelefone(),
                        atualizado.getCpf(), atualizado.getSenha() // Assume que a senha pode ser atualizada aqui
                    );
                    carregarClientes(); // Recarrega
                    JOptionPane.showMessageDialog(this, "Cliente atualizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Erro ao atualizar cliente:\n" + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Selecione um cliente na tabela para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void excluirCliente() {
        int linha = tabelaClientes.getSelectedRow();
        if (linha >= 0) {
            int id = (int) modeloClientes.getValueAt(linha, 0);
            Cliente existente = null;
            try {
                existente = facade.buscarClientePorId(id);
            } catch (Exception e) {
                 e.printStackTrace();
                 JOptionPane.showMessageDialog(this, "Erro ao buscar cliente para exclusão:\n" + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                 return;
            }

             if (existente == null) {
                 JOptionPane.showMessageDialog(this, "Cliente (ID: " + id + ") não encontrado. Atualize a lista.", "Erro", JOptionPane.ERROR_MESSAGE);
                 return;
            }

            // **Verificação Importante:** Checar se o cliente possui animais ou consultas antes de excluir
            boolean podeExcluir = true;
            String msgRestricao = "";
            try {
                if (!facade.listarAnimaisPorCliente(id).isEmpty()) {
                    podeExcluir = false;
                    msgRestricao += "- O cliente possui animais cadastrados.\n";
                }
                // Adicionar verificação de consultas se necessário (exigiria método na facade/DAO)
                // if (facade.clienteTemConsultas(id)) {
                //     podeExcluir = false;
                //     msgRestricao += "- O cliente possui consultas registradas.\n";
                // }
            } catch (Exception e) {
                 e.printStackTrace();
                 JOptionPane.showMessageDialog(this, "Erro ao verificar dependências do cliente:\n" + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                 return; // Não prossegue se não puder verificar
            }

            if (!podeExcluir) {
                 JOptionPane.showMessageDialog(this,
                     "Não é possível excluir o cliente '" + existente.getNome() + "' pelos seguintes motivos:\n" + msgRestricao +
                     "Remova as dependências primeiro.",
                     "Exclusão Bloqueada", JOptionPane.WARNING_MESSAGE);
                 return;
            }

            // Confirmação final
            int confirm = JOptionPane.showConfirmDialog(this,
                "Tem certeza que deseja excluir permanentemente o cliente '" + existente.getNome() + "' (ID: " + id + ")?",
                "Confirmar Exclusão", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                 try {
                    facade.removerCliente(id);
                    carregarClientes(); // Recarrega
                    JOptionPane.showMessageDialog(this, "Cliente excluído com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                 } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Erro ao excluir cliente:\n" + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Selecione um cliente na tabela para excluir.", "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void selecionarCliente() {
        int linha = tabelaClientes.getSelectedRow();
        if (linha >= 0) {
            try {
                 // Pega o ID da coluna 0 da linha selecionada
                 int clienteId = (int) modeloClientes.getValueAt(linha, 0);
                 clienteSelecionado = facade.buscarClientePorId(clienteId); // Busca na facade

                 if (clienteSelecionado != null) {
                     lblAnimaisTitulo.setText("🐶 Animais de: " + clienteSelecionado.getNome());
                     // Carrega animais usando facade
                     atualizarTabelaAnimais(facade.listarAnimaisPorCliente(clienteId));
                 } else {
                     // Cliente da linha selecionada não foi encontrado (pode ter sido excluído por outra ação)
                     lblAnimaisTitulo.setText("🐶 Cliente (ID: " + clienteId + ") não encontrado.");
                     clienteSelecionado = null; // Garante que está nulo
                     atualizarTabelaAnimais(null); // Limpa tabela de animais
                 }
            } catch (Exception e) {
                 e.printStackTrace();
                 JOptionPane.showMessageDialog(this, "Erro ao selecionar cliente ou carregar animais:\n" + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                 lblAnimaisTitulo.setText("🐶 Erro ao carregar animais.");
                 clienteSelecionado = null;
                 atualizarTabelaAnimais(null);
            }
        } else {
            // Nenhuma linha selecionada
            lblAnimaisTitulo.setText("🐶 Animais do Cliente Selecionado:");
            clienteSelecionado = null;
            atualizarTabelaAnimais(null); // Limpa tabela de animais
        }
    }

    // Método auxiliar para atualizar a tabela de animais
    private void atualizarTabelaAnimais(List<Animal> animais) {
        modeloAnimais.setRowCount(0); // Limpa tabela de animais
        if (animais != null) {
            animais.forEach(a ->
                modeloAnimais.addRow(new Object[]{
                    a.getId(),
                    a.getNome() != null ? a.getNome() : "",
                    a.getEspecie() != null ? a.getEspecie() : "",
                    a.getRaca() != null ? a.getRaca() : "",
                    a.getDataNascimento() != null ? a.getDataNascimento().format(dateFormatter) : "N/A"
                })
            );
        }
        modeloAnimais.fireTableDataChanged(); // Notifica a tabela de animais
        habilitarBotoesAnimal(clienteSelecionado != null); // Reavalia estado dos botões de animal
    }

    // Método para carregar animais (chamado internamente por selecionarCliente)
    private void carregarAnimaisDoCliente(int clienteId) {
         try {
              atualizarTabelaAnimais(facade.listarAnimaisPorCliente(clienteId));
         } catch (Exception e) {
              e.printStackTrace();
              JOptionPane.showMessageDialog(this, "Erro ao carregar animais: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
              atualizarTabelaAnimais(null); // Limpa em caso de erro
         }
    }


    // --- MÉTODOS PARA ANIMAIS ---

    private void adicionarAnimal() {
        if (clienteSelecionado != null) {
            // Usa o diálogo interno AnimalDialog
            AnimalDialog dialog = new AnimalDialog((JFrame) SwingUtilities.getWindowAncestor(this), null, clienteSelecionado.getId(), facade);
            dialog.setVisible(true);
            if (dialog.foiSalvo()) {
                carregarAnimaisDoCliente(clienteSelecionado.getId()); // Recarrega a lista de animais
            }
        } else {
             JOptionPane.showMessageDialog(this, "Selecione um cliente na tabela primeiro.", "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void editarAnimal() {
        int linhaAnimal = tabelaAnimais.getSelectedRow();
        if (linhaAnimal >= 0 && clienteSelecionado != null) {
            int animalId = (int) modeloAnimais.getValueAt(linhaAnimal, 0);
            Animal animalExistente = null;
             try {
                 animalExistente = facade.buscarAnimalPorId(animalId);
             } catch (Exception e) {
                 e.printStackTrace();
                 JOptionPane.showMessageDialog(this, "Erro ao buscar animal para edição:\n" + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                 return;
             }

             if (animalExistente == null) {
                 JOptionPane.showMessageDialog(this, "Animal (ID: " + animalId + ") não encontrado. Atualize a lista.", "Erro", JOptionPane.ERROR_MESSAGE);
                 return;
            }
            // Usa o diálogo interno AnimalDialog
            AnimalDialog dialog = new AnimalDialog((JFrame) SwingUtilities.getWindowAncestor(this), animalExistente, clienteSelecionado.getId(), facade);
             dialog.setVisible(true);
            if (dialog.foiSalvo()) {
                carregarAnimaisDoCliente(clienteSelecionado.getId()); // Recarrega a lista de animais
            }
        } else {
             if (clienteSelecionado == null)
                 JOptionPane.showMessageDialog(this, "Selecione um cliente na tabela primeiro.", "Aviso", JOptionPane.WARNING_MESSAGE);
             else
                JOptionPane.showMessageDialog(this, "Selecione um animal na tabela para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void excluirAnimal() {
        int linhaAnimal = tabelaAnimais.getSelectedRow();
        if (linhaAnimal >= 0 && clienteSelecionado != null) {
            int animalId = (int) modeloAnimais.getValueAt(linhaAnimal, 0);
            Animal existente = null;
            try {
                existente = facade.buscarAnimalPorId(animalId);
            } catch (Exception e) {
                 e.printStackTrace();
                 JOptionPane.showMessageDialog(this, "Erro ao buscar animal para exclusão:\n" + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                 return;
            }

             if (existente == null) {
                 JOptionPane.showMessageDialog(this, "Animal (ID: " + animalId + ") não encontrado. Atualize a lista.", "Erro", JOptionPane.ERROR_MESSAGE);
                 return;
            }

             // **Verificação Importante:** Checar se o animal possui consultas antes de excluir
             boolean podeExcluirAnimal = true;
             String msgRestricaoAnimal = "";
             // Adicionar verificação de consultas do animal se necessário (exigiria método na facade/DAO)
             // try {
             //    if (facade.animalTemConsultas(animalId)) {
             //        podeExcluirAnimal = false;
             //        msgRestricaoAnimal = "- O animal possui consultas registradas.\n";
             //    }
             // } catch (Exception e) { ... }

             if (!podeExcluirAnimal) {
                  JOptionPane.showMessageDialog(this,
                      "Não é possível excluir o animal '" + existente.getNome() + "' pelos seguintes motivos:\n" + msgRestricaoAnimal +
                      "Remova as dependências primeiro.",
                      "Exclusão Bloqueada", JOptionPane.WARNING_MESSAGE);
                  return;
             }

            int confirm = JOptionPane.showConfirmDialog(this,
                "Tem certeza que deseja excluir permanentemente o animal '" + existente.getNome() + "' (ID: " + animalId + ")?",
                "Confirmar Exclusão", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                 try {
                    facade.removerAnimal(animalId);
                    carregarAnimaisDoCliente(clienteSelecionado.getId()); // Recarrega
                    JOptionPane.showMessageDialog(this, "Animal excluído com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                 } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Erro ao excluir animal:\n" + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
             if (clienteSelecionado == null)
                 JOptionPane.showMessageDialog(this, "Selecione um cliente na tabela primeiro.", "Aviso", JOptionPane.WARNING_MESSAGE);
             else
                 JOptionPane.showMessageDialog(this, "Selecione um animal na tabela para excluir.", "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }

    // --- MÉTODOS UTILITÁRIOS e BUSCA ---

    private void habilitarBotoesAnimal(boolean clienteSelecionadoValido) {
        btnAdicionarAnimal.setEnabled(clienteSelecionadoValido);
        boolean temAnimaisNaTabela = modeloAnimais.getRowCount() > 0;
        // Habilita editar/excluir apenas se um cliente estiver selecionado E houver animais na lista
        btnEditarAnimal.setEnabled(clienteSelecionadoValido && temAnimaisNaTabela);
        btnExcluirAnimal.setEnabled(clienteSelecionadoValido && temAnimaisNaTabela);
        // Desseleciona animal na tabela se o cliente mudar ou não houver animais
        if (!clienteSelecionadoValido || !temAnimaisNaTabela) {
            tabelaAnimais.clearSelection();
        }
    }

    private void buscarPorNome() {
        String nome = txtBuscaNome.getText().trim();
        try {
            if (!nome.isEmpty()) {
                atualizarTabelaClientes(facade.buscarClientesPorNome(nome));
            } else {
                carregarClientes(); // Lista todos se busca vazia
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao buscar clientes por nome:\n" + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void buscarPorId() {
        String idStr = txtBuscaId.getText().trim();
         if (idStr.isEmpty()){
             JOptionPane.showMessageDialog(this, "Digite um ID numérico para buscar.", "Aviso", JOptionPane.WARNING_MESSAGE);
             return;
         }
        try {
            int id = Integer.parseInt(idStr);
            Cliente c = facade.buscarClientePorId(id);
            List<Cliente> resultado = (c != null) ? Collections.singletonList(c) : Collections.emptyList();
            atualizarTabelaClientes(resultado); // Atualiza tabela (limpa animais)
            if (c == null) {
                 JOptionPane.showMessageDialog(this, "Nenhum cliente encontrado com o ID: " + id, "Não Encontrado", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "ID inválido! Insira apenas números.", "Erro de Formato", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
             ex.printStackTrace();
             JOptionPane.showMessageDialog(this, "Erro ao buscar cliente por ID:\n" + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Atualiza a tabela de clientes e limpa a de animais
    private void atualizarTabelaClientes(List<Cliente> clientes) {
        modeloClientes.setRowCount(0); // Limpa
        if (clientes != null) {
             clientes.forEach(c -> modeloClientes.addRow(new Object[]{
                 c.getId(),
                 c.getNome() != null ? c.getNome() : "",
                 c.getEndereco() != null ? c.getEndereco() : "",
                 c.getEmail() != null ? c.getEmail() : "",
                 c.getTelefone() != null ? c.getTelefone() : "",
                 c.getCpf() != null ? c.getCpf() : ""
             }));
        }
        modeloClientes.fireTableDataChanged(); // Notifica
        tabelaClientes.clearSelection(); // Limpa seleção ao atualizar
        atualizarTabelaAnimais(null); // Limpa animais e ajusta botões
    }

    // --- MÉTODO PARA GERAR RELATÓRIO ---

    private void gerarRelatorioClientes() {
        try {
             List<Cliente> clientes = facade.listarTodosClientes();
             if (clientes == null || clientes.isEmpty()) {
                 JOptionPane.showMessageDialog(this, "Não há clientes cadastrados para gerar o relatório.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
                 return;
             }

             StringBuilder content = new StringBuilder();
             content.append("RELATÓRIO DE CLIENTES\n");
             content.append("Gerado em: ").append(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date())).append("\n");
             content.append("================================================================================\n\n");

             clientes.forEach(c ->
                 content.append(String.format(
                     "ID: %-5d | Nome: %-30s | CPF: %-15s\n          | Endereço: %-40s\n          | Email: %-30s | Telefone: %s\n",
                     c.getId(),
                     c.getNome() != null ? c.getNome() : "-",
                     c.getCpf() != null ? c.getCpf() : "-",
                     c.getEndereco() != null ? c.getEndereco() : "-",
                     c.getEmail() != null ? c.getEmail() : "-",
                     c.getTelefone() != null ? c.getTelefone() : "-"
                 )).append("--------------------------------------------------------------------------------\n")
             );

             SimpleDateFormat sdfFile = new SimpleDateFormat("yyyyMMdd_HHmmss");
             String timestamp = sdfFile.format(new Date());
             String outputPath = "relatorio_clientes_" + timestamp + ".pdf";

             // Usa a classe ClientReportPDFGenerator para criar o PDF
             ClientReportPDFGenerator generator = new ClientReportPDFGenerator(outputPath, content.toString());
             generator.generateReport();

             JOptionPane.showMessageDialog(this, "Relatório gerado com sucesso!\nSalvo como: " + outputPath, "Relatório Gerado", JOptionPane.INFORMATION_MESSAGE);

         } catch (Exception e) {
             JOptionPane.showMessageDialog(this, "Erro ao gerar relatório PDF:\n" + e.getMessage(), "Erro de Relatório", JOptionPane.ERROR_MESSAGE);
             e.printStackTrace();
         }
    }


} 
