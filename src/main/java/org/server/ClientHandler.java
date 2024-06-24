package org.server;

import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

public class ClientHandler implements Runnable {
    private static final String LOG_FILE = "server.log";
    private final Socket socket;
    private final Set<ClientHandler> clientHandlers;

    public ClientHandler(Socket socket, Set<ClientHandler> clientHandlers) {
        this.socket = socket;
        this.clientHandlers = clientHandlers;
    }

    public void run() {
        try (PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            out.println("Enter your username: ");
            String username = in.readLine();
            String welcomeMessage = formatMessage("Server", username + " has joined the chat");
            logMessage(welcomeMessage);
            broadcastMessage(welcomeMessage);

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                if (inputLine.equalsIgnoreCase("/exit")) {
                    break;
                }
                String message = formatMessage(username, inputLine);
                logMessage(message);
                broadcastMessage(message);
            }

            String goodbyeMessage = formatMessage("Server", username + " has left the chat");
            logMessage(goodbyeMessage);
            broadcastMessage(goodbyeMessage);

        } catch (IOException e) {
            logMessage("Error: " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                logMessage("Error: " + e.getMessage());
            }
            clientHandlers.remove(this);
        }
    }

    private String formatMessage(String username, String message) {
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        return "[" + time + "] " + username + ": " + message;
    }

    private void logMessage(String message) {
        try (FileWriter fw = new FileWriter(LOG_FILE, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            out.println(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void broadcastMessage(String message) {
        for (ClientHandler clientHandler : clientHandlers) {
            try {
                PrintWriter clientOut = new PrintWriter(clientHandler.socket.getOutputStream(), true);
                clientOut.println(message);
            } catch (IOException e) {
                logMessage("Error: " + e.getMessage());
            }
        }
    }
}