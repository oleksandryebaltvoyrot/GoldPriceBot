package enums;

public enum Storage {
    GOLD_FILE_PATH("src/main/resources/gold_storage.txt"),
    PASS_FILE_PATH("src/main/resources/pass_storage.txt"),
    ULTIMATE_FILE_PATH("src/main/resources/ultimate_storage.txt");
    private final String path;

    Storage(String regexp) {
        this.path = regexp;
    }

    public String getPath() {
        return path;
    }
}
