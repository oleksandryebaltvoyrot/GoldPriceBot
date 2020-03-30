package bot;

import enums.SimpleMessages;
import enums.Subscriptions;
import models.XboxSubscriptionPrice;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Update;
import services.PriceStorage;
import utils.Emoji;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static enums.Subscriptions.*;
import static utils.Emoji.*;
import static utils.XboxSubscriptionHelper.*;


public class XboxSubscriptionCheckerBot extends TelegramLongPollingBot {
    private static final Logger logger = LogManager.getLogger(XboxSubscriptionCheckerBot.class);
    private PriceStorage priceStorage = new PriceStorage();

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
////        return "";
////    }
////
////    public String getChatList() {
////        return "";
////    }

    @Override
    public void onUpdateReceived(Update update) {
        // We check if the update has a message and the message has text
        if (update.hasMessage()) {
            final String request = update.getMessage().getText().toLowerCase();
            final String userId = update.getMessage().getChatId().toString();
            String header = Arrays.asList(SimpleMessages.values()).get(new Random().nextInt(SimpleMessages.values().length)).getMessage();
            if (request.contains("gold")) {
                List<Subscriptions> goldList = Arrays.asList(GOLD_MONTH, GOLD_THREE, GOLD_YEAR);
                List<XboxSubscriptionPrice> priceList = goldList.stream().map(sub ->
                        priceStorage.getPriceBySubscription(sub)).collect(Collectors.toList());
                String message = priceList.stream().map(price -> price.toFormattedPriceAsString() + "\n").collect(Collectors.joining());
                sendPriceMessage(userId, String.format(header, "GOLD"), message);
            }
            if (request.contains("ultimate")) {
                XboxSubscriptionPrice price = priceStorage.getPriceBySubscription(ULTIMATE);
                sendPriceMessage(userId, String.format(header, price.getSubscription().name()), price.toFormattedPriceAsString());
            }
            if (request.contains("game_pass")) {
                XboxSubscriptionPrice price = priceStorage.getPriceBySubscription(GAME_PASS);
                sendPriceMessage(userId, String.format(header, price.getSubscription().name().replace("_", " ")), price.toFormattedPriceAsString());
            }
            if (request.contains("check")) {
                try {
                    String message = createNotUdatedSubscriptionMessage(dailyPriceCheck());
                    sendPriceMessage(userId, "There is nothing new " + Emoji.WORRIED_EMOJI, message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String createNotUdatedSubscriptionMessage(List<XboxSubscriptionPrice> subscriptionsWithoutChanges) {
        if (!subscriptionsWithoutChanges.isEmpty()) {
            List<XboxSubscriptionPrice> list = subscriptionsWithoutChanges.stream()
                    .sorted(Comparator.comparingInt(i -> i.getSubscription().getRegExpCode())).collect(Collectors.toList());
            Collections.reverse(list);
            return list.stream()
                    .map(price -> price.toFormattedPriceAsString() + "\n")
                    .collect(Collectors.joining());
        }
        return WARNING + " Something went wrong. Call the police !!!";

    }

    public void sendPriceChangedMessage(String price) {
        final String headerMessage = String.format("%s Price wa$ changed %s", Emoji.ROTATING_LIGHT, Emoji.ROTATING_LIGHT);
        Stream.of(getChatList().split(","))
                .forEach(user -> sendPriceMessage(user, headerMessage, price));
    }

    public List<XboxSubscriptionPrice> dailyPriceCheck() throws IOException {
        List<XboxSubscriptionPrice> golds = extractGoldPrice();
        HashMap<Subscriptions, XboxSubscriptionPrice> subscriptionsList = new HashMap<>();
        subscriptionsList.put(Subscriptions.GOLD_MONTH, golds.get(0));
        subscriptionsList.put(Subscriptions.GOLD_THREE, golds.get(1));
        subscriptionsList.put(Subscriptions.GOLD_YEAR, golds.get(2));
        subscriptionsList.put(Subscriptions.ULTIMATE, extractGameUltimatePrice());
        subscriptionsList.put(Subscriptions.GAME_PASS, extractGamePassPrice());

        subscriptionsList.keySet().forEach(subscription -> {
                    XboxSubscriptionPrice newSPrice = subscriptionsList.get(subscription);
                    Double newPrice = newSPrice.getPrice();
                    Double oldPrice = priceStorage.getPriceBySubscription(subscription).getPrice();
                    if (!newPrice.equals(oldPrice)) {
                        priceStorage.updatePrice(newSPrice);
                        if (newPrice > oldPrice) {
                            sendPriceChangedMessage(SMALL_RED_TRIANGLE + " " + subscriptionsList.get(subscription).toFormattedPriceAsString());
                        } else {
                            sendPriceChangedMessage(SMALL_RED_TRIANGLE_DOWN + " " + subscriptionsList.get(subscription).toFormattedPriceAsString());
                        }
                        subscriptionsList.remove(subscription);
                    }
                }
        );
        return new ArrayList<>(subscriptionsList.values());
    }

    void sendPriceMessage(String chatId, String header, String price) {
        try {
            File headerLogo = new File("src/main/resources/logo/default.jpg");
            SendPhoto message = new SendPhoto()
                    .setChatId(chatId)
                    .setPhoto(headerLogo)
                    .setCaption(header + "\n\n" + price)
                    .setParseMode("Markdown");

            execute(message);
            logger.info(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
