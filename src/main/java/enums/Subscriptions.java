package enums;

public enum Subscriptions {
    GOLD_MONTH("gold_1", 3, "src/main/resources/logo/gold.jpg"),
    GOLD_THREE("gold_3", 2, "src/main/resources/logo/gold.jpg"),
    GOLD_YEAR("gold_12", 1, "src/main/resources/logo/gold.jpg"),
    ULTIMATE("ultimate", 5, "src/main/resources/logo/ultimate.jpg"),
    GAME_PASS("game_pass", 4, "src/main/resources/logo/pass.jpg"),
    EA_ACCESS("ea_access", 6, "src/main/resources/logo/eaaccess.jpg");

    private final String dbColumnName;
    private final String logoPath;
    private final int regExpCode;

    Subscriptions(String dbColumnName, int code, String logoPath) {
        this.dbColumnName = dbColumnName;
        this.regExpCode = code;
        this.logoPath = logoPath;
    }

    public String getDBColumnName() {
        return dbColumnName;
    }

    public String getLogoPath() {
        return logoPath;
    }

    public int getRegExpCode() {
        return regExpCode;
    }
}
