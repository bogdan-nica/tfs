package lib;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexHelper {

    public static final String rxMathSign = ".*?([\\s-+*:]+).+";

    public static final String rxFeedSyntaxAll = "^([A-Z]+[A-Z_]+)\\s+(.+)[|](.+)[?](.*?)$";
    public static final String rxFeedSyntaxNoInstruct = "^([A-Z]+[A-Z_]+)\\s+(.+)[|](.+?)$";
    public static final String rxFeedSyntaxNoCondition = "^([A-Z]+[A-Z_]+)\\s+(.+)[?](.+?)$";
    public static final String rxFeedSyntaxCommandData = "^([A-Z]+[A-Z_]+)\\s+(.*?)$";

    public static final String rxJustComparer = "(!CONTAINS|CONTAINS|==|!=|>=|<=|>|<)";
    public static final String rxBy = "[@a-zA-Z0-9_()]+[=]['\"].+['\"]";
    public static final String rxFeedIfElseCondition = "(.+?)\\s*" + rxJustComparer + "\\s*(.+)$";
    public static final String rxAssertXpath = "(THIS|//[*a-zA-Z0-9]+\\[.*\\])\\s+" +
            rxJustComparer + "\\s+(" + rxBy + "|//[*a-zA-Z0-9=]+\\[.*\\]|[a-zA-Z0-9_]+)";
    public static final String rxXpath = "//([*a-zA-Z0-9]+\\[.*\\])$";
    public static final String rxContainsXpath = ".*?//([*a-zA-Z0-9]+\\[.*\\]).*$";
    public static final String rxElemType = "^(.+?)(\\s*[=]\\s*.+)";

    public static final String rxDollNotation = "([a-zA-Z0-9.]+)";
    public static final String rxDollSyntax = "\\$\\{[a-zA-Z0-9.]+\\}";
    /**
     * finds
     * ex:
     * some text in front:${(api.user.01).${email}}.;content:${caps.regex}rxVerficationLink(${(api.user.01).${user.id}})
     * ${(api.user.01).${email}}
     */
    public static final String rxUserObjectDollSyntax = "(\\$\\{\\([A-Za-z0-9.]+\\)([.]" + rxDollSyntax + ")+\\})";
    public static final String rxUserKeyDoll = "\\$\\{\\(([a-zA-Z0-9.]+)\\).+";
    public static final String rxArrayDollsSyntax = "(\\$\\{[a-zA-Z0-9.]+([\\[][A-Za-z0-9_\\s]+[\\]])+\\})";
    public static final String rxArrayDollsSyntax2 = "(\\$\\{([a-zA-Z0-9.]+)[\\[]([0-9\\s-+*:]+)[\\]][\\.]([a-zA-Z0-9._]+)\\})";
    public static final String rxNowDollSyntax = "(\\$\\{now.[a-zA-Z0-9.]+\\s[+-]\\d+[A-Za-z]\\})";
    public static final String rxNowDollExtract = "^(\\$\\{)(now.[a-zA-Z0-9.]+\\s[+-]\\d+[A-Za-z])(\\})";
    public static final String rxGetMilisecSyntax = "([0-9]+)\\s*([A-Za-z]+)";

    public static final String rxArrayAllDollsSyntax =
            "(\\$\\{([a-zA-Z0-9.]+)[\\[]([0-9\\s-+*:]+)[\\]][\\[]([A-Za-z0-9\\s_]+)[\\]]\\})" +         //${sql.json.data[0][last_name_tx]}
                    "|(\\$\\{([a-zA-Z0-9.]+)[\\[]([0-9\\s-+*:]+)[\\]][\\[]\\$\\{([a-zA-Z0-9._]+)\\}[\\]]\\})" +  //${sql.json.data[0][${data.field}]}
                    "|(\\$\\{([a-zA-Z0-9.]+)[\\[]([0-9\\s-+*:]+)[\\]][\\.]\\$\\{([a-zA-Z0-9._]+)\\}\\})" +       //${sql.json.data[0].${data.field}}
                    "|(\\$\\{([a-zA-Z0-9.]+)[\\[]([0-9\\s-+*:]+)[\\]][\\.]([a-zA-Z0-9._]+)\\})";                 //${sql.json.customer[0].customer_id}
    public static final String rxArrayElemDollsSyntax = "(\\$\\{([a-zA-Z0-9.]+)[\\[]([0-9\\s-+*:]+)[\\]]\\})";

    public static String rxFirstDollName = "([A-Za-z0-9]+)([\\.].*|$)";
    public static final String rxHeaderKey = "([A-Za-z-_]+[:] )+";

    public static String findDolls(String text) {
        String dolls = findString(text, "^(" + rxDollSyntax + ")");//"^(\\$\\{[a-zA-Z0-9.]+\\s*[\\+0-9A-Za-z]*?\\})");//
        String dolls1 = findString(text, "^(.*?)(" + rxDollSyntax + ")(.*)", 2);
        String dolls2 = findString(text, rxNowDollSyntax);
        String dolls3 = findString(text, rxArrayDollsSyntax);
        String dolls4 = findString(text, "^(.*?)(" + rxArrayAllDollsSyntax + ")(.*)", 2);

        return dolls.isEmpty() || dolls.length() < 4 ? dolls1.length() < 4 ? dolls2.length() < 4 ? dolls3.length() < 4 ? dolls4.length() < 4 ? "" : dolls4 : dolls3 : dolls2 : dolls1 : dolls;
    }

    public static String extractDollName(String text) {
        String doll = findString(text, "^(\\$\\{)([a-zA-Z0-9.]+)(\\})", 2);
        String doll1 = findString(text, "^(.*?)(\\$\\{)([a-zA-Z0-9.]+)(\\})(.*)", 3);
        String doll2 = findString(text, rxNowDollExtract, 2);

        return doll.isEmpty() || doll.length() < 2 ? doll1.length() < 2 ? doll2.length() < 2 ? text : doll2 : doll1 : doll;
    }

    /**
    if not ${notation} returns string
    If it finds ${first.second.etc} it returns first
     */
    public static String findFirstInDollName(String dollCandidate) {
        if (findDolls(dollCandidate).isEmpty()) return dollCandidate;

        return RegexHelper.findString(extractDollName(dollCandidate),
                rxFirstDollName);
    }

    public static String rxElemValue(String key) {
        return "(" + key + "\\s*[=]\\s*[\"'])(.+?)([\"']|$)";
    }

    public static String[] findAllStrings(String text, String regex) {
        return findAllStrings(text, regex, Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
    }

    public static String[] findAllStrings(String text, String regex, int flags) {
        try {
            Pattern ptrn = Pattern.compile(regex, flags);
            Matcher matcher = ptrn.matcher(text);
            if (matcher.find()) {
                if (matcher.groupCount() > 0) {
                    int max = matcher.groupCount();
                    List<String> res = new ArrayList();
                    for (int i = 0; i <= max; i++) {
                        if (matcher.group(i) == null) continue;

                        res.add(matcher.group(i));
                    }
                    String[] resp = new String[res.size()];
                    return res.toArray(resp);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new String[0];
    }

    public static String findString(String text, String regex) {
        return findString(text, regex, 1,
                Pattern.MULTILINE);
    }

    public static String findString(String text, String regex, int groupindex) {
        return findString(text, regex, groupindex,
                Pattern.MULTILINE);
    }

    public static String findString(String text, String regex, int groupindex, int flags) {
        try {
            Pattern ptrn = Pattern.compile(regex, flags);
            Matcher matcher = ptrn.matcher(text);

            if (matcher.find()) {
                int grupcount = matcher.groupCount();
                if (grupcount >= groupindex) {
                    //NOTE: group 0 always returns all string
                    return matcher.group(groupindex);
                }
            }
        } catch (Exception ex) {
        }
        return "";
    }

}
