import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Lyra2Test {
    Lyra2 lyra = new Lyra2(new Parameters(256,100,10,10,12, 8));
    Lyra2 lyra2 = new Lyra2(new Parameters(2560,100,10,10,20, 8));
    Lyra2 lyra3 = new Lyra2(new Parameters(128,10,10,100,20, 8));


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

    @Test
    void test1(){
        Assert.assertEquals("3D 6A 49 7B 5B 48 6F 0F 1C 8B", lyra.phsString("password", "ssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss"));
    }

    @Test
    void test2(){
        Assert.assertEquals("45 B0 8E AB 1B 51 A6 86 42 82", lyra2.phsString("password", "123"));
    }

    @Test
    void test3(){
        Assert.assertEquals("FB C8 A9 FB 52 56 42 90 99 7A 96 1D 95 77 DC 36 71 15 A3 8C 2B 23 08 49 69 C5 80 3F B9 EC 27 32 32 E1 1A 00 2E E2 27 A3 09 9E DD AE 98 A6 32 91 07 83 C4 E7 AF 02 E7 3A 2A 1F DC 58 01 7B 85 FC 8B 05 80 C8 7C B9 28 D5 6A 9E 13 A2 A6 5A 28 34 66 15 9B 3E 83 DF 17 B7 11 47 F0 96 49 45 A2 AC A5 F2 BE 94",
                lyra3.phsString("password", "123"));
    }


}