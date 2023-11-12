import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;

public class Emetteur {
    public static void main(String[] args) {
        if (args.length != 4) {
            System.out.println("Usage: java Sender <Nom_Machine> <Numero_Port> <Nom_fichier> <0>");
            return;
        }

        String serverName = args[0];
        int port = Integer.parseInt(args[1]);
        String fileName = args[2];
        int command = Integer.parseInt(args[3]);

        try (
                Socket socket = new Socket(serverName, port);
                FileInputStream fileInputStream = new FileInputStream(fileName);
                DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream())
        ) {
            dataOutputStream.writeUTF(fileName);
            dataOutputStream.writeInt(command);

            byte[] buffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                dataOutputStream.write(buffer, 0, bytesRead);
            }

            System.out.println("Le fichier a été envoyé avec succès.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
