package com.clinica.controller;

import com.clinica.DAO.ClienteDAO;
import com.clinica.model.Cliente;

import java.util.List;

public class ClienteController {
    private ClienteDAO clienteDAO;

    public ClienteController() {
        this.clienteDAO = new ClienteDAO();
    }

    // 1️⃣ Adicionar um novo cliente
    public void adicionarCliente(String nome, String endereco, String email, String telefone, String cpf) {
        Cliente cliente = new Cliente(nome, endereco, email, telefone, cpf);
        clienteDAO.inserir(cliente);
        System.out.println("✅ Cliente cadastrado com sucesso!");
    }

    // Atualizar informações de um cliente existente
    public void atualizarCliente(int id, String nome, String endereco, String email, String telefone, String cpf) {
        Cliente cliente = new Cliente(nome, endereco, email, telefone, cpf);
        cliente.setId(id);
        clienteDAO.alterar(cliente);
        System.out.println("✅ Cliente atualizado com sucesso!");
    }

    //  Buscar clientes pelo nome
    public List<Cliente> buscarClientesPorNome(String nome) {
        return clienteDAO.pesquisarPorNome(nome);
    }

    // Remover um cliente pelo ID
    public void removerCliente(int id) {
        clienteDAO.remover(id);
        System.out.println("✅ Cliente removido com sucesso!");
    }

    // Listar todos os clientes
    public List<Cliente> listarTodosClientes() {
        return clienteDAO.listarTodos();
    }

    // Buscar um cliente pelo ID
    public Cliente buscarClientePorId(int id) {
        return clienteDAO.exibir(id);
    }
}
