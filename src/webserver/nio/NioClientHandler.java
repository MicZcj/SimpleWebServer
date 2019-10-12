package webserver.nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

public class NioClientHandler implements Runnable {

    private Selector selector;

    public NioClientHandler(Selector selector) {
        this.selector = selector;

    }

    @Override
    public void run() {
        try {
            while (true) {
                int keyNums = 0;
                keyNums = selector.select();
                if (keyNums == 0) continue;
                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = keys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();
                    /**
                     * 7. 处理监听事件
                     */
                    if (key.isReadable()) {
                        readHandler(key, selector);
                    }
                    // socketChannel.write(Charset.forName("UTF-8").encode("hello world!"));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readHandler(SelectionKey key, Selector selector) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        ByteBuffer readBuf = ByteBuffer.allocate(1024);
        StringBuffer sb = new StringBuffer();
        while (socketChannel.read(readBuf) > 0) {
            readBuf.flip();
            sb.append(Charset.forName("UTF-8").decode(readBuf));
        }
        String readMessage = sb.toString();
        if (readMessage.length() > 0) {
            System.out.println(readMessage);
        }
        /**
         * 再次把SocketChannel注册到Selector,继续监听服务器端
         */
        socketChannel.register(selector, SelectionKey.OP_READ);
    }
}
