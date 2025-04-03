package com.clinica.model;

import java.util.List;


public class Cliente extends Usuario {
    private String endereco;
    private List<Animal> animais;

    public Cliente(String nome, String endereco, String email, String telefone, String cpf) {
        super(nome, email, telefone, cpf);
        
        this.endereco = endereco;
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

    public void adicionarAnimal(Animal animal) {
        animais.add(animal);
    }
    
    @Override
    public String toString() {
        return "Cliente{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", enderecoLinha1='" + endereco + '\'' +
                ", email='" + email + '\'' +
                ", telefone='" + telefone + '\'' +
                '}';
    }
}
