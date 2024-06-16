package org.server;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.*;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ClientHandlerTest {

    private Socket mockSocket;
    private Set<ClientHandler> clientHandlers;
    private ClientHandler clientHandler;
    private ByteArrayOutputStream outContent;

    @BeforeEach
    public void setUp() throws IOException {
        mockSocket = Mockito.mock(Socket.class);
        outContent = new ByteArrayOutputStream();

        Mockito.when(mockSocket.getOutputStream()).thenReturn(outContent);
        Mockito.when(mockSocket.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[0]));

        clientHandlers = new HashSet<>();
        clientHandler = new ClientHandler(mockSocket, clientHandlers);
        clientHandlers.add(clientHandler);
    }

    @Test
    public void testBroadcastMessage() {
        clientHandler.out = new PrintWriter(new OutputStreamWriter(outContent), true);

        String testMessage = "Hello, World!";
        clientHandler.broadcastMessage(testMessage);

        String output = outContent.toString();
        assertTrue(output.contains(testMessage));
    }
}