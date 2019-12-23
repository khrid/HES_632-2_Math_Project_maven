package shamir;

import tools.Math;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;

public class ShamirSecret {

    public static final int MAX_SECRET_SIZE = 256;
    public static final int MIN_SECRET_SIZE = 8;
    public static final int MAX_SHARES = 32;

    private BigInteger secret;
    private BigInteger p;
    private int tailleSecret; // en bit
    private int seuil;
    private int nombreDeParts;

    public ShamirSecret(int _tailleSecret, int _seuil, int _nombreDeParts) throws Exception {
        if (_tailleSecret > MAX_SECRET_SIZE) {
            throw new Exception("Taille du secret trop élevée (<= " + MAX_SECRET_SIZE + ")");
        }

        if (_tailleSecret < MIN_SECRET_SIZE) {
            throw new Exception("Taille du secret trop faible (min " + MIN_SECRET_SIZE + ")");
        }

        if (_seuil > _nombreDeParts) {
            throw new Exception("Le seuil (" + _seuil + ") ne peut excéder le nombre de parts (" + _nombreDeParts + ")");
        }

        if (_nombreDeParts <= 1) {
            throw new Exception("Pas assez de parts (min 2)");
        }

        tailleSecret = _tailleSecret;
        seuil = _seuil;
        nombreDeParts = _nombreDeParts;

        genererSecret();
    }

    public BigInteger getSecret() {
        return secret;
    }

    public BigInteger getP() {
        return p;
    }

    public int getSeuil() {
        return seuil;
    }

    public int getNombreDeParts() {
        return nombreDeParts;
    }

    public int getTailleSecret() {
        return tailleSecret;
    }

    private void genererSecret() {
        secret = BigInteger.probablePrime(tailleSecret, new SecureRandom());
        p = secret.nextProbablePrime();
        //System.out.println("secret : "+secret);
    }


    public Share[] split(BigInteger secret, int seuil, int nombreDeParts, BigInteger p) {
        Share[] shares = new Share[nombreDeParts];
        Random random = new SecureRandom();

        BigInteger[] tmp = new BigInteger[seuil];
        tmp[0] = secret;
        for (int i = 1; i < seuil; i++) {
            BigInteger r;
            do {
                r = new BigInteger(p.bitLength(), random);
            } while (r.compareTo(BigInteger.ZERO) <= 0 || r.compareTo(p) >= 0);
            tmp[i] = r;
        }

        for (int i = 1; i <= nombreDeParts; i++) {
            BigInteger accum = secret;
            for (int exp = 1; exp < seuil; exp++) {
                accum = accum.add(tmp[exp].multiply(BigInteger.valueOf(i).pow(exp).mod(p))).mod(p);
            }
            shares[i - 1] = new Share(i, accum);
        }

        return shares;
    }

    public BigInteger combine(int x, Share[] shares, BigInteger p) throws Exception {
        if (shares.length == seuil) {
            BigInteger rv = BigInteger.ZERO;
            for (int i = 0; i < shares.length; i++) {
                rv = rv.add(shares[i].y.multiply(Math.l(x, shares, i, p)));
            }
            return rv.mod(p);
        } else {
            throw new Exception("Pas assez de parts pour reconstruire le secret");
        }
    }

    public ShamirSecret genererNouvellePart() throws Exception {
        if (nombreDeParts < MAX_SHARES) {
            System.out.println("Secret partagé en " + nombreDeParts + ", ajout d'une nouvelle part.");

            nombreDeParts += 1;
        } else {
            throw new Exception("Nombre maximale de part atteint.");
        }
        return this;
    }

    public ShamirSecret enleverPart() throws Exception {
        // on ne doit pas avoir moins de parts que le seuil
        if (nombreDeParts - 1 >= seuil) {
            // on ne doit pas avoir moins de 2 parts
            if (nombreDeParts - 1 > 2) {
                System.out.println("Secret partagé en " + nombreDeParts + ", retrait d'une part.");
                nombreDeParts -= 1;
                return this;
            } else {
                throw new Exception("Plus assez de parts à retirer.");
            }
        } else {
            throw new Exception("Le nombre de parts ne peut pas être inférieur au seuil.");
        }
    }
}
