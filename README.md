# Messenger Relay Server

A simple Java-based relay server for routing messages between clients over the internet.

## Features

* TCP-based message relay
* Supports multiple connected clients
* Routes messages using `peerId`
* Enables communication across different networks (NAT bypass via relay)

## Requirements

* Java 21
* Maven

## Build

```bash
mvn clean package
```

## Run

```bash
java -jar target/messenger-relay-server-1.0.0.jar [port]
```

* Default port: `6000`
* Example:

```bash
java -jar target/messenger-relay-server-1.0.0.jar 7000
```

## Usage

1. Start the relay server
2. Clients connect
3. Each client sends its `peerId` after connecting
4. Messages are routed in format:

   ```
   FROM|TO|MESSAGE
   ```

## Architecture

* `RelayServer` - Listens for incoming connections
* `ClientHandler` - Handles individual client sessions
* `ConnectionRegistry` - Maps `peerId` to active connections
* `Packet` - (optional) message structure

## Notes

* This is an MVP: no encryption or anonymity yet
* The relay can see all messages (will be fixed in future versions)
* Designed as a foundation for secure, decentralized messaging

