package com.cintie.messenger.network;

import java.util.concurrent.ConcurrentHashMap;

public class ConnectionRegistry {
    private final ConcurrentHashMap<String, ClientHandler> peers = new ConcurrentHashMap<>();

    public void register(String peerId, ClientHandler handler){
        peers.put(peerId, handler);
        System.out.println("Registered: " + peerId);
    }

    public void unregister(String peerId){
        peers.remove(peerId);
        System.out.println("Disconnected: " + peerId);
    }

    public ClientHandler get(String peerId){
        return peers.get(peerId);
    }
}
