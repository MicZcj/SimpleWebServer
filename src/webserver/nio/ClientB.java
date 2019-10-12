package webserver.nio;

import java.io.IOException;

public class ClientB {
    public static void main(String[] args) throws IOException {
        new NioClient("B").start();
    }
}
