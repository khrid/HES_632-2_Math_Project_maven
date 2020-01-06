package shamir;

import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TestShare {

    @Test
    void testNewShare() {
        Share share = new Share(1, BigInteger.valueOf(2));
        assertEquals(1, share.x);
        assertEquals(BigInteger.valueOf(2), share.y);
    }

    @Test
    void testYisBigInteger() {
        Share share = new Share(1, BigInteger.TEN);
        assertEquals(BigInteger.class, share.y.getClass());
    }
}
