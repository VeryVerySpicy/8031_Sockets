import java.io.*;
import java.net.*;

public class Server {
    int port;
    boolean stopAfterOne = false;
    String keyword = "";

    public static void main(String[] args) throws IOException {
        Server server = new Server();
        server.parseArgs(args);
        server.waitForWork(server.port);
    }

    void parseArgs(String[] args)
    {
        if (args.length != 1)
        {
            System.err.println("Incorrect number of arguments.");
            System.exit(1);
        }
        else
        {
            port = Integer.parseInt(args[0]);
        }
    }

    void waitForWork(int port) {
        ServerSocket serverSocket = null;
        try
        {
            serverSocket = new ServerSocket(port);
            serverSocket.setReuseAddress(true);

            while (true)
            {
                Socket cSocket = serverSocket.accept();
                System.out.println("New client connected: " + cSocket.getInetAddress().getHostAddress());

                Decrypter clientDecrypter = new Decrypter(cSocket);
                new Thread(clientDecrypter).start();
                if (stopAfterOne)
                    return;
            }
        }
        catch (UnknownHostException e)
        {
            System.err.println("Unknown host. " + e.getMessage());
            System.exit(1);
        }
        catch (IOException e)
        {
            System.err.println("I/O exception. " + e.getMessage());
            System.exit(1);
        }
        finally
        {
            if (serverSocket != null)
            {
                try
                {
                    serverSocket.close();
                }
                catch (IOException e) {
                    System.err.println("Could not close server socket.");
                    System.exit(1);
                }
            }
        }
    }

    private static class Decrypter implements Runnable
    {
        private final Socket cSocket;

        public Decrypter(Socket socket)
        {
            cSocket = socket;
        }

        public void run()
        {
            PrintWriter out = null;
            BufferedReader in = null;
            String msg = "";
            String keyword = "";
            try
            {
                out = new PrintWriter(cSocket.getOutputStream(), true);
                InputStream inputStream = cSocket.getInputStream();
                DataInputStream dataInputStream = new DataInputStream(inputStream);
                msg = dataInputStream.readUTF();
                String[] splitString = msg.split("\n", 2);
                keyword = splitString[0];
                msg = splitString[1];
                out.print(decryptMsg(msg, keyword));
                out.flush();
            }
            catch (IOException e)
            {
                System.err.println("I/O exception. " + e.getMessage());
                System.exit(1);
            }
            finally
            {
                if (out != null)
                {
                    out.close();
                }
            }
        }
    }

    static String decryptMsg(String msg, String keyword)
    {
        StringBuilder emsg = new StringBuilder();
        if (keyword.length() < msg.length())
        {
            keyword = keyExtend(msg.length(), keyword);
        }

        for (int i = 0; i < msg.length(); i++)
        {
            if (Character.isLetter(msg.charAt(i)))
            {
                char nchar = msg.charAt(i);
                int ascii = (int) nchar - 'A';
                int shift = keyword.charAt(i) - 'A';
                nchar = (char) (((ascii - shift) % 26 + 26) % 26 + 'A');
                emsg.append(nchar);
            }
        }
        return emsg.toString();
    }

    static String keyExtend(int keySize, String keyword)
    {
        int initSize = keyword.length();
        for (int i = 0;; i++)
        {

            if (initSize == i)
            {
                i = 0;
            }
            if (keyword.length() == keySize)
            {
                break;
            }
            if (keyword.length() > keySize)
            {
                System.err.println("Key is too long.");
                System.exit(1);
            }
            keyword += keyword.charAt(i);
        }
        System.out.println(keyword);
        return keyword;
    }
}
