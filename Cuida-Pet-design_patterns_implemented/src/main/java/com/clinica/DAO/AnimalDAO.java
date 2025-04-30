package com.clinica.DAO;

import com.clinica.model.Animal;
import com.clinica.model.Cliente; // Import necessário para buscar cliente

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object para a entidade Animal.
 * Responsável pelas operações CRUD (Create, Read, Update, Delete) relacionadas aos animais.
 * Refatorado para receber a conexão via construtor (para uso com DAOFactory).
 */
public class AnimalDAO {
    private Connection conexao;

    /**
     * Construtor que recebe a conexão com o banco de dados.
     * A conexão é gerenciada externamente (pela DAOFactory, usando o Singleton ConexaoMySQL).
     *
     * @param conexao A conexão SQL ativa.
     */
    public AnimalDAO(Connection conexao) {
        // Remove a obtenção direta da conexão daqui
        // this.conexao = ConexaoMySQL.getConexao(); // Linha antiga removida
        if (conexao == null) {
            throw new IllegalArgumentException("Conexão não pode ser nula ao criar AnimalDAO");
        }
        this.conexao = conexao;
    }

    /**
     * Insere um novo animal no banco de dados.
     *
     * @param animal O objeto Animal a ser inserido.
     * @throws SQLException Se ocorrer um erro durante a inserção SQL.
     */
    public void inserir(Animal animal) throws SQLException {
        String sql = "INSERT INTO animal (nome, especie, raca, data_nascimento, cliente_id) VALUES (?, ?, ?, ?, ?)";
        // Usando try-with-resources para PreparedStatement
        try (PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, animal.getNome());
            stmt.setString(2, animal.getEspecie());
            stmt.setString(3, animal.getRaca());
            // Converte LocalDate para java.sql.Date
            if (animal.getDataNascimento() != null) {
                stmt.setDate(4, Date.valueOf(animal.getDataNascimento()));
            } else {
                stmt.setNull(4, Types.DATE);
            }
            // Verifica se o cliente foi setado no animal
            if (animal.getCliente() != null) {
                 stmt.setInt(5, animal.getCliente().getId());
            } else if (animal.getClienteId() > 0) { // Fallback para ID se o objeto não estiver carregado
                 stmt.setInt(5, animal.getClienteId());
            } else {
                 // Lançar exceção ou tratar como erro, animal precisa de um dono
                 throw new SQLException("Cliente ID não fornecido para o animal.");
            }

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Falha ao inserir animal, nenhuma linha afetada.");
            }

