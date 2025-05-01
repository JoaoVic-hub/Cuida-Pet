package com.clinica.command;

import java.util.Stack;

public class UndoManager {
    private final Stack<Command> undoStack = new Stack<>();
    private final Stack<Command> redoStack = new Stack<>(); // Opcional para Redo

    /**
     * Executa um comando e o adiciona à pilha de undo.
     * Limpa a pilha de redo, pois uma nova ação invalida o redo anterior.
     * @param command O comando a ser executado.
     * @throws Exception Se a execução do comando falhar.
     */
    public void executeCommand(Command command) throws Exception {
        command.execute(); // Executa a ação
        undoStack.push(command); // Adiciona à pilha de undo
        redoStack.clear(); // Limpa a pilha de redo
        System.out.println("Comando executado e adicionado ao Undo: " + command.getClass().getSimpleName()); // Log
        printStackStatus(); // Log
    }

    /**
     * Verifica se a operação de Undo é possível.
     * @return true se a pilha de undo não estiver vazia.
     */
    public boolean canUndo() {
        return !undoStack.isEmpty();
    }

    /**
     * Desfaz o último comando executado.
     * Move o comando desfeito para a pilha de redo (se aplicável).
     * @throws Exception Se o desfazimento falhar ou não houver comando para desfazer.
     */
    public void undo() throws Exception {
        if (!canUndo()) {
            System.out.println("Nada para desfazer.");
            // Poderia lançar uma exceção ou simplesmente retornar
            return;
            // throw new IllegalStateException("Não há ações para desfazer.");
        }
        Command command = undoStack.pop();
        command.undo(); // Desfaz a ação
        redoStack.push(command); // Adiciona à pilha de redo
        System.out.println("Comando desfeito: " + command.getClass().getSimpleName()); // Log
        printStackStatus(); // Log
    }

    // --- Métodos Redo (Opcionais) ---
    public boolean canRedo() {
        return !redoStack.isEmpty();
    }

    public void redo() throws Exception {
        if (!canRedo()) {
             System.out.println("Nada para refazer.");
             return;
            // throw new IllegalStateException("Não há ações para refazer.");
        }
        Command command = redoStack.pop();
        command.execute(); // Re-executa a ação (ou chama um método redo() específico se necessário)
        undoStack.push(command); // Adiciona de volta à pilha de undo
        System.out.println("Comando refeito: " + command.getClass().getSimpleName()); // Log
        printStackStatus(); // Log
    }
    // --- Fim dos Métodos Redo ---

    private void printStackStatus() {
        System.out.println("  Undo Stack Size: " + undoStack.size());
        System.out.println("  Redo Stack Size: " + redoStack.size());
    }
}