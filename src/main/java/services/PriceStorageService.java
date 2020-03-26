package services;

import enums.Subscriptions;
import models.XboxSubscriptionPrice;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;

import static services.PostgreSQLJDBC.getConnection;

public class PriceStorageService {
    private static final Logger logger = LogManager.getLogger(PriceStorageService.class);

    private SubscriptionDBUtils dbUtils = new SubscriptionDBUtils();

    public XboxSubscriptionPrice getPriceBySubscription(Subscriptions subscription) {
        Connection connection = getConnection();
        try {
            return dbUtils.selectPrice(connection, subscription);
        } finally {
            assert connection != null;
            try {
                connection.close();
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
    }

    public void updatePrice(XboxSubscriptionPrice price) {
        Connection connection = getConnection();
        try {
            dbUtils.insertOrUpdatePrice(connection, price);
        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            assert connection != null;
            try {
                connection.close();
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
    }

    public void getAll() {
        Connection connection = getConnection();
        try {
            dbUtils.selectAll(connection);
        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            assert connection != null;
            try {
                connection.close();
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
    }
}
