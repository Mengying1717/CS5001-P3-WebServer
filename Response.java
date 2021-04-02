
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.net.Socket;
import java.util.Date;

/**
 * Response class return the file based on the different request method.
 * @version 1.0
 * @since 30 October 2020
 */
public class Response {
    private Socket conn;
    private Request request;
    private DataOutputStream dos;
    private String path;
    private String filename;
    private StringBuilder logresponse = new StringBuilder();
    private Date date = new Date();

    /**
     * Constructor for Response object.
     * @param conn - the socket in which connection is hosted
     * @param request - the request from client
     * @throws IOException - I/O
     */
    public Response(Socket conn, Request request) throws IOException {
        this.conn = conn;
        this.request = request;
        this.path = request.getFullPath();
        dos = new DataOutputStream(conn.getOutputStream());
        this.filename = request.getFilename();

        if (request.getMethod().equals("GET")) {
            respGET(path);
        } else if (request.getMethod().equals("HEAD")) {
            respHEAD(path);
        } else if (request.getMethod().equals("DELETE")) {
            respDELET(path);
        } else {
            respNotImplemented();
        }
        dos.close();
    }

    /**
     * getFile method read the file content and store the content in a bytes array.
     * @param path - the fullPath of the file (file root + filename)
     * @return dataBody
     * @throws IOException - I/O
     */
    public byte[] getFile(String path) throws IOException {
        try {
            FileInputStream inputFile = new FileInputStream(path);
            byte[] dataBody = new byte[inputFile.available()];
            inputFile.read(dataBody);
            return dataBody;
        } catch (FileNotFoundException e) {
            dos.writeBytes("HTTP/1.1 404 Not Found\r\n");
            logresponse.append("HTTP/1.1 404 Not Found\r\n");
            dos.flush();
            return null;
        } catch (IOException e) {
            System.out.println("getFile error: " + e.getMessage());
            return null;
        }
    }

    /**
     * respGET method generate the response for request method of GET.
     * @param path - the fullPath of the file (file root + filename)
     * @throws IOException - I/O
     */
    public void respGET(String path) throws IOException {
        byte[] dataBody = getFile(path);
        if (dataBody != null) {
            try {
                dos.writeBytes("HTTP/1.1 200 OK\r\n");
                dos.writeBytes("Server: Simple Java Http\r\n");
                dos.writeBytes("Content-Type: " + fileType() + "\r\n");
                dos.writeBytes("Content-Length: " + dataBody.length + "\r\n\r\n");
                logresponse.append("HTTP/1.1 200 OK\r\n" + "Server: Simple Java Http\r\n" + "Content-Type: " + fileType() + "\r\n" + "Content-Length: " + dataBody.length + "\r\n\r\n");
                dos.write(dataBody);
                dos.flush();
            } catch (Exception e) {
                dos.writeBytes("HTTP/1.1 404 Not Found\r\n");
                logresponse.append("HTTP/1.1 404 Not Found\r\n");
                dos.writeBytes("\r\n");
                dos.flush();
            }
        }
    }

    /**
     * respHEAD method generate the response for request method of HEAD.
     * @param path - the fullPath of the file (file root + filename)
     * @throws IOException - I/O
     */
    public void respHEAD(String path) throws IOException {
        byte[] dataBody = getFile(path);
        if (dataBody != null) {
            try {
                dos.writeBytes("HTTP/1.1 200 OK\r\n");
                dos.writeBytes("Server: Simple Java Http\r\n");
                dos.writeBytes("Content-Type: " + fileType() + "\r\n");
                dos.writeBytes("Content-Length: " + dataBody.length + "\r\n\r\n");
                logresponse.append("HTTP/1.1 200 OK\r\n" + "Server: Simple Java Http\r\n" + "Content-Type: " + fileType() + "\r\n" + "Content-Length: " + dataBody.length + "\r\n\r\n");
                dos.flush();
            } catch (Exception e) {
                dos.writeBytes("HTTP/1.1 404 Not Found\r\n");
                logresponse.append("HTTP/1.1 404 Not Found\r\n");
                dos.writeBytes("\r\n");
                dos.flush();
            }
        }
    }
    /**
     * respDELETE method generate the response for request method of DELETE.
     * @param path - the fullPath of the file (file root + filename)
     * @throws IOException - I/O
     */
    public void respDELET(String path) throws IOException {
        File file = new File(path);
        if (file.exists()) {
            try {
                file.delete();
                dos.writeBytes("HTTP/1.1 200 OK\r\n");
                logresponse.append("HTTP/1.1 200 OK\r\n");
                dos.flush();
            } catch (Exception e) {
                dos.writeBytes("HTTP/1.1 202 Accepted\r\n");
                dos.writeBytes("\r\n");
                logresponse.append("HTTP/1.1 202 Accepted\r\n");
                dos.flush();
            }
        } else {
            dos.writeBytes("HTTP/1.1 204 No Content\r\n");
            logresponse.append("HTTP/1.1 204 No Content\r\n");
            dos.flush();
        }
    }

    /**
     * respLogger method write log content(date, request method, response content) to a file.
     * @param request - each time request from client
     * @param logFile - the file which stores the log content
     * @throws IOException - I/O
     */

    public void respLogger(Request request, File logFile) throws IOException {
        String logContent = "";
        logContent += "\r\nLogging date: " + date;
        logContent += "\r\nRequest Type: " + request.getMethod();
        logContent += "\r\nResponse: \r\n";
        FileWriter fw = null;
        BufferedWriter bw = null;
        try {
            fw = new FileWriter(logFile, true);
            bw = new BufferedWriter(fw);
            bw.write(logContent);
            bw.write(this.logresponse.toString());
            bw.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }
                if (fw != null) {
                    fw.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * private fileType method return the different Content-Type to the response based on the type of file.
     * @return Content-Type String
     */
    private String fileType() {
        if (filename.endsWith(".html")) {
            return "text/html";
        } else if (filename.endsWith(".jpg")) {
            return  "image/jpeg";
        } else if (filename.endsWith(".png")) {
            return "image/png";
        } else if (filename.endsWith(".gif")) {
            return "image/gif";
        } else {
            return "text/plain";
        }
    }

    /**
     * respNotImplemented method generate the response for request when the request is not implemented.
     * @throws IOException - I/O
     */
    public void respNotImplemented() throws IOException {
        dos.writeBytes("HTTP/1.1 501 Not Implemented\r\n");
        dos.writeBytes("\r\n");
        logresponse.append("HTTP/1.1 501 Not Implemented\r\n");
        dos.flush();
    }
}
