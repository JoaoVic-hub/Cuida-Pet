package com.clinica.controller;

import com.clinica.model.Cliente;
import com.clinica.repository.UsuarioRepository;

public class ClienteController {
    private UsuarioRepository repository = UsuarioRepository.getInstance();
    
    /**
     * Adiciona um novo Cliente com os campos: nome, endereço (2 linhas), email e telefone.
     */
    public void adicionarCliente(String nome, String enderecoLinha1, String enderecoLinha2, String email, String telefone) {
        Cliente cliente = new Cliente(nome, enderecoLinha1, enderecoLinha2, email, telefone);
        repository.addCliente(cliente);
        System.out.println("Cliente adicionado: " + cliente);
    }
    
    /**
     * Remove um Cliente com base no id.
     */
    public void removerCliente(int id) {
        if(repository.removeCliente(id)) {
            System.out.println("Cliente removido com sucesso!");
        } else {
            System.out.println("Cliente não encontrado.");
        }
    }
}
