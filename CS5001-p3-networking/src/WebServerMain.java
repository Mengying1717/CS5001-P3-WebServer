
/**
 * A main class to create Web Server object.
 * @version 1.0
 * @since 30 October 2020
 */
public class WebServerMain {
    private static final int TWO_ARGUS_LENGTH = 2;

    /**
     * A main method to accept two arguments from the command line.
     * @param args - command line arguments
     * @throws InterruptedException Interrupted Exception
     */
    public static void main(String[] args) throws InterruptedException {
        if (args.length == TWO_ARGUS_LENGTH) {
            String root = args[0];
            int port = Integer.parseInt(args[1]);
            //Create server object with DEFAULT_PORT
            new WebServer(root, port);
        } else {
            System.out.println("Usage: java WebServerMain <document_root> <port>\n");
            System.exit(1);
        }
    }
}
