public class Test {
    public static void main(String[] args) {
        long b = 33;
        long c = Long.rotateRight(b,30);
        long d = (b << (64 - 30)) | (b >>> 30);
        System.out.println(c);
        System.out.println(d);
    }
}
