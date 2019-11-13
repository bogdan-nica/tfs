package lib;

import java.util.NoSuchElementException;
import java.util.regex.Pattern;

public class Utils {

    public static String repeat(String s, int n){

        return new String(new char[n]).replace("\0", s);
    }

    public static long parseLong(String text) {
        try {
                return Long.parseLong(text);
        } catch (Exception ex) {
            return -1;
        }
    }
    /**
     * this function also solves strings like "7 - 2", "7+2" "7*2" "7:2"
     * @param text
     * @return
     */
    public static int parseInt(String text) {
        try {
            //resolve 7 - 2 etc:
            if (text.matches(RegexHelper.rxMathSign)) {
                return solveMathInt(text);
            } else {
                return Integer.parseInt(text);
            }
        } catch (Exception ex) {
            return -1;
        }
    }

    private static int solveMathInt(String text) {
        String sign = RegexHelper.findString(text, RegexHelper.rxMathSign);
        if (sign.isEmpty()) return parseInt(text);

        String[] split = text.split(Pattern.quote(sign));

        if (split[0].matches(RegexHelper.rxMathSign)) {
            throw new NoSuchElementException();
        }
        int first = 0;
        if(!split[0].isEmpty()) {
            first = parseInt(split[0]);
        }
        int index = text.indexOf(sign)+ sign.length();
        String rest = text.substring(index).trim();

        switch (sign.trim()) {
            case "+":
                return first + solveMathInt(rest);
            case "-":
                return first - solveMathInt(rest);
            case "*":
                return first * solveMathInt(rest);
            case ":":
                return first / solveMathInt(rest);
            default:
                throw new NoSuchElementException();
        }
    }
}
