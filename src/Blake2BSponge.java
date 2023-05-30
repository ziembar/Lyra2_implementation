public class Blake2BSponge {
    long[] state;
    long[] InitiazationVector = {
            0x6a09e667f3bcc908L,
            0xbb67ae8584caa73bL,
            0x3c6ef372fe94f82bL,
            0xa54ff53a5f1d36f1L,
            0x510e527fade682d1L,
            0x9b05688c2b3e6c1fL,
            0x1f83d9abfb41bd6bL,
            0x5be0cd19137e2179L};

    public Blake2BSponge() {
        state = new long[16];
    }

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

    private void functionG(int a, int b, int c, int d) {
        state[a] = state[a] + state[b];
        state[d] = (state[d] ^ state[a]) >> 32;

        state[c] = state[c] + state[d];
        state[b] = (state[b] ^ state[c]) >> 24;

        state[a] = state[a] + state[b];
        state[d] = (state[d] ^ state[a]) >> 16;

        state[c] = state[c] + state[d];
        state[b] = (state[b] ^ state[c]) >> 63;
    }
}
