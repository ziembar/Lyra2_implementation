public class Lyra2 {
    private final int BLOCK_LENGTH_IN_LONG;
    private final int BLOCK_LENGTH_IN_BYTES;
    private final int N_COLS;
    private final int FULL_ROUNDS;
    private final int HALF_ROUNDS;

    public Lyra2() {
        this.BLOCK_LENGTH_IN_LONG = Parameters.BLOCK_LENGTH_IN_LONG;
        this.BLOCK_LENGTH_IN_BYTES = Parameters.BLOCK_LENGTH_IN_BYTES;
        this.N_COLS = Parameters.N_COLS;
        this.FULL_ROUNDS = Parameters.FULL_ROUNDS;
        this.HALF_ROUNDS = Parameters.HALF_ROUNDS;
    }

    /**
     * Compute password hash using salt and other parameters.
     *
     * @param pass a password that was converted to byte form
     * @param salt a salt (defeats ahead-of-time hash computation)
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

            }
            l |= bytes[i * 8 + 7] & 0x00000000000000FFL;

            longs[i] = l;
        }

        if (mod != 0) {
            long l = 0;

            for (int i = 0; i != mod - 1; ++i) {
                l |= (bytes[div * 8 + i] & 0x00000000000000FFL);

                l <<= 8;
            }
            l |= (bytes[div * 8 + mod - 1] & 0x00000000000000FFL);

            l <<= (8 * (8 - mod));

            longs[div] = l;
        }

        return longs;
    }


    private static byte[] stringToBytes(String data) {
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
     * @param hash byte array that will contain computed hash
     * @param pass a password that was converted to byte form
     * @param salt a salt (defeats ahead-of-time hash computation)
     */
    public static void hash(byte[] hash, byte[] pass, byte[] salt) {
        byte[] initData = padding(concatenateBytes(pass, salt, intToBytes(Parameters.KEY_LENGTH),
                intToBytes(pass.length), intToBytes(salt.length), intToBytes(Parameters.TIME_COST),
                intToBytes(Parameters.ROW_LENGTH_IN_BYTES * 8), intToBytes(Parameters.N_COLS)));

        long[][] matrix = new long[Parameters.ROW_LENGTH_IN_BYTES * 8][Parameters.N_COLS];
        Blake2BSponge sponge = new Blake2BSponge();

        long[] packedInit = packToLongs(initData);
        sponge.absorbBlock(packedInit, packedInit.length);

        int gap = 1;
        int stp = 1;
        int wnd = 2;
        int sqrt = 2;
        int prev0 = 2;
        int row1 = 1;
        int prev1 = 0;

        sponge.reducedSqueezeRow(matrix[0]);
        sponge.reducedDuplexRow1And2(matrix[1], matrix[0]);
        sponge.reducedDuplexRow1And2(matrix[2], matrix[1]);

        int row0;

        for (row0 = 3; row0 < Parameters.ROW_LENGTH_IN_BYTES * 8; row0++) {
            sponge.reducedDuplexFillingLoop(matrix[row1], matrix[row0], matrix[prev0], matrix[prev1]);
            prev0 = row0;
            prev1 = row1;
            row1 = (row1 + stp) % wnd;
            if (row1 == 0) {
                wnd *= 2;
                stp = sqrt + gap;
                gap = -gap;
                if (gap == -1) {
                    sqrt *= 2;
                }
            }
        }

        for (int wCount = 0; wCount < Parameters.ROW_LENGTH_IN_BYTES * 8; wCount++) {
            row0 = (int) sponge.state[0] % Parameters.ROW_LENGTH_IN_BYTES * 8;
            row1 = (int) sponge.state[2] % Parameters.ROW_LENGTH_IN_BYTES * 8;
            sponge.reducedDuplexWandering(matrix[row1], matrix[row0], matrix[prev1], matrix[prev0]);
            prev0 = row0;
            prev1 = row1;
        }

        sponge.absorbBlock(matrix[row0], Parameters.N_COLS);

        sponge.squeeze(hash, Parameters.KEY_LENGTH);
    }

    public static void main(String[] args) {
        Lyra2 lyra = new Lyra2();
        lyra.phs("bolec", "123");
    }
}