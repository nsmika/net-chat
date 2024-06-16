package org.server;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Settings {
    private static String host;
    private static int port;

    public static void loadSettings() {
        Properties properties = new Properties();
        try (InputStream input = new FileInputStream("settings.txt")) {
            properties.load(input);
            host = properties.getProperty("host", "localhost");
            port = Integer.parseInt(properties.getProperty("port", "8211"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getHost() {
        return host;
    }

    public static int getPort() {
        return port;
    }
}