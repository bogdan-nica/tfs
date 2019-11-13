package syntax;

import lib.Common;
import lib.RegexHelper;
import lib.Utils;

public class Offset {
    private int x = 0;
    private int y = 0;
    private boolean isSet = false;

    public Offset(String instruct){
        if(!instruct.isEmpty()){
            try {
                String regex = ".*?" + Common.OFFSET + "\\s([0-9-]+)\\s([0-9-]+)(,.*?$|$)";
                String[] found = RegexHelper.findAllStrings(instruct, regex);
                if (found.length == 4) {
                    x = Utils.parseInt(found[1]);
                    y = Utils.parseInt(found[2]);
                    isSet = true;
                }
            }catch (Exception ex ){isSet = false;}
        }
    }
    public int getX(){return x;}
    public int getY(){return y;}
    public boolean getIsSet(){return isSet;}
}
