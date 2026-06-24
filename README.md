# LOG660 - Laboratoire 2

Application Web Java/Hibernate basee sur le schema relationnel du laboratoire 1.

## Cas d'utilisation couverts

- Cas 2: connexion d'un client par courriel et mot de passe.
- Cas 3: consultation interactive des films avec recherche par titre, annee, pays, langue, genre, realisateur et acteur.
- Cas 4: location d'un produit de film par un client connecte.

## Configuration a modifier

Avant l'execution, ouvrir `src/main/resources/hibernate.cfg.xml` et remplacer:

- `equipeXX` par le compte Oracle de l'equipe;
- `leMotDePasse` par le mot de passe Oracle;
- `EQUIPEXX` par le schema Oracle en majuscules.

Le projet utilise `Oracle12cDialect`, car le schema du labo 1 contient des colonnes `IDENTITY`.
`org.hibernate.dialect.Oracle10gDialect` pouvais aussi être utiliser.

## Compilation et deploiement

Avec Maven:

```bash
mvn clean package
```

Le fichier WAR sera produit dans:

```text
target/log660-labo2-equipeX.war
```

Il peut ensuite etre deploye dans Tomcat.

## Pages principales

- `/connexion`: connexion d'un client.
- `/films`: recherche de films.
- `/film?id=1`: consultation d'un film.
- `/louer`: servlet POST utilise par le bouton de location.

## Points importants

- Toute operation complexe passe par `WebflixFacade`.
- Les mappings Hibernate XML sont dans `src/main/resources/hibernate`.
- Les regles de location sont verifiees dans l'application et restent aussi protegees par les triggers Oracle du labo 1.
- Le type Oracle `scenariste_list` n'est pas mappe, car il n'est pas necessaire aux cas d'utilisation 2, 3 et 4.
- `FilmProduit.id` et `ContenuMultimedia.id` sont en generation `assigned`, car les tables correspondantes du labo 1 ne declarent pas ces colonnes comme identites.


# Guide d'installation et de lancement - LOG660 Labo 2

Ce document explique comment installer l'environnement necessaire, configurer Tomcat, deployer l'application Webflix du laboratoire 2 et tester les principales fonctionnalites.

## 1. Prerequis

Avant de lancer l'application, il faut avoir:

- Java JDK installe;
- Maven installe;
- Apache Tomcat 9 installe;
- un acces VPN ou reseau ETS fonctionnel;
- le compte Oracle de l'equipe;
- les tables du laboratoire 1 deja creees dans Oracle.

Attention: utiliser Tomcat 9 et non Tomcat 10. Le projet utilise `javax.servlet`, compatible avec Tomcat 9. Tomcat 10 utilise `jakarta.servlet`, ce qui demanderait une migration du code.

## 2. Verifier Java et Maven

Ouvrir PowerShell et executer:

```powershell
java -version
mvn -version
```

Si les deux commandes affichent une version, l'environnement Java/Maven est pret.

## 3. Installer Apache Tomcat 9

1. Telecharger Apache Tomcat 9, version Windows ZIP.
2. Decompresser le fichier, par exemple dans:

```text
C:\apache-tomcat-9.0.118-windows-x64\apache-tomcat-9.0.118
```

3. Configurer les variables d'environnement dans PowerShell:

```powershell
[Environment]::SetEnvironmentVariable("CATALINA_HOME", "C:\apache-tomcat-9.0.118-windows-x64\apache-tomcat-9.0.118", "User")
[Environment]::SetEnvironmentVariable("CATALINA_BASE", "C:\apache-tomcat-9.0.118-windows-x64\apache-tomcat-9.0.118", "User")
```

Fermer et rouvrir PowerShell apres cette configuration.

## 4. Configurer le port de Tomcat

Si le port `8080` est deja utilise, par exemple par Traefik ou Docker, changer le port de Tomcat.

Ouvrir:

```text
C:\apache-tomcat-9.0.118-windows-x64\apache-tomcat-9.0.118\conf\server.xml
```

Chercher:

```xml
<Connector port="8080"
```

Remplacer par:

```xml
<Connector port="8081"
```

Sauvegarder le fichier.

## 5. Demarrer et arreter Tomcat

Pour demarrer Tomcat:

```powershell
cd C:\apache-tomcat-9.0.118-windows-x64\apache-tomcat-9.0.118\bin
.\startup.bat
```

Ouvrir ensuite:

```text
http://localhost:8081
```

Si la page Apache Tomcat apparait, Tomcat fonctionne.

Pour arreter Tomcat:

```powershell
cd C:\apache-tomcat-9.0.118-windows-x64\apache-tomcat-9.0.118\bin
.\shutdown.bat
```

## 6. Configurer Hibernate

Dans le projet, ouvrir:

```text
src/main/resources/hibernate.cfg.xml
```

Verifier et adapter les valeurs suivantes:

```xml
<property name="hibernate.connection.url">jdbc:oracle:thin:@bdlog660.ens.ad.etsmtl.ca:1521:LOG660</property>
<property name="hibernate.connection.username">equipe205</property>
<property name="hibernate.connection.password">VOTRE_MOT_DE_PASSE</property>
<property name="hibernate.default_schema">EQUIPE205</property>
```

Important:

- `hibernate.connection.username` doit correspondre au compte Oracle de l'equipe;
- `hibernate.connection.password` doit correspondre au mot de passe Oracle;
- `hibernate.default_schema` doit etre en majuscules;
- ne pas publier le mot de passe dans GitHub ou dans le rapport.

## 7. Verifier l'acces au serveur Oracle

Avant de tester l'application, verifier que le serveur Oracle est accessible:

```powershell
Test-NetConnection bdlog660.ens.ad.etsmtl.ca -Port 1521
```

Le resultat attendu est:

```text
TcpTestSucceeded : True
```

Si le resultat est `False`, l'application ne pourra pas se connecter a Oracle. Dans ce cas:

- verifier que le VPN ETS est bien connecte;
- verifier que le bon profil VPN est utilise;
- tester depuis le reseau de l'ETS si possible;
- demander au charge de laboratoire si le VPN donne acces au serveur `big-data-3` sur le port `1521`.

## 8. Compiler et generer le WAR

Depuis le dossier du projet:

```powershell
cd "D:\ETS\Session 9 ete 2026\LOG660-01\LABORATOIRES\Travail pratique 2\log660-labo2-equipe5"
mvn clean package
```

Si tout fonctionne, Maven cree le fichier WAR dans:

```text
target\log660-labo2-equipe5.war
```

Si le fichier porte encore le nom `log660-labo2-equipeX.war`, modifier le fichier `pom.xml`:

```xml
<finalName>log660-labo2-equipe5</finalName>
```

Puis relancer:

```powershell
mvn clean package
```

## 9. Deployer l'application dans Tomcat

Copier le fichier WAR vers le dossier `webapps` de Tomcat:

```powershell
copy target\log660-labo2-equipe5.war C:\apache-tomcat-9.0.118-windows-x64\apache-tomcat-9.0.118\webapps\
```

Redemarrer Tomcat:

```powershell
cd C:\apache-tomcat-9.0.118-windows-x64\apache-tomcat-9.0.118\bin
.\shutdown.bat
.\startup.bat
```

Attendre quelques secondes. Tomcat doit extraire automatiquement le WAR dans:

```text
C:\apache-tomcat-9.0.118-windows-x64\apache-tomcat-9.0.118\webapps\log660-labo2-equipe5
```

## 10. Lancer l'application

Ouvrir l'application dans le navigateur:

```text
http://localhost:8081/log660-labo2-equipe5/
```

Pages principales:

```text
http://localhost:8081/log660-labo2-equipe5/index.html
http://localhost:8081/log660-labo2-equipe5/connexion
http://localhost:8081/log660-labo2-equipe5/films
```

## 11. Tester la connexion

Il faut utiliser un client qui existe deja dans la base Oracle.

Exemple de requete SQL pour trouver un client:

