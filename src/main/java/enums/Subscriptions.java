package enums;

public enum Subscriptions {
    GOLD_MONTH("gold_1", 1),
    GOLD_THREE("gold_3", 2),
    GOLD_YEAR("gold_12", 3),
    ULTIMATE("ultimate", 1),
    GAME_PASS("game_pass", 1);

    private final String name;
    private final int frequency;

    Subscriptions(String name, int frequency) {
        this.name = name;
        this.frequency = frequency;
    }

    public String getSubscriptionName() {
        return name;
    }

    public int getFrequency() {
        return frequency;
    }
}
