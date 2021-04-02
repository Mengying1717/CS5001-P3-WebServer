
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.File;
import java.net.Socket;

/**
 * ConnectionHandler class handle the connection between server and client.
 * @version 1.0
 * @since 30 October 2020
 */
public class ConnectionHandler extends Thread {
    private String root;
    private Socket conn;
    private InputStream is; //get data from client on the InputStream
    private OutputStream os; //send data back to client on the OutputStream
    private BufferedReader br; //use BufferReader to read the getting client data
    private String path;

    /**
     * Constructor for ConnectionHandler object.
     * @param root - the directory from which the server will serve documents to clients
     * @param conn - the socket in which connection is hosted
     * @param counter - the record of the number of the connection
     */
    public ConnectionHandler(String root, Socket conn, int counter) {
        this.root = root;
        this.conn = conn;
        try {
            is = conn.getInputStream();
            os = conn.getOutputStream();
            br = new BufferedReader(new InputStreamReader(is));
        }
        catch (IOException ioe) {
            System.out.println("ConnectionHandler:" + ioe.getMessage());
        }
    }

    /**
     *run method is invoked when the Thread's start method is invoked.
     */
        public void run() {
        System.out.println("new ConnectionHandler thread started...");
        try {
            Request request = new Request(conn, this.root);
            Response response = new Response(conn, request);
            File logfile = new File("logfile");
            response.respLogger(request, logfile);
        } catch (Exception e) {
            System.out.println("ConnectionHandler: run" + e.getMessage());
        }
        cleanup();
    }

    /**
     *cleanup method close the connection after server have handled the request.
     */
    private void cleanup() {
        System.out.println("ConnectionHandler: ... cleaning up and exiting ... ");
        try {
            br.close();
            is.close();
            conn.close();
        } catch (IOException ioe) {
            System.out.println("ConnectionHandler:cleanup " + ioe.getMessage());
        }
    }
}
