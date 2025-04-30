package com.clinica.model;

import com.clinica.persistence.JsonPersistenceHelper;

public class Veterinario extends Usuario implements JsonPersistenceHelper.Identifiable{
    private String crmv;
    private String especialidade;

    public Veterinario() {
        super();
    }

    public Veterinario(String nome, String email, String telefone, String cpf, String senha,
                       String crmv, String especialidade) 
    {

        super(nome, email, telefone, cpf, senha);
        this.crmv = crmv;
        this.especialidade = especialidade;
    }


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
                '}';
    }
}
