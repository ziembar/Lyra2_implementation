import java.nio.ByteBuffer;

/**
 * Klasa Blake2bSponge reprezentuje strukturę gąbki kryptograficznej,
 * korzystającej z algorytmu Blake2b jako swojej funkcji trnsformującej f
 */
public class Blake2BSponge {
    /**
     * Tablica zmiennych typu long reprezentująca aktualny stan wewnętrzny gąbki
     */
    long[] state;

    //Przepisywanie zmiennych początkowych z klasy Parameters
    /////////////////////////////////////////
    private final int BLOCK_LENGTH_IN_LONG;
    private final int BLOCK_LENGTH_IN_BYTES;
    private final int N_COLS;
    private final int FULL_ROUNDS;
    private final int HALF_ROUNDS;
    /////////////////////////////////////////

    /**
     * Metoda dodająca określoną liczbę wyrazów (long).
     * Zamiana kolejności bajtów w liczbach wynika z faktu, iż oryginalny algorytm,
     * w odróżnieniu od sposobu zapisywania liczb w Javie, korzysta z Little-Endian.
     *
     * @param longs wyrazy, które mają zostać zsumowane
     * @return wynik sumowania wyrazów w long
     */
    public long addWordwise(long... longs) {
        long result = 0;
        for (long l : longs) {
            result += switchEndian(l);
        }
        return switchEndian(result);
    }

