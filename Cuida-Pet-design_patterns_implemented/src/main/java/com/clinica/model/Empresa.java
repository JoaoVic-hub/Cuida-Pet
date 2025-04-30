package com.clinica.model;

public class Empresa {
    private int id;
    private String nome;
    private String email;
    private String senha;
    private String cnpj;
    private String telefone;
    
    
    public Empresa() {
    }
    

    public Empresa(String nome, String email, String senha, String cnpj, String telefone) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.cnpj = cnpj;
        this.telefone = telefone;
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
    
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getSenha() {
        return senha;
    }
    public void setSenha(String senha) {
        this.senha = senha;
    }
    
    public String getCnpj() {
        return cnpj;
    }
    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }
    
    public String getTelefone() {
        return telefone;
    }
    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }
    
    @Override
    public String toString() {
        return "Empresa{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                ", telefone='" + telefone + '\'' +
                ", cnpj='" + cnpj + '\'' +
                '}';
    }
}
