package webserver.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

public class NioClient {
    private String name;
    private ByteBuffer readBuf;

    public NioClient(String name) {
        this.name = name;
    }

    /**
     * 客户端启动程序
     */
    public void start() throws IOException {
        /**
         * 1. 创建Selector选择器
         */
        Selector selector = Selector.open();
        /**
         * 2. 连接服务器获得SocketChannel
         */
        System.out.println("正在连接服务器...");
        SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 4343));
        /**
         * 3. 设置SocketChannel通道为非阻塞
         */
        socketChannel.configureBlocking(false);
        /**
         * 4. 把SocketChannel的读事件注册到selector中
         */
        socketChannel.register(selector, SelectionKey.OP_READ);
        /**
         * 5. 新开一个线程，循环等待事件触发
         */
        new Thread(new NioClientHandler(selector)).start();
        /**
         * 6. 键盘输入消息发送到服务器端
         */
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String request = scanner.nextLine();
            if (request != null && request.length() > 0) {
                socketChannel.write(Charset.forName("UTF-8").encode(this.name + ":" + request));
            }
        }
    }

    public static void main(String[] args) throws IOException {
        // new NioClient("Client1").start();
    }
}
