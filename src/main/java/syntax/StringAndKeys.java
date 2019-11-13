package syntax;

import lib.Common;

/**
 * @author Bogdan.Nica
 *
 * parse input of type  string,KEY1,KEY2...
 */
public class StringAndKeys {
    private String string = "";
    private String[] keys = new String[0];
    private boolean isSet = false;

    public String getString(){return string;}
    public String[] getKeys(){return keys;}
    public boolean getIsSet(){return isSet;}

    public StringAndKeys(String instruct){
        if(!instruct.isEmpty()){
            String[] split = Common.replaceExceptedChars(
                    Common.exceptCharsInXpath(instruct)).split(",");
            if(split.length >= 2){
                string = Common.restoreExceptedChars(split[0].trim());
                keys = new String[split.length-1];
                for(int i = 1; i<split.length;i++){
                    keys[i-1] = Common.restoreExceptedChars(split[i].trim());
                }
                isSet = true;
            }else if(split.length == 1){
                string = Common.restoreExceptedChars(split[0].trim());
                isSet = true;
            }
        }
    }


}