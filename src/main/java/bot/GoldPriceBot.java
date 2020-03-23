package bot;

import models.XboxGoldPrice;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static services.StorageService.*;
import static utils.XboxNowHelper.collectInfo;

public class GoldPriceBot extends TelegramLongPollingBot {
    private static final Logger logger = LogManager.getLogger(GoldPriceBot.class);


    @Override
    public void onUpdateReceived(Update update) {
        // We check if the update has a message and the message has text
        if (update.hasMessage() && update.getMessage().getText().toLowerCase()
                .contains("gold")) {
            SendMessage message = new SendMessage() // Create a SendMessage object with mandatory fields
                    .setChatId(update.getMessage().getChatId())
                    .setText(getStoredGoldPriceAsString());
            try {
                execute(message); // Call method to send the message
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
        if (update.hasMessage() && update.getMessage().getText().toLowerCase().contains("check")) {
            if (!dailyPriceCheck()) {
                SendMessage message = new SendMessage() // Create a SendMessage object with mandatory fields
                        .setChatId(update.getMessage().getChatId())
                        .setText(getStoredGoldPriceAsString());
                try {
                    execute(message); // Call method to send the message
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public String getBotUsername() {
        return System.getenv("username");
    }

    @Override
    public String getBotToken() {
        return System.getenv("token");
    }

    public String getChatList() {
        return System.getenv("CHAT_LIST"); //117209127
    }

//    @Override
//    public String getBotUsername() {
//        return "GoldPriceBot";
//    }
//
//    @Override
//    public String getBotToken() {
//        return "";
//    }

    public boolean dailyPriceCheck() {
        List<XboxGoldPrice> actualPrice = collectInfo();
        String superMessage;
        logger.info("actual price: " + Arrays.toString(actualPrice.toArray()));
        logger.info("storage price: " + Arrays.toString(getPriceFromStorage().toArray()));
        if (!actualPrice.equals(getPriceFromStorage())) {
            cleanUpStorage();
            storePrice(actualPrice);
            superMessage = "Price were changed \n \n";
            Stream.of(getChatList().split(",")).forEach(user -> {
                SendMessage message = new SendMessage() // Create a SendMessage object with mandatory fields
                        .setChatId(user)
                        .setText(superMessage + getStoredGoldPriceAsString());
                try {
                    execute(message);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            });
            return true;
        }
        return false;
    }
}
