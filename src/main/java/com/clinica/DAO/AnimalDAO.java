package com.clinica.DAO;

import com.clinica.model.Animal;
import com.clinica.persistence.JsonPersistenceHelper;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AnimalDAO {

    private final JsonPersistenceHelper<Animal> persistenceHelper;
    private List<Animal> animais;

    public AnimalDAO() {
        this.persistenceHelper = new JsonPersistenceHelper<>("animais.json", new TypeReference<List<Animal>>() {});
        this.animais = persistenceHelper.readAll();
    }

    private void saveData() {
        persistenceHelper.writeAll(animais);
    }

    public void inserir(Animal animal) {
        this.animais = persistenceHelper.readAll(); 
        int nextId = persistenceHelper.getNextId(animais);
        animal.setId(nextId);
        animais.add(animal);
        saveData();
    }

    public void alterar(Animal animalAtualizado) {
        this.animais = persistenceHelper.readAll(); 
        Optional<Animal> animalExistente = animais.stream()
                .filter(a -> a.getId() == animalAtualizado.getId())
                .findFirst();

        animalExistente.ifPresent(a -> {
            a.setNome(animalAtualizado.getNome());
            a.setEspecie(animalAtualizado.getEspecie());
            a.setRaca(animalAtualizado.getRaca());
            a.setDataNascimento(animalAtualizado.getDataNascimento());
            a.setClienteId(animalAtualizado.getClienteId());
            saveData(); // Salva lista modificada
        });
         if (animalExistente.isEmpty()) {
             System.err.println("Tentativa de alterar animal inexistente com ID: " + animalAtualizado.getId());
        }
    }

    public void remover(int id) {
        this.animais = persistenceHelper.readAll();
        boolean removed = animais.removeIf(a -> a.getId() == id);
        if (removed) {
            saveData(); // Salva após remover
        }
         if (!removed) {
             System.err.println("Tentativa de remover animal inexistente com ID: " + id);
        }
    }

    public List<Animal> listarTodos() {
        this.animais = persistenceHelper.readAll();
        return new ArrayList<>(animais);
    }

    public List<Animal> listarPorCliente(int clienteId) {
        this.animais = persistenceHelper.readAll();
        return animais.stream()
                .filter(a -> a.getClienteId() == clienteId)
                .collect(Collectors.toList());
    }

    public Animal exibir(int id) {
        this.animais = persistenceHelper.readAll();
        return animais.stream()
                .filter(a -> a.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public List<Animal> pesquisarPorNome(String nome) {
         this.animais = persistenceHelper.readAll();
         if (nome == null || nome.trim().isEmpty()) {
            return new ArrayList<>(animais);
        }
        String nomeLower = nome.toLowerCase();
        return animais.stream()
                .filter(a -> a.getNome() != null && a.getNome().toLowerCase().contains(nomeLower))
                .collect(Collectors.toList());
    }

    public List<Animal> pesquisarPorNomeECliente(String nome, int clienteId) {
        this.animais = persistenceHelper.readAll();
        if (nome == null || nome.trim().isEmpty()) {
            return listarPorCliente(clienteId);
        }
        String nomeLower = nome.toLowerCase();
        return animais.stream()
                .filter(a -> a.getClienteId() == clienteId &&
                             a.getNome() != null && a.getNome().toLowerCase().contains(nomeLower))
                .collect(Collectors.toList());
    }
}
