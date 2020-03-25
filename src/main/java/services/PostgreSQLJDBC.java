package services;

import enums.Storage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.*;

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

    public static void insertPrice(Connection connection, Storage name, double price) throws SQLException {
        String sql = String.format("INSERT INTO SUBSCRIPTIONS (NAME,PRICE) VALUES ('%s', %s);", name.getStorageName(), price);
        Statement stmt = connection.createStatement();
        stmt.executeUpdate(sql);
        logger.info("SUBSCRIPTIONS table updated {}", sql);
        stmt.close();
    }

    public static void updatePrice(Connection connection, Storage name, double price) throws SQLException {
        String sql = String.format("UPDATE SUBSCRIPTIONS SET PRICE = %s WHERE NAME='%s';", price, name.getStorageName());
        Statement stmt = connection.createStatement();
        stmt.executeUpdate(sql);
        logger.info(sql);
        stmt.close();
    }

    public static Double selectPrice(Connection connection, Storage name) throws SQLException {
        String sql = String.format("SELECT PRICE FROM SUBSCRIPTIONS WHERE NAME='%s';", name.getStorageName());
        Statement stmt = connection.createStatement();
        ResultSet resultSet = stmt.executeQuery(sql);
        double price = resultSet.next() ? resultSet.getDouble("price") : 0;
        logger.info(sql);
        stmt.close();
        logger.info(price);
        return price;
    }
}
