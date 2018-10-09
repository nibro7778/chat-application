
package clientserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

public class ChatServer {

    private final int port;
    private final Set<String> userNames = new HashSet<>();
    private final Set<UserThread> userThreads = new HashSet<>();

    public ChatServer(int port) {
        this.port = port;
    }

    public void execute() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {

            while (true) {
                Socket socket = serverSocket.accept();
                UserThread newUser = new UserThread(socket, this);
                userThreads.add(newUser);
                newUser.start();
            }

        } catch (IOException ex) {
            System.out.println("Error in the server: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        ChatServer server = new ChatServer(8000);
        server.execute();
    }    
    void broadcast(String message, UserThread excludeUser) {
        userThreads.stream().filter((aUser) -> (aUser != excludeUser)).forEachOrdered((aUser) -> {
            aUser.sendMessage(message);
        });
    }
}
