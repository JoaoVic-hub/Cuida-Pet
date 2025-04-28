package com.clinica.controller;

import com.clinica.DAO.AnimalDAO;
import com.clinica.model.Animal;
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
}
