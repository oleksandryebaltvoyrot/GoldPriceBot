package enums;

public enum XboxNowFrequency {
    MONTH(3),
    THREE(2),
    YEAR1(1);

    private final int regexp;

    XboxNowFrequency(int regexp) {
        this.regexp = regexp;
    }

    public int getRegexp() {
        return regexp;
    }
}
