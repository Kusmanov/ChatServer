import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ChatServer {
    // ��������� ���� �������� �������������� � �������
    static ArrayList<Client> clients = new ArrayList<>();

    // �������� ����� receive � ������� ������� ��� �������� ���������
    public static void sendAll(String message, String userName) {
        for (Client client : clients) {
            if (!userName.equals(client.getUserName())) {
                client.receive(message, userName);
            }
        }
    }

    // �������� ������ ������������� � �������� � ��� ���������� ������������ GG
    public static void showUsers(String userName) {
        StringBuilder clientsString = new StringBuilder();
        clientsString.append("| ");
        for (Client client : clients) {
            if (client.isActive()) {
                clientsString.append(client.getUserName()).append(" | ");
            }
        }
        for (Client client : clients) {
            if (userName.equals(client.getUserName())) {
                client.receive(clientsString.toString().trim());
            }
        }
    }

    public static void main(String[] args) throws IOException {
        // ������� ��������� ����� �� ����� 1234
        try (ServerSocket server = new ServerSocket(1234)) {
            while (true) {
                System.out.println("Waiting...");
                // ���� �������
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


