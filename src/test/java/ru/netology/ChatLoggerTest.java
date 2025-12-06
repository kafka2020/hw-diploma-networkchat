package ru.netology;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class ChatLoggerTest {

    // Имя файла должно совпадать с именем в ChatLogger
    private static final String TEST_LOG_FILE = "file.log";
    private Path logPath;

    @BeforeEach
    void setUp() {
        // Указываем путь к файлу
        logPath = Paths.get(TEST_LOG_FILE);
    }

    @AfterEach
    void tearDown() {
        // Очистка: удаляем файл после каждого теста
        try {
            Files.deleteIfExists(logPath);
        } catch (IOException e) {
            System.err.println("Не удалось удалить лог-файл после теста: " + e.getMessage());
        }
    }

    @Test
    void testLogFileCreationAndContent() throws IOException {
        String testMessage = "Это тестовое сообщение 123";

        // 1. Вызываем метод логирования
        ChatLogger.log(testMessage);

        // 2. Проверяем, что файл создан
        assertTrue(Files.exists(logPath), "Лог-файл должен быть создан.");

        // 3. Считываем все строки из файла
        List<String> lines = Files.readAllLines(logPath);

        // 4. Проверяем, что файл содержит ровно одну строку
        assertEquals(1, lines.size(), "Лог-файл должен содержать ровно одну строку.");

        String logLine = lines.get(0);

        // 5. Проверяем, что строка содержит тестовое сообщение
        assertTrue(logLine.contains(testMessage), "Лог-строка должна содержать переданное сообщение.");

        // 6. Проверяем формат времени (начинается с "[", затем 4 цифры года, слеш и т.д.)
        // Мы не можем точно предсказать секунды, но можем проверить структуру
        assertTrue(logLine.startsWith("["), "Лог-строка должна начинаться с временной метки.");
        assertTrue(logLine.length() > 20, "Длина строки должна быть достаточной для метки и сообщения.");
    }

    @Test
    void testLogAppendsContent() throws IOException {
        String message1 = "Первое сообщение.";
        String message2 = "Второе сообщение.";

        // 1. Логируем первое сообщение
        ChatLogger.log(message1);

        // 2. Логируем второе сообщение
        ChatLogger.log(message2);

        // 3. Считываем все строки из файла
        List<String> lines = Files.readAllLines(logPath);

        // 4. Проверяем, что файл содержит две строки (проверка режима добавления 'true' в FileWriter)
        assertEquals(2, lines.size(), "Лог-файл должен быть дополнен и содержать две строки.");

        // 5. Проверяем, что обе строки присутствуют
        assertTrue(lines.get(0).contains(message1), "Первая строка должна содержать первое сообщение.");
        assertTrue(lines.get(1).contains(message2), "Вторая строка должна содержать второе сообщение.");
    }
}