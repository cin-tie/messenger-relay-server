package com.cintie.messenger.network;

import java.util.concurrent.ConcurrentHashMap;

// All client connections
public class ConnectionRegistry {
    private final ConcurrentHashMap<String, ClientHandler> peers = new ConcurrentHashMap<>();

    // Register client
    public void register(String peerId, ClientHandler handler){
        peers.put(peerId, handler);
        System.out.println("Registered: " + peerId);
    }

    // Unregister client
    public void unregister(String peerId){
        peers.remove(peerId);
        System.out.println("Disconnected: " + peerId);
    }

    // Get client handler by id
    public ClientHandler get(String peerId){
        return peers.get(peerId);
    }
}
