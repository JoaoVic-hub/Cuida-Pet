package com.clinica.DAO;

import com.clinica.model.Agenda;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AgendaDAO {
    private Connection conexao;

    public AgendaDAO() {
        this.conexao = ConexaoMySQL.getConexao();
    }

  
    public List<Agenda> listarTodos() {
        List<Agenda> agendaList = new ArrayList<>();
        String sql = "SELECT * FROM vw_agenda_veterinario";
        try (Statement stmt = conexao.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Agenda agenda = new Agenda();

                agenda.setConsultaId(rs.getInt("consulta_id"));
                Timestamp ts = rs.getTimestamp("data_hora");
                if (ts != null) {
                    agenda.setDataHora(ts.toLocalDateTime());
                }
                agenda.setStatus(rs.getString("status"));
                agenda.setVeterinarioId(rs.getInt("veterinario_id"));
                agenda.setNomeVeterinario(rs.getString("nome_veterinario"));
                agenda.setAnimalId(rs.getInt("animal_id"));
                agenda.setNomeAnimal(rs.getString("nome_animal"));
                agenda.setClienteId(rs.getInt("cliente_id"));
                agenda.setNomeCliente(rs.getString("nome_cliente"));
                agenda.setEnderecoCliente(rs.getString("endereco_cliente"));

                agendaList.add(agenda);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return agendaList;
    }

    
    public List<Agenda> listarPorVeterinario(int veterinarioId) {
        List<Agenda> agendaList = new ArrayList<>();
        String sql = "SELECT * FROM vw_agenda_veterinario WHERE veterinario_id = ?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, veterinarioId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Agenda agenda = new Agenda();

                    agenda.setConsultaId(rs.getInt("consulta_id"));
                    Timestamp ts = rs.getTimestamp("data_hora");
                    if (ts != null) {
                        agenda.setDataHora(ts.toLocalDateTime());
                    }
                    agenda.setStatus(rs.getString("status"));
                    agenda.setVeterinarioId(rs.getInt("veterinario_id"));
                    agenda.setNomeVeterinario(rs.getString("nome_veterinario"));
                    agenda.setAnimalId(rs.getInt("animal_id"));
                    agenda.setNomeAnimal(rs.getString("nome_animal"));
                    agenda.setClienteId(rs.getInt("cliente_id"));
                    agenda.setNomeCliente(rs.getString("nome_cliente"));
                    agenda.setEnderecoCliente(rs.getString("endereco_cliente"));

                    agendaList.add(agenda);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return agendaList;
    }
}
