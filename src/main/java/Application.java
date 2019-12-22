import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class Application {

    public static final String METADATA_LOCATION = "C:\\tmp\\shamir\\metadata.json";
    public static final String SHARES_LOCATION = "C:\\tmp\\shamir\\shares.json";
    private static ShamirSecret existingShamir;

    /* TODO
        factoriser la création d'un nouveau secret
        factoriser l'écriture des méta data + shares
     */
    public static void main(String[] args) throws Exception {

        loadExistingMedata();

        String[] actions = new String[]{
                "0 - Quitter le programme",
                "1 - Générer un nouveau secret",
                "2 - Calculer un secret",
                "3 - Générer une nouvelle part",
                "4 - Départ d'un employé",
                "5 - Changement du seuil"
        };

        System.out.println("Sélectionner une action : ");
        for (String a :
                actions) {
            System.out.println(a);
        }

        Scanner scanner = new Scanner(System.in);
        int selection = 0;

        do {
            System.out.print("Action choisie : ");
            selection = scanner.nextInt();
            if (selection <= actions.length) {
                switch (selection) {
                    case 1:
                        System.out.println("Génération d'un secret");
                        genererSecret();
                        return;
                    case 2:
                        System.out.println("Calculer un secret");
                        calculerSecret();
                        return;
                    case 3:
                        System.out.println("Générer une nouvelle part");
                        genererNouvellePart();
                        return;
                    case 4:
                        System.out.println("Retirer une part");
                        enleverPart();
                        return;
                    case 5:
                        System.out.println("Changement du seuil");
                        changementSeuil();
                        return;
                }
            }
        } while (selection != 0);
    }

    private static void loadExistingMedata() throws Exception {
        if (Files.exists(Paths.get(METADATA_LOCATION))) {
            System.out.println("Metadonnées trouvées, secret reconstructible.");
            Gson gson = new Gson();
            JsonReader reader = new JsonReader(new FileReader(METADATA_LOCATION));
            existingShamir = gson.fromJson(reader, ShamirSecret.class);
        }
    }

    public static void genererSecret() throws Exception {
        Scanner scanner = new Scanner(System.in);
        int tailleSecret;
        int seuil;
        int nombreDeParts;
        do {
            System.out.print("Taille du secret (" + ShamirSecret.MIN_SECRET_SIZE + " <= n <= " + ShamirSecret.MAX_SECRET_SIZE + ") : ");
            tailleSecret = scanner.nextInt();
        } while (tailleSecret > ShamirSecret.MAX_SECRET_SIZE || tailleSecret < ShamirSecret.MIN_SECRET_SIZE);

        do {
            System.out.print("Nombre de parts (2 < n <= " + ShamirSecret.MAX_SHARES + ") : ");
            nombreDeParts = scanner.nextInt();
        } while (nombreDeParts > ShamirSecret.MAX_SHARES || nombreDeParts < 2);

        do {
            System.out.print("Nombre de parts pour reconstituer le secret (1 < n <= " + nombreDeParts + ") : ");
            seuil = scanner.nextInt();
        } while (seuil > nombreDeParts || seuil < 1);

        existingShamir = new ShamirSecret(tailleSecret, seuil, nombreDeParts);
        saveShamir();
    }

    private static void calculerSecret() {
        if (existingShamir != null) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Pour reconstruire le secret, " + existingShamir.getSeuil() + " parts nécessaires.");
            Share[] partsConnues = new Share[existingShamir.getSeuil()];
            for (int i = 0; i < partsConnues.length; i++) {
                int noDePart;
                BigInteger valeur;
                System.out.print("Part n° : ");
                noDePart = scanner.nextInt();
                System.out.print("Valeur de la part " + noDePart + " : ");
                valeur = scanner.nextBigInteger();
                partsConnues[i] = new Share(noDePart, valeur);
            }

            if (existingShamir.combine(0, partsConnues, existingShamir.getP()).equals(existingShamir.getSecret())) {
                System.out.println("Secret reconstruit avec succès !");
                System.out.println("\t" + existingShamir.getSecret());
            } else {
                System.out.println("Impossible de reconstruire le secret.");
            }
        } else {
            System.out.println("Pas de secret à reconstruire.");
        }
    }

    private static void genererNouvellePart() throws Exception {
        if (existingShamir != null) {
            existingShamir.genererNouvellePart();
            saveShamir();
        } else {
            System.out.println("Pas de secret existant.");
        }
    }

    private static void enleverPart() throws Exception {
        if (existingShamir != null) {
            existingShamir = existingShamir.enleverPart();
            saveShamir();
        } else {
            System.out.println("Pas de secret existant.");
        }
    }

    private static void changementSeuil() throws Exception {
        if (existingShamir != null) {
            Scanner scanner = new Scanner(System.in);
            int seuil;
            do {
                System.out.print("Nombre de parts pour reconstituer le secret (1 < n <= " + existingShamir.getNombreDeParts() + ") : ");
                seuil = scanner.nextInt();
            } while (seuil > existingShamir.getNombreDeParts() || seuil < 1);

            System.out.println("Nouveau seuil pour la reconstitution du secret.");

            existingShamir = new ShamirSecret(existingShamir.getTailleSecret(), seuil, existingShamir.getNombreDeParts());
            saveShamir();
        } else {
            System.out.println("Pas de secret existant.");
        }
    }

    private static void saveShamir() throws Exception {
        Share[] partsDuSecret = existingShamir.split(existingShamir.getSecret(), existingShamir.getSeuil(), existingShamir.getNombreDeParts(), existingShamir.getP());
        System.out.println("Parts du secret : ");
        for (Share s :
                partsDuSecret) {
            System.out.println(s.x + " / " + s.y);
        }

        try (FileWriter writer = new FileWriter(new File(METADATA_LOCATION))) {
            new Gson().toJson(existingShamir, writer);
            writer.flush();
            System.out.println("Métadonnées enregistrées.");
        }
        try (FileWriter writer = new FileWriter(new File(SHARES_LOCATION))) {
            new Gson().toJson(partsDuSecret, writer);
            writer.flush();
            System.out.println("Parts du secret enregistrées.");
        }
    }
}