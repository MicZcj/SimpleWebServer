package webserver.aio;

import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;

public class AioClientConnectHandler implements CompletionHandler<Void, AioClient> {
    @Override
    public void completed(Void result, AioClient attachAioClient) {
        /**
         * 1. 向服务器端发送数据
         */
        attachAioClient.getaSocketChannel().write(Charset.forName("UTF-8").encode(attachAioClient.getName() + " request connect..."));
        /**
         * 2. 开新线程接收服务器响应数据
         */
        new Thread(new AioClientReadThread(attachAioClient.getaSocketChannel())).start();
    }

    @Override
    public void failed(Throwable exc, AioClient attachAioClient) {

    }
}
