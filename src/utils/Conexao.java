/**
    Classe responsável por estabelecer a conexão com o DB.
 */

package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexao {
    public Connection getConnection() {
        try {
            // Tenta a conexão
            String url = "jdbc:postgresql://localhost:5432/plataforma_doacao"; 
            String user = "postgres";
            String senha = "teste"; 

            // se der boa retorna a conexão funcionando
            return DriverManager.getConnection(url, user, senha);
        } catch (SQLException ex) {
            System.out.println("Erro de Conexao: " + ex.getMessage());
            return null;
        }
    }
}
