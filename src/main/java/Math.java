import java.math.BigInteger;

public final class Math {
    public static BigInteger l(int x, Share[] shares, int i, BigInteger p) {
        BigInteger rv = BigInteger.ONE;
        for (int m = 0; m <= shares.length - 1; m++) {
            if (m == i) {
                continue;
            }
            rv = rv.multiply(BigInteger.valueOf(x - shares[m].x)).multiply(EEA(p, BigInteger.valueOf(shares[i].x - shares[m].x)));
        }
        return rv;
    }

    // TODO refactor selon commentaire de Jean Luc
    public static BigInteger EEA(BigInteger a, BigInteger b) {
        BigInteger[] r = new BigInteger[99];
        BigInteger[] q = new BigInteger[99];
        BigInteger[] x = new BigInteger[99];
        BigInteger[] y = new BigInteger[99];

        r[0] = a;
        r[1] = b.mod(a);
        x[0] = BigInteger.ONE;
        x[1] = BigInteger.ZERO;
        y[0] = BigInteger.ZERO;
        y[1] = BigInteger.ONE;

        int i = 0;
        while (!BigInteger.ZERO.equals(r[i + 1])) {
            i++;
            //q[i] = (r[i-1]/r[i]);
            q[i] = r[i - 1].divide(r[i]);
            //r[i+1] = r[i-1] -  q[i]*r[i];
            r[i + 1] = r[i - 1].subtract((q[i].multiply(r[i])));
            //x[i+1] = x[i-1]-q[i]*x[i];
            x[i + 1] = x[i - 1].subtract((q[i].multiply(x[i])));
            //y[i+1] = y[i-1]-q[i]*y[i];
            y[i + 1] = y[i - 1].subtract((q[i].multiply(y[i])));
            //System.out.println("a -> "+r[i-1]+" // b -> "+r[i]);
        }
        //System.out.print("eea with "+a+" and "+b);
        //System.out.println(" -> equals : r "+r[i]+" x "+x[i]+" y "+y[i]);
        return y[i].mod(a);
    }
}
