package shamir;

import java.math.BigInteger;

/**
 * Stocke les coordonnées d'une part de secret
 */
public class Share {
    public int x;
    public BigInteger y;

    /**
     * Créé une nouvelle part de secret
     * @param x la coordonnée x du secret
     * @param y la coordonnée y du secret
     */
    public Share(int x, BigInteger y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Pour afficher les coordonnées de la part dans la console de manière lisible
     * @return une chaine de caractère lisible avec les coordonnées x et y
     */
    @Override
    public String toString() {
        return "shamir.Share{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
