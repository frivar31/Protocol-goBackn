public class CRC {
        private static final int POLYNOMIAL = 0x1021; // Polynôme générateur pour CRC-CCITT

        public static byte[] calculateCRC(byte[] data) {
            int crc = 0xFFFF; // Valeur initiale pour le CRC

            for (byte b : data) {
                for (int i = 0; i < 8; i++) {
                    boolean bit = ((b >> (7 - i) & 1) == 1);
                    boolean c15 = ((crc >> 15 & 1) == 1);
                    crc <<= 1;
                    if (c15 ^ bit) crc ^= POLYNOMIAL;
                }
            }

            crc &= 0xFFFF;

            // Retourne le CRC sous forme d'un tableau de 2 bytes
            return new byte[]{(byte) ((crc >> 8) & 0xFF), (byte) (crc & 0xFF)};
        }

}
