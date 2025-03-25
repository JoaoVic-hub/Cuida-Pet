package com.clinica.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import java.util.ArrayList;
import java.util.List;

public class Prontuario {
    // Identificação do Animal
    private String nomeAnimal;
    private String especie;
    private String raca;
    private String sexo;
    private String dataNascimento;
    private String corMarcacoes;
    private String numeroMicrochip;

    // Informações do Proprietário
    private String nomeProprietario;
    private String endereco;
    private String telefone;
    private String email;

    // Histórico Médico
    private List<String> vacinacoes;
    private List<String> doencasPreExistentes;
    private List<String> cirurgias;
    private List<String> alergias;
    private List<String> medicamentos;

    // Consultas e Exames
    private List<Consulta> consultas;

    // Controle de Parasitas
    private List<String> tratamentosParasitas;

    // Dieta e Nutrição
    private String tipoDieta;
    private List<String> restricoesAlimentares;
    private List<String> suplementos;

    // Comportamento e Socialização
    private String comportamento;
    private String interacaoSocial;

    // Informações Adicionais
    private String observacoesGerais;
    private String planoTratamento;
    private String contatoEmergencia;

    // Construtor
    public Prontuario(String nomeAnimal, String especie, String raca, String sexo, String dataNascimento,
                      String corMarcacoes, String numeroMicrochip, String nomeProprietario, String endereco,
                      String telefone, String email) {
        this.nomeAnimal = nomeAnimal;
        this.especie = especie;
        this.raca = raca;
        this.sexo = sexo;
        this.dataNascimento = dataNascimento;
        this.corMarcacoes = corMarcacoes;
        this.numeroMicrochip = numeroMicrochip;
        this.nomeProprietario = nomeProprietario;
        this.endereco = endereco;
        this.telefone = telefone;
        this.email = email;
        
        this.vacinacoes = new ArrayList<>();
        this.doencasPreExistentes = new ArrayList<>();
        this.cirurgias = new ArrayList<>();
        this.alergias = new ArrayList<>();
        this.medicamentos = new ArrayList<>();
        this.consultas = new ArrayList<>();
        this.tratamentosParasitas = new ArrayList<>();
        this.restricoesAlimentares = new ArrayList<>();
        this.suplementos = new ArrayList<>();
    }

    // Métodos para adicionar informações
    public void adicionarVacinacao(String vacina) {
        vacinacoes.add(vacina);
    }

    public void adicionarDoenca(String doenca) {
        doencasPreExistentes.add(doenca);
    }

    public void adicionarCirurgia(String cirurgia) {
        cirurgias.add(cirurgia);
    }

    public void adicionarAlergia(String alergia) {
        alergias.add(alergia);
    }

    public void adicionarMedicamento(String medicamento) {
        medicamentos.add(medicamento);
    }

    public void adicionarConsulta(Consulta consulta) {
        consultas.add(consulta);
    }

    public void adicionarTratamentoParasita(String tratamento) {
        tratamentosParasitas.add(tratamento);
    }

    public void definirDieta(String dieta) {
        this.tipoDieta = dieta;
    }

    public void adicionarRestricaoAlimentar(String restricao) {
        restricoesAlimentares.add(restricao);
    }

    public void adicionarSuplemento(String suplemento) {
        suplementos.add(suplemento);
    }

    public void definirComportamento(String comportamento) {
        this.comportamento = comportamento;
    }

    public void definirInteracaoSocial(String interacaoSocial) {
        this.interacaoSocial = interacaoSocial;
    }

    public void definirObservacoesGerais(String observacoes) {
        this.observacoesGerais = observacoes;
    }

    public void definirPlanoTratamento(String plano) {
        this.planoTratamento = plano;
    }

    public void definirContatoEmergencia(String contato) {
        this.contatoEmergencia = contato;
    }

    // Getters para usar na interface gráfica
    public String getNomeAnimal() { return nomeAnimal; }
    public String getEspecie() { return especie; }
    public String getRaca() { return raca; }
    public String getSexo() { return sexo; }
    public String getDataNascimento() { return dataNascimento; }
    public String getCorMarcacoes() { return corMarcacoes; }
    public String getNumeroMicrochip() { return numeroMicrochip; }
    public String getNomeProprietario() { return nomeProprietario; }
    public String getEndereco() { return endereco; }
    public String getTelefone() { return telefone; }
    public String getEmail() { return email; }
    public List<String> getVacinacoes() { return vacinacoes; }
    public List<String> getDoencasPreExistentes() { return doencasPreExistentes; }
    public List<String> getCirurgias() { return cirurgias; }
    public List<String> getAlergias() { return alergias; }
    public List<String> getMedicamentos() { return medicamentos; }
    public List<Consulta> getConsultas() { return consultas; }
    public List<String> getTratamentosParasitas() { return tratamentosParasitas; }
    public String getTipoDieta() { return tipoDieta; }
    public List<String> getRestricoesAlimentares() { return restricoesAlimentares; }
    public List<String> getSuplementos() { return suplementos; }
    public String getComportamento() { return comportamento; }
    public String getInteracaoSocial() { return interacaoSocial; }
    public String getObservacoesGerais() { return observacoesGerais; }
    public String getPlanoTratamento() { return planoTratamento; }
    public String getContatoEmergencia() { return contatoEmergencia; }
}
