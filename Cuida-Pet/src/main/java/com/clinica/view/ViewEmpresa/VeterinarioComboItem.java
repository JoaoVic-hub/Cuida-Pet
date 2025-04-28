package com.clinica.view.ViewEmpresa;


public class VeterinarioComboItem {
    private int id;
    private String nome;

    public VeterinarioComboItem(int id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return nome; 
    }
}