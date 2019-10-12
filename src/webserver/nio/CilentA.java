package webserver.nio;

import java.io.IOException;

public class CilentA {
    public static void main(String[] args) throws IOException {
        new NioClient("A").start();
    }
}
