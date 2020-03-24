package enums;

import com.vdurmont.emoji.EmojiParser;

public enum SimpleMessages {
    YOUR_COLA("Your " + EmojiParser.parseToUnicode(":hamburger:") + ", standard " + EmojiParser.parseToUnicode(":fries:") + " and fresh %s"),
    YOUR_ORDER(EmojiParser.parseToUnicode(":rocket:") + " Your order. Free gluten %s"),
    TAKE_IT(EmojiParser.parseToUnicode(":wave:") + " Oh, please, take your %s"),
    DID_YOU_ORDER_THIS("Did you order this incredible %s " + EmojiParser.parseToUnicode(":wave:")),
    I_DONT_THINK_YOU_HAVE_ENOUGH_MONEY("I don't think you have enough money for %s " + EmojiParser.parseToUnicode(":money_mouth_face:")),
    HERE_IS_COME(EmojiParser.parseToUnicode(":alien:") + " Here is come...%s");
    private final String text;

    SimpleMessages(String text) {
        this.text = text;
    }

    public String getMessage() {
        return text;
    }
}
