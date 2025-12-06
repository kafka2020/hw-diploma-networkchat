package ru.netology;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    // Добавляем переменную для хранения имени, чтобы логгер её видел
    // volatile нужна, так как переменную читает один поток, а пишет другой
    private volatile String userName = "Unknown-Client";

    public void start() {
        Settings settings = new Settings("settings.txt");

        try {
            socket = new Socket(settings.getHost(), settings.getPort());
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // Логируем, кто именно подключился (пока имя неизвестно)
            ChatLogger.log("[CLIENT " + userName + "] Подключение к серверу");

            // Поток для чтения сообщений от сервера
            new Thread(() -> {
                try {
                    String msg;
                    while ((msg = in.readLine()) != null) {
                        System.out.println(msg);
                        ChatLogger.log("[CLIENT " + userName + "] Входящее: " + msg);
                    }
                } catch (IOException e) {
                    System.out.println("Соединение с сервером закрыто.");
                }
            }).start();

            // Основной поток для чтения с консоли
            Scanner scanner = new Scanner(System.in);

            // Считываем первое сообщение отдельно - это Имя клиента
            if (scanner.hasNextLine()) {
                String nameInput = scanner.nextLine();

                // Проверка на немедленный /exit
                if ("/exit".equalsIgnoreCase(nameInput.trim())) {
                    out.println("/exit"); // Отправляем серверу сигнал о выходе

                    // Закрываем сокет, чтобы разблокировать читающий поток и завершить клиент
                    try {
                        if (socket != null) {
                            socket.close();
                        }
                    } catch (IOException e) { }

                    // Выходим из метода start() до того, как начнется основной цикл while(true)
                    return;
                }

                // Сохраняем введенное имя в переменную
                this.userName = nameInput;

                out.println(nameInput); // Отправляем на сервер
                ChatLogger.log("[CLIENT " + userName + "] Исходящее (имя): " + nameInput);
            }

            // Далее обычный цикл обмена сообщениями
            while (true) {
                String msg = scanner.nextLine();
                if ("/exit".equalsIgnoreCase(msg)) {
                    out.println("/exit");
                    ChatLogger.log("[CLIENT " + userName + "] Исходящее (выход): " + msg);
                    break;
                }
                out.println(msg);
                ChatLogger.log("[CLIENT " + userName + "] Исходящее: " + msg);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try { socket.close(); } catch (Exception e) {}
        }
    }

    public static void main(String[] args) {
        new Client().start();
    }
}