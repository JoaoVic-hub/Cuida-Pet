package com.clinica.view.ViewEmpresa;

import com.clinica.facade.ClinicaFacade;
import com.clinica.model.Veterinario;
import com.clinica.observer.DataObserver; // << Importar Observer
import com.clinica.observer.DataType;     // << Importar DataType
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
    private JButton btnAdicionar, btnEditar, btnExcluir; // Botão Atualizar removido

    public PainelGerenciarVeterinarios() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- UI ---
        JLabel titulo = new JLabel("🩺 Gerenciamento de Veterinários", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        add(titulo, BorderLayout.NORTH);

        modelo = new DefaultTableModel(
                new String[]{"ID", "Nome", "Email", "Telefone", "CPF", "CRMV", "Especialidade"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabela = new JTable(modelo);
        tabela.setRowHeight(24);
        tabela.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabela.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        scrollPane = new JScrollPane(tabela);
        add(scrollPane, BorderLayout.CENTER);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnAdicionar = new JButton("➕ Adicionar");
        btnEditar = new JButton("✏️ Editar");
        btnExcluir = new JButton("🗑️ Excluir");
        // REMOVIDO: JButton btnAtualizar = new JButton("🔄 Atualizar");
        painelBotoes.add(btnAdicionar);
        painelBotoes.add(btnEditar);
        painelBotoes.add(btnExcluir);
        // REMOVIDO: painelBotoes.add(btnAtualizar);
        add(painelBotoes, BorderLayout.SOUTH);

        // --- Listeners ---
        btnAdicionar.addActionListener(e -> adicionarVeterinario());
        btnEditar.addActionListener(e -> editarVeterinario());
        btnExcluir.addActionListener(e -> excluirVeterinario());
        // REMOVIDO: btnAtualizar.addActionListener(e -> carregarVeterinarios());

        // --- Inicialização ---
        carregarVeterinarios();

        // --- REGISTRAR COMO OBSERVER ---
        facade.removeObserver(this);
        facade.addObserver(this);
        // -----------------------------
    }

    // --- Implementação do Método update() ---
    @Override
    public void update(DataType typeChanged) {
        System.out.println("PainelGerenciarVeterinarios notificado sobre: " + typeChanged); // Log
        // Recarrega se veterinários mudaram
        if (typeChanged == DataType.VETERINARIO) {
            System.out.println("-> Recarregando veterinários...");
            carregarVeterinarios();
        }
    }

    // --- Métodos de Ação ---

     // Dentro de PainelGerenciarVeterinarios.java

private void adicionarVeterinario() {
    // Cria e exibe o diálogo para coletar os dados do novo veterinário
    VeterinarioFormDialog dialog = new VeterinarioFormDialog((JFrame) SwingUtilities.getWindowAncestor(this), null);
    dialog.setVisible(true);

    // Verifica se o usuário clicou em "Salvar" no diálogo
    if (dialog.foiSalvo()) {
        try {
            // Obtém o objeto Veterinario preenchido do diálogo
            Veterinario novoVeterinario = dialog.getVeterinario();

            // *** ADICIONAR ESTA CHAMADA PARA A FACADE ***
            // Chama o método da facade para realmente adicionar o veterinário ao sistema
            facade.adicionarVeterinario(
                    novoVeterinario.getNome(),
                    novoVeterinario.getEmail(),
                    novoVeterinario.getTelefone(),
                    novoVeterinario.getCpf(),
                    novoVeterinario.getSenha(), // Garanta que a senha está sendo coletada corretamente no dialog
                    novoVeterinario.getCrmv(),
                    novoVeterinario.getEspecialidade()
            );
            // *** FIM DA CHAMADA ADICIONADA ***

            // A facade chamará notifyObservers(DataType.VETERINARIO),
            // o que fará este painel (que é um observer) atualizar a tabela
            // automaticamente através do método update().

            // Exibe a mensagem de sucesso
            JOptionPane.showMessageDialog(this, "Veterinário adicionado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);

        } catch (IllegalArgumentException ex) {
            // Captura erros de validação específicos que podem ser lançados pela facade
            JOptionPane.showMessageDialog(this, "Erro de validação ao adicionar: " + ex.getMessage(), "Erro de Validação", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            // Captura quaisquer outros erros que possam ocorrer durante a adição
            ex.printStackTrace(); // Imprime o erro detalhado no console para depuração
            JOptionPane.showMessageDialog(this, "Erro ao adicionar veterinário:\n" + ex.getMessage(), "Erro Inesperado", JOptionPane.ERROR_MESSAGE);
        }
    }
    // O restante da classe permanece o mesmo...
}

    // Dentro de PainelGerenciarVeterinarios.java

private void editarVeterinario() {
    // Obtém a linha selecionada na tabela
    int linha = tabela.getSelectedRow();
    if (linha >= 0) {
        // Obtém o ID do veterinário da linha selecionada
        int id = (int) modelo.getValueAt(linha, 0);
        Veterinario existente = null;
        try {
            // Busca o objeto Veterinario original usando a facade
            existente = facade.buscarVeterinarioPorId(id);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao buscar veterinário para edição:\n" + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            return; // Interrompe se não conseguir buscar
        }
        // Verifica se o veterinário foi encontrado
        if (existente == null) {
            JOptionPane.showMessageDialog(this, "Veterinário (ID: " + id + ") não encontrado. A lista pode estar desatualizada.", "Erro", JOptionPane.ERROR_MESSAGE);
            return; // Interrompe se não encontrou
        }

        // Cria e exibe o diálogo, passando o veterinário existente para preencher os campos
        VeterinarioFormDialog dialog = new VeterinarioFormDialog((JFrame) SwingUtilities.getWindowAncestor(this), existente);
        dialog.setVisible(true);

        // Verifica se o usuário clicou em "Salvar" no diálogo
        if (dialog.foiSalvo()) {
            try {
                // Obtém o objeto Veterinario com os dados (potencialmente) modificados do diálogo
                Veterinario vetAtualizado = dialog.getVeterinario();

                // *** ADICIONAR ESTA CHAMADA PARA A FACADE ***
                // Chama o método da facade para atualizar o veterinário no sistema
                facade.atualizarVeterinario(
                        id, // Passa o ID original do veterinário que está sendo editado
                        vetAtualizado.getNome(),
                        vetAtualizado.getEmail(),
                        vetAtualizado.getTelefone(),
                        vetAtualizado.getCpf(),
                        vetAtualizado.getSenha(), // Assume que o dialog.getVeterinario() retorna a senha atualizada
                        vetAtualizado.getCrmv(),
                        vetAtualizado.getEspecialidade()
                );
                // *** FIM DA CHAMADA ADICIONADA ***

                // A facade notificará os observers (incluindo este painel),
                // e a tabela será atualizada automaticamente.

                // Exibe a mensagem de sucesso
                JOptionPane.showMessageDialog(this, "Veterinário atualizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);

            } catch (IllegalArgumentException ex) {
                // Captura erros de validação específicos
                JOptionPane.showMessageDialog(this, "Erro de validação ao atualizar: " + ex.getMessage(), "Erro de Validação", JOptionPane.WARNING_MESSAGE);
            } catch (Exception ex) {
                // Captura outros erros
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Erro ao atualizar veterinário:\n" + ex.getMessage(), "Erro Inesperado", JOptionPane.ERROR_MESSAGE);
            }
        }
        // Se o usuário não selecionou nenhuma linha
    } else {
        JOptionPane.showMessageDialog(this, "Selecione um veterinário na tabela para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
    }
}

    private void excluirVeterinario() {
        int linha = tabela.getSelectedRow();
        if (linha >= 0) {
            int id = (int) modelo.getValueAt(linha, 0);
            Veterinario existente = null;
            try { existente = facade.buscarVeterinarioPorId(id); } catch (Exception e) { e.printStackTrace(); JOptionPane.showMessageDialog(this, "Erro ao buscar vet:\n" + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE); return; }
            if (existente == null) { JOptionPane.showMessageDialog(this, "Veterinário (ID: " + id + ") não encontrado.", "Erro", JOptionPane.ERROR_MESSAGE); return; }

            // TODO: Verificar se veterinário tem consultas antes de excluir
            int confirm = JOptionPane.showConfirmDialog(this, "Excluir permanentemente '" + existente.getNome() + "'?", "Confirmar Exclusão", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                 try {
                    facade.removerVeterinario(id); // Facade notificará
                    // carregarVeterinarios(); // Não precisa mais
                    JOptionPane.showMessageDialog(this, "Veterinário excluído!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                 } catch (Exception e) { e.printStackTrace(); JOptionPane.showMessageDialog(this, "Erro ao excluir vet:\n" + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE); }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Selecione um veterinário para excluir.", "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }


    // --- Método para Carregar/Atualizar Tabela ---

    private void carregarVeterinarios() {
        modelo.setRowCount(0); // Limpa
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
        modelo.fireTableDataChanged(); // Notifica
    }
}
