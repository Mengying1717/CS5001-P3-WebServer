
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * WebServer class listens for the connection request.
 * @version 1.0
 * @since 30 October 2020
 */
public class WebServer {
    private static final int THREAD_LIMIT = 500;
    private static final int THREAD_SLEEP_TIME = 500;
    private ServerSocket ss; //listen for client connection requests on this server socket

    /**
     * Constructor for Webserver object.
     * @param root - the directory from which the server will serve documents to clients
     * @param port - the port on which the server should listen
     * @throws InterruptedException - Interrupted Exception
     */
    public WebServer(String root, int port) throws InterruptedException {
        try {
            int counter = 0;
            ss = new ServerSocket(port); //Create a new ServerSocket object，which listen on port
            System.out.println("Server started...listening on port" + port + "...");

            while (true) { //After client accept, return connection to the client
                counter++;
                Socket conn = ss.accept();
                System.out.println("Server got new connection request from" + conn.getInetAddress());
                ConnectionHandler ch = new ConnectionHandler(root, conn, counter);
                if (counter >= THREAD_LIMIT) {
                    Thread.sleep(THREAD_SLEEP_TIME);
                }
                ch.start(); //run for the connection handler
            }
        } catch (IOException ioe) {
            System.out.println("Ooops" + ioe.getMessage());
        }
    }
}

