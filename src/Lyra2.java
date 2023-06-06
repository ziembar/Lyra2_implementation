public class Lyra2 {
    /**
     * Compute password hash using salt and other parameters.
     *
     * @param pass   a password that was converted to byte form
     * @param salt   a salt (defeats ahead-of-time hash computation)
     */


    public static String phs(String pass, String salt) {
        byte[] hash = new byte[Parameters.KEY_LENGTH];
        hash(hash, stringToBytes(pass), stringToBytes(salt));
        return hash.toString();
    }

    public static byte[] padding(byte[] input) {
        int blockSize = Parameters.BLOCK_LENGTH_IN_BYTES;
        int inputLength = input.length;
        int paddingLength = blockSize - (inputLength % blockSize);
        if (paddingLength == 0) {
            paddingLength = blockSize;
        }

        byte[] padded = new byte[inputLength + paddingLength];
        System.arraycopy(input, 0, padded, 0, inputLength);

        padded[inputLength] = (byte) 0x80; // Dodanie 1-bitowego oznaczenia początku paddingu

        for (int i = inputLength + 1; i < padded.length - 1; i++) {
            padded[i] = 0; // Dodanie zer po 1-bitowym oznaczeniu
        }

        return padded;
    }

    public static long[] packToLongs(byte[] bytes) {
        int div = bytes.length / 8;
        int mod = bytes.length % 8;

        long[] longs = new long[div + (mod == 0 ? 0 : 1)];

        for (int i = 0; i != div; ++i) {
            long l = 0L;

            for (int j = 0; j != 7; ++j, l <<= 8) {
                // Upcasting a negative value gives a negative value
                // So, mask the result of an upcast to last byte only
                l |= (bytes[i * 8 + j] & 0x00000000000000FFL);

            } l |= bytes[i * 8 + 7] & 0x00000000000000FFL;

            longs[i] = l;
        }

        if (mod != 0) {
            long l = 0;

            for (int i = 0; i != mod - 1; ++i) {
                l |= (bytes[div * 8 + i] & 0x00000000000000FFL);

                l <<= 8;
            } l |= (bytes[div * 8 + mod - 1] & 0x00000000000000FFL);

            l <<= (8 * (8 - mod));

            longs[div] = l;
        }

        return longs;
    }


        private static byte[] stringToBytes(String data){
            return data.getBytes();
    }

    public static byte[] intToBytes(int number) {
        byte[] byteArray = new byte[4]; // Tworzenie tablicy bajtów o rozmiarze 4 (int ma 4 bajty)

        for (int i = 0; i < 4; i++) {
            byteArray[i] = (byte) (number >>> (i * 8)); // Konwersja int na bajty
        }

        return byteArray;
    }



    private static byte[] concatenateBytes(byte[]... arrays) {
        int totalLength = 0;
        for (byte[] array : arrays) {
            totalLength += array.length;
        }

        byte[] result = new byte[totalLength];
        int destPos = 0;
        for (byte[] array : arrays) {
            System.arraycopy(array, 0, result, destPos, array.length);
            destPos += array.length;
        }
        return result;
    }


    /**
     * Compute password hash using salt and other parameters.
     *
     * @param hash   byte array that will contain computed hash
     * @param pass   a password that was converted to byte form
     * @param salt   a salt (defeats ahead-of-time hash computation)
     */
    public static void hash(byte[] hash, byte[] pass, byte[] salt) {
        byte[] initData = padding(concatenateBytes(pass,salt, intToBytes(Parameters.KEY_LENGTH),
                intToBytes(pass.length), intToBytes(salt.length), intToBytes(Parameters.TIME_COST),
                intToBytes(Parameters.ROW_LENGTH_IN_BYTES * 8),intToBytes(Parameters.N_COLS)));

        Blake2BSponge sponge = new Blake2BSponge();
        sponge.absorbBlock(packToLongs(initData), Parameters.N_COLS);

        int gap = 1;
        int stp = 1;
        int wnd = 2;
        int sqrt = 2;
        int prev0 = 2;
        int row1 = 1;
        int prev1 = 0;

    }
    public static void main(String[] args){
        Lyra2 lyra = new Lyra2();
        lyra.phs("bolec", "123");
    }
}