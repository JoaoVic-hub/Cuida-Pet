package com.clinica.model;

/**
 * Memento: Armazena o estado interno de um objeto Cliente.
 * Permite que o estado do Cliente seja salvo e restaurado posteriormente
 * sem violar o encapsulamento do objeto original.
 * 
 * Versão atualizada para incluir todos os campos relevantes de Cliente.
 */
public class ClienteMemento {
    // Campos herdados de Usuario
    private final String nome;
    private final String email;
    private final String telefone;
    private final String cpf;
    // Campos específicos de Cliente
    private final String endereco;
    private final String cep;
    private final String numero;
    private final String complemento;
    // Nota: A senha e a lista de animais não são salvas intencionalmente.

    /**
     * Construtor do Memento. Recebe o estado a ser salvo.
     *
     * @param nome Nome do cliente.
     * @param email Email do cliente.
     * @param telefone Telefone do cliente.
     * @param cpf CPF do cliente.
     * @param endereco Endereço do cliente.
     * @param cep CEP do cliente.
     * @param numero Número do endereço.
     * @param complemento Complemento do endereço.
     */
    public ClienteMemento(String nome, String email, String telefone, String cpf, String endereco, String cep, String numero, String complemento) {
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.cpf = cpf;
        this.endereco = endereco;
        this.cep = cep;
        this.numero = numero;
        this.complemento = complemento;
    }

    // Getters para recuperar o estado salvo (sem setters para imutabilidade)

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getTelefone() {
        return telefone;
    }

    public String getCpf() {
        return cpf;
    }

    public String getEndereco() {
        return endereco;
    }

    public String getCep() {
        return cep;
    }

    public String getNumero() {
        return numero;
    }

    public String getComplemento() {
        return complemento;
    }
}

