package com.clinica.DAO;

import com.clinica.model.Animal;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AnimalDAO {
    private Connection conexao;

    public AnimalDAO() {
        this.conexao = ConexaoMySQL.getConexao();
    }

    public void inserir(Animal animal) {
        String sql = "INSERT INTO animal (nome, especie, raca, data_nascimento, cliente_id) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, animal.getNome());
            stmt.setString(2, animal.getEspecie());
            stmt.setString(3, animal.getRaca());
            
            stmt.setDate(4, Date.valueOf(animal.getDataNascimento()));
            stmt.setInt(5, animal.getClienteId());

            stmt.executeUpdate();


            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    animal.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void alterar(Animal animal) {
        String sql = "UPDATE animal SET nome=?, especie=?, raca=?, data_nascimento=?, cliente_id=? WHERE id=?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, animal.getNome());
            stmt.setString(2, animal.getEspecie());
            stmt.setString(3, animal.getRaca());
            stmt.setDate(4, Date.valueOf(animal.getDataNascimento()));
            stmt.setInt(5, animal.getClienteId());
            stmt.setInt(6, animal.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void remover(int id) {
        String sql = "DELETE FROM animal WHERE id=?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Animal> listarTodos() {
        List<Animal> animais = new ArrayList<>();
        String sql = "SELECT * FROM animal";
        try (Statement stmt = conexao.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Animal animal = new Animal();
                animal.setId(rs.getInt("id"));
                animal.setNome(rs.getString("nome"));
                animal.setEspecie(rs.getString("especie"));
                animal.setRaca(rs.getString("raca"));
                Date sqlDate = rs.getDate("data_nascimento");
                if (sqlDate != null) {
                    animal.setDataNascimento(sqlDate.toLocalDate());
                }
                animal.setClienteId(rs.getInt("cliente_id"));

                animais.add(animal);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return animais;
    }

    public Animal exibir(int id) {
        Animal animal = null;
        String sql = "SELECT * FROM animal WHERE id=?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    animal = new Animal();
                    animal.setId(rs.getInt("id"));
                    animal.setNome(rs.getString("nome"));
                    animal.setEspecie(rs.getString("especie"));
                    animal.setRaca(rs.getString("raca"));
                    Date sqlDate = rs.getDate("data_nascimento");
                    if (sqlDate != null) {
                        animal.setDataNascimento(sqlDate.toLocalDate());
                    }
                    animal.setClienteId(rs.getInt("cliente_id"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return animal;
    }
    
 
    public List<Animal> pesquisarPorNome(String nome) {
        List<Animal> animais = new ArrayList<>();
        String sql = "SELECT * FROM animal WHERE nome LIKE ?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, "%" + nome + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Animal animal = new Animal();
                    animal.setId(rs.getInt("id"));
                    animal.setNome(rs.getString("nome"));
                    animal.setEspecie(rs.getString("especie"));
                    animal.setRaca(rs.getString("raca"));
                    Date sqlDate = rs.getDate("data_nascimento");
                    if (sqlDate != null) {
                        animal.setDataNascimento(sqlDate.toLocalDate());
                    }
                    animal.setClienteId(rs.getInt("cliente_id"));
                    animais.add(animal);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return animais;
    }
}
