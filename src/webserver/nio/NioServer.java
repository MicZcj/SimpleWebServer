package webserver.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

public class NioServer {
    /**
     * 启动web服务器
     */
    public void start() throws IOException {
        /**
         * 1. 打开Selector选择器
         */
        Selector selector = Selector.open();
        /**
         * 2. 打开ServerSocketChannel通道
         */
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        /**
         * 3. ServerSocketChannel通道绑定端口
         */
        serverSocketChannel.bind(new InetSocketAddress(4343));
        /**
         * 2. 设置ServerSocketChannel通道非阻塞
         */
        serverSocketChannel.configureBlocking(false);
        /**
         * 4. 把ServerSocketChannel注册到selector
         */
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        /**
         * 6. 选择器selector循环监听通道
         */
        System.out.println("服务器已启动");
        while (true) {
            int keyNums = selector.select();
            if (keyNums == 0) {
                continue;
            }
            /**
             * 6. 获得就绪channel的key集合
             */
            Set<SelectionKey> set = selector.selectedKeys();
            Iterator<SelectionKey> iterator = set.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();
                /**
                 * 7. 处理数据读写业务
                 */
                // 接入事件
                if (key.isAcceptable()) {
                    acceptHandler(key, selector);
                }
                // 读数据事件
                if (key.isReadable()) {
                    readHandler(key, selector);
                }
                // 写数据事件
                if (key.isWritable()) {
                    // do somthing
                }
            }
        }
    }

    /**
     * 服务器读通道 处理函数
     * @param key
     * @param selector
     */
    private void readHandler(SelectionKey key, Selector selector) throws IOException {
        /**
         * 1. 获取可读写的SocketChannel通道
         */
        SocketChannel socketChannel = (SocketChannel) key.channel();
        /**
         * 2. 创建读缓冲区
         */
        ByteBuffer readBuf = ByteBuffer.allocate(1024);

        /**
         * 3. 循环读取socketChannel中客户端发来的数据
         */
        StringBuffer sb = new StringBuffer();
        while (socketChannel.read(readBuf) > 0) {
            /**
             * ByteBuffer缓冲区"写模式"转换为"读模式"
             */
            readBuf.flip();
            sb.append(Charset.forName("UTF-8").decode(readBuf));
        }
        String readMessage = sb.toString();
        /**
         * 5. 将客户端的请求广播给其他用户
         */
        if (readMessage.length() > 0) {
            System.out.println(readMessage);
            broadCast(selector, socketChannel, readMessage);
        }
        /**
         * 6. 再次注册到Selectot上继续监听该通道的读事件
         */
        socketChannel.register(selector, SelectionKey.OP_READ);
    }

    /**
     * 向所有接入的客户端发送消息
     * @param selector
     * @param sourceChannel
     * @param message
     */
    private void broadCast(Selector selector, SocketChannel sourceChannel, String message) {
        /**
         * 1. 获取所有接入的客户端的key
         */
        Set<SelectionKey> keys = selector.keys();
        /**
         * 2. 向所有接入的客户端广播消息
         */
        keys.forEach(key -> {
            Channel targetChannel = key.channel();
            if (targetChannel instanceof SocketChannel && targetChannel != sourceChannel) {
                try {
                    ((SocketChannel) targetChannel).write(Charset.forName("UTF-8").encode(message));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 服务器接收客户端通道连接 处理函数
     * @param key
     * @param selector
     */
    private void acceptHandler(SelectionKey key, Selector selector) throws IOException {
        SelectableChannel selectableChannel = key.channel();
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) selectableChannel;
        /**
         * 1. 接收客户端连接创建SocketChannel
         */
        SocketChannel socketChannel = serverSocketChannel.accept();
        /**
         * 2. socketChannel注册非阻塞模式
         */
        socketChannel.configureBlocking(false);
        /**
         * 3. 把socketChannel可读写通道注册到selector
         */
        socketChannel.register(selector, SelectionKey.OP_READ);
        /**
         * 4. 回复客户端消息
         */
        socketChannel.write(Charset.forName("UTF-8").encode("已接入聊天室，请注意隐私安全！"));
    }

    public static void main(String[] args) throws IOException {
        new NioServer().start();
    }
}
