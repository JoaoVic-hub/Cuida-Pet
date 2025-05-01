package com.clinica.DAO;

import com.clinica.model.Cliente;
import com.clinica.persistence.JsonPersistenceHelper;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class ClienteDAO {

    private final JsonPersistenceHelper<Cliente> persistenceHelper;
    private List<Cliente> clientes; // Cache em memória

    public ClienteDAO() {
        this.persistenceHelper = new JsonPersistenceHelper<>("clientes.json", new TypeReference<List<Cliente>>() {});
        this.clientes = persistenceHelper.readAll(); // Carrega na inicialização
    }

    private void saveData() {
        persistenceHelper.writeAll(clientes); // Salva a lista inteira no arquivo
    }

    public void inserir(Cliente cliente) {
        // Recarrega antes de gerar ID para evitar colisões se outro processo modificou o arquivo
        this.clientes = persistenceHelper.readAll();
        int nextId = persistenceHelper.getNextId(clientes);
        cliente.setId(nextId);
        clientes.add(cliente);
        saveData();
    }

    public void alterar(Cliente clienteAtualizado) {
        this.clientes = persistenceHelper.readAll();
        Optional<Cliente> clienteExistente = clientes.stream()
                .filter(c -> c.getId() == clienteAtualizado.getId())
                .findFirst();

        clienteExistente.ifPresent(c -> {
            c.setNome(clienteAtualizado.getNome());
            c.setEndereco(clienteAtualizado.getEndereco());
            c.setEmail(clienteAtualizado.getEmail());
            c.setTelefone(clienteAtualizado.getTelefone());
            c.setCpf(clienteAtualizado.getCpf());
            c.setSenha(clienteAtualizado.getSenha());
            saveData(); // Salva a lista modificada
        });
        if (clienteExistente.isEmpty()) {
             System.err.println("Tentativa de alterar cliente inexistente com ID: " + clienteAtualizado.getId());
        }
    }

    public List<Cliente> pesquisarPorNome(String nome) {
        this.clientes = persistenceHelper.readAll();
        if (nome == null || nome.trim().isEmpty()) {
            return new ArrayList<>(clientes); // Retorna todos se a busca for vazia
        }
        String nomeLower = nome.toLowerCase();
        return clientes.stream()
                .filter(c -> c.getNome() != null && c.getNome().toLowerCase().contains(nomeLower))
                .collect(Collectors.toList());
    }

    public void remover(int id) {
        // Recarrega antes de remover
        this.clientes = persistenceHelper.readAll();
        boolean removed = clientes.removeIf(c -> c.getId() == id);
        if (removed) {
            saveData(); // Salva a lista após remover
        }
        if (!removed) {
             System.err.println("Tentativa de remover cliente inexistente com ID: " + id);
        }
    }

    public List<Cliente> listarTodos() {
        this.clientes = persistenceHelper.readAll();
        return new ArrayList<>(clientes); // Retorna cópia defensiva
    }

    public Cliente exibir(int id) {
        this.clientes = persistenceHelper.readAll(); // Recarrega
        return clientes.stream()
                .filter(c -> c.getId() == id)
                .findFirst()
                .orElse(null); // Retorna null se não encontrar
    }

    public Cliente autenticar(String email, String senha) {
         this.clientes = persistenceHelper.readAll(); 
         if (email == null || senha == null) return null;
         return clientes.stream()
                .filter(c -> email.equalsIgnoreCase(c.getEmail()) && senha.equals(c.getSenha()))
                .findFirst()
                .orElse(null);
    }
}
