package shamir;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestShamirSecret {

    ShamirSecret shamirSecret;

    @Test
    public void testTailleSecretTropGrande() {
        assertThrows(Exception.class, () ->
                shamirSecret = new ShamirSecret(ShamirSecret.MAX_SECRET_SIZE + 1, 5, 10));
    }

    @Test
    public void testTailleSecretTropPetite() {
        assertThrows(Exception.class, () ->
                shamirSecret = new ShamirSecret(ShamirSecret.MIN_SECRET_SIZE - 1, 5, 10));
    }

    @Test
    public void testNombreDePartsTropGrand() {
        assertThrows(Exception.class, () ->
                shamirSecret = new ShamirSecret(0, 5, ShamirSecret.MAX_SHARES + 1));
    }

    @Test
    public void testNombreDePartsTropPetit() {
        assertThrows(Exception.class, () ->
                shamirSecret = new ShamirSecret(0, 1, 1));
    }

    @Test
    public void testSeuilPlusGrandQueNombreDeParts() {
        assertThrows(Exception.class, () ->
                shamirSecret = new ShamirSecret(0, 2, 1));
    }

    @Test
    public void testCreationShamir() throws Exception {
        assertNotNull(new ShamirSecret(ShamirSecret.MIN_SECRET_SIZE, ShamirSecret.MAX_SHARES, ShamirSecret.MAX_SHARES).getSecret());
    }

    @Test
    public void testPartageDeSecret() throws Exception {
        shamirSecret = new ShamirSecret(ShamirSecret.MIN_SECRET_SIZE, ShamirSecret.MAX_SHARES, ShamirSecret.MAX_SHARES);
        Share[] shares = shamirSecret.split(shamirSecret.getSecret(), shamirSecret.getSeuil(), shamirSecret.getNombreDeParts(), shamirSecret.getP());
        assertEquals(shares.length, shamirSecret.getNombreDeParts());
    }

    @Test
    public void testAjoutDePart() throws Exception {
        shamirSecret = new ShamirSecret(ShamirSecret.MIN_SECRET_SIZE, ShamirSecret.MAX_SHARES - 1, ShamirSecret.MAX_SHARES - 1);
        Share[] shares = shamirSecret.split(shamirSecret.getSecret(), shamirSecret.getSeuil(), shamirSecret.getNombreDeParts(), shamirSecret.getP());
        shamirSecret.genererNouvellePart();
        assertEquals(shares.length + 1, shamirSecret.getNombreDeParts());
    }

    @Test
    public void testAjoutDePartImpossibleSiMax() throws Exception {
        shamirSecret = new ShamirSecret(ShamirSecret.MIN_SECRET_SIZE, ShamirSecret.MAX_SHARES, ShamirSecret.MAX_SHARES);
        Share[] shares = shamirSecret.split(shamirSecret.getSecret(), shamirSecret.getSeuil(), shamirSecret.getNombreDeParts(), shamirSecret.getP());
        assertThrows(Exception.class, () ->
                shamirSecret.genererNouvellePart());
    }

    @Test
    public void tesRetraitDePart() throws Exception {
        shamirSecret = new ShamirSecret(ShamirSecret.MIN_SECRET_SIZE, ShamirSecret.MAX_SHARES - 1, ShamirSecret.MAX_SHARES);
        Share[] shares = shamirSecret.split(shamirSecret.getSecret(), shamirSecret.getSeuil(), shamirSecret.getNombreDeParts(), shamirSecret.getP());
        shamirSecret.enleverPart();
        assertEquals(shares.length - 1, shamirSecret.getNombreDeParts());
    }

    @Test
    public void testRetraitDePartImpossibleSiMin() throws Exception {
        shamirSecret = new ShamirSecret(ShamirSecret.MIN_SECRET_SIZE, 2, 2);
        Share[] shares = shamirSecret.split(shamirSecret.getSecret(), shamirSecret.getSeuil(), shamirSecret.getNombreDeParts(), shamirSecret.getP());
        assertThrows(Exception.class, () ->
                shamirSecret.enleverPart());
    }

    @Test
    public void testRetraitDePartImpossibleSiPlusPetitQueSeuil() throws Exception {
        shamirSecret = new ShamirSecret(ShamirSecret.MIN_SECRET_SIZE, ShamirSecret.MAX_SHARES, ShamirSecret.MAX_SHARES);
        Share[] shares = shamirSecret.split(shamirSecret.getSecret(), shamirSecret.getSeuil(), shamirSecret.getNombreDeParts(), shamirSecret.getP());
        assertThrows(Exception.class, () ->
                shamirSecret.enleverPart());
    }

    @Test
    public void testReconstructionSecret() throws Exception {
        shamirSecret = new ShamirSecret(ShamirSecret.MIN_SECRET_SIZE, 3, 5);
        Share[] shares = shamirSecret.split(shamirSecret.getSecret(), shamirSecret.getSeuil(), shamirSecret.getNombreDeParts(), shamirSecret.getP());

        int[][] possiblites = new int[][]{
                {1, 2, 3},
                {1, 2, 4},
                {1, 2, 5},
                {1, 3, 4},
                {1, 3, 5},
                {1, 4, 5},
                {2, 3, 4},
                {2, 3, 5},
                {2, 4, 5}
        };

        for (int i = 0; i < possiblites.length - 1; i++) {
            System.out.print("Reconstruction du secret avec les parts ");
            Share[] partsConnues = new Share[possiblites[i].length];
            for (int j = 0; j < possiblites[i].length; j++) {
                partsConnues[j] = shares[possiblites[i][j] - 1]; // l'index des parts commence à 1 et non 0
                System.out.print(possiblites[i][j] + " ");
            }
            System.out.println();
            assertEquals(shamirSecret.getSecret(), shamirSecret.combine(0, partsConnues, shamirSecret.getP()));
        }
    }

    @Test
    public void testReconstructionSecretSansAssezDeParts() throws Exception {
        shamirSecret = new ShamirSecret(ShamirSecret.MIN_SECRET_SIZE, 3, 5);
        Share[] shares = shamirSecret.split(shamirSecret.getSecret(), shamirSecret.getSeuil(), shamirSecret.getNombreDeParts(), shamirSecret.getP());
        Share[] partsConnues = new Share[]{shares[0], shares[2]};

        assertThrows(Exception.class, () ->
                shamirSecret.combine(0, partsConnues, shamirSecret.getP()));
    }
}
