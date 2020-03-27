package models;

import enums.Subscriptions;

import java.util.Objects;

import static utils.Emoji.HEAVY_MULTIPLICATION_X;
import static utils.Emoji.POUND;

public class XboxSubscriptionPrice {
    private Subscriptions subscriptionName;
    private Double price;

    public Subscriptions getSubscription() {
        return subscriptionName;
    }

    public XboxSubscriptionPrice setSubscription(Subscriptions subscriptionName) {
        this.subscriptionName = subscriptionName;
        return this;
    }

    public Double getPrice() {
        return price;
    }

    public XboxSubscriptionPrice setPrice(Double price) {
        this.price = price;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof XboxSubscriptionPrice)) return false;
        XboxSubscriptionPrice that = (XboxSubscriptionPrice) o;
        return Objects.equals(getSubscription(), that.getSubscription()) &&
                Objects.equals(getPrice(), that.getPrice());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSubscription(), getPrice());
    }

    @Override
    public String toString() {
        return String.format("%s::%s", subscriptionName, price);
    }

    public String toFormattedPriceAsString(int nameWidth, int priceWidth) {
        return HEAVY_MULTIPLICATION_X
                + " | " + subscriptionName.name().replace("_"," ")
                + " | " + HEAVY_MULTIPLICATION_X
                + " | _" + price
                + "_ | " + POUND;
    }

    public String toFormattedPriceAsString() {
        return toFormattedPriceAsString(subscriptionName.name().length(), price.toString().length());
    }

}
