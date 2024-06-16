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
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private BufferedReader consoleInput;

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
        try {
            socket = new Socket(host, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            consoleInput = new BufferedReader(new InputStreamReader(System.in));


            System.out.print(in.readLine());
            String username = consoleInput.readLine();
            out.println(username);

            new Thread(new IncomingReader()).start();
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
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
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
        @Override
        public void run() {
            String message;
            try {
                while ((message = in.readLine()) != null) {
                    System.out.println(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
