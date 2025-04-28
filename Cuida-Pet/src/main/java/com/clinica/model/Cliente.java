package com.clinica.model;

import com.clinica.persistence.JsonPersistenceHelper;
import java.util.ArrayList;
import java.util.List;

public class Cliente extends Usuario implements JsonPersistenceHelper.Identifiable{
    private String endereco;
    private List<Animal> animais;

    
    public Cliente() {
        this.animais = new ArrayList<>();
    }

   
    public Cliente(String nome,
                   String endereco,
                   String email,
                   String telefone,
                   String cpf,
                   String senha)
    {
        
        super(nome, email, telefone, cpf, senha);

        this.endereco = endereco;

        this.animais = new ArrayList<>();
    }

 
    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public List<Animal> getAnimais() {
        return animais;
    }

    public void setAnimais(List<Animal> animais) {
        this.animais = animais;
    }

    public void adicionarAnimal(Animal animal) {
        animais.add(animal);
    }


    @Override
    public String toString() {
        return "Cliente{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", endereco='" + endereco + '\'' +
                ", email='" + email + '\'' +
                ", telefone='" + telefone + '\'' +
                ", cpf='" + cpf + '\'' +
                '}';
    }
}
