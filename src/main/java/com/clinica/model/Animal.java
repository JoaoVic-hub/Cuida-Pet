package com.clinica.model;

/**
 * Classe Animal, representando os animais cadastrados na clínica.
 */
public class Animal {
    private int id;
    private String nome;
    private String especie;
    private String raca;
    private int idade;
    private int clienteId; // Referência ao dono do animal (Cliente)

    // Construtor sem ID (usado para inserção no banco)
    public Animal(String nome, String especie, String raca, int idade, int clienteId) {
        this.nome = nome;
        this.especie = especie;
        this.raca = raca;
        this.idade = idade;
        this.clienteId = clienteId;
    }

    // Construtor com ID (usado para buscas no banco)
    public Animal(int id, String nome, String especie, String raca, int idade, int clienteId) {
        this.id = id;
        this.nome = nome;
        this.especie = especie;
        this.raca = raca;
        this.idade = idade;
        this.clienteId = clienteId;
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEspecie() {
        return especie;
    }

    public void setEspecie(String especie) {
        this.especie = especie;
    }

    public String getRaca() {
        return raca;
    }

    public void setRaca(String raca) {
        this.raca = raca;
    }

    public int getIdade() {
        return idade;
    }

    public void setIdade(int idade) {
        this.idade = idade;
    }

    public int getClienteId() {
        return clienteId;
    }

    public void setClienteId(int clienteId) {
        this.clienteId = clienteId;
    }

    @Override
    public String toString() {
        return "Animal{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", especie='" + especie + '\'' +
                ", raca='" + raca + '\'' +
                ", idade=" + idade +
                ", clienteId=" + clienteId +
                '}';
    }
}
