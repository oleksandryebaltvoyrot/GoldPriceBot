package utils;

import enums.Subscriptions;
import models.XboxSubscriptionPrice;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static enums.Subscriptions.*;


public class XboxSubscriptionHelper {
    private static final Logger logger = LogManager.getLogger(XboxSubscriptionHelper.class);
    private static String url = "https://www.xbox-now.com/en/xbox-live-gold-comparison?page=3";
    private static String urlPass = "https://www.xbox-now.com/en/game-pass-comparison?page=2";
    private static String urlUltimatePass = "https://www.xbox-now.com/en/game-pass-ultimate-comparison?page=3";
    private static final Pattern PASS_PATTERN = Pattern.compile("<span.*\">(.*)GBP</span>");
    private static OkHttpClient client = new OkHttpClient();

    private static String run(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .header("User-Agent", "Mozilla/5.0 (Linux; U; Android 2.2) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1")
                .build();
        logger.info(request);
        Response response = client.newCall(request).execute();
        String responseBody = response.body().string().trim().replace("\n", "").replace("\r", "");
        logger.info("status code " + response.code());
        return responseBody;//.length() > 350000 ? responseBody : responseBody.substring(350000);
    }

    public static List<XboxSubscriptionPrice> extractGoldPrice() throws IOException {
        String out = run(url);
        logger.info("start looking for gold");
        Pattern p = Pattern.compile("title=\"UK\".*Original Price:(.*)GBP.*Original Price:(.*)GBP.*Original Price:(.*)GBP");
        Matcher matcher = p.matcher(out);
        List<Subscriptions> goldList = Arrays.asList(GOLD_MONTH, GOLD_THREE, GOLD_YEAR);
        if (matcher.find()) {
            return goldList.stream()
                    .map(frequency -> new XboxSubscriptionPrice()
                            .setSubscriptionName(frequency)
                            .setPrice(Double.valueOf(matcher.group(frequency.getRegExpCode()).trim())))
                    .collect(Collectors.toList());
        }
        logger.info("price not found");
        return Collections.emptyList();
    }

    public static XboxSubscriptionPrice extractGameUltimatePrice() throws IOException {
        return extractGamePassPrice(ULTIMATE, urlUltimatePass);
    }

    public static XboxSubscriptionPrice extractGamePassPrice() throws IOException {
        return extractGamePassPrice(Subscriptions.GAME_PASS, urlPass);
    }

    private static XboxSubscriptionPrice extractGamePassPrice(Subscriptions subscription, String url) throws IOException {
        String out = run(url);
        logger.info("start looking for " + subscription.getSubscriptionName());
        Matcher matcher = PASS_PATTERN.matcher(out);
        if (matcher.find()) {
            return new XboxSubscriptionPrice()
                    .setSubscriptionName(subscription)
                    .setPrice(Double.valueOf(matcher.group(1).trim()));
        }
        logger.info("price not found");
        return null;
    }

    public String mapToString(Map<String, String> map) {
        return map.keySet().stream()
                .map(key -> key + "=" + map.get(key))
                .collect(Collectors.joining(", \n"));
    }


}
