package enums;

import static utils.Emoji.*;

public enum SimpleMessages {
    YOUR_COLA("Your " + HAMBURGER_EMOJI + ", standard " + FRIES_EMOJI + " and fresh %s"),
    YOUR_ORDER(ROCKET_EMOJI + " Your order. Free gluten %s"),
    TAKE_IT(WAVE_EMOJI + " Oh, please, take your %s"),
    DID_YOU_ORDER_THIS("Did you order this incredible %s " + HAMBURGER_EMOJI),
    I_DONT_THINK_YOU_HAVE_ENOUGH_MONEY("I don't think you have enough money for %s " + DOLLAR_EMOJI),
    HERE_IS_COME(ALIEN_EMOJI + " Here is come...%s");
    private final String text;

    SimpleMessages(String text) {
        this.text = text;
    }

    public String getMessage() {
        return text;
    }
}
