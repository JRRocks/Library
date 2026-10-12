# Google : autorisation (Identity) et stockage Drive `appDataFolder`

| Champ | Valeur |
|---|---|
| **Identifiant** | `google-drive-appdata` |
| **Statut** | ✅ **Éprouvé.** Connexion Google et dépôt sur Drive confirmés sur appareil réel, debug **et** release signée, en production chez les utilisateurs de Popote Compagnie. Les parties pures sont couvertes par `DriveAppDataTest` |
| **Plateforme** | Android natif, Kotlin, minSdk 26 |
| **Dépendances** | `com.google.android.gms:play-services-auth:21.3.0` (API `Identity.getAuthorizationClient`), `kotlinx-serialization-json` 1.7.3, coroutines. **Pas** de SDK Drive Java ni de Google API Client : du REST pur via `HttpURLConnection` |
| **Provenance** | [JRRocks/Suivie-Popote-Cie](https://github.com/JRRocks/Suivie-Popote-Cie) (privé), commit `08c211a` (2026-10-12) |
| **Utilisé dans** | Popote Compagnie (sauvegardes chiffrées, photos de passation) |
| **Fichiers d'origine** | `core/src/main/kotlin/fr/popote/core/sauvegarde/DriveAppData.kt`, `app-terrain/.../ui/Sauvegarde.kt` (lanceurs Compose), `core/.../sauvegarde/TravailSauvegarde.kt` (WorkManager), `docs/decisions.md` (section « La clé de signature debug se versionne… ») |
| **Dernière mise à jour de la fiche** | 2026-10-12 |

## À quoi ça sert

On dépose et on relit des fichiers (sauvegardes chiffrées, photos…) dans le **dossier
applicatif caché** du Google Drive de l'utilisateur :

- portée **`drive.appdata`** : un dossier par application et par compte, invisible dans le
  Drive, illisible par les autres apps ;
- portée **non sensible** au sens de Google : **pas de validation Google** à passer pour
  publier, ce qui est praticable pour une petite app ;
- les données suivent le **compte**, pas le téléphone : sur un appareil neuf, il suffit de
  se reconnecter au même compte pour retrouver le dossier ;
- **aucun secret dans l'APK** : Google reconnaît l'app par son *package* et le **SHA-1 du
  certificat de signature**.

## Prérequis hors code (console Google Cloud, une fois)

1. Créer un projet Cloud et y **activer l'API Google Drive**.
2. Écran de consentement OAuth : en mode test, ajouter les adresses testeuses ; ou le
   **publier** (portées non sensibles, donc sans revue).
3. Créer un **client OAuth de type Android** avec le package `applicationId` et le **SHA-1**
   du certificat.
4. **Déclarer les deux SHA-1, debug et release, sur le même client.** Un client Android
   accepte plusieurs empreintes. Si la release manque, la release signée renvoie
   `UNREGISTERED_ON_API_CONSOLE` alors que le debug fonctionne (vécu).
5. **Versionnez la clé de debug** (`app/keystore/debug.keystore`, alias `androiddebugkey`,
   mot de passe `android`) et pointez-la dans `signingConfigs.debug`. Sinon, chaque conteneur
   cloud génère une clé neuve, son SHA-1 ne correspond plus et la connexion échoue « sans
   qu'aucun code n'ait changé ». La clé de **release**, elle, ne se versionne **jamais**.
6. Si la clé release change un jour, ajoutez la nouvelle empreinte au même client **avant**
   de publier.

## Flux d'autorisation (API moderne `AuthorizationClient`, pas l'ancien `GoogleSignIn`)

```kotlin
// 1. Demande : appDataFolder + email (l'email sert seulement à AFFICHER le compte utilisé)
AuthorizationRequest.builder()
    .setRequestedScopes(listOf(Scope("https://www.googleapis.com/auth/drive.appdata"), Scope(Scopes.EMAIL)))
    .apply { compte?.let { setAccount(it) } }     // épingler un compte précis pour « changer de compte »
    .build()

// 2. Identity.getAuthorizationClient(ctx).authorize(req) rend :
//    - hasResolution()  → AEcran(pendingIntent) : écran de consentement à présenter (normal au 1er branchement)
//    - accessToken != null → Accordee(email)    : l'email est relu via https://www.googleapis.com/oauth2/v3/userinfo
//                                                (AuthorizationResult ne porte pas l'identité du compte)
```

Côté Compose :

```kotlin
val consentement = rememberLauncherForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { retour ->
    scope.launch { traiter(DriveAppData.autorisationDepuisIntention(ctx, retour.data)) }
}
// au clic :
when (val a = DriveAppData.demanderAutorisation(ctx)) {
    is Autorisation.Accordee -> afficherCompte(a.compte)
    is Autorisation.AEcran   -> consentement.launch(IntentSenderRequest.Builder(a.intention).build())
    is Autorisation.Echec    -> afficherErreur(a.motif)
}
```

**Changer de compte** : `authorize()` réutilise en silence le compte déjà autorisé. Il faut
d'abord faire choisir un compte avec
`AccountManager.newChooseAccountIntent(null, null, arrayOf("com.google"), null, null, null, null)`,
puis rappeler `demanderAutorisation(ctx, Account(nom, type))` pour l'épingler.

**En tâche de fond** (WorkManager) : `jeton(ctx)` lève `IllegalStateException` si
l'autorisation n'a jamais été donnée (échec **définitif** : il faut quelqu'un devant
l'écran) et `IOException` pour le transitoire (à **réessayer**). Un HTTP 401 ou 403 de Drive
devient `AutorisationRevoquee`, à traiter aussi comme définitif pour éviter des réessais
infinis.

## Appels REST Drive v3 (dans `DriveAppData`)

| Fonction | Requête |
|---|---|
| `lister(ctx)` | `GET /drive/v3/files?spaces=appDataFolder&fields=nextPageToken,files(id,name,modifiedTime,size)&pageSize=1000`, en **suivant `nextPageToken`** (20 pages max en garde-fou) |
| `envoyer(ctx, nom, octets)` | `POST /upload/drive/v3/files?uploadType=multipart` (création, avec `parents:["appDataFolder"]`) ou `PATCH .../files/{id}?uploadType=multipart` (remplacement, **sans** `parents`) |
| `envoyerFichier(...)` | Pareil, en flux (gros fichiers, pas tout en mémoire) |
| `telecharger(ctx, id)` | `GET /drive/v3/files/{id}?alt=media`, avec progression |
| `supprimer(ctx, id)` | `DELETE /drive/v3/files/{id}` |

Délais : 15 s pour la connexion et 15 s pour la lecture.

## Intégration dans une nouvelle app

1. Faites les prérequis de la console ci-dessus.
2. `implementation("com.google.android.gms:play-services-auth:21.3.0")`, la sérialisation
   Kotlin, la permission `INTERNET`.
3. Copiez `src/DriveAppData.kt`, changez le package, puis **retirez la partie propre à
   Popote** : la classe `Espace`, le nommage et la rotation des archives (`nomSocle`,
   `nomPoint`, `aPurger`, `eclaircir`, `nomQuotidien`, `nomTrousseau`, `nomPhoto`) et l'import
   de `ParametresEntity`. Le **noyau générique** à garder : `requeteAutorisation`,
   `Autorisation`, `demanderAutorisation`, `autorisationDepuisIntention`, `lireResultat`,
   `jeton`, `lister`, `envoyer`, `envoyerFichier`, `telecharger`, `supprimer`, `lireListe`,
   `lireJetonPage`, `lireEmail`, `construire*Multipart`, `appel`, `appelFlux`,
   `lireReponse`, `AutorisationRevoquee`.
4. Branchez les lanceurs Compose comme ci-dessus.
5. Pour des sauvegardes périodiques : un `CoroutineWorker` qui appelle `envoyer` et mappe
   `IllegalStateException`/`AutorisationRevoquee` vers `Result.failure()` et `IOException`
   vers `Result.retry()`.

## Points de vigilance

- **Paginer, toujours.** La première version demandait 100 fichiers sans suivre
  `nextPageToken`. Au-delà, des fichiers étaient **silencieusement invisibles** et une clé
  présente se lisait comme absente.
- **Ne pas rejouer `parents` sur un PATCH** : Drive le refuse.
- Le **chiffrement** des données envoyées n'est pas dans ce moteur. Dans Popote, il est fait
  en amont (trousseau asymétrique, `Trousseau.kt`, `Enveloppe.kt`) : candidat séparé, voir
  l'INDEX.
- Une donnée nominative qui quitte le téléphone doit être annoncée aux personnes concernées.
- Rien de tout cela ne se teste en JVM (pas de compte, pas de Play Services) : seules les
  fonctions pures sont testées.

## Historique des retours d'usage

| Date | Source | Retour | Effet sur la fiche |
|---|---|---|---|
| Phase 8 | Popote | Connexion qui casse d'un conteneur à l'autre | Clé debug versionnée (prérequis 5) |
| Première release signée | Popote, appareil réel | `UNREGISTERED_ON_API_CONSOLE` | Second SHA-1 sur le même client (prérequis 4) |
| Plus tard | Popote | Archives invisibles au-delà de 100 fichiers | Pagination complète |
| 2026-10-12 | Catalogage Library | En production | Statut ✅ |
