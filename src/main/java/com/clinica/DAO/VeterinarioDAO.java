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
        this.veterinarios = persistenceHelper.readAll(); // Recarrega
        int nextId = persistenceHelper.getNextId(veterinarios);
        vet.setId(nextId);
        veterinarios.add(vet);
        saveData();
    }

    public void alterar(Veterinario vetAtualizado) {
        this.veterinarios = persistenceHelper.readAll(); // Recarrega
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
            saveData(); // Salva lista modificada
        });
         if (vetExistente.isEmpty()) {
             System.err.println("Tentativa de alterar veterinário inexistente com ID: " + vetAtualizado.getId());
        }
    }

    public void remover(int id) {
        this.veterinarios = persistenceHelper.readAll(); // Recarrega
        boolean removed = veterinarios.removeIf(v -> v.getId() == id);
        if (removed) {
            saveData(); // Salva após remover
        }
         if (!removed) {
             System.err.println("Tentativa de remover veterinário inexistente com ID: " + id);
        }
    }

    public List<Veterinario> listarTodos() {
        this.veterinarios = persistenceHelper.readAll(); // Recarrega
        return new ArrayList<>(veterinarios);
    }

    public Veterinario exibir(int id) {
        this.veterinarios = persistenceHelper.readAll(); // Recarrega
        return veterinarios.stream()
                .filter(v -> v.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public List<Veterinario> pesquisarPorNome(String nome) {
         this.veterinarios = persistenceHelper.readAll(); // Recarrega
         if (nome == null || nome.trim().isEmpty()) {
            return new ArrayList<>(veterinarios);
        }
        String nomeLower = nome.toLowerCase();
        return veterinarios.stream()
                .filter(v -> v.getNome() != null && v.getNome().toLowerCase().contains(nomeLower))
                .collect(Collectors.toList());
    }

    public Veterinario autenticar(String email, String senha) {
         this.veterinarios = persistenceHelper.readAll(); // Recarrega
         if (email == null || senha == null) return null;
         return veterinarios.stream()
                .filter(v -> email.equalsIgnoreCase(v.getEmail()) && senha.equals(v.getSenha()))
                .findFirst()
                .orElse(null);
    }
}
