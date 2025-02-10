import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

public class Client {

    String encryptedmsg = "";
    String ip = "";
    String filename = "";
    String keyword;
    int port = 0;

    public static void main(String[] args) throws Exception
    {
        Client client = new Client();
        client.parseArgs(args);
        String msg = client.readFile(client.filename);
        client.encryptedmsg = client.encryptMsg(msg, client.ip, client.port, client.keyword);
        System.out.println("The encrypted message is: \n");
        System.out.println(client.encryptedmsg);
    }

    void parseArgs(String[] args)
    {
        if (args.length != 4)
        {
            System.err.println("Incorrect number of arguments.");
            System.exit(1);
        }
        else
        {
            filename = args[0];
            ip = args[1];
            port = Integer.parseInt(args[2]);
            keyword = args[3];
        }
    }

    String readFile(String filename) throws IOException
    {
        if (filename.isEmpty())
        {
            System.exit(1);
        }
        File file;
        file = new File(filename);
        StringBuilder msg = new StringBuilder();
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
            try {
                if (reader != null)reader.close();
            } catch (IOException ex) {
                System.err.println("I/O exception. " + ex.getMessage());
                System.exit(1);
            }
        }
        return msg.toString();
    }

    String encryptMsg(String msg, String ip, int port, String keyword) throws IOException
    {
        String emsg = "";
        try {
            Socket socket = new Socket(ip, port);
            OutputStream outputStream = socket.getOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
            dataOutputStream.writeUTF(keyword + msg);
            dataOutputStream.flush(); // send the message

            //PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            //out.print(keyword);
            System.out.println("encryptMsg: " + msg);
            //out.print(msg);
            //out.flush();
            String inLine;
            while((inLine = in.readLine()) != null)
            {
                emsg += inLine;
            }
            if (emsg.isEmpty())
            {
                System.err.println("No message received.");
                System.exit(1);
            }
            in.close();
            dataOutputStream.close(); // close the output stream when we're done.
        }
        catch (SocketException e)
        {
            System.err.println("Socket exception: " + e.getMessage());
            System.exit(1);
        }
        return emsg;
    }
}
