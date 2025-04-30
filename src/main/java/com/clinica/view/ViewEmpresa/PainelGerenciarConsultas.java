package com.clinica.view.ViewEmpresa;

import com.clinica.controller.ConsultaController;     // Necessário para ConsultaFormDialog
import com.clinica.model.Consulta;    // Necessário para ConsultaFormDialog
import java.awt.*;
import java.time.format.DateTimeFormatter; // Necessário para ConsultaFormDialog
import java.util.List; // Removido - não usado aqui
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class PainelGerenciarConsultas extends JPanel {

    private JTable tabela;
    private DefaultTableModel modelo;
    private JScrollPane scrollPane;
    private ConsultaController controller = new ConsultaController();
    // Mova os controllers auxiliares para cá se for usar a versão NÃO otimizada
    // private ClienteController cc = new ClienteController();
    // private VeterinarioController vc = new VeterinarioController();
    // private AnimalController ac = new AnimalController();
    private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private JButton btnAdicionar, btnEditar, btnExcluir, btnAtualizar; // Adicionado para referência

    public PainelGerenciarConsultas() {
        setLayout(new BorderLayout(10, 10)); // Espaçamento

        // Cabeçalho com título
        JLabel titulo = new JLabel("Gerenciamento de Consultas", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titulo.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(titulo, BorderLayout.NORTH);

        // Configuração da tabela
        modelo = new DefaultTableModel(
                new String[]{"ID", "Data/Hora", "Status", "Cliente", "Animal", "Veterinário"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Torna a tabela não editável
            }
        };
        tabela = new JTable(modelo);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Permite selecionar apenas uma linha
        tabela.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabela.setRowHeight(22);
        tabela.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));

        scrollPane = new JScrollPane(tabela);
        add(scrollPane, BorderLayout.CENTER);

        // Painel de botões
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10)); // Adiciona espaçamento
        btnAdicionar = new JButton("Adicionar");
        btnEditar = new JButton("Editar");
        btnExcluir = new JButton("Excluir");
        btnAtualizar = new JButton("Atualizar Lista");

        painelBotoes.add(btnAdicionar);
        painelBotoes.add(btnEditar);
        painelBotoes.add(btnExcluir);
        painelBotoes.add(btnAtualizar);
        add(painelBotoes, BorderLayout.SOUTH);

        // --- Ações dos Botões ---
        btnAdicionar.addActionListener(e -> abrirDialogoConsulta(null));

        btnEditar.addActionListener(e -> {
            int linhaSelecionada = tabela.getSelectedRow();
            if (linhaSelecionada >= 0) {
                int consultaId = (int) modelo.getValueAt(linhaSelecionada, 0);
                Consulta consultaParaEditar = controller.buscarConsultaPorId(consultaId);
                if (consultaParaEditar != null) {
                    abrirDialogoConsulta(consultaParaEditar);
                } else {
                    JOptionPane.showMessageDialog(this, "Consulta não encontrada.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Selecione uma consulta para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        });

        btnExcluir.addActionListener(e -> {
            int linhaSelecionada = tabela.getSelectedRow();
            if (linhaSelecionada >= 0) {
                int consultaId = (int) modelo.getValueAt(linhaSelecionada, 0);
                int confirm = JOptionPane.showConfirmDialog(this,
                        "Tem certeza que deseja excluir a consulta ID: " + consultaId + "?",
                        "Confirmar Exclusão", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (confirm == JOptionPane.YES_OPTION) {
                    controller.removerConsulta(consultaId);
                    carregarConsultas(); // Atualiza a tabela
                }
            } else {
                JOptionPane.showMessageDialog(this, "Selecione uma consulta para excluir.", "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        });

        btnAtualizar.addActionListener(e -> carregarConsultas());

        // Carrega dados iniciais
        carregarConsultas();
    }

    // Abre o diálogo para adicionar ou editar
    private void abrirDialogoConsulta(Consulta consulta) {
        // Assume que existe um Frame pai, pode precisar ajustar
        JFrame framePai = (JFrame) SwingUtilities.getWindowAncestor(this);
        ConsultaFormDialog dialog = new ConsultaFormDialog(framePai, consulta);
        dialog.setVisible(true);

        // Se o diálogo foi salvo com sucesso, atualiza a tabela
        if (dialog.foiSalvo()) {
            carregarConsultas();
        }
    }


    private void carregarConsultas() {
        try {
            List<Consulta> consultas = controller.listarTodasConsultas();
            atualizarTabela(consultas); // Chama o método correto
        } catch (Exception e) {
             System.err.println("Erro ao carregar consultas: " + e.getMessage());
             e.printStackTrace();
             JOptionPane.showMessageDialog(this, "Erro ao carregar consultas:\n" + e.getMessage(), "Erro de Carregamento", JOptionPane.ERROR_MESSAGE);
        }
    }

    // --- MÉTODO ATUALIZAR TABELA CORRIGIDO (VERSÃO OTIMIZADA) ---
    // Aceita List<Consulta> e exibe os nomes diretamente
    // Dentro da classe PainelGerenciarConsultas

// --- MÉTODO ATUALIZAR TABELA CORRIGIDO ---
// Aceita List<Consulta> e exibe os nomes diretamente
private void atualizarTabela(List<Consulta> lista) {
    modelo.setRowCount(0); // Limpa tabela

    if (dtf == null) {
        dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    }

    if (lista != null) { // Adiciona verificação de nulidade para a lista
        for (Consulta c : lista) {
            if (c == null) continue; // Pula consultas nulas

            String dataFormatada = (c.getDataHora() != null) ? dtf.format(c.getDataHora()) : "N/A";
            String clienteInfo = (c.getCliente() != null && c.getCliente().getNome() != null)
                               ? c.getCliente().getId() + " - " + c.getCliente().getNome()
                               : (c.getCliente() != null ? c.getCliente().getId() + " - ?" : "N/A");

            String animalInfo = (c.getAnimal() != null && c.getAnimal().getNome() != null)
                              ? c.getAnimal().getId() + " - " + c.getAnimal().getNome()
                              : (c.getAnimal() != null ? c.getAnimal().getId() + " - ?" : "-"); // Ajuste para mostrar ID se nome for nulo, ou "-"

            String vetInfo = (c.getVeterinario() != null && c.getVeterinario().getNome() != null)
                           ? c.getVeterinario().getId() + " - " + c.getVeterinario().getNome()
                           : (c.getVeterinario() != null ? c.getVeterinario().getId() + " - ?" : "N/A");

            modelo.addRow(new Object[]{
                c.getId(),
                dataFormatada,
                c.getStatus() != null ? c.getStatus() : "N/A",
                clienteInfo,
                animalInfo,
                vetInfo
            });
        }
    }
    // --- ADICIONAR ESTA LINHA ---
    // ---------------------------
}
}
