package com.clinica.DAO;

import com.clinica.model.Consulta;
import com.clinica.model.Animal;
import com.clinica.model.Cliente;
import com.clinica.model.Veterinario;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object para a entidade Consulta.
 * Responsável pelas operações CRUD relacionadas às consultas.
 * Refatorado para receber a conexão via construtor (para uso com DAOFactory).
 */
public class ConsultaDAO {
    private Connection conexao;

    /**
     * Construtor que recebe a conexão com o banco de dados.
     * A conexão é gerenciada externamente (pela DAOFactory, usando o Singleton ConexaoMySQL).
     *
     * @param conexao A conexão SQL ativa.
     */
    public ConsultaDAO(Connection conexao) {
        // this.conexao = ConexaoMySQL.getConexao(); // Linha antiga removida
        if (conexao == null) {
            throw new IllegalArgumentException("Conexão não pode ser nula ao criar ConsultaDAO");
        }
        this.conexao = conexao;
    }

    /**
     * Insere uma nova consulta no banco de dados.
     *
     * @param consulta O objeto Consulta a ser inserido.
     * @throws SQLException Se ocorrer um erro durante a inserção SQL.
     */
    public void inserir(Consulta consulta) throws SQLException {
        String sql = "INSERT INTO consulta (data_hora, status, id_cliente, id_animal, id_veterinario) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            if (consulta.getDataHora() != null) {
                stmt.setTimestamp(1, Timestamp.valueOf(consulta.getDataHora()));
            } else {
                stmt.setNull(1, Types.TIMESTAMP);
            }
            stmt.setString(2, consulta.getStatus());
            
            // Valida se os objetos relacionados existem e têm IDs
            if (consulta.getCliente() == null || consulta.getCliente().getId() <= 0) {
                 throw new SQLException("Cliente inválido ou sem ID para a consulta.");
            }
            stmt.setInt(3, consulta.getCliente().getId());

            if (consulta.getAnimal() != null && consulta.getAnimal().getId() > 0) {
                stmt.setInt(4, consulta.getAnimal().getId());
            } else {
                // Permitir animal nulo dependendo da regra de negócio
                stmt.setNull(4, java.sql.Types.INTEGER); 
            }
            
