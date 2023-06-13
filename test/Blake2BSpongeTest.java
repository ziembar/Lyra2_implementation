import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

class Blake2BSpongeTest {
    Blake2BSponge sponge;

    @BeforeEach
    void setup() {
        Parameters params = new Parameters(10,5,4,10,12,8);
        params.FULL_ROUNDS = 0;
        sponge = new Blake2BSponge(params);
        for (int i = 0; i < sponge.state.length; i++) {
            sponge.state[i] = i;
        }
    }

    @org.junit.jupiter.api.Test
    void addWordwise() {
        long a = 0, b = 1, c = 2, d = 3;
        assertEquals(sponge.addWordwise(a, b), 1);
        assertEquals(sponge.addWordwise(a, b, c, d), 6);
    }

    @org.junit.jupiter.api.Test
    void longToBytes() {
        long a = 1, b = 11111111;
        byte[] a1 = {0, 0, 0, 0, 0, 0, 0, 1}, b1 = {0, 0, 0, 0, 0, (byte) 169, (byte) 138, (byte) 199};
        assertArrayEquals(sponge.longToBytes(a), a1);
        assertArrayEquals(sponge.longToBytes(b), b1);
    }

    @org.junit.jupiter.api.Test
    void switchEndian() {
        long a = 4, b = 12413425;
        long a1 = 288230376151711744L, b1 = -1051101230316650496L;
        assertEquals(sponge.switchEndian(a), a1);
        assertEquals(sponge.switchEndian(b), b1);
    }

    @org.junit.jupiter.api.Test
    void squeeze() {
        byte[] result = new byte[16];
        sponge.squeeze(result, 16);
        byte[] expected = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1};
        assertArrayEquals(result, expected);
    }

    @org.junit.jupiter.api.Test
    void absorbBlock() {
        long[] block = {1, 2, 3, 4, 5, 6, 7, 8};
        sponge.absorbBlock(block, 8, 0);
        long[] result = {1, 3, 1, 7, 1, 3, 1, 15, 8, 9, 10, 11, 12, 13, 14, 15};
        assertArrayEquals(sponge.state, result);
    }

}