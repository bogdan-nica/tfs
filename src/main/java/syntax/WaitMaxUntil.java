package syntax;

import lib.Common;
import lib.NowHelper;

public class WaitMaxUntil {

    private long max = -1;
    private String until = "";
    private boolean isSet = false;

    public Long getMax(){return max;}
    public String getUntil(){return until;}
    public boolean getIsSet(){return isSet;}

    /**
     * handles syntax of type:
     * 20 sec, 1sec, 500, 200000 (when the unit is not mentioned
     * the long is assumed to be millisecond
     * or
     * MAX 20sec,UNTIL xpath
     * @param instruct
     */
    public WaitMaxUntil(String instruct){
        if(!instruct.isEmpty()) {
            String[] split = Common.exceptCharsInXpath(instruct).split(",");
            if (split.length >= 2) {
                long val = NowHelper.getMillisec(Common.restoreExceptedChars(split[0].replace("MAX", "").trim()));
                String str = Common.restoreExceptedChars(split[1].trim());
                if (val > 0 && str.startsWith("UNTIL ")) {
                    max = val;
                    until = str.replace("UNTIL ", "").trim();
                }
                isSet = true;
            } else if (split.length == 1) {
                max = NowHelper.getMillisec(Common.restoreExceptedChars(split[0].trim()));
                isSet = true;
            }
        }
    }
}
