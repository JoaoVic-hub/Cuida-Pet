package com.clinica.view.ViewEmpresa;

import com.clinica.controller.AnimalController;
import com.clinica.controller.ClienteController;
import com.clinica.controller.ConsultaController;
import com.clinica.controller.VeterinarioController;
import com.clinica.model.Consulta;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PainelGerenciarConsultas extends JPanel {

    private JTable tabela;
    private DefaultTableModel modelo;
    private JScrollPane scrollPane;
    private ConsultaController controller = new ConsultaController();
    private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public PainelGerenciarConsultas() {
        setLayout(new BorderLayout());

        // Cabeçalho com título
        JLabel titulo = new JLabel("Gerenciamento de Consultas", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titulo.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(titulo, BorderLayout.NORTH);

        // Configuração da tabela para exibição das consultas
        modelo = new DefaultTableModel(
                new String[]{"ID", "Data/Hora", "Status", "Cliente", "Animal", "Veterinário"}, 0
        );
        tabela = new JTable(modelo);
        tabela.setRowHeight(24);
        tabela.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabela.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        scrollPane = new JScrollPane(tabela);
        add(scrollPane, BorderLayout.CENTER);

        // Painel de botões para operações
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton btnAdicionar = new JButton("➕ Cadastrar");
        JButton btnEditar = new JButton("✏️ Editar");
        JButton btnExcluir = new JButton("🗑️ Deletar");
        JButton btnAtualizar = new JButton("🔄 Atualizar");
        painelBotoes.add(btnAdicionar);
        painelBotoes.add(btnEditar);
        painelBotoes.add(btnExcluir);
        painelBotoes.add(btnAtualizar);
        add(painelBotoes, BorderLayout.SOUTH);

        // Ação do botão "Cadastrar" (Adiciona nova consulta)
        btnAdicionar.addActionListener(e -> {
            // Abre o formulário de consulta; o segundo parâmetro nulo indica criação de nova consulta.
            ConsultaFormDialog dialog = new ConsultaFormDialog((JFrame) SwingUtilities.getWindowAncestor(this), null);
            dialog.setVisible(true);

            if (dialog.foiSalvo()) {
                Consulta novaConsulta = dialog.getConsulta();
                controller.adicionarConsulta(novaConsulta);
                carregarConsultas();
            }
        });

        // Ação do botão "Editar" (Edita a consulta selecionada)
        btnEditar.addActionListener(e -> {
            int linha = tabela.getSelectedRow();
            if (linha >= 0) {
                int id = (int) modelo.getValueAt(linha, 0);
                Consulta existente = controller.buscarConsultaPorId(id);
                ConsultaFormDialog dialog = new ConsultaFormDialog((JFrame) SwingUtilities.getWindowAncestor(this), existente);
                dialog.setVisible(true);
                if (dialog.foiSalvo()) {
                    Consulta atualizada = dialog.getConsulta();
                    controller.atualizarConsulta(id, atualizada);
                    carregarConsultas();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Selecione uma consulta para editar.");
            }
        });

        btnExcluir.addActionListener(e -> {
            int linha = tabela.getSelectedRow();
            if (linha >= 0) {
                int id = (int) modelo.getValueAt(linha, 0);
                int confirm = JOptionPane.showConfirmDialog(this, "Deseja excluir esta consulta?", "Confirmação", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    controller.removerConsulta(id);
                    carregarConsultas();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Selecione uma consulta para excluir.");
            }
        });

        btnAtualizar.addActionListener(e -> carregarConsultas());

        carregarConsultas();
    }

    private void carregarConsultas() {
        List<Consulta> consultas = controller.listarTodasConsultas();
        atualizarTabela(consultas);
    }

    private void atualizarTabela(List<Consulta> lista) {
        ClienteController cc = new ClienteController();
        VeterinarioController vc = new VeterinarioController();
        AnimalController ac = new AnimalController();
        modelo.setRowCount(0);
        for (Consulta c : lista) {
            String dataFormatada = (c.getDataHora() != null) ? dtf.format(c.getDataHora()) : "N/A";
            String clienteInfo = (c.getCliente() != null)
                    ? c.getCliente().getId() + " - " + cc.buscarClientePorId(c.getCliente().getId()).getNome()
                    : "N/A";
            String animalInfo = (c.getAnimal() == null)
                    ? c.getAnimal().getId() + " - " + ac.buscarAnimalPorId(c.getAnimal().getId()).getNome()
                    : "N/A";
            String vetInfo = (c.getVeterinario() != null)
                    ? c.getVeterinario().getId() + " - " + vc.buscarVeterinarioPorId(c.getVeterinario().getId()).getNome()
                    : "N/A";
    
            modelo.addRow(new Object[]{
                c.getId(),
                dataFormatada,
                c.getStatus(),
                clienteInfo,
                animalInfo,
                vetInfo
            });
        }
    }    
}
