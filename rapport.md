# Rapport du projet

## Description de la bibliothèque mathématique

## Guide de l'utilisateur
Le programme offre plusieurs possiblités à son lancement.

* Fermeture du programme
* Générer un nouveau secret
* Calculer un secret
* Générer une nouvelle part
* Départ d'un employé
* Changement du seuil

### Fermeture du programme
En utilisant l'option 0, le programme se ferme.

### Générer un nouveau secret
Cette option permet de générer un nouveau secret. Le programme demande les trois paramètres suivants, et dans cet ordre :
* La taille du secret, entre 8 et 256 bits compris,
* Le nombre de parts, entre 2 et 32,
* Le seuil, entre 2 et le nombre de parts.

Une fois les informations saisies, le secret va être généré, ainsi que les parts. Ces dernières sont affichées à l'utilisateur, et le programme se termine. Les métadonnées sont générées (voir section _Metadonnées_).

### Calculer un secret
Cette option permet de reconstruire un secret à partir de parts. Le programme va demander les coordonnées du nomnbre de parts nécessaires à la reconstruction.
Si les informations renseignées permettent la reconstruction du secret, celui-ci est affiché et le programme se termine.

### Génerer une nouvelle part
Cette option permet d'ajouter une nouvelle part à un partage de secret, dans la limite du nombre maximale de part permise par le programme (32).

### Départ d'un employé
Cette option permet de retirer une part à un partage de secret, selon les restrictions du nombre de parts minimales (2), et du seuil.

### Changement du seuil
Cette option permet de changer le seuil de parts nécessaires pour reconstruire le secret. Le nouveau seuil ne peut pas être supérieur aux nombres de parts actuelles.

## Metadonnées
Deux fichiers .json de métadonnées sont enregistrés dans C:\tmp\shamir (ou /tmp/shamir).
* _metadata.json_, qui contient diverses métadonnées nécessaires pour les opérations sur le secret.
  * le secret,
  * le "p",
  * la taille du secret,
  * le seuil,
  * le nombre de parts.
* _shares.json_, qui contient les coordonnées des différentes parts générées.
