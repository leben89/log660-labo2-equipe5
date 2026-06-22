# Brouillon de rapport - LOG660 Labo 2

## Manuel utilisateur

### Cas 2 - Connexion au systeme

L'utilisateur ouvre la page `/connexion`, entre son courriel et son mot de passe, puis soumet le formulaire. Si les informations correspondent a un client present dans la base de donnees, l'application cree une session HTTP contenant l'identifiant du client et redirige vers la page de recherche des films. En cas d'erreur, un message indique que le courriel ou le mot de passe est invalide.

### Cas 3 - Consultation interactive des films

La page `/films` permet de rechercher les films selon plusieurs criteres: titre, intervalle d'annees, pays de production, langue originale, genre, nom du realisateur et nom d'un acteur. Les resultats sont affiches sous forme de tableau. Chaque resultat contient un lien "Consulter" qui ouvre la fiche detaillee du film. La fiche affiche le titre, l'annee, la langue, la duree, les genres, les pays, les realisateurs, les acteurs, le resume, les bandes-annonces et les produits disponibles pour la location.

### Cas 4 - Location de films

Un client doit d'abord etre connecte. Sur la page de consultation d'un film, l'application affiche les produits de film et le nombre de copies disponibles. Le bouton "Louer" cree une location, associe le produit au client et decremente le nombre de copies disponibles. Si aucune copie n'est disponible ou si la limite du forfait est atteinte, la location est refusee avec un message explicite.

## Strategie de test

Les tests fonctionnels ont ete organises autour des trois cas d'utilisation demandes. Pour la connexion, les essais couvrent un courriel valide, un mot de passe invalide, un formulaire incomplet et un client inexistant. Pour la recherche, chaque critere est teste separement afin de verifier le bon fonctionnement des jointures Hibernate: titre, annee, pays, langue, genre, realisateur et acteur. Des tests combinant plusieurs criteres permettent aussi de verifier que la requete dynamique retourne uniquement les films pertinents. Pour la location, les tests couvrent une location valide, la location sans connexion, la location d'un produit inexistant, la location sans copie disponible et le depassement de la limite du forfait.

La validation finale peut etre faite manuellement dans Tomcat avec des captures d'ecran pour chaque cas d'utilisation, puis par inspection directe de la base Oracle pour confirmer la creation des lignes dans `LOCATION` et `LOCATION_FILM_PRODUIT`, ainsi que la diminution de `FILM_PRODUIT.COPIE_DISPO`.

## Patrons de conception employes

Le patron de facade est utilise par la classe `WebflixFacade`. Cette classe offre une interface simple a la couche presentation: `connecter`, `rechercherFilms`, `consulterFilm` et `louerFilm`. Les servlets n'ont donc pas a connaitre les details des requetes HQL, des transactions ou des contraintes de location.

L'application utilise aussi un utilitaire de fabrique pour Hibernate avec `HibernateUtil`. Cette classe centralise la creation du `SessionFactory`, evite la duplication de la configuration Hibernate et facilite l'ouverture des sessions dans la facade.

## Validation des contraintes

Les contraintes structurelles restent validees dans Oracle par les cles primaires, cles etrangeres, contraintes `CHECK` et contraintes d'unicite creees au laboratoire 1. Les regles de location sont verifiees dans l'application avant l'insertion: le produit doit exister, le nombre de copies disponibles doit etre superieur a zero et le client ne doit pas depasser le nombre de locations permis par son forfait. Les triggers du laboratoire 1 restent une protection supplementaire au niveau de la base de donnees.

## Validation de l'acces et securite du systeme

L'acces a la location est controle par la session HTTP. Un client doit etre connecte pour louer un film. La connexion est faite a partir du courriel unique et du mot de passe stockes dans la table `CLIENT`. Dans une version de production, les mots de passe devraient etre haches avec un algorithme adapte, par exemple BCrypt, et la connexion devrait utiliser HTTPS. Dans ce laboratoire, l'objectif principal est de demontrer le fonctionnement de l'architecture trois couches et du mapping ORM.

## Modifications au schema

Aucune modification majeure n'est necessaire pour les cas 2, 3 et 4. Le type Oracle `scenariste_list` n'est pas mappe dans l'application, car les scenaristes ne sont pas requis par les cas d'utilisation demandes. Les identifiants de `FILM_PRODUIT` et `CONTENU_MULTIMEDIA` sont traites comme des identifiants assignes dans Hibernate, puisque les colonnes correspondantes ne sont pas declarees comme identites dans le schema du laboratoire 1.

## Difficultes rencontrees avec Hibernate

La principale difficulte vient du mapping d'un schema deja normalise, avec plusieurs tables de liaison plusieurs-a-plusieurs (`FILM_ACTEUR`, `FILM_REALISATEUR`, `FILM_GENRE`, `FILM_PAYS_PRODUCTION`). Une autre difficulte est la strategie de specialisation: `CLIENT` et `PERSONNE` sont modelises comme sous-classes de `UTILISATEUR`, ce qui correspond a une strategie "table par sous-classe" avec des jointures. Enfin, le type Oracle `VARRAY` pour les scenaristes est specifique a Oracle et n'est pas necessaire pour les cas d'utilisation du labo.

## Code de mappage pour les films, acteurs et realisateurs

Les fichiers principaux sont:

- `src/main/resources/hibernate/Film.hbm.xml`
- `src/main/resources/hibernate/Utilisateur.hbm.xml`
- `src/main/resources/hibernate/FilmProduit.hbm.xml`
- `src/main/resources/hibernate/Location.hbm.xml`

Dans `Film.hbm.xml`, les acteurs et les realisateurs sont mappes comme des associations plusieurs-a-plusieurs vers la classe `Personne`, via les tables `FILM_ACTEUR` et `FILM_REALISATEUR`. Ce choix correspond directement au schema relationnel du laboratoire 1: un film peut avoir plusieurs acteurs et un acteur peut jouer dans plusieurs films; de meme, un film peut avoir plusieurs realisateurs et une personne peut realiser plusieurs films.

## Planification des taches

| Tache | Responsable | Debut | Fin | Commentaire |
|---|---|---:|---:|---|
| Analyse de l'enonce et du bareme | Equipe | Jour 1 | Jour 1 | Identification des cas 2, 3 et 4 |
| Creation des classes metier | Membre 1 | Jour 1 | Jour 2 | Film, Personne, Client, Location |
| Configuration Hibernate | Membre 2 | Jour 2 | Jour 3 | hibernate.cfg.xml et fichiers hbm |
| Facade et logique transactionnelle | Membre 3 | Jour 3 | Jour 4 | Connexion, recherche, consultation, location |
| Client mince Web | Membre 1 | Jour 4 | Jour 5 | Servlets et formulaires HTML |
| Tests et captures d'ecran | Equipe | Jour 5 | Jour 6 | Validation des cas normaux et erreurs |
| Rapport final | Equipe | Jour 6 | Jour 7 | Justification des choix et manuel utilisateur |

## Question theorique 1

Deux avantages d'un ORM comme Hibernate sont la reduction du code repetitif JDBC et la gestion transparente des associations entre objets. Hibernate evite d'ecrire manuellement toutes les requetes d'insertion, de mise a jour et de lecture simples, tout en permettant de naviguer dans le modele objet avec des associations comme `film.getActeurs()`. Il facilite aussi la gestion des transactions, du chargement paresseux et du mapping entre objets Java et tables relationnelles.

Ces avantages sont moins utiles lorsque l'application execute surtout des traitements analytiques massifs, des requetes SQL tres optimisees, des procedures stockees complexes ou des operations ou la performance SQL fine est plus importante que la facilite de navigation objet. Dans ces contextes, du SQL explicite ou des outils specialises peuvent etre plus appropries.

## Question theorique 2

Le patron de facade sert a fournir un point d'entree simple et stable vers un sous-systeme plus complexe. Dans une application a plusieurs couches, la facade protege la couche presentation des details de persistance, des requetes Hibernate, des transactions et des regles d'affaires internes. Les servlets appellent seulement des methodes de haut niveau, ce qui rend le code plus lisible, plus facile a tester et plus simple a modifier si la logique interne change.
