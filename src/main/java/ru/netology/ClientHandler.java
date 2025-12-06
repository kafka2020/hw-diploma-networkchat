package ru.netology;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

class ClientHandler implements Runnable {
    private Socket socket;
    private Server server;
    private PrintWriter out;
    private BufferedReader in;
    private String clientName; // Инициализируется как null

    public ClientHandler(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;
    }

    @Override
    public void run() {
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            out.println("Введите ваше имя:");
            String nameInput;

            while (true) {
                nameInput = in.readLine();

                if (nameInput == null) {
                    // Соединение было потеряно, выходим из цикла
                    break;
                }

                // Убираем лишние пробелы
                String trimmedInput = nameInput.trim();

                // Проверка на команду выхода - если клиент в строке ввода имени напишет "/exit"
                if ("/exit".equalsIgnoreCase(trimmedInput)) {
                    break;
                }

                // Проверка на корректность имени (не пустое)
                if (!trimmedInput.isEmpty()) {
                    clientName = trimmedInput;
                    break; // Имя успешно получено, выходим из цикла
                }

                // Если имя некорректно, просим ввести снова
                out.println("Имя не может быть пустым или состоять из пробелов. Введите корректное имя:");
            }

            // Если при выходе из цикла clientName == null, значит был /exit или обрыв соединения.
            if (clientName == null) {
                // Если это была команда /exit, то log будет @Anonymous-Exit (через finally)
                return; // Пропускаем всю логику чата и переходим к блоку finally
            }

            server.broadcastMessage("Пользователь " + clientName + " присоединился к чату!", this);

            String message;
            while ((message = in.readLine()) != null) {
                if ("/exit".equalsIgnoreCase(message)) {
                    break;
                }
                server.broadcastMessage(clientName + ": " + message, this);
            }

        } catch (IOException e) {
            System.err.println("Связь с клиентом прервана");
        } finally {
            // getClientName() обеспечит, что в лог не попадет "null"
            String finalName = getClientName();

            server.removeClient(this);
            // Используем безопасное finalName, чтобы не отправить "null"
            server.broadcastMessage("Пользователь " + finalName + " покинул чат.", this);
            try { socket.close(); } catch (IOException e) { }
        }
    }

    public void sendMessage(String msg) {
        out.println(msg);
    }

    public String getClientName() {
        // Защита от попадания "null" в лог сервера.
        return (this.clientName != null && !this.clientName.trim().isEmpty()) ? this.clientName : "@Anonymous-Exit";
    }
}