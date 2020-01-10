package shamir;

import tools.Math;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;

public class ShamirSecret {

    public static final int MAX_SECRET_SIZE = 4096;
    public static final int MIN_SECRET_SIZE = 8;
    public static final int MAX_SHARES = 32;

    private transient BigInteger secret;
    private BigInteger p;
    private int tailleSecret; // en bit
    private int seuil;
    private int nombreDeParts;

    /**
     *
     * @param _tailleSecret la taille du secret (entre MIN_SECRET_SIZE et MAX_SECRET_SIZE)
     * @param _seuil le nombre de parts minimale (entre 2 et _nombreDeParts)
     * @param _nombreDeParts le nombre de parts totales du secret
     * @throws Exception en cas de problème avec la saisie utilisateur
     */
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

    /**
     * Permet d'obtenir le secret
     * @return le secret
     */
    public BigInteger getSecret() {
        return secret;
    }

    /**
     * Permet d'obtenir le nextProbablePrime calculé initialement
     * @return le p
     */
    public BigInteger getP() {
        return p;
    }

    /**
     * Permet d'obtenir le seuil de parts nécessaires à la reconstruction du secret
     * @return le nombre de parts nécessaires
     */
    public int getSeuil() {
        return seuil;
    }

    /**
     * Permet d'obtenir le nombre de parts totales
     * @return le nombre de parts
     */
    public int getNombreDeParts() {
        return nombreDeParts;
    }

    /**
     * Permet d'obtenir la taille du secret
     * @return la taille du secret
     */
    public int getTailleSecret() {
        return tailleSecret;
    }

    /**
     * Génère le secret et sauve le nextProbablePrime relatif
     */
    private void genererSecret() {
        secret = BigInteger.probablePrime(tailleSecret, new SecureRandom());
        p = secret.nextProbablePrime();
    }

    /**
     * Permet de split le secret
     * @param secret le secret à splitter
     * @param seuil le nombre de parts nécessaires pour reconstruire le secret
     * @param nombreDeParts le nombre de parts en lequel le secret doit être splitté
     * @param p le nextProbablePrime relatif au secret
     * @return un tableau de Share contenant les coordonnées des parts
     */
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

    /**
     * Permet de mettre ensemble des parts pour reconstruire le secret
     * @param x la part cible ( = 0)
     * @param shares les parts connues
     * @param p le nextProbablePrime du secret
     * @return le secret
     * @throws Exception en cas de problème lors de la reconstruction du secret
     */
    public BigInteger combine(int x, Share[] shares, BigInteger p) throws Exception {
        if (shares.length >= seuil) {
            BigInteger rv = BigInteger.ZERO;
            for (int i = 0; i < shares.length; i++) {
                rv = rv.add(shares[i].y.multiply(Math.l(x, shares, i, p)));
            }
            return rv.mod(p);
        } else {
            throw new Exception("Pas assez de parts pour reconstruire le secret");
        }
    }

    /**
     * Permet de générer une nouvelle part
     * @return soi même
     * @throws Exception en cas de problème lors de la génération d'une nouvelle part
     */
    public ShamirSecret genererNouvellePart() throws Exception {
        if (nombreDeParts < MAX_SHARES) {
            System.out.println("Secret partagé en " + nombreDeParts + ", ajout d'une nouvelle part.");

            nombreDeParts += 1;
        } else {
            throw new Exception("Nombre maximale de part atteint.");
        }
        return this;
    }

    /**
     * Permet de retirer une part
     * @return soi même
     * @throws Exception en cas de problème lors du retrait d'une part
     */
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
