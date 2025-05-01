// src/main/java/com/clinica/command/UpdateVeterinarioCommand.java
package com.clinica.command;

import com.clinica.facade.ClinicaFacade;
import com.clinica.model.Veterinario;

public class UpdateVeterinarioCommand implements Command {

    private final ClinicaFacade facade;
    private final Veterinario estadoNovo;
    private Veterinario estadoAntigo; // Memento: guarda o estado anterior
    private final int idVeterinario;

    public UpdateVeterinarioCommand(ClinicaFacade facade, int id, Veterinario estadoNovo) {
        this.facade = facade;
        this.idVeterinario = id;
        // Cria cópia do estado novo para segurança
        this.estadoNovo = new Veterinario(
             estadoNovo.getNome(), estadoNovo.getEmail(), estadoNovo.getTelefone(),
             estadoNovo.getCpf(), estadoNovo.getSenha(), estadoNovo.getCrmv(),
             estadoNovo.getEspecialidade()
        );
        this.estadoNovo.setId(id); // Garante o ID
        this.estadoAntigo = null; // Será preenchido no execute (se facade retornar)
    }

    @Override
    public void execute() throws Exception {
        // Chama a facade para atualizar e guarda o estado antigo retornado
        this.estadoAntigo = facade.atualizarVeterinario(
            idVeterinario, estadoNovo.getNome(), estadoNovo.getEmail(),
            estadoNovo.getTelefone(), estadoNovo.getCpf(), estadoNovo.getSenha(),
            estadoNovo.getCrmv(), estadoNovo.getEspecialidade()
        );
        if (this.estadoAntigo == null) {
            throw new IllegalStateException("Falha ao obter o estado antigo do veterinário durante a atualização.");
        }
         System.out.println("Exec: Veterinário atualizado (ID: " + idVeterinario + ")");
    }

    @Override
    public void undo() throws Exception {
        if (estadoAntigo != null) {
            // Chama a facade para atualizar, mas passando os dados do estado antigo
            facade.atualizarVeterinario(
                idVeterinario, estadoAntigo.getNome(), estadoAntigo.getEmail(),
                estadoAntigo.getTelefone(), estadoAntigo.getCpf(), estadoAntigo.getSenha(),
                estadoAntigo.getCrmv(), estadoAntigo.getEspecialidade()
            );
            // O estado retornado pelo atualizarVeterinario no undo não é necessário aqui
             System.out.println("Undo: Veterinário restaurado para estado anterior (ID: " + idVeterinario + ")");
        } else {
            throw new IllegalStateException("Não é possível desfazer a atualização: estado antigo não foi guardado.");
        }
    }
}