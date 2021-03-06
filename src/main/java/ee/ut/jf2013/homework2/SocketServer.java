package ee.ut.jf2013.homework2;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static ee.ut.jf2013.homework2.JettyServer.startJetty;
import static java.nio.channels.SelectionKey.OP_ACCEPT;
import static java.nio.channels.SelectionKey.OP_READ;

public class SocketServer {

    private static final Map<String, SocketChannel> clients = new HashMap<>();
    private static final CharsetEncoder encoder = Charset.forName("UTF-8").newEncoder();
    private static final CharsetDecoder decoder = Charset.forName("UTF-8").newDecoder();

    public static void main(String[] args) throws Exception {
        startJetty(clients);
        startSocketServer();
    }

    private static void startSocketServer() throws IOException {
        try (ServerSocketChannel server = ServerSocketChannel.open()) {
            server.configureBlocking(false);
            server.socket().bind(new InetSocketAddress(8888));
            Selector selector = Selector.open();
            server.register(selector, OP_ACCEPT);

            while (true) {
                selector.select();
                Set<SelectionKey> keys = selector.selectedKeys();
                for (SelectionKey key : keys) {
                    handleKey(key);
                    keys.remove(key);
                }
            }

        }
    }

    private static void handleKey(SelectionKey key) throws IOException {
        if (key.isAcceptable()) {
            acceptChannel(key);
        } else if (key.isReadable()) {
            readInputChannel((SocketChannel) key.channel());
        }
    }

    private static void acceptChannel(SelectionKey key) throws IOException {
        ServerSocketChannel srv = (ServerSocketChannel) key.channel();
        SocketChannel client = srv.accept();
        client.configureBlocking(false);
        client.register(key.selector(), OP_READ);
    }

    private static void readInputChannel(SocketChannel client) throws IOException {
        ByteBuffer buf = ByteBuffer.allocate(10);
        client.read(buf);
        buf.flip();
        String name = decoder.decode(buf).toString().trim();
        if (clients.containsKey(name) && blockClient(client, name) || name.equals("") && disconnectClient(client)) {
            return;
        }
        System.out.println(name + " is connected to the server.");
        client.write(encode("Welcome, " + name + ", to simple server."));
        clients.put(name, client);
    }

    private static boolean blockClient(SocketChannel client, String name) throws IOException {
        System.out.println("Client with such name: " + name + " already exists!");
        client.close();
        return true;
    }

    private static boolean disconnectClient(SocketChannel client) throws IOException {
        for (Map.Entry<String, SocketChannel> entry : clients.entrySet()) {
            if (entry.getValue().equals(client)) {
                System.out.println(entry.getKey() + " left the chat");
                clients.remove(entry.getKey());
                client.close();
                return true;
            }
        }
        return false;
    }

    static ByteBuffer encode(String message) throws CharacterCodingException {
        return encoder.encode(CharBuffer.wrap(message + "\r\n"));
    }

}
