package com.clinica.controller;

import com.clinica.DAO.AnimalDAO;
import com.clinica.model.Animal;
import java.util.ArrayList;
import java.util.List;

public class AnimalController {
    private AnimalDAO animalDAO;

    public AnimalController() {
        this.animalDAO = new AnimalDAO();
    }

    public void adicionarAnimal(String nome, String especie, String raca, 
                                java.time.LocalDate dataNascimento, int clienteId) {
        Animal animal = new Animal(nome, especie, raca, dataNascimento, clienteId);
        animalDAO.inserir(animal);
        System.out.println("Animal cadastrado com sucesso!");
    }

   
    public void atualizarAnimal(int id, String nome, String especie, String raca, 
                                java.time.LocalDate dataNascimento, int clienteId) {
        Animal animal = new Animal(nome, especie, raca, dataNascimento, clienteId);
        animal.setId(id);
        animalDAO.alterar(animal);
        System.out.println("Animal atualizado com sucesso!");
    }

    
    public void removerAnimal(int id) {
        animalDAO.remover(id);
        System.out.println("Animal removido com sucesso!");
    }

   
    public List<Animal> listarTodosAnimais() {
        return animalDAO.listarTodos();
    }

    
    public Animal buscarAnimalPorId(int id) {
        return animalDAO.exibir(id);
    }

    
    public List<Animal> pesquisarAnimaisPorNome(String nome) {
        return animalDAO.pesquisarPorNome(nome);
    }

     // --- NOVO MÉTODO ---
    /**
     * Lista todos os animais pertencentes a um cliente específico.
     * @param clienteId O ID do cliente.
     * @return Uma lista de animais do cliente, ou lista vazia se não encontrar ou ID inválido.
     */
    public List<Animal> listarAnimaisPorCliente(int clienteId) {
        if (clienteId <= 0) {
            return new ArrayList<>(); // Retorna lista vazia para ID inválido
        }
        return animalDAO.listarPorCliente(clienteId);
    }
    // -----------------

     // Método adicionarAnimal modificado para receber o objeto (melhor para forms)
     public boolean adicionarAnimalObj(Animal animal) {
         if (animal == null || animal.getClienteId() <= 0 /*|| clienteDAO.exibir(animal.getClienteId()) == null*/) {
             System.err.println("Controller: Dados inválidos ou cliente não encontrado para adicionar animal.");
             return false;
         }
         try {
             animalDAO.inserir(animal);
             System.out.println("Controller: Animal adicionado para cliente ID " + animal.getClienteId());
             return true;
         } catch (Exception e) {
             System.err.println("Controller: Erro ao adicionar animal: " + e.getMessage());
             e.printStackTrace();
             return false;
         }
     }

     // Método atualizarAnimal modificado para receber o objeto
      public boolean atualizarAnimalObj(Animal animal) {
         if (animal == null || animal.getId() <= 0 || animal.getClienteId() <= 0 /*|| clienteDAO.exibir(animal.getClienteId()) == null*/) {
             System.err.println("Controller: Dados inválidos ou cliente não encontrado para atualizar animal.");
             return false;
         }
         try {
             animalDAO.alterar(animal);
             System.out.println("Controller: Animal ID " + animal.getId() + " atualizado.");
             return true;
         } catch (Exception e) {
             System.err.println("Controller: Erro ao atualizar animal: " + e.getMessage());
             e.printStackTrace();
             return false;
         }
     }
}
