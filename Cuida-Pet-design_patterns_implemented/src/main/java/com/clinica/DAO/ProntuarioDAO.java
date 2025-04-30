package com.clinica.DAO;

import com.clinica.model.Prontuario;
import com.clinica.model.Consulta;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object para a entidade Prontuario.
 * Responsável pelas operações CRUD relacionadas aos prontuários médicos.
 * Refatorado para receber a conexão via construtor (para uso com DAOFactory).
 */
public class ProntuarioDAO {
    private Connection conexao;

    /**
     * Construtor que recebe a conexão com o banco de dados.
     * A conexão é gerenciada externamente (pela DAOFactory, usando o Singleton ConexaoMySQL).
     *
     * @param conexao A conexão SQL ativa.
     */
    public ProntuarioDAO(Connection conexao) {
        // this.conexao = ConexaoMySQL.getConexao(); // Linha antiga removida
        if (conexao == null) {
            throw new IllegalArgumentException("Conexão não pode ser nula ao criar ProntuarioDAO");
        }
        this.conexao = conexao;
    }

    /**
     * Insere um novo prontuário no banco de dados.
     *
     * @param prontuario O objeto Prontuario a ser inserido.
     * @throws SQLException Se ocorrer um erro durante a inserção SQL.
     */
    public void inserir(Prontuario prontuario) throws SQLException {
        String sql = "INSERT INTO prontuario (id_consulta, observacoes, diagnostico, data_registro) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
   
            if (prontuario.getConsulta() == null || prontuario.getConsulta().getId() <= 0) {
                throw new SQLException("Consulta inválida ou sem ID para o prontuário.");
            }
            stmt.setInt(1, prontuario.getConsulta().getId());
            stmt.setString(2, prontuario.getObservacoes());
            stmt.setString(3, prontuario.getDiagnostico());
            
            // Usa a data/hora atual se não for fornecida
            LocalDateTime dataRegistro = prontuario.getDataRegistro() != null ? prontuario.getDataRegistro() : LocalDateTime.now();
            stmt.setTimestamp(4, Timestamp.valueOf(dataRegistro));
            prontuario.setDataRegistro(dataRegistro); // Atualiza o objeto caso tenha sido setado agora
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Falha ao inserir prontuário, nenhuma linha afetada.");
            }

            // Recupera o ID gerado
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    prontuario.setId(rs.getInt(1));
                } else {
                    throw new SQLException("Falha ao inserir prontuário, nenhum ID obtido.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao inserir prontuário: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Altera os dados de um prontuário existente no banco de dados.
     *
     * @param prontuario O objeto Prontuario com os dados atualizados.
     * @throws SQLException Se ocorrer um erro durante a atualização SQL.
     */
    public void alterar(Prontuario prontuario) throws SQLException {
        // Geralmente não se altera o id_consulta de um prontuário existente, mas mantendo como no original
        // String sql = "UPDATE prontuario SET id_consulta=?, observacoes=?, diagnostico=?, data_registro=? WHERE id=?";
        String sql = "UPDATE prontuario SET observacoes=?, diagnostico=?, data_registro=? WHERE id=?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            // stmt.setInt(1, prontuario.getConsulta().getId()); // Removido - não deve alterar a consulta associada
            stmt.setString(1, prontuario.getObservacoes());
            stmt.setString(2, prontuario.getDiagnostico());
            
            LocalDateTime dataRegistro = prontuario.getDataRegistro() != null ? prontuario.getDataRegistro() : LocalDateTime.now();
            stmt.setTimestamp(3, Timestamp.valueOf(dataRegistro));
            prontuario.setDataRegistro(dataRegistro); // Atualiza o objeto
            
            stmt.setInt(4, prontuario.getId());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Falha ao alterar prontuário, nenhum prontuário encontrado com o ID: " + prontuario.getId());
            }
        } catch (SQLException e) {
            System.err.println("Erro ao alterar prontuário (ID: " + prontuario.getId() + "): " + e.getMessage());
            throw e;
        }
    }

    /**
     * Remove um prontuário do banco de dados pelo seu ID.
     *
     * @param id O ID do prontuário a ser removido.
     * @throws SQLException Se ocorrer um erro durante a remoção SQL.
     */
    public void remover(int id) throws SQLException {
        String sql = "DELETE FROM prontuario WHERE id=?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int affectedRows = stmt.executeUpdate();
             if (affectedRows == 0) {
                System.out.println("Nenhum prontuário encontrado para remover com o ID: " + id);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao remover prontuário (ID: " + id + "): " + e.getMessage());
            throw e;
        }
    }

    /**
     * Lista todos os prontuários cadastrados.
     * Nota: Carrega apenas o ID da Consulta relacionada.
     *
     * @return Uma lista de objetos Prontuario.
     * @throws SQLException Se ocorrer um erro durante a consulta SQL.
     */
    public List<Prontuario> listarTodos() throws SQLException {
        List<Prontuario> prontuarios = new ArrayList<>();
        String sql = "SELECT * FROM prontuario";
        try (Statement stmt = conexao.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                prontuarios.add(mapResultSetToProntuario(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar todos os prontuários: " + e.getMessage());
            throw e;
        }
        return prontuarios;
    }

    /**
     * Busca um prontuário pelo seu ID.
     * Nota: Carrega apenas o ID da Consulta relacionada.
     *
     * @param id O ID do prontuário.
     * @return O objeto Prontuario encontrado, ou null se não existir.
     * @throws SQLException Se ocorrer um erro durante a consulta SQL.
     */
    public Prontuario buscarPorId(int id) throws SQLException {
        Prontuario prontuario = null;
        String sql = "SELECT * FROM prontuario WHERE id=?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    prontuario = mapResultSetToProntuario(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar prontuário por ID (ID: " + id + "): " + e.getMessage());
            throw e;
        }
        return prontuario;
    }
    
    /**
     * Busca todos os prontuários associados a uma consulta específica.
     *
     * @param idConsulta O ID da consulta.
     * @return Uma lista de Prontuarios para a consulta especificada.
     * @throws SQLException Se ocorrer um erro durante a consulta SQL.
     */
    public List<Prontuario> listarPorConsulta(int idConsulta) throws SQLException {
        List<Prontuario> prontuarios = new ArrayList<>();
        String sql = "SELECT * FROM prontuario WHERE id_consulta = ? ORDER BY data_registro DESC"; // Ordena do mais recente para o mais antigo
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, idConsulta);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    prontuarios.add(mapResultSetToProntuario(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar prontuários por consulta (ID Consulta: " + idConsulta + "): " + e.getMessage());
            throw e;
        }
        return prontuarios;
    }

    /**
     * Método auxiliar para mapear um ResultSet para um objeto Prontuario.
     * Cria uma instância vazia para Consulta e seta apenas o ID.
     *
     * @param rs O ResultSet posicionado na linha correta.
     * @return Um objeto Prontuario populado com os dados do ResultSet.
     * @throws SQLException Se ocorrer um erro ao acessar os dados do ResultSet.
     */
    private Prontuario mapResultSetToProntuario(ResultSet rs) throws SQLException {
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
        return prontuario;
    }
}

