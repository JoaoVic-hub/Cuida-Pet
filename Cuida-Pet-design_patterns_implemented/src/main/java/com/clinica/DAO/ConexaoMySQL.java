package com.clinica.DAO;



import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Implementa o padrão Singleton para gerenciar a conexão com o banco de dados MySQL.
 * Garante que exista apenas uma instância da conexão durante a execução da aplicação,
 * otimizando o uso de recursos e centralizando o controle da conexão.
 */
public class ConexaoMySQL {
    // Constantes para os detalhes da conexão (idealmente, deveriam vir de um arquivo de configuração)
    private static final String URL = "jdbc:mysql://localhost:3306/cuida_pet";
    private static final String USUARIO = "root";
    private static final String SENHA = "iluminadosql23$$"; // ATENÇÃO: Armazenar senhas em código não é seguro!

    // A única instância da classe ConexaoMySQL (Singleton)
    private static ConexaoMySQL instancia;

    // A conexão ativa com o banco de dados
    private Connection conexao;

    /**
     * Construtor privado para impedir a instanciação direta da classe.
     * Estabelece a conexão com o banco de dados quando a instância é criada.
     */
    private ConexaoMySQL() {
        try {
            // Carrega o driver JDBC (necessário em algumas configurações mais antigas)
            // Class.forName("com.mysql.cj.jdbc.Driver"); 
            this.conexao = DriverManager.getConnection(URL, USUARIO, SENHA);
            System.out.println("Conexão com o banco de dados estabelecida com sucesso.");
        } catch (SQLException e) {
            System.err.println("Erro Crítico ao conectar ao banco de dados: " + e.getMessage());
            // Em uma aplicação real, seria melhor lançar uma exceção personalizada ou tratar o erro de forma mais robusta.
            throw new RuntimeException("Falha ao inicializar a conexão com o banco de dados.", e);
        } /*catch (ClassNotFoundException e) {
            System.err.println("Erro: Driver JDBC do MySQL não encontrado. Verifique o classpath.");
            throw new RuntimeException("Driver JDBC não encontrado.", e);
        }*/
    }

    /**
     * Método público estático para obter a instância única da classe (Singleton).
     * Cria a instância na primeira chamada (lazy initialization).
     *
     * @return A instância única de ConexaoMySQL.
     */
    public static synchronized ConexaoMySQL getInstancia() {
        if (instancia == null) {
            instancia = new ConexaoMySQL();
        }
        return instancia;
    }

    /**
     * Retorna a conexão ativa com o banco de dados.
     *
     * @return A objeto Connection.
     * @throws SQLException Se a conexão estiver fechada ou inválida.
     */
    public Connection getConexao() throws SQLException {
        // Verifica se a conexão ainda é válida antes de retornar
        if (conexao == null || conexao.isClosed()) {
            System.err.println("Tentativa de obter uma conexão fechada ou nula. Recriando conexão...");
            // Tenta recriar a conexão em caso de falha
            try {
                 this.conexao = DriverManager.getConnection(URL, USUARIO, SENHA);
                 System.out.println("Conexão com o banco de dados restabelecida.");
            } catch (SQLException e) {
                 System.err.println("Erro Crítico ao reconectar ao banco de dados: " + e.getMessage());
                 throw new RuntimeException("Falha ao restabelecer a conexão com o banco de dados.", e);
            }
        }
        return conexao;
    }

    /**
     * Fecha a conexão com o banco de dados.
     * Este método deve ser chamado ao final da aplicação para liberar os recursos.
     */
    public void fecharConexao() {
        try {
            if (conexao != null && !conexao.isClosed()) {
                conexao.close();
                System.out.println("Conexão com o banco de dados fechada com sucesso.");
            }
        } catch (SQLException e) {
            System.err.println("Erro ao fechar a conexão com o banco de dados: " + e.getMessage());
        }
    }
    
    // Método principal para teste (opcional)
    /*
    public static void main(String[] args) {
        try {
            // Obtém a instância Singleton
            ConexaoMySQL conexaoSingleton = ConexaoMySQL.getInstancia();
            Connection conn1 = conexaoSingleton.getConexao();
            System.out.println("Conexão 1 obtida: " + conn1);

            // Tenta obter a instância novamente (deve retornar a mesma)
            ConexaoMySQL outraInstancia = ConexaoMySQL.getInstancia();
            Connection conn2 = outraInstancia.getConexao();
            System.out.println("Conexão 2 obtida: " + conn2);

            // Verifica se as conexões são as mesmas
            System.out.println("As conexões são as mesmas? " + (conn1 == conn2));

            // Fecha a conexão ao final
            conexaoSingleton.fecharConexao();

        } catch (SQLException e) {
            System.err.println("Erro durante o teste da conexão: " + e.getMessage());
        }
    }
    */
}

