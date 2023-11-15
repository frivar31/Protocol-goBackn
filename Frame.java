import java.util.Arrays;

public class Frame {
    private static final byte FLAG = 0x7E;
    private byte type;
    private byte num;
    private byte[] data;
    private byte[] crc;

    // Constructeur
    public Frame(byte type, byte num, byte[] data, byte[] crc) {
        this.type = type;
        this.num = num;
        this.data = data;
        this.crc = crc;
    }

    // Getters et setters
    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public byte getNum() {
        return num;
    }

    public void setNum(byte num) {
        this.num = num;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public byte[] getCRC() {
        return crc;
    }

    // Méthode pour obtenir le contenu de la trame sans CRC pour le calcul
    private byte[] getFrameContentsWithoutCRC() {
        int totalLength = 1 + 1 + (data != null ? data.length : 0);
        byte[] frameContents = new byte[totalLength];
        int position = 0;
        frameContents[position++] = type;
        frameContents[position++] = num;
        if (data != null && data.length > 0) {
            System.arraycopy(data, 0, frameContents, position, data.length);
        }
        return frameContents;
    }

    // Méthode pour le bit stuffing
    private byte[] performBitStuffing(byte[] data) {
        StringBuilder bitString = new StringBuilder();
        for (byte b : data) {
            String bits = Integer.toBinaryString(b & 0xFF);
            bits = String.format("%8s", bits).replace(' ', '0'); // 8 bits par byte
            bitString.append(bits);
        }
        String stuffedString = bitString.toString().replace("11111", "111110");
        int byteSize = (stuffedString.length() + 7) / 8;
        byte[] stuffedData = new byte[byteSize];
        for (int i = 0; i < stuffedString.length(); i += 8) {
            int endIndex = Math.min(i + 8, stuffedString.length());
            String byteString = stuffedString.substring(i, endIndex);
            while (byteString.length() < 8) {
                byteString += "0"; // Compléter avec des zéros
            }
            byte byteValue = (byte) Integer.parseInt(byteString, 2);
            stuffedData[i / 8] = byteValue;
        }
        return stuffedData;
    }

    // Méthode pour retirer le bit stuffing
    private static byte[] removeBitStuffing(byte[] stuffedData) {
        StringBuilder bitString = new StringBuilder();
        for (byte b : stuffedData) {
            String bits = Integer.toBinaryString(b & 0xFF);
            bits = String.format("%8s", bits).replace(' ', '0'); // 8 bits par byte
            bitString.append(bits);
        }
        String destuffedString = bitString.toString().replaceAll("111110", "11111");
        int byteSize = (destuffedString.length() + 7) / 8;
        byte[] destuffedData = new byte[byteSize];
        for (int i = 0; i < destuffedString.length(); i += 8) {
            int endIndex = Math.min(i + 8, destuffedString.length());
            String byteString = destuffedString.substring(i, endIndex);
            while (byteString.length() < 8) {
                byteString += "0"; // Compléter avec des zéros
            }
            byte byteValue = (byte) Integer.parseInt(byteString, 2);
            destuffedData[i / 8] = byteValue;
        }
        return destuffedData;
    }

    // Méthode pour convertir la trame en bytes pour l'envoi
    public byte[] toBytes() {
        int frameSize = 1 + 1 + 1 + (data != null ? data.length : 0) + (crc != null ? crc.length : 0) + 1;
        byte[] frame = new byte[frameSize];
        int position = 0;
        frame[position++] = FLAG;
        frame[position++] = type;
        frame[position++] = num;
        if (data != null && data.length > 0) {
            System.arraycopy(data, 0, frame, position, data.length);
            position += data.length;
        }
        if (crc != null && crc.length > 0) {
            System.arraycopy(crc, 0, frame, position, crc.length);
            position += crc.length;
        }
        frame[position] = FLAG;
        return performBitStuffing(Arrays.copyOfRange(frame, 1, position));
    }

    // Méthode statique pour parser une trame reçue en un objet Frame
    public static Frame parseFrame(byte[] frameData) {
        byte[] destuffedData = removeBitStuffing(frameData);
        int crcLength = 2;
        byte[] dataWithoutCRC = Arrays.copyOf(destuffedData, destuffedData.length - crcLength);
        byte[] crc = Arrays.copyOfRange(destuffedData, destuffedData.length - crcLength, destuffedData.length);
        byte[] calculatedCRC = calculateCRC(dataWithoutCRC);
        if (!Arrays.equals(crc, calculatedCRC)) {
            throw new IllegalArgumentException("CRC mismatch");
        }
        byte flag = destuffedData[0];
        byte type = destuffedData[1];
        byte num = destuffedData[2];
        byte[] data = Arrays.copyOfRange(destuffedData, 3, destuffedData.length - crcLength);
        return new Frame(type, num, data, crc);
    }
    public static byte[] calculateCRC(byte[] data) {
        int crc = 0xFFFF; // Valeur initiale pour le CRC-CCITT

        for (byte b : data) {
            crc ^= b << 8; // Appliquer XOR avec le byte de données décalé

            for (int i = 0; i < 8; i++) {
                if ((crc & 0x8000) != 0) { // Si le bit le plus à gauche est 1
                    crc = (crc << 1) ^ 0x1021; // Décaler à gauche et appliquer XOR avec le polynôme générateur
                } else {
                    crc = crc << 1; // Sinon, simplement décaler à gauche
                }
            }
        }

        crc &= 0xFFFF; // S'assurer que le CRC est dans la plage 16 bits

        // Convertir le CRC en tableau de bytes
        byte[] crcBytes = new byte[2];
        crcBytes[0] = (byte) (crc >> 8); // Byte supérieur
        crcBytes[1] = (byte) (crc);      // Byte inférieur

        return crcBytes;
    }
}
