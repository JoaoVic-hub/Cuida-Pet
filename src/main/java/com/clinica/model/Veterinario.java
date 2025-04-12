package com.clinica.model;

public class Veterinario extends Usuario {
    private String crmv;
    private String especialidade;

    // Construtor vazio
    public Veterinario() {
        super();
    }

    /**
     * Construtor completo para Veterinario.
     * Ordem dos parâmetros:
     * (nome, email, telefone, cpf, senha, crmv, especialidade)
     */
    public Veterinario(String nome, String email, String telefone, String cpf, String senha,
                       String crmv, String especialidade) 
    {
        // Chama o construtor da classe base Usuario
        super(nome, email, telefone, cpf, senha);
        this.crmv = crmv;
        this.especialidade = especialidade;
    }

    // Getters e setters para os atributos específicos

    public String getCrmv() {
        return crmv;
    }

    public void setCrmv(String crmv) {
        this.crmv = crmv;
    }

    public String getEspecialidade() {
        return especialidade;
    }

    public void setEspecialidade(String especialidade) {
        this.especialidade = especialidade;
    }

    @Override
    public String toString() {
        return "Veterinario{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                ", telefone='" + telefone + '\'' +
                ", cpf='" + cpf + '\'' +
                ", crmv='" + crmv + '\'' +
                ", especialidade='" + especialidade + '\'' +
                // Geralmente não mostramos a senha
                '}';
    }
}
