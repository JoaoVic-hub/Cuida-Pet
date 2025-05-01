package com.clinica.view.ViewEmpresa;

import com.clinica.model.Veterinario;
import com.clinica.Util.ValidadorUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class VeterinarioFormDialog extends JDialog {

    private JTextField campoNome;
    private JTextField campoEmail;
    private JTextField campoTelefone;
    private JTextField campoCpf;
    private JPasswordField campoSenha;
    private JTextField campoCrmv;
    private JTextField campoEspecialidade;
    private boolean salvo = false;
    private Veterinario veterinario;

    public VeterinarioFormDialog(JFrame parent, Veterinario vetExistente) {
        super(parent, true);
        setTitle(vetExistente == null ? "Adicionar Veterinário" : "Editar Veterinário");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(new EmptyBorder(15, 15, 15, 15));
        getContentPane().add(panelPrincipal);

        JLabel lblTitulo = new JLabel("Cadastro de Veterinário", SwingConstants.CENTER);
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

        try {
            addField(panelForm, gbc, 0, "Nome:", campoNome = new JTextField(20));
            addField(panelForm, gbc, 1, "Email:", campoEmail = new JTextField(20));
            addField(panelForm, gbc, 2, "Telefone:", campoTelefone = new JTextField(15));
            addField(panelForm, gbc, 3, "CPF:", campoCpf = new JTextField(15));
            addField(panelForm, gbc, 4, "Senha:", campoSenha = new JPasswordField(15));
            addField(panelForm, gbc, 5, "CRMV:", campoCrmv = new JTextField(15));
            addField(panelForm, gbc, 6, "Especialidade:", campoEspecialidade = new JTextField(20));
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao criar os campos do formulário: " + e.getMessage(),
                    "Erro na Interface", JOptionPane.ERROR_MESSAGE);
        }

        
        JPanel panelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        JButton btnCancelar = new JButton("Cancelar");
        JButton btnSalvar = new JButton("Salvar");
        btnCancelar.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnSalvar.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panelBotoes.add(btnCancelar);
        panelBotoes.add(btnSalvar);
        panelPrincipal.add(panelBotoes, BorderLayout.SOUTH);

        this.veterinario = vetExistente;
        if (vetExistente != null) {
            try {
                campoNome.setText(vetExistente.getNome());
                campoEmail.setText(vetExistente.getEmail());
                campoTelefone.setText(vetExistente.getTelefone());
                campoCpf.setText(vetExistente.getCpf());
                
                campoCrmv.setText(vetExistente.getCrmv());
                campoEspecialidade.setText(vetExistente.getEspecialidade());
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Erro ao carregar os dados existentes: " + e.getMessage(),
                        "Erro de Carregamento", JOptionPane.ERROR_MESSAGE);
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

    private void addField(JPanel panel, GridBagConstraints gbc, int rowIndex, String labelText, JComponent field) {
        gbc.gridx = 0;
        gbc.gridy = rowIndex;
        gbc.weightx = 0.0;
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(new Color(60, 60, 60));
        panel.add(label, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panel.add(field, gbc);
    }

   
    private void validarCampos() throws Exception {
        String nome = campoNome.getText().trim();
        String email = campoEmail.getText().trim();
        String telefone = campoTelefone.getText().trim();
        String cpf = campoCpf.getText().trim();
        String senha = new String(campoSenha.getPassword()).trim();
        String crmv = campoCrmv.getText().trim();
        String especialidade = campoEspecialidade.getText().trim();

        if (nome.isEmpty() || email.isEmpty() || telefone.isEmpty() || cpf.isEmpty() ||
                senha.isEmpty() || crmv.isEmpty() || especialidade.isEmpty()) {
            throw new Exception("⚠️ Todos os campos devem ser preenchidos.");
        }
        if (!ValidadorUtil.isEmailValido(email)) {
            throw new Exception("Email inválido! Use o formato nome@dominio.com.");
        }
        if (!ValidadorUtil.isTelefoneValido(telefone)) {
            throw new Exception("Telefone inválido! Use apenas números e símbolos, como () ou -.");
        }

        if (!ValidadorUtil.isCrmvValido(crmv)) {
            throw new Exception("CRMV inválido! Use o formato: CRMV-XX 00000-Z");
        }
        
    }

    
    public boolean foiSalvo() {
        return salvo;
    }

    
    public Veterinario getVeterinario() {
        try {
            String nome = campoNome.getText().trim();
            String email = campoEmail.getText().trim();
            String telefone = campoTelefone.getText().trim();
            String cpf = campoCpf.getText().trim();
            String senha = new String(campoSenha.getPassword()).trim();
            String crmv = campoCrmv.getText().trim();
            String especialidade = campoEspecialidade.getText().trim();

            if (veterinario == null) {
                veterinario = new Veterinario(nome, email, telefone, cpf, senha, crmv, especialidade);
            } else {
                veterinario.setNome(nome);
                veterinario.setEmail(email);
                veterinario.setTelefone(telefone);
                veterinario.setCpf(cpf);
                veterinario.setSenha(senha);
                veterinario.setCrmv(crmv);
                veterinario.setEspecialidade(especialidade);
            }
            return veterinario;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao montar o objeto Veterinario: " + e.getMessage(), e);
        }
    }
}
