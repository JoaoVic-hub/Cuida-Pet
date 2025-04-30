package com.clinica.view.ViewEmpresa; // Pacote correto

import com.clinica.facade.ClinicaFacade; // Importar a Facade
import com.clinica.model.Consulta;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class PainelGerenciarConsultas extends JPanel {

    private JTable tabela;
    private DefaultTableModel modelo;
    private JScrollPane scrollPane;
    // REMOVER: private ConsultaController controller = new ConsultaController();
    private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private JButton btnAdicionar, btnEditar, btnExcluir, btnAtualizar;

    // --- USAR A FACADE ---
    private ClinicaFacade facade = ClinicaFacade.getInstance();
    // ---------------------

    public PainelGerenciarConsultas() {
        setLayout(new BorderLayout(10, 10));

        // ... (Configuração da UI: título, tabela, scrollpane - sem mudanças) ...
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
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnAdicionar = new JButton("Adicionar");
        btnEditar = new JButton("Editar");
        btnExcluir = new JButton("Excluir");
        btnAtualizar = new JButton("Atualizar Lista");

        painelBotoes.add(btnAdicionar);
        painelBotoes.add(btnEditar);
        painelBotoes.add(btnExcluir);
        painelBotoes.add(btnAtualizar);
        add(painelBotoes, BorderLayout.SOUTH);

        // --- Ações dos Botões (AJUSTAR CHAMADAS) ---
        btnAdicionar.addActionListener(e -> abrirDialogoConsulta(null)); // Dialog usará facade

        btnEditar.addActionListener(e -> {
            int linhaSelecionada = tabela.getSelectedRow();
            if (linhaSelecionada >= 0) {
                int consultaId = (int) modelo.getValueAt(linhaSelecionada, 0);
                // USA A FACADE
                Consulta consultaParaEditar = facade.buscarConsultaPorId(consultaId);
                if (consultaParaEditar != null) {
                    abrirDialogoConsulta(consultaParaEditar); // Dialog usará facade
                } else {
                    JOptionPane.showMessageDialog(this, "Consulta não encontrada (ID: " + consultaId +"). Atualize a lista.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Selecione uma consulta para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        });

        btnExcluir.addActionListener(e -> {
            int linhaSelecionada = tabela.getSelectedRow();
            if (linhaSelecionada >= 0) {
                int consultaId = (int) modelo.getValueAt(linhaSelecionada, 0);
                 // Adiciona verificação se consulta existe
                 Consulta existente = facade.buscarConsultaPorId(consultaId);
                 if (existente == null) {
                     JOptionPane.showMessageDialog(this, "Consulta não encontrada para exclusão (ID: " + consultaId + "). Atualize a lista.", "Erro", JOptionPane.ERROR_MESSAGE);
                     return;
                 }

                int confirm = JOptionPane.showConfirmDialog(this,
                        "Tem certeza que deseja excluir a consulta ID: " + consultaId + "?",
                        "Confirmar Exclusão", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (confirm == JOptionPane.YES_OPTION) {
                    // USA A FACADE
                    facade.removerConsulta(consultaId);
                    carregarConsultas(); // Atualiza a tabela
                }
            } else {
                JOptionPane.showMessageDialog(this, "Selecione uma consulta para excluir.", "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        });

        btnAtualizar.addActionListener(e -> carregarConsultas()); // Método usa facade

        // Carrega dados iniciais
        carregarConsultas();
    }

    // Abre o diálogo para adicionar ou editar (O DIÁLOGO PRECISA SER AJUSTADO TAMBÉM)
    private void abrirDialogoConsulta(Consulta consulta) {
        JFrame framePai = (JFrame) SwingUtilities.getWindowAncestor(this);
        // O ConsultaFormDialog PRECISA ser modificado para usar a Facade também!
        ConsultaFormDialog dialog = new ConsultaFormDialog(framePai, consulta);
        dialog.setVisible(true);

        if (dialog.foiSalvo()) {
            carregarConsultas(); // Recarrega a lista neste painel
        }
    }


    private void carregarConsultas() {
        try {
            // USA A FACADE
            List<Consulta> consultas = facade.listarTodasConsultas();
            atualizarTabela(consultas);
        } catch (Exception e) {
             System.err.println("Erro ao carregar consultas: " + e.getMessage());
             e.printStackTrace();
             JOptionPane.showMessageDialog(this, "Erro ao carregar consultas:\n" + e.getMessage(), "Erro de Carregamento", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Método atualizarTabela permanece o mesmo, mas recebe dados da facade
    private void atualizarTabela(List<Consulta> lista) {
        modelo.setRowCount(0); // Limpa tabela

        if (dtf == null) {
            dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        }

        if (lista != null) {
            for (Consulta c : lista) {
                if (c == null) continue;

                String dataFormatada = (c.getDataHora() != null) ? dtf.format(c.getDataHora()) : "N/A";

                // Nome Cliente (verifica se objeto e nome existem)
                 String clienteInfo = (c.getCliente() != null && c.getCliente().getNome() != null)
                                   ? c.getCliente().getId() + " - " + c.getCliente().getNome()
                                   : (c.getCliente() != null ? c.getCliente().getId() + " - ?" : "N/A");

                // Nome Animal (verifica se objeto e nome existem)
                 String animalInfo = (c.getAnimal() != null && c.getAnimal().getNome() != null)
                                  ? c.getAnimal().getId() + " - " + c.getAnimal().getNome()
                                  : (c.getAnimal() != null ? c.getAnimal().getId() + " - ?" : "-"); // Se animal existe mas nome não, mostra ID; senão "-"

                 // Nome Veterinário (verifica se objeto e nome existem)
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
        // Notifica a tabela sobre a mudança de dados
        modelo.fireTableDataChanged();
    }
} 