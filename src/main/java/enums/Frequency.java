package enums;

public enum Frequency {
    MONTH(".*\"CurrentWithTax\":\"(.*)\\/month\",\"Sku\".*"),
    THREE(".*\"CurrentWithTax\":\"(.*)\\/quarterly\",\"Sku\".*"),
    YEAR1(".*\"CurrentWithTax\":\"(.*)\\/year\",\"Sku\".*");

    private final String regexp;

    Frequency(String regexp) {
        this.regexp = regexp;
    }

    public String getRegexp() {
        return regexp;
    }
}
