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
    private static final String NAME = "NAME";
    private static final String PRICE = "PRICE";
    private static final String SUBSCRIPTIONS = "SUBSCRIPTIONS";

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
        PreparedStatement statement = new InsertCreator(SUBSCRIPTIONS)
                .setValue(NAME, name.getStorageName())
                .setValue(PRICE, price)
                .createPreparedStatement(connection);
        statement.executeUpdate();
        logger.info("Price inserted. NAME:{} PRICE:{}", name.getStorageName(), price);
        statement.close();
    }

    public static void insertOrUpdatePrice(Connection connection, Storage name, double price) throws SQLException {
        PreparedStatement statement =
                new ParameterizedPreparedStatementCreator()
                        .setSql("INSERT INTO :table (:columnName,:columnPrice) VALUES (:name, :price) " +
                                "ON CONFLICT (NAME) DO UPDATE SET PRICE=:price;")
                        .setParameter("table", SUBSCRIPTIONS)
                        .setParameter("columnName", NAME)
                        .setParameter("columnPrice", PRICE)
                        .setParameter("name", name.getStorageName())
                        .setParameter("price", price).createPreparedStatement(connection);
        statement.executeUpdate();
        logger.info("Price changed. NAME:{} PRICE:{}", name.getStorageName(), price);
        statement.close();
    }

    public static void updatePrice(Connection connection, Storage name, double price) throws SQLException {
        PreparedStatement statement =
                new UpdateCreator(SUBSCRIPTIONS)
                        .setValue(PRICE, price)
                        .whereEquals(NAME, name.getStorageName())
                        .createPreparedStatement(connection);
        logger.info("Price updated. NAME:{} PRICE:{}", name.getStorageName(), price);
        statement.executeUpdate();
        statement.close();
    }

    public static void selectAll(Connection connection) throws SQLException {
        PreparedStatement statement = new SelectCreator()
                .column("*")
                .from(SUBSCRIPTIONS)
                .createPreparedStatement(connection);
        ResultSet resultSet = statement.executeQuery();
        while (resultSet.next()) {
            logger.info("=============================");
            logger.info("NAME {}", resultSet.getString(NAME));
            logger.info("PRICE {}", resultSet.getString(PRICE));
            logger.info("=============================");
        }
        statement.close();
    }

    public static Double selectPrice(Connection connection, Storage name) throws SQLException {
        PreparedStatement statement =
                new SelectCreator()
                        .column(PRICE)
                        .from(SUBSCRIPTIONS)
                        .whereEquals(NAME, name.getStorageName())
                        .createPreparedStatement(connection);
        ResultSet resultSet = statement.executeQuery();
        double price = resultSet.next() ? resultSet.getDouble(PRICE) : 0;
        logger.info("Price selected. NAME:{} PRICE:{}", name.getStorageName(), price);
        statement.close();
        return price;
    }
}

