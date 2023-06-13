/**
 * Klasa zawierająca parametry początkowe algorytmu.
 * Wszystkie zmienne są statyczne,
 * gdyż są używane w różnych klasach i nie ulegają zmianie w trakcie działania algorytmu.
 */
public class Parameters {
    /**
     * Liczba kolumn w macierzy pamięci (C)
     */
    public final int N_COLS;
    /**
     * Liczba określająca przybliżony czas wykonania (T)
     */
    public final int TIME_COST;
    /**
     * Liczba określająca pamięć potrzebną algorytmowi,
     * a zarazem liczbę rzędów w macierzy pamięci (R)
     */
    public final int MEMORY_COST;
    /**
     * Długość zabezpieczonego hasła w bajtach (k)
     */
    public final int KEY_LENGTH;
    /**
     * Pełna ilość rund używana podczas mieszania stanu wewn. gąbki;
     * zwyczajowo 12.
     */
    public int FULL_ROUNDS;
    /**
     * Zredukowana ilość rund używana podczas mieszania stanu wewn. gąbki;
     * zwyczajowo równa połowie pełnej liczbie rund
     */
    public final int HALF_ROUNDS;
    /**
     * Długość jednego bloku w ilości zmiennych long.
     * Wielkość bloku to wielkość jednej komórki w macierzy pamięci
     * oraz jednocześnie pojemność gąbki kryptograficznej (b)
     */
    public final int BLOCK_LENGTH_IN_LONG;
    /**
     * Długość jednego bloku w bajtach (b).
     */
    public final int BLOCK_LENGTH_IN_BYTES;
    /**
     * Długość jednego rzędu macierzy pamięci w ilości zmiennych long
     */
    public final int ROW_LENGTH_IN_LONG;
    /**
     * Długość jednego rzędu macierzy pamięci w bajtach
     */
    public final int ROW_LENGTH_IN_BYTES;

    public Parameters(int n_COLS, int TIME_COST, int MEMORY_COST, int KEY_LENGTH, int FULL_ROUNDS, int BLOCK_LENGTH_IN_LONG) {
        this.N_COLS = n_COLS;
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
