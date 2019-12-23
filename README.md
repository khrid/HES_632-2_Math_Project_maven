## 632-2 Math project - Shamir's secret sharing

:construction: Work in progress :construction:

### Students
* [David Crittin](mailto:david.crittin@students.hevs.ch)
* [Sylvain Meyer](mailto:sylvain.meyer@students.hevs.ch)

### Professor
* [Jean-Luc Beuchat](mailto:jean-luc.beuchat@hevs.ch)

### Goal
> _from project.md found on Cyberlearn_

Écrire un logiciel permettant le partage d’un secret (*Shamir's secret
sharing* étudié pendant le cours). Le secret est une chaîne de bits
aléatoires (on peut supposer que ce nombre est un multiple de 8). Ce
logiciel est par exemple utilisé pour protéger des clés cryptographiques
(de 128 à 4096 bits). Les fonctions suivantes sont demandées:

- Génération d’un nouveau secret
	- Entrées : taille du secret (nombre de bytes), nombre minimum
	  de parts pour reconstruire le secret, nombre total de parts à générer.
	- Sorties : les parts du secret ainsi que d’éventuelles *metadata*
	  utiles pour la reconstruction du secret.
	- Un message d’erreur est affiché si les paramètres d’entrée sont erronés.
- Calcul du secret à partir de parts et des éventuelles *metadata*.
  Une erreur est affichée si la reconstruction n’est pas possible.
- Génération d’une nouvelle part de secret pour un employé rejoignant
  l’entreprise. A quelles conditions peut-on effectuer cette opération ?
- Gestion du départ d’employés. Le logiciel permet de renouveler les parts
  du secret des employés restant dans l’entreprise. On peut également
	utiliser cette fonction pour modifier le seuil.
- Interface utilisateur : ligne de commande.

**Pour les groupes de trois étudiants, le secret peut également être une
chaîne de caractères choisie par l’utilisateur. Écrire une fonction prenant
en paramètre le secret, le nombre minimum de parts pour le reconstruire et
le nombres de parts à générer.**

Contraintes :
- Le projet doit être réalisé en Java et testé unitairement (*Junit 5*).
- La seule bibliothèque externe autorisée pour coder l’interpolaton de
  Lagrange est *BigInteger*.
- Il est demandé d’implémenter l’algorithme d’Euclide étendu pour calculer
  les inverses muliplicatifs et de le tester en le comparant à
	*BigInteger.modInverse*.
- Le code est documenté (*Javadoc*).
- Organisation/qualité du code: appliquer les principes de la POO étudiés au
  semestre précédent.
- Gestion de versions : *git* (*GitHub* ou *GitLab*)

