package com.clinica.controller;

import com.clinica.DAO.ClienteDAO;
import com.clinica.model.Cliente;

import java.util.List;

public class ClienteController {
    private ClienteDAO clienteDAO;

    public ClienteController() {
        this.clienteDAO = new ClienteDAO();
    }

    public void adicionarCliente(String nome, String endereco, String email, String telefone, String cpf) {
        Cliente cliente = new Cliente(nome, endereco, email, telefone, cpf);
        clienteDAO.inserir(cliente);
        System.out.println("Cliente cadastrado com sucesso!");
    }

    public void atualizarCliente(int id, String nome, String endereco, String email, String telefone, String cpf) {
        Cliente cliente = new Cliente(nome, endereco, email, telefone, cpf);
        cliente.setId(id);
        clienteDAO.alterar(cliente);
        System.out.println("Cliente atualizado com sucesso!");
    }

    public List<Cliente> buscarClientesPorNome(String nome) {
        return clienteDAO.pesquisarPorNome(nome);
    }

    public void removerCliente(int id) {
        clienteDAO.remover(id);
        System.out.println("Cliente removido com sucesso!");
    }

    public List<Cliente> listarTodosClientes() {
        return clienteDAO.listarTodos();
    }

    public Cliente buscarClientePorId(int id) {
        return clienteDAO.exibir(id);
    }
}
