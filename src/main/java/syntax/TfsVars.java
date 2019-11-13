package syntax;

import atfs.Tfs;
import lib.*;
//import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;


import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public class TfsVars {

    public static void setVarsFromString(String input, ConcurrentHashMap<String, Object> coll) {
        String[] split = Common.replaceExceptedChars(input).split(Pattern.quote(";"));
        for (String var : split) {
            String[] splitvar = var.split(Pattern.quote(":"));
            switch (splitvar.length) {
                case 1:
                    String msg = "SYNTAX ERROR: " + var + Common.LF +
                            "ERROR INPUT:  " + input;
                    throw new TfsSyntaxError(msg);
                case 2:
                    String key = splitvar[0].trim();
                    coll.put(key, setValue(key, splitvar[1], coll));
                    break;
                default:
                    msg = "SYNTAX ERROR: " + var + Common.LF +
                            "ERROR INPUT:  " + input;
                    throw new TfsSyntaxError(msg);
            }
        }
    }

    private static Object setValue(String key, String value,
                                   ConcurrentHashMap<String, Object> coll) {

        String[] splitter = key.split(Pattern.quote("."));
        String keyStartsWith = splitter[0];
        switch (keyStartsWith) {
            case "int":
                String val = replaceKeys(Common.restoreExceptedChars(value.trim()), coll);
                return Utils.parseInt(val);
            case "bool":
                val = replaceKeys(Common.restoreExceptedChars(value.trim()), coll);
                return Boolean.parseBoolean(val);
            default:
                return replaceKeys(Common.restoreExceptedChars(value.trim()), coll);

        }
    }

    private static int countrecursive = 0;
    private static Integer witness = 0;
    private static final int MAX_REC = 90;

    private static final int MAX_CYCLES = 15;
    private static final int MAX_WITNESS = 15;

    private static boolean isWitness(String found, String value) {
        if (value.equals(found)) {
            if (witness >= MAX_WITNESS) {
                witness = 0;
                throw new NoSuchElementException();
            } else {
                String msg = ++witness + " | " + Common.NOT_FOUND + " YET: found: " + found //+ Common.CRLF
                        + Utils.repeat(" ", witness.toString().length())
                        + "   " + Utils.repeat(" ", Common.NOT_FOUND.length()) + "  value: " + value;
                System.out.println(msg);
            }
        }
        return true;
    }

    public static String replaceKeys(String value, ConcurrentHashMap<String, Object> coll) {
        int countcicles = 0;
        countrecursive = 0;
        witness = 0;
        while (countrecursive == 0 && countcicles++ < MAX_CYCLES) {
            value = replaceKeysDolls(value, coll);
        }
        countrecursive = 0;
        witness = 0;
        return value;
    }


    private static String replaceKeysDolls(String value, ConcurrentHashMap<String, Object> localVars) {
        if (++countrecursive > MAX_REC) {
            System.out.println("COUNTED: " + countrecursive);
            countrecursive = 0;
            return value;
        }
        String found = "";
        if (value.matches(".*?" + RegexHelper.rxUserObjectDollSyntax + ".*")) {
            String userpath = RegexHelper.findString(value, ".*?" + RegexHelper.rxUserObjectDollSyntax + ".*");
            String userkey = RegexHelper.findString(userpath, RegexHelper.rxUserKeyDoll);
            String whatfromuser = RegexHelper.findString(userpath, ".+?(" + RegexHelper.rxDollSyntax + ").*");
            //Object user = Globals.globalVars.get(userkey);
            //if (user instanceof iAtf) {
                //Emu useremu = new Emu(((iAtf) user).getEmu());
            String elemfound = replaceKeysDolls(whatfromuser, localVars);
            value = value.replace(userpath, elemfound);
            value = replaceKeysDolls(value, localVars);
            //}
        } else if (value.matches(".*?" + RegexHelper.rxArrayDollsSyntax + ".*")) {
            value = getCollection(value, localVars);
            //}
        } else if (value.matches(".*?" + RegexHelper.rxArrayDollsSyntax2 + ".*")) {
            value = getCollection(value, localVars);
        }
        try {
            while (!(found = RegexHelper.findDolls(value)).isEmpty()) {
                String key = found;
                String val = getKeywordValueDolls(found, value, localVars);
                value = value.replace(key, val);
                if (isWitness(found, val)) value = replaceKeysDolls(value, localVars);
            }
            return value;
        } catch (StackOverflowError ste) {
            System.out.println( "found: " + found + Common.LF);
            throw ste;
        }
    }

    private static String getKeywordValueDolls(String found, String alltext, ConcurrentHashMap<String, Object> localVars) {
        if (found.isEmpty()) return found;

        String value = found;
        String firstInDollName = RegexHelper.findFirstInDollName(found);

        switch (firstInDollName) {
            case "now":
            case "random":
                throw new TfsSyntaxError("IMPLEMENT ME in getKeywordValueDolls for " + firstInDollName);
            default:
                String val = getStringValueFromCollections(found, localVars);
                if(!val.isEmpty())return val;
                else return value;
        }
    }

    private static String getCollection(String value,  ConcurrentHashMap<String, Object> localVars) {
        int start = 1;
        String[] matches = RegexHelper.findAllStrings(value, RegexHelper.rxArrayAllDollsSyntax);
        if(matches.length < 3) {
            start = 0;
            matches = RegexHelper.findAllStrings(value, RegexHelper.rxArrayElemDollsSyntax);
        }

        String arrayKey = matches[matches.length - (start + 2)];
        int index = Utils.parseInt(matches[matches.length - (start + 1)]);
        String header = "";
        if (start > 0) {
            header = matches[matches.length - start];
        }
        Object  arr = localVars.get(arrayKey);

        if (arr instanceof JsonArray) {
            String localfound =matches[0];
            String json = ((JsonArray) arr).get(index).toString();
            if (header.isEmpty()) {
                value = value.replace(localfound,json);
            } else {
                String elem = JsonHelper.getValue(json, header);
                value = value.replace(localfound,elem);
            }
        } else if (arr instanceof Collection) {
            throw new TfsSyntaxError("IMPLEMENT ME in getCollection for Collection type");
        }
        return value;
    }

    private static String getStringValueFromCollections(String found, ConcurrentHashMap<String, Object> localColl){
        Object val = getRawValueFromCollections(found, localColl);
        if(val == null) return "";

        return replaceKeysDolls(val.toString(), localColl);
    }

    private static Object getRawValueFromCollections(String doll, ConcurrentHashMap<String, Object> localColl){
        String nameDoll = RegexHelper.extractDollName(doll);
        Object val = localColl.get(nameDoll);
        return val;
    }
}
