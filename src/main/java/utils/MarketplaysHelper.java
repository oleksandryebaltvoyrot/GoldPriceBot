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


public class MarketplaysHelper {
    private static final Logger logger = LogManager.getLogger(MarketplaysHelper.class);
    private static String url = "https://www.xbox.com/en-US/xbox-game-pass#join";
    private static OkHttpClient client = new OkHttpClient();

    private static String run(String url) throws IOException, URISyntaxException, UnirestException {

        HttpResponse<String> response1 = Unirest.get(url)
                .header("cookie", "ONERFSSO=1; MC1=GUID=14312492841d4a06ae481a27254764f5&HASH=1431&LV=202003&V=4&LU=1584181784716; akacd_OneRF=1591957787~rv=9~id=5a1969ee90b3494ff86b55fdd8b6fe75; isFirstSession=1; MSFPC=GUID=14312492841d4a06ae481a27254764f5&HASH=1431&LV=202003&V=4&LU=1584181784716; MUID=2735EAA8D8876F503C92E709DC876CD3; uhf_hide_epb=true; __RequestVerificationToken=L2bcLADm-m1HFJJRXVJKfEdblvvqsfmmoGqIX0MiJxoPYVkLOfQWqFBJSYCZBqfsCiHCzeTE-iNGemdIrkr1Je2XO0M1; IR_gbd=microsoft.com; AAMC_mscom_0=REGION%7C6; aam_uuid=19191293108100985973179304635328162403; ANON=A=316E6C96D0977FBC14253972FFFFFFFF&E=17d1&W=1; NAP=V=1.9&E=1777&C=Qm4Jox_qpiasAyE1vwsDDB6aOxB39wKoqfHZUGyPMk2zkCYPyQkZZw&W=1; IR_PI=e1d14c48-669d-11ea-beac-062af258c8f2%7C1584350287095; WRUIDCD03072018=2690146101854315; ClicktaleReplayLink=https://dmz01.app.clicktale.com/Player.aspx?PID=1009&UID=2690146101854315&SID=2690146101854315; ANON=A=316E6C96D0977FBC14253972FFFFFFFF&E=17d1&W=1; NAP=V=1.9&E=1777&C=Qm4Jox_qpiasAyE1vwsDDB6aOxB39wKoqfHZUGyPMk2zkCYPyQkZZw&W=1; IR_7811=1584608294290%7C0%7C1584608201692%7C%7C; market=RU; optimizelyEndUserId=oeu1584612410004r0.8718726592539667; recentlyShownDialog=1; emailNewsletterDialogShown=1; fptctx2=H3ihr9e92IdW6yd1ZgQ9S04xKNnhrQdHdluvYU%252bLEnQ4wWPRZeXFucMAQ6gnoltHT47ycebwkgKlg0g1MadXfjEFWcWegCF3b5icRxGwsV1qtAKMw1Knn8nHhjCVkmctamaMDF21Un4rnQNXCL6jCeouvoV%252bSKRL3Q0%252bT%252bAhcMhTsLBPugCx1ndp9LgLSL6IYogVteOj4096QbFZBZIVUWRu8e6foVHgou79fW%252fra5doi6Vsb4QWF9IagNzJ2sqko84rwK91ehNSZBnHzSm5TxYUQrZkcOMwvYPHahYaS00fKSx0LOO0jZINfZfnZ%252bIg; MSCC=1584888504; _ga=GA1.2.1180298000.1584888538; _gid=GA1.2.345003494.1584888538; ctm={'pgv':1430382904641559|'vst':5991336190647398|'vstr':5735653489049884|'intr':1584888595196|'v':1}; check=true; mbox=session#7624ded06ebe4f02ad6560754320625c#1584952363|PC#7624ded06ebe4f02ad6560754320625c.26_0#1648195304; mboxEdgeCluster=26; MS0=cf699f27efa0480b9ba68058073e56f3; ONERFSSO=1; AMCVS_EA76ADE95776D2EC7F000101%40AdobeOrg=1; AMCV_EA76ADE95776D2EC7F000101%40AdobeOrg=-894706358%7CMCIDTS%7C18345%7CMCMID%7C18752251913224608423223194321100407660%7CMCAAMLH-1585555303%7C6%7CMCAAMB-1585555303%7CRKhpRz8krg2tLO6pguXWp5olkAcUniQYPHaMWWgdJ3xzPWQmdj0y%7CMCCIDH%7C-1247996913%7CMCOPTOUT-1584957703s%7CNONE%7CvVersion%7C2.3.0; WRIgnore=true; X-FD-FEATURES=ids=sfwaaa%2c2662t1%2c20349050c%2c1951t2%2c2424c%2c1674t2row2%2ctasmigration010%2ccartemberpl%2cdisablenorefunds%2cdaconvertenabled&imp=ebd85846-5756-4df8-b4df-c60a4a4a2717; X-FD-Time=1; mslocale={'r':'1'|'u':'en-us'}; MS-CV=Dngh3GuDs0WfNXeW.1; IR_7593=1584952074628%7C0%7C1584950761184%7C%7C; __CT_Data=gpv=5&ckp=cd&dm=www.microsoft.com&apv_1022_www32=3&cpv_1022_www32=3&apv_1009_www32=2&cpv_1009_www32=2&rpv_1009_www32=2; ctm=eydwZ3YnOjE5MjQ4Mzk5MzIxMjQ3MDN8J3ZzdCc6JzE5NTA5ODYyMDE5NzI1Nyd8J3ZzdHInOic0NzY4NzAzMTE1ODU1MzM3J3wnaW50cic6MTU4NDk1MjA3NDg0Nnwndic6MX0=; graceIncr=0")
                .header("User-Agent", "Mozilla/5.0 (Linux; U; Android 2.2) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1")
                .asString();


        logger.info("Unirest : "+response1.getBody().trim());
        Request request = new Request.Builder()
                .url(encodeUrl(url))
                .header("User-Agent", "Mozilla/5.0 (Linux; U; Android 2.2) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1")
                .build();
        logger.info(request);
        Response response = client.newCall(request).execute();
        String responseBody = response.body().string();
        logger.info(responseBody.trim().substring(0, 100));
        return responseBody;
    }

    static String encodeUrl(String urlToEncode) throws URISyntaxException {
        return new URI(urlToEncode).toASCIIString();
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
