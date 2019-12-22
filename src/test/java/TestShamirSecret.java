import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestShamirSecret {

    ShamirSecret shamirSecret;

    @Test
    public void testTailleSecretTropGrande() throws Exception {
        assertThrows(Exception.class, () ->
        {
            shamirSecret = new ShamirSecret(ShamirSecret.MAX_SECRET_SIZE+1, 5, 10);
        });
    }

    @Test
    public void testTailleSecretTropPetite() throws Exception {
        assertThrows(Exception.class, () ->
        {
            shamirSecret = new ShamirSecret(ShamirSecret.MIN_SECRET_SIZE-1, 5, 10);
        });
    }

    @Test
    public void testNombreDePartsTropGrand() throws Exception {
        assertThrows(Exception.class, () ->
        {
            shamirSecret = new ShamirSecret(0, 5, ShamirSecret.MAX_SHARES+1);
        });
    }

    @Test
    public void testNombreDePartsTropPetit() throws Exception {
        assertThrows(Exception.class, () ->
        {
            shamirSecret = new ShamirSecret(0, 1, 1);
        });
    }

    @Test
    public void testSeuilPlusGrandQueNombreDeParts() throws Exception {
        assertThrows(Exception.class, () ->
        {
            shamirSecret = new ShamirSecret(0, 2, 1);
        });
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
        shamirSecret = new ShamirSecret(ShamirSecret.MIN_SECRET_SIZE, ShamirSecret.MAX_SHARES-1, ShamirSecret.MAX_SHARES-1);
        Share[] shares = shamirSecret.split(shamirSecret.getSecret(), shamirSecret.getSeuil(), shamirSecret.getNombreDeParts(), shamirSecret.getP());
        shamirSecret.genererNouvellePart();
        assertEquals(shares.length+1, shamirSecret.getNombreDeParts());
    }

    @Test
    public void tesRetraitDePart() throws Exception {
        shamirSecret = new ShamirSecret(ShamirSecret.MIN_SECRET_SIZE, ShamirSecret.MAX_SHARES-1, ShamirSecret.MAX_SHARES);
        Share[] shares = shamirSecret.split(shamirSecret.getSecret(), shamirSecret.getSeuil(), shamirSecret.getNombreDeParts(), shamirSecret.getP());
        shamirSecret.enleverPart();
        assertEquals(shares.length-1, shamirSecret.getNombreDeParts());
    }
}
