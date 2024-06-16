package org.server;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SettingsTest {
    @Test
    public void testLoadSettings() {
        Settings.loadSettings();
        assertEquals("localhost", Settings.getHost());
        assertEquals(12345, Settings.getPort());
    }
}