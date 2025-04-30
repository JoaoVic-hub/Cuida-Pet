package com.clinica.model;

// import java.util.Stack; // Descomente para implementar histórico/desfazer

/**
 * Caretaker (Zelador): Responsável por guardar o(s) Memento(s).
 * Não opera ou examina o conteúdo do Memento, apenas o armazena.
 * 
 * Esta implementação simples guarda apenas o último Memento salvo.
 * Para funcionalidades de desfazer/refazer, uma estrutura como Stack seria mais apropriada.
 */
public class ClienteCareTaker {
    private ClienteMemento memento;
    // private Stack<ClienteMemento> historico = new Stack<>(); // Para histórico

    /**
     * Salva o estado (Memento) fornecido.
     * Substitui qualquer Memento salvo anteriormente nesta implementação simples.
     *
     * @param memento O ClienteMemento a ser salvo.
     */
    public void saveState(ClienteMemento memento) {
        System.out.println("CareTaker: Salvando Memento...");
        this.memento = memento;
        // historico.push(memento); // Para histórico
    }

    /**
     * Recupera o último Memento salvo.
     *
     * @return O último ClienteMemento salvo, ou null se nenhum foi salvo ainda.
     */
    public ClienteMemento retrieveLastState() {
        System.out.println("CareTaker: Recuperando último Memento salvo...");
        // return historico.pop(); // Para histórico (desfazer)
        return this.memento;
    }
    
    // Métodos adicionais poderiam ser implementados para gerenciar um histórico
    // public boolean hasHistory() { return !historico.isEmpty(); }
    // public ClienteMemento peekLastState() { return historico.peek(); } // Ver sem remover
}

