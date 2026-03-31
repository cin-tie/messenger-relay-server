package com.cintie.messenger.network;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable{
    private final Socket socket;
    private final ConnectionRegistry connectionRegistry;

    private BufferedWriter writer;
    private BufferedReader reader;

    private String peerId;

    public ClientHandler(Socket socket, ConnectionRegistry connectionRegistry){
        this.socket = socket;
        this.connectionRegistry = connectionRegistry;

        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            this.peerId = reader.readLine();
            connectionRegistry.register(peerId, this);

            String line;
            while ((line = reader.readLine()) != null){
                handlePacket(line);
            }
        } catch (Exception e){
            System.out.println("Error: " + peerId + e.getMessage());
        } finally {
            connectionRegistry.unregister(peerId);
        }
    }

    private void handlePacket(String raw){
        String[] parts = raw.split("\\|", 3);

        String from = parts[0];
        String to = parts[1];
        String payload = parts[2];

        ClientHandler target = connectionRegistry.get(to);
        try {
            if (target != null) {
                target.send(from + "|" + payload);
            }
        } catch (IOException e){

        }
    }

    public void send(String msg) throws IOException{
        writer.write(msg);
        writer.flush();
    }
}
