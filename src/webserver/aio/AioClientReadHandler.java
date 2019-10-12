package webserver.aio;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;

public class AioClientReadHandler implements CompletionHandler<Integer, AsynchronousSocketChannel> {

    private ByteBuffer buffer;

    public AioClientReadHandler(ByteBuffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public void completed(Integer result, AsynchronousSocketChannel attachASocketChannel) {
        // 服务器端关闭
        if (result < 0) {
            /*
            try {
                attachASocketChannel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            */
            System.out.println("关闭连接");
        }
        // 空数据
        else if (result == 0) {
            System.out.println("空数据");
        }
        // 有数据
        else if (result > 0) {
            /**
             * 1. ByteBuffer由"写模式"转换为"读模式"
             */
            this.buffer.flip();
            /**
             * 2. 从ByteBuffer获取消息
             */
            String readMessage = Charset.forName("UTF-8").decode(this.buffer).toString();
            System.out.println(readMessage);
        }
        /**
         * 5. aSocketChannel通道注册read监听器
         */
        this.buffer.clear();
        attachASocketChannel.read(this.buffer, attachASocketChannel, new AioServerReadHandler(this.buffer));
    }

    @Override
    public void failed(Throwable exc, AsynchronousSocketChannel attachASocketChannel) {

    }
}

