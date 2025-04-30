package com.clinica.DAO;

import com.clinica.model.Empresa;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object para a entidade Empresa.
 * Responsável pelas operações CRUD e autenticação relacionadas às empresas (clínicas).
 * Refatorado para receber a conexão via construtor (para uso com DAOFactory).
 */
public class EmpresaDAO {
    private Connection conexao;

    /**
     * Construtor que recebe a conexão com o banco de dados.
     * A conexão é gerenciada externamente (pela DAOFactory, usando o Singleton ConexaoMySQL).
     *
     * @param conexao A conexão SQL ativa.
     */
    public EmpresaDAO(Connection conexao) {
        // this.conexao = ConexaoMySQL.getConexao(); // Linha antiga removida
        if (conexao == null) {
            throw new IllegalArgumentException("Conexão não pode ser nula ao criar EmpresaDAO");
        }
        this.conexao = conexao;
    }

    /**
     * Insere uma nova empresa no banco de dados.
     * ATENÇÃO: Armazenar senhas em texto plano não é seguro.
     *
     * @param empresa O objeto Empresa a ser inserido.
     * @throws SQLException Se ocorrer um erro durante a inserção SQL.
     */
    public void inserir(Empresa empresa) throws SQLException {
        String sql = "INSERT INTO empresa (nome, email, senha, cnpj, telefone) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, empresa.getNome());
            stmt.setString(2, empresa.getEmail());
            stmt.setString(3, empresa.getSenha()); // CUIDADO: Senha em texto plano!
            stmt.setString(4, empresa.getCnpj());
            stmt.setString(5, empresa.getTelefone());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Falha ao inserir empresa, nenhuma linha afetada.");
            }
            
            // Recupera o ID gerado
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    empresa.setId(rs.getInt(1));
                } else {
                     throw new SQLException("Falha ao inserir empresa, nenhum ID obtido.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao inserir empresa: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Altera os dados de uma empresa existente no banco de dados.
     * ATENÇÃO: Armazenar senhas em texto plano não é seguro.
     *
     * @param empresa O objeto Empresa com os dados atualizados.
     * @throws SQLException Se ocorrer um erro durante a atualização SQL.
     */
    public void alterar(Empresa empresa) throws SQLException {
        String sql = "UPDATE empresa SET nome=?, email=?, senha=?, cnpj=?, telefone=? WHERE id=?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, empresa.getNome());
            stmt.setString(2, empresa.getEmail());
            stmt.setString(3, empresa.getSenha()); // CUIDADO: Senha em texto plano!
            stmt.setString(4, empresa.getCnpj());
            stmt.setString(5, empresa.getTelefone());
            stmt.setInt(6, empresa.getId());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                 throw new SQLException("Falha ao alterar empresa, nenhuma empresa encontrada com o ID: " + empresa.getId());
            }
        } catch (SQLException e) {
            System.err.println("Erro ao alterar empresa (ID: " + empresa.getId() + "): " + e.getMessage());
            throw e;
        }
    }

    /**
     * Remove uma empresa do banco de dados pelo seu ID.
     * CUIDADO: Considere o impacto em registros relacionados (veterinários, etc.).
     *
     * @param id O ID da empresa a ser removida.
     * @throws SQLException Se ocorrer um erro durante a remoção SQL.
     */
    public void remover(int id) throws SQLException {
        String sql = "DELETE FROM empresa WHERE id=?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int affectedRows = stmt.executeUpdate();
             if (affectedRows == 0) {
                System.out.println("Nenhuma empresa encontrada para remover com o ID: " + id);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao remover empresa (ID: " + id + "): " + e.getMessage());
            throw e;
        }
    }

    /**
     * Lista todas as empresas cadastradas.
     *
     * @return Uma lista de objetos Empresa.
     * @throws SQLException Se ocorrer um erro durante a consulta SQL.
     */
    public List<Empresa> listarTodos() throws SQLException {
        List<Empresa> empresas = new ArrayList<>();
        String sql = "SELECT * FROM empresa";
        try (Statement stmt = conexao.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                empresas.add(mapResultSetToEmpresa(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar todas as empresas: " + e.getMessage());
            throw e;
        }
        return empresas;
    }

    /**
     * Busca uma empresa pelo seu ID.
     *
     * @param id O ID da empresa.
     * @return O objeto Empresa encontrado, ou null se não existir.
     * @throws SQLException Se ocorrer um erro durante a consulta SQL.
     */
    public Empresa buscarPorId(int id) throws SQLException {
        Empresa empresa = null;
        String sql = "SELECT * FROM empresa WHERE id=?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    empresa = mapResultSetToEmpresa(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar empresa por ID (ID: " + id + "): " + e.getMessage());
            throw e;
        }
        return empresa;
    }

    /**
     * Autentica uma empresa pelo email e senha.
     * ATENÇÃO: Comparar senhas em texto plano não é seguro.
     *
     * @param email O email da empresa.
     * @param senha A senha (em texto plano) da empresa.
     * @return O objeto Empresa se a autenticação for bem-sucedida, ou null caso contrário.
     * @throws SQLException Se ocorrer um erro durante a consulta SQL.
     */
    public Empresa autenticar(String email, String senha) throws SQLException {
        Empresa empresa = null;
        String sql = "SELECT * FROM empresa WHERE email=? AND senha=?"; // CUIDADO: Comparação de senha em texto plano!
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.setString(2, senha);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    empresa = mapResultSetToEmpresa(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao autenticar empresa (email: " + email + "): " + e.getMessage());
            throw e;
        }
        return empresa;
    }
    
    /**
     * Método auxiliar para mapear um ResultSet para um objeto Empresa.
     *
     * @param rs O ResultSet posicionado na linha correta.
     * @return Um objeto Empresa populado com os dados do ResultSet.
     * @throws SQLException Se ocorrer um erro ao acessar os dados do ResultSet.
     */
    private Empresa mapResultSetToEmpresa(ResultSet rs) throws SQLException {
         Empresa empresa = new Empresa(
                rs.getString("nome"),
                rs.getString("email"),
                rs.getString("senha"), // CUIDADO: Expondo senha
                rs.getString("cnpj"),
                rs.getString("telefone")
            );
        empresa.setId(rs.getInt("id"));
        return empresa;
    }
}

