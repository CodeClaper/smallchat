import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.LinkedHashMap;
import java.util.Map;

public class SocketServer {

    private static final String DEFAULT_NAME = "anonymous"; 
    private final Map<SelectionKey, String> clients;

    public SocketServer() {
        this.clients = new LinkedHashMap<>();
    }

    /**
     * Init chart server.
     */
    public Selector initChatServer(int port) throws IOException {
        Selector selector = Selector.open();
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        serverSocketChannel.bind(new InetSocketAddress(InetAddress.getLoopbackAddress(), port));
        return selector;
    }

    /**
     * Register.
     * @param key   SelectionKey.
     */
    public void registerClient(SelectionKey key) {
        this.clients.put(key, DEFAULT_NAME);
    }

    /**
     * Deregister.
     * @param key    SelectionKey.
     */
    public void deregisterClient(SelectionKey key) {
        this.clients.remove(key);
    }

    /**
     *  Update nickname.
     * @param key       SelectionKey.
     * @param nickname  Nickname.
     */
    public void updateNickname(SelectionKey key, String nickname) {
        this.clients.replace(key, DEFAULT_NAME, nickname);
    }

    /**
     * Get client nickname.
     * @param key       SelectionKey.
     * @return          Nickname or default('anonymous').
     */
    public String getNickname(SelectionKey key) {
        return this.clients.getOrDefault(key, DEFAULT_NAME);
    }

    /**
     * Send Welcome to client.
     * @param key           SelectKey.
     * @param msg           Message.
     * @throws IOException  IOException.
     */
    public void send(SelectionKey key, String msg) throws IOException {
        ByteBuffer buffer = ByteBufferUtils.strToBuffer(msg);
        key.attach(buffer);
        key.interestOps(key.interestOps() | SelectionKey.OP_WRITE);
    }

    /**
     * Send msg to All clients.
     * @param excludeKey    Exclude Key.
     * @param msg           Message.
     * @throws IOException  IOException.
     */
    public void sendAll(SelectionKey excludeKey, String msg) throws IOException {
        ByteBuffer buffer = ByteBufferUtils.strToBuffer(msg);
        for (SelectionKey key : this.clients.keySet()) {
            if (key.equals(excludeKey)) continue;
            key.attach(buffer);
            key.interestOps(key.interestOps() | SelectionKey.OP_WRITE);
        }
    }
}
