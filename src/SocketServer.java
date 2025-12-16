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
        this.clients.put(key, null);
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
        this.clients.replace(key, null, nickname);
    }

    /**
     * Get client nickname.
     * @param key       SelectionKey.
     * @return          Nickname or default('anonymous').
     */
    public String getNickname(SelectionKey key) {
        return this.clients.getOrDefault(key, "anonymous");
    }

    /**
     * Send Welcome to client.
     * @param key           SelectKey.
     * @throws IOException  IOException.
     */
    public void sendWelcome(SelectionKey key) throws IOException {
        String msg = "Welcome to Small Chart! \nUse /nick <nick> to set your nick name.";
        ByteBuffer buffer = ByteBufferUtils.strToBuffer(msg);
        key.attach(buffer);
    }

    /**
     * Send msg to All clients.
     * @param msg           Message.
     * @throws IOException  IOException.
     */
    public void sendAll(String msg) throws IOException {
        ByteBuffer buffer = ByteBufferUtils.strToBuffer(msg);
        for (SelectionKey key : this.clients.keySet()) {
            key.attach(buffer);
        }
    }
}
