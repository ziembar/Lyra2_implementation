import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Lyra2Test {
    Lyra2 lyra = new Lyra2(new Parameters());

    @Test
    void phs() {

    }

    @Test
    void padding() {
        byte[] input = {0x01, 0x02, 0x03};
        byte[] expected = {0x01, 0x02, 0x03, (byte) 0x80, 0x00, 0x00};

        byte[] result = lyra.padding(input);

        Assert.assertEquals(64, result.length);
    }

    @Test
    void byteArrayToString() {
        byte[] bytes = {0x48, 0x65, 0x6C, 0x6C, 0x6F, 0x20, 0x57, 0x6F, 0x72, 0x6C, 0x64};
        String expected = "Hello World";

        String result = lyra.byteArrayToString(bytes);

        Assert.assertEquals(expected, result);
    }

    @Test
    void packToLongs() {
        byte[] bytes = {0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0A};
        long[] expected = {0x0102030405060708L, 0x090A000000000000L};

        long[] result = lyra.packToLongs(bytes);

        Assert.assertArrayEquals(expected, result);
    }

    @Test
    void intToBytes() {
        int number = 16909060;
        byte[] expected = {0x01, 0x02, 0x03, 0x04};

        byte[] result = lyra.intToBytes(number);

        Assert.assertArrayEquals(expected, result);
    }
}