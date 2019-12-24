package ui;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import shamir.ShamirSecret;
import shamir.Share;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class UserInterface {

    Scanner scanner = new Scanner(System.in);
    int selection;
    private ShamirSecret shamirSecret;

    public static final String BASE_DIR = "/tmp/shamir";
    public static final String METADATA_LOCATION = BASE_DIR+"/metadata.json";
    public static final String SHARES_LOCATION = BASE_DIR+"/shares.json";


    private void loadExistingMedata() throws Exception {
        System.out.println(" /!\\ Metadonnées trouvées, secret reconstructible /!\\");
        System.out.println("------------------------------------------------------");
        Gson gson = new Gson();
        JsonReader reader = new JsonReader(new FileReader(METADATA_LOCATION));
        shamirSecret = gson.fromJson(reader, ShamirSecret.class);
    }

    public void start() throws Exception {
        System.out.println("======================================================");
        System.out.println("\t Projet Math | David Crittin & Sylvain Meyer");
        System.out.println("======================================================");
        if(!Files.exists(Paths.get(BASE_DIR))) {
            if (!new File(BASE_DIR).mkdir()) {
                System.out.println("Impossible de créer les répertoires.");
                return;
            }
        }

        if (Files.exists(Paths.get(METADATA_LOCATION))) {
            loadExistingMedata();
        }

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

    public void genererSecret() throws Exception {
        int tailleSecret, seuil, nombreDeParts;
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
        } while (seuil > nombreDeParts || seuil <= 1);

        shamirSecret = new ShamirSecret(tailleSecret, seuil, nombreDeParts);
        saveShamir();
    }

    private void calculerSecret() {
        if (shamirSecret != null) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Pour reconstruire le secret, " + shamirSecret.getSeuil() + " parts nécessaires.");
            Share[] partsConnues = new Share[shamirSecret.getSeuil()];
            for (int i = 0; i < partsConnues.length; i++) {
                int noDePart;
                BigInteger valeur;
                System.out.print("Part n° : ");
                noDePart = scanner.nextInt();
                System.out.print("Valeur de la part " + noDePart + " : ");
                valeur = scanner.nextBigInteger();
                partsConnues[i] = new Share(noDePart, valeur);
            }

            BigInteger secretReconstruit = null;
            try {
                secretReconstruit = shamirSecret.combine(0, partsConnues, shamirSecret.getP());
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

            if (secretReconstruit != null) {
                if (secretReconstruit.equals(shamirSecret.getSecret())) {
                    System.out.println("Secret reconstruit avec succès !");
                    System.out.println("\t" + shamirSecret.getSecret());
                } else {
                    System.out.println("Impossible de reconstruire le secret.");
                }
            }
        } else {
            System.out.println("Pas de secret à reconstruire.");
        }
    }

    private void genererNouvellePart() {
        if (shamirSecret != null) {
            try {
                shamirSecret.genererNouvellePart();
                saveShamir();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } else {
            System.out.println("Pas de secret existant.");
        }
    }

    private void enleverPart() {
        if (shamirSecret != null) {
            try {
                shamirSecret.enleverPart();
                saveShamir();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } else {
            System.out.println("Pas de secret existant.");
        }
    }

    private void changementSeuil() throws Exception {
        if (shamirSecret != null) {
            Scanner scanner = new Scanner(System.in);
            int seuil;
            do {
                System.out.print("Nombre de parts pour reconstituer le secret (1 < n <= " + shamirSecret.getNombreDeParts() + ") : ");
                seuil = scanner.nextInt();
            } while (seuil > shamirSecret.getNombreDeParts() || seuil < 1);

            System.out.println("Nouveau seuil pour la reconstitution du secret.");

            shamirSecret = new ShamirSecret(shamirSecret.getTailleSecret(), seuil, shamirSecret.getNombreDeParts());
            saveShamir();
        } else {
            System.out.println("Pas de secret existant.");
        }
    }

    private void saveShamir() throws Exception {
        Share[] partsDuSecret = shamirSecret.split(shamirSecret.getSecret(), shamirSecret.getSeuil(), shamirSecret.getNombreDeParts(), shamirSecret.getP());
        System.out.println("Parts du secret : ");
        for (Share s :
                partsDuSecret) {
            System.out.println(s.x + " / " + s.y);
        }

        try (FileWriter writer = new FileWriter(new File(METADATA_LOCATION))) {
            new GsonBuilder().setPrettyPrinting().create().toJson(shamirSecret, writer);
            writer.flush();
            System.out.println("Métadonnées enregistrées.");
        }
        try (FileWriter writer = new FileWriter(new File(SHARES_LOCATION))) {
            new GsonBuilder().setPrettyPrinting().create().toJson(partsDuSecret, writer);
            writer.flush();
            System.out.println("Parts du secret enregistrées.");
        }
    }
}