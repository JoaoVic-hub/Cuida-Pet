package com.clinica.DAO;
import java.sql.Connection;

public class TesteConexao {
    public static void main(String[] args) {
        // Testando a conexão com o banco de dados
        try {
            Connection conexao = ConexaoMySQL.getConexao();
            if (conexao != null) {
                System.out.println("Conexão bem-sucedida!");
                conexao.close(); // Fechando a conexão após o teste
            } else {
                System.out.println("Falha ao conectar ao banco de dados.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
