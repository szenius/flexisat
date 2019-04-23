package bayesian_encoder;

public class Helper {
    // Returns a Small Endian array
    protected static boolean[] getBitsOfInteger (int numBits, int value) {
        boolean[] bitArray = new boolean[numBits];
        for (int i = numBits - 1; i >= 0; i --) {
            bitArray[i] = (value & (1 << i)) != 0;
        }
        return bitArray;
    }
}
