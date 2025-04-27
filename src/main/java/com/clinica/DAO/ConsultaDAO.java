package com.clinica.DAO;

import com.clinica.model.Consulta;
import com.clinica.model.Animal;
import com.clinica.model.Cliente;
import com.clinica.model.Veterinario;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ConsultaDAO {
    private Connection conexao;

    public ConsultaDAO() {
        this.conexao = ConexaoMySQL.getConexao();
    }

  
    public void inserir(Consulta consulta) {
        String sql = "INSERT INTO consulta (data_hora, status, id_cliente, id_animal, id_veterinario) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setTimestamp(1, Timestamp.valueOf(consulta.getDataHora()));
            stmt.setString(2, consulta.getStatus());
            stmt.setInt(3, consulta.getCliente().getId());
            if (consulta.getAnimal() != null) {
                stmt.setInt(4, consulta.getAnimal().getId());
            } else {
                stmt.setNull(4, java.sql.Types.INTEGER);
            }
            stmt.setInt(5, consulta.getVeterinario().getId());

            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    consulta.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void alterar(Consulta consulta) {
        String sql = "UPDATE consulta SET data_hora=?, status=?, id_cliente=?, id_animal=?, id_veterinario=? WHERE id=?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setTimestamp(1, Timestamp.valueOf(consulta.getDataHora()));
            stmt.setString(2, consulta.getStatus());
            stmt.setInt(3, consulta.getCliente().getId());
            stmt.setInt(4, consulta.getAnimal().getId());
            stmt.setInt(5, consulta.getVeterinario().getId());
            stmt.setInt(6, consulta.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void remover(int id) {
        String sql = "DELETE FROM consulta WHERE id=?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public List<Consulta> listarTodos() {
        List<Consulta> consultas = new ArrayList<>();
        String sql = "SELECT * FROM consulta";
        try (Statement stmt = conexao.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Consulta consulta = new Consulta();
                consulta.setId(rs.getInt("id"));

                Timestamp ts = rs.getTimestamp("data_hora");
                if (ts != null) {
                    consulta.setDataHora(ts.toLocalDateTime());
                }
                consulta.setStatus(rs.getString("status"));


                Cliente cliente = new Cliente();
                cliente.setId(rs.getInt("id_cliente"));
                consulta.setCliente(cliente);

                Animal animal = new Animal();
                animal.setId(rs.getInt("id_animal"));
                consulta.setAnimal(animal);

                Veterinario vet = new Veterinario();
                vet.setId(rs.getInt("id_veterinario"));
                consulta.setVeterinario(vet);

                consultas.add(consulta);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return consultas;
    }


    public Consulta exibir(int id) {
        Consulta consulta = null;
        String sql = "SELECT * FROM consulta WHERE id=?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    consulta = new Consulta();
                    consulta.setId(rs.getInt("id"));

                    Timestamp ts = rs.getTimestamp("data_hora");
                    if (ts != null) {
                        consulta.setDataHora(ts.toLocalDateTime());
                    }
                    consulta.setStatus(rs.getString("status"));

                    Cliente cliente = new Cliente();
                    cliente.setId(rs.getInt("id_cliente"));
                    consulta.setCliente(cliente);

                    Animal animal = new Animal();
                    animal.setId(rs.getInt("id_animal"));
                    consulta.setAnimal(animal);

                    Veterinario vet = new Veterinario();
                    vet.setId(rs.getInt("id_veterinario"));
                    consulta.setVeterinario(vet);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return consulta;
    }
}
