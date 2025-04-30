package com.clinica.DAO;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Implementa o padrão Factory para criar instâncias das classes DAO.
 * Centraliza a lógica de criação dos objetos DAO, facilitando a manutenção
 * e permitindo futuras extensões ou substituições de implementações.
 */
public class DAOFactory {

    // Instância Singleton da conexão com o banco de dados
    private static ConexaoMySQL conexaoMySQL = ConexaoMySQL.getInstancia();

    /**
     * Cria e retorna uma instância de AgendaDAO.
     *
     * @return Uma nova instância de AgendaDAO.
     * @throws RuntimeException Se ocorrer um erro ao obter a conexão com o banco de dados.
     */
    public static AgendaDAO createAgendaDAO() {
        try {
            Connection conexao = conexaoMySQL.getConexao();
            return new AgendaDAO(conexao);
        } catch (SQLException e) {
            System.err.println("Erro ao obter conexão para AgendaDAO: " + e.getMessage());
            throw new RuntimeException("Falha ao criar AgendaDAO", e);
        }
    }

    /**
     * Cria e retorna uma instância de AnimalDAO.
     *
     * @return Uma nova instância de AnimalDAO.
     * @throws RuntimeException Se ocorrer um erro ao obter a conexão com o banco de dados.
     */
    public static AnimalDAO createAnimalDAO() {
        try {
            Connection conexao = conexaoMySQL.getConexao();
            return new AnimalDAO(conexao);
        } catch (SQLException e) {
            System.err.println("Erro ao obter conexão para AnimalDAO: " + e.getMessage());
            throw new RuntimeException("Falha ao criar AnimalDAO", e);
        }
    }

    /**
     * Cria e retorna uma instância de ClienteDAO.
     *
     * @return Uma nova instância de ClienteDAO.
     * @throws RuntimeException Se ocorrer um erro ao obter a conexão com o banco de dados.
     */
    public static ClienteDAO createClienteDAO() {
        try {
            Connection conexao = conexaoMySQL.getConexao();
            return new ClienteDAO(conexao);
        } catch (SQLException e) {
            System.err.println("Erro ao obter conexão para ClienteDAO: " + e.getMessage());
            throw new RuntimeException("Falha ao criar ClienteDAO", e);
        }
    }

    /**
     * Cria e retorna uma instância de ConsultaDAO.
     *
     * @return Uma nova instância de ConsultaDAO.
     * @throws RuntimeException Se ocorrer um erro ao obter a conexão com o banco de dados.
     */
    public static ConsultaDAO createConsultaDAO() {
        try {
            Connection conexao = conexaoMySQL.getConexao();
            return new ConsultaDAO(conexao);
        } catch (SQLException e) {
            System.err.println("Erro ao obter conexão para ConsultaDAO: " + e.getMessage());
            throw new RuntimeException("Falha ao criar ConsultaDAO", e);
        }
    }

    /**
     * Cria e retorna uma instância de EmpresaDAO.
     *
     * @return Uma nova instância de EmpresaDAO.
     * @throws RuntimeException Se ocorrer um erro ao obter a conexão com o banco de dados.
     */
    public static EmpresaDAO createEmpresaDAO() {
        try {
            Connection conexao = conexaoMySQL.getConexao();
            return new EmpresaDAO(conexao);
        } catch (SQLException e) {
            System.err.println("Erro ao obter conexão para EmpresaDAO: " + e.getMessage());
            throw new RuntimeException("Falha ao criar EmpresaDAO", e);
        }
    }

    /**
     * Cria e retorna uma instância de ProntuarioDAO.
     *
     * @return Uma nova instância de ProntuarioDAO.
     * @throws RuntimeException Se ocorrer um erro ao obter a conexão com o banco de dados.
     */
    public static ProntuarioDAO createProntuarioDAO() {
        try {
            Connection conexao = conexaoMySQL.getConexao();
            return new ProntuarioDAO(conexao);
        } catch (SQLException e) {
            System.err.println("Erro ao obter conexão para ProntuarioDAO: " + e.getMessage());
            throw new RuntimeException("Falha ao criar ProntuarioDAO", e);
        }
    }

    /**
     * Cria e retorna uma instância de VeterinarioDAO.
     *
     * @return Uma nova instância de VeterinarioDAO.
     * @throws RuntimeException Se ocorrer um erro ao obter a conexão com o banco de dados.
     */
    public static VeterinarioDAO createVeterinarioDAO() {
        try {
            Connection conexao = conexaoMySQL.getConexao();
            return new VeterinarioDAO(conexao);
        } catch (SQLException e) {
            System.err.println("Erro ao obter conexão para VeterinarioDAO: " + e.getMessage());
            throw new RuntimeException("Falha ao criar VeterinarioDAO", e);
        }
    }
    
    // Nota: Para que esta Factory funcione corretamente, as classes DAO 
    // (AgendaDAO, AnimalDAO, etc.) precisam ter um construtor que aceite 
    // um objeto Connection. Será necessário refatorar essas classes.
}

