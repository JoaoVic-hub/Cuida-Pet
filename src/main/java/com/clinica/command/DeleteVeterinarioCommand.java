// src/main/java/com/clinica/command/DeleteVeterinarioCommand.java
package com.clinica.command;

import com.clinica.facade.ClinicaFacade;
import com.clinica.model.Veterinario;

public class DeleteVeterinarioCommand implements Command {

    private final ClinicaFacade facade;
    private final int idVeterinarioParaRemover;
    private Veterinario veterinarioRemovido; // Memento: guarda o estado do vet removido

    public DeleteVeterinarioCommand(ClinicaFacade facade, int idVeterinario) {
        this.facade = facade;
        this.idVeterinarioParaRemover = idVeterinario;
        this.veterinarioRemovido = null; // Será preenchido no execute (se facade retornar)
    }

    @Override
    public void execute() throws Exception {
        // Chama a facade para remover E guarda o objeto removido (se facade retornar)
        this.veterinarioRemovido = facade.removerVeterinario(idVeterinarioParaRemover);
        if (this.veterinarioRemovido == null) {
             // Isso não deveria acontecer se removerVeterinario lançar exceção em caso de erro
             throw new IllegalStateException("Falha ao obter o veterinário removido durante a execução.");
        }
         System.out.println("Exec: Veterinário removido (ID: " + idVeterinarioParaRemover + ")");
    }

    @Override
    public void undo() throws Exception {
        if (veterinarioRemovido != null) {
            // Chama a facade para adicionar o veterinário de volta
            // ATENÇÃO: Isso pode gerar um ID DIFERENTE se a lógica de ID for sequencial!
            // Idealmente, o DAO/Facade precisaria de um método "inserirComId" ou
            // a lógica de ID precisaria ser mais robusta.
            // Solução simples (mas com potencial problema de ID):
            facade.adicionarVeterinario(
                veterinarioRemovido.getNome(), veterinarioRemovido.getEmail(),
                veterinarioRemovido.getTelefone(), veterinarioRemovido.getCpf(),
                veterinarioRemovido.getSenha(), veterinarioRemovido.getCrmv(),
                veterinarioRemovido.getEspecialidade()
            );
            System.out.println("Undo: Veterinário readicionado (pode ter novo ID) - Nome: " + veterinarioRemovido.getNome());

            // TODO: Uma solução mais robusta seria ter um método no DAO/Facade
            // para reinserir com o ID original, ou marcar como "ativo/inativo"
            // em vez de remover fisicamente.
        } else {
            throw new IllegalStateException("Não é possível desfazer a remoção: objeto removido não foi guardado.");
        }
    }
}