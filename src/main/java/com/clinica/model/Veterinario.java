package com.clinica.model;

/**
 * Classe Veterinario, que representa um veterinário da clínica.
 * <p>
 * Atributos:
 * - id: herdado de Usuario
 * - nome: até 60 posições (herdado de Usuario)
 * - especialidade: até 40 posições (alfanumérico)
 * - CRMV: número do CRMV, até 20 posições (alfanumérico)
 * - email: até 60 posições (herdado de Usuario)
 * - telefone: até 20 posições (herdado de Usuario)
 */
public class Veterinario extends Usuario {
    private String especialidade;
    private String crmv;
    
    public Veterinario(String nome, String especialidade, String crmv, String email, String telefone, String cpf) {
        super(nome, email, telefone, cpf);
        // Aqui também pode ser aplicada validação de tamanho, se necessário.
        this.especialidade = especialidade;
        this.crmv = crmv;
    }
    
    public String getEspecialidade() {
        return especialidade;
    }
    
    public String getCrmv() {
        return crmv;
    }
    
    @Override
    public String toString() {
        return "Veterinario{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", especialidade='" + especialidade + '\'' +
                ", crmv='" + crmv + '\'' +
                ", email='" + email + '\'' +
                ", telefone='" + telefone + '\'' +
                '}';
    }
}
