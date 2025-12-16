import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

public class SmallChatClient {
    public static final String DEFAULT_HOST = "127.0.0.1";
    public static final int DEFAULT_PORT = 20090;

    public static void main(String[] args) {
        SocketClient socketClient = new SocketClient();
        try (Selector selector = socketClient.initChatClient(DEFAULT_HOST, DEFAULT_PORT)) {
            System.out.printf("Small Chart client started. Connecting to %s:%s\n", DEFAULT_HOST, DEFAULT_PORT);
            socketClient.startUp();
            while (true) {
                int numKeys = selector.select();
                if (numKeys == 0) continue;
                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = keys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();
                    if (!key.isValid()) continue;
                    if (key.isConnectable()) {
                        SocketChannel client = (SocketChannel) key.channel();
                        if (client.isConnectionPending()) {
                            client.finishConnect();
                        }
                        System.out.println("Connection established.");
                        key.interestOps(SelectionKey.OP_READ);
                    } else if (key.isReadable()) {
                        SocketChannel client = (SocketChannel) key.channel();
                        ByteBuffer buffer = ByteBuffer.allocate(4096);
                        int byteSize = client.read(buffer);
                        if (byteSize > 0) {
                            String rev = ByteBufferUtils.bufferToStr(buffer);
                            System.out.println(rev);
                            key.interestOps(key.interestOps() | SelectionKey.OP_WRITE);
                        } else if (byteSize == -1) {
                            System.out.println("Server has disconnected.");
                            client.close();
                        }
                    }
                }
            }
        } catch (IOException exception) {
            System.out.println(exception.getMessage());
        }
    }
}
