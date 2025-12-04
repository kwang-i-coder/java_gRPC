package com.network.grpc.bidirectional;

import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import java.io.IOException;
import java.util.concurrent.Executors;

import com.network.grpc.bidirectional.BidirectionalGrpc;
import com.network.grpc.bidirectional.BidirectionalProto;

public class Server {

    static class BidirectionalService extends BidirectionalGrpc.BidirectionalImplBase {

        @Override
        public StreamObserver<Message> getServerResponse(StreamObserver<Message> responseObserver) {
            System.out.println("Server processing gRPC bidirectional streaming.");

            return new StreamObserver<Message>() {
                @Override
                public void onNext(Message request) {
                    //
                    responseObserver.onNext(request);
                }

                @Override
                public void onError(Throwable t) {
                    System.err.println("Server Error: " + t.getMessage());
                }

                @Override
                public void onCompleted() {
                    responseObserver.onCompleted();
                }
            };
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        int port = 50051;

        io.grpc.Server server = ServerBuilder.forPort(port)
                .executor(Executors.newFixedThreadPool(10))
                .addService(new BidirectionalService())
                .build();

        System.out.println("Server started, listening on " + port);
        server.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.err.println("Shutting down gRPC bidirectional server");
            server.shutdown();
            System.out.println("grpc Server shut down");
        }));

        server.awaitTermination();
    }
}
