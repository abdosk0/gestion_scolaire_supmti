# Gestion Scolaire – Mini-Projet Java

Application JavaFX + PostgreSQL pour gérer les étudiants, les cours et les notes.
Réalisée dans le cadre du module Programmation Java – SUPMTI Rabat.

## Prérequis

- Java JDK 17+
- Maven
- PostgreSQL (le mot de passe par défaut dans le code est `root`)

## Mise en place

1. Créer la base de données :
   ```
   psql -U postgres -f database.sql
   ```

2. Vérifier les identifiants dans `src/main/java/utils/DatabaseConnection.java`

3. Lancer l'application :
   ```
   mvn javafx:run
   ```
   Ou utiliser la configuration Run **"Main (JavaFX)"** dans IntelliJ.

## Structure

```
src/main/java/
    Main.java
    controllers/   (MainController, EtudiantController, CoursController, NoteController)
    models/        (Etudiant, Cours, Note + DAOs)
    utils/         (DatabaseConnection)
src/main/resources/
    views/         (fichiers FXML)
    styles/        (app.css)
```
