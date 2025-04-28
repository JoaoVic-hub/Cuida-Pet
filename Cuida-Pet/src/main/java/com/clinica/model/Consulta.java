package com.clinica.model;

import com.clinica.persistence.JsonPersistenceHelper;
import java.time.LocalDateTime;

public class Consulta implements JsonPersistenceHelper.Identifiable{
    private int id;
    private LocalDateTime dataHora; 
    private String status; 
    private Cliente cliente;
    private Animal animal;
    private Veterinario veterinario;


    public Consulta() { }


    public Consulta(LocalDateTime dataHora, String status, 
                    Cliente cliente, Animal animal, Veterinario veterinario) {
        this.dataHora = dataHora;
        this.status = status;
        this.cliente = cliente;
        this.animal = animal;
        this.veterinario = veterinario;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public LocalDateTime getDataHora() {
        return dataHora;
    }
    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public Cliente getCliente() {
        return cliente;
    }
    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }
    public Animal getAnimal() {
        return animal;
    }
    public void setAnimal(Animal animal) {
        this.animal = animal;
    }
    public Veterinario getVeterinario() {
        return veterinario;
    }
    public void setVeterinario(Veterinario veterinario) {
        this.veterinario = veterinario;
    }

    @Override
    public String toString() {
        return "Consulta{" +
                "id=" + id +
                ", dataHora=" + dataHora +
                ", status='" + status + '\'' +
                ", cliente=" + (cliente != null ? cliente.getNome() : "n/a") +
                ", animal=" + (animal != null ? animal.getNome() : "n/a") +
                ", veterinario=" + (veterinario != null ? veterinario.getNome() : "n/a") +
                '}';
    }
}
