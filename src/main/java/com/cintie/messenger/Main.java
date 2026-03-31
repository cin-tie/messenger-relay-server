package com.cintie.messenger;

import com.cintie.messenger.network.RelayServer;

public class Main {
    public static void main(String[] args){
        int port = 6000;

        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.out.println("Invalid port, using default 6000");
            }
        }

        new RelayServer(port).start();
    }
}
