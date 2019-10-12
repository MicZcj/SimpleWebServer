package webserver.aio;

import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;

public class AioServerAcceptHandler implements CompletionHandler<AsynchronousSocketChannel, AioServer> {
    @Override
    public void completed(AsynchronousSocketChannel aSocketChannel, AioServer attachAioServer) {
        /**
         * 1. ByteBuffer由"读模式"转换为"写模式"
         */
        ByteBuffer buffer;
        String writeMessage = "已接入服务器，请注意隐私安全！";
        buffer = ByteBuffer.wrap(writeMessage.getBytes(Charset.forName("UTF-8")));
        /**
         * 2. 服务器响应客户端，ASocketChannel通道注册写监听器
         */
        aSocketChannel.write(buffer, aSocketChannel, new AioServerWriteHandler(buffer));
        /**
         * 3. aSocketChannel通道注册read监听器
         */
        aSocketChannel.read(buffer, aSocketChannel, new AioServerReadHandler(buffer));
        /**
         * 4. 注册新的accpet监听器
         */
        attachAioServer.getAServerSocketChannel().accept(attachAioServer, new AioServerAcceptHandler());
    }

    @Override
    public void failed(Throwable exc, AioServer attachAioServer) {
        try {
            attachAioServer.getAServerSocketChannel().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
