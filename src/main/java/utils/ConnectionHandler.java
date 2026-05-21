package utils;

import exceptions.GenericSystemException;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionHandler {
    private static ConnectionHandler instance = null;
    private final Properties prop = new Properties();

    private ConnectionHandler() throws GenericSystemException {

        try (FileInputStream dbInfoFile = new FileInputStream("src/main/resources/database.properties")) {
            prop.load(dbInfoFile);
        } catch (IOException e) {
            throw new GenericSystemException("Impossibile caricare il file di configurazione del DB", e);
        }
    }

    public static ConnectionHandler getInstance() throws GenericSystemException {
        if (instance == null) {
            instance = new ConnectionHandler();
        }
        return instance;
    }

    public Connection getConnection() throws GenericSystemException {
        try {
            String connectionUrl = prop.getProperty("url");
            String user = prop.getProperty("user");
            String pass = prop.getProperty("password");

            return DriverManager.getConnection(connectionUrl, user, pass);
        } catch (SQLException e) {
            throw new GenericSystemException("Errore durante l'apertura della connessione al database", e);
        }
    }
}