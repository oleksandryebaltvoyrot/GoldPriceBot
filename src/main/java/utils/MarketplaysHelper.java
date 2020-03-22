package utils;

import enums.Frequency;
import models.XboxGoldPrice;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import services.StorageService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


public class MarketplaysHelper {
    private static final Logger logger = LogManager.getLogger(MarketplaysHelper.class);
    private static String url = "https://www.microsoft.com/en-us/p/xbox-live-gold/cfq7ttc0k5dj?activetab=pivot%3aoverviewtab";
    private static OkHttpClient client = new OkHttpClient();
    private static String run(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .header("User-Agent", "PostmanRuntime/7.15.2")
                .header("content-type", "text/html; charset=utf-8")
                .build();

        Response response = client.newCall(request).execute();
        String responseBody = response.body().string();
        logger.info(responseBody.trim().substring(0,100));
        return responseBody;
    }

    private static String extractAllGroups(String text, Frequency pattern) throws InterruptedException {
        logger.info("start looking for {}", pattern.name());
        Pattern p = Pattern.compile(pattern.getRegexp());
        Matcher matcher = p.matcher(text);
        if (matcher.find()) {
            return matcher.group(1);
        }
        Thread.sleep(1000);
        collectInfo();
        logger.info("{}::price not found", pattern.name());
        return "error";
    }

    public static List<XboxGoldPrice> collectInfo() {
        List<XboxGoldPrice> map = new ArrayList<>();
        logger.info("start collecting info");
        try {
            String out = run(url);
            if (out.length() > 350000) {
                out = out.substring(350000);
            } else {
                collectInfo();
            }
            for (Frequency frq : Frequency.values()) {
                map.add(new XboxGoldPrice()
                        .setFrequency(frq.name())
                        .setPrice(extractAllGroups(out, frq)));
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return map;
    }

    public String mapToString(Map<String, String> map) {
        return map.keySet().stream()
                .map(key -> key + "=" + map.get(key))
                .collect(Collectors.joining(", \n"));
    }

    public static void storePrice(List<XboxGoldPrice> list) {
        list.forEach(StorageService::addPriceToStorage);
    }

}
