package enums;

public enum Subscriptions {
    GOLD_MONTH("gold_1", 3),
    GOLD_THREE("gold_3", 2),
    GOLD_YEAR("gold_12", 1),
    ULTIMATE("ultimate", 1),
    GAME_PASS("game_pass", 1);

    private final String dbColumnName;
    private final int regExpCode;

    Subscriptions(String dbColumnName, int code) {
        this.dbColumnName = dbColumnName;
        this.regExpCode = code;
    }

    public String getDBColumnName() {
        return dbColumnName;
    }

    public int getRegExpCode() {
        return regExpCode;
    }
}
