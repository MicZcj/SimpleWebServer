package webserver.aio;



import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class ClientA {
    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        new AioClient("Client A").start();
    }
}
