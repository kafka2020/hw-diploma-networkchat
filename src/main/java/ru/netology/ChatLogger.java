package ru.netology;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ChatLogger {
    private static final String LOG_FILE = "file.log";
    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

    // synchronized важен, так как писать могут несколько потоков одновременно
    public static synchronized void log(String msg) {
        try (FileWriter fw = new FileWriter(LOG_FILE, true);
             PrintWriter pw = new PrintWriter(fw)) {
            String time = dtf.format(LocalDateTime.now());
            pw.println("[" + time + "] " + msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}