package com.clinica.model;

import com.clinica.persistence.JsonPersistenceHelper;
import java.time.LocalDate;

public class Animal implements JsonPersistenceHelper.Identifiable{
    private int id;
    private String nome;
    private String especie;
    private String raca;
    private LocalDate dataNascimento;
    private int clienteId; 

    public Animal() {
    }


    public Animal(String nome, String especie, String raca, LocalDate dataNascimento, int clienteId) {
        this.nome = nome;
        this.especie = especie;
        this.raca = raca;
        this.dataNascimento = dataNascimento;
        this.clienteId = clienteId;
    }

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
    
    public LocalDate getDataNascimento() {
        return dataNascimento;
    }
    
    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
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
                ", dataNascimento=" + dataNascimento +
                ", clienteId=" + clienteId +
                '}';
    }
}