            // Recupera o ID gerado pelo banco
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    animal.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Falha ao inserir animal, nenhum ID obtido.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao inserir animal: " + e.getMessage());
            throw e; // Relança a exceção
        }
    }

    /**
     * Altera os dados de um animal existente no banco de dados.
     *
     * @param animal O objeto Animal com os dados atualizados.
     * @throws SQLException Se ocorrer um erro durante a atualização SQL.
     */
    public void alterar(Animal animal) throws SQLException {
        String sql = "UPDATE animal SET nome=?, especie=?, raca=?, data_nascimento=?, cliente_id=? WHERE id=?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, animal.getNome());
            stmt.setString(2, animal.getEspecie());
            stmt.setString(3, animal.getRaca());
            if (animal.getDataNascimento() != null) {
                stmt.setDate(4, Date.valueOf(animal.getDataNascimento()));
            } else {
                stmt.setNull(4, Types.DATE);
            }
             if (animal.getCliente() != null) {
                 stmt.setInt(5, animal.getCliente().getId());
            } else if (animal.getClienteId() > 0) {
                 stmt.setInt(5, animal.getClienteId());
            } else {
                 throw new SQLException("Cliente ID não fornecido para o animal.");
            }
            stmt.setInt(6, animal.getId());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Falha ao alterar animal, nenhum animal encontrado com o ID: " + animal.getId());
            }
        } catch (SQLException e) {
            System.err.println("Erro ao alterar animal (ID: " + animal.getId() + "): " + e.getMessage());
            throw e;
        }
    }

    /**
     * Remove um animal do banco de dados pelo seu ID.
     *
     * @param id O ID do animal a ser removido.
     * @throws SQLException Se ocorrer um erro durante a remoção SQL.
     */
    public void remover(int id) throws SQLException {
        String sql = "DELETE FROM animal WHERE id=?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int affectedRows = stmt.executeUpdate();
             if (affectedRows == 0) {
                // Pode não ser um erro se a intenção for 'garantir que não existe'
                System.out.println("Nenhum animal encontrado para remover com o ID: " + id);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao remover animal (ID: " + id + "): " + e.getMessage());
            throw e;
        }
    }

    /**
     * Lista todos os animais cadastrados no banco de dados.
     *
     * @return Uma lista de objetos Animal.
     * @throws SQLException Se ocorrer um erro durante a consulta SQL.
     */
    public List<Animal> listarTodos() throws SQLException {
        List<Animal> animais = new ArrayList<>();
        String sql = "SELECT * FROM animal"; // Considerar adicionar JOIN com cliente se necessário frequentemente
        try (Statement stmt = conexao.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                animais.add(mapResultSetToAnimal(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar todos os animais: " + e.getMessage());
            throw e;
        }
        return animais;
    }
    
    /**
     * Lista todos os animais pertencentes a um cliente específico.
     *
     * @param idCliente O ID do cliente.
     * @return Uma lista de objetos Animal do cliente.
     * @throws SQLException Se ocorrer um erro durante a consulta SQL.
     */
    public List<Animal> listarPorCliente(int idCliente) throws SQLException {
        List<Animal> animais = new ArrayList<>();
        String sql = "SELECT * FROM animal WHERE cliente_id = ?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, idCliente);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    animais.add(mapResultSetToAnimal(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar animais por cliente (ID: " + idCliente + "): " + e.getMessage());
            throw e;
        }
        return animais;
    }

    /**
     * Busca um animal pelo seu ID.
     *
     * @param id O ID do animal.
     * @return O objeto Animal encontrado, ou null se não existir.
     * @throws SQLException Se ocorrer um erro durante a consulta SQL.
     */
    public Animal buscarPorId(int id) throws SQLException {
        Animal animal = null;
        String sql = "SELECT * FROM animal WHERE id=?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    animal = mapResultSetToAnimal(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar animal por ID (ID: " + id + "): " + e.getMessage());
            throw e;
        }
        return animal;
    }

    /**
     * Pesquisa animais pelo nome (busca parcial).
     *
     * @param nome O nome ou parte do nome a ser pesquisado.
     * @return Uma lista de animais que correspondem ao critério de busca.
     * @throws SQLException Se ocorrer um erro durante a consulta SQL.
     */
    public List<Animal> pesquisarPorNome(String nome) throws SQLException {
        List<Animal> animais = new ArrayList<>();
        String sql = "SELECT * FROM animal WHERE nome LIKE ?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, "%" + nome + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    animais.add(mapResultSetToAnimal(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao pesquisar animais por nome (" + nome + "): " + e.getMessage());
            throw e;
        }
        return animais;
    }

    /**
     * Método auxiliar para mapear um ResultSet para um objeto Animal.
     *
     * @param rs O ResultSet posicionado na linha correta.
     * @return Um objeto Animal populado com os dados do ResultSet.
     * @throws SQLException Se ocorrer um erro ao acessar os dados do ResultSet.
     */
    private Animal mapResultSetToAnimal(ResultSet rs) throws SQLException {
        Animal animal = new Animal();
        animal.setId(rs.getInt("id"));
        animal.setNome(rs.getString("nome"));
        animal.setEspecie(rs.getString("especie"));
        animal.setRaca(rs.getString("raca"));
        Date sqlDate = rs.getDate("data_nascimento");
        if (sqlDate != null) {
            animal.setDataNascimento(sqlDate.toLocalDate());
        }
        animal.setClienteId(rs.getInt("cliente_id")); // Guarda o ID do cliente
        // Nota: Para carregar o objeto Cliente completo, seria necessário um JOIN na query
        // ou uma chamada separada ao ClienteDAO (lazy loading), o que adicionaria complexidade.
        // Por ora, apenas o ID do cliente é armazenado diretamente no Animal.
        return animal;
    }
}

