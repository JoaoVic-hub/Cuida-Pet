package com.clinica.view.ViewEmpresa;

import com.clinica.model.Cliente;
import com.clinica.Util.ValidadorUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ClienteFormDialog extends JDialog {

    private JTextField campoNome, campoCEP, campoEndereco, campoEmail, campoTelefone, campoCpf;
    private JPasswordField campoSenha;  
    private boolean salvo = false;
    private Cliente cliente;

    public ClienteFormDialog(JFrame parent, Cliente clienteExistente) {
        super(parent, true);
        setTitle(clienteExistente == null ? "Adicionar Cliente" : "Editar Cliente");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(new EmptyBorder(15, 15, 15, 15));
        getContentPane().add(panelPrincipal);

        JLabel lblTitulo = new JLabel("Cadastro de Cliente", SwingConstants.CENTER);
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

        addField(panelForm, gbc, 0, "Nome:", campoNome = new JTextField(20));
        addField(panelForm, gbc, 1, "CEP:", campoCEP = new JTextField(10));
        addField(panelForm, gbc, 2, "Endereço:", campoEndereco = new JTextField(20));
        addField(panelForm, gbc, 3, "Email:", campoEmail = new JTextField(20));
        addField(panelForm, gbc, 4, "Telefone:", campoTelefone = new JTextField(15));
        addField(panelForm, gbc, 5, "CPF:", campoCpf = new JTextField(15));

        campoSenha = new JPasswordField(15);
        addField(panelForm, gbc, 6, "Senha:", campoSenha);

        JPanel panelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        JButton btnCancelar = new JButton("Cancelar");
        JButton btnSalvar = new JButton("Salvar");

        btnCancelar.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnSalvar.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        panelBotoes.add(btnCancelar);
        panelBotoes.add(btnSalvar);
        panelPrincipal.add(panelBotoes, BorderLayout.SOUTH);

        this.cliente = clienteExistente;
        if (clienteExistente != null) {
            campoNome.setText(clienteExistente.getNome());
            campoEndereco.setText(clienteExistente.getEndereco());
            campoEmail.setText(clienteExistente.getEmail());
            campoTelefone.setText(clienteExistente.getTelefone());
            campoCpf.setText(clienteExistente.getCpf());
        }

      
        btnSalvar.addActionListener(e -> {
            try {
                validarCampos();
                salvo = true;
                dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(),
                        "Erro de Validação", JOptionPane.WARNING_MESSAGE);
            }
        });

        btnCancelar.addActionListener(e -> dispose());

        pack();
        setLocationRelativeTo(parent);
    }

    private void addField(JPanel panel, GridBagConstraints gbc, int rowIndex,
                          String labelText, JComponent textField) {
        gbc.gridx = 0;
        gbc.gridy = rowIndex;
        gbc.weightx = 0.0;

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(new Color(60, 60, 60));
        panel.add(label, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panel.add(textField, gbc);
    }


    private void validarCampos() throws Exception {
        String nome = campoNome.getText().trim();
        String endereco = campoEndereco.getText().trim();
        String email = campoEmail.getText().trim();
        String telefone = campoTelefone.getText().trim();
        String cpf = campoCpf.getText().trim();
        String senha = new String(campoSenha.getPassword()).trim(); // Lê a senha

        if (nome.isEmpty() || endereco.isEmpty() || email.isEmpty()
                || telefone.isEmpty() || cpf.isEmpty() || senha.isEmpty()) {
            throw new Exception("⚠️ Todos os campos (inclusive senha) devem ser preenchidos.");
        }
        if (!cpf.matches("\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}")) {
            throw new Exception("CPF inválido! Use o formato 000.000.000-00");
        }
        if (!ValidadorUtil.isCpfValido(cpf)) {
            throw new Exception("CPF inválido! Dígitos verificadores incorretos.");
        }
        if (!ValidadorUtil.isEmailValido(email)) {
            throw new Exception("Email inválido! Use o formato nome@dominio.com");
        }
        if (!ValidadorUtil.isTelefoneValido(telefone)) {
            throw new Exception("Telefone inválido! Use apenas números e símbolos como () ou -");
        }
        if (!ValidadorUtil.isEnderecoValido(endereco)) {
            throw new Exception("Endereço inválido! Inclua o número da residência.");
        }
    }


    public boolean foiSalvo() {
        return salvo;
    }


    public Cliente getCliente() {
        String nome = campoNome.getText().trim();
        String endereco = campoEndereco.getText().trim();
        String email = campoEmail.getText().trim();
        String telefone = campoTelefone.getText().trim();
        String cpf = campoCpf.getText().trim();
        String senha = new String(campoSenha.getPassword()).trim();

        if (cliente == null) {
            cliente = new Cliente(nome, endereco, email, telefone, cpf, senha);
        } else {
            cliente.setNome(nome);
            cliente.setEndereco(endereco);
            cliente.setEmail(email);
            cliente.setTelefone(telefone);
            cliente.setCpf(cpf);
            cliente.setSenha(senha);
        }
        return cliente;
    }

    public String getCep() {
        return campoCEP.getText().trim();
    }
}
