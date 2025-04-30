package com.clinica.DAO;

import com.clinica.model.Animal; // << PRECISA implementar Identifiable
import com.clinica.persistence.JsonPersistenceHelper;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AnimalDAO {

    private final JsonPersistenceHelper<Animal> persistenceHelper;
    private List<Animal> animais;

    // Dependência do ClienteDAO para validar/associar cliente se necessário
    // Pode ser útil, mas não estritamente necessário se você confia nos IDs
    // private final ClienteDAO clienteDAO;

    public AnimalDAO(/* ClienteDAO clienteDAO */) {
        this.persistenceHelper = new JsonPersistenceHelper<>("animais.json", new TypeReference<List<Animal>>() {});
        this.animais = persistenceHelper.readAll();
        // this.clienteDAO = clienteDAO; // Se injetar a dependência
    }

    private void saveData() {
        persistenceHelper.writeAll(animais);
    }

    public void inserir(Animal animal) {
        // Opcional: Validar se animal.getClienteId() existe usando clienteDAO
        int nextId = persistenceHelper.getNextId(animais);
        animal.setId(nextId);
        animais.add(animal);
        saveData();
    }

    public void alterar(Animal animalAtualizado) {
        Optional<Animal> animalExistente = animais.stream()
                .filter(a -> a.getId() == animalAtualizado.getId())
                .findFirst();

        animalExistente.ifPresent(a -> {
            a.setNome(animalAtualizado.getNome());
            a.setEspecie(animalAtualizado.getEspecie());
            a.setRaca(animalAtualizado.getRaca());
            a.setDataNascimento(animalAtualizado.getDataNascimento());
            a.setClienteId(animalAtualizado.getClienteId()); // Atualiza o ID do cliente dono
            // Opcional: Validar se o novo clienteId existe
            saveData();
        });
         if (animalExistente.isEmpty()) {
             System.err.println("Tentativa de alterar animal inexistente com ID: " + animalAtualizado.getId());
        }
    }

    public void remover(int id) {
        boolean removed = animais.removeIf(a -> a.getId() == id);
        if (removed) {
            saveData();
        }
         if (!removed) {
             System.err.println("Tentativa de remover animal inexistente com ID: " + id);
        }
    }

    public List<Animal> listarTodos() {
        return new ArrayList<>(animais);
    }

    // Lista animais pertencentes a um cliente específico
    public List<Animal> listarPorCliente(int clienteId) {
        return animais.stream()
                .filter(a -> a.getClienteId() == clienteId)
                .collect(Collectors.toList());
    }

    public Animal exibir(int id) {
        return animais.stream()
                .filter(a -> a.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public List<Animal> pesquisarPorNome(String nome) {
         if (nome == null || nome.trim().isEmpty()) {
            return new ArrayList<>(animais);
        }
        String nomeLower = nome.toLowerCase();
        return animais.stream()
                .filter(a -> a.getNome() != null && a.getNome().toLowerCase().contains(nomeLower))
                .collect(Collectors.toList());
    }

     // Pesquisa animais por nome DENTRO de um cliente específico
    public List<Animal> pesquisarPorNomeECliente(String nome, int clienteId) {
        if (nome == null || nome.trim().isEmpty()) {
            return listarPorCliente(clienteId); // Retorna todos do cliente se nome vazio
        }
        String nomeLower = nome.toLowerCase();
        return animais.stream()
                .filter(a -> a.getClienteId() == clienteId &&
                             a.getNome() != null && a.getNome().toLowerCase().contains(nomeLower))
                .collect(Collectors.toList());
    }
}