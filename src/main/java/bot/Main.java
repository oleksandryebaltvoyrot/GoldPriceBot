package bot;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

public class Main {
    public static void main(String[] args) throws TelegramApiRequestException {

        ApiContextInitializer.init();

        TelegramBotsApi botsApi = new TelegramBotsApi();
        botsApi.registerBot(new GoldPriceBot());

//        try {
//            botsApi.registerBot(new GoldPriceBot());
//            ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
//            Runnable task = () -> {
//                try {
//                    new GoldPriceBot().sendPriceChangedMessage("test");
//                    new GoldPriceBot().dailyPriceCheck();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            };
//
//            executor.scheduleWithFixedDelay(task, 0, 2, TimeUnit.MINUTES);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }
}
