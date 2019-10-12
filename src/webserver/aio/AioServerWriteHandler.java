package webserver.aio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class AioServerWriteHandler implements CompletionHandler<Integer, AsynchronousSocketChannel> {

    private ByteBuffer buffer;

    public AioServerWriteHandler(ByteBuffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public void completed(Integer result, AsynchronousSocketChannel attachASocketChannel) {
        this.buffer.clear();
    }

    @Override
    public void failed(Throwable exc, AsynchronousSocketChannel attachASocketChannel) {
        try {
            attachASocketChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
