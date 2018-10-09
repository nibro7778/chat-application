
package clientserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;


public class UserThread extends Thread {

    private final Socket socket;
    private final ChatServer server;
    private PrintWriter writer;

    public UserThread(Socket socket, ChatServer server) {
        this.socket = socket;
        this.server = server;
    }

    @Override
    public void run() {
        try {
            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            OutputStream output = socket.getOutputStream();
            writer = new PrintWriter(output, true);
            server.broadcast(reader.readLine(), this);
            
        } catch (IOException ex) {
            System.out.println("Error in UserThread: " + ex.getMessage());
        }
    }
    
    void sendMessage(String message) {
        writer.println(message);
    }
}
