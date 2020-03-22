package models;

import java.util.Objects;

public class XboxGoldPrice {
    private String frequency;
    private String price;

    public String getFrequency() {
        return frequency;
    }

    public XboxGoldPrice setFrequency(String frequency) {
        this.frequency = frequency;
        return this;
    }

    public String getPrice() {
        return price;
    }

    public XboxGoldPrice setPrice(String price) {
        this.price = price;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof XboxGoldPrice)) return false;
        XboxGoldPrice that = (XboxGoldPrice) o;
        return Objects.equals(getFrequency(), that.getFrequency()) &&
                Objects.equals(getPrice(), that.getPrice());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFrequency(), getPrice());
    }

    @Override
    public String toString() {
        return String.format("%s::%s", frequency, price);
    }
}
