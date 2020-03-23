package utils;

import com.mashape.unirest.http.exceptions.UnirestException;
import enums.XboxNowFrequency;
import models.XboxGoldPrice;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import services.StorageService;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;


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
        logger.info(responseBody.trim().substring(0, 300));
        return responseBody;
    }

    private static List<XboxGoldPrice> extractAllGroups(String text) {
        logger.info("start looking for");
        Pattern p = Pattern.compile("title=\"UK\".*Original Price:(.*)GBP.*Original Price:(.*)GBP.*Original Price:(.*)GBP");
        Matcher matcher = p.matcher(text.trim().replace("\n", "").replace("\r", ""));
        if (matcher.find()) {
            return Stream.of(XboxNowFrequency.values())
                    .map(frequency -> new XboxGoldPrice()
                            .setFrequency(frequency.name())
                            .setPrice(matcher.group(frequency.getRegexp()).trim()+"-GPB"))
                    .collect(Collectors.toList());
        }
        logger.info("price not found");
        return null;
    }

    public static List<XboxGoldPrice> collectInfo() {
        logger.info("start collecting info");
        try {
            String out = run(url);
            if (out.length() > 350000) {
                out = out.substring(350000);
            }
            return extractAllGroups(out);
        } catch (IOException | URISyntaxException | UnirestException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String mapToString(Map<String, String> map) {
        return map.keySet().stream()
                .map(key -> key + "=" + map.get(key))
                .collect(Collectors.joining(", \n"));
    }


}
