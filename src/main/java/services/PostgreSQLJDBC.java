package services;

import enums.Storage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class PostgreSQLJDBC {
    private static final Logger logger = LogManager.getLogger(PostgreSQLJDBC.class);

    private static Connection connection = null;


    public static Connection getConnection() throws URISyntaxException, SQLException {
        URI dbUri = new URI(System.getenv("DATABASE_URL"));

        String username = dbUri.getUserInfo().split(":")[0];
        String password = dbUri.getUserInfo().split(":")[1];
        String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath();
        logger.info("connecting to DB {}", dbUrl);
        if (connection == null) {
            return DriverManager.getConnection(dbUrl, username, password);
        }
        return connection;
    }

    public static void createTable(Connection connection, String sql) throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.executeUpdate(sql);
        logger.info("create a table {}", sql);
        stmt.close();
    }

    public static void insertPrice(Connection connection, Storage name, String price) throws SQLException {
        String sql = String.format("INSERT INTO SUBSCRIPTIONS (ID,NAME,PRICE) VALUES (1, '%s', '%s');", name.getStorageName(), price);
        Statement stmt = connection.createStatement();
        stmt.executeUpdate(sql);
        logger.info("SUBSCRIPTIONS table updated {}", sql);
        stmt.close();
    }
}
