package bot;

import com.sun.net.httpserver.HttpServer;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.SQLException;

import static enums.Storage.GOLD_FILE_PATH;
import static services.PostgreSQLJDBC.*;

public class Main {
    public static void main(String[] args) throws IOException, TelegramApiRequestException {

        //heroku ps:scale worker=1 -a xboxsubscriptionchecker

        ApiContextInitializer.init();
        TelegramBotsApi botsApi = new TelegramBotsApi();
        botsApi.registerBot(new GoldPriceBot());

        int serverPort = Integer.parseInt(System.getenv("PORT"));
        HttpServer server = HttpServer.create(new InetSocketAddress(serverPort), 0);
        server.createContext("/", (exchange -> {
            String respText = "I'm working!";
            exchange.sendResponseHeaders(200, respText.getBytes().length);
            OutputStream output = exchange.getResponseBody();
            output.write(respText.getBytes());
            output.flush();
            new GoldPriceBot().dailyPriceCheck();
            String sql = "CREATE TABLE SUBSCRIPTIONS " +
                    "(NAME TEXT PRIMARY KEY     NOT NULL," +
                    " PRICE            REAL     NOT NULL)";
            try {
                Connection connection = getConnection();
                createTable(connection, sql);
                insertPrice(connection, GOLD_FILE_PATH, 19.99);
                updatePrice(connection, GOLD_FILE_PATH, 20.99);
                selectPrice(connection, GOLD_FILE_PATH);
                connection.close();
            } catch (URISyntaxException | SQLException e) {
                e.printStackTrace();
            }

            exchange.close();
        }));
        server.setExecutor(null); // creates a default executor
        server.start();
    }
}
