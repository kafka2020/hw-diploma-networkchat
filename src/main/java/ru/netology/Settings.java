package ru.netology;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Settings {
    private int port;
    private String host;

    public Settings(String filePath) {
        try (FileInputStream fis = new FileInputStream(filePath)) {
            Properties props = new Properties();
            props.load(fis);
            this.port = Integer.parseInt(props.getProperty("port"));
            this.host = props.getProperty("host", "127.0.0.1");
        } catch (IOException e) {
            System.err.println("Ошибка чтения настроек, используем дефолтные.");
            this.port = 8080;
            this.host = "127.0.0.1";
        }
    }

    public int getPort() { return port; }
    public String getHost() { return host; }
}