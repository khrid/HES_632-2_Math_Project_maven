package shamir;

import org.junit.jupiter.api.Test;
import shamir.Share;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;

public class TestShare {

    @Test
    public void testNewShare() {
        Share share = new Share(1, BigInteger.TWO);
        assertEquals(1, share.x);
        assertEquals(BigInteger.TWO, share.y);
    }

    @Test
    public void testYisBigInteger() {
        Share share = new Share(1, BigInteger.TEN);
        assertEquals(BigInteger.class, share.y.getClass());
    }
}
