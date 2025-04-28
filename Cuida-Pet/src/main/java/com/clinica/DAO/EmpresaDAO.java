package com.clinica.DAO;

import com.clinica.model.Empresa; // << PRECISA implementar Identifiable
import com.clinica.persistence.JsonPersistenceHelper;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;


public class EmpresaDAO {

    private final JsonPersistenceHelper<Empresa> persistenceHelper;
    private List<Empresa> empresas; // Normalmente haverá apenas uma, mas a estrutura suporta mais

    public EmpresaDAO() {
        // Usando um nome singular pois geralmente só há uma empresa configurada
        this.persistenceHelper = new JsonPersistenceHelper<>("empresa.json", new TypeReference<List<Empresa>>() {});
        this.empresas = persistenceHelper.readAll();
    }

     private void saveData() {
        persistenceHelper.writeAll(empresas);
    }

    // Em muitas aplicações, só existe UMA empresa. Métodos de inserir/alterar podem ser adaptados.
    // Este exemplo mantém a lógica de múltiplos registros, caso necessário.

    public void inserir(Empresa empresa) {
         // Se só pode haver uma empresa, verificar antes de inserir
         // if (!empresas.isEmpty()) {
         //     System.err.println("Já existe uma empresa cadastrada. Use 'alterar'.");
         //     return; // Ou lançar exceção
         // }
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
            e.setSenha(empresaAtualizada.getSenha()); // !! Cuidado !!
            e.setCnpj(empresaAtualizada.getCnpj());
            e.setTelefone(empresaAtualizada.getTelefone());
            saveData();
        });
         if (empresaExistente.isEmpty()) {
             System.err.println("Tentativa de alterar empresa inexistente com ID: " + empresaAtualizada.getId());
        }
    }

     // Remover pode não fazer sentido se só pode haver uma empresa
    public void remover(int id) {
        boolean removed = empresas.removeIf(e -> e.getId() == id);
        if (removed) {
            saveData();
        }
         if (!removed) {
             System.err.println("Tentativa de remover empresa inexistente com ID: " + id);
        }
    }

     // Retorna a primeira empresa encontrada, ou null.
     // Idealmente, haveria apenas uma.
     public Empresa getEmpresa() {
          return empresas.isEmpty() ? null : empresas.get(0);
     }

    // Lista todas as empresas (caso haja mais de uma)
    public List<Empresa> listarTodos() {
        return new ArrayList<>(empresas);
    }

    public Empresa exibir(int id) {
        return empresas.stream()
                .filter(e -> e.getId() == id)
                .findFirst()
                .orElse(null);
    }

     // !! CUIDADO: Senha em texto plano é inseguro !!
    public Empresa autenticar(String email, String senha) {
        if (email == null || senha == null) return null;
        return empresas.stream()
                .filter(e -> email.equalsIgnoreCase(e.getEmail()) && senha.equals(e.getSenha()))
                .findFirst()
                .orElse(null);
    }
}