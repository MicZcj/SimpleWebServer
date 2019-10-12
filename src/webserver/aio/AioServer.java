package webserver.aio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AioServer {

    private ExecutorService threadPool;
    private AsynchronousChannelGroup group;
    private AsynchronousServerSocketChannel aServerSocketChannel;

    /**
     * 启动异步IO服务器
     */
    private void start() throws IOException {
        /**
         * 1. 创建线程池
         */
        threadPool = Executors.newFixedThreadPool(20);
        /**
         * 2. 把AsynchronousChannel异步通道组注册到线程池
         */
        group = AsynchronousChannelGroup.withThreadPool(threadPool);
        /**
         * 3. 打开ServerSocketChannel通道
         */
        aServerSocketChannel = AsynchronousServerSocketChannel.open(group);
        /**
         * 4. aServerSocketChannel通道绑定端口
         */
        aServerSocketChannel.bind(new InetSocketAddress(4343));
        /**
         * 5. aServerSocketChannel通道注册accept事件的监听器
         */
        System.out.println("服务器已启动");
        aServerSocketChannel.accept(this, new AioServerAcceptHandler());
        /**
         * 6. 启动一个处理其他业务的线程
         */
        Thread thread = new Thread(() -> {
            System.out.println("业务线程正在运行...");
            while (true) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    public AsynchronousServerSocketChannel getAServerSocketChannel() {
        return aServerSocketChannel;
    }

    public static void main(String[] args) throws IOException {
        new AioServer().start();
    }
}
