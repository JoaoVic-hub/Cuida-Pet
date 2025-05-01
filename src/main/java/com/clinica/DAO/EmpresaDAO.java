package com.clinica.DAO;

import com.clinica.model.Empresa; 
import com.clinica.persistence.JsonPersistenceHelper;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EmpresaDAO {

    private final JsonPersistenceHelper<Empresa> persistenceHelper;
    private List<Empresa> empresas; 

    public EmpresaDAO() {
        this.persistenceHelper = new JsonPersistenceHelper<>("empresa.json", new TypeReference<List<Empresa>>() {});
        this.empresas = persistenceHelper.readAll();
    }

     private void saveData() {
        persistenceHelper.writeAll(empresas);
    }

    public void inserir(Empresa empresa) {
        int nextId = persistenceHelper.getNextId(empresas);
        empresa.setId(nextId);
        empresas.add(empresa);
        saveData();
    }

     public void alterar(Empresa empresaAtualizada) {
        Optional<Empresa> empresaExistente = empresas.stream()
                .filter(e -> e.getId() == empresaAtualizada.getId())
                .findFirst();

        empresaExistente.ifPresent(e -> {
            e.setNome(empresaAtualizada.getNome());
            e.setEmail(empresaAtualizada.getEmail());
            e.setSenha(empresaAtualizada.getSenha());
            e.setCnpj(empresaAtualizada.getCnpj());
            e.setTelefone(empresaAtualizada.getTelefone());
            saveData();
        });
         if (empresaExistente.isEmpty()) {
             System.err.println("Tentativa de alterar empresa inexistente com ID: " + empresaAtualizada.getId());
        }
    }

    public void remover(int id) {
        boolean removed = empresas.removeIf(e -> e.getId() == id);
        if (removed) {
            saveData();
        }
         if (!removed) {
             System.err.println("Tentativa de remover empresa inexistente com ID: " + id);
        }
    }

     public Empresa getEmpresa() {
          return empresas.isEmpty() ? null : empresas.get(0);
     }

    public List<Empresa> listarTodos() {
        return new ArrayList<>(empresas);
    }

    public Empresa exibir(int id) {
        return empresas.stream()
                .filter(e -> e.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public Empresa autenticar(String email, String senha) {
        if (email == null || senha == null) return null;
        return empresas.stream()
                .filter(e -> email.equalsIgnoreCase(e.getEmail()) && senha.equals(e.getSenha()))
                .findFirst()
                .orElse(null);
    }
}