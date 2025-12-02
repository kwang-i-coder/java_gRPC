package com.network.grpc.hello;

// (1) grpc 모듈 import
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

// (2) protoc가 생성한 클래스 import
import com.network.grpc.hello.MyServiceGrpc;
import com.network.grpc.hello.MyNumber;

public class Client {
    public static void main(String[] args) {
        // (3) grpc 통신 채널 생성
        String host = "localhost";
        int port = 50051;

        ManagedChannel channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();

        // (4) stub 함수를 사용하여 stub를 생성
        MyServiceGrpc.MyServiceBlockingStub stub = MyServiceGrpc.newBlockingStub(channel);

        // (5) 원격 함수에 전달할 메시지를 만들고, 전달할 값을 저장
        MyNumber request = MyNumber.newBuilder().setValue(4).build();

        // (6) 원격 함수를 stub을 사용하여 호출
        MyNumber response = stub.myFunction(request);

        // (7) 결과
        System.out.println("gRPC result:" + response.getValue());
    }
}
