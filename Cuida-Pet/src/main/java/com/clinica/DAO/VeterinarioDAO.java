package com.clinica.DAO;

import com.clinica.model.Veterinario; // << PRECISA implementar Identifiable
import com.clinica.persistence.JsonPersistenceHelper;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class VeterinarioDAO {

    private final JsonPersistenceHelper<Veterinario> persistenceHelper;
    private List<Veterinario> veterinarios;

    public VeterinarioDAO() {
        this.persistenceHelper = new JsonPersistenceHelper<>("veterinarios.json", new TypeReference<List<Veterinario>>() {});
        this.veterinarios = persistenceHelper.readAll();
    }

    private void saveData() {
        persistenceHelper.writeAll(veterinarios);
    }

    public void inserir(Veterinario vet) {
        int nextId = persistenceHelper.getNextId(veterinarios);
        vet.setId(nextId);
        veterinarios.add(vet);
        saveData();
    }

    public void alterar(Veterinario vetAtualizado) {
        Optional<Veterinario> vetExistente = veterinarios.stream()
                .filter(v -> v.getId() == vetAtualizado.getId())
                .findFirst();

        vetExistente.ifPresent(v -> {
            v.setNome(vetAtualizado.getNome());
            v.setEmail(vetAtualizado.getEmail());
            v.setTelefone(vetAtualizado.getTelefone());
            v.setCpf(vetAtualizado.getCpf());
            v.setSenha(vetAtualizado.getSenha()); // !! Cuidado com senha em texto plano !!
            v.setCrmv(vetAtualizado.getCrmv());
            v.setEspecialidade(vetAtualizado.getEspecialidade());
            saveData();
        });
         if (vetExistente.isEmpty()) {
             System.err.println("Tentativa de alterar veterinário inexistente com ID: " + vetAtualizado.getId());
        }
    }

    public void remover(int id) {
        boolean removed = veterinarios.removeIf(v -> v.getId() == id);
        if (removed) {
            saveData();
        }
         if (!removed) {
             System.err.println("Tentativa de remover veterinário inexistente com ID: " + id);
        }
    }

    public List<Veterinario> listarTodos() {
        return new ArrayList<>(veterinarios);
    }

    public Veterinario exibir(int id) {
        return veterinarios.stream()
                .filter(v -> v.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public List<Veterinario> pesquisarPorNome(String nome) {
         if (nome == null || nome.trim().isEmpty()) {
            return new ArrayList<>(veterinarios);
        }
        String nomeLower = nome.toLowerCase();
        return veterinarios.stream()
                .filter(v -> v.getNome() != null && v.getNome().toLowerCase().contains(nomeLower))
                .collect(Collectors.toList());
    }

    // !! CUIDADO: Senha em texto plano é inseguro !!
    public Veterinario autenticar(String email, String senha) {
         if (email == null || senha == null) return null;
         return veterinarios.stream()
                .filter(v -> email.equalsIgnoreCase(v.getEmail()) && senha.equals(v.getSenha()))
                .findFirst()
                .orElse(null);
    }
}