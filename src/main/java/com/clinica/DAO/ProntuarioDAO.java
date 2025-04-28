package com.clinica.DAO;

import com.clinica.model.Prontuario;
import com.clinica.model.Consulta;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ProntuarioDAO {
    private Connection conexao;

    public ProntuarioDAO() {
        this.conexao = ConexaoMySQL.getConexao();
    }

    public void inserir(Prontuario prontuario) {
        String sql = "INSERT INTO prontuario (id_consulta, observacoes, diagnostico, data_registro) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
   
            stmt.setInt(1, prontuario.getConsulta().getId());
            stmt.setString(2, prontuario.getObservacoes());
            stmt.setString(3, prontuario.getDiagnostico());
            stmt.setTimestamp(4, Timestamp.valueOf(prontuario.getDataRegistro()));
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    prontuario.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void alterar(Prontuario prontuario) {
        String sql = "UPDATE prontuario SET observacoes=?, diagnostico=?, data_registro=? WHERE id=?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, prontuario.getObservacoes());
            stmt.setString(2, prontuario.getDiagnostico());
            stmt.setTimestamp(3, Timestamp.valueOf(prontuario.getDataRegistro()));
            stmt.setInt(4, prontuario.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void remover(int id) {
        String sql = "DELETE FROM prontuario WHERE id=?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

 
    public List<Prontuario> listarTodos() {
        List<Prontuario> prontuarios = new ArrayList<>();
        String sql = "SELECT * FROM prontuario";
        try (Statement stmt = conexao.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Prontuario prontuario = new Prontuario();
                prontuario.setId(rs.getInt("id"));
              
                Consulta consulta = new Consulta();
                consulta.setId(rs.getInt("id_consulta"));
                prontuario.setConsulta(consulta);

                Timestamp ts = rs.getTimestamp("data_registro");
                if (ts != null) {
                    prontuario.setDataRegistro(ts.toLocalDateTime());
                }
                prontuario.setObservacoes(rs.getString("observacoes"));
                prontuario.setDiagnostico(rs.getString("diagnostico"));
                prontuarios.add(prontuario);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return prontuarios;
    }

  
    public Prontuario exibir(int id) {
        Prontuario prontuario = null;
        String sql = "SELECT * FROM prontuario WHERE id=?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    prontuario = new Prontuario();
                    prontuario.setId(rs.getInt("id"));
                    Consulta consulta = new Consulta();
                    consulta.setId(rs.getInt("id_consulta"));
                    prontuario.setConsulta(consulta);
                    
                    Timestamp ts = rs.getTimestamp("data_registro");
                    if (ts != null) {
                        prontuario.setDataRegistro(ts.toLocalDateTime());
                    }
                    prontuario.setObservacoes(rs.getString("observacoes"));
                    prontuario.setDiagnostico(rs.getString("diagnostico"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return prontuario;
    }
}
