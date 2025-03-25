package com.clinica.model;

import java.util.List;

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
    private String endereco;
    private List<Animal> animais;

    public Cliente(String nome, String endereco, String email, String telefone, String cpf) {
        super(nome, email, telefone, cpf);
        // Aqui também pode ser aplicada validação de tamanho para os endereços.
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

    // Método para adicionar um animal ao cliente
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
