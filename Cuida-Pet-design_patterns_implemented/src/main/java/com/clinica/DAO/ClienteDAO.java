package com.clinica.DAO;

import com.clinica.model.Cliente;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object para a entidade Cliente.
 * Responsável pelas operações CRUD e autenticação relacionadas aos clientes.
 * Refatorado para receber a conexão via construtor (para uso com DAOFactory).
 */
public class ClienteDAO {
    private Connection conexao;

    /**
     * Construtor que recebe a conexão com o banco de dados.
     * A conexão é gerenciada externamente (pela DAOFactory, usando o Singleton ConexaoMySQL).
     *
     * @param conexao A conexão SQL ativa.
     */
    public ClienteDAO(Connection conexao) {
        // this.conexao = ConexaoMySQL.getConexao(); // Linha antiga removida
        if (conexao == null) {
            throw new IllegalArgumentException("Conexão não pode ser nula ao criar ClienteDAO");
        }
        this.conexao = conexao;
    }

    /**
     * Insere um novo cliente no banco de dados.
     * ATENÇÃO: Armazenar senhas em texto plano não é seguro. Considere usar hashing.
     *
     * @param cliente O objeto Cliente a ser inserido.
     * @throws SQLException Se ocorrer um erro durante a inserção SQL.
     */
    public void inserir(Cliente cliente) throws SQLException {
        // Adiciona os novos campos ao SQL INSERT
        String sql = "INSERT INTO cliente (nome, endereco, email, telefone, cpf, senha, cep, numero, complemento) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, cliente.getNome());
            stmt.setString(2, cliente.getEndereco());
            stmt.setString(3, cliente.getEmail());
            stmt.setString(4, cliente.getTelefone());
            stmt.setString(5, cliente.getCpf());
            stmt.setString(6, cliente.getSenha()); // CUIDADO: Senha em texto plano!
            stmt.setString(7, cliente.getCep());
            stmt.setString(8, cliente.getNumero());
            stmt.setString(9, cliente.getComplemento());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Falha ao inserir cliente, nenhuma linha afetada.");
            }

