package bot;

import com.vdurmont.emoji.EmojiParser;
import enums.Storage;
import models.XboxGoldPrice;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static services.StorageService.*;
import static utils.XboxNowHelper.*;

public class GoldPriceBot extends TelegramLongPollingBot {
    private static final Logger logger = LogManager.getLogger(GoldPriceBot.class);
    private String dragonFaceEmoji = EmojiParser.parseToUnicode(":dragon_face:");
    private String dollarEmoji = EmojiParser.parseToUnicode(":dollar:");
    private String moneyEmoji = EmojiParser.parseToUnicode(":moneybag:");

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
//        return "GoldenBoy";
//    }
//
//    @Override
//    public String getBotToken() {
//        return "356162982:AAGFOsyBumDpMw0nUiSw3pg7WCejrmT0SvA";
//    }
//
//    public String getChatList() {
//        return "117209127";
//    }

    @Override
    public void onUpdateReceived(Update update) {
        // We check if the update has a message and the message has text
        if (update.hasMessage() && update.getMessage().getText().toLowerCase()
                .contains("gold")) {
            SendMessage message = new SendMessage() // Create a SendMessage object with mandatory fields
                    .setChatId(update.getMessage().getChatId())
                    .setText(getFormattedPriceAsString(Storage.GOLD_FILE_PATH));
            logger.info(getFormattedPriceAsString(Storage.GOLD_FILE_PATH));
            try {
                execute(message); // Call method to send the message
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
        if (update.hasMessage() && update.getMessage().getText().toLowerCase().contains("check")) {
            try {
                dailyPriceCheck().forEach(path -> {
                    SendMessage message = new SendMessage() // Create a SendMessage object with mandatory fields
                            .setChatId(update.getMessage().getChatId())
                            .setText(getFormattedPriceAsString(path));
                    try {
                        execute(message);
                        logger.info(message);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendPriceChangedMessage(String price) {
        final String headerMessage = String.format("%s %s Price was changed %s %s", dragonFaceEmoji, dollarEmoji, moneyEmoji, " \n \n");
        Stream.of(getChatList().split(","))
                .forEach(user -> {
                    SendMessage message = new SendMessage() // Create a SendMessage object with mandatory fields
                            .setChatId(user)
                            .setText(headerMessage + price);
                    try {
                        execute(message);
                        logger.info(message);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                });
    }


    public Set<Storage> dailyPriceCheck() throws IOException {
        HashMap<Storage, List<XboxGoldPrice>> items = new HashMap<>();
        items.put(Storage.PASS_FILE_PATH, Collections.singletonList(extractGamePassPrice()));
        items.put(Storage.ULTIMATE_FILE_PATH, Collections.singletonList(extractGameUltimatePrice()));
        items.put(Storage.GOLD_FILE_PATH, extractGoldPrice());

        items.keySet().forEach(path -> {
            if (!items.get(path).equals(getPriceFromStorage(path))) {
                cleanUpStorage(path);
                storePrice(items.get(path), path);
                sendPriceChangedMessage(getFormattedPriceAsString(path));
                items.remove(path);
            }
        });
        return items.keySet();
    }
}
