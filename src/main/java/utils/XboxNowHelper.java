package utils;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import enums.Frequency;
import models.XboxGoldPrice;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import services.StorageService;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


public class XboxNowHelper {
    private static final Logger logger = LogManager.getLogger(XboxNowHelper.class);
    private static String url = "https://www.xbox-now.com/en/xbox-live-gold-comparison?page=2";
    private static OkHttpClient client = new OkHttpClient();

    private static String run(String url) throws IOException, URISyntaxException, UnirestException {

        Request request = new Request.Builder()
                .url(url)
                .header("User-Agent", "Mozilla/5.0 (Linux; U; Android 2.2) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1")
                .build();
        logger.info(request);
        Response response = client.newCall(request).execute();
        String responseBody = response.body().string();
        logger.info(responseBody.trim().substring(0, 100));
        return responseBody;
    }

    private static String extractAllGroups(String text, Frequency pattern) throws InterruptedException {
        logger.info("start looking for {}", pattern.name());
        Pattern p = Pattern.compile("Original Price:(.)GBP");
        Matcher matcher = p.matcher(text);
        if (matcher.find()) {
            return matcher.group(pattern.getRegexp());
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
        } catch (IOException | InterruptedException | URISyntaxException | UnirestException e) {
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