    /**
     * Metoda zamieniająca zmienną typu long na tablicę typy byte
     *
     * @param x podana zmienna typu long
     * @return tablica byte otrzymana w wyniku podziału podanej zmiennej
     */
    public byte[] longToBytes(long x) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(x);
        return buffer.array();
    }

    /**
     * Metoda zmieniająca kolejność bajtów w zmiennej typu long
     * z Big-Endian na Little-Endian oraz odwrotnie
     *
     * @param x zmienna, w której należy zmienić kolejność bajtów
     * @return zmienna z zamienioną kolejnością bajtów
     */
    public static long switchEndian(final long x) {
        return (x & 0x00000000000000FFL) << 56
                | (x & 0x000000000000FF00L) << 40
                | (x & 0x0000000000FF0000L) << 24
                | (x & 0x00000000FF000000L) << 8
                | (x & 0x000000FF00000000L) >>> 8
                | (x & 0x0000FF0000000000L) >>> 24
                | (x & 0x00FF000000000000L) >>> 40
                | (x & 0xFF00000000000000L) >>> 56;
    }

    /**
     * Wektor początkowy funkcji Blake2b, używany do określenia
     * początkowego stanu gąbki
     */
    long[] InitiazationVector = {
            0x6a09e667f3bcc908L,
            0xbb67ae8584caa73bL,
            0x3c6ef372fe94f82bL,
            0xa54ff53a5f1d36f1L,
            0x510e527fade682d1L,
            0x9b05688c2b3e6c1fL,
            0x1f83d9abfb41bd6bL,
            0x5be0cd19137e2179L};

    /**
     * Konstruktor gąbki inicjalizujący jej pocżątkowy stan oraz przypisujący
     * do zmiennych zadane parametry działania algorytmu zabezpieczania.
     */
    public Blake2BSponge(Parameters params) {
        this.BLOCK_LENGTH_IN_LONG = params.BLOCK_LENGTH_IN_LONG;
        this.BLOCK_LENGTH_IN_BYTES = params.BLOCK_LENGTH_IN_BYTES;
        this.N_COLS = params.N_COLS;
        this.FULL_ROUNDS = params.FULL_ROUNDS;
        this.HALF_ROUNDS = params.HALF_ROUNDS;
        state = new long[16];
        for (int i = 0; i < 8; i++) {
            state[i] = 0;
            state[i + 8] = InitiazationVector[i];
        }
    }

    /**
     * Metoda dokonująca przemieszania stanu gąbki, zgodnie z zasadami
     * działania algorytmu Blake2b, zadaną ilość razy.
     * Wyrazy, dla których wywoływana jest funkcja G nie zą dobierane losowo.
     *
     * @param rounds ilość rund określająca, ile razy zostanie wykonane przemieszanie stanu gąbki
     */
    private void shuffle(int rounds) {
        for (int i = 0; i < rounds; i++) {
            functionG(0, 4, 8, 12);
            functionG(1, 5, 9, 13);
            functionG(2, 6, 10, 14);
            functionG(3, 7, 11, 15);
            functionG(0, 5, 10, 15);
            functionG(1, 6, 11, 12);
            functionG(2, 7, 8, 13);
            functionG(3, 4, 9, 14);
        }
    }

    /**
     * Metoda używana podczas mieszania stanu wewnętrznego,
     * która zmienia wartość zadanych wyrazów zgodnie z zasadami algorytmu BLake2b.
     * Zmiana kolejności bajtów po raz kolejny wynika z różnic w zapisie używanym
     * w algorytmie oraz tym używanym w Javie.
     *
     * @param a zadany wyraz
     * @param b zadany wyraz
     * @param c zadany wyraz
     * @param d zadany wyraz
     */
    private void functionG(int a, int b, int c, int d) {
        state[a] = addWordwise(state[a], state[b]);
        state[d] = switchEndian(Long.rotateRight(switchEndian(state[d] ^ state[a]), 32));

        state[c] = addWordwise(state[c], state[d]);
        state[b] = switchEndian(Long.rotateRight(switchEndian(state[b] ^ state[c]), 24));

        state[a] = addWordwise(state[a], state[b]);
        state[d] = switchEndian(Long.rotateRight(switchEndian(state[d] ^ state[a]), 16));

        state[c] = addWordwise(state[c], state[d]);
        state[b] = switchEndian(Long.rotateRight(switchEndian(state[b] ^ state[c]), 63));
    }

    /**
     * Metoda odpowiedzialna za "wyciśnięcie" z gąbki zadanej ilości bajtów
     * do podanej tablicy
     *
     * @param out    tablica, do której zostaną "wyciśnięte" bajty
     * @param amount ilość "wyciskanych" bajtów
     */
    public void squeeze(byte[] out, int amount) {
        int iterator = 0;
        //whole blocks
        int numberOfBlocks = amount / BLOCK_LENGTH_IN_BYTES;
        int rest = amount % BLOCK_LENGTH_IN_BYTES;
        for (int i = 0; i < numberOfBlocks; i++) {
            for (int j = 0; j < BLOCK_LENGTH_IN_LONG; j++) {
                byte[] bytes = longToBytes(state[j]);
                for (int k = 0; k < 8; k++) {
                    out[iterator] = bytes[k];
                    iterator++;
                }
            }
            shuffle(FULL_ROUNDS);
        }
        //rest
        int longsInRest = rest / 8;
        int restOfRest = rest % 8;
        for (int i = 0; i < longsInRest; i++) {
            byte[] bytes = longToBytes(state[i]);
            for (int j = 0; j < 8; j++) {
                out[iterator] = bytes[j];
                iterator++;
            }
        }
        //remaining bytes
        for (int i = 0; i < restOfRest; i++) {
            byte[] bytes = longToBytes(state[longsInRest]);
            out[iterator] = bytes[i];
            iterator++;
        }
    }

    /**
     * Metoda odpowiedzialna za absorbowanie jednego bloku podanego
     * w postaci tablicy long
     *
     * @param in     tablica podana do absorbowania
     * @param length długość absorbowanej tablicy
     * @param offset wyraz od którego należy zacząć absorbowanie bloku zawartego w tablicy in
     */
    public void absorbBlock(long[] in, int length, int offset) {
        for (int i = 0; i < length; i++) {
            state[i] ^= in[i + offset];
        }
        shuffle(FULL_ROUNDS);
    }

    /**
     * Metoda odpowiedzialna za "wyciśnięcie" z gąbki całego rzędu macierzy pamięci.
     * Używana dla rzędu zerowego podczas inicjalizowania macierzy pamięci
     *
     * @param out rząd zerowy macierzy poddawany operacji zredukowanego "wyciśnięcia"
     */
    public void reducedSqueezeRow(long[] out) {

        for (int i = 0; i < N_COLS; i++) {
            int iterator = 0;
            for (int j = 0; j < BLOCK_LENGTH_IN_LONG; j++) {
                out[iterator] = state[j];
                iterator++;
            }
            shuffle(HALF_ROUNDS);
        }
    }

    /**
     * Metoda dupleksująca dla gąbki ze zredukowaną liczbą rund,
     * używana podczas inicjalizowania macierzy pamięci.
     * Używana tylko dla pierwszego i drugiegu rzędy macierzy.
     * Odpowiednio użyta wykonuje w pełni dziewiątą i dziesiątą linijkę pseudokodu.
     *
     * @param out tablica, do której zostaną wrzucone wartości (efekt operacji)
     * @param in  tablica, z której brane są wartości konieczne do przeprowadzenia operacji
     */
    public void reducedDuplexRow1And2(long[] out, long[] in) {
        int iteratorIn = 0;
        for (int i = 0; i < N_COLS; i++) {
            int iteratorOut = (N_COLS - 1 - i) * BLOCK_LENGTH_IN_LONG;
            for (int j = 0; j < BLOCK_LENGTH_IN_LONG; j++) {
                state[j] ^= in[iteratorIn];
                iteratorIn++;
            }
            iteratorIn -= BLOCK_LENGTH_IN_LONG;
            shuffle(HALF_ROUNDS);
            for (int j = 0; j < BLOCK_LENGTH_IN_LONG; j++) {
                out[iteratorOut] = state[j] ^ in[iteratorIn];
                iteratorIn++;
                iteratorOut++;
            }
        }
    }

    /**
     * Metoda dupleksująca dla gąbki ze zredukowaną liczbą rund,
     * używana podczas fazy "Filling Loop".
     * Odpowiednio użyta wykonuje w pełni od 13. do 17. linijki pseudokodu.
     *
     * @param row1  tablica zawierająca rząd o indeksie row1
     * @param row0  tablica zawierająca rząd o indeksie row0
     * @param prev0 tablica zawierająca rząd o indeksie prev0
     * @param prev1 tablica zawierająca rząd o indeksie prev1
     */
    public void reducedDuplexFillingLoop(long[] row1, long[] row0, long[] prev0, long[] prev1) {
        for (int i = 0; i < N_COLS; i++) {
            for (int j = 0; j < BLOCK_LENGTH_IN_LONG; j++) {
                int offset = i * BLOCK_LENGTH_IN_LONG;
                state[j] ^= addWordwise(row1[offset + j], prev0[offset + j], prev1[offset + j]);

            }
            shuffle(HALF_ROUNDS);
            for (int j = 0; j < BLOCK_LENGTH_IN_LONG; j++) {
                int offset = i * BLOCK_LENGTH_IN_LONG;
                row0[(N_COLS - 1 - i) * BLOCK_LENGTH_IN_LONG + j]
                        = prev0[offset + j] ^ state[j];
            }
            for (int j = 0; j < BLOCK_LENGTH_IN_LONG; j++) {
                int offset = i * BLOCK_LENGTH_IN_LONG;
                row1[offset + j] ^= state[(j + 2) % BLOCK_LENGTH_IN_LONG];
            }
        }
    }

    /**
     * Metoda dupleksująca dla gąbki ze zredukowaną liczbą rund,
     * używana podczas fazy "Wandering Phase".
     * Odpowiednio użyta wykonuje w pełni od 29. do 35. linijki pseudokodu.
     *
     * @param row1  tablica zawierająca rząd o indeksie row1
     * @param row0  tablica zawierająca rząd o indeksie row0
     * @param prev0 tablica zawierająca rząd o indeksie prev0
     * @param prev1 tablica zawierająca rząd o indeksie prev1
     */
    public void reducedDuplexWandering(long[] row1, long[] row0, long[] prev0, long[] prev1) {
        for (int i = 0; i < N_COLS; i++) {
            int col0 = (int) Long.remainderUnsigned(switchEndian(state[4]),
                    N_COLS);
            int col1 = (int) Long.remainderUnsigned(switchEndian(state[6]),
                    N_COLS);
            int offset = i * BLOCK_LENGTH_IN_LONG;
            for (int j = 0; j < BLOCK_LENGTH_IN_LONG; j++) {
                state[j] ^= addWordwise(row0[offset + j], row1[offset + j]
                        , prev0[BLOCK_LENGTH_IN_LONG * col0 + j]
                        , prev1[BLOCK_LENGTH_IN_LONG * col1 + j]);
            }
            shuffle(HALF_ROUNDS);
            for (int j = 0; j < BLOCK_LENGTH_IN_LONG; j++) {
                row0[offset + j] ^= state[j];
            }
            for (int j = 0; j < BLOCK_LENGTH_IN_LONG; j++) {
                row1[offset + j] ^= state[(j + 2) % BLOCK_LENGTH_IN_LONG];
            }
        }
    }
}
