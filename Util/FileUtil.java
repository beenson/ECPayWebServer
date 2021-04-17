package Util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class FileUtil {
    public static final String log = "log/"; // log存放路徑

    public static String getWorkingPath() {
        File now_directory = new File(".");
        String current_path = now_directory.getAbsolutePath().replaceAll("\\.","");
        return  current_path;
    }

    public static void logToFile(final String file, final String[] msgs) {
        for (int i = 0; i < msgs.length; i++) {
            logToFile(file, msgs[i], false);
            if (i < msgs.length - 1) {
                logToFile(file, "\r\n", false);
            }
        }
    }

    public static void log(String file, String msg){
        logToFile(log + "/" + file,msg+"\r\n");
    }

    public static void logToFile(final String file, final String msg) {
        logToFile(file, msg, false);
    }

    public static void logToFileIfNotExists(final String file, final String msg) {
        logToFile(file, msg, true);
    }

    public static void logToFile(final String file, final String msg, boolean notExists) {
        FileOutputStream out = null;
        try {
            File outputFile = new File(file);
            if(outputFile.exists() && outputFile.isFile() && outputFile.length() >= 512000) { // 避免單檔過大 超過上限時自動建新檔
                outputFile.renameTo(new File(file.substring(0, file.length() - 4) + "_" + DateUtil.getReadableTime() + file.substring(file.length() - 4, file.length())));
                outputFile = new File(file);
            }
            if (outputFile.getParentFile() != null) {
                outputFile.getParentFile().mkdirs();
            }
            out = new FileOutputStream(file, true);
            if (!out.toString().contains(msg) || !notExists) {
                OutputStreamWriter osw = new OutputStreamWriter(out, "UTF-8");
                osw.write(msg);
                osw.flush();
            }
        } catch (IOException ess) {
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException ignore) {

            }
        }
    }

}
