package com.clinica.model;

import java.util.ArrayList;
import java.util.List;

public class Cliente extends Usuario {
    private String endereco;
    private List<Animal> animais;

    /**
     * Construtor vazio (necessário para quando você quer instanciar
     * sem ter todos os dados imediatamente, ou uso de frameworks).
     */
    public Cliente() {
        this.animais = new ArrayList<>();
    }

    /**
     * Construtor completo, na ordem:
     * (nome, endereco, email, telefone, cpf, senha)
     *
     * Isso bate com as colunas:
     * - 1º param: nome      (coluna 'nome')
     * - 2º param: endereco  (coluna 'endereco')
     * - 3º param: email     (coluna 'email')
     * - 4º param: telefone  (coluna 'telefone')
     * - 5º param: cpf       (coluna 'cpf')
     * - 6º param: senha     (coluna 'senha')
     */
    public Cliente(String nome,
                   String endereco,
                   String email,
                   String telefone,
                   String cpf,
                   String senha)
    {
        /*
         * O construtor de Usuario normalmente é:
         *   super(String nome, String email, String telefone, String cpf, String senha)
         *
         * Portanto, precisamos passar:
         *   nome  (vem do param #1)
         *   email (vem do param #3)
         *   telefone (vem do param #4)
         *   cpf (vem do param #5)
         *   senha (vem do param #6)
         *
         * E o param #2 ("endereco") armazenamos no campo 'endereco' desta classe.
         */
        super(nome, email, telefone, cpf, senha);

        // O segundo parâmetro do construtor é 'endereco'
        this.endereco = endereco;

        // Lista para evitar NullPointerException
        this.animais = new ArrayList<>();
    }

    // ------------------- GETs e SETs -------------------
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

    // ------------------- toString -------------------
    @Override
    public String toString() {
        return "Cliente{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", endereco='" + endereco + '\'' +
                ", email='" + email + '\'' +
                ", telefone='" + telefone + '\'' +
                ", cpf='" + cpf + '\'' +
                // Geralmente não se mostra a senha
                '}';
    }
}
