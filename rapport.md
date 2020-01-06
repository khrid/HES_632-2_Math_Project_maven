#Rapport du projet

##Description de la bibliothèque mathématique

##Guide de l'utilisateur
Le programme offre plusieurs possiblités à son lancement.

* Fermeture du programme
* Générer un nouveau secret
* Calculer un secret
* Générer une nouvelle part
* Départ d'un employé
* Changement du seuil

#### Fermeture du programme
En utilisant l'option 0, le programme se ferme.

#### Générer un nouveau secret
Cette option permet de générer un nouveau secret. Le programme demande les trois paramètres suivants, et dans cet ordre :
* La taille du secret, entre 8 et 256 bits compris,
* Le nombre de parts, entre 2 et 32,
* Le seuil, entre 2 et le nombre de parts.

Une fois les informations saisies, le secret va être généré, ainsi que les parts. Ces dernières sont affichées à l'utilisateur, et le programme se termine.

Deux fichiers .json de métadonnées sont enregistrés dans C:\tmp\shamir (ou /tmp/shamir).
* **metadata.json** qui contient
* shares.json