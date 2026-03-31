package com.cintie.messenger;

import com.cintie.messenger.network.RelayServer;

public class Main {
    public static void main(String[] args){
        int port = 6000;
        new RelayServer(port).start();
    }
}
