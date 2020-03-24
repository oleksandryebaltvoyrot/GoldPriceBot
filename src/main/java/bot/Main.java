package bot;

import com.sun.net.httpserver.HttpServer;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws IOException {

        //heroku ps:scale worker=1 -a xboxsubscriptionchecker

        ApiContextInitializer.init();

        TelegramBotsApi botsApi = new TelegramBotsApi();

        try {
            botsApi.registerBot(new GoldPriceBot());
            ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
            Runnable task = () -> {
                try {
                    new GoldPriceBot().sendPriceChangedMessage("test");
                    new GoldPriceBot().dailyPriceCheck();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            };

            executor.scheduleWithFixedDelay(task, 0, 2, TimeUnit.MINUTES);
        } catch (Exception e) {
            e.printStackTrace();
        }

        int serverPort = Integer.parseInt(System.getenv("PORT"));
        HttpServer server = HttpServer.create(new InetSocketAddress(serverPort), 0);
        server.createContext("/api/hello", (exchange -> {
            String respText = "Hello!";
            exchange.sendResponseHeaders(200, respText.getBytes().length);
            OutputStream output = exchange.getResponseBody();
            output.write(respText.getBytes());
            output.flush();
            exchange.close();
        }));
        server.setExecutor(null); // creates a default executor
        server.start();
    }
}
