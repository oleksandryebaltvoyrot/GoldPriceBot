package bot;

import enums.SimpleMessages;
import enums.Storage;
import models.XboxGoldPrice;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import utils.Emoji;

import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;

import static services.StorageService.*;
import static utils.XboxNowHelper.*;

@Component
public class GoldPriceBot extends TelegramLongPollingBot {
    private static final Logger logger = LogManager.getLogger(GoldPriceBot.class);

//    @Override
//    public String getBotUsername() {
//        return System.getenv("username");
//    }
//
//    @Override
//    public String getBotToken() {
//        return System.getenv("token");
//    }
//
//    public String getChatList() {
//        return System.getenv("CHAT_LIST");
//    }

    @Override
    public String getBotUsername() {
        return "GoldenBoy";
    }

    @Override
    public String getBotToken() {
        return "356162982:AAGFOsyBumDpMw0nUiSw3pg7WCejrmT0SvA";
    }

    public String getChatList() {
        return "117209127";
    }

    @Override
    public void onUpdateReceived(Update update) {
        // We check if the update has a message and the message has text
        if (update.hasMessage()) {
            String request = update.getMessage().getText().toLowerCase();
            String userId = update.getMessage().getChatId().toString();
            Random rand = new Random();
            String header = Arrays.asList(SimpleMessages.values()).get(rand.nextInt(SimpleMessages.values().length)).getMessage();
            if (request.contains(Storage.GOLD_FILE_PATH.getStorageName())) {
                sendPriceMessage(userId, String.format(header, Storage.GOLD_FILE_PATH.getStorageName().toUpperCase()), getFormattedPriceAsString(Storage.GOLD_FILE_PATH));
            }
            if (request.contains("ultimate")) {
                sendPriceMessage(userId, String.format(header, Storage.ULTIMATE_FILE_PATH.getStorageName().toUpperCase()), getFormattedPriceAsString(Storage.ULTIMATE_FILE_PATH));
            }
            if (request.contains("game_pass")) {
                sendPriceMessage(userId, String.format(header, Storage.PASS_FILE_PATH.getStorageName().toUpperCase()), getFormattedPriceAsString(Storage.PASS_FILE_PATH));
            }
            if (request.contains("check")) {
                try {
                    Set<Storage> subscriptionsWithoutChanges = dailyPriceCheck();
                    subscriptionsWithoutChanges.forEach(subscription ->
                            sendPriceMessage(userId, "There is nothing new " + Emoji.WORRIED_EMOJI, getFormattedPriceAsString(subscription)));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void sendPriceChangedMessage(String price) {
        final String headerMessage = String.format("%s Price wa$ changed %s", Emoji.ROTATING_LIGHT, Emoji.ROTATING_LIGHT);
        Stream.of(getChatList().split(","))
                .forEach(user -> sendPriceMessage(user, headerMessage, price));
    }

    public Set<Storage> dailyPriceCheck() throws IOException {
        HashMap<Storage, List<XboxGoldPrice>> subscriptionsList = new HashMap<>();
        subscriptionsList.put(Storage.PASS_FILE_PATH, Collections.singletonList(extractGamePassPrice()));
        subscriptionsList.put(Storage.ULTIMATE_FILE_PATH, Collections.singletonList(extractGameUltimatePrice()));
        subscriptionsList.put(Storage.GOLD_FILE_PATH, extractGoldPrice());

        subscriptionsList.keySet().forEach(subscription -> {
            if (!subscriptionsList.get(subscription).equals(getPriceFromStorage(subscription))) {
                cleanUpStorage(subscription);
                storePrice(subscriptionsList.get(subscription), subscription);
                sendPriceChangedMessage(getFormattedPriceAsString(subscription));
                subscriptionsList.remove(subscription);
            }
        });
        return subscriptionsList.keySet();
    }

    void sendPriceMessage(String chatId, String header, String price) {
        SendMessage message = new SendMessage()
                .setChatId(chatId)
                .setText(header + "\n\n" + price);
        try {
            execute(message);
            logger.info(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
