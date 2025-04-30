package com.clinica.DAO;

import com.clinica.model.Veterinario;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object para a entidade Veterinario.
 * Responsável pelas operações CRUD e autenticação relacionadas aos veterinários.
 * Refatorado para receber a conexão via construtor (para uso com DAOFactory).
 */
public class VeterinarioDAO {
    private Connection conexao;

    /**
     * Construtor que recebe a conexão com o banco de dados.
     * A conexão é gerenciada externamente (pela DAOFactory, usando o Singleton ConexaoMySQL).
     *
     * @param conexao A conexão SQL ativa.
     */
    public VeterinarioDAO(Connection conexao) {
        // this.conexao = ConexaoMySQL.getConexao(); // Linha antiga removida
        if (conexao == null) {
            throw new IllegalArgumentException("Conexão não pode ser nula ao criar VeterinarioDAO");
        }
        this.conexao = conexao;
    }

    /**
     * Insere um novo veterinário no banco de dados.
     * ATENÇÃO: Armazenar senhas em texto plano não é seguro.
     *
     * @param vet O objeto Veterinario a ser inserido.
     * @throws SQLException Se ocorrer um erro durante a inserção SQL.
     */
    public void inserir(Veterinario vet) throws SQLException {
        String sql = "INSERT INTO veterinario (nome, email, telefone, cpf, senha, crmv, especialidade) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, vet.getNome());
            stmt.setString(2, vet.getEmail());
            stmt.setString(3, vet.getTelefone());
            stmt.setString(4, vet.getCpf());
            stmt.setString(5, vet.getSenha()); // CUIDADO: Senha em texto plano!
            stmt.setString(6, vet.getCrmv());
            stmt.setString(7, vet.getEspecialidade());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Falha ao inserir veterinário, nenhuma linha afetada.");
            }

            // Recupera o ID gerado
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    vet.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Falha ao inserir veterinário, nenhum ID obtido.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao inserir veterinário: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Altera os dados de um veterinário existente no banco de dados.
     * ATENÇÃO: Armazenar senhas em texto plano não é seguro.
     *
     * @param vet O objeto Veterinario com os dados atualizados.
     * @throws SQLException Se ocorrer um erro durante a atualização SQL.
     */
    public void alterar(Veterinario vet) throws SQLException {
        String sql = "UPDATE veterinario SET nome=?, email=?, telefone=?, cpf=?, senha=?, crmv=?, especialidade=? " +
                     "WHERE id=?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, vet.getNome());
            stmt.setString(2, vet.getEmail());
            stmt.setString(3, vet.getTelefone());
            stmt.setString(4, vet.getCpf());
            stmt.setString(5, vet.getSenha()); // CUIDADO: Senha em texto plano!
            stmt.setString(6, vet.getCrmv());
            stmt.setString(7, vet.getEspecialidade());
            stmt.setInt(8, vet.getId());

            int affectedRows = stmt.executeUpdate();
             if (affectedRows == 0) {
                 throw new SQLException("Falha ao alterar veterinário, nenhum veterinário encontrado com o ID: " + vet.getId());
            }
        } catch (SQLException e) {
            System.err.println("Erro ao alterar veterinário (ID: " + vet.getId() + "): " + e.getMessage());
            throw e;
        }
    }

    /**
     * Remove um veterinário do banco de dados pelo seu ID.
     * CUIDADO: Considere o impacto em registros relacionados (consultas, agenda).
     *
     * @param id O ID do veterinário a ser removido.
     * @throws SQLException Se ocorrer um erro durante a remoção SQL.
     */
    public void remover(int id) throws SQLException {
        String sql = "DELETE FROM veterinario WHERE id=?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                System.out.println("Nenhum veterinário encontrado para remover com o ID: " + id);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao remover veterinário (ID: " + id + "): " + e.getMessage());
            throw e;
        }
    }

    /**
     * Lista todos os veterinários cadastrados.
     *
     * @return Uma lista de objetos Veterinario.
     * @throws SQLException Se ocorrer um erro durante a consulta SQL.
     */
    public List<Veterinario> listarTodos() throws SQLException {
        List<Veterinario> vets = new ArrayList<>();
        String sql = "SELECT * FROM veterinario";
        try (Statement stmt = conexao.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                vets.add(mapResultSetToVeterinario(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar todos os veterinários: " + e.getMessage());
            throw e;
        }
        return vets;
    }

    /**
     * Busca um veterinário pelo seu ID.
     *
     * @param id O ID do veterinário.
     * @return O objeto Veterinario encontrado, ou null se não existir.
     * @throws SQLException Se ocorrer um erro durante a consulta SQL.
     */
    public Veterinario buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM veterinario WHERE id=?";
        Veterinario vet = null;
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    vet = mapResultSetToVeterinario(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar veterinário por ID (ID: " + id + "): " + e.getMessage());
            throw e;
        }
        return vet;
    }

    /**
     * Autentica um veterinário pelo email e senha.
     * ATENÇÃO: Comparar senhas em texto plano não é seguro.
     *
     * @param email O email do veterinário.
     * @param senha A senha (em texto plano) do veterinário.
     * @return O objeto Veterinario se a autenticação for bem-sucedida, ou null caso contrário.
     * @throws SQLException Se ocorrer um erro durante a consulta SQL.
     */
    public Veterinario autenticar(String email, String senha) throws SQLException {
        String sql = "SELECT * FROM veterinario WHERE email=? AND senha=?"; // CUIDADO: Comparação de senha em texto plano!
        Veterinario vet = null;
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.setString(2, senha);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    vet = mapResultSetToVeterinario(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao autenticar veterinário (email: " + email + "): " + e.getMessage());
            throw e;
        }
        return vet;
    }
    
    /**
     * Método auxiliar para mapear um ResultSet para um objeto Veterinario.
     *
     * @param rs O ResultSet posicionado na linha correta.
     * @return Um objeto Veterinario populado com os dados do ResultSet.
     * @throws SQLException Se ocorrer um erro ao acessar os dados do ResultSet.
     */
    private Veterinario mapResultSetToVeterinario(ResultSet rs) throws SQLException {
         Veterinario vet = new Veterinario(
                rs.getString("nome"),
                rs.getString("email"),
                rs.getString("telefone"),
                rs.getString("cpf"),
                rs.getString("senha"), // CUIDADO: Expondo senha
                rs.getString("crmv"),
                rs.getString("especialidade")
            );
        vet.setId(rs.getInt("id"));
        return vet;
    }
}

