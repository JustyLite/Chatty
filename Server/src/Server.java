import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Server {
    private static final int serverPort = 25567;
    private static ArrayList<Socket> sockets = new ArrayList<>();
    private static void sendToAll(String message) throws IOException {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        String currentTime = dateFormat.format(new Date());
        for (Socket socket : sockets) {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            writer.write("["+currentTime+"]"+message + "\n");
            writer.flush();
        }
    }
    private static void listen(Socket socket) {
        new Thread(()->{
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String message;
                while ((message = reader.readLine())!=null) {
                    sendToAll(message);
                    System.out.println(message);
                }
            } catch (IOException e) {
                sockets.remove(socket);
                System.out.print("\rActive connections: " + getActiveConnections());
            }
        }).start();
    }
    private static int getActiveConnections() {
        return sockets.size();
    }
    public static void main(String[] args) throws IOException {
        System.out.println("SERVER IS RUNNING ON PORT: "+serverPort);
        ServerSocket serverSocket = new ServerSocket(serverPort);
        while (true) {
            System.out.print("\rActive connections: " + getActiveConnections());
            Socket socket = serverSocket.accept();
            sockets.add(socket);
            listen(socket);
        }
    }
}