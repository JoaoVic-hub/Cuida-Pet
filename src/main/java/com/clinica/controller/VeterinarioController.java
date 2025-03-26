package com.clinica.controller;

import com.clinica.DAO.UsuarioRepository;
import com.clinica.model.Veterinario;

public class VeterinarioController {
    private UsuarioRepository repository = UsuarioRepository.getInstance();
    
    /**
     * Adiciona um novo Veterinário com os campos: nome, especialidade, CRMV, email e telefone.
     */
    public void adicionarVeterinario(String nome, String especialidade, String crmv, String email, String telefone, String cpf) {
        Veterinario vet = new Veterinario(nome, especialidade, crmv, email, telefone, cpf);
        repository.addVeterinario(vet);
        System.out.println("Veterinário adicionado: " + vet);
    }
    
    /**
     * Remove um Veterinário com base no id.
     */
    public void removerVeterinario(int id) {
        if(repository.removeVeterinario(id)) {
            System.out.println("Veterinário removido com sucesso!");
        } else {
            System.out.println("Veterinário não encontrado.");
        }
    }
}
