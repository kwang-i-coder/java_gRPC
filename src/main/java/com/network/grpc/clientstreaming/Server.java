package com.network.grpc.clientstreaming;

import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import java.io.IOException;
import java.util.concurrent.Executors;

public class Server {
    static class ClientStreamingService extends ClientStreamingGrpc.ClientStreamingImplBase {
        @Override
        public StreamObserver<Message> getServerResponse(StreamObserver<Number> responseObserver) {
            System.out.println("Server processing gRPC client-streaming.");

            return new StreamObserver<Message>() {
                int count = 0;

                @Override
                public void onNext(Message request) {
                    count++;
                }

                @Override
                public void onError(Throwable t) {
                    System.err.println("Error: " + t.getMessage());
                }

                @Override
                public void onCompleted() {
                    Number response = Number.newBuilder().setValue(count).build();

                    responseObserver.onNext(response);
                    responseObserver.onCompleted();
                }
            };
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        int port = 50051;

        io.grpc.Server server = ServerBuilder.forPort(port)
                .executor(Executors.newFixedThreadPool(10))
                .addService(new ClientStreamingService())
                .build();

        System.out.println("Server started, listening on " + port);
        server.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            server.shutdown();
        }));

        server.awaitTermination();
    }
}
