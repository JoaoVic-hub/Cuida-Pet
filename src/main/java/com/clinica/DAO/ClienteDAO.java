package com.clinica.DAO;

import com.clinica.model.Cliente;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {
    private Connection conexao;

    public ClienteDAO() {
        this.conexao = ConexaoMySQL.getConexao();
    }

    
    public void inserir(Cliente cliente) {
        String sql = "INSERT INTO cliente (nome, endereco, email, telefone, cpf) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, cliente.getNome());
            stmt.setString(2, cliente.getEndereco());
            stmt.setString(3, cliente.getEmail());
            stmt.setString(4, cliente.getTelefone());
            stmt.setString(5, cliente.getCpf());

            stmt.executeUpdate();

            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    cliente.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    
    public void alterar(Cliente cliente) {
        String sql = "UPDATE cliente SET nome=?, endereco=?, email=?, telefone=?, cpf=? WHERE id=?";

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, cliente.getNome());
            stmt.setString(2, cliente.getEndereco());
            stmt.setString(3, cliente.getEmail());
            stmt.setString(4, cliente.getTelefone());
            stmt.setString(5, cliente.getCpf());
            stmt.setInt(6, cliente.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    
    public List<Cliente> pesquisarPorNome(String nome) {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT * FROM cliente WHERE nome LIKE ?";
    
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, "%" + nome + "%");
            ResultSet rs = stmt.executeQuery();
    
            while (rs.next()) {
                Cliente cliente = new Cliente(
                    rs.getString("nome"),
                    rs.getString("endereco"),
                    rs.getString("email"),
                    rs.getString("telefone"),
                    rs.getString("cpf")
                );
                cliente.setId(rs.getInt("id")); 
                clientes.add(cliente);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    
        return clientes;
    }
    

    
    public void remover(int id) {
        String sql = "DELETE FROM cliente WHERE id=?";

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    
    public List<Cliente> listarTodos() {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT * FROM cliente";
    
        try (Statement stmt = conexao.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
    
            while (rs.next()) {
                Cliente cliente = new Cliente(
                    rs.getString("nome"),
                    rs.getString("endereco"),
                    rs.getString("email"),
                    rs.getString("telefone"),
                    rs.getString("cpf")
                );
                cliente.setId(rs.getInt("id")); 
                clientes.add(cliente);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clientes;
    }
    

    
    public Cliente exibir(int id) {
        String sql = "SELECT * FROM cliente WHERE id=?";
        Cliente cliente = null;
    
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
    
            if (rs.next()) {
                cliente = new Cliente(
                    rs.getString("nome"),
                    rs.getString("endereco"),
                    rs.getString("email"),
                    rs.getString("telefone"),
                    rs.getString("cpf")
                );
                cliente.setId(rs.getInt("id")); 
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    
        return cliente;
    }

}
