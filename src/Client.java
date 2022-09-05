import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class Client implements Runnable {
    private final Socket socket;
    private PrintStream out;
    private String userName;
    private boolean isActive;

    public Client(Socket socket) {
        this.socket = socket;
        this.isActive = true;
    }

    public String getUserName() {
        return userName;
    }

    public boolean isActive() {
        return isActive;
    }

    public void receive(String message, String userName) {
        out.println(userName + ": " + message + '\u0007');

    }

    public void receive(String message) {
        out.println(message + '\u0007');
    }

    @Override
    public void run() {
        try {
            // получаем потоки ввода и вывода GG
            InputStream is = socket.getInputStream();
            OutputStream os = socket.getOutputStream();

            // создаем удобные средства ввода и вывода
            Scanner in = new Scanner(is);
            out = new PrintStream(os);

            // читаем из сети и пишем в сеть
            out.println("Welcome to chat!");
            out.println("What's your name?");
            userName = in.nextLine();
            out.println("Hello, " + userName + "!" + " Chat is started! Type Ctrl-C to exit. Type \"SU\" to show users.");
            String input = in.nextLine();
            while (!input.equals(Character.toString('\u0003'))) {
                if (input.equals("SU")) {
                    ChatServer.showUsers(userName);
                } else {
                    ChatServer.sendAll(input, userName);
                }
                input = in.nextLine();
            }
            socket.close();
            isActive = false;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}