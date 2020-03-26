package services;

import ca.krasnay.sqlbuilder.InsertCreator;
import ca.krasnay.sqlbuilder.ParameterizedPreparedStatementCreator;
import ca.krasnay.sqlbuilder.SelectCreator;
import ca.krasnay.sqlbuilder.UpdateCreator;
import enums.Storage;
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
    }

    public void createSubscriptionTable(Connection connection) throws SQLException {
        String sql = "CREATE TABLE " + SUBSCRIPTIONS +
                "(" + NAME + " TEXT PRIMARY KEY NOT NULL, "
                + PRICE + " DOUBLE PRECISION NOT NULL)";
        createTable(connection, sql);
    }


    public void insertPrice(Connection connection, Storage name, double price) throws SQLException {
        PreparedStatement statement = new InsertCreator(SUBSCRIPTIONS)
                .setValue(NAME, name.getStorageName())
                .setValue(PRICE, price)
                .createPreparedStatement(connection);
        statement.executeUpdate();
        logger.info("Price inserted. NAME:{} PRICE:{}", name.getStorageName(), price);
        statement.close();
    }

    public void insertOrUpdatePrice(Connection connection, XboxSubscriptionPrice subscriptionPrice) throws SQLException {
        PreparedStatement statement =
                new ParameterizedPreparedStatementCreator()
                        .setSql("INSERT INTO " + SUBSCRIPTIONS + " (" + NAME + "," + PRICE + ") " +
                                "VALUES (:name, :price) " +
                                "ON CONFLICT (" + NAME + ") " +
                                "DO UPDATE SET " + PRICE + "=:price;")
                        .setParameter("name", subscriptionPrice.getSubscription().getSubscriptionName())
                        .setParameter("price", subscriptionPrice.getPrice()).createPreparedStatement(connection);
        statement.executeUpdate();
        logger.info("Price changed. NAME:{} PRICE:{}", subscriptionPrice.getSubscription().getSubscriptionName(), subscriptionPrice.getPrice());
        statement.close();
    }

    public void updatePrice(Connection connection, XboxSubscriptionPrice subscriptionPrice) throws SQLException {
        PreparedStatement statement =
                new UpdateCreator(SUBSCRIPTIONS)
                        .setValue(PRICE, subscriptionPrice.getPrice())
                        .whereEquals(NAME, subscriptionPrice.getSubscription().getSubscriptionName())
                        .createPreparedStatement(connection);
        logger.info("Price updated. NAME:{} PRICE:{}", subscriptionPrice.getSubscription().getSubscriptionName(), subscriptionPrice.getPrice());
        statement.executeUpdate();
        statement.close();
    }

    public void selectAll(Connection connection) throws SQLException {
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
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return new XboxSubscriptionPrice().setPrice(price).setSubscriptionName(subscription);
    }
}
