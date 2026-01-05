package model;

import java.sql.*;

public class provaconndb {
    // 1. L'URL DEVE iniziare con jdbc:postgresql
    private final String url = "jdbc:postgresql://localhost:5432/postgres";
    private final String user = "admin";
    private final String password = "password";

    public void testConnessione() {
        try {
            // 2. Forza il caricamento del driver Postgres
            Class.forName("org.postgresql.Driver");

            Connection conn = DriverManager.getConnection(url, user, password);
            System.out.println("Connesso con successo a POSTGRESQL!");

        } catch (ClassNotFoundException e) {
            System.err.println("Driver Postgres non trovato nel Classpath!");
        } catch (SQLException e) {
            System.err.println("Errore SQL: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        new provaconndb().testConnessione();
    }
}