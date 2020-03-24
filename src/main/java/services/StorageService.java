package services;


import com.vdurmont.emoji.EmojiParser;
import enums.Storage;
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

import static enums.Storage.GOLD_FILE_PATH;

public class StorageService {
    private static final Logger LOGGER = LogManager.getLogger(StorageService.class);
    private static final Pattern PRICE_PATTERN = Pattern.compile("(\\S+)::(\\S+)");

    public static void addPriceToStorage(XboxGoldPrice user, String storage) {
        try {
            FileWriter fw = new FileWriter(storage, true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(user.toString());
            bw.newLine();
            bw.close();
            LOGGER.info("Price {} added", user.toString());
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
    }

    public static List<XboxGoldPrice> getPriceFromStorage(Storage storage) {
        List<XboxGoldPrice> users = new ArrayList<>();
        File file = new File(storage.getStoragePath());
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
        File file = new File(GOLD_FILE_PATH.getStoragePath());
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

    public static String getFormattedPriceAsString(Storage storage) {
        File file = new File(storage.getStoragePath());
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
        return out.toString()
                .replace("::", String.format(" %s ", EmojiParser.parseToUnicode(":heavy_multiplication_x:")))
                .replace("-GBP", String.format(" %s", EmojiParser.parseToUnicode(":pound:")));
    }

    public static void cleanUpStorage(Storage storage) {
        try {
            FileChannel.open(Paths.get(storage.getStoragePath()), StandardOpenOption.WRITE).truncate(0).close();
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
    }

    public static void storePrice(List<XboxGoldPrice> list, Storage storage) {
        list.forEach(goldPrice -> addPriceToStorage(goldPrice, storage.getStoragePath()));
    }
}
