package ru.netology;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    // Список всех подключенных клиентов для рассылки сообщений
    private List<ClientHandler> clients = new ArrayList<>();

    public void start() {
        Settings settings = new Settings("settings.txt");
        int port = settings.getPort();

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Сервер запущен на порту: " + port);
            ChatLogger.log("[SERVER] Сервер запущен на порту: " + port);

            while (true) {
                // Ждем подключения (блокирующая операция)
                Socket clientSocket = serverSocket.accept();
                // Создаем обработчик для клиента
                ClientHandler client = new ClientHandler(clientSocket, this);
                clients.add(client);
                // Запускаем обработку в новом потоке
                new Thread(client).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Метод рассылки сообщения всем
    public synchronized void broadcastMessage(String message, ClientHandler sender) {
        ChatLogger.log("[SERVER] " + message);
        System.out.println("[CHAT LOG] " + message);
        for (ClientHandler client : clients) {
            // Не отправляем сообщение самому себе
            if (client != sender) {
                client.sendMessage(message);
            }
        }
    }

    // Удаление клиента при отключении
    public synchronized void removeClient(ClientHandler client) {
        String name = client.getClientName();
        clients.remove(client);
        System.out.println("Клиент " + name + " отключился от сервера.");
    }

    public static void main(String[] args) {
        new Server().start();
    }
}