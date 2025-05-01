package com.clinica.view.ViewEmpresa;

import com.clinica.facade.ClinicaFacade;
import com.clinica.model.Consulta;
import com.clinica.observer.DataObserver; // << Importar Observer
import com.clinica.observer.DataType;     // << Importar DataType
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

// Implementa a interface DataObserver
public class PainelGerenciarConsultas extends JPanel implements DataObserver {

    private JTable tabela;
    private DefaultTableModel modelo;
    private JScrollPane scrollPane;
    private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private JButton btnAdicionar, btnEditar, btnExcluir; // Botão Atualizar removido
    private ClinicaFacade facade = ClinicaFacade.getInstance();

    public PainelGerenciarConsultas() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- UI ---
        JLabel titulo = new JLabel("📋 Gerenciamento de Consultas", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        add(titulo, BorderLayout.NORTH);

        modelo = new DefaultTableModel(
                new String[]{"ID", "Data/Hora", "Status", "Cliente", "Animal", "Veterinário"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tabela = new JTable(modelo);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabela.setRowHeight(22);
        tabela.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        scrollPane = new JScrollPane(tabela);
        add(scrollPane, BorderLayout.CENTER);

        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnAdicionar = new JButton("➕ Adicionar");
        btnEditar = new JButton("✏️ Editar");
        btnExcluir = new JButton("🗑️ Excluir");
        // REMOVIDO: JButton btnAtualizar = new JButton("Atualizar Lista");
        painelBotoes.add(btnAdicionar);
        painelBotoes.add(btnEditar);
        painelBotoes.add(btnExcluir);
        // REMOVIDO: painelBotoes.add(btnAtualizar);
        add(painelBotoes, BorderLayout.SOUTH);

        // --- Listeners ---
        btnAdicionar.addActionListener(e -> abrirDialogoConsulta(null));
        btnEditar.addActionListener(e -> editarConsulta());
        btnExcluir.addActionListener(e -> excluirConsulta());
        // REMOVIDO: btnAtualizar.addActionListener(e -> carregarConsultas());

        // --- Inicialização ---
        carregarConsultas(); // Carrega dados iniciais

        // --- REGISTRAR COMO OBSERVER ---
        facade.removeObserver(this);
        facade.addObserver(this);
         // Tenta remover primeiro
        // -----------------------------
    }

    // --- Implementação do Método update() ---
    @Override
    public void update(DataType typeChanged) {
        System.out.println("PainelGerenciarConsultas notificado sobre: " + typeChanged); // Log
        // Este painel precisa recarregar se Consultas, Clientes, Animais ou Veterinários mudarem
        // (pois exibe informações de todos eles)
        if (typeChanged == DataType.CONSULTA ||
            typeChanged == DataType.CLIENTE ||
            typeChanged == DataType.ANIMAL ||
            typeChanged == DataType.VETERINARIO)
        {
            System.out.println("-> Recarregando consultas...");
            carregarConsultas();
        }
    }

    // --- Métodos de Ação ---

    private void abrirDialogoConsulta(Consulta consulta) {
        JFrame framePai = (JFrame) SwingUtilities.getWindowAncestor(this);
        // O ConsultaFormDialog PRECISA ser modificado para usar a Facade também!
        ConsultaFormDialog dialog = new ConsultaFormDialog(framePai, consulta);
        dialog.setVisible(true);
        // if (dialog.foiSalvo()) { // Não precisa mais checar, a facade notificará
        //     carregarConsultas();
        // }
    }

     private void editarConsulta() {
        int linhaSelecionada = tabela.getSelectedRow();
        if (linhaSelecionada >= 0) {
            int consultaId = (int) modelo.getValueAt(linhaSelecionada, 0);
            Consulta consultaParaEditar = null;
            try {
                 consultaParaEditar = facade.buscarConsultaPorId(consultaId);
            } catch (Exception e) {
                 e.printStackTrace(); JOptionPane.showMessageDialog(this, "Erro ao buscar consulta:\n" + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE); return;
            }

            if (consultaParaEditar != null) {
                abrirDialogoConsulta(consultaParaEditar);
            } else {
                JOptionPane.showMessageDialog(this, "Consulta (ID: " + consultaId +") não encontrada. A lista pode estar desatualizada.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Selecione uma consulta na tabela para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }

     private void excluirConsulta() {
        int linhaSelecionada = tabela.getSelectedRow();
        if (linhaSelecionada >= 0) {
            int consultaId = (int) modelo.getValueAt(linhaSelecionada, 0);
            // Opcional: Buscar a consulta para mostrar detalhes na confirmação
            Consulta c = null;
            try { c = facade.buscarConsultaPorId(consultaId); } catch (Exception e) { /* Ignora erro aqui */ }
            String msg = (c != null) ? "Tem certeza que deseja excluir a consulta de " + (c.getCliente() != null ? c.getCliente().getNome() : "?") + " em " + (c.getDataHora() != null ? dtf.format(c.getDataHora()) : "?") : "Tem certeza que deseja excluir a consulta ID: " + consultaId;

            int confirm = JOptionPane.showConfirmDialog(this,
                    msg + "?\nEsta ação não pode ser desfeita.",
                    "Confirmar Exclusão", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                 try {
                    facade.removerConsulta(consultaId); // Facade notificará
                    // carregarConsultas(); // Não precisa mais
                    JOptionPane.showMessageDialog(this, "Consulta excluída!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                 } catch (Exception e) {
                    e.printStackTrace(); JOptionPane.showMessageDialog(this, "Erro ao excluir consulta:\n" + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                 }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Selecione uma consulta na tabela para excluir.", "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }


    // --- Método para Carregar/Atualizar Tabela ---

    private void carregarConsultas() {
        modelo.setRowCount(0); // Limpa tabela
        try {
            List<Consulta> consultas = facade.listarTodasConsultas(); // Busca via Facade
            if (consultas != null) {
                for (Consulta c : consultas) {
                    if (c == null) continue;

                    String dataFormatada = (c.getDataHora() != null) ? dtf.format(c.getDataHora()) : "N/A";
                    // Obtém nomes dos relacionamentos (tratando nulls)
                    String clienteInfo = (c.getCliente() != null) ? (c.getCliente().getId() + " - " + (c.getCliente().getNome() != null ? c.getCliente().getNome() : "?")) : "N/A";
                    String animalInfo = (c.getAnimal() != null) ? (c.getAnimal().getId() + " - " + (c.getAnimal().getNome() != null ? c.getAnimal().getNome() : "?")) : "-";
                    String vetInfo = (c.getVeterinario() != null) ? (c.getVeterinario().getId() + " - " + (c.getVeterinario().getNome() != null ? c.getVeterinario().getNome() : "?")) : "N/A";

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
        } catch (Exception e) {
             System.err.println("Erro ao carregar consultas no painel: " + e.getMessage());
             e.printStackTrace();
             JOptionPane.showMessageDialog(this, "Erro ao carregar dados das consultas:\n" + e.getMessage(), "Erro de Carregamento", JOptionPane.ERROR_MESSAGE);
        }
        modelo.fireTableDataChanged(); // Notifica a tabela
    }

} // Fim da classe PainelGerenciarConsultas
