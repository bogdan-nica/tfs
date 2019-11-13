package lib;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ConcurrentHashMap;

public class Common {

    public static final String  COLUMN = "COLUMN",
            COMMA = "COMMA",
            BASE = "BASE",
            CONTAINS = "CONTAINS",
            EMPTY = "EMPTY",
            EQUALS = "EQUALS",
            HTTP = "http://",
            HTTPS = "https://",
            IMPORT = "IMPORT:",
            //JSON = "JSON",
            NOW = "NOW",
            NOT_FOUND = "NOT FOUND",
            OFFSET      ="OFFSET",
            POPULATE = "POPULATE",
            PROCESS = "PROCESS",
            QUESTION = "QUESTION",
            RAND = "RAND",
            REGEX = "REGEX",
            RESOURCES = "RESOURCES",
            RESPONSE = "RESPONSE",
            REQUEST = "REQUEST",
            SEMICLMN = "SEMICLMN",
    //STATUS_CODE = "STATUS_CODE",
    TCINDEX = "TCINDEX",
            THIS = "THIS",
            TSINDEX = "TSINDEX",
            URL_ROOT = "URL_ROOT",
            VERTICALBAR = "VERTICALBAR";

    public static final String CR = "\r",
            LF = "\n",
            TAB = "\t";

    public static final int NUM_CORES = Runtime.getRuntime().availableProcessors();

    public static int chromeVersion = -1;

    public final static String relPath = "src/main/resources/drivers";

    /**
     * the folder containing chromeVersion.txt must have read rights for this to work
     * @return
     */
    public static boolean getChromeVersionFromFile() {
        if (chromeVersion <= 0) {
            String pathVersion = FileHelper.getAbsolute("");
            pathVersion = FileHelper.appendPaths(pathVersion,"chromeVersion.txt");
            Path p = new File(pathVersion).toPath();
            if (Files.exists(p)) {
                String content = FileHelper.read(pathVersion);
                chromeVersion = Utils.parseInt(content);
            }
        }
        return chromeVersion > 0;
    }

    /**
     * Ui interface needs more parsing than Api calls.
     * in order to keep the performance to an optimum
     * this function is addressed separately
     * it will serve only functional testing
     * @param input
     * @return
     */
    public static String exceptCharsInXpath(String input) {
        if(input.matches(RegexHelper.rxContainsXpath)) {

            //TODO: replace below with a regex expression that fits all situations below
            String[] find = {".,'",
                    "),'",
                    "id,'",
                    "name,'",
                    ".,\"",
                    "),\"",
                    "id,\"",
                    "name,\""};

            for (String s : find) {
                int start = input.indexOf(s);
                if (start >= 0) {
                    String replaceWith = s.replace(",", COMMA);
                    input = input.replace(s, replaceWith);
                }
            }
        }
        return input;
    }

    public static String replaceExceptedChars(String input) {
        if (input != null && !input.isEmpty()) {

            int start = input.indexOf("\\;");
            if (start >= 0) {
                input = input.replace("\\;", SEMICLMN);
            }
            if ((start = input.indexOf("\\|")) >= 0) {
                input = input.replace("\\|", VERTICALBAR);
            }

            if ((start = input.indexOf("\\?")) >= 0) {
                input = input.replace("\\?", QUESTION);
            }
//            if ((start = input.indexOf("\\,")) >= 0) {
//                input = input.replace("\\,", Globals.COMMA);
//            }
            input = input.replaceAll("\\\\:", COLUMN);
        }
        return input;
    }

    public static String restoreExceptedChars(String input) {
        input = input.replace(SEMICLMN, ";");
        input = input.replace(COLUMN, ":");
        input = input.replace(VERTICALBAR, "|");
        input = input.replace(QUESTION, "?");
        input = input.replace(COMMA, ",");
        return input;
    }

    public static void pause(long miliseconds) {
        try {
            Thread.sleep(miliseconds);
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
    }


    /**
     * Predim the concurrent HashMap for fasster concurent access
     * EX   concurrency = 8 (8 = 2^3)
     *      initial size 2^4 = 16 | increase HashMap size when 12 elem are assigned | concurrent access 8 simultaneous threads
     * @param concurrency this should be power of 2
     * @param <T>
     * @param <V>
     * @return
     */
    public static<T,V> ConcurrentHashMap<T, V> calculateSegmentSize(int concurrency){
        //find the power of 2 representation of concurrency
        Double pow = (Math.log(concurrency)/Math.log(2));
        //extract int
        int power = pow.intValue();
        Double newpow = Math.pow(2, ++power);
        int initialSize = newpow.intValue();
        float loadFactor = 0.75f;
        return new ConcurrentHashMap<T, V>(initialSize,loadFactor,concurrency);
    }
}
