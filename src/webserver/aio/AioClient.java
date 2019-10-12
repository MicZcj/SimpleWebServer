package webserver.aio;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.Charset;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;


public class AioClient {

    private String name;
    private AsynchronousSocketChannel aSocketChannel;

    public AioClient(String name) {
        this.name = name;
    }

    public void start() throws IOException, ExecutionException, InterruptedException {
        /**
         * 1. 打开aSocketChannel
         */
        aSocketChannel = AsynchronousSocketChannel.open();
        /**
         * 2. 连接远程服务器
         */
        System.out.println("正在连接服务器...");
        aSocketChannel.connect(new InetSocketAddress(InetAddress.getLocalHost(), 4343),
                this, new AioClientConnectHandler());
        /**
         * 3. 键盘输入消息发送到服务器端
         */
        Scanner scanner = new Scanner(System.in);
        System.out.print("please input : ");
        while (scanner.hasNextLine()) {
            String request = scanner.nextLine();
            if (request != null && request.length() > 0) {
                aSocketChannel.write(Charset.forName("UTF-8").encode(this.name + " : " + request));
            }
            System.out.print("->  ");
        }
    }

    public AsynchronousSocketChannel getaSocketChannel() {
        return aSocketChannel;
    }

    public String getName() {
        return name;
    }

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        new AioClient("Client A").start();
    }
}
