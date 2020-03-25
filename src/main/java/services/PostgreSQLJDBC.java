package services;

import ca.krasnay.sqlbuilder.InsertCreator;
import ca.krasnay.sqlbuilder.ParameterizedPreparedStatementCreator;
import ca.krasnay.sqlbuilder.SelectCreator;
import ca.krasnay.sqlbuilder.UpdateCreator;
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
        PreparedStatement statement = new InsertCreator("SUBSCRIPTIONS")
                .setValue("NAME", name.getStorageName())
                .setValue("PRICE", price)
                .createPreparedStatement(connection);
        statement.executeUpdate();
        logger.info("Price inserted. NAME:{} PRICE:{}", name.getStorageName(), price);
        statement.close();

//        String sql = String.format("INSERT INTO SUBSCRIPTIONS (NAME,PRICE) VALUES ('%s', %s);", name.getStorageName(), price);
//        Statement stmt = connection.createStatement();
//        stmt.executeUpdate(sql);
//        logger.info("SUBSCRIPTIONS table updated {}", sql);
//        stmt.close();
    }

    public static void insertOrUpdatePrice(Connection connection, Storage name, double price) throws SQLException {
        String statement =
                new ParameterizedPreparedStatementCreator()
                        .setSql("INSERT INTO SUBSCRIPTIONS (NAME,PRICE) VALUES (':name', :price) " +
                                "ON CONFLICT (NAME) DO UPDATE SET PRICE=:price;")
                        .setParameter("name", name.getStorageName())
                        .setParameter("price", price).getSql();
        //statement.executeUpdate();
        logger.info("Price changed. NAME:{} PRICE:{}", name.getStorageName(), price);
        logger.info(statement);
        //statement.close();

//        String sql = String.format("INSERT INTO SUBSCRIPTIONS (NAME,PRICE) VALUES ('%s', %s) ON CONFLICT (NAME) DO UPDATE SET PRICE=%s;", name.getStorageName(), price, price);
//        Statement stmt = connection.createStatement();
//        stmt.executeUpdate(sql);
//        logger.info(sql);
//        stmt.close();
    }

    public static void updatePrice(Connection connection, Storage name, double price) throws SQLException {
        PreparedStatement statement =
                new UpdateCreator("SUBSCRIPTIONS")
                        .setValue("PRICE", price)
                        .whereEquals("NAME", name.getStorageName())
                        .createPreparedStatement(connection);
        logger.info("Price updated. NAME:{} PRICE:{}", name.getStorageName(), price);
        statement.executeUpdate();
        statement.close();
        //String sql = String.format("UPDATE SUBSCRIPTIONS SET PRICE = %s WHERE NAME='%s';", price, name.getStorageName());
    }

    public static void selectAll(Connection connection) throws SQLException {
        String sql = "SELECT * FROM SUBSCRIPTIONS;";
        Statement stmt = connection.createStatement();
        ResultSet resultSet = stmt.executeQuery(sql);
        while (resultSet.next()) {
            logger.info("NAME {}", resultSet.getString("NAME"));
            logger.info("PRICE {}", resultSet.getString("PRICE"));
            logger.info("=============================");
        }
        stmt.close();
    }

    public static Double selectPrice(Connection connection, Storage name) throws SQLException {
        PreparedStatement statement =
                new SelectCreator()
                        .column("PRICE")
                        .whereEquals("NAME", "'" + name.getStorageName() + "'")
                        .createPreparedStatement(connection);
        ResultSet resultSet = statement.executeQuery();
        double price = resultSet.next() ? resultSet.getDouble("price") : 0;
        logger.info("Price selected. NAME:{} PRICE:{}", name.getStorageName(), price);
        statement.close();
        return price;
//        String sql = String.format("SELECT PRICE FROM SUBSCRIPTIONS WHERE NAME='%s';", name.getStorageName());
//        Statement stmt = connection.createStatement();
//        ResultSet resultSet = stmt.executeQuery(sql);
//        double price = resultSet.next() ? resultSet.getDouble("price") : 0;
//        logger.info(sql);
//        stmt.close();
//        logger.info(price);
//        return price;
    }
}
