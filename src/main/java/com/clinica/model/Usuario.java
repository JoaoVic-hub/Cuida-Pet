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
    
    public Usuario(String nome, String email, String telefone) {
        // Validações de tamanho podem ser adicionadas aqui, se necessário.
        this.id = contador++;
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
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
    
    @Override
    public abstract String toString();
}
