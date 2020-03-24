package bot;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {

        ApiContextInitializer.init();

        TelegramBotsApi botsApi = new TelegramBotsApi();

        try {
            botsApi.registerBot(new GoldPriceBot());
            ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
            Runnable task = () -> {
                try {
                    new GoldPriceBot().dailyPriceCheck();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            };

            executor.scheduleWithFixedDelay(task, 0, 10, TimeUnit.HOURS);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
