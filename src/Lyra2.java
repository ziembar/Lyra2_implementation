public class Lyra2 {
    private static int KEY_LENGTH;
    private static int BLOCK_LENGTH_IN_LONG;
    private static int BLOCK_LENGTH_IN_BYTES;
    private static int N_COLS;
    private static int FULL_ROUNDS;
    private static int HALF_ROUNDS;
    private static int TIME_COST;
    private static int ROW_LENGTH_IN_BYTES;
    private static int MEMORY_COST;
    private static int ROW_LENGTH_IN_LONG;
    private static Blake2BSponge sponge;


    public Lyra2(Parameters params) {
        this.BLOCK_LENGTH_IN_LONG = params.BLOCK_LENGTH_IN_LONG;
        this.BLOCK_LENGTH_IN_BYTES = params.BLOCK_LENGTH_IN_BYTES;
        this.N_COLS = params.N_COLS;
        this.FULL_ROUNDS = params.FULL_ROUNDS;
        this.HALF_ROUNDS = params.HALF_ROUNDS;
        this.KEY_LENGTH = params.KEY_LENGTH;
        this.TIME_COST = params.TIME_COST;
        this.ROW_LENGTH_IN_BYTES = params.ROW_LENGTH_IN_BYTES;
        this.MEMORY_COST = params.MEMORY_COST;
        this.ROW_LENGTH_IN_LONG = params.ROW_LENGTH_IN_LONG;

        this.sponge = new Blake2BSponge(params);
    }

    /**
     * Compute password hash using salt and other parameters.
     *
     * @param pass a password that was converted to byte form
     * @param salt a salt (defeats ahead-of-time hash computation)
     */


    public static String phs(String pass, String salt) {
        byte[] hash = new byte[KEY_LENGTH];
        hash(hash, stringToBytes(pass), stringToBytes(salt));
        return byteArrayToString(hash);
    }

    public static byte[] padding(byte[] input) {
        int blockSize = BLOCK_LENGTH_IN_BYTES;
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

    public static String byteArrayToString(byte[] bytes) {
        StringBuilder stringBuilder = new StringBuilder();
        for (byte b : bytes) {
            stringBuilder.append((char) b);
        }
        return stringBuilder.toString();
    }
    public static long[] packToLongs(byte[] bytes) {
        int div = bytes.length / 8;
        int mod = bytes.length % 8;

        long[] longs = new long[div + (mod == 0 ? 0 : 1)];

        for (int i = 0; i != div; ++i) {
            long l = 0L;

            for (int j = 0; j != 7; ++j, l <<= 8) {
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

        for (int i = 3; i >= 0; i--) {
            byteArray[3-i] = (byte) (number >>> (i * 8)); // Konwersja int na bajty
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
        byte[] initData = padding(concatenateBytes(pass, salt, intToBytes(KEY_LENGTH),
                intToBytes(pass.length), intToBytes(salt.length), intToBytes(TIME_COST),
                intToBytes(MEMORY_COST), intToBytes(N_COLS)));

        long[][] matrix = new long[MEMORY_COST][ROW_LENGTH_IN_LONG];


        long[] packedInit = packToLongs(initData);
        for (int i =0;i<packedInit.length/BLOCK_LENGTH_IN_LONG;i++){
            int offset = i*BLOCK_LENGTH_IN_LONG;
            sponge.absorbBlock(packedInit,BLOCK_LENGTH_IN_LONG,offset);
        }


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

        for (row0 = 3; row0 < MEMORY_COST; row0++) {
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

        for (int wCount = 0; wCount < TIME_COST * MEMORY_COST; wCount++) {
            row0 = (int) Long.remainderUnsigned(sponge.state[0], MEMORY_COST);
            row1 = (int) Long.remainderUnsigned(sponge.state[2], MEMORY_COST);
            sponge.reducedDuplexWandering(matrix[row1], matrix[row0], matrix[prev1], matrix[prev0]);
            prev0 = row0;
            prev1 = row1;
        }

        sponge.absorbBlock(matrix[row0],BLOCK_LENGTH_IN_LONG,0);

        sponge.squeeze(hash, KEY_LENGTH);
    }

    public static void main(String[] args) {
        Parameters params = new Parameters();
        params.KEY_LENGTH = 9;
        Lyra2 lyra = new Lyra2(params);
        System.out.println(lyra.phs("bolec", "113"));
    }
}