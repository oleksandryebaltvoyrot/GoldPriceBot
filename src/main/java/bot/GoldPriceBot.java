package bot;

import models.XboxGoldPrice;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import utils.MarketplaysHelper;
import utils.XboxNowHelper;

import java.util.List;

import static services.StorageService.*;
import static utils.MarketplaysHelper.collectInfo;

public class GoldPriceBot extends TelegramLongPollingBot {
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
            dailyPriceCheck();
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

    @Override
    public String getBotUsername() {
        return System.getenv("username");
    }

    @Override
    public String getBotToken() {
        return System.getenv("token");
    }

//    @Override
//    public String getBotUsername() {
//        return "";
//    }
//
//    @Override
//    public String getBotToken() {
//        return "";
//    }

    public void dailyPriceCheck() {
        List<XboxGoldPrice> actualPrice = collectInfo();
        List<XboxGoldPrice> storedPrice = getPriceFromStorage();
        try {
            String superMessage;
            if (!actualPrice.equals(storedPrice)) {
                cleanUpStorage();
                XboxNowHelper.storePrice(actualPrice);
                superMessage = "Price is changed \n \n";
                SendMessage message = new SendMessage() // Create a SendMessage object with mandatory fields
                        .setChatId("117209127")
                        .setText(superMessage + getStoredGoldPriceAsString());
                execute(message);
            }
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
