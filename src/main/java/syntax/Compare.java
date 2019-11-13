package syntax;

import atfs.ui.Ui;
import atfs.ui.UiHelper;
import interfaces.iTfs;
import lib.Common;
import lib.RegexHelper;
import lib.Utils;

public class Compare {
    /**
     * in:      IF ${json.user.profile}.avatar == EMPTY THEN
     * condition   ${json.user.profile}.avatar == EMPTY
     * finds sign and 2 parts:
     * [${json.user.profile}.avatar] [==] [EMPTY]
     * and compare the 2 sides
     *
     *
     * TODO: refactor code for input like:
     *       cond AND cond1 OR cond 3AND cond4
     *       it should accept any combination of AND OR
     *       without affecting load performance
     *       at this time below accepts only one cond AND/OR cond1
     *
     * @param atf
     * @param cond
     * @return
     */
    public static boolean analiseCondition(iTfs atf, String cond) {
        if (cond.isEmpty()) return true;

        StringAndKeys sk = new StringAndKeys(cond);
        if(sk.getKeys().length>0){
            for(String key : sk.getKeys()){
                switch(key){
                    case FeedData.VISIBLE:
                        if(atf instanceof Ui) {
                            ((Ui)atf).setIsVisible(true);
                        }
                        break;
                }
            }
        }

        String[] conditions = new String[]{sk.getString()};
        String andOr = "";
        String orSplitter = " OR ", andSplitter = " AND ";
        if (sk.getString().contains(orSplitter)) {
            andOr = "OR";
            conditions = sk.getString().split(orSplitter);
        } else if (sk.getString().contains(andSplitter)) {
            andOr = "AND";
            conditions = sk.getString().split(andSplitter);
        }

        boolean res = false;
        for (String condpart : conditions) {
            //TODO: in case of AND OR the result of each comparison must be captured
            return parseCondition(condpart, atf);
        }
        return res;
    }

    private static boolean parseCondition(String condition, iTfs atf) {
        String[] found = RegexHelper.findAllStrings(condition, RegexHelper.rxFeedIfElseCondition);

        if (found.length != 4) {
            throw new CannotCompare(condition);
        }

        String first = atf.replaceKeys(found[1]).trim();
        String operator = found[2].trim();
        String second = atf.replaceKeys(found[3]).trim();
        return isCondition(first, operator, second, atf);
    }

    private static boolean isCondition(String first, String operator, String second, iTfs tfs) {
        switch (operator) {
            case "!=":
                int frst = Utils.parseInt(first);
                if (frst >= 0) return frst != Utils.parseInt(second);
                else return !first.equals(second);
            case "==":
                frst = Utils.parseInt(first);
                if (frst >= 0) return frst == Utils.parseInt(second);
                else return first.equals(second);
            case "CONTAINS":
                if(tfs instanceof Ui){
                    return UiHelper.compareThis(first, operator, second,
                            ((Ui)tfs).getDriver(),
                            ((Ui)tfs).getIsVisible(),
                            ((Ui)tfs).getIsParent()).IsPassed();
                } else return first.contains(second);
            case "!CONTAINS":
                if(tfs instanceof Ui){
                    return !(UiHelper.compareThis(first, operator, second,
                            ((Ui)tfs).getDriver(),
                            ((Ui)tfs).getIsVisible(),
                            ((Ui)tfs).getIsParent()).IsPassed());
                } else return !first.contains(second);
            case ">=":
            case ">":
            case "<=":
            case "<":
                return compareIntegers(first, operator, second);
            default:
                String msg = "Cannot validate |" + first + "|" + operator + "|" + second + "|";
                throw new CannotCompare(msg);
        }
    }

    /**
     * compares only if first and second can be parsed in pozitive integers
     * @param first
     * @param operator
     * @param second
     * @return
     */
    private static boolean compareIntegers(String first, String operator, String second){
        int frst = Utils.parseInt(first);
        String msg = "";
        if (frst >= 0) {
            int scnd = Utils.parseInt(second);
            if(scnd >=0) {
                switch(operator){
                    case "<=":
                        return frst <= scnd;
                    case ">=":
                        return frst >= scnd;
                    case ">":
                        return frst>scnd;
                    case "<":
                        return frst<scnd;
                    default:
                        msg ="CANNOT COMPARE USING: " + operator + "!!!";
                        throw new CannotCompare(msg);
                }
            } else msg = "CANNOT COMPARE using '" + operator + "'. Second member is not a positive integer!!!"
                    + Common.LF + "PHRASE: " + first + " " + operator + " " + second;
        }
        else  msg = msg = "CANNOT COMPARE using '" + operator + "'. First member is not a positive integer!!!"
                + Common.LF + "PHRASE: " + first + " " + operator + " " + second;
        throw new CannotCompare(msg);
    }

}
