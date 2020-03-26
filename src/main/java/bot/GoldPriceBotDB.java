package bot;

import enums.SimpleMessages;
import enums.Subscriptions;
import models.XboxSubscriptionPrice;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import services.PriceStorageService;
import utils.Emoji;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static enums.Subscriptions.*;
import static utils.XboxSubscriptionHelper.*;


public class GoldPriceBotDB extends TelegramLongPollingBot {
    private static final Logger logger = LogManager.getLogger(GoldPriceBotDB.class);
    PriceStorageService priceStorageService = new PriceStorageService();

    @Override
    public String getBotUsername() {
        return System.getenv("username");
    }

    @Override
    public String getBotToken() {
        return System.getenv("token");
    }

    public String getChatList() {
        return System.getenv("CHAT_LIST");
    }

//    @Override
////    public String getBotUsername() {
////        return "GoldenBoy";
////    }
////
////    @Override
////    public String getBotToken() {
////        return "356162982:AAGFOsyBumDpMw0nUiSw3pg7WCejrmT0SvA";
////    }
////
////    public String getChatList() {
////        return "117209127";
////    }

    @Override
    public void onUpdateReceived(Update update) {
        // We check if the update has a message and the message has text
        if (update.hasMessage()) {
            String request = update.getMessage().getText().toLowerCase();
            String userId = update.getMessage().getChatId().toString();
            Random rand = new Random();
            String header = Arrays.asList(SimpleMessages.values()).get(rand.nextInt(SimpleMessages.values().length)).getMessage();
            if (request.contains("gold")) {
                List<Subscriptions> goldList = Arrays.asList(GOLD_MONTH, GOLD_THREE, GOLD_YEAR);
                List<XboxSubscriptionPrice> priceList = goldList.stream().map(sub ->
                        priceStorageService.getPriceBySubscription(sub)).collect(Collectors.toList());
                priceList.forEach(price -> sendPriceMessage(userId, String.format(header, price.getName()), price.getPrice().toString()));
            }
            if (request.contains("ultimate")) {
                XboxSubscriptionPrice price = priceStorageService.getPriceBySubscription(ULTIMATE);
                sendPriceMessage(userId, String.format(header, price.getName()), price.getPrice().toString());
            }
            if (request.contains("game_pass")) {
                XboxSubscriptionPrice price = priceStorageService.getPriceBySubscription(GAME_PASS);
                sendPriceMessage(userId, String.format(header, price.getName()), price.getPrice().toString());
            }
            if (request.contains("check")) {
                try {
                    Set<Subscriptions> subscriptionsWithoutChanges = dailyPriceCheck();
                    AtomicReference<XboxSubscriptionPrice> price = null;
                    subscriptionsWithoutChanges.forEach(subscription -> {
                        price.set(priceStorageService.getPriceBySubscription(GAME_PASS));
                        sendPriceMessage(userId, "There is nothing new " + Emoji.WORRIED_EMOJI, price.get().getPrice().toString());
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void sendPriceChangedMessage(String price) {
        final String headerMessage = String.format("%s Price wa$ changed %s", Emoji.ROTATING_LIGHT, Emoji.ROTATING_LIGHT);
        Stream.of(getChatList().split(","))
                .forEach(user -> sendPriceMessage(user, headerMessage, price));
    }

    public Set<Subscriptions> dailyPriceCheck() throws IOException {
        List<XboxSubscriptionPrice> golds = extractGoldPrice();
        HashMap<Subscriptions, XboxSubscriptionPrice> subscriptionsList = new HashMap<>();
        subscriptionsList.put(Subscriptions.GOLD_MONTH, golds.get(2));
        subscriptionsList.put(Subscriptions.GOLD_THREE, golds.get(1));
        subscriptionsList.put(Subscriptions.GOLD_YEAR, golds.get(0));
        subscriptionsList.put(Subscriptions.ULTIMATE, extractGameUltimatePrice());
        subscriptionsList.put(Subscriptions.GAME_PASS, extractGamePassPrice());

        subscriptionsList.keySet().forEach(subscription -> {
            if (!subscriptionsList.get(subscription).getPrice().equals(priceStorageService.getPriceBySubscription(subscription).getPrice())) {
                priceStorageService.updatePrice(subscriptionsList.get(subscription));
                sendPriceChangedMessage(subscriptionsList.get(subscription).toFormattedPriceAsString());
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