            if (consulta.getVeterinario() == null || consulta.getVeterinario().getId() <= 0) {
                 throw new SQLException("Veterinário inválido ou sem ID para a consulta.");
            }
            stmt.setInt(5, consulta.getVeterinario().getId());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Falha ao inserir consulta, nenhuma linha afetada.");
            }

            // Recupera o ID gerado
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    consulta.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Falha ao inserir consulta, nenhum ID obtido.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao inserir consulta: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Altera os dados de uma consulta existente no banco de dados.
     *
     * @param consulta O objeto Consulta com os dados atualizados.
     * @throws SQLException Se ocorrer um erro durante a atualização SQL.
     */
    public void alterar(Consulta consulta) throws SQLException {
        String sql = "UPDATE consulta SET data_hora=?, status=?, id_cliente=?, id_animal=?, id_veterinario=? WHERE id=?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            if (consulta.getDataHora() != null) {
                stmt.setTimestamp(1, Timestamp.valueOf(consulta.getDataHora()));
            } else {
                stmt.setNull(1, Types.TIMESTAMP);
            }
            stmt.setString(2, consulta.getStatus());
            
            if (consulta.getCliente() == null || consulta.getCliente().getId() <= 0) {
                 throw new SQLException("Cliente inválido ou sem ID para a consulta.");
            }
            stmt.setInt(3, consulta.getCliente().getId());

            if (consulta.getAnimal() != null && consulta.getAnimal().getId() > 0) {
                stmt.setInt(4, consulta.getAnimal().getId());
            } else {
                stmt.setNull(4, java.sql.Types.INTEGER);
            }
            
            if (consulta.getVeterinario() == null || consulta.getVeterinario().getId() <= 0) {
                 throw new SQLException("Veterinário inválido ou sem ID para a consulta.");
            }
            stmt.setInt(5, consulta.getVeterinario().getId());
            
            stmt.setInt(6, consulta.getId());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Falha ao alterar consulta, nenhuma consulta encontrada com o ID: " + consulta.getId());
            }
        } catch (SQLException e) {
            System.err.println("Erro ao alterar consulta (ID: " + consulta.getId() + "): " + e.getMessage());
            throw e;
        }
    }

    /**
     * Remove uma consulta do banco de dados pelo seu ID.
     *
     * @param id O ID da consulta a ser removida.
     * @throws SQLException Se ocorrer um erro durante a remoção SQL.
     */
    public void remover(int id) throws SQLException {
        String sql = "DELETE FROM consulta WHERE id=?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                System.out.println("Nenhuma consulta encontrada para remover com o ID: " + id);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao remover consulta (ID: " + id + "): " + e.getMessage());
            throw e;
        }
    }

    /**
     * Lista todas as consultas cadastradas.
     * Nota: Carrega apenas os IDs dos objetos relacionados (Cliente, Animal, Veterinario).
     *
     * @return Uma lista de objetos Consulta.
     * @throws SQLException Se ocorrer um erro durante a consulta SQL.
     */
    public List<Consulta> listarTodos() throws SQLException {
        List<Consulta> consultas = new ArrayList<>();
        // Considerar usar a view vw_agenda_veterinario se ela contiver todas as colunas necessárias
        // ou fazer JOINs explícitos para buscar nomes, etc., se necessário aqui.
        String sql = "SELECT * FROM consulta"; 
        try (Statement stmt = conexao.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                consultas.add(mapResultSetToConsulta(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar todas as consultas: " + e.getMessage());
            throw e;
        }
        return consultas;
    }

    /**
     * Busca uma consulta pelo seu ID.
     * Nota: Carrega apenas os IDs dos objetos relacionados.
     *
     * @param id O ID da consulta.
     * @return O objeto Consulta encontrado, ou null se não existir.
     * @throws SQLException Se ocorrer um erro durante a consulta SQL.
     */
    public Consulta buscarPorId(int id) throws SQLException {
        Consulta consulta = null;
        String sql = "SELECT * FROM consulta WHERE id=?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    consulta = mapResultSetToConsulta(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar consulta por ID (ID: " + id + "): " + e.getMessage());
            throw e;
        }
        return consulta;
    }
    
    /**
     * Método auxiliar para mapear um ResultSet para um objeto Consulta.
     * Cria instâncias vazias para Cliente, Animal, Veterinario e seta apenas os IDs.
     *
     * @param rs O ResultSet posicionado na linha correta.
     * @return Um objeto Consulta populado com os dados do ResultSet.
     * @throws SQLException Se ocorrer um erro ao acessar os dados do ResultSet.
     */
    private Consulta mapResultSetToConsulta(ResultSet rs) throws SQLException {
        Consulta consulta = new Consulta();
        consulta.setId(rs.getInt("id"));

        Timestamp ts = rs.getTimestamp("data_hora");
        if (ts != null) {
            consulta.setDataHora(ts.toLocalDateTime());
        }
        consulta.setStatus(rs.getString("status"));

        // Cria objetos vazios e seta apenas os IDs
        // A camada de serviço/controller seria responsável por carregar os detalhes se necessário
        Cliente cliente = new Cliente();
        cliente.setId(rs.getInt("id_cliente"));
        consulta.setCliente(cliente);

        int animalId = rs.getInt("id_animal");
        if (!rs.wasNull()) { // Verifica se o id_animal não era NULL no banco
            Animal animal = new Animal();
            animal.setId(animalId);
            consulta.setAnimal(animal);
        } else {
            consulta.setAnimal(null); // Garante que o animal seja null se o ID for NULL
        }

        Veterinario vet = new Veterinario();
        vet.setId(rs.getInt("id_veterinario"));
        consulta.setVeterinario(vet);

        return consulta;
    }
}

