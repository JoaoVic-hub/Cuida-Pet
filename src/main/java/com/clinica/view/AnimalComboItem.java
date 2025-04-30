package com.clinica.view; // Ou o pacote apropriado para itens de view/helpers

// Classe auxiliar para exibir animais no ComboBox
public class AnimalComboItem {
    private int id;
    private String nome;
    private String especie; // Pode ser útil exibir a espécie junto ao nome

    public AnimalComboItem(int id, String nome, String especie) {
        this.id = id;
        this.nome = nome;
        this.especie = especie;
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    @Override
    public String toString() {
        // Exibe ID, Nome e Espécie no ComboBox
        return String.format("%d - %s (%s)", id, nome, especie);
        // Ou apenas: return nome; se preferir
    }

    // Opcional: equals e hashCode se precisar comparar itens
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnimalComboItem that = (AnimalComboItem) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}