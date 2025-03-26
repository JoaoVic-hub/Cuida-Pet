package com.clinica.view.ViewEmpresa;

import com.clinica.model.Cliente;
import com.clinica.Util.ValidadorUtil;

import javax.swing.*;
import java.awt.*;

public class ClienteFormDialog extends JDialog {

    private JTextField campoNome, campoEndereco, campoEmail, campoTelefone, campoCpf;
    private boolean salvo = false;
    private Cliente cliente;

    public ClienteFormDialog(JFrame parent, Cliente clienteExistente) {
        super(parent, true);
        setTitle(clienteExistente == null ? "Adicionar Cliente" : "Editar Cliente");
        setSize(400, 300);
        setLocationRelativeTo(parent);
        setLayout(new GridLayout(7, 2, 5, 5));

        this.cliente = clienteExistente;

        add(new JLabel("Nome:"));
        campoNome = new JTextField();
        add(campoNome);

        add(new JLabel("Endereço:"));
        campoEndereco = new JTextField();
        add(campoEndereco);

        add(new JLabel("Email:"));
        campoEmail = new JTextField();
        add(campoEmail);

        add(new JLabel("Telefone:"));
        campoTelefone = new JTextField();
        add(campoTelefone);

        add(new JLabel("CPF:"));
        campoCpf = new JTextField();
        add(campoCpf);

        JButton btnSalvar = new JButton("Salvar");
        JButton btnCancelar = new JButton("Cancelar");

        add(btnSalvar);
        add(btnCancelar);

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
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro de Validação", JOptionPane.WARNING_MESSAGE);
            }
        });

        btnCancelar.addActionListener(e -> dispose());
    }

    private void validarCampos() throws Exception {
        String nome = campoNome.getText().trim();
        String endereco = campoEndereco.getText().trim();
        String email = campoEmail.getText().trim();
        String telefone = campoTelefone.getText().trim();
        String cpf = campoCpf.getText().trim();

        if (nome.isEmpty() || endereco.isEmpty() || email.isEmpty() || telefone.isEmpty() || cpf.isEmpty()) {
            throw new Exception("⚠️ Todos os campos devem ser preenchidos.");
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
        if (cliente == null) {
            cliente = new Cliente(
                campoNome.getText(),
                campoEndereco.getText(),
                campoEmail.getText(),
                campoTelefone.getText(),
                campoCpf.getText()
            );
        } else {
            cliente.setNome(campoNome.getText());
            cliente.setEndereco(campoEndereco.getText());
            cliente.setEmail(campoEmail.getText());
            cliente.setTelefone(campoTelefone.getText());
            cliente.setCpf(campoCpf.getText());
        }
        return cliente;
    }
}
