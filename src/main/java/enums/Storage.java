package enums;

public enum Storage {
    GOLD_FILE_PATH("src/main/resources/gold_storage.txt", "gold"),
    PASS_FILE_PATH("src/main/resources/pass_storage.txt", "game_pass"),
    ULTIMATE_FILE_PATH("src/main/resources/ultimate_storage.txt", "ultimate");
    private final String storagePath;
    private final String storageName;

    Storage(String storagePath, String storageName) {
        this.storagePath = storagePath;
        this.storageName = storageName;
    }

    public String getStoragePath() {
        return storagePath;
    }

    public String getStorageName() {
        return storageName;
    }
}
