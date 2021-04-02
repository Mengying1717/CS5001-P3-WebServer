
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.IOException;
import java.net.ProtocolException;
import java.net.Socket;
import java.util.StringTokenizer;

/**
 * Request class phase the request.
 * @version 1.0
 * @since 30 October 2020
 */
public class Request {
    private String root;
    private String version;
    private InputStream is;
    private BufferedReader br;
    private String line;
    private Socket conn;
    private String filename;
    private String method;
    private String fullPath;

    /**
     * Constructor for Request object.
     * @param conn - the socket in which connection is hosted
     * @param root - the directory from which the server will serve documents to clients
     * @throws IOException - I/O
     * @throws ProtocolException - Protocol Exception
     */
    public Request(Socket conn, String root) throws IOException, ProtocolException {
        try {
            this.conn = conn;
            this.root = root;
            is = conn.getInputStream();
            br = new BufferedReader(new InputStreamReader(is));
            line = br.readLine();
            parseRequest(line);
        } catch (Exception e) {
            System.out.println("\"ConnectionHandler: run\" + e.getMessage()");
        }
    }

    /**
     * parseResquest method parse the request from client to three parts(method, filename, and version).
     * @param s - s represents the string which need to be parsed
     * @throws IOException - I/O
     * @throws ProtocolException - Protocol Exception
     */
    private void parseRequest(String s) throws IOException, ProtocolException {
        StringTokenizer st = new StringTokenizer(s, " ");
        this.method = st.nextToken();
        this.filename = st.nextToken();
        this.version = st.nextToken();
        while (st.hasMoreTokens()) {
            s += method;
            s += filename;
            s += version;
        }
        this.fullPath = root + filename;
    }

    /**
     * Getter.
     * @return name of request method
     */
    public String getMethod() {
        return method;
    }

    /**
     * Getter.
     * @return filename
     */
    public String getFilename() {
        return filename;
    }

    /**
     * Getter.
     * @return fullPath
     */
    public String getFullPath() {
        return fullPath;
    }
}
