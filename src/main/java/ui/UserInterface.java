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

    private Scanner scanner = new Scanner(System.in);
    private int selection;
    private ShamirSecret shamirSecret;
    private Share[] sharesFromMetadata;
    private BigInteger secret;

    private static final String BASE_DIR = "/tmp/shamir";
    private static final String METADATA_LOCATION = BASE_DIR+"/metadata.json";
    private static final String SHARES_LOCATION = BASE_DIR+"/shares.json";

    /**
     * Charge les métadonnées depuis une précédente exécution.
     * @throws Exception en cas de problème lors de la lecture du fichier de métadonnées
     */
    private void loadExistingMedata() throws Exception {
        System.out.println(" /!\\ Metadonnées trouvées, secret reconstructible /!\\");
        System.out.println("------------------------------------------------------");
        Gson gson = new Gson();
        JsonReader reader = new JsonReader(new FileReader(METADATA_LOCATION));
        shamirSecret = gson.fromJson(reader, ShamirSecret.class);
        reader = new JsonReader(new FileReader(SHARES_LOCATION));
        sharesFromMetadata = gson.fromJson(reader, Share[].class);
        secret = shamirSecret.combine(0, sharesFromMetadata, shamirSecret.getP());
    }

    /**
     * Affiche les interactions possibles avec l'utilisateur
     * @throws Exception en cas de problème lors de la création des répertoires
     */
    public void start() throws Exception {
        System.out.println("======================================================");
        System.out.println("\t Projet Math | David Crittin & Sylvain Meyer");
        System.out.println("======================================================");
        if(!Files.exists(Paths.get(BASE_DIR))) {
            if (!new File(BASE_DIR).mkdir()) {
                System.out.println("Impossible de créer les répertoires. Merci de les créer manuellement (C:"+BASE_DIR+" ou "+BASE_DIR+")");
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

    /**
     * Démarre le processus utilisateur pour la création d'un nouveau secret
     * @throws Exception en cas de problème lors de la création d'une nouvelle part
     */
    private void genererSecret() throws Exception {
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

    /**
     * Démarrer le processus utilisateur pour la reconstruction d'un secret
     */
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
                try {
                    //if (secret.equals(secretReconstruit)) {
                    //    System.out.println("Secret reconstruit avec succès !");
                    //    System.out.println("\t" + secret);
                    //} else {
                    //    System.out.println("Impossible de reconstruire le secret.");
                    //}
                    // on ne doit pas contrôler si les parts sont correctes. L'utilisateur le verra lorsqu'il voudra
                    // employer le secret. Mais pour être sûr on l'affiche quand même :)
                    System.out.println("Secret : "+secretReconstruit + " ("+secret+")");
                } catch (Exception e) {
                    System.out.println("Impossible de reconstruire le secret.");
                }
            }
        } else {
            System.out.println("Pas de secret à reconstruire.");
        }
    }

    /**
     * Démarrer le processus utilisateur pour la génération d'une nouvelle part
     */
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

    /**
     * Démarrer le processus utilisateur pour le retrait d'une part
     */
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

    /**
     * Démarrer le processus utilisateur pour le changement du seuil
     * @throws Exception en cas de problème lors du changement de seuil
     */
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

    /**
     * Enregistre l'instance courante de l'objet ShamirSecret dans un fichier de métadonnées, ainsi que les parts du secret.
     * @throws Exception en cas de problème lors de l'écriture des fichiers
     */
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