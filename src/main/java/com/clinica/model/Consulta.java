package com.clinica.model;

import java.util.Date;

public class Consulta {
    private Date dataConsulta;
    private String motivo;
    private String observacoes;
    private String diagnostico;
    private String tratamentoPrescrito;
    private String examesSolicitados;
    private Prontuario prontuario;

    // Construtor
    public Consulta(Date dataConsulta, String motivo, Prontuario prontuario) {
        this.dataConsulta = dataConsulta;
        this.motivo = motivo;
        this.prontuario = prontuario;
        this.observacoes = "";
        this.diagnostico = "";
        this.tratamentoPrescrito = "";
        this.examesSolicitados = "";
    }

    // Getters e Setters
    public Date getDataConsulta() { return dataConsulta; }
    public void setDataConsulta(Date dataConsulta) { this.dataConsulta = dataConsulta; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

    public String getDiagnostico() { return diagnostico; }
    public void setDiagnostico(String diagnostico) { this.diagnostico = diagnostico; }

    public String getTratamentoPrescrito() { return tratamentoPrescrito; }
    public void setTratamentoPrescrito(String tratamentoPrescrito) { this.tratamentoPrescrito = tratamentoPrescrito; }

    public String getExamesSolicitados() { return examesSolicitados; }
    public void setExamesSolicitados(String examesSolicitados) { this.examesSolicitados = examesSolicitados; }

    public Prontuario getProntuario() { return prontuario; }
    public void setProntuario(Prontuario prontuario) { this.prontuario = prontuario; }
}
