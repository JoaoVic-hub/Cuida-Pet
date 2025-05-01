package com.clinica.model; 

import com.clinica.persistence.JsonPersistenceHelper;
import java.time.LocalDateTime;
//BUILDER
public class Consulta implements JsonPersistenceHelper.Identifiable {

    private int id;
    private LocalDateTime dataHora;
    private String status;
    private Cliente cliente;
    private Animal animal; // Pode ser null
    private Veterinario veterinario;

    public Consulta() {
    }

    // Construtor privado chamado pelo Builder
    private Consulta(Builder builder) {
        this.dataHora = builder.dataHora;
        this.status = builder.status;
        this.cliente = builder.cliente;
        this.animal = builder.animal;
        this.veterinario = builder.veterinario;
    }

    // --- Getters ---

    @Override
    public int getId() {
        return id;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public String getStatus() {
        return status;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public Animal getAnimal() {
        return animal;
    }

    public Veterinario getVeterinario() {
        return veterinario;
    }

    // --- Setters ---

    @Override
    public void setId(int id) {
        this.id = id;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public void setAnimal(Animal animal) {
        this.animal = animal;
    }

    public void setVeterinario(Veterinario veterinario) {
        this.veterinario = veterinario;
    }

    // --- Classe Builder Interna Estática ---
    public static class Builder {
        private LocalDateTime dataHora;
        private String status = "Agendada"; // Valor padrão opcional
        private Cliente cliente;
        private Animal animal;
        private Veterinario veterinario;

        public Builder dataHora(LocalDateTime dataHora) {
            this.dataHora = dataHora;
            return this;
        }

        public Builder status(String status) {
            this.status = status;
            return this;
        }

        public Builder cliente(Cliente cliente) {
            this.cliente = cliente;
            return this;
        }

        public Builder animal(Animal animal) { // Permite animal null
            this.animal = animal;
            return this;
        }

        public Builder veterinario(Veterinario veterinario) {
            this.veterinario = veterinario;
            return this;
        }

        // Constrói o objeto Consulta
        public Consulta build() {
            // Validações dos dados recebidos pelo builder
            if (dataHora == null || cliente == null || cliente.getId() <= 0 ||
                veterinario == null || veterinario.getId() <= 0 || status == null || status.trim().isEmpty()) {
                throw new IllegalStateException("Campos obrigatórios (Data/Hora, Cliente válido, Veterinário válido, Status) não podem ser nulos ou inválidos para construir a Consulta.");
            }
            if (animal != null && animal.getId() <= 0) {
                 throw new IllegalStateException("Animal fornecido para consulta deve ter um ID válido.");
            }
            return new Consulta(this);
        }
    }

     @Override
     public String toString() {
         return "Consulta{" +
                "id=" + id +
                ", dataHora=" + (dataHora != null ? dataHora.format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME) : "null") +
                ", status='" + status + '\'' +
                ", clienteId=" + (cliente != null ? cliente.getId() : "null") +
                ", animalId=" + (animal != null ? animal.getId() : "null") +
                ", veterinarioId=" + (veterinario != null ? veterinario.getId() : "null") +
                '}';
     }

     @Override
     public boolean equals(Object o) {
         if (this == o) return true;
         if (o == null || getClass() != o.getClass()) return false;
         Consulta consulta = (Consulta) o;
         return id == consulta.id;
     }

     @Override
     public int hashCode() {
         return java.util.Objects.hash(id);
     }
}