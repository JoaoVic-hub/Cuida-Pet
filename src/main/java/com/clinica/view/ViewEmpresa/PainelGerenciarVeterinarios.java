package com.clinica.view.ViewEmpresa;

import com.clinica.command.AddVeterinarioCommand;
import com.clinica.command.Command;
import com.clinica.command.DeleteVeterinarioCommand;
import com.clinica.command.UndoManager;
import com.clinica.command.UpdateVeterinarioCommand;
import com.clinica.facade.ClinicaFacade;
import com.clinica.model.Veterinario;
import com.clinica.observer.DataObserver;
import com.clinica.observer.DataType;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

// Implementa a interface DataObserver
public class PainelGerenciarVeterinarios extends JPanel implements DataObserver {
    private JTable tabela;
    private DefaultTableModel modelo;
    private JScrollPane scrollPane;
    private ClinicaFacade facade = ClinicaFacade.getInstance();
    private JButton btnAdicionar, btnEditar, btnExcluir, btnDesfazer; // Botão Desfazer adicionado
    private UndoManager undoManager; // Instância do gerenciador de Undo/Redo
    private Timer undoButtonTimer; // Timer para atualizar estado do botão Undo

    // Construtor - Idealmente receberia a instância global do UndoManager
    public PainelGerenciarVeterinarios(/* UndoManager globalUndoManager */) {
        // Se um UndoManager global for passado, use-o. Senão, crie um local.
        // if (globalUndoManager != null) {
        //     this.undoManager = globalUndoManager;
        // } else {
             System.out.println("WARN: Criando UndoManager local para PainelGerenciarVeterinarios.");
             this.undoManager = new UndoManager(); // Cria instância local (Undo só funciona neste painel)
        // }

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- UI ---
        JLabel titulo = new JLabel("🩺 Gerenciamento de Veterinários", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        add(titulo, BorderLayout.NORTH);

        modelo = new DefaultTableModel(
                new String[]{"ID", "Nome", "Email", "Telefone", "CPF", "CRMV", "Especialidade"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; } // Células não editáveis
        };
        tabela = new JTable(modelo);
        tabela.setRowHeight(24);
        tabela.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabela.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Apenas uma linha selecionável
        scrollPane = new JScrollPane(tabela);
        add(scrollPane, BorderLayout.CENTER);

        // --- Painel de Botões ---
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnAdicionar = new JButton("➕ Adicionar");
        btnEditar = new JButton("✏️ Editar");
        btnExcluir = new JButton("🗑️ Excluir");
        btnDesfazer = new JButton("↩️ Desfazer"); // Botão Desfazer
        painelBotoes.add(btnAdicionar);
        painelBotoes.add(btnEditar);
        painelBotoes.add(btnExcluir);
        painelBotoes.add(btnDesfazer); // Adiciona o botão Desfazer à UI
        add(painelBotoes, BorderLayout.SOUTH);

        // --- Listeners ---
        btnAdicionar.addActionListener(e -> adicionarVeterinario()); // Chama método adaptado
        btnEditar.addActionListener(e -> editarVeterinario());     // Chama método adaptado
        btnExcluir.addActionListener(e -> excluirVeterinario());   // Chama método adaptado
        btnDesfazer.addActionListener(e -> desfazAcao());        // Chama o método de desfazer

        // --- Inicialização ---
        carregarVeterinarios();      // Carrega dados iniciais da tabela
        configurarTimerBotaoUndo(); // Configura e inicia o timer para o botão Desfazer

        // --- REGISTRAR COMO OBSERVER ---
        facade.removeObserver(this); // Remove instâncias anteriores (se houver)
        facade.addObserver(this);    // Adiciona esta instância como observer
        // -----------------------------
    }

    // Método para configurar o Timer que atualiza o botão Undo
    private void configurarTimerBotaoUndo() {
        // Cria um Timer que dispara a cada 500ms
        undoButtonTimer = new Timer(500, e -> atualizarEstadoBotaoUndo());
        undoButtonTimer.setRepeats(true); // Faz o timer repetir
        undoButtonTimer.start(); // Inicia o timer
        atualizarEstadoBotaoUndo(); // Atualiza o estado inicial do botão
        System.out.println("Timer do botão Undo iniciado para: " + this.getClass().getSimpleName());
    }

    // Atualiza o estado (habilitado/desabilitado) do botão Desfazer
    private void atualizarEstadoBotaoUndo() {
        if (btnDesfazer != null && undoManager != null) {
             // Habilita o botão se o UndoManager indicar que pode desfazer
            btnDesfazer.setEnabled(undoManager.canUndo());
        }
        // System.out.println("Timer check: canUndo = " + undoManager.canUndo()); // Log para debug
    }

    // --- Implementação do Método update() da Interface DataObserver ---
    @Override
    public void update(DataType typeChanged) {
        System.out.println("PainelGerenciarVeterinarios notificado sobre: " + typeChanged + " | Thread: " + Thread.currentThread().getName()); // Log
        // Recarrega a lista de veterinários se os dados de VETERINARIO mudaram
        if (typeChanged == DataType.VETERINARIO) {
            System.out.println("-> Recarregando veterinários...");
            carregarVeterinarios(); // Atualiza a tabela
        }
        // É importante atualizar o estado do botão Desfazer após qualquer mudança
        // relevante, mas o Timer já faz isso periodicamente.
        // Se quiser atualização imediata (pode ser redundante):
        // SwingUtilities.invokeLater(this::atualizarEstadoBotaoUndo);
    }

    // --- Métodos de Ação (Adaptados para usar Command e UndoManager) ---

    private void adicionarVeterinario() {
        VeterinarioFormDialog dialog = new VeterinarioFormDialog((JFrame) SwingUtilities.getWindowAncestor(this), null);
        dialog.setVisible(true);
        if (dialog.foiSalvo()) {
            try {
                Veterinario novoVeterinario = dialog.getVeterinario();
                // 1. Cria o Comando de Adição
                Command command = new AddVeterinarioCommand(facade, novoVeterinario);
                // 2. Executa o Comando através do Gerenciador de Undo
                undoManager.executeCommand(command);
                JOptionPane.showMessageDialog(this, "Veterinário adicionado!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                // Trata erros durante a execução do comando (validação, etc.)
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Erro ao adicionar veterinário:\n" + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            } finally {
                // Garante a atualização do botão mesmo se ocorrer erro na execução do comando
                 atualizarEstadoBotaoUndo();
            }
        }
    }

    private void editarVeterinario() {
        int linha = tabela.getSelectedRow();
        if (linha >= 0) {
            int id = (int) modelo.getValueAt(linha, 0);
            Veterinario existente; // Não precisa inicializar aqui
            try {
                // Busca o estado ATUAL antes de abrir o diálogo de edição
                existente = facade.buscarVeterinarioPorId(id);
                 if (existente == null) {
                     JOptionPane.showMessageDialog(this, "Veterinário (ID: " + id + ") não encontrado. A lista pode estar desatualizada.", "Erro", JOptionPane.ERROR_MESSAGE);
                     return;
                 }
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Erro ao buscar veterinário para edição:\n" + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Mostra o diálogo preenchido com os dados existentes
            VeterinarioFormDialog dialog = new VeterinarioFormDialog((JFrame) SwingUtilities.getWindowAncestor(this), existente);
            dialog.setVisible(true);

            if (dialog.foiSalvo()) {
                try {
                    // Pega o estado NOVO (modificado) do diálogo
                    Veterinario vetAtualizado = dialog.getVeterinario();
                    // 1. Cria o Comando de Atualização (passa ID e o estado novo)
                    Command command = new UpdateVeterinarioCommand(facade, id, vetAtualizado);
                    // 2. Executa o Comando através do Gerenciador
                    undoManager.executeCommand(command);
                    JOptionPane.showMessageDialog(this, "Veterinário atualizado!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    // Trata erros durante a execução do comando
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Erro ao atualizar veterinário:\n" + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                } finally {
                     atualizarEstadoBotaoUndo();
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Selecione um veterinário na tabela para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void excluirVeterinario() {
        int linha = tabela.getSelectedRow();
        if (linha >= 0) {
            int id = (int) modelo.getValueAt(linha, 0);
            // Confirmação antes de excluir
            String nomeVet = (String) modelo.getValueAt(linha, 1); // Pega o nome da tabela para a mensagem
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Excluir permanentemente o veterinário '" + nomeVet + "' (ID: " + id + ")?",
                    "Confirmar Exclusão",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                 try {
                    // 1. Cria o Comando de Exclusão (passa o ID)
                    Command command = new DeleteVeterinarioCommand(facade, id);
                    // 2. Executa o Comando através do Gerenciador
                    // O comando tentará buscar o vet antes de chamar facade.removerVeterinario
                    undoManager.executeCommand(command);
                    JOptionPane.showMessageDialog(this, "Veterinário excluído!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                 } catch (Exception e) {
                    // Trata erros (ex: vet não encontrado, restrições de exclusão na facade)
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Erro ao excluir veterinário:\n" + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                 } finally {
                      atualizarEstadoBotaoUndo();
                 }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Selecione um veterinário na tabela para excluir.", "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }

    // --- Método para chamar o UndoManager ---
    private void desfazAcao() {
        System.out.println("Botão Desfazer pressionado."); // Log
        try {
            undoManager.undo(); // Chama o método undo do gerenciador
            // A tabela será atualizada automaticamente via Observer
            JOptionPane.showMessageDialog(this, "Última ação desfeita!", "Desfazer", JOptionPane.INFORMATION_MESSAGE);
        } catch (IllegalStateException ex) { // Exceção comum se a pilha estiver vazia
             JOptionPane.showMessageDialog(this, "Nada para desfazer.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) { // Outras exceções durante o undo() do comando
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao desfazer ação:\n" + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        } finally {
             atualizarEstadoBotaoUndo(); // Atualiza o botão após tentar desfazer
        }
    }

    // --- Método para Carregar/Atualizar Tabela (sem mudanças na lógica interna) ---
    private void carregarVeterinarios() {
        // Guarda a seleção atual para tentar restaurar depois
        int selectedRow = tabela.getSelectedRow();
        Object selectedId = null;
        if (selectedRow >= 0) {
             try {
                 selectedId = modelo.getValueAt(selectedRow, 0);
             } catch (ArrayIndexOutOfBoundsException e) { selectedRow = -1; /* Linha sumiu */ }
        }

        modelo.setRowCount(0); // Limpa a tabela
        try {
            List<Veterinario> vets = facade.listarTodosVeterinarios(); // Busca via Facade
            if (vets != null) {
                for (Veterinario vet : vets) {
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
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao carregar veterinários: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
        modelo.fireTableDataChanged(); // Notifica a tabela sobre a mudança de dados

        // Tenta restaurar a seleção
        if (selectedId != null) {
            for (int i = 0; i < modelo.getRowCount(); i++) {
                if (selectedId.equals(modelo.getValueAt(i, 0))) {
                    tabela.setRowSelectionInterval(i, i);
                    break;
                }
            }
        }
        // Se não restaurou, garante que não há linha selecionada
        if (tabela.getSelectedRow() < 0) {
             // Limpar detalhes ou ações dependentes da seleção aqui, se houver
        }
    }

     // --- Limpeza ao remover o painel (IMPORTANTE para parar o Timer) ---
     @Override
     public void removeNotify() {
         super.removeNotify();
         // Para o Timer quando o painel não estiver mais visível/ativo
         if (undoButtonTimer != null && undoButtonTimer.isRunning()) {
             undoButtonTimer.stop();
             System.out.println("Timer do botão Undo parado para: " + this.getClass().getSimpleName());
         }
         // É uma boa prática remover o observer quando o componente é descartado
         facade.removeObserver(this);
         System.out.println("Observer removido para: " + this.getClass().getSimpleName());
     }

} // Fim da classe PainelGerenciarVeterinarios