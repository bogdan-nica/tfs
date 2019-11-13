package lib;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileHelper {

    public static String separator = File.separator;

    public static boolean write(String content, String path, boolean isAppend) {
        try {
            createDirectoryIfNeeded(path);
            BufferedWriter writer = new BufferedWriter(new FileWriter(path, isAppend));
            if(isAppend){
                writer.append(content);
            }else{writer.write(content);}
            writer.close();
            return true;
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public static String appendPaths(String prefix, String relative) {
        if(prefix.lastIndexOf(separator) < prefix.length()-1){prefix +=separator;}
        if(!separator.equals("/") && relative.contains("/")){relative = relative.replace("/", separator);}
        if(relative.startsWith(separator)){relative = relative.substring(1);}
        if(isAbsolute(relative)){ return relative; }
        else return prefix + relative;
    }

    public static boolean isAbsolute(String path) {
        try {
            Path p = Paths.get(path);
            return p.isAbsolute();
        } catch (Exception ex) {
            return false;
        }
    }

    public static String getAbsolute(String path){
        Path p = Paths.get(path);
        return p.toAbsolutePath().toString();
    }

    public static boolean createDirectoryIfNeeded(String path) {
        if (path == null || path.isEmpty()) return false;
        try {
            Path p = Paths.get(path);
            if (Files.isDirectory(p.getParent())) {
                if (!Files.isDirectory(p) &&
                        !Files.isRegularFile(p)) {
                    File file = new File(path);
                    if (file.isDirectory()) {
                        Files.createDirectory(p);
                        return true;
                    }
                }
            } else {
                createDirectoryIfNeeded(p.getParent().toAbsolutePath().toString());
                return createDirectoryIfNeeded(p.toAbsolutePath().toString());
            }
        } catch (NotDirectoryException dex) {
            dex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public static String read(String path ) {
        if( path.isEmpty() ) return "";
        File file = new File(path);
        if (!file.exists()) {
            System.out.println(path + " does not exist.");
            return null;
        }
        if (!(file.isFile() && file.canRead())) {
            System.out.println(file.getName() + " cannot be read from.");
            return null;
        }
        try {
            FileInputStream fis = new FileInputStream(file);
            char current;
            String content = "";
            while (fis.available() > 0) {
                current = (char) fis.read();
                content += current;
            }
            fis.close();
            return content;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
