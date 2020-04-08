package bot;

import enums.Subscriptions;
import models.XboxSubscriptionPrice;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Update;
import services.PriceStorage;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static enums.Subscriptions.*;
import static utils.Emoji.*;
import static utils.XboxSubscriptionHelper.*;


public class XboxSubscriptionCheckerBot extends TelegramLongPollingBot {
    private static final Logger logger = LogManager.getLogger(XboxSubscriptionCheckerBot.class);
    private PriceStorage priceStorage = new PriceStorage();
    private static final String DEFAULT_LOGO_PATH = "src/main/resources/logo/default.jpg";
    private static final String UPDATED_LOGO_PATH = "src/main/resources/logo/updated.jpg";
    private static final String LAST_TIME_WAS_CHANGED = "\n _last time was changed: ";

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
            //String header = Arrays.asList(SimpleMessages.values()).get(new Random().nextInt(SimpleMessages.values().length)).getMessage();
            if (request.contains("gold")) {
                List<Subscriptions> goldList = Arrays.asList(GOLD_MONTH, GOLD_THREE, GOLD_YEAR);
                List<XboxSubscriptionPrice> priceList = goldList.stream().map(sub ->
                        priceStorage.getPriceBySubscription(sub)).collect(Collectors.toList());
                String message = priceList.stream().map(price -> price.toFormattedPriceAsString() + LAST_TIME_WAS_CHANGED + price.getLastUpdate() + "_ \n").collect(Collectors.joining());
                sendPricePhotoMessage(userId, message, goldList.get(1).getLogoPath());
            }
            if (request.contains("ultimate")) {
                XboxSubscriptionPrice price = priceStorage.getPriceBySubscription(ULTIMATE);
                sendPricePhotoMessage(userId, price.toFormattedPriceAsString() + LAST_TIME_WAS_CHANGED + price.getLastUpdate() + "_ ", price.getSubscription().getLogoPath());
            }
            if (request.contains("game_pass")) {
                XboxSubscriptionPrice price = priceStorage.getPriceBySubscription(GAME_PASS);
                sendPricePhotoMessage(userId, price.toFormattedPriceAsString() + LAST_TIME_WAS_CHANGED + price.getLastUpdate() + "_ ", price.getSubscription().getLogoPath());
            }
            if (request.contains("ea_access")) {
                List<Subscriptions> eaList = Arrays.asList(EA_ACCESS_MONTH, EA_ACCESS_YEAR);
                List<XboxSubscriptionPrice> priceList = eaList.stream().map(sub ->
                        priceStorage.getPriceBySubscription(sub)).collect(Collectors.toList());
                String message = priceList.stream().map(price -> price.toFormattedPriceAsString() + LAST_TIME_WAS_CHANGED + price.getLastUpdate() + "_ \n").collect(Collectors.joining());
                sendPricePhotoMessage(userId, message, eaList.get(1).getLogoPath());
            }
            if (request.contains("check")) {
                try {
                    String message = createNotUdatedSubscriptionMessage(dailyPriceCheck());
                    sendPricePhotoMessage(userId, "*SUBSCRIPTIONS LIST*" + ALIEN_EMOJI + "\n", message + "\n\\*_price for UK region in GBP_ ", "");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String createNotUdatedSubscriptionMessage(List<XboxSubscriptionPrice> subscriptionsWithoutChanges) {
        if (!subscriptionsWithoutChanges.isEmpty()) {
            List<XboxSubscriptionPrice> list = subscriptionsWithoutChanges.stream()
                    .sorted(Comparator.comparingInt(i -> i.getSubscription().getSortingValue())).collect(Collectors.toList());
            return list.stream()
                    .map(price -> price.toFormattedPriceAsString() + "\n")
                    .collect(Collectors.joining());
        }
        return WARNING + " Something went wrong. Call the police !!!";

    }

    public void sendPriceChangedMessage(String price, String logoPath) {
        Stream.of(getChatList().split(","))
                .forEach(user -> sendPricePhotoMessage(user, price, logoPath));
    }

    public List<XboxSubscriptionPrice> dailyPriceCheck() throws IOException {
        List<XboxSubscriptionPrice> golds = extractGoldPrice();
        List<XboxSubscriptionPrice> ea = extractEaAccessPrice();
        HashMap<Subscriptions, XboxSubscriptionPrice> subscriptionsList = new HashMap<>();
        subscriptionsList.put(GOLD_MONTH, golds.get(0));
        subscriptionsList.put(GOLD_THREE, golds.get(1));
        subscriptionsList.put(GOLD_YEAR, golds.get(2));
        subscriptionsList.put(ULTIMATE, extractGameUltimatePrice());
        subscriptionsList.put(GAME_PASS, extractGamePassPrice());
        subscriptionsList.put(EA_ACCESS_MONTH, ea.get(0));
        subscriptionsList.put(EA_ACCESS_YEAR, ea.get(1));


        subscriptionsList.keySet().forEach(subscription -> {
                    String date = new SimpleDateFormat("dd MMMM yyyy").format(new Date());
                    XboxSubscriptionPrice newSPrice = subscriptionsList.get(subscription);
                    Double newPrice = newSPrice.getPrice();
                    Double oldPrice = priceStorage.getPriceBySubscription(subscription).getPrice();
                    if (!newPrice.equals(oldPrice)) {
                        newSPrice.setLastUpdate(date);
                        priceStorage.updatePrice(newSPrice);
                        if (newPrice > oldPrice) {
                            sendPriceChangedMessage(SMALL_RED_TRIANGLE + " Price UP " + subscriptionsList.get(subscription).toFormattedPriceAsString(), UPDATED_LOGO_PATH);
                        } else {
                            sendPriceChangedMessage(SMALL_RED_TRIANGLE_DOWN + " Price DOWN " + subscriptionsList.get(subscription).toFormattedPriceAsString(), UPDATED_LOGO_PATH);
                        }
                        subscriptionsList.remove(subscription);
                    }
                }
        );
        return new ArrayList<>(subscriptionsList.values());
    }

    void sendPricePhotoMessage(String chatId, String header, String price, String logoPath) {
        try {
            logoPath = logoPath.isEmpty() ? DEFAULT_LOGO_PATH : logoPath;
            File headerLogo = new File(logoPath);
            SendPhoto message = new SendPhoto()
                    .setChatId(chatId)
                    .setPhoto(headerLogo)
                    .setCaption(header + "\n" + price)
                    .setParseMode("Markdown");

            execute(message);
            logger.info(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void sendPricePhotoMessage(String chatId, String price, String logoPath) {
        sendPricePhotoMessage(chatId, "", price, logoPath);
    }
}
