package com.clinica.view;

import com.clinica.DAO.UsuarioRepository;
import com.clinica.controller.ClienteController;
import com.clinica.controller.VeterinarioController;
import com.clinica.model.Cliente;
import com.clinica.model.Veterinario;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class ClinicaView extends JFrame {

    private ClienteController clienteController;
    private VeterinarioController veterinarioController;
    private UsuarioRepository repository;

    public ClinicaView() {
        clienteController = new ClienteController();
        veterinarioController = new VeterinarioController();
        repository = UsuarioRepository.getInstance();
        initComponents();
    }

    private void initComponents() {
        setTitle("Clínica Veterinária");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 350);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(6, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton btnAddCliente = new JButton("Adicionar Cliente");
        JButton btnAddVet = new JButton("Adicionar Veterinário");
        JButton btnDelCliente = new JButton("Deletar Cliente");
        JButton btnDelVet = new JButton("Deletar Veterinário");
        JButton btnListarClientes = new JButton("Listar Clientes");
        JButton btnListarVeterinarios = new JButton("Listar Veterinários");

        btnAddCliente.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                adicionarCliente();
            }
        });

        btnAddVet.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                adicionarVeterinario();
            }
        });

        btnDelCliente.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removerCliente();
            }
        });

        btnDelVet.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removerVeterinario();
            }
        });

        btnListarClientes.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listarClientes();
            }
        });

        btnListarVeterinarios.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listarVeterinarios();
            }
        });

        panel.add(btnAddCliente);
        panel.add(btnAddVet);
        panel.add(btnDelCliente);
        panel.add(btnDelVet);
        panel.add(btnListarClientes);
        panel.add(btnListarVeterinarios);

        add(panel);
    }

    private void adicionarCliente() {
        String nome = JOptionPane.showInputDialog(this, "Nome (até 60 caracteres):");
        if (nome == null || nome.isEmpty()) return;
        String endereco1 = JOptionPane.showInputDialog(this, "Endereço - Linha 1 (até 60 caracteres):");
        if (endereco1 == null || endereco1.isEmpty()) return;
        String endereco2 = JOptionPane.showInputDialog(this, "Endereço - Linha 2 (até 60 caracteres):");
        if (endereco2 == null || endereco2.isEmpty()) return;
        String email = JOptionPane.showInputDialog(this, "Email (até 60 caracteres):");
        if (email == null || email.isEmpty()) return;
        String telefone = JOptionPane.showInputDialog(this, "Telefone (até 20 caracteres):");
        if (telefone == null || telefone.isEmpty()) return;

        clienteController.adicionarCliente(nome, endereco1, endereco2, email, telefone);
        JOptionPane.showMessageDialog(this, "Cliente adicionado com sucesso!");
    }

    private void adicionarVeterinario() {
        String nome = JOptionPane.showInputDialog(this, "Nome (até 60 caracteres):");
        if (nome == null || nome.isEmpty()) return;
        String especialidade = JOptionPane.showInputDialog(this, "Especialidade (até 40 caracteres):");
        if (especialidade == null || especialidade.isEmpty()) return;
        String crmv = JOptionPane.showInputDialog(this, "CRMV (até 20 caracteres):");
        if (crmv == null || crmv.isEmpty()) return;
        String email = JOptionPane.showInputDialog(this, "Email (até 60 caracteres):");
        if (email == null || email.isEmpty()) return;
        String telefone = JOptionPane.showInputDialog(this, "Telefone (até 20 caracteres):");
        if (telefone == null || telefone.isEmpty()) return;

        veterinarioController.adicionarVeterinario(nome, especialidade, crmv, email, telefone, cpf);
        JOptionPane.showMessageDialog(this, "Veterinário adicionado com sucesso!");
    }

    private void removerCliente() {
        String idStr = JOptionPane.showInputDialog(this, "Informe o ID do Cliente a ser removido:");
        if (idStr == null || idStr.isEmpty()) return;
        try {
            int id = Integer.parseInt(idStr);
            clienteController.removerCliente(id);
            JOptionPane.showMessageDialog(this, "Operação de remoção realizada.");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "ID inválido!", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void removerVeterinario() {
        String idStr = JOptionPane.showInputDialog(this, "Informe o ID do Veterinário a ser removido:");
        if (idStr == null || idStr.isEmpty()) return;
        try {
            int id = Integer.parseInt(idStr);
            veterinarioController.removerVeterinario(id);
            JOptionPane.showMessageDialog(this, "Operação de remoção realizada.");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "ID inválido!", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void listarClientes() {
        List<Cliente> clientes = repository.getClientes();
        StringBuilder sb = new StringBuilder();
        if (clientes.isEmpty()) {
            sb.append("Nenhum cliente cadastrado.");
        } else {
            for (Cliente cliente : clientes) {
                sb.append(cliente).append("\n");
            }
        }
        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(400, 200));
        JOptionPane.showMessageDialog(this, scrollPane, "Clientes Cadastrados", JOptionPane.INFORMATION_MESSAGE);
    }

    private void listarVeterinarios() {
        List<Veterinario> vets = repository.getVeterinarios();
        StringBuilder sb = new StringBuilder();
        if (vets.isEmpty()) {
            sb.append("Nenhum veterinário cadastrado.");
        } else {
            for (Veterinario vet : vets) {
                sb.append(vet).append("\n");
            }
        }
        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(400, 200));
        JOptionPane.showMessageDialog(this, scrollPane, "Veterinários Cadastrados", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                ClinicaView view = new ClinicaView();
                view.setVisible(true);
            }
        });
    }
}
