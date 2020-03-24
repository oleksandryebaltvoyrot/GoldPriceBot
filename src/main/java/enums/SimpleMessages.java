package enums;

public enum SimpleMessages {
    YOUR_COLA("Your :hamburger:, standard :fries: and fresh %s"),
    YOUR_ORDER(":rocket: Your order. Free gluten %s"),
    TAKE_IT(":wave: Oh, please, take your %s"),
    DID_YOU_ORDER_THIS("Did you order this incredible %s :gift:"),
    I_DONT_THINK_YOU_HAVE_ENOUGH_MONEY("I don't think you have enough money for %s :money_mouth_face:"),
    HERE_IS_COME(":alien: Here is come...%s");
    private final String text;

    SimpleMessages(String text) {
        this.text = text;
    }

    public String getMessage() {
        return text;
    }
}