            // Recupera o ID gerado
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    cliente.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Falha ao inserir cliente, nenhum ID obtido.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao inserir cliente: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Altera os dados de um cliente existente no banco de dados.
     * ATENÇÃO: Armazenar senhas em texto plano não é seguro.
     *
     * @param cliente O objeto Cliente com os dados atualizados.
     * @throws SQLException Se ocorrer um erro durante a atualização SQL.
     */
    public void alterar(Cliente cliente) throws SQLException {
        // Adiciona os novos campos ao SQL UPDATE
        String sql = "UPDATE cliente "
                   + "SET nome=?, endereco=?, email=?, telefone=?, cpf=?, senha=?, cep=?, numero=?, complemento=? "
                   + "WHERE id=?";

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, cliente.getNome());
            stmt.setString(2, cliente.getEndereco());
            stmt.setString(3, cliente.getEmail());
            stmt.setString(4, cliente.getTelefone());
            stmt.setString(5, cliente.getCpf());
            stmt.setString(6, cliente.getSenha()); // CUIDADO: Senha em texto plano!
            stmt.setString(7, cliente.getCep());
            stmt.setString(8, cliente.getNumero());
            stmt.setString(9, cliente.getComplemento());
            stmt.setInt(10, cliente.getId());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Falha ao alterar cliente, nenhum cliente encontrado com o ID: " + cliente.getId());
            }
        } catch (SQLException e) {
            System.err.println("Erro ao alterar cliente (ID: " + cliente.getId() + "): " + e.getMessage());
            throw e;
        }
    }

    /**
     * Pesquisa clientes pelo nome (busca parcial).
     *
     * @param nome O nome ou parte do nome a ser pesquisado.
     * @return Uma lista de clientes que correspondem ao critério de busca.
     * @throws SQLException Se ocorrer um erro durante a consulta SQL.
     */
    public List<Cliente> pesquisarPorNome(String nome) throws SQLException {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT * FROM cliente WHERE nome LIKE ?";

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, "%" + nome + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    clientes.add(mapResultSetToCliente(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao pesquisar clientes por nome (" + nome + "): " + e.getMessage());
            throw e;
        }
        return clientes;
    }

    /**
     * Remove um cliente do banco de dados pelo seu ID.
     * CUIDADO: Considere o impacto em registros relacionados (animais, consultas) - pode ser necessário configurar ON DELETE CASCADE no banco ou tratar na aplicação.
     *
     * @param id O ID do cliente a ser removido.
     * @throws SQLException Se ocorrer um erro durante la remoção SQL.
     */
    public void remover(int id) throws SQLException {
        // Antes de remover o cliente, pode ser necessário remover ou desassociar animais e consultas
        // Ex: animalDAO.removerPorCliente(id); consultaDAO.removerPorCliente(id);
        String sql = "DELETE FROM cliente WHERE id=?";

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                System.out.println("Nenhum cliente encontrado para remover com o ID: " + id);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao remover cliente (ID: " + id + "): " + e.getMessage());
            // Verificar se o erro é de chave estrangeira e dar uma mensagem mais clara
            throw e;
        }
    }

    /**
     * Lista todos os clientes cadastrados.
     *
     * @return Uma lista de objetos Cliente.
     * @throws SQLException Se ocorrer um erro durante a consulta SQL.
     */
    public List<Cliente> listarTodos() throws SQLException {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT * FROM cliente";

        try (Statement stmt = conexao.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                clientes.add(mapResultSetToCliente(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar todos os clientes: " + e.getMessage());
            throw e;
        }
        return clientes;
    }

    /**
     * Busca um cliente pelo seu ID.
     *
     * @param id O ID do cliente.
     * @return O objeto Cliente encontrado, ou null se não existir.
     * @throws SQLException Se ocorrer um erro durante a consulta SQL.
     */
    public Cliente buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM cliente WHERE id=?";
        Cliente cliente = null;

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    cliente = mapResultSetToCliente(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar cliente por ID (ID: " + id + "): " + e.getMessage());
            throw e;
        }
        return cliente;
    }

    /**
     * Autentica um cliente pelo email e senha.
     * ATENÇÃO: Comparar senhas em texto plano não é seguro.
     *
     * @param email O email do cliente.
     * @param senha A senha (em texto plano) do cliente.
     * @return O objeto Cliente se a autenticação for bem-sucedida, ou null caso contrário.
     * @throws SQLException Se ocorrer um erro durante a consulta SQL.
     */
    public Cliente autenticar(String email, String senha) throws SQLException {
        String sql = "SELECT * FROM cliente WHERE email=? AND senha=?"; // CUIDADO: Comparação de senha em texto plano!
        Cliente cliente = null;

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.setString(2, senha);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    cliente = mapResultSetToCliente(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao autenticar cliente (email: " + email + "): " + e.getMessage());
            throw e;
        }
        return cliente;
    }

    /**
     * Método auxiliar para mapear um ResultSet para um objeto Cliente.
     *
     * @param rs O ResultSet posicionado na linha correta.
     * @return Um objeto Cliente populado com os dados do ResultSet.
     * @throws SQLException Se ocorrer um erro ao acessar os dados do ResultSet.
     */
    private Cliente mapResultSetToCliente(ResultSet rs) throws SQLException {
        Cliente cliente = new Cliente();
        cliente.setId(rs.getInt("id"));
        cliente.setNome(rs.getString("nome"));
        cliente.setEndereco(rs.getString("endereco"));
        cliente.setEmail(rs.getString("email"));
        cliente.setTelefone(rs.getString("telefone"));
        cliente.setCpf(rs.getString("cpf"));
        cliente.setSenha(rs.getString("senha")); // CUIDADO: Expondo senha
        cliente.setCep(rs.getString("cep"));
        cliente.setNumero(rs.getString("numero"));
        cliente.setComplemento(rs.getString("complemento"));
        // Nota: A lista de animais não é carregada aqui (lazy loading seria uma opção)
        return cliente;
    }
}

