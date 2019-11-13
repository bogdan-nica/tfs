package syntax;

import lib.Common;
import lib.RegexHelper;

public class FeedData {


    public static final String  ASYNC   = "ASYNC",
                                CLEAR   = "CLEAR",
                                PARENT  = "PARENT",
                                SYNC    = "SYNC",
                                VISIBLE = "VISIBLE";

    public FeedData(String allLine){
        if(allLine.isEmpty())throw new TfsSyntaxError("SYNTAX ERROR: test line cannot be empty");

        line = allLine;
        line = Common.replaceExceptedChars(allLine);
        validate();
    }

    private String line = "";
    public void setLine(String line) {
        this.line = line;
    }
    public String getLine() {
        return line;
    }

    private String command;
    public void setCommand(String command) {
        this.command = command;
    }
    public String getCommand() {
        return command;
    }

    private String data = "";
    public void setData(String data) {
        this.data = data;
    }
    public String getData() {
        return data;
    }

    private String condition = "";
    public void setCondition(String condition) {
        this.condition = condition;
    }
    public String getCondition() {
        return condition;
    }

    private String instruct = "";
    public void setInstruct(String instruct) {
        this.instruct = instruct;
    }
    public String getInstruct() {
        return instruct;
    }

    private void validate() {
        String regex = RegexHelper.rxFeedSyntaxAll;
        String[] found = RegexHelper.findAllStrings(line, regex);
        if (found.length == 5) {
            command = found[1];
            data = found[2];
            condition = found[3];
            instruct = found[4];
            return;
        } else {
            regex = RegexHelper.rxFeedSyntaxNoCondition;
            found = RegexHelper.findAllStrings(line, regex);
            if (found.length == 4) {
                command = found[1];
                data = found[2];
                instruct = found[3];
                return;
            } else {
                regex = RegexHelper.rxFeedSyntaxNoInstruct;
                found = RegexHelper.findAllStrings(line, regex);
                if (found.length == 4) {
                    command = found[1];
                    data = found[2];
                    condition = found[3];
                    return;
                } else {
                    regex = RegexHelper.rxFeedSyntaxCommandData;
                    found = RegexHelper.findAllStrings(line, regex);
                    if (found.length == 3) {
                        command = found[1];
                        data = found[2];
                        return;
                    } else{
                        String msg = "SYNTAX ERROR: at line: " + line + Common.LF
                                + "found length: " + found.length;
                        throw new TfsSyntaxError(msg);
                    }
                }
            }
        }
    }


}
