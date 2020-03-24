package bot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.ApiContextInitializer;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        ApiContextInitializer.init();
        SpringApplication.run(Main.class, args);
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
    }
}
