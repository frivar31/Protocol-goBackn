import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Recepteur {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java Receiver <Numero_Port>");
            return;
        }

        int port = Integer.parseInt(args[0]);

        try (ServerSocket serverSocket = new ServerSocket(port);
             Socket socket = serverSocket.accept();
             DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
             FileOutputStream fileOutputStream = new FileOutputStream(dataInputStream.readUTF())
        ) {
            int command = dataInputStream.readInt();
            byte[] buffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = dataInputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, bytesRead);
            }

            System.out.println("Le fichier a été reçu avec succès.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
