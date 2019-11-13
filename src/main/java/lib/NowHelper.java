package lib;

public class NowHelper {
    public static long getMillisec(String value) {
        long ret = 0;
        if ((ret = Utils.parseLong(value)) >= 0) return ret;
        else ret = 0;

        String[] found = RegexHelper.findAllStrings(value, RegexHelper.rxGetMilisecSyntax);
        if (found.length == 3) {
            String unit = found[2];
            String val = found[1];
            long lval =Utils.parseLong(val);
            if (lval >= 0) {
                switch (unit.toLowerCase()) {
                    case "milli":
                        return lval;
                    case "sec":
                        return lval * 1000;
                    case "min":
                        return lval * 60 * 1000;
                    case "hour":
                        return lval * 3600 * 1000;
                    case "day":
                    case "days":
                        return lval * 24 * 3600 * 1000;
                }
            }
        }
        return ret;
    }


}
