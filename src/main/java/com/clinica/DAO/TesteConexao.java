package com.clinica.DAO;
import java.sql.Connection;

public class TesteConexao {
    public static void main(String[] args) {
        
        try {
            Connection conexao = ConexaoMySQL.getConexao();
            if (conexao != null) {
                System.out.println("Conexão bem-sucedida!");
                conexao.close(); 
            } else {
                System.out.println("Falha ao conectar ao banco de dados.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
