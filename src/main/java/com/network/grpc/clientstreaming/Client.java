package com.network.grpc.clientstreaming;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class Client {
    private static Message makeMessage(String message) {
        return Message.newBuilder().setMessage(message).build();
    }
    public static void main(String[] args) throws InterruptedException {
        String host = "localhost";
        int port = 50051;

        ManagedChannel channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();

        CountDownLatch finishLatch = new CountDownLatch(1);

        ClientStreamingGrpc.ClientStreamingStub stub = ClientStreamingGrpc.newStub(channel);

        StreamObserver<Number> responseObserver = new StreamObserver<Number>() {
            @Override
            public void onNext(Number response) {
                System.out.println("[server to client] " + response.getValue());
            }

            @Override
            public void onError(Throwable t) {
                finishLatch.countDown();
            }

            @Override
            public void onCompleted() {
                finishLatch.countDown();
            }
        };

        StreamObserver<Message> requestObserver = stub.getServerResponse(responseObserver);

        for (int i = 1; i <= 5; i++) {
            String text = "message #" + i;
            Message msg =  makeMessage(text);

            System.out.println("[client to server] " + text);

            requestObserver.onNext(msg);
        }

        requestObserver.onCompleted();

        finishLatch.await(1, TimeUnit.MINUTES);

        channel.shutdown();
    }
}
