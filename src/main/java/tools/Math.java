package tools;

import shamir.Share;

import java.math.BigInteger;

public final class Math {
    /**
     * Permet la reconstruction du secret
     * @param x la part cible ( = 0)
     * @param shares les parts connues
     * @param i la part en cours
     * @param p pour le modulo
     * @return la valeur calculée pour la part
     */
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

    /**
     * Extended Euclidian Algorithm
     * @param a la première valeur
     * @param b la seconde valeur
     * @return l'inverse multiplicatif
     */
    static BigInteger EEA(BigInteger a, BigInteger b) {
        // TODO intégrer la sliding window selon indications de Jean-Luc
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
            q[i] = r[i - 1].divide(r[i]);
            r[i + 1] = r[i - 1].subtract((q[i].multiply(r[i])));
            x[i + 1] = x[i - 1].subtract((q[i].multiply(x[i])));
            y[i + 1] = y[i - 1].subtract((q[i].multiply(y[i])));
        }
        return y[i].mod(a);
    }
}
