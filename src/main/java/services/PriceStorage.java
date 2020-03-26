package services;

import enums.Subscriptions;
import models.XboxSubscriptionPrice;
import utils.SubscriptionDBUtils;

import java.sql.Connection;

import static services.PostgreSQLJDBC.getConnection;

public class PriceStorage {
    private SubscriptionDBUtils dbUtils = new SubscriptionDBUtils();

    public XboxSubscriptionPrice getPriceBySubscription(Subscriptions subscription) {
        Connection connection = getConnection();
        return dbUtils.selectPrice(connection, subscription);
    }


    public void updatePrice(XboxSubscriptionPrice price) {
        Connection connection = getConnection();
        dbUtils.insertOrUpdatePrice(connection, price);
    }

    public void createSubscriptionTable() {
        Connection connection = getConnection();
        dbUtils.createSubscriptionTable(connection);
    }

    public void logTable() {
        Connection connection = getConnection();
        dbUtils.selectAll(connection);
    }
}
