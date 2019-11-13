package lib;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import syntax.TfsSyntaxError;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

public class ProcessHelper {


    public static List<String> run(String command){
        return runCommand(command, command, ProcessHelper::captureConsole);
    }

    private static List<String> captureConsole(String command, BufferedReader input){
        List<String>res = new ArrayList<>();
        String line = "";
        try{
            while ((line = input.readLine()) != null) {
                res.add(line);
                System.out.println(line);
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }

    private static<T> T runCommand(String command, String text, BiFunction<String, BufferedReader,T> func){
        T res = null;
        try {
            Process p = Runtime.getRuntime().exec(command);
            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));

            res = func.apply(text,input);

            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }
    /**
     * Ex:
     * Windows display example:
     *svchost.exe                   3648 Services                   0      2,140 K
     *Memory Compression            3656 Services                   0    158,316 K
     *
     * Unix dysplay example:
     *    0     1     0   0 18Sep13 ??         6:43.95 /sbin/launchd
     *    0    11     1   0 18Sep13 ??         0:07.98 /usr/libexec/UserEventAgent (System)
     *    0    12     1   0 18Sep13 ??         0:07.13 /usr/libexec/kextd
     *
     * @param key the key to search upon
     * @param command for unix: ex: "ps -few" for windows: ex: tasklist
     * @return a list of string[] string[0] is the process name, [1] process id
     */
    public static ConcurrentHashMap<Integer,String> findProcesses(String key, String command) {
        ConcurrentHashMap<Integer, String> res = Common.calculateSegmentSize(8);
        if (command.contains("task")) {
            res = runCommand(command, key, ProcessHelper::findWin);
        } else {
            throw new TfsSyntaxError("BOGDAN SAIS IMPLEMENT ME :)");
        }
        return res;
    }

    private static ConcurrentHashMap<Integer,String> findWin(String key, BufferedReader input) {
        ConcurrentHashMap<Integer, String> res = Common.calculateSegmentSize(8);
        try {
            String process = "";
            while ((process = input.readLine()) != null) {
                if (process.contains(key)) {
                    System.out.println(process);
                    String[] found = RegexHelper.findAllStrings(process, "([a-zA-Z 0-9.]+)\\s+?([0-9]+?)\\s[A-za-z]+.+$");

                    if (found.length == 3) {
                        Integer procno = Utils.parseInt(found[2]);
                        if (procno > 0) res.put(procno, found[1].trim());
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }

    private static ConcurrentHashMap<Integer,String> findLin(String key, BufferedReader input) {

        ConcurrentHashMap<Integer, String> res = Common.calculateSegmentSize(8);
        try {
            String process = "";
            while ((process = input.readLine()) != null) {
                if (process.contains(key)) {
                    System.out.println(process); // <-- Print all Process here line
                    //TODO: implement this to parse Linex
                    String[] found = RegexHelper.findAllStrings(process, "([a-zA-Z 0-9.]+)\\s+?([0-9]+?)\\s[A-za-z]+.+$");

                    if (found.length == 3) {
                        Integer procno = Utils.parseInt(found[2]);
                        if (procno > 0) res.put(procno, found[1].trim());
                    }
                }
            }
        } catch (IOException e) {
           e.printStackTrace();
        }
        return res;
    }
    public static long getProcessId(Process p){
        long pid = -1;
        try {
            //for windows
            if (p.getClass().getName().equals("java.lang.Win32Process") || p.getClass().getName().equals("java.lang.ProcessImpl")) {
                Field f = p.getClass().getDeclaredField("handle");
                f.setAccessible(true);
                //long handl = f.getLong(p);
                f.setAccessible(false);
            }
            //for unix based operating systems
            //TODO: test this part on unix
            else if (p.getClass().getName().equals("java.lang.UNIXProcess"))
            {
                Field f = p.getClass().getDeclaredField("pid");
                f.setAccessible(true);
                pid = f.getLong(p);
                f.setAccessible(false);
            }
        }
        catch(Exception ex)
        {
            pid = -1;
        }
        return pid;
    }
}
