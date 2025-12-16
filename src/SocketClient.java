import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public class SocketClient {

    /**
     * Init chart client.
     */
    public Selector initChatClient(String host, int port) throws IOException {
        Selector selector = Selector.open();
        SocketChannel clientSocketChannel = SocketChannel.open();
        clientSocketChannel.configureBlocking(false);
        clientSocketChannel.connect(new InetSocketAddress(InetAddress.getByName(host), port));
        clientSocketChannel.register(selector, SelectionKey.OP_CONNECT);
        return selector;
    }
}
