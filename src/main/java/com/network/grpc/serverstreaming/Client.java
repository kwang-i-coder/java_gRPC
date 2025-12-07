package com.network.grpc.serverstreaming;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.util.Iterator;

public class Client {
    private static void recvMessage(ServerStreamingGrpc.ServerStreamingBlockingStub stub) {
        Number request = Number.newBuilder().setValue(5).build();

        Iterator<Message> responses = stub.getServerResponse(request);

        while (responses.hasNext()) {
            Message response = responses.next();
            System.out.println("[server to client] " + response.getMessage());
        }
    }
    public static void main(String[] args) {
        String host = "localhost";
        int port = 50051;
        ManagedChannel channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();

        ServerStreamingGrpc.ServerStreamingBlockingStub stub = ServerStreamingGrpc.newBlockingStub(channel);

        recvMessage(stub);
        channel.shutdown();
    }
}