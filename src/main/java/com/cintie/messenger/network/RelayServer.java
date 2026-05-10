package com.cintie.messenger.network;

import java.net.ServerSocket;
import java.net.Socket;

// Server
public class RelayServer {
    public final int port;
    public final ConnectionRegistry connectionRegistry = new ConnectionRegistry();

    public RelayServer(int port){
        this.port = port;
    }

    // Start server
    public void start() {
        // Creating server socket
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Relay running on port " + port);

            // Run thread with client handler
            while (true) {
                Socket socket = serverSocket.accept();
                ClientHandler handler = new ClientHandler(socket, connectionRegistry);
                new Thread(handler).start();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
