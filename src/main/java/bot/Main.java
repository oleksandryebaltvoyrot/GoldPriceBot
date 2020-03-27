package bot;

import com.sun.net.httpserver.HttpServer;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import services.PriceStorage;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class Main {
    public static void main(String[] args) throws IOException, TelegramApiRequestException {

        //heroku ps:scale worker=1 -a xboxsubscriptionchecker

        ApiContextInitializer.init();
        TelegramBotsApi botsApi = new TelegramBotsApi();
        botsApi.registerBot(new XboxSubscriptionCheckerBot());

        int serverPort = Integer.parseInt(System.getenv("PORT"));
        HttpServer server = HttpServer.create(new InetSocketAddress(serverPort), 0);
        server.createContext("/", (exchange -> {
            String respText = "I'm working!";
            exchange.sendResponseHeaders(200, respText.getBytes().length);
            OutputStream output = exchange.getResponseBody();
            output.write(respText.getBytes());
            output.flush();
            //new PriceStorage().createSubscriptionTable();
            new XboxSubscriptionCheckerBot().dailyPriceCheck();
            new PriceStorage().logTable();
            exchange.close();
        }));
        server.setExecutor(null); // creates a default executor
        server.start();
    }
}
