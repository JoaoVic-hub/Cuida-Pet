package com.clinica.model;

import java.time.LocalDateTime;

import com.clinica.persistence.JsonPersistenceHelper;

public class Agenda implements JsonPersistenceHelper.Identifiable{
    // Campos que a view disponibiliza
    private int consultaId;
    private LocalDateTime dataHora;
    private String status;
    private int veterinarioId;
    private String nomeVeterinario;
    private int animalId;
    private String nomeAnimal;
    private int clienteId;
    private String nomeCliente;
    private String enderecoCliente;

    public Agenda() { }

    public Agenda(int consultaId, LocalDateTime dataHora, String status,
                  int veterinarioId, String nomeVeterinario,
                  int animalId, String nomeAnimal,
                  int clienteId, String nomeCliente,
                  String enderecoCliente) {
        this.consultaId = consultaId;
        this.dataHora = dataHora;
        this.status = status;
        this.veterinarioId = veterinarioId;
        this.nomeVeterinario = nomeVeterinario;
        this.animalId = animalId;
        this.nomeAnimal = nomeAnimal;
        this.clienteId = clienteId;
        this.nomeCliente = nomeCliente;
        this.enderecoCliente = enderecoCliente;
    }

    public int getConsultaId() {
        return consultaId;
    }
    public void setConsultaId(int consultaId) {
        this.consultaId = consultaId;
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
    public int getVeterinarioId() {
        return veterinarioId;
    }
    public void setVeterinarioId(int veterinarioId) {
        this.veterinarioId = veterinarioId;
    }
    public String getNomeVeterinario() {
        return nomeVeterinario;
    }
    public void setNomeVeterinario(String nomeVeterinario) {
        this.nomeVeterinario = nomeVeterinario;
    }
    public int getAnimalId() {
        return animalId;
    }
    public void setAnimalId(int animalId) {
        this.animalId = animalId;
    }
    public String getNomeAnimal() {
        return nomeAnimal;
    }
    public void setNomeAnimal(String nomeAnimal) {
        this.nomeAnimal = nomeAnimal;
    }
    public int getClienteId() {
        return clienteId;
    }
    public void setClienteId(int clienteId) {
        this.clienteId = clienteId;
    }
    public String getNomeCliente() {
        return nomeCliente;
    }
    public void setNomeCliente(String nomeCliente) {
        this.nomeCliente = nomeCliente;
    }
    public String getEnderecoCliente() {
        return enderecoCliente;
    }
    public void setEnderecoCliente(String enderecoCliente) {
        this.enderecoCliente = enderecoCliente;
    }

    @Override
    public String toString() {
        return "Agenda{" +
                "consultaId=" + consultaId +
                ", dataHora=" + dataHora +
                ", status='" + status + '\'' +
                ", veterinarioId=" + veterinarioId +
                ", nomeVeterinario='" + nomeVeterinario + '\'' +
                ", animalId=" + animalId +
                ", nomeAnimal='" + nomeAnimal + '\'' +
                ", clienteId=" + clienteId +
                ", nomeCliente='" + nomeCliente + '\'' +
                ", enderecoCliente='" + enderecoCliente + '\'' +
                '}';
    }
}
