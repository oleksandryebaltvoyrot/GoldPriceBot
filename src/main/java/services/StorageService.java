package services;


import com.vdurmont.emoji.EmojiParser;
import models.XboxGoldPrice;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StorageService {
    private static final Logger LOGGER = LogManager.getLogger(StorageService.class);

    private static final String FILE_PATH = "src/main/resources/storage.txt";
    private static final Pattern PRICE_PATTERN = Pattern.compile("(\\S+)::(\\S+)");

    public static void addPriceToStorage(XboxGoldPrice user) {
        try {
            FileWriter fw = new FileWriter(FILE_PATH, true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(user.toString());
            bw.newLine();
            bw.close();
            LOGGER.info("Price {} added", user.toString());
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    public static List<XboxGoldPrice> getPriceFromStorage() {
        List<XboxGoldPrice> users = new ArrayList<>();
        File file = new File(FILE_PATH);
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String st;
            while ((st = br.readLine()) != null) {
                Matcher match = PRICE_PATTERN.matcher(st);
                if (match.matches()) {
                    users.add(new XboxGoldPrice()
                            .setPrice(match.group(2))
                            .setFrequency(match.group(1)));
                }
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
        return users;
    }

    public static String getStoredGoldPriceAsString() {
        File file = new File(FILE_PATH);
        StringBuilder out = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String st;
            while ((st = br.readLine()) != null) {
                out.append(st);
                out.append("\n");
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
        return out.toString();
    }

    public static String getFormattedStoredGoldPriceAsString() {
        File file = new File(FILE_PATH);
        StringBuilder out = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String st;
            while ((st = br.readLine()) != null) {
                out.append(EmojiParser.parseToUnicode(":white_check_mark:")).append(st);
                out.append("\n");
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
        return out.toString().replace("::", EmojiParser.parseToUnicode(":heavy_multiplication_x:"));
    }

    public static void cleanUpStorage() {
        try {
            FileChannel.open(Paths.get(FILE_PATH), StandardOpenOption.WRITE).truncate(0).close();
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
    }

    public static void storePrice(List<XboxGoldPrice> list) {
        list.forEach(StorageService::addPriceToStorage);
    }
}
