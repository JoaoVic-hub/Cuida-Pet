package com.clinica.DAO;

import com.clinica.model.Cliente;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {
    private Connection conexao;

    // Construtor obtendo a conexão
    public ClienteDAO() {
        this.conexao = ConexaoMySQL.getConexao();
    }

    // =========== MÉTODO INSERIR ===============
    public void inserir(Cliente cliente) {
        // Observando a ordem das colunas na tabela: (nome, endereco, email, telefone, cpf, senha)
        String sql = "INSERT INTO cliente (nome, endereco, email, telefone, cpf, senha) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            // Ajuste a ordem dos parâmetros conforme as colunas (nome, endereco, email, telefone, cpf, senha)
            stmt.setString(1, cliente.getNome());         // 1 => nome
            stmt.setString(2, cliente.getEndereco());     // 2 => endereco
            stmt.setString(3, cliente.getEmail());        // 3 => email
            stmt.setString(4, cliente.getTelefone());     // 4 => telefone
            stmt.setString(5, cliente.getCpf());          // 5 => cpf
            stmt.setString(6, cliente.getSenha());        // 6 => senha

            stmt.executeUpdate();

            // Recupera o ID auto-increment gerado
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    cliente.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // =========== MÉTODO ALTERAR ===============
    public void alterar(Cliente cliente) {
        String sql = "UPDATE cliente "
                   + "SET nome=?, endereco=?, email=?, telefone=?, cpf=?, senha=? "
                   + "WHERE id=?";

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            // Mesma sequência das colunas
            stmt.setString(1, cliente.getNome());         // nome
            stmt.setString(2, cliente.getEndereco());     // endereco
            stmt.setString(3, cliente.getEmail());        // email
            stmt.setString(4, cliente.getTelefone());     // telefone
            stmt.setString(5, cliente.getCpf());          // cpf
            stmt.setString(6, cliente.getSenha());        // senha

            stmt.setInt(7, cliente.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // =========== MÉTODO PESQUISAR POR NOME ===============
    public List<Cliente> pesquisarPorNome(String nome) {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT * FROM cliente WHERE nome LIKE ?";

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, "%" + nome + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    // Construtor: (nome, endereco, email, telefone, cpf, senha)
                    // Observando as colunas do BD
                    Cliente cliente = new Cliente(
                        rs.getString("nome"),         // 1 => nome
                        rs.getString("endereco"),     // 2 => endereco
                        rs.getString("email"),        // 3 => email
                        rs.getString("telefone"),     // 4 => telefone
                        rs.getString("cpf"),          // 5 => cpf
                        rs.getString("senha")         // 6 => senha
                    );
                    cliente.setId(rs.getInt("id"));
                    clientes.add(cliente);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return clientes;
    }

    // =========== MÉTODO REMOVER ===============
    public void remover(int id) {
        String sql = "DELETE FROM cliente WHERE id=?";

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // =========== MÉTODO LISTAR TODOS ===============
    public List<Cliente> listarTodos() {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT * FROM cliente";

        try (Statement stmt = conexao.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                // Mesmo construtor
                Cliente cliente = new Cliente(
                    rs.getString("nome"),       // nome
                    rs.getString("endereco"),   // endereco
                    rs.getString("email"),      // email
                    rs.getString("telefone"),   // telefone
                    rs.getString("cpf"),        // cpf
                    rs.getString("senha")       // senha
                );
                cliente.setId(rs.getInt("id"));
                clientes.add(cliente);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clientes;
    }

    // =========== MÉTODO EXIBIR POR ID ===============
    public Cliente exibir(int id) {
        String sql = "SELECT * FROM cliente WHERE id=?";
        Cliente cliente = null;

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    cliente = new Cliente(
                        rs.getString("nome"),      // nome
                        rs.getString("endereco"),  // endereco
                        rs.getString("email"),     // email
                        rs.getString("telefone"),  // telefone
                        rs.getString("cpf"),       // cpf
                        rs.getString("senha")      // senha
                    );
                    cliente.setId(rs.getInt("id"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return cliente;
    }

    // =========== (OPCIONAL) MÉTODO AUTENTICAR ===============
    // Se quiser um login usando email+senha
    public Cliente autenticar(String email, String senha) {
        String sql = "SELECT * FROM cliente WHERE email=? AND senha=?";
        Cliente cliente = null;

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.setString(2, senha);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    cliente = new Cliente(
                        rs.getString("nome"),
                        rs.getString("endereco"),
                        rs.getString("email"),
                        rs.getString("telefone"),
                        rs.getString("cpf"),
                        rs.getString("senha")
                    );
                    cliente.setId(rs.getInt("id"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return cliente; // null se não encontrou
    }
}
