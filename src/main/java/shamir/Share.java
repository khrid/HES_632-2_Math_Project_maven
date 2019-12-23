package shamir;

import java.math.BigInteger;

public class Share {
    public int x;
    public BigInteger y;

    public Share(int x, BigInteger y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "shamir.Share{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
