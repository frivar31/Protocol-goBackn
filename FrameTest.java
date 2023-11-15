public class FrameTest {
    public static void main(String[] args) {

    byte type = 0x01;
    byte num = 0x01;
    byte[] data = "Hello, World!".getBytes();

// Calculez le CRC pour les données
    byte[] crc = CRC.calculateCRC(data);

// Créez une trame avec le bon CRC
    Frame frame = new Frame(type, num, data, crc);

// Encodez la trame
    byte[] encodedFrame = frame.toBytes();

// Décodage et vérification
    try {
        Frame decodedFrame = Frame.parseFrame(encodedFrame);
        System.out.println("Trame décodée avec succès!");
    } catch (IllegalArgumentException e) {
        e.printStackTrace();
    }
}}
