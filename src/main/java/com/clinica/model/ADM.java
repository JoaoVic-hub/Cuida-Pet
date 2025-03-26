package com.clinica.model;

/**
 * Entidade ADM (Administrador) que representa o usuário administrador.
 * Esta classe herda de Usuario e não possui métodos CRUD.
 */
public class ADM extends Usuario {

    public ADM(String nome, String email, String telefone, String cpf) {
        super(nome, email, telefone, cpf);
    }
    
    @Override
    public String toString() {
        return "ADM{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                ", telefone='" + telefone + '\'' +
                '}';
    }
}
