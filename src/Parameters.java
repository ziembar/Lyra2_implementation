public class Parameters {
    public static int N_COLS;
    public static int N_ROWS;
    public static final int TIME_COST = 5;
    public static final int MEMORY_COST = 100;
    public static final int KEY_LENGTH = 64;
    public static final int FULL_ROUNDS = 12;
    public static final int HALF_ROUNDS = FULL_ROUNDS/2;
    public static final int BLOCK_LENGTH_IN_LONG = 8;
    public static int BLOCK_LENGTH_IN_BYTES = BLOCK_LENGTH_IN_LONG * 8;
    public static int ROW_LENGTH_IN_LONG = N_COLS * BLOCK_LENGTH_IN_LONG;
    public static int ROW_LENGTH_IN_BYTES = ROW_LENGTH_IN_LONG * 8;
}
