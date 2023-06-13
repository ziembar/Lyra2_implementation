/**
 * Klasa zawierająca parametry początkowe algorytmu.
 * Wszystkie zmienne są statyczne,
 * gdyż są używane w różnych klasach i nie ulegają zmianie w trakcie działania algorytmu.
 */
public class Parameters {
    /**
     * Liczba kolumn w macierzy pamięci (C)
     */
    public int N_COLS = 64;
    /**
     * Liczba określająca przybliżony czas wykonania (T)
     */
    public int TIME_COST = 5;
    /**
     * Liczba określająca pamięć potrzebną algorytmowi,
     * a zarazem liczbę rzędów w macierzy pamięci (R)
     */
    public int MEMORY_COST = 16;
    /**
     * Długość zabezpieczonego hasła w bajtach (k)
     */
    public int KEY_LENGTH = 64;
    /**
     * Pełna ilość rund używana podczas mieszania stanu wewn. gąbki;
     * zwyczajowo 12.
     */
    public int FULL_ROUNDS = 12;
    /**
     * Zredukowana ilość rund używana podczas mieszania stanu wewn. gąbki;
     * zwyczajowo równa połowie pełnej liczbie rund
     */
    public int HALF_ROUNDS;
    /**
     * Długość jednego bloku w ilości zmiennych long.
     * Wielkość bloku to wielkość jednej komórki w macierzy pamięci
     * oraz jednocześnie pojemność gąbki kryptograficznej (b)
     */
    public int BLOCK_LENGTH_IN_LONG;
    /**
     * Długość jednego bloku w bajtach (b).
     */
    public int BLOCK_LENGTH_IN_BYTES;
    /**
     * Długość jednego rzędu macierzy pamięci w ilości zmiennych long
     */
    public int ROW_LENGTH_IN_LONG;
    /**
     * Długość jednego rzędu macierzy pamięci w bajtach
     */
    public int ROW_LENGTH_IN_BYTES;

    public Parameters(int n_COLS, int TIME_COST, int MEMORY_COST, int KEY_LENGTH, int FULL_ROUNDS, int BLOCK_LENGTH_IN_LONG) {
        N_COLS = n_COLS;
        this.TIME_COST = TIME_COST;
        this.MEMORY_COST = MEMORY_COST;
        this.KEY_LENGTH = KEY_LENGTH;
        this.FULL_ROUNDS = FULL_ROUNDS;
        this.HALF_ROUNDS = FULL_ROUNDS/2;
        this.BLOCK_LENGTH_IN_LONG = BLOCK_LENGTH_IN_LONG;
        this.BLOCK_LENGTH_IN_BYTES = this.BLOCK_LENGTH_IN_LONG * 8;
        this.ROW_LENGTH_IN_LONG = N_COLS * BLOCK_LENGTH_IN_LONG;
        this.ROW_LENGTH_IN_BYTES =  this.ROW_LENGTH_IN_LONG * 8;
    }
}
