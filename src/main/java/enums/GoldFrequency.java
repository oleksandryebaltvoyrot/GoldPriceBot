package enums;

public enum GoldFrequency {
    MONTH(3),
    THREE(2),
    YEAR1(1);

    private final int regexp;

    GoldFrequency(int regexp) {
        this.regexp = regexp;
    }

    public int getRegexp() {
        return regexp;
    }
}
