package webserver.aio;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;

public class AioClientReadThread implements Runnable {
    private AsynchronousSocketChannel aSocketChannel;

    public AioClientReadThread(AsynchronousSocketChannel aSocketChannel) {
        this.aSocketChannel = aSocketChannel;
    }

    @Override
    public void run() {
        /**
         * 1. 创建ByteBuffer缓冲区
         */
        ByteBuffer readBuffer = ByteBuffer.allocate(1024);
        /**
         * 2. aSocketChannel通道注册read监听器
         */
        this.aSocketChannel.read(readBuffer, aSocketChannel, new AioClientReadHandler(readBuffer));
    }
}
