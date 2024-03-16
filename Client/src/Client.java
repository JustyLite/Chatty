import java.io.*;
import java.net.Socket;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Client {
    static private final String version = "DESKTOP v1.0";
    static private String IP;
    static private int port;
    static private String nickname;
    static private Socket socket;
    static private boolean isCmd;
    private static String getIP() {
        Scanner in = new Scanner(System.in);
        System.out.print("Введите IP-адрес сервера: ");
        return in.nextLine();
    }
    private static int getPort() {
        while (true) {
            try {
                Scanner in = new Scanner(System.in);
                System.out.print("Введите порт сервера: ");
                return in.nextInt();
            } catch (InputMismatchException e) {
                System.out.print("Неверный порт!\nПовторите попытку. ");
            }
        }
    }
    private static String getNickname() {
        System.out.print("Введите Ваш никнейм: ");
        return getMessage();
    }
    private static void getInfo(String status) {
        System.out.println("IP: " + IP);
        System.out.println("PORT: " + port);
        System.out.println("NICKNAME: " + nickname);
        System.out.println("STATUS: " + status);
    }
    private static boolean connect(String IP, int port) {
        try {
            System.out.println("Подключение...");
            socket = new Socket(IP, port);
            return true;
        } catch (IOException e) {
            return false;
        }
    }
    private static void send(String message) {
        try {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            writer.write("["+nickname+"] >>> " + message + "\n");
            writer.flush();
        } catch (IOException e) {
            System.out.println("СОЕДИНЕНИЕ ПОТЕРЯНО");
        }
    }
    private static void receive() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        String message;
        while ((message = reader.readLine())!=null) {
            System.out.println(message);
        }
    }
    private static String getMessage() {
        Scanner in;
        if (isCmd) in = new Scanner(System.in, "cp866");
        else in = new Scanner(System.in);
        return in.nextLine();
    }
    public static void main(String[] args) {
        if (args.length>0) isCmd = true;
        System.out.println("VERSION: " + version);
        IP = getIP();
        port = getPort();
        nickname = getNickname();
        if (connect(IP, port)) {
            getInfo("ПОДКЛЮЧЕНО");
        } else {
            getInfo("ОШИБКА ПРИ ПОДКЛЮЧЕНИИ");
            System.exit(0);
        }

        new Thread(()->{
            while (true) {
                String message = getMessage();
                send(message);
            }
        }).start();

        new Thread(()->{
            try {
                receive();
            } catch (IOException e) {
                System.out.println("СОЕДИНЕНИЕ ПОТЕРЯНО");
            }
        }).start();
    }
}