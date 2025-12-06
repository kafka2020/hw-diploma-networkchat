package ru.netology;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class SettingsTest {

    @Test
    public void testSettingsLoad() throws IOException {
        // Создаем временный файл настроек
        File tempConfig = new File("test_settings.txt");
        try (FileWriter fw = new FileWriter(tempConfig)) {
            fw.write("port=9999\n");
            fw.write("host=192.168.0.1\n");
        }

        // Тестируем класс
        Settings settings = new Settings("test_settings.txt");

        Assertions.assertEquals(9999, settings.getPort());
        Assertions.assertEquals("192.168.0.1", settings.getHost());

        // Удаляем временный файл
        tempConfig.delete();
    }

    @Test
    public void testDefaultSettings() {
        // Читаем несуществующий файл
        Settings settings = new Settings("no_file.txt");
        // Должны быть дефолтные
        Assertions.assertEquals(8080, settings.getPort());
    }
}