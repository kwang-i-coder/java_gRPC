package com.network.grpc.hello;

// (1) grpc 관련 라이브러리 import
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import java.io.IOException;
import java.util.concurrent.Executors;

// (2) protoc가 생성한 클래스를 import 함
import com.network.grpc.hello.MyServiceGrpc;
import com.network.grpc.hello.MyNumber;

public class Server {
    // (4) protoc가 생성한 ImplBase를 상속받아 서비스 클래스 생성
    static class MyServiceServicer extends MyServiceGrpc.MyServiceImplBase {

        // (5) 서버 클래스에 원격 호출될 함수에 대한 rpc 함수를 작성함
        @Override
        public void myFunction(MyNumber request, StreamObserver<MyNumber> responseObserver) {
            int input = request.getValue();
            int result = HelloGrpc.myFunc(input);

            MyNumber response = MyNumber.newBuilder().setValue(result).build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        int port = 50051;

        // (6) gRPC.server를 생성함
        io.grpc.Server server = ServerBuilder.forPort(port)
                .executor(Executors.newFixedThreadPool(10))
                // (7) sServicer를 추가함
                .addService(new MyServiceServicer())
                .build();

        // (8) grpc.server의 통신 포트를 열고, start()로 서버를 실행
        System.out.println("Server started, listening on " + port);
        server.start();

        // (9) grpc.server가 유지되도록 프로그램 실행을 유지함
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.err.println("Shuting down gRPC server since JVM is shutting down");
            server.shutdown();
            System.err.println("gRPC server shut down");
        }));

        server.awaitTermination();
    }
}