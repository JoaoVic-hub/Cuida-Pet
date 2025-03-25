package com.clinica.model;

/**
 * Classe base para representar um usuário.
 * <p>
 * Atributos:
 * - id: identificador único gerado automaticamente.
 * - nome: até 60 posições (alfanumérico)
 * - email: até 60 posições (alfanumérico)
 * - telefone: até 20 posições (alfanumérico)
 */
public abstract class Usuario {
    private static int contador = 1;
    protected int id;
    protected String nome;
    protected String email;
    protected String telefone;
    private String cpf;
    
    public Usuario(String nome, String email, String telefone, String cpf) {
        // Validações de tamanho podem ser adicionadas aqui, se necessário.
        this.id = contador++;
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.cpf = cpf;
    }
    
    public int getId() {
        return id;
    }
    
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

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public void setId(int id) {
        this.id = id;
    }


    
    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    @Override
    public abstract String toString();
}
