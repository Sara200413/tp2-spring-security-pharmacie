# TP2 - Sécurisation de gestion de pharmacie (Spring Boot + Spring Security + MySQL)

Module Spring Boot - MicroServices | ENSA Fès | ILIA2

## Lancer
1. Démarrer MySQL (XAMPP / WAMP).
2. Vérifier `src/main/resources/application.properties` (user `root`, mot de passe).
3. Lancer `PharmaSecurityApplication.java` (bouton **Run**). Application sur http://localhost:8081

## Comptes de test (créés par InitData)
| Utilisateur | Mot de passe | Rôle |
|---|---|---|
| admin | admin123 | ADMIN |
| pharma | pharma123 | PHARMACIEN |
| caisse | caisse123 | CAISSIER |
| client | client123 | CLIENT |

## Matrice d'accès
| Page | ADMIN | PHARMACIEN | CAISSIER | CLIENT |
|---|---|---|---|---|
| /login | oui | oui | oui | oui |
| / (accueil) | oui | oui | oui | oui |
| /medicaments (consulter) | oui (tout) | oui (tout) | 403 | oui (disponibles seulement) |
| modifier un stock | oui | oui | 403 | 403 |
| ajouter / supprimer un médicament | oui | 403 | 403 | 403 |
| /ventes | oui | oui | oui | 403 |
| /admin (utilisateurs) | oui | 403 | 403 | 403 |
