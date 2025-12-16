import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class ByteBufferUtils {

    public static ByteBuffer strToBuffer(String msg) {
        return ByteBuffer.wrap(msg.getBytes(StandardCharsets.UTF_8));
    }

    public static String bufferToStr(ByteBuffer buffer) {
        StringBuilder sb = new StringBuilder();
        buffer.flip();
        while (buffer.hasRemaining()) {
            sb.append((char) buffer.get());
        }
        return sb.toString();
    }
}
