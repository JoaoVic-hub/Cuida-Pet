// src/main/java/com/clinica/command/AddVeterinarioCommand.java
package com.clinica.command;

import com.clinica.facade.ClinicaFacade;
import com.clinica.model.Veterinario;

public class AddVeterinarioCommand implements Command {

    private final ClinicaFacade facade;
    private final Veterinario veterinarioParaAdicionar;
    private int idVeterinarioAdicionado = -1;

    public AddVeterinarioCommand(ClinicaFacade facade, Veterinario veterinario) {
        this.facade = facade;
        this.veterinarioParaAdicionar = new Veterinario(
            veterinario.getNome(), veterinario.getEmail(), veterinario.getTelefone(),
            veterinario.getCpf(), veterinario.getSenha(), veterinario.getCrmv(),
            veterinario.getEspecialidade()
        );
    }

    @Override
    public void execute() throws Exception {
        facade.adicionarVeterinario(
            veterinarioParaAdicionar.getNome(), veterinarioParaAdicionar.getEmail(),
            veterinarioParaAdicionar.getTelefone(), veterinarioParaAdicionar.getCpf(),
            veterinarioParaAdicionar.getSenha(), veterinarioParaAdicionar.getCrmv(),
            veterinarioParaAdicionar.getEspecialidade()
        );
        Veterinario adicionado = facade.listarTodosVeterinarios().stream()
                                    .filter(v -> v.getEmail().equals(veterinarioParaAdicionar.getEmail()))
                                    .findFirst().orElse(null); // Simplificado - ideal seria melhor critério
        if (adicionado != null) {
             this.idVeterinarioAdicionado = adicionado.getId();
             System.out.println("ID do veterinário adicionado para Undo: " + this.idVeterinarioAdicionado);
        } else {
             System.err.println("AVISO: Não foi possível obter o ID do veterinário recém-adicionado para o Undo.");
             this.idVeterinarioAdicionado = -1; // Indica falha
        }
    }

    @Override
    public void undo() throws Exception {
        if (idVeterinarioAdicionado != -1) {
            // Chama o método da facade para remover, usando o ID guardado
            facade.removerVeterinario(idVeterinarioAdicionado);
             System.out.println("Undo: Veterinário removido (ID: " + idVeterinarioAdicionado + ")");
        } else {
             throw new IllegalStateException("Não é possível desfazer a adição: ID do veterinário não foi determinado.");
        }
    }
}