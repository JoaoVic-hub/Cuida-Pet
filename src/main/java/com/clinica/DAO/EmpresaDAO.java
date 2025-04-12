package com.clinica.DAO;

import com.clinica.model.Empresa;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmpresaDAO {
    private Connection conexao;

    public EmpresaDAO() {
        this.conexao = ConexaoMySQL.getConexao();
    }

    // ==================== MÉTODO INSERIR ====================
    public void inserir(Empresa empresa) {
        // Observe que a ordem dos campos no INSERT deve combinar com a estrutura desejada.
        String sql = "INSERT INTO empresa (nome, email, senha, cnpj, telefone) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, empresa.getNome());
            stmt.setString(2, empresa.getEmail());
            stmt.setString(3, empresa.getSenha());
            stmt.setString(4, empresa.getCnpj());
            stmt.setString(5, empresa.getTelefone());
            stmt.executeUpdate();
            
            // Recupera o ID gerado pelo banco e seta no objeto empresa
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    empresa.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ==================== MÉTODO ALTERAR ====================
    public void alterar(Empresa empresa) {
        String sql = "UPDATE empresa SET nome=?, email=?, senha=?, cnpj=?, telefone=? WHERE id=?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, empresa.getNome());
            stmt.setString(2, empresa.getEmail());
            stmt.setString(3, empresa.getSenha());
            stmt.setString(4, empresa.getCnpj());
            stmt.setString(5, empresa.getTelefone());
            stmt.setInt(6, empresa.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ==================== MÉTODO REMOVER ====================
    public void remover(int id) {
        String sql = "DELETE FROM empresa WHERE id=?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ==================== MÉTODO LISTAR TODOS ====================
    public List<Empresa> listarTodos() {
        List<Empresa> empresas = new ArrayList<>();
        String sql = "SELECT * FROM empresa";
        try (Statement stmt = conexao.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                // Construtor da classe Empresa: (nome, email, senha, cnpj, telefone)
                Empresa empresa = new Empresa(
                    rs.getString("nome"),      // nome
                    rs.getString("email"),     // email
                    rs.getString("senha"),     // senha
                    rs.getString("cnpj"),      // cnpj
                    rs.getString("telefone")   // telefone
                );
                empresa.setId(rs.getInt("id"));
                empresas.add(empresa);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return empresas;
    }

    // ==================== MÉTODO EXIBIR POR ID ====================
    public Empresa exibir(int id) {
        Empresa empresa = null;
        String sql = "SELECT * FROM empresa WHERE id=?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    empresa = new Empresa(
                        rs.getString("nome"),
                        rs.getString("email"),
                        rs.getString("senha"),
                        rs.getString("cnpj"),
                        rs.getString("telefone")
                    );
                    empresa.setId(rs.getInt("id"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return empresa;
    }

    // ==================== MÉTODO AUTENTICAR ====================
    public Empresa autenticar(String email, String senha) {
        Empresa empresa = null;
        String sql = "SELECT * FROM empresa WHERE email=? AND senha=?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.setString(2, senha);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    empresa = new Empresa(
                        rs.getString("nome"),
                        rs.getString("email"),
                        rs.getString("senha"),
                        rs.getString("cnpj"),
                        rs.getString("telefone")
                    );
                    empresa.setId(rs.getInt("id"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return empresa;
    }
}
