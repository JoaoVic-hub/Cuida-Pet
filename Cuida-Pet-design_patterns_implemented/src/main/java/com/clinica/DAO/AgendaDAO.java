package com.clinica.DAO;

import com.clinica.model.Agenda;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object para a entidade Agenda.
 * Responsável pelas operações de banco de dados relacionadas à agenda.
 * Refatorado para receber a conexão via construtor (para uso com DAOFactory).
 */
public class AgendaDAO {
    private Connection conexao;

    /**
     * Construtor que recebe a conexão com o banco de dados.
     * A conexão é gerenciada externamente (pela DAOFactory, usando o Singleton ConexaoMySQL).
     *
     * @param conexao A conexão SQL ativa.
     */
    public AgendaDAO(Connection conexao) {
        // Remove a obtenção direta da conexão daqui
        // this.conexao = ConexaoMySQL.getConexao(); // Linha antiga removida
        if (conexao == null) {
            throw new IllegalArgumentException("Conexão não pode ser nula ao criar AgendaDAO");
        }
        this.conexao = conexao;
    }

    /**
     * Lista todos os registros da view de agenda.
     *
     * @return Uma lista de objetos Agenda.
     * @throws SQLException Se ocorrer um erro durante a consulta SQL.
     */
    public List<Agenda> listarTodos() throws SQLException {
        List<Agenda> agendaList = new ArrayList<>();
        // Usa a view vw_agenda_veterinario que já une as informações necessárias
        String sql = "SELECT * FROM vw_agenda_veterinario"; 
        
        // Usando try-with-resources para garantir o fechamento do Statement e ResultSet
        try (Statement stmt = conexao.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Agenda agenda = new Agenda();
                // Mapeia os resultados do ResultSet para o objeto Agenda
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
            System.err.println("Erro ao listar todos os agendamentos: " + e.getMessage());
            // Relança a exceção para que a camada superior possa tratá-la
            throw e; 
        }
        return agendaList;
    }

    /**
     * Lista os agendamentos para um veterinário específico.
     *
     * @param veterinarioId O ID do veterinário.
     * @return Uma lista de objetos Agenda para o veterinário especificado.
     * @throws SQLException Se ocorrer um erro durante a consulta SQL.
     */
    public List<Agenda> listarPorVeterinario(int veterinarioId) throws SQLException {
        List<Agenda> agendaList = new ArrayList<>();
        String sql = "SELECT * FROM vw_agenda_veterinario WHERE veterinario_id = ?";
        
        // Usando try-with-resources para PreparedStatement
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, veterinarioId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Agenda agenda = new Agenda();
                    // Mapeia os resultados
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
            System.err.println("Erro ao listar agendamentos por veterinário (ID: " + veterinarioId + "): " + e.getMessage());
            // Relança a exceção
            throw e; 
        }
        return agendaList;
    }
    
    // Nota: Outros métodos CRUD (inserir, atualizar, deletar) para Agenda 
    // não estavam presentes no código original fornecido para esta classe específica.
    // Se existissem, também precisariam usar a 'this.conexao'.
}

