package com.clinica.model;

import java.time.LocalDateTime;

public class Prontuario {
    private int id;
    private Consulta consulta; 
    private String observacoes;
    private String diagnostico;
    private LocalDateTime dataRegistro;


    public Prontuario() { }

    public Prontuario(Consulta consulta, String observacoes, String diagnostico, LocalDateTime dataRegistro) {
        this.consulta = consulta;
        this.observacoes = observacoes;
        this.diagnostico = diagnostico;
        this.dataRegistro = dataRegistro;
    }


    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public Consulta getConsulta() {
        return consulta;
    }
    public void setConsulta(Consulta consulta) {
        this.consulta = consulta;
    }
    public String getObservacoes() {
        return observacoes;
    }
    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
    public String getDiagnostico() {
        return diagnostico;
    }
    public void setDiagnostico(String diagnostico) {
        this.diagnostico = diagnostico;
    }
    public LocalDateTime getDataRegistro() {
        return dataRegistro;
    }
    public void setDataRegistro(LocalDateTime dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    @Override
    public String toString() {
        return "Prontuario{" +
                "id=" + id +
                ", consultaId=" + (consulta != null ? consulta.getId() : "n/a") +
                ", observacoes='" + observacoes + '\'' +
                ", diagnostico='" + diagnostico + '\'' +
                ", dataRegistro=" + dataRegistro +
                '}';
    }
}
