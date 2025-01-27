import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
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

//● Accepts the name of a file as a command-line argument.
//● Reads the contents of the file.
//● Sends the file's content to the server via a UNIX Domain Socket.
//● Receives the encrypted file from the server and prints it to the terminal

public class UDSClient {

    static String encryptedmsg = "";

    public static void main(String[] args) throws Exception {

        UDSClient usdClient = new UDSClient();

        //Accepts the name of a file as a command-line argument.
        File file;
        StringBuilder msg = new StringBuilder();
        if (args.length > 0) {
            file = new File(args[0]);
            BufferedReader reader = null;
            try
            {
                //Reads the contents of the file.
                String current;
                reader = new BufferedReader(new FileReader(file));
                while ((current = reader.readLine()) != null)
                {
                    msg.append("\n").append(current);
                }
            }
            catch
            (IOException e) {
                e.printStackTrace();
            }
            finally
            {
                try {
                    if (reader != null)reader.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        } else {
            System.err.println("No file specified in CMD line arguments.");
            System.exit(1);
        }
        usdClient.send(String.valueOf(msg));
        usdClient.receive();
        System.out.println(encryptedmsg);
    }

    void receive() throws IOException, InterruptedException {
        Path socketPath = Path.of(System.getProperty("user.home")).resolve("wei.socket");
        Files.deleteIfExists(socketPath);
        UnixDomainSocketAddress socketAddress = getAddress(socketPath);

        ServerSocketChannel serverChannel = createServerSocketChannel(socketAddress);

        SocketChannel channel = serverChannel.accept();

        while (true) {
            AtomicReference<String> output = new AtomicReference<>("");
            readSocketMessage(channel).ifPresent(output::set);
            String foutput = String.valueOf(output);
            if (!foutput.isEmpty())
            {
                encryptedmsg = foutput;
                return;
            }
            Thread.sleep(100);
        }
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

    ServerSocketChannel createServerSocketChannel(UnixDomainSocketAddress socketAddress) throws IOException {
        ServerSocketChannel serverChannel = ServerSocketChannel.open(StandardProtocolFamily.UNIX);
        serverChannel.bind(socketAddress);
        return serverChannel;
    }

    void send(String msg) throws IOException {
        Path socketPath = Path.of(System.getProperty("user.home")).resolve("wei.socket");
        UnixDomainSocketAddress socketAddress = getAddress(socketPath);
        SocketChannel channel = openSocketChannel(socketAddress);
        writeMessage(channel, msg);
    }

    UnixDomainSocketAddress getAddress(Path socketPath) {
        return UnixDomainSocketAddress.of(socketPath);
    }

    SocketChannel openSocketChannel(UnixDomainSocketAddress socketAddress) throws IOException {
        SocketChannel channel = SocketChannel.open(StandardProtocolFamily.UNIX);
        channel.connect(socketAddress);
        return channel;
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
}
