package com.clinica.command;

public interface Command {
    /**
     * Executa a ação do comando.
     * @throws Exception se ocorrer um erro durante a execução.
     */
    void execute() throws Exception;

    /**
     * Desfaz a ação previamente executada pelo comando.
     * @throws Exception se ocorrer um erro durante o desfazimento.
     */
    void undo() throws Exception;

    // Opcional: Método para Redo
    // void redo() throws Exception;
}