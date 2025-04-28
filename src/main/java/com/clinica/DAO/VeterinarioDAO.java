package com.clinica.DAO;

import com.clinica.model.Veterinario;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VeterinarioDAO {
    private Connection conexao;

    public VeterinarioDAO() {
        this.conexao = ConexaoMySQL.getConexao();
    }


    public void inserir(Veterinario vet) {
        String sql = "INSERT INTO veterinario (nome, email, telefone, cpf, senha, crmv, especialidade) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, vet.getNome());
            stmt.setString(2, vet.getEmail());
            stmt.setString(3, vet.getTelefone());
            stmt.setString(4, vet.getCpf());
            stmt.setString(5, vet.getSenha());
            stmt.setString(6, vet.getCrmv());
            stmt.setString(7, vet.getEspecialidade());

            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    vet.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void alterar(Veterinario vet) {
        String sql = "UPDATE veterinario SET nome=?, email=?, telefone=?, cpf=?, senha=?, crmv=?, especialidade=? " +
                     "WHERE id=?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, vet.getNome());
            stmt.setString(2, vet.getEmail());
            stmt.setString(3, vet.getTelefone());
            stmt.setString(4, vet.getCpf());
            stmt.setString(5, vet.getSenha());
            stmt.setString(6, vet.getCrmv());
            stmt.setString(7, vet.getEspecialidade());
            stmt.setInt(8, vet.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void remover(int id) {
        String sql = "DELETE FROM veterinario WHERE id=?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Veterinario> listarTodos() {
        List<Veterinario> vets = new ArrayList<>();
        String sql = "SELECT * FROM veterinario";
        try (Statement stmt = conexao.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Veterinario vet = new Veterinario(
                    rs.getString("nome"),        // nome
                    rs.getString("email"),       // email
                    rs.getString("telefone"),    // telefone
                    rs.getString("cpf"),         // cpf
                    rs.getString("senha"),       // senha
                    rs.getString("crmv"),        // crmv
                    rs.getString("especialidade")// especialidade
                );
                vet.setId(rs.getInt("id"));
                vets.add(vet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return vets;
    }


    public Veterinario exibir(int id) {
        String sql = "SELECT * FROM veterinario WHERE id=?";
        Veterinario vet = null;
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    vet = new Veterinario(
                        rs.getString("nome"),
                        rs.getString("email"),
                        rs.getString("telefone"),
                        rs.getString("cpf"),
                        rs.getString("senha"),
                        rs.getString("crmv"),
                        rs.getString("especialidade")
                    );
                    vet.setId(rs.getInt("id"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return vet;
    }


    public Veterinario autenticar(String email, String senha) {
        String sql = "SELECT * FROM veterinario WHERE email=? AND senha=?";
        Veterinario vet = null;
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.setString(2, senha);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    vet = new Veterinario(
                        rs.getString("nome"),
                        rs.getString("email"),
                        rs.getString("telefone"),
                        rs.getString("cpf"),
                        rs.getString("senha"),
                        rs.getString("crmv"),
                        rs.getString("especialidade")
                    );
                    vet.setId(rs.getInt("id"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return vet;
    }
}
