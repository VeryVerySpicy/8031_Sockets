import java.io.IOException;
import java.net.SocketException;
import java.net.StandardProtocolFamily;
import java.net.UnixDomainSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;


public class USDServer {

    static String encryptedmsg = "";

    public static void main(String[] args) throws IOException, InterruptedException {
        USDServer usdServer = new USDServer();
        if (args.length > 0) {
            usdServer.receive(Integer.parseInt(args[0]));
        }
        else {
            usdServer.receive(0);
        }
        usdServer.send(encryptedmsg);
    }

    void send(String msg) throws IOException, InterruptedException {
        Thread.sleep(500);
        Path socketPath = Path.of(System.getProperty("user.home")).resolve("wei.socket");
        UnixDomainSocketAddress socketAddress = getAddress(socketPath);
        SocketChannel channel = openSocketChannel(socketAddress);
        writeMessage(channel, msg);
    }

    void writeMessage(SocketChannel socketChannel, String message) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(2048);
        buffer.clear();
        buffer.put(message.getBytes());
        buffer.flip();
        //Sends the file's content to the server via a UNIX Domain Socket.
        while (buffer.hasRemaining()) {
            socketChannel.write(buffer);
        }
        System.out.println("File sent over");
    }

    SocketChannel openSocketChannel(UnixDomainSocketAddress socketAddress) throws IOException {
        SocketChannel channel = SocketChannel.open(StandardProtocolFamily.UNIX);
        channel.connect(socketAddress);
        return channel;
    }

    void receive(int shift) throws IOException, InterruptedException {
        Path socketPath = Path.of(System.getProperty("user.home")).resolve("wei.socket");
        Files.deleteIfExists(socketPath);
        UnixDomainSocketAddress socketAddress = getAddress(socketPath);

        ServerSocketChannel serverChannel = createServerSocketChannel(socketAddress);

        SocketChannel channel = serverChannel.accept();

        while (true) {
            AtomicReference<String> output = new AtomicReference<>("");
            readSocketMessage(channel).ifPresent(output::set);
            String foutput = caesarShift(String.valueOf(output), shift);
            System.out.printf("[Received] %s%n", foutput);
            if (!foutput.isEmpty())
            {
                encryptedmsg = foutput;
                return;
            }
            Thread.sleep(100);
        }
    }

    UnixDomainSocketAddress getAddress(Path socketPath) {
        return UnixDomainSocketAddress.of(socketPath);
    }

    ServerSocketChannel createServerSocketChannel(UnixDomainSocketAddress socketAddress) throws IOException {
        ServerSocketChannel serverChannel = ServerSocketChannel.open(StandardProtocolFamily.UNIX);
        serverChannel.bind(socketAddress);
        return serverChannel;
    }

    Optional<String> readSocketMessage(SocketChannel channel) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        String msg = "";
        try
        {
            int bytesRead = channel.read(buffer);
            if (bytesRead < 0) return Optional.empty();
            byte[] bytes = new byte[bytesRead];
            buffer.flip();
            buffer.get(bytes);
            msg = new String(bytes);
        } catch (SocketException se) {
            System.out.println("Socket most likely closed");
            System.exit(0);
        }

        return Optional.of(msg);
    }

    String caesarShift (String msg, int shift)
    {
        final String lower = "abcdefghijklmnopqrstuvwxyz";
        final String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder cmsg = new StringBuilder();
        char nchar = 0;
        for (int i = 0; i<msg.length(); i++)
        {
            int pos = -1;

            if ((pos = lower.indexOf(msg.charAt(i))) != -1)
            {
                int key = (shift + pos) % 26;
                nchar = lower.charAt(key);
            }
            else if ((pos = upper.indexOf(msg.charAt(i))) != -1)
            {
                int key = (shift + pos) % 26;
                nchar = upper.charAt(key);
            }
            else
            {
                nchar = msg.charAt(i);
            }

            cmsg.append(nchar);
        }

        return cmsg.toString();
    }

}
