package com.clinica.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa um cliente da clínica veterinária.
 * Herda atributos básicos de Usuário e adiciona informações específicas do cliente.
 * Implementa métodos para suportar o padrão Memento (salvar/restaurar estado).
 */
public class Cliente extends Usuario {
    private String endereco;
    private List<Animal> animais; // A lista de animais não faz parte do estado salvo pelo Memento nesta implementação

    // Campos adicionais que estavam faltando na leitura anterior, mas presentes no DAO/Form
    private String cep;
    private String numero;
    private String complemento;

    public Cliente() {
        this.animais = new ArrayList<>();
    }

    public Cliente(String nome,
                   String endereco,
                   String email,
                   String telefone,
                   String cpf,
                   String senha,
                   String cep, // Adicionado
                   String numero, // Adicionado
                   String complemento) // Adicionado
    {
        super(nome, email, telefone, cpf, senha);
        this.endereco = endereco;
        this.cep = cep;
        this.numero = numero;
        this.complemento = complemento;
        this.animais = new ArrayList<>();
    }

    // Getters e Setters

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

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    // --- Métodos do Padrão Memento --- 

    /**
     * Salva o estado atual do objeto Cliente em um Memento.
     * 
     * @return Um objeto ClienteMemento contendo o estado atual.
     */
    public ClienteMemento saveToMemento() {
        System.out.println("Cliente: Salvando estado para Memento...");
        // Passa os atributos relevantes para o construtor do Memento
        return new ClienteMemento(this.nome, this.email, this.telefone, this.cpf, this.endereco, this.cep, this.numero, this.complemento);
    }

    /**
     * Restaura o estado do objeto Cliente a partir de um Memento.
     *
     * @param memento O ClienteMemento contendo o estado a ser restaurado.
     */
    public void restoreFromMemento(ClienteMemento memento) {
        System.out.println("Cliente: Restaurando estado do Memento...");
        this.nome = memento.getNome();
        this.email = memento.getEmail();
        this.telefone = memento.getTelefone();
        this.cpf = memento.getCpf();
        this.endereco = memento.getEndereco();
        this.cep = memento.getCep();
        this.numero = memento.getNumero();
        this.complemento = memento.getComplemento();
        // Nota: A senha e a lista de animais não são restauradas por este Memento.
        // A senha geralmente não deve ser parte do estado restaurável por segurança.
        // A lista de animais exigiria um Memento mais complexo ou outra estratégia.
    }

    // --- Fim dos Métodos do Padrão Memento --- 

    @Override
    public String toString() {
        return "Cliente{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", cpf='" + cpf + '\'' +
                ", telefone='" + telefone + '\'' +
                ", email='" + email + '\'' +
                ", endereco='" + endereco + '\'' +
                ", cep='" + cep + '\'' +
                ", numero='" + numero + '\'' +
                ", complemento='" + complemento + '\'' +
                // Não incluir a senha no toString por segurança
                // ", animais=" + animais + // Pode ser muito longo
                '}';
    }
}

