package com.clinica.view.ViewEmpresa;

import javax.swing.*;
import java.awt.*;

public class TelaEmpresa extends JFrame {

    private JPanel painelConteudo;

    public TelaEmpresa() {
        setTitle("🐾 Cuida Pet - Painel da Empresa");
        setSize(1000, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JLabel titulo = new JLabel("Bem-vindo ao Sistema da Clínica Veterinária", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titulo.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(titulo, BorderLayout.NORTH);

        JPanel menuLateral = new JPanel();
        menuLateral.setBackground(new Color(33, 150, 243));
        menuLateral.setPreferredSize(new Dimension(200, getHeight()));
        menuLateral.setLayout(new GridLayout(6, 1, 5, 5));

        JButton btnClientes = criarBotao("👥 Gerenciar Clientes");
        JButton btnVeterinarios = criarBotao("🩺 Gerenciar Veterinários");
        JButton btnConsultas = criarBotao("📋 Ver Consultas");
        JButton btnAgenda = criarBotao("📆 Ver Agenda");
        JButton btnRelatorios = criarBotao("📊 Relatórios");
        JButton btnSair = criarBotao("🚪 Sair");

        menuLateral.add(btnClientes);
        menuLateral.add(btnVeterinarios);
        menuLateral.add(btnConsultas);
        menuLateral.add(btnAgenda);
        menuLateral.add(btnRelatorios);
        menuLateral.add(btnSair);

        add(menuLateral, BorderLayout.WEST);

        painelConteudo = new JPanel(new BorderLayout());
        painelConteudo.add(new JLabel("Selecione uma opção à esquerda.", SwingConstants.CENTER), BorderLayout.CENTER);
        add(painelConteudo, BorderLayout.CENTER);

        btnClientes.addActionListener(e -> carregarPainel(new com.clinica.view.ViewEmpresa.PainelGerenciarClientes()));
        btnVeterinarios.addActionListener(e -> mostrarEmBreve("Veterinários"));
        btnConsultas.addActionListener(e -> mostrarEmBreve("Consultas"));
        btnAgenda.addActionListener(e -> mostrarEmBreve("Agenda"));
        btnRelatorios.addActionListener(e -> mostrarEmBreve("Relatórios"));
        btnSair.addActionListener(e -> System.exit(0));

        setVisible(true);
    }

    private JButton criarBotao(String texto) {
        JButton botao = new JButton(texto);
        botao.setFocusPainted(false);
        botao.setForeground(Color.WHITE);
        botao.setBackground(new Color(25, 118, 210));
        botao.setFont(new Font("Segoe UI", Font.BOLD, 14));
        botao.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        return botao;
    }

    private void carregarPainel(JPanel novoPainel) {
        painelConteudo.removeAll();
        painelConteudo.add(novoPainel, BorderLayout.CENTER);
        painelConteudo.revalidate();
        painelConteudo.repaint();
    }

    private void mostrarEmBreve(String nome) {
        painelConteudo.removeAll();
        painelConteudo.add(new JLabel(nome + " - Em breve!", SwingConstants.CENTER), BorderLayout.CENTER);
        painelConteudo.revalidate();
        painelConteudo.repaint();
    }
}
