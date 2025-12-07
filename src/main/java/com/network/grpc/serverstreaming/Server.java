package com.network.grpc.serverstreaming;

import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import java.io.IOException;
import java.util.concurrent.Executors;

public class Server {
    private static Message makeMessage(String message) {
        return Message.newBuilder().setMessage(message).build();
    }
    static class ServerStreamingService extends ServerStreamingGrpc.ServerStreamingImplBase {
        @Override
        public void getServerResponse(Number request, StreamObserver<Message> responseObserver) {
            Message[] messages = {
                    makeMessage("message #1"),
                    makeMessage("message #2"),
                    makeMessage("message #3"),
                    makeMessage("message #4"),
                    makeMessage("message #5")
            };
            System.out.println("Server processing gRPC server-streaming{" + request.getValue() + "}.");

            for (Message msg : messages) {
                responseObserver.onNext(msg);
            }
            responseObserver.onCompleted();
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        int port = 50051;

        io.grpc.Server server = ServerBuilder.forPort(port)
                .executor(Executors.newFixedThreadPool(10))
                .addService(new ServerStreamingService())
                .build();

        System.out.println("Server started, listening on " + port);
        server.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            server.shutdown();
        }));

        server.awaitTermination();
    }
}
