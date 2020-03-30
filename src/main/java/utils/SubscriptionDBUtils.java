package utils;

import ca.krasnay.sqlbuilder.ParameterizedPreparedStatementCreator;
import ca.krasnay.sqlbuilder.SelectCreator;
import ca.krasnay.sqlbuilder.UpdateCreator;
import enums.Subscriptions;
import models.XboxSubscriptionPrice;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;

public class SubscriptionDBUtils {
    private static final Logger logger = LogManager.getLogger(SubscriptionDBUtils.class);

    private static final String NAME = "NAME";
    private static final String PRICE = "PRICE";
    private static final String DATE = "DATE";
    private static final String SUBSCRIPTIONS = "SUBSCRIPTIONS";

    public void createTable(Connection connection, String sql) throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.executeUpdate(sql);
        logger.info("create a table {}", sql);
        stmt.close();
        connection.close();
    }

    public void createSubscriptionTable(Connection connection) {
        String sql = "CREATE TABLE " + SUBSCRIPTIONS +
                "(" + NAME + " TEXT PRIMARY KEY NOT NULL, "
                + DATE + " TEXT NOT NULL, "
                + PRICE + " DOUBLE PRECISION NOT NULL)";
        try {
            createTable(connection, sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertOrUpdatePrice(Connection connection, XboxSubscriptionPrice subscriptionPrice) {
        ParameterizedPreparedStatementCreator creator =
                new ParameterizedPreparedStatementCreator()
                        .setSql("INSERT INTO " + SUBSCRIPTIONS + " (" + NAME + "," + PRICE + "," + DATE + ") " +
                                "VALUES (:name, :price, :date) " +
                                "ON CONFLICT (" + NAME + ") " +
                                "DO UPDATE SET " + DATE + "=:date, " + PRICE + "=:price;")
                        .setParameter("name", subscriptionPrice.getSubscription().getDBColumnName())
                        .setParameter("date", subscriptionPrice.getLastUpdate())
                        .setParameter("price", subscriptionPrice.getPrice());
        try {
            PreparedStatement statement = creator.createPreparedStatement(connection);
            statement.executeUpdate();
            statement.close();
            connection.close();
            logger.info("Price changed. NAME:{} PRICE:{}", subscriptionPrice.getSubscription().getDBColumnName(), subscriptionPrice.getPrice());
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
    }

    public void updatePrice(Connection connection, XboxSubscriptionPrice subscriptionPrice) {
        UpdateCreator creator =
                new UpdateCreator(SUBSCRIPTIONS)
                        .setValue(PRICE, subscriptionPrice.getPrice())
                        .setValue(DATE, subscriptionPrice.getLastUpdate())
                        .whereEquals(NAME, subscriptionPrice.getSubscription().getDBColumnName());
        try {
            PreparedStatement statement = creator.createPreparedStatement(connection);
            statement.executeUpdate();
            statement.close();
            connection.close();
            logger.info("Price updated. NAME:{} PRICE:{}", subscriptionPrice.getSubscription().getDBColumnName(), subscriptionPrice.getPrice());
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
    }

    public void selectAll(Connection connection) {
        SelectCreator selector = new SelectCreator()
                .column("*")
                .from(SUBSCRIPTIONS);
        try {
            PreparedStatement statement = selector.createPreparedStatement(connection);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                logger.info("=============================");
                logger.info("NAME {}", resultSet.getString(NAME));
                logger.info("PRICE {}", resultSet.getString(PRICE));
                logger.info("LAST UPDATES {}", resultSet.getString(DATE));
                logger.info("=============================");
            }
            statement.close();
            connection.close();
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
    }

    public XboxSubscriptionPrice selectPrice(Connection connection, Subscriptions subscription) {
        double price = 0;
        String date = "never";
        SelectCreator selector =
                new SelectCreator()
                        .column(PRICE)
                        .column(DATE)
                        .from(SUBSCRIPTIONS)
                        .whereEquals(NAME, subscription.getDBColumnName());
        try {
            PreparedStatement statement = selector.createPreparedStatement(connection);
            ResultSet resultSet = statement.executeQuery();
            price = resultSet.next() ? resultSet.getDouble(PRICE) : price;
            date = resultSet.next() ? resultSet.getString(DATE) : date;
            logger.info("Price selected. NAME:{} PRICE:{}", subscription.getDBColumnName(), price);
            statement.close();
            connection.close();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return new XboxSubscriptionPrice().setPrice(price).setSubscription(subscription).setLastUpdate(date);
    }

    public void insertOrUpdateDate(Connection connection, String date) {

    }
}
