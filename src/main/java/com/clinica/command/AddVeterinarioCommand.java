// src/main/java/com/clinica/command/AddVeterinarioCommand.java
package com.clinica.command;

import com.clinica.facade.ClinicaFacade;
import com.clinica.model.Veterinario;

public class AddVeterinarioCommand implements Command {

    private final ClinicaFacade facade;
    private final Veterinario veterinarioParaAdicionar;
    private int idVeterinarioAdicionado = -1; // Para guardar o ID após adição

    public AddVeterinarioCommand(ClinicaFacade facade, Veterinario veterinario) {
        this.facade = facade;
        // É importante criar uma cópia para evitar que o objeto original
        // seja modificado externamente antes do execute/undo
        this.veterinarioParaAdicionar = new Veterinario(
            veterinario.getNome(), veterinario.getEmail(), veterinario.getTelefone(),
            veterinario.getCpf(), veterinario.getSenha(), veterinario.getCrmv(),
            veterinario.getEspecialidade()
        );
        // O ID será definido após a inserção
    }

    @Override
    public void execute() throws Exception {
        // Chama o método original da facade para adicionar
        // Nota: O método da facade não precisa retornar o objeto aqui,
        // pois já o temos em veterinarioParaAdicionar. O ID será setado pela facade/DAO.
        facade.adicionarVeterinario(
            veterinarioParaAdicionar.getNome(), veterinarioParaAdicionar.getEmail(),
            veterinarioParaAdicionar.getTelefone(), veterinarioParaAdicionar.getCpf(),
            veterinarioParaAdicionar.getSenha(), veterinarioParaAdicionar.getCrmv(),
            veterinarioParaAdicionar.getEspecialidade()
        );
        // Precisamos recuperar o ID que foi gerado para o undo funcionar
        // Isso é um ponto fraco do design atual, a facade não retorna o ID.
        // Solução alternativa: Buscar o último veterinário adicionado (arriscado se houver concorrência)
        // Solução melhor: Modificar facade/DAO para retornar o objeto com ID.
        // Assumindo que a facade/DAO SETA o ID no objeto passado ou retorna o objeto com ID:
        // Vamos buscar pelo email/cpf (assumindo que são únicos) para obter o ID para o UNDO.
        Veterinario adicionado = facade.listarTodosVeterinarios().stream()
                                    .filter(v -> v.getEmail().equals(veterinarioParaAdicionar.getEmail()))
                                    .findFirst().orElse(null); // Simplificado - ideal seria melhor critério
        if (adicionado != null) {
             this.idVeterinarioAdicionado = adicionado.getId();
             System.out.println("ID do veterinário adicionado para Undo: " + this.idVeterinarioAdicionado);
        } else {
             // Não conseguiu ID, Undo pode falhar
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