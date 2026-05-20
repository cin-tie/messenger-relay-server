package com.cintie.messenger.network;

import com.cintie.messenger.protocol.*;

import java.io.*;
import java.net.Socket;

// Client thread
public class ClientHandler implements Runnable{
    private final Socket socket;
    private final ConnectionRegistry connectionRegistry;

    private BufferedWriter writer;
    private BufferedReader reader;

    private String peerId;

    // Constructor
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

    // Run thread
    @Override
    public void run() {
        try {
            // Register new client - first HELLO packet
            String line = reader.readLine();
            if(line != null){
                Packet helloPacket = PacketSerializer.deserialize(line);

                if(helloPacket != null && PacketValidator.validate(helloPacket) && helloPacket.getPacketType() == PacketType.HELLO){
                    this.peerId = helloPacket.getSenderId();
                    connectionRegistry.register(peerId, this);

                    // Send ACK for success registration
                    sendPacket(PacketBuilder.ack(peerId, peerId));

                    System.out.println("Client " + peerId + " registered successfully");
                } else {
                    System.out.println("Invalid HELLO packet from client, closing connection");
                    return;
                }
            }

            // Read JSON packets
            while ((line = reader.readLine()) != null){
                try {
                    Packet packet = PacketSerializer.deserialize(line);

                    if(PacketValidator.validate(packet)){
                        handlePacket(packet);
                    }
                    else{
                        System.out.println("Invalid packet from " + peerId + ": validation failed");
                        sendPacket(PacketBuilder.error(peerId, null, "Invalid packet"));
                    }
                } catch(Exception e){
                    System.out.println("Error deserializing packet from " + peerId + ": " + e.getMessage());
                    sendPacket(PacketBuilder.error(null, peerId, "Malformed packet"));
                }
            }

        } catch (Exception e){
            System.out.println("Error: " + peerId + e.getMessage());
        } finally {
            connectionRegistry.unregister(peerId);
        }
    }

    // Handle packet based on type
    private void handlePacket(Packet packet) throws Exception{
        String from = packet.getSenderId();
        String to = packet.getRecipientId();
        PacketType packetType = packet.getPacketType();

        switch (packetType){
            case HELLO:
                break;

            case MESSAGE:
            case KEY_EXCHANGE:
            case ROUTE_BUILD:
            case ROUTE_FORWARD:
                // Forward to recipient
                if(to != null && !to.isEmpty()){
                    ClientHandler target = connectionRegistry.get(to);
                    if(target != null){
                        try {
                            target.sendPacket(packet);
                            System.out.println("Forwarded " + packetType + " from " + from + " to " + to);
                            sendPacket(PacketBuilder.ack(peerId, from));
                        } catch (Exception e){
                            System.out.println("Failed to forward packet to " + to + ": " + e.getMessage());
                            sendPacket(PacketBuilder.error(peerId, from, "Recipient not reachable"));
                        }
                    }
                    else{
                        System.out.println("Recipient " + to + " not found");
                        sendPacket(PacketBuilder.error(peerId, from, "Recipient offline"));
                    }
                }
                break;

            case ACK:
                System.out.println("Received ACK from " + from + " for " + packet.getPacketId());
                break;

            case PING:
                sendPacket(PacketBuilder.ack(peerId, from));
                break;

            case ERROR:
                System.out.println("Received error from " + from + ": " +
                        (packet.getPayload() != null ? packet.getPayload().toString() : "Unknown error"));
                break;

            default:
                System.out.println("Unhandled packet type: " + packetType + " from " + from);
        }
    }

    // Send JSON packet
    private void sendPacket(Packet packet) throws IOException{
        try {
            String json = PacketSerializer.serialize(packet);
            writer.write(json);
            writer.newLine();
            writer.flush();
        } catch (Exception e){
            throw new IOException("Failed to serialize packet", e);
        }
    }
}
