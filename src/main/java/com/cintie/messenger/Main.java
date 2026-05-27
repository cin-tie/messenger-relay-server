package com.cintie.messenger;

import com.cintie.messenger.network.RelayServer;

// Main
public class Main {
    public static void main(String[] args){
        // Default port
        int port = 6000;

        // Port from args
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.out.println("Invalid port, using default 6000");
            }
        }

        // Start server
        new RelayServer(port).start();
    }
}
