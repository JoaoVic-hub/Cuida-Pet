package com.clinica.DAO;

import com.clinica.model.Animal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AnimalDAO {

    // Método para inserir um novo animal no banco de dados
    public void inserir(Animal animal) {
        String sql = "INSERT INTO animais (nome, especie, raca, idade, cliente_id) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = ConexaoMySQL.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, animal.getNome());
            stmt.setString(2, animal.getEspecie());
            stmt.setString(3, animal.getRaca());
            stmt.setInt(4, animal.getIdade());
            stmt.setInt(5, animal.getClienteId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Método para alterar os dados de um animal
    public void alterar(Animal animal) {
        String sql = "UPDATE animais SET nome = ?, especie = ?, raca = ?, idade = ? WHERE id = ?";
        
        try (Connection conn = ConexaoMySQL.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, animal.getNome());
            stmt.setString(2, animal.getEspecie());
            stmt.setString(3, animal.getRaca());
            stmt.setInt(4, animal.getIdade());
            stmt.setInt(5, animal.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Método para buscar um animal pelo nome
    public List<Animal> pesquisarPorNome(String nome) {
        List<Animal> animais = new ArrayList<>();
        String sql = "SELECT * FROM animais WHERE nome LIKE ?";
        
        try (Connection conn = ConexaoMySQL.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + nome + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Animal animal = new Animal(
                    rs.getInt("id"),
                    rs.getString("nome"),
                    rs.getString("especie"),
                    rs.getString("raca"),
                    rs.getInt("idade"),
                    rs.getInt("cliente_id")
                );
                animais.add(animal);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return animais;
    }

    // Método para remover um animal pelo ID
    public void remover(int id) {
        String sql = "DELETE FROM animais WHERE id = ?";
        
        try (Connection conn = ConexaoMySQL.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Método para listar todos os animais do banco de dados
    public List<Animal> listarTodos() {
        List<Animal> animais = new ArrayList<>();
        String sql = "SELECT * FROM animais";
        
        try (Connection conn = ConexaoMySQL.getConexao();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Animal animal = new Animal(
                    rs.getInt("id"),
                    rs.getString("nome"),
                    rs.getString("especie"),
                    rs.getString("raca"),
                    rs.getInt("idade"),
                    rs.getInt("cliente_id")
                );
                animais.add(animal);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return animais;
    }

    // Método para retornar um animal pelo ID
    public Animal retornaAnimal(int id) {
        String sql = "SELECT * FROM animais WHERE id = ?";
        Animal animal = null;
        
        try (Connection conn = ConexaoMySQL.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                animal = new Animal(
                    rs.getInt("id"),
                    rs.getString("nome"),
                    rs.getString("especie"),
                    rs.getString("raca"),
                    rs.getInt("idade"),
                    rs.getInt("cliente_id")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return animal;
    }
}
