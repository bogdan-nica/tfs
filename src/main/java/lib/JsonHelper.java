package lib;

import syntax.TfsSyntaxError;

import javax.json.*;
import java.io.StringReader;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;

public class JsonHelper {


    public static boolean isAJson(String json) {
        try {
            StringReader r = new StringReader(json);
            JsonReader reader = Json.createReader(r);
            JsonStructure jsnst = reader.readObject();
            JsonObject jsn = (JsonObject) jsnst;
            return jsn != null;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isAJsonArray(String json) {
        try {
            StringReader r = new StringReader(json);
            JsonReader reader = Json.createReader(r);//new StringReader(json));
            JsonStructure jsnst = reader.readArray();
            JsonArray jsn = (JsonArray) jsnst;
            return jsn != null;
        } catch (Exception e) {
            return false;
        }
    }
    private static boolean isAJson(JsonValue jsonelem) {
        try {
            jsonelem.asJsonObject();
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public static JsonObject toJsonObject(String json) {
        try {
            StringReader r = new StringReader(json);
            JsonReader reader = Json.createReader(r);//new StringReader(json));
            JsonStructure jsonst = reader.readObject();
            r.close();
            return json == null || json.isEmpty() ? null : (JsonObject) jsonst;
        } catch (Exception e) {
            String msg = "JSON ERROR: json: |" + json + Common.LF + e.getMessage();
            System.out.println(msg);
            return null;
        }
    }

    public static JsonArray toJsonArray(String json) {
        try {
            StringReader r = new StringReader(json);
            JsonReader reader = Json.createReader(r);//new StringReader(json));
            JsonStructure jsonst = reader.readArray();
            r.close();
            return json == null || json.isEmpty() ? null : (JsonArray) jsonst;
        } catch (Exception e) {
            String msg = "JSON ERROR: json: |" + json + Common.LF + e.getMessage();
            System.out.println(msg);
            return null;
        }
    }

    public static String getValue(String json, String key) {
        if (json == null || json.isEmpty()) {
            return null;
        }
        if (key == null || key.isEmpty()) return json;

        String[] splitted = key.split(Pattern.quote("."));
        if (splitted.length > 1) {
            for (String k : splitted) {
                json = getValue(json, k);
                if (json == null || json.isEmpty() || json.toLowerCase().equals("\"null\"")) return null;
            }
            return json;
        } else {
            try {
                JsonObject object = toJsonObject(json);
                JsonValue jsonelem = null;
                if (key.contains("[")) {
                    String sindex = RegexHelper.findString(key, ".*\\[(.+?)\\].*");
                    int index = Utils.parseInt(sindex);
                    if(index<0)return null;

                    String elem = RegexHelper.findString(key, "(^|.+[.])(.+?)(\\[[0-9\\s-+*:]+\\].*)", 2);
                    JsonArray myarray = object.getJsonArray(elem);
                    if (myarray != null && myarray.size() > index) {
                        jsonelem = myarray.getJsonObject(index);
                    }
                } else {
                    jsonelem = object.get(key);
                }

                if (jsonelem != null) {
                    JsonValue.ValueType elemtype = jsonelem.getValueType();
                    if (elemtype.compareTo(JsonValue.ValueType.STRING) == 0) {
                        return removeCotes(jsonelem.toString());
                    }else if(elemtype.compareTo(JsonValue.ValueType.NULL) == 0){
                        return null;
                    }else {
                        return !isAJson(jsonelem)? jsonelem.toString() : jsonelem.asJsonObject().toString();
                    }
                } else {
                    return null;
                }
            } catch (Exception e) {
                String msg = "JSON ERROR: key: |" + key + "| json: |" + json + Common.LF + e.getMessage();
                System.out.println(msg);
                return null;
            }
        }
    }
    public static String getValue(Object json, String key) {
        if (json == null) {
            return null;
        }
        if (key == null || key.isEmpty()) return "";

        String[] splitted = key.split(Pattern.quote("."));
        JsonValue jsonelem = null;
        if (json instanceof JsonObject) {
            JsonObject val = (JsonObject) json;
            if (splitted.length > 1) {
                for (String k : splitted) {
                    val = toJsonObject(getValue(val, k));
                    if (val == null) return null;
                }
                return val.toString();
            } else {
                jsonelem = val.get(key);
            }

        } else if (json instanceof JsonArray) {
            try {
                JsonArray jarr = (JsonArray) json;
                if (key.contains("[")) {
                    String sindex = RegexHelper.findString(key, ".*\\[(.+?)\\].*");
                    int index = Utils.parseInt(sindex);
                    if (index < 0) return null;

                    String elem = RegexHelper.findString(key, "(^|.+[.])(.+?)(\\[[0-9\\s-+*:]+\\].*)", 2);
//                        JsonArray myarray = jarr.getJsonArray(elem);
//                        if (myarray != null && myarray.size() > index) {
//                            jsonelem = myarray.getJsonObject(index);
//                        }
                    throw new TfsSyntaxError("BOGDAN SAIS IMPLEMENT ME!!! in getValue(Object json, String key)");
                } else {
                    throw new TfsSyntaxError("BOGDAN SAIS IMPLEMENT ME!!! in getValue(Object json, String key)");
                }

            } catch (Exception e) {
                String msg = "JSON ERROR: key: |" + key + "| json: |" + json + Common.LF + e.getMessage();
                System.out.println(msg);
                return null;
            }
        }
        if (jsonelem != null) {
            JsonValue.ValueType elemtype = jsonelem.getValueType();
            if (elemtype.compareTo(JsonValue.ValueType.STRING) == 0) {
                return removeCotes(jsonelem.toString());
            } else if (elemtype.compareTo(JsonValue.ValueType.NULL) == 0) {
                return null;
            } else {
                return !isAJson(jsonelem) ? jsonelem.toString() : jsonelem.asJsonObject().toString();
            }
        } else {
            return null;
        }
    }

    public static boolean hasKey(JsonObject json, String key) {
        try {
            Object value = json.get(key);
            return value != null;
        } catch (Exception ex) {
            ex = null;
        }
        return false;
    }

    public static JsonObject deepMerge(JsonObject source, JsonObject target) {
        //matching keys and get values
        for (String key : source.keySet()) {
            JsonValue value = source.get(key);
            if (!hasKey(target, key)) {
                //target.entrySet(key, value);
                throw new NoSuchElementException();
            } else {
                //discution of the elem...
                if (value instanceof JsonObject) {
                    deepMerge((JsonObject) value, target.getJsonObject(key));
                } else if (hasKey(target, key)) {
                    throw new NoSuchElementException();
                    //target.put(key, value);
                }
            }
        }
        return target;
    }

    private static String removeCotes(String value){
        if(value.toCharArray()[0]=='"'){
            value = value.substring(1,value.length());
            if(value.toCharArray()[value.length()-1]=='"'){
                value = value.substring(0,value.length()-1 );
            }
        }
        return value;
    }

}
