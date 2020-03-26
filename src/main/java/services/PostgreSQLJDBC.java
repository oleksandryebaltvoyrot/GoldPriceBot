package services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URI;
import java.sql.Connection;
import java.sql.DriverManager;

public class PostgreSQLJDBC {
    private static final Logger logger = LogManager.getLogger(PostgreSQLJDBC.class);

    private static Connection connection = null;


    public static Connection getConnection() {
        if (connection == null) {
            try {
                URI dbUri = new URI(System.getenv("DATABASE_URL"));
                String username = dbUri.getUserInfo().split(":")[0];
                String password = dbUri.getUserInfo().split(":")[1];
                String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath();
                logger.info("connecting to DB {}", dbUrl);
                return DriverManager.getConnection(dbUrl, username, password);
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
        return connection;
    }

}

