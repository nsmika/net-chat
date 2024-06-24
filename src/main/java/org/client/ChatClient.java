package org.client;

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

public class ChatClient {
    private static final String SETTINGS_FILE = "settings.txt";
    private static final String LOG_FILE = "client.log";
    private static String host;
    private static int port;

    public static void main(String[] args) {
        loadSettings();
        new ChatClient().start();
    }

    private static void loadSettings() {
        Properties properties = new Properties();
        try (InputStream input = new FileInputStream(SETTINGS_FILE)) {
            properties.load(input);
            host = properties.getProperty("host", "localhost");
            port = Integer.parseInt(properties.getProperty("port", "8211"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        try (Socket socket = new Socket(host, port);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in))) {

            System.out.print(in.readLine());
            String username = consoleInput.readLine();
            out.println(username);

            new Thread(new IncomingReader(in)).start();
            String message;
            while ((message = consoleInput.readLine()) != null) {
                if (message.equalsIgnoreCase("/exit")) {
                    out.println(message);
                    logMessage(String.format("[%s] %s: %s",
                            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()), username, "disconnect"));
                    break;
                }
                out.println(message);
                logMessage(String.format("[%s] %s: %s",
                        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()), username, message));

                // Тест вызова ошибки и ее логирования
//                if (message.equalsIgnoreCase("/error")) {
//                    throw new IOException("Simulated IOException");
//                }
            }
        } catch (IOException e) {
            logMessage("Error: " + e.getMessage());
        }
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

    private class IncomingReader implements Runnable {
        private final BufferedReader in;

        public IncomingReader(BufferedReader in) {
            this.in = in;
        }

        @Override
        public void run() {
            String message;
            try {
                while ((message = in.readLine()) != null) {
                    System.out.println(message);
                }
            } catch (IOException e) {
                logMessage(e.getMessage());
            }
        }
    }
}
