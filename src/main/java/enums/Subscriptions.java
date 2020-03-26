package enums;

public enum Subscriptions {
    GOLD_MONTH("gold_1", 3),
    GOLD_THREE("gold_3", 2),
    GOLD_YEAR("gold_12", 1),
    ULTIMATE("ultimate", 1),
    GAME_PASS("game_pass", 1);

    private final String name;
    private final int regExpCode;

    Subscriptions(String name, int code) {
        this.name = name;
        this.regExpCode = code;
    }

    public String getSubscriptionName() {
        return name;
    }

    public int getRegExpCode() {
        return regExpCode;
    }
}
