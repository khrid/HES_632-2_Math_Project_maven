package tools;

import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.security.SecureRandom;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestMath {

    @Test
    public void testMyEeaEqualsBigIntegerModInverse() {
        BigInteger m = BigInteger.probablePrime(32, new SecureRandom());
        assertEquals(BigInteger.TEN.modInverse(m), Math.EEA(m, BigInteger.TEN));
    }

    @Test
    public void testMyEeaSlidingWindowEqualsBigIntegerModInverse() {
        //BigInteger m = BigInteger.probablePrime(32, new SecureRandom());
        //assertEquals(BigInteger.TEN.modInverse(m), Math.EEArework(m, BigInteger.TEN));
    }
}
