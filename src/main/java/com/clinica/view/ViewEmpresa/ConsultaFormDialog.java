package com.clinica.view.ViewEmpresa;

import com.clinica.controller.ClienteController;
import com.clinica.controller.VeterinarioController;
import com.clinica.model.Consulta;
import com.clinica.model.Cliente;
import com.clinica.model.Veterinario;
import com.clinica.Util.ValidadorUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public class ConsultaFormDialog extends JDialog {

    private JFormattedTextField campoDataHora;
    private JComboBox<String> comboStatus;
    private JTextField campoCliente; 

    private JComboBox<VeterinarioComboItem> comboVeterinario;
    private boolean salvo = false;
    private Consulta consulta;

    public ConsultaFormDialog(JFrame parent, Consulta consultaExistente) {
        super(parent, true);
        setTitle(consultaExistente == null ? "Adicionar Consulta" : "Editar Consulta");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(new EmptyBorder(15, 15, 15, 15));
        getContentPane().add(panelPrincipal);

        
        JLabel lblTitulo = new JLabel("Cadastro de Consulta", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(new Color(0, 102, 204));
        panelPrincipal.add(lblTitulo, BorderLayout.NORTH);

        
        JPanel panelForm = new JPanel(new GridBagLayout());
        panelForm.setBorder(new EmptyBorder(10, 10, 10, 10));
        panelPrincipal.add(panelForm, BorderLayout.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        try {
            campoDataHora = new JFormattedTextField(dateFormat);
            campoDataHora.setColumns(20);
        } catch (Exception ex) {
            campoDataHora = new JFormattedTextField();
            ex.printStackTrace();
        }
        addField(panelForm, gbc, 0, "Data/Hora (dd/MM/yyyy HH:mm):", campoDataHora);

        
        comboStatus = new JComboBox<>(new String[]{"AGENDADA", "CONCLUIDA", "CANCELADA"});
        addField(panelForm, gbc, 1, "Status:", comboStatus);

        
        campoCliente = new JTextField(20);
        addField(panelForm, gbc, 2, "Cliente (Nome):", campoCliente);

        
        comboVeterinario = new JComboBox<>();
        comboVeterinario.setPreferredSize(new Dimension(250, 25));
        addField(panelForm, gbc, 3, "Veterinário:", comboVeterinario);

        
        carregarVeterinariosCombo();

        // Painel de botões: Salvar e Cancelar
        JPanel panelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        JButton btnCancelar = new JButton("Cancelar");
        JButton btnSalvar = new JButton("Salvar");

        btnCancelar.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnSalvar.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        panelBotoes.add(btnCancelar);
        panelBotoes.add(btnSalvar);
        panelPrincipal.add(panelBotoes, BorderLayout.SOUTH);

        this.consulta = consultaExistente;
        if (consultaExistente != null) {
            try {
                if (consultaExistente.getDataHora() != null) {
                    campoDataHora.setText(dateFormat.format(java.sql.Timestamp.valueOf(consultaExistente.getDataHora())));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            comboStatus.setSelectedItem(consultaExistente.getStatus());
            campoCliente.setText(consultaExistente.getCliente() != null ? consultaExistente.getCliente().getNome() : "");

            if (consultaExistente.getVeterinario() != null) {
                selecionarVeterinarioNoCombo(consultaExistente.getVeterinario().getId());
            }
        }

        btnSalvar.addActionListener(e -> {
            try {
                validarCampos();
                salvo = true;
                dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro de Validação", JOptionPane.WARNING_MESSAGE);
            }
        });

        btnCancelar.addActionListener(e -> dispose());

        pack();
        setLocationRelativeTo(parent);
    }


    private void addField(JPanel panel, GridBagConstraints gbc, int rowIndex, String labelText, JComponent comp) {
        gbc.gridx = 0;
        gbc.gridy = rowIndex;
        gbc.weightx = 0.0;
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(new Color(60, 60, 60));
        panel.add(label, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        comp.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panel.add(comp, gbc);
    }


    private void validarCampos() throws Exception {
        String dataHoraStr = campoDataHora.getText().trim();
        String status = (String) comboStatus.getSelectedItem();
        String nomeCliente = campoCliente.getText().trim();
        // Verifica se há um veterinário selecionado
        if (dataHoraStr.isEmpty() || status.isEmpty() || nomeCliente.isEmpty() || comboVeterinario.getSelectedItem() == null) {
            throw new Exception("⚠️ Os campos obrigatórios (Data/Hora, Status, Cliente e Veterinário) devem ser preenchidos.");
        }

        try {
            new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(dataHoraStr);
        } catch (ParseException e) {
            throw new Exception("Data/Hora inválida! Use o formato dd/MM/yyyy HH:mm");
        }
    }

    public boolean foiSalvo() {
        return salvo;
    }

   
    public Consulta getConsulta() {
        LocalDateTime dataHora = null;
        String dataHoraStr = campoDataHora.getText().trim();
        try {
            Date date = new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(dataHoraStr);
            dataHora = date.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this, "Erro na conversão da data/hora: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Data/Hora inválida.");
        }
        
        String status = (String) comboStatus.getSelectedItem();
        
        String nomeCliente = campoCliente.getText().trim();
        ClienteController clienteController = new ClienteController();
        Cliente cliente = (Cliente) clienteController.buscarClientePorNome(nomeCliente);
        if (cliente == null) {
            throw new RuntimeException("Cliente não encontrado com o nome: " + nomeCliente);
        }
        
        VeterinarioComboItem item = (VeterinarioComboItem) comboVeterinario.getSelectedItem();
        if (item == null) {
            throw new RuntimeException("Nenhum veterinário selecionado.");
        }
        VeterinarioController vetController = new VeterinarioController();
        Veterinario vet = vetController.buscarVeterinarioPorId(item.getId());
        if (vet == null) {
            throw new RuntimeException("Veterinário não encontrado com o ID: " + item.getId());
        }
        
        Consulta consulta = new Consulta(dataHora, status, cliente, null, vet);
        return consulta;
    }

    
    private void carregarVeterinariosCombo() {
        comboVeterinario.removeAllItems();
        VeterinarioController vetController = new VeterinarioController();
        List<Veterinario> vets = vetController.listarTodosVeterinarios();
        for (Veterinario v : vets) {
            comboVeterinario.addItem(new VeterinarioComboItem(v.getId(), v.getNome()));
        }
    }

    
    private void selecionarVeterinarioNoCombo(int vetId) {
        for (int i = 0; i < comboVeterinario.getItemCount(); i++) {
            VeterinarioComboItem item = comboVeterinario.getItemAt(i);
            if (item.getId() == vetId) {
                comboVeterinario.setSelectedIndex(i);
                break;
            }
        }
    }
}