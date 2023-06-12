/**
 * Klasa zawierająca parametry początkowe algorytmu.
 * Wszystkie zmienne są statyczne i końcowe,
 * gdyż są używane w różnych klasach i nie ulegają zmianie w trakcie działanie algorytmu.
 */
public class Parameters {
    /**
     * Liczba kolumn w macierzy pamięci (C)
     */
    public static final int N_COLS = 64;
    /**
     * Liczba określająca przybliżony czas wykonania (T)
     */
    public static final int TIME_COST = 5;
    /**
     * Liczba określająca pamięć potrzebną algorytmowi,
     * a zarazem liczbę rzędów w macierzy pamięci (R)
     */
    public static final int MEMORY_COST = 16;
    /**
     * Długość zabezpieczonego hasła w bajtach (k)
     */
    public static final int KEY_LENGTH = 64;
    /**
     * Pełna ilość rund używana podczas mieszania stanu wewn. gąbki;
     * zwyczajowo 12.
     */
    public static final int FULL_ROUNDS = 12;
    /**
     * Zredukowana ilość rund używana podczas mieszania stanu wewn. gąbki;
     * zwyczajowo równa połowie pełnej liczbie rund
     */
    public static final int HALF_ROUNDS = FULL_ROUNDS/2;
    /**
     * Długość jednego bloku w ilości zmiennych long.
     * Wielkość bloku to wielkość jednej komórki w macierzy pamięci
     * oraz jednocześnie pojemność gąbki kryptograficznej (b)
     */
    public static final int BLOCK_LENGTH_IN_LONG = 8;
    /**
     * Długość jednego bloku w bajtach (b).
     */
    public static final int BLOCK_LENGTH_IN_BYTES = BLOCK_LENGTH_IN_LONG * 8;
    /**
     * Długość jednego rzędu macierzy pamięci w ilości zmiennych long
     */
    public static final int ROW_LENGTH_IN_LONG = N_COLS * BLOCK_LENGTH_IN_LONG;
    /**
     * Długość jednego rzędu macierzy pamięci w bajtach
     */
    public static final int ROW_LENGTH_IN_BYTES = ROW_LENGTH_IN_LONG * 8;
}
