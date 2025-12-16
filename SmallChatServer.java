import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * Small Chat Server.
 */
public class SmallChatServer {

    public static final String WELCOME_MSG = "Welcome to Small Chart! \nUse /nick <nick> to set your nick name.";
    public static final String UNSUPPORTED_MSG = "Unsupported command!";
    public static final int DEFAULT_PORT = 20090;

    public static void main(String[] args) {
        SocketServer socketServer = new SocketServer();
        try (Selector selector = socketServer.initChatServer(DEFAULT_PORT)) {
            System.out.println("Start chart server and listen: " + DEFAULT_PORT);
            while (true) {
                int numKeyReady = selector.select();
                if (numKeyReady == 0) continue;
                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = keys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();
                    if (!key.isValid()) continue;
                    if (key.isAcceptable()) {
                        ServerSocketChannel server = (ServerSocketChannel) key.channel();
                        SocketChannel client = server.accept();
                        client.configureBlocking(false);
                        SelectionKey clientKey = client.register(selector, SelectionKey.OP_READ);
                        socketServer.registerClient(clientKey);
                        socketServer.send(clientKey, WELCOME_MSG);
                        System.out.println("Connection Accepted: " + client.getRemoteAddress());
                    } else if (key.isReadable()) {
                        SocketChannel client = (SocketChannel) key.channel();
                        ByteBuffer buffer = ByteBuffer.allocate(4096);
                        int byteSize = client.read(buffer);
                        if (byteSize > 0) {
                            String rev = ByteBufferUtils.bufferToStr(buffer);
                            if (rev.startsWith("/")) {
                                if (rev.startsWith("/nick")) {
                                    String nickname = rev.replace("/nick", "").trim();
                                    socketServer.updateNickname(key, nickname);
                                    String message = "system > " + nickname + " join in.";
                                    socketServer.sendAll(null, message);
                                    System.out.printf(message);
                                } else if (rev.startsWith("/exit")) {
                                    String nickname = socketServer.getNickname(key);
                                    socketServer.deregisterClient(key);
                                    client.close();
                                    String message = "system > " + nickname + " leave.";
                                    socketServer.sendAll(null, message);
                                    System.out.printf(message);
                                } else {
                                    socketServer.send(key, UNSUPPORTED_MSG);
                                }
                            } else {
                                String nickname = socketServer.getNickname(key);
                                String message = nickname + " > " + rev;
                                socketServer.sendAll(key, message);
                                System.out.println(message);
                            }
                        } else if (byteSize == -1) {
                            System.out.println("Client has disconnected: " + client.getRemoteAddress());
                            client.close();
                            socketServer.deregisterClient(key);
                        }
                    } else if (key.isWritable()) {
                        SocketChannel client = (SocketChannel) key.channel();
                        ByteBuffer buffer = (ByteBuffer) key.attachment();
                        int send = client.write(buffer.duplicate());
                        if (send > 0) {
                            key.interestOps(key.interestOps() & ~SelectionKey.OP_WRITE);
                        } else {
                            System.out.println("Client has disconnected: " + client.getRemoteAddress());
                            client.close();
                            socketServer.deregisterClient(key);
                        }
                    }
                }
            }
        } catch (IOException exception) {
            System.out.println(exception.getMessage());
        }
    }
}
