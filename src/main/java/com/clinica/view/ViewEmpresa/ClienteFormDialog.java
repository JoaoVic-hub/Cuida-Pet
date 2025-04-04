package com.clinica.view.ViewEmpresa;

import com.clinica.model.Cliente;
import com.clinica.Util.ValidadorUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ClienteFormDialog extends JDialog {

    private JTextField campoNome, campoCEP, campoEndereco, campoEmail, campoTelefone, campoCpf;
    private boolean salvo = false;
    private Cliente cliente;

    public ClienteFormDialog(JFrame parent, Cliente clienteExistente) {
        super(parent, true);
        setTitle(clienteExistente == null ? "Adicionar Cliente" : "Editar Cliente");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // Painel principal com BorderLayout e margens
        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(new EmptyBorder(15, 15, 15, 15));
        getContentPane().add(panelPrincipal);

        // Cabeçalho: Título centralizado
        JLabel lblTitulo = new JLabel("Cadastro de Cliente", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));      // Título maior e em negrito
        lblTitulo.setForeground(new Color(0, 102, 204));             // Azul mais vivo
        panelPrincipal.add(lblTitulo, BorderLayout.NORTH);

        // Painel do formulário com GridBagLayout
        JPanel panelForm = new JPanel(new GridBagLayout());
        panelForm.setBorder(new EmptyBorder(10, 10, 10, 10));
        panelPrincipal.add(panelForm, BorderLayout.CENTER);

        // Cor de fundo suave (opcional)
        // panelForm.setBackground(new Color(245, 245, 245));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Método auxiliar para adicionar rótulos e campos de texto com estilo
        addField(panelForm, gbc, 0, "Nome:", campoNome = new JTextField(20));
        addField(panelForm, gbc, 1, "CEP:", campoCEP = new JTextField(10));
        addField(panelForm, gbc, 2, "Endereço:", campoEndereco = new JTextField(20));
        addField(panelForm, gbc, 3, "Email:", campoEmail = new JTextField(20));
        addField(panelForm, gbc, 4, "Telefone:", campoTelefone = new JTextField(15));
        addField(panelForm, gbc, 5, "CPF:", campoCpf = new JTextField(15));

        // Painel de botões: Salvar e Cancelar
        JPanel panelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        JButton btnCancelar = new JButton("Cancelar");
        JButton btnSalvar = new JButton("Salvar");

        // Deixe os botões com uma fonte e cor mais destacada
        btnCancelar.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnSalvar.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        // Cor de fundo dos botões (opcional)
        // btnCancelar.setBackground(new Color(220, 220, 220));
        // btnSalvar.setBackground(new Color(0, 153, 51));
        // btnSalvar.setForeground(Color.WHITE);

        panelBotoes.add(btnCancelar);
        panelBotoes.add(btnSalvar);
        panelPrincipal.add(panelBotoes, BorderLayout.SOUTH);

        // Preenche os campos se for edição
        this.cliente = clienteExistente;
        if (clienteExistente != null) {
            campoNome.setText(clienteExistente.getNome());
            campoEndereco.setText(clienteExistente.getEndereco());
            campoEmail.setText(clienteExistente.getEmail());
            campoTelefone.setText(clienteExistente.getTelefone());
            campoCpf.setText(clienteExistente.getCpf());
            // Se houver CEP armazenado, você pode preencher o campoCEP aqui.
        }

        // Ação dos botões
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

    /**
     * Método auxiliar para adicionar um rótulo e um campo de texto na mesma linha do GridBagLayout.
     */
    private void addField(JPanel panel, GridBagConstraints gbc, int rowIndex, String labelText, JTextField textField) {
        // Rótulo
        gbc.gridx = 0;
        gbc.gridy = rowIndex;
        gbc.weightx = 0.0;

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));    // Rótulo em negrito
        label.setForeground(new Color(60, 60, 60));            // Cor cinza escuro
        panel.add(label, gbc);

        // Campo de texto
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
                campoNome.getText().trim(),
                campoEndereco.getText().trim(),
                campoEmail.getText().trim(),
                campoTelefone.getText().trim(),
                campoCpf.getText().trim()
            );
        } else {
            cliente.setNome(campoNome.getText().trim());
            cliente.setEndereco(campoEndereco.getText().trim());
            cliente.setEmail(campoEmail.getText().trim());
            cliente.setTelefone(campoTelefone.getText().trim());
            cliente.setCpf(campoCpf.getText().trim());
        }
        return cliente;
    }

    // Método para acessar o valor do CEP
    public String getCep() {
        return campoCEP.getText().trim();
    }

    // Exemplo de uso
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ClienteFormDialog dialog = new ClienteFormDialog(null, null);
            dialog.setVisible(true);
            if (dialog.foiSalvo()) {
                Cliente cli = dialog.getCliente();
                System.out.println("Cliente cadastrado:");
                System.out.println("Nome: " + cli.getNome());
                System.out.println("CEP: " + dialog.getCep());
                System.out.println("Endereço: " + cli.getEndereco());
                System.out.println("Email: " + cli.getEmail());
                System.out.println("Telefone: " + cli.getTelefone());
                System.out.println("CPF: " + cli.getCpf());
            }
        });
    }
}
