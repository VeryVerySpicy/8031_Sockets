import java.io.*;
import java.net.*;

public class Server {
    int port;
    String keyword = "";

    public static void main(String[] args) throws IOException {
        Server server = new Server();
        server.parseArgs(args);
        server.encyptMsg(server.port);
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

    void encyptMsg(int port) {
        PrintWriter out = null;
        BufferedReader in = null;
        String msg = "";
        try
        {
            ServerSocket sSocket = new ServerSocket(port);
            Socket cSocket = sSocket.accept();
            cSocket.setSoTimeout(5000);
            out = new PrintWriter(cSocket.getOutputStream(), true);
            InputStream inputStream = cSocket.getInputStream();
            DataInputStream dataInputStream = new DataInputStream(inputStream);
            msg = dataInputStream.readUTF();
            String[] splitString = msg.split("\n", 2);
            keyword = splitString[0];
            msg = splitString[1];
            out.print(vigCipher(msg, keyword));
            out.flush();
            out.close();
        }
        catch(SocketTimeoutException e)
        {
            assert out != null;
            out.print(vigCipher(msg, keyword));
            out.flush();
            out.close();

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
    }

    String vigCipher (String msg, String keyword)
    {
        StringBuilder emsg = new StringBuilder();
        if (keyword.length() < msg.length())
        {
            keyword = keyExtend(msg.length(), keyword);
        }
        final String lower = "abcdefghijklmnopqrstuvwxyz";
        final String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

        for (int i = 0; i < msg.length(); i++)
        {
            int pos = -1;
            int shift = -1;
            char nchar = 0;
            keyword = keyword.toLowerCase();
            shift = lower.indexOf(keyword.charAt(i));
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
            emsg.append(nchar);
        }
        return emsg.toString();
    }

    String keyExtend (int keySize, String keyword)
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
