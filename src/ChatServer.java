import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class ChatServer {
    // коллекция всех клиентов присоединенных к серверу
    static ArrayList<Client> clients = new ArrayList<>();

    // вызываем метод receive у каждого клиента для рассылки сообщения
    public static void sendAll(String message, String userName) {
        for (Client client : clients) {
            if (!userName.equals(client.getUserName())) {
                client.receive(message, userName);
            }
        }
    }

    // получаем список пользователей и сообщаем о них вызвавшему пользователю
    public static void getClients(String userName) {
        StringBuilder clientsString = new StringBuilder();
        clientsString.append("| ");
        for (Client client : clients) {
            clientsString.append(client.getUserName()).append(" | ");
        }
        for (Client client : clients) {
            if (userName.equals(client.getUserName())) {
                client.receive(clientsString.toString().trim(), "");
            }
        }
    }

    public static void main(String[] args) throws IOException {
        // создаем серверный сокет на порту 1234
        try (ServerSocket server = new ServerSocket(1234)) {
            while (true) {
                System.out.println("Waiting...");
                // ждем клиента
                Socket socket = server.accept();
                System.out.println("Client connected!");
                Client client = new Client(socket);
                clients.add(client);
                Thread thread = new Thread(client);
                thread.start();
            }
        }
    }
}

class Client implements Runnable {
    private final Socket socket;
    private PrintStream out;
    private String userName;

    public String getUserName() {
        return userName;
    }

    public Client(Socket socket) {
        this.socket = socket;
    }

    public void receive(String message, String userName) {
        if (userName.equals("")) {
            out.println(message + '\u0007');
        } else {
            out.println(userName + ": " + message + '\u0007');
        }
    }

    @Override
    public void run() {
        try {
            // получаем потоки ввода и вывода
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
                    ChatServer.getClients(userName);
                } else {
                    ChatServer.sendAll(input, userName);
                }
                input = in.nextLine();
            }

            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
