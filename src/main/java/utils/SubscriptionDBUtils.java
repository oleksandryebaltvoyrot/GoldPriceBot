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
                        .setSql("INSERT INTO " + SUBSCRIPTIONS + " (" + NAME + "," + PRICE + ") " +
                                "VALUES (:name, :price) " +
                                "ON CONFLICT (" + NAME + ") " +
                                "DO UPDATE SET " + PRICE + "=:price;")
                        .setParameter("name", subscriptionPrice.getSubscription().getSubscriptionName())
                        .setParameter("price", subscriptionPrice.getPrice());
        try {
            PreparedStatement statement = creator.createPreparedStatement(connection);
            statement.executeUpdate();
            statement.close();
            connection.close();
            logger.info("Price changed. NAME:{} PRICE:{}", subscriptionPrice.getSubscription().getSubscriptionName(), subscriptionPrice.getPrice());
        } catch (SQLException e) {
            logger.error(e.getMessage());
        }
    }

    public void updatePrice(Connection connection, XboxSubscriptionPrice subscriptionPrice) {
        UpdateCreator creator =
                new UpdateCreator(SUBSCRIPTIONS)
                        .setValue(PRICE, subscriptionPrice.getPrice())
                        .whereEquals(NAME, subscriptionPrice.getSubscription().getSubscriptionName());
        try {
            PreparedStatement statement = creator.createPreparedStatement(connection);
            statement.executeUpdate();
            statement.close();
            connection.close();
            logger.info("Price updated. NAME:{} PRICE:{}", subscriptionPrice.getSubscription().getSubscriptionName(), subscriptionPrice.getPrice());
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
        SelectCreator selector =
                new SelectCreator()
                        .column(PRICE)
                        .from(SUBSCRIPTIONS)
                        .whereEquals(NAME, subscription.getSubscriptionName());
        try {
            PreparedStatement statement = selector.createPreparedStatement(connection);
            ResultSet resultSet = statement.executeQuery();
            price = resultSet.next() ? resultSet.getDouble(PRICE) : 0;
            logger.info("Price selected. NAME:{} PRICE:{}", subscription.getSubscriptionName(), price);
            statement.close();
            connection.close();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return new XboxSubscriptionPrice().setPrice(price).setSubscription(subscription);
    }
}