```sql
SELECT c.courriel, c.mot_de_passe, u.prenom, u.nom
FROM Client c
JOIN Utilisateur u ON c.id_client = u.id;
```

Ensuite:

1. ouvrir `/connexion`;
2. entrer le courriel du client;
3. entrer son mot de passe;
4. cliquer sur `Se connecter`.

Si la connexion reussit, l'application redirige vers la page de recherche des films.

## 12. Tester la recherche de films

Ouvrir:

```text
http://localhost:8081/log660-labo2-equipe5/films
```

Tester les criteres suivants:

- titre;
- annee minimale et annee maximale;
- pays de production;
- langue;
- genre;
- realisateur;
- acteur.

Cliquer ensuite sur `Consulter` pour afficher la fiche detaillee d'un film.

## 13. Tester la location

Pour louer un film:

1. se connecter avec un client existant;
2. rechercher un film;
3. ouvrir la fiche du film;
4. cliquer sur `Louer` pour un produit disponible.

L'application doit:

- creer une ligne dans `LOCATION`;
- creer une association dans `LOCATION_FILM_PRODUIT`;
- diminuer `FILM_PRODUIT.COPIE_DISPO`;
- refuser la location si aucune copie n'est disponible;
- refuser la location si la limite du forfait est atteinte.

## 14. Problemes frequents

### Erreur 404 sur l'application

Exemple:

```text
Etat HTTP 404 - /log660-labo2-equipe5/ n'est pas disponible
```

Causes possibles:

- le fichier WAR n'a pas ete copie dans `webapps`;
- le nom du WAR n'est pas `log660-labo2-equipe5.war`;
- Tomcat n'a pas encore extrait le WAR;
- l'application n'a pas demarre correctement.

Verifier:

```powershell
dir C:\apache-tomcat-9.0.118-windows-x64\apache-tomcat-9.0.118\webapps
```

### Erreur 500 lors de la connexion

Si l'erreur contient:

```text
Unable to create requested service [JdbcEnvironment]
```

Alors Hibernate n'arrive pas a se connecter a Oracle.

Verifier:

```powershell
Test-NetConnection bdlog660.ens.ad.etsmtl.ca -Port 1521
```

Si `TcpTestSucceeded` vaut `False`, le probleme est reseau/VPN.

### Mauvais identifiants Oracle

Si les logs contiennent:

```text
ORA-01017
```

Alors le nom d'utilisateur ou le mot de passe Oracle est incorrect.

### Table ou colonne introuvable

Si les logs contiennent:

```text
ORA-00942
```

ou:

```text
Invalid column name
```

Alors il faut verifier:

- que les tables du laboratoire 1 existent;
- que `hibernate.default_schema` est correct;
- que les noms de colonnes dans les fichiers `.hbm.xml` correspondent au schema Oracle.

## 15. Consulter les logs Tomcat

Les logs sont dans:

```text
C:\apache-tomcat-9.0.118-windows-x64\apache-tomcat-9.0.118\logs
```

Afficher les dernieres lignes:

```powershell
cd C:\apache-tomcat-9.0.118-windows-x64\apache-tomcat-9.0.118\logs
Get-Content .\catalina.2026-06-21.log -Tail 120
```

Selon le fichier disponible, il peut aussi falloir lire:

```powershell
Get-Content .\localhost.2026-06-21.log -Tail 120
```

Les lignes les plus importantes commencent souvent par:

```text
Caused by:
```

## 16. Resume des commandes principales

```powershell
# Tester Oracle
Test-NetConnection bdlog660.ens.ad.etsmtl.ca -Port 1521

# Compiler le projet
mvn clean package

# Copier le WAR
copy target\log660-labo2-equipe5.war C:\apache-tomcat-9.0.118-windows-x64\apache-tomcat-9.0.118\webapps\

# Redemarrer Tomcat
cd C:\apache-tomcat-9.0.118-windows-x64\apache-tomcat-9.0.118\bin
.\shutdown.bat
.\startup.bat

# Ouvrir l'application
http://localhost:8081/log660-labo2-equipe5/
```

