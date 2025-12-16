import javax.swing.plaf.TableHeaderUI;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class SocketClient implements Runnable {

    private SocketChannel client;
    private Thread readThread;

    /**
     * Init chart client.
     */
    public Selector initChatClient(String host, int port) throws IOException {
        Selector selector = Selector.open();
        this.client = SocketChannel.open();
        this.client.configureBlocking(false);
        this.client.connect(new InetSocketAddress(InetAddress.getByName(host), port));
        this.client.register(selector, SelectionKey.OP_CONNECT);
        this.readThread = new Thread(this);
        return selector;
    }

    public void startUp() {
        this.readThread.start();
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        try {
            while (!Thread.currentThread().isInterrupted()) {
                String line = scanner.nextLine();
                if (this.client == null) {
                    System.out.println("Not join.");
                } else if (line != null && !line.isEmpty()) {
                    this.client.write(ByteBufferUtils.strToBuffer(line));
                }
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }
}
