package com.clinica.repository;

import com.clinica.model.Cliente;
import com.clinica.model.Veterinario;
import com.clinica.model.ADM;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

public class UsuarioRepository {
    private static UsuarioRepository instance;
    
    private List<Cliente> clientes;
    private List<Veterinario> veterinarios;
    private ADM adm; // Instância única de ADM
    
    private UsuarioRepository() {
        clientes = new ArrayList<>();
        veterinarios = new ArrayList<>();
        // Criação de um ADM padrão
        adm = new ADM("Administrador", "admin@clinica.com", "123456789");
    }
    
    public static UsuarioRepository getInstance() {
        if (instance == null) {
            instance = new UsuarioRepository();
        }
        return instance;
    }
    
    // Métodos para Cliente
    public void addCliente(Cliente cliente) {
        clientes.add(cliente);
    }
    
    public boolean removeCliente(int id) {
        Iterator<Cliente> iterator = clientes.iterator();
        while (iterator.hasNext()) {
            Cliente c = iterator.next();
            if(c.getId() == id) {
                iterator.remove();
                return true;
            }
        }
        return false;
    }
    
    public List<Cliente> getClientes() {
        return clientes;
    }
    
    // Métodos para Veterinário
    public void addVeterinario(Veterinario vet) {
        veterinarios.add(vet);
    }
    
    public boolean removeVeterinario(int id) {
        Iterator<Veterinario> iterator = veterinarios.iterator();
        while (iterator.hasNext()) {
            Veterinario v = iterator.next();
            if(v.getId() == id) {
                iterator.remove();
                return true;
            }
        }
        return false;
    }
    
    public List<Veterinario> getVeterinarios() {
        return veterinarios;
    }
    
    // Método para acessar o ADM (sem métodos CRUD)
    public ADM getAdm() {
        return adm;
    }
    
    // Método para obter todos os usuários (ADM, Clientes e Veterinários)
    public List<Object> getAllUsuarios() {
        List<Object> usuarios = new ArrayList<>();
        usuarios.add(adm);
        usuarios.addAll(clientes);
        usuarios.addAll(veterinarios);
        return usuarios;
    }
}
