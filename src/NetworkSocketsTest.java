import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class NetworkSocketsTest {

    static Client client;
    static Server server;


    @BeforeAll
    static void setUp()
    {
        client = new Client();
        server = new Server();
    }

    @Test
    public void parseArgsTooFew()
    {
        try {
            String[] args = new String[1];
            client.parseArgs(args);
        }
        catch (TestException e) {
            assertEquals("Incorrect number of arguments.", e.getErrMsg());
        }
    }

    @Test
    public void parseArgsTooMany()
    {
        try {
            String[] args = new String[9];
            client.parseArgs(args);
        }
        catch (TestException e) {
            assertEquals("Incorrect number of arguments.", e.getErrMsg());
        }
    }

    @Test
    public void readFileNoName()
    {
        try {
            String filename = "";
            client.readFile(filename);
        }
        catch (TestException e) {
            assertEquals("Filename is empty.", e.getErrMsg());
        }
    }

    @Test
    public void readFileSmall()
    {
        try {
            String filename = "test.txt";
            client.readFile(filename);
        }
        catch (TestException e) {
            assertEquals("ATTACKATDAWNattackatdawn", client.fileContents);
        }
    }

    @Test
    public void readFileLarge()
    {
        try {
            String filename = "longtest.txt";
            client.readFile(filename);
        }
        catch (TestException e) {
            assertEquals("ATTACKATDAWNattackatdawnATTACKATDAWNattackatdawnATTACKATDAWNattackatdawnATTACKATDAWNattackatdawnATTACKATDAWNattackatdawnATTACKATDAWNattackatdawnATTACKATDAWNattackatdawnATTACKATDAWNattackatdawnATTACKATDAWNattackatdawnATTACKATDAWNattackatdawnATTACKATDAWNattackatdawnATTACKATDAWNattackatdawnATTACKATDAWNattackatdawnATTACKATDAWNattackatdawnATTACKATDAWNattackatdawnATTACKATDAWNattackatdawnATTACKATDAWNattackatdawnATTACKATDAWNattackatdawnATTACKATDAWNattackatdawnATTACKATDAWNattackatdawnATTACKATDAWNattackatdawnATTACKATDAWNattackatdawnATTACKATDAWNattackatdawnATTACKATDAWNattackatdawnATTACKATDAWNattackatdawnATTACKATDAWNattackatdawnATTACKATDAWNattackatdawnATTACKATDAWNattackatdawnATTACKATDAWNattackatdawnATTACKATDAWNattackatdawnATTACKATDAWNattackatdawnATTACKATDAWNattackatdawnATTACKATDAWNattackatdawnATTACKATDAWNattackatdawnATTACKATDAWNattackatdawnATTACKATDAWNattackatdawnATTACKATDAWNattackatdawnATTACKATDAWNattackatdawnATTACKATDAWNattackatdawnATTACKATDAWNattackatdawnATTACKATDAWNattackatdawnATTACKATDAWNattackatdawnATTACKATDAWNattackatdawnATTACKATDAWNattackatdawnATTACKATDAWNattackatdawnATTACKATDAWNattackatdawnATTACKATDAWNattackatdawnATTACKATDAWNattackatdawnATTACKATDAWNattackatdawnATTACKATDAWNattackatdawnATTACKATDAWNattackatdawnATTACKATDAWNattackatdawnATTACKATDAWNattackatdawnATTACKATDAWNattackatdawnATTACKATDAWNattackatdawnATTACKATDAWNattackatdawnATTACKATDAWNattackatdawnATTACKATDAWNattackatdawnATTACKATDAWNattackatdawnATTACKATDAWNattackatdawnATTACKATDAWNattackatdawnATTACKATDAWNattackatdawnATTACKATDAWNattackatdawnATTACKATDAWNattackatdawnATTACKATDAWNattackatdawnATTACKATDAWNattackatdawnATTACKATDAWNattackatdawnATTACKATDAWNattackatdawnATTACKATDAWNattackatdawnATTACKATDAWNattackatdawnATTACKATDAWNattackatdawnATTACKATDAWNattackatdawnATTACKATDAWNattackatdawnATTACKATDAWNattackatdawnATTACKATDAWNattackatdawnATTACKATDAWNattackatdawnATTACKATDAWNattackatdawnATTACKATDAWNattackatdawnATTACKATDAWNattackatdawnATTACKATDAWNattackatdawnATTACKATDAWNattackatdawnATTACKATDAWNattackatdawnATTACKATDAWNattackatdawnATTACKATDAWNattackatdawnATTACKATDAWNattackatdawnATTACKATDAWNattackatdawnATTACKATDAWNattackatdawnATTACKATDAWNattackatdawnATTACKATDAWNattackatdawnATTACKATDAWNattackatdawnATTACKATDAWNattackatdawnATTACKATDAWNattackatdawnATTACKATDAWNattackatdawnATTACKATDAWNattackatdawnATTACKATDAWNattackatdawnATTACKATDAWNattackatdawnATTACKATDAWNattackatdawnATTACKATDAWNattackatdawnATTACKATDAWNattackatdawnATTACKATDAWNattackatdawnATTACKATDAWNattackatdawnATTACKATDAWNattackatdawnATTACKATDAWNattackatdawnATTACKATDAWNattackatdawnATTACKATDAWNattackatdawnATTACKATDAWNattackatdawnATTACKATDAWNattackatdawnATTACKATDAWNattackatdawnATTACKATDAWNattackatdawnATTACKATDAWNattackatdawn", client.fileContents);
        }
    }
}