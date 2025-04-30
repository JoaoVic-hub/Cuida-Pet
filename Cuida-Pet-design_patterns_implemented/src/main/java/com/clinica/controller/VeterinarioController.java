package com.clinica.controller;

import com.clinica.DAO.VeterinarioDAO;
import com.clinica.model.Veterinario;
import java.util.List;

public class VeterinarioController {
    private VeterinarioDAO veterinarioDAO;

    public VeterinarioController() {
        this.veterinarioDAO = new VeterinarioDAO();
    }

  
    public void adicionarVeterinario(String nome, String email, String telefone, String cpf,
                                     String senha, String crmv, String especialidade) {
        Veterinario vet = new Veterinario(nome, email, telefone, cpf, senha, crmv, especialidade);
        veterinarioDAO.inserir(vet);
        System.out.println("Veterinário cadastrado com sucesso!");
    }

   
    public void atualizarVeterinario(int id, String nome, String email, String telefone, String cpf,
                                     String senha, String crmv, String especialidade) {
        Veterinario vet = new Veterinario(nome, email, telefone, cpf, senha, crmv, especialidade);
        vet.setId(id);
        veterinarioDAO.alterar(vet);
        System.out.println("Veterinário atualizado com sucesso!");
    }

    public void removerVeterinario(int id) {
        veterinarioDAO.remover(id);
        System.out.println("Veterinário removido com sucesso!");
    }

    public List<Veterinario> listarTodosVeterinarios() {
        return veterinarioDAO.listarTodos();
    }

    public Veterinario buscarVeterinarioPorId(int id) {
        return veterinarioDAO.exibir(id);
    }

    public Veterinario autenticar(String email, String senha) {
        return veterinarioDAO.autenticar(email, senha);
    }
}
