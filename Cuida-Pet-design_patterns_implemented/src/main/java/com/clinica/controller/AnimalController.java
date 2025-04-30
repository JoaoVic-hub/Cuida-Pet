package com.clinica.controller;

import com.clinica.DAO.AnimalDAO;
import com.clinica.DAO.DAOFactory; // Importar a Factory
import com.clinica.model.Animal;
import com.clinica.model.Cliente; // Importar Cliente se necessário para setar no Animal

import java.sql.SQLException; // Importar SQLException
import java.util.ArrayList; // Para retornar lista vazia em caso de erro
import java.util.List;

/**
 * Controller para gerenciar operações relacionadas a Animais.
 * Utiliza AnimalDAO (obtido via DAOFactory) para interagir com o banco de dados.
 * Adicionado tratamento de exceções SQLException.
 */
public class AnimalController {
    private AnimalDAO animalDAO;

    public AnimalController() {
        // Obtém a instância do DAO através da Factory
        this.animalDAO = DAOFactory.createAnimalDAO(); 
    }

    /**
     * Adiciona um novo animal ao sistema.
     *
     * @param nome Nome do animal.
     * @param especie Espécie do animal.
     * @param raca Raça do animal.
     * @param dataNascimento Data de nascimento do animal.
     * @param clienteId ID do cliente proprietário.
     * @return true se o animal foi adicionado com sucesso, false caso contrário.
     */
    public boolean adicionarAnimal(String nome, String especie, String raca, 
                                java.time.LocalDate dataNascimento, int clienteId) {
        // Criar o objeto Cliente apenas com o ID para associação
        Cliente clienteProprietario = new Cliente();
        clienteProprietario.setId(clienteId);
        
        Animal animal = new Animal(nome, especie, raca, dataNascimento, clienteProprietario); // Passa o objeto Cliente
        // animal.setClienteId(clienteId); // Alternativa se o construtor não aceitar Cliente
        
        try {
            animalDAO.inserir(animal);
            System.out.println("Animal cadastrado com sucesso! ID: " + animal.getId());
            return true;
        } catch (SQLException e) {
            System.err.println("Erro ao adicionar animal: " + e.getMessage());
            // Em uma aplicação real, poderia logar o erro ou notificar o usuário de forma mais apropriada
            return false;
        }
    }

    /**
     * Atualiza os dados de um animal existente.
     *
     * @param id ID do animal a ser atualizado.
     * @param nome Novo nome do animal.
     * @param especie Nova espécie do animal.
     * @param raca Nova raça do animal.
     * @param dataNascimento Nova data de nascimento do animal.
     * @param clienteId ID do cliente proprietário.
     * @return true se o animal foi atualizado com sucesso, false caso contrário.
     */
    public boolean atualizarAnimal(int id, String nome, String especie, String raca, 
                                java.time.LocalDate dataNascimento, int clienteId) {
        Cliente clienteProprietario = new Cliente();
        clienteProprietario.setId(clienteId);
        
        Animal animal = new Animal(nome, especie, raca, dataNascimento, clienteProprietario);
        animal.setId(id); // Define o ID para identificar qual animal atualizar
        
        try {
            animalDAO.alterar(animal);
            System.out.println("Animal atualizado com sucesso! ID: " + id);
            return true;
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar animal (ID: " + id + "): " + e.getMessage());
            return false;
        }
    }

    /**
     * Remove um animal do sistema.
     *
     * @param id ID do animal a ser removido.
     * @return true se o animal foi removido com sucesso, false caso contrário.
     */
    public boolean removerAnimal(int id) {
        try {
            animalDAO.remover(id);
            System.out.println("Animal removido com sucesso! ID: " + id);
            return true;
        } catch (SQLException e) {
            System.err.println("Erro ao remover animal (ID: " + id + "): " + e.getMessage());
            return false;
        }
    }

    /**
     * Lista todos os animais cadastrados.
     *
     * @return Uma lista de todos os animais, ou uma lista vazia em caso de erro.
     */
    public List<Animal> listarTodosAnimais() {
        try {
            return animalDAO.listarTodos();
        } catch (SQLException e) {
            System.err.println("Erro ao listar todos os animais: " + e.getMessage());
            return new ArrayList<>(); // Retorna lista vazia em caso de erro
        }
    }
    
    /**
     * Lista todos os animais de um cliente específico.
     *
     * @param idCliente O ID do cliente.
     * @return Uma lista de animais do cliente, ou uma lista vazia em caso de erro.
     */
    public List<Animal> listarAnimaisPorCliente(int idCliente) {
        try {
            return animalDAO.listarPorCliente(idCliente);
        } catch (SQLException e) {
            System.err.println("Erro ao listar animais por cliente (ID: " + idCliente + "): " + e.getMessage());
            return new ArrayList<>(); // Retorna lista vazia em caso de erro
        }
    }

    /**
     * Busca um animal pelo seu ID.
     *
     * @param id O ID do animal.
     * @return O objeto Animal encontrado, ou null se não for encontrado ou em caso de erro.
     */
    public Animal buscarAnimalPorId(int id) {
        try {
            // Corrigido: usar buscarPorId em vez de exibir
            return animalDAO.buscarPorId(id); 
        } catch (SQLException e) {
            System.err.println("Erro ao buscar animal por ID (ID: " + id + "): " + e.getMessage());
            return null;
        }
    }

    /**
     * Pesquisa animais pelo nome (busca parcial).
     *
     * @param nome O nome ou parte do nome a ser pesquisado.
     * @return Uma lista de animais que correspondem à pesquisa, ou uma lista vazia em caso de erro.
     */
    public List<Animal> pesquisarAnimaisPorNome(String nome) {
        try {
            return animalDAO.pesquisarPorNome(nome);
        } catch (SQLException e) {
            System.err.println("Erro ao pesquisar animais por nome (" + nome + "): " + e.getMessage());
            return new ArrayList<>();
        }
    }
}

