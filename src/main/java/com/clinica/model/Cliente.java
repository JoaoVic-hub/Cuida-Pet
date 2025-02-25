package com.clinica.model;

/**
 * Classe Cliente, que representa um cliente da clínica.
 * <p>
 * Atributos:
 * - id: herdado de Usuario
 * - nome: até 60 posições (herdado de Usuario)
 * - endereço: duas linhas, cada uma com até 60 posições (alfanumérico)
 * - email: até 60 posições (herdado de Usuario)
 * - telefone: até 20 posições (herdado de Usuario)
 */
public class Cliente extends Usuario {
    private String enderecoLinha1;
    private String enderecoLinha2;
    
    public Cliente(String nome, String enderecoLinha1, String enderecoLinha2, String email, String telefone) {
        super(nome, email, telefone);
        // Aqui também pode ser aplicada validação de tamanho para os endereços.
        this.enderecoLinha1 = enderecoLinha1;
        this.enderecoLinha2 = enderecoLinha2;
    }
    
    public String getEnderecoLinha1() {
        return enderecoLinha1;
    }
    
    public String getEnderecoLinha2() {
        return enderecoLinha2;
    }
    
    @Override
    public String toString() {
        return "Cliente{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", enderecoLinha1='" + enderecoLinha1 + '\'' +
                ", enderecoLinha2='" + enderecoLinha2 + '\'' +
                ", email='" + email + '\'' +
                ", telefone='" + telefone + '\'' +
                '}';
    }
}
