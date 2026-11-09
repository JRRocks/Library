# Google : autorisation (Identity) et stockage Drive `appDataFolder`

| Champ | Valeur |
|---|---|
| **Identifiant** | `google-drive-appdata` |
| **Statut** | ✅ **Éprouvé.** Connexion Google et dépôt sur Drive confirmés sur appareil réel, debug **et** release signée, en production chez les utilisateurs de Popote Compagnie. Le dépôt éprouvé est le dépôt **manuel** (« Sauvegarder maintenant ») : l'envoi périodique par WorkManager n'a jamais été vérifié en réel. Les parties pures sont couvertes par `DriveAppDataTest` (28 tests). ⚠️ Lecture des refus 403 : **patch proposé le 2026-10-26** (tests JVM), non vérifié sur appareil, pas encore reporté dans Popote. ⚠️ Jeton révoqué gardé en cache (suspecté ; correctif seulement dans le Carnet, ni dans `src/` ni dans Popote). Voir les points de vigilance |
| **Plateforme** | Android natif, Kotlin, minSdk 26 |
| **Dépendances** | `com.google.android.gms:play-services-auth:21.3.0` (API `Identity.getAuthorizationClient`), `kotlinx-serialization-json` 1.7.3, coroutines. **Pas** de SDK Drive Java ni de Google API Client : du REST pur via `HttpURLConnection` |
| **Provenance** | [JRRocks/Suivie-Popote-Cie](https://github.com/JRRocks/Suivie-Popote-Cie) (privé), commit `08c211a` (2026-10-12) ; `DriveAppData.kt` et ses tests revérifiés identiques à `6149a81` (2026-10-26), patchs Library mis à part, et inchangés jusqu'à la pointe `93e5d5b` (2026-10-27). Variante : [JRRocks/Carnet-de-recette](https://github.com/JRRocks/Carnet-de-recette) (public), `f198fd3` (2026-10-26), revue à `8dc4820` |
| **Utilisé dans** | Popote Compagnie (sauvegardes chiffrées, photos de passation) · Carnet de recette (intégré le 2026-10-26, variante « dossier visible en `drive.file` », pas encore essayé sur appareil) |
| **Fichiers d'origine** | `core/src/main/kotlin/fr/popote/core/sauvegarde/DriveAppData.kt`, `app-terrain/.../ui/Sauvegarde.kt` (lanceurs Compose), `core/.../sauvegarde/TravailSauvegarde.kt` (WorkManager), `docs/decisions.md` (section « La clé de signature debug se versionne… ») ; variante : `app/src/main/kotlin/fr/jrrocks/carnetderecette/drive/DriveCarnet.kt` (Carnet) |
| **Dernière mise à jour de la fiche** | 2026-10-27 |

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
   **publier** (portées non sensibles, donc sans revue). En mode test, l'accord pourrait
   expirer au bout de 7 jours : voir les points de vigilance.
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

> ⚠️ **Dépôt public : ne combinez jamais les points 4 et 5.** Une clé de debug versionnée
> dans un dépôt **public** est publique. Si son SHA-1 est déclaré au client OAuth, n'importe
> qui peut signer avec elle un APK du même package, que Google prendra pour l'app (avec
> `drive.file`, il verrait tous les fichiers de l'app sur le compte de qui l'installe). Dans
> un dépôt public, on ne déclare **que** le SHA-1 release, et les essais sur téléphone se
> font avec des APK signés de la clé release (relevé dans Carnet de recette, 2026-10-26).
> Second risque, même sans OAuth : n'importe qui peut signer avec cette clé un APK du même
> package qui s'installe **par-dessus** une version de debug déjà présente et hérite de ses
> données. Dans un dépôt public, une version de debug ne va donc pas sur un téléphone qui porte
> de vraies données (ToolMap, ADR-047 ; Carnet de recette versionne sa clé de debug).

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
l'écran) et `IOException` pour le transitoire (à **réessayer**). Un HTTP 401, ou un 403 de
raison `insufficientPermissions` ou `authError`, devient `AutorisationRevoquee`, à traiter
aussi comme définitif pour éviter des réessais infinis. Depuis le patch du 2026-10-26, tout
autre refus de Drive reste une `IOException`, donc réessayée (voir les points de vigilance).
Ce chemin de fond n'a jamais été vérifié sur appareil.

## Variante : un dossier visible, en portée `drive.file` (Carnet de recette)

Provenance : Carnet de recette, branche `claude/recipe-app-planner-92zsww`. Dernier changement
fonctionnel du moteur en `f198fd3` (2026-10-26) ; pointe revue `8dc4820` (2026-10-26 ; dans
`DriveCarnet.kt`, espaces insécables et un texte seulement) ; `DriveCarnet.kt` et
`DriveCarnetTest.kt` inchangés jusqu'à la pointe `797fae7` (2026-10-27). **Jamais essayée sur
appareil** : l'essai sur téléphone est repoussé « à la toute fin » (`docs/cadrage.md` l.5 et
l.402 à `797fae7`).

Quand un **autre outil** doit lire ce que l'app dépose (Claude Cowork, un script, la personne
elle-même dans son Drive), `appDataFolder` ne convient pas : il est caché à tous sauf à
l'app. La variante du Carnet :

- portée `https://www.googleapis.com/auth/drive.file` au lieu de `drive.appdata`. Elle reste
  **non sensible** : publication sans validation Google ;
- un **dossier visible**, retrouvé par son nom ou créé :
  `q = name = '<nom>' and mimeType = 'application/vnd.google-apps.folder' and trashed = false`,
  le plus ancien d'abord (`orderBy=createdTime`), pour que deux téléphones convergent ;
- `lister` par `'<id>' in parents and trashed = false`, `parents = [<id>]` à la création ;
- métadonnées construites par la bibliothèque JSON (un nom avec guillemets restait sinon un
  JSON invalide) ;
- erreurs lues dans le corps de la réponse (même patch que ci-dessous).

**Limite structurante** : avec `drive.file`, l'app ne voit **que les fichiers qu'elle a
créés**. Un fichier déposé par un autre outil dans le même dossier lui est invisible : le
chemin retour passe par autre chose (dans le Carnet, le sélecteur de fichiers d'Android).
Le dossier devrait rester retrouvable après un déplacement par l'utilisateur, puisque la
recherche par nom se fait sans parent. Ce n'est qu'un commentaire de code
(`DriveCarnet.kt` l.239-241), jamais vérifié sur appareil.

**Ce que le Carnet fait en plus.** Documenté ici, **pas reporté dans `src/`** : rien n'a
tourné sur un téléphone. `estPanneReseau`, `echecGoogle` et `Echec(reseau)` datent de
`594ffef` ; `RefusDrive`, la chaîne des causes, la détection d'`UNREGISTERED_ON_API_CONSOLE`
par le texte et `clearToken` de `f198fd3` ; les espaces insécables de `8dc4820`. Lignes citées : `DriveCarnet.kt` à `8dc4820`.

- **Refus ou panne de réseau.** `RefusDrive` (l.96-106) est l'`IOException` d'un refus :
  Drive a répondu hors 2xx. `AutorisationRevoquee` en hérite. `estPanneReseau` (l.378-391)
  dit si rien n'a répondu. Elle remonte la chaîne des causes (10 niveaux au plus) :
  `UnknownHostException`, `SocketTimeoutException`, `SocketException` (dont
  `NoRouteToHostException`, « connection reset »), `SSLException`, et `ApiException`
  `NETWORK_ERROR` enveloppée par `Tasks.await` dans une `ExecutionException`. Un
  `RefusDrive` n'est jamais une panne. Dans `src/`, les refus non liés à l'autorisation sont
  de simples `IOException`, qu'on ne distingue pas d'une coupure.
- **Échecs de Play Services en français.** `echecGoogle(code, message)` (l.393-414) remplace
  le « ApiException: 7: » brut que montre `Tasks.await` (`src/` affiche ce message brut).
  `NETWORK_ERROR` donne un message de réseau. `DEVELOPER_ERROR`, ou un texte qui contient
  `UNREGISTERED_ON_API_CONSOLE` **quel que soit le code**, renvoie au package et au SHA-1.
  `CANCELED` donne « Autorisation annulée ». Un autre code garde son numéro et le texte de
  Google. Le cas « code 8 » (commentaire l.402-403, test l.279) n'est qu'une affirmation de
  relecture : jamais observé.
- **Hors ligne.** `Autorisation.Echec(motif, reseau = true)` (l.137-138). Au lancement, un
  téléphone déjà branché garde son branchement (`MOTIF_HORS_LIGNE` ;
  `ui/AccueilViewModel.kt` l.107-120). Au premier branchement, rien n'est mémorisé et
  l'erreur dit seulement qu'il manque le réseau (`MOTIF_RESEAU`).
- **Espaces insécables** (U+00A0) devant « : » et dans les guillemets des messages
  affichés, vérifiés par un test.
- **Jeton refusé retiré du cache** (`avecJeton`, `oublierJeton`, l.213-232) : voir les points
  de vigilance.

Tests, tous en JVM : `DriveCarnetTest.kt` l.233 et l.242 (`estPanneReseau`), l.261 et l.279
(`echecGoogle`), l.302 (espaces insécables). Pour Popote, ces gains servent surtout
l'interface : `TravailSauvegarde` a déjà sa règle (`IOException` réessayée).

Code de référence : [JRRocks/Carnet-de-recette](https://github.com/JRRocks/Carnet-de-recette)
(public), `app/src/main/kotlin/fr/jrrocks/carnetderecette/drive/DriveCarnet.kt` et
`app/src/test/kotlin/fr/jrrocks/carnetderecette/drive/DriveCarnetTest.kt` (24 tests), à
`8dc4820`.

**Voir aussi** : InnovAdapt-CRM (privé), branche `claude/android-rust-mobile` @ `d2c2f6e`,
réimplémente le même besoin sans reprendre ce code : `native/drive-auth/DriveAuthPlugin.kt`
(plugin Capacitor, `AuthorizationClient` en `drive.file`) et un client Rust pour le bureau
(OAuth « application installée », PKCE et redirection loopback,
`crates/crm-core/src/sync/{oauth,drive}.rs`). Candidats de l'INDEX, sans usage réel consigné,
non copiés ici.

## Appels REST Drive v3 (dans `DriveAppData`)

| Fonction | Requête |
|---|---|
| `lister(ctx)` | `GET /drive/v3/files?spaces=appDataFolder&fields=nextPageToken,files(id,name,modifiedTime,size)&pageSize=1000`, en **suivant `nextPageToken`** (20 pages max en garde-fou) |
| `envoyer(ctx, nom, octets)` | `POST /upload/drive/v3/files?uploadType=multipart` (création, avec `parents:["appDataFolder"]`) ou `PATCH .../files/{id}?uploadType=multipart` (remplacement, **sans** `parents`) |
| `envoyerFichier(...)` | Pareil, en flux (gros fichiers, pas tout en mémoire) |
| `telecharger(ctx, id)` | `GET /drive/v3/files/{id}?alt=media`, avec progression |
| `supprimer(ctx, id)` | `DELETE /drive/v3/files/{id}` |

Délais : 15 s pour la connexion et 15 s pour la lecture.

## Composants

| Partie de `src/DriveAppData.kt` | Rôle | Dépend d'Android ? | Tests (`tests/`) | Statut |
|---|---|---|---|---|
| Autorisation (`requeteAutorisation`, `demanderAutorisation`, `autorisationDepuisIntention`, `lireResultat`, `jeton`) | Consentement, jeton, adresse du compte | Oui (Play Services Auth) | `lireEmail` seulement | ✅ sur appareil (Popote) · ⚠️ jeton révoqué gardé en cache (suspecté) |
| REST Drive (`lister`, `envoyer`, `envoyerFichier`, `telecharger`, `supprimer`, `appel`, `appelFlux`, `lireReponse`) | Liste paginée, dépôt multipart, lecture, suppression | En partie : transport en `HttpURLConnection`, mais `Context` et `jeton` | Parties pures : multipart, liste, pagination, progression | ✅ dépôt manuel et lecture sur appareil · envoi périodique jamais vérifié |
| Lecture des erreurs (`exceptionPour`, `lireErreurDrive`, `ErreurDrive`, `lireCorpsErreur`, `RAISONS_AUTORISATION`) | Seul un vrai refus du jeton devient `AutorisationRevoquee` | Non | 4 tests | ⚠️ patch proposé le 2026-10-26, non vérifié sur appareil |
| Partie Popote (`Espace`, `Archive`, nommage, rotation, `aPurger`, `aSupprimer`) | Archives de Popote | Non | Tests de nommage et de rotation | Hors moteur : à retirer |

`tests/DriveAppDataTest.kt` : 28 tests, tous sur des parties pures.

## Intégration dans une nouvelle app

1. Faites les prérequis de la console ci-dessus.
2. `implementation("com.google.android.gms:play-services-auth:21.3.0")`, la sérialisation
   Kotlin, la permission `INTERNET`.
3. Copiez `src/DriveAppData.kt`, changez le package, puis **retirez la partie propre à
   Popote** : les classes `Espace` et `Archive`, le nommage et la rotation des archives
   (`nomSocle`, `nomPoint`, `aPurger`, `aSupprimer`, `eclaircir`, `nomQuotidien`,
   `nomTrousseau`, `estTrousseau`, `nomPhoto`, `instantDansNom`, `instantDuNom` et leurs
   constantes) et l'import de `ParametresEntity`. Le **noyau générique** à garder :
   `requeteAutorisation`, `Autorisation`, `demanderAutorisation`,
   `autorisationDepuisIntention`, `lireResultat`, `jeton`, `lister`, `envoyer`,
   `envoyerFichier`, `telecharger`, `supprimer`, `lireListe`, `lireJetonPage`, `lireEmail`,
   `construire*Multipart` (avec `PartiesMultipart`), `appel`, `appelFlux`, `lireReponse`,
   `lireAvecProgression`, `FichierDrive`, `AutorisationRevoquee`, et les fonctions du patch
   403 : `exceptionPour`, `lireErreurDrive`, `ErreurDrive`, `lireCorpsErreur`,
   `RAISONS_AUTORISATION`. Sans elles, la copie ne compile pas (`lireReponse` et
   `telecharger` appellent `exceptionPour`).
4. Branchez les lanceurs Compose comme ci-dessus.
5. Pour des sauvegardes périodiques : un `CoroutineWorker` qui appelle `envoyer` et mappe
   `IllegalStateException`/`AutorisationRevoquee` vers `Result.failure()` et `IOException`
   vers `Result.retry()`. **Jamais vérifié en réel** : dans Popote, seul le dépôt manuel
   est validé sur appareil, pas le `PeriodicWorkRequest` de 24 h. Avec le patch 403, un
   refus non reconnu part aussi en `Result.retry()` (voir les points de vigilance).

## Réglages éprouvés

| Paramètre | Valeur | Pourquoi |
|---|---|---|
| Liste des fichiers (`lister`) | `pageSize=1000` (le maximum de Drive v3), en suivant `nextPageToken` | Avec 100 fichiers par page sans suivre la suite, des archives devenaient invisibles (historique, « Plus tard ») |
| Empreintes du client OAuth Android | SHA-1 debug **et** release sur le même client (dépôt privé ; dépôt public : release seule, voir l'avertissement des prérequis) | Sans la release, `UNREGISTERED_ON_API_CONSOLE` en release signée (vécu, prérequis 4) |

Les délais de 15 s et le plafond de 20 pages sont les valeurs d'origine : aucun essai réel ne
les a ajustés.

## Points de vigilance

- **⚠️ Tout 403 était lu comme une autorisation révoquée (patch proposé le 2026-10-26).**
  Drive rend aussi un 403 pour « API non activée » (`accessNotConfigured`), une limite de
  débit (`rateLimitExceeded`) ou un quota plein (`storageQuotaExceeded`). Effets : message
  faux (« reconnectez le compte ») et, dans `TravailSauvegarde`, un refus transitoire traité
  en échec définitif. Correctif dans `src/` : le corps d'erreur est lu (`exceptionPour`) et
  seuls un 401 ou un 403 `insufficientPermissions`/`authError` donnent
  `AutorisationRevoquee`. Couvert par `DriveAppDataTest`. Relevé par une relecture du Carnet
  de recette ; **Popote porte encore le défaut** (vérifié à `6149a81` : `DriveAppData.kt`
  l.484 et l.807, `if (code == 401 || code == 403)`).
- **Le patch 403 change aussi le comportement (à surveiller, non constaté).** Tout 403 hors
  `insufficientPermissions`/`authError` devient une `IOException` transitoire, y compris un
  403 sans corps ou `appNotAuthorizedToFile`. Un Worker à la `TravailSauvegarde`
  (`IOException` → `Result.retry()`) le réessaiera alors sans fin au lieu d'échouer.
- **⚠️ Jeton révoqué gardé en cache (suspecté, relevé le 2026-10-26).** Si l'utilisateur
  retire l'accès depuis son compte Google, Play Services rend encore le jeton en cache :
  `authorize()` ne présente pas l'écran de consentement, Drive répond 401, et « reconnecter »
  boucle jusqu'à l'expiration du jeton (jusqu'à une heure). La documentation Android
  (« Clear the token cache ») demande de vider ce cache. **Patch proposé** dans la variante du
  Carnet : `DriveCarnet.avecJeton` appelle `GoogleAuthUtil.clearToken(contexte, jeton)` (déjà
  présent avec `play-services-auth` 21.3.0) sur tout `AutorisationRevoquee`, y compris au
  point `userinfo`. **Non reporté dans `src/`** : il faut restructurer cinq fonctions, et la
  Library ne se compile pas ici. Non vérifié sur appareil.
  Le même défaut suspecté touche Popote (aucun `clearToken` ni `GoogleAuthUtil` à `6149a81`)
  et les réimplémentations d'InnovAdapt-CRM (`DriveAuthPlugin.kt` sans `clearToken` ; jeton
  réutilisé jusqu'à son expiration dans `crates/crm-core/src/sync/oauth.rs` l.282-284 et
  `pwa/bridge.mjs` l.52). Le correctif n'existe que dans le Carnet : Popote est à prévenir
  d'un **défaut suspecté**, avec renvoi au Carnet, pas d'un patch Library à reporter.
- **Seul le dépôt manuel est éprouvé.** Dans Popote, « Sauvegarder maintenant » est validé
  sur appareil depuis le 01/09. L'envoi périodique (`PeriodicWorkRequest` de 24 h, popotier
  inactif) n'a jamais été vérifié en réel (Popote `docs/todo.md` l.66-83 à `6149a81`). Le
  dépôt Popote se contredit d'ailleurs : `docs/debogage-sqlcipher.md` l.233-235 dit encore
  le sens sauvegarde « à éprouver ».
- **Écran de consentement en mode « Test » (à vérifier).** Selon la documentation Google, les
  refresh tokens d'un client OAuth y expirent au bout de 7 jours (relevé par InnovAdapt-CRM,
  `GUIDE-GOOGLE-CLOUD.md` l.29 ; le Carnet parle d'un « accord à redonner chaque semaine »,
  `DriveCarnet.kt` l.116-118). Avec `AuthorizationClient`, l'app ne détient pas de refresh
  token : l'effet réel n'est pas démontré. Le plus sûr reste de publier l'écran (portées non
  sensibles, sans revue).
- **Ne jamais traiter un 404 comme un succès en écriture.** Les deux clients Drive
  d'InnovAdapt-CRM le font (`crates/crm-core/src/sync/drive.rs` l.65 et l.172-187,
  `adapters/google-drive.mjs` l.25) : surtout côté navigateur, où l'identifiant du dossier
  reste en cache, un dossier supprimé fait perdre les envois en silence.
  `DriveAppData` n'a pas ce défaut (`lireReponse` l.847 et `telecharger` l.494 lèvent hors
  2xx) : à garder ainsi.
- **Durée de vie du jeton inconnue.** `AuthorizationResult` ne la donne pas. InnovAdapt-CRM,
  qui garde le jeton en cache, la fixe à 50 min par prudence (`DriveAuthPlugin.kt` l.54-55).
  `DriveAppData` n'a pas de cache propre : il redemande le jeton à `authorize()` à chaque
  opération.
- **Pas d'épinglage TLS** (facultatif). L'appel Drive repose sur le magasin d'autorités du
  système. Constat de pentest dans Popote, décision non tranchée (`docs/todo.md` l.422-441 à
  `6149a81`) :
  un épinglage mal entretenu casserait la sauvegarde du jour au lendemain.

- **Paginer, toujours.** La première version demandait 100 fichiers sans suivre
  `nextPageToken`. Au-delà, des fichiers étaient **silencieusement invisibles** et une clé
  présente se lisait comme absente.
- **Ne pas rejouer `parents` sur un PATCH** : Drive le refuse.
- Le **chiffrement** des données envoyées n'est pas dans ce moteur. Dans Popote, il est fait
  en amont (trousseau asymétrique, `Trousseau.kt`, `Enveloppe.kt`) : candidat séparé, voir
  l'INDEX.
- Une donnée nominative qui quitte le téléphone doit être annoncée aux personnes concernées.
- Rien de tout cela ne se teste en JVM (pas de compte, pas de Play Services) : seules les
  fonctions pures sont testées. Le 2026-10-27, les 28 tests ont tourné hors Gradle, avec des
  bouchons des classes Android et Play Services pour compiler `src/` (et kotlinx-serialization
  1.6.2 au lieu de 1.7.3) : tous verts.

## Historique des retours d'usage

| Date | Source | Retour | Effet sur la fiche |
|---|---|---|---|
| Phase 8 | Popote | Connexion qui casse d'un conteneur à l'autre | Clé debug versionnée (prérequis 5) |
| Première release signée | Popote, appareil réel | `UNREGISTERED_ON_API_CONSOLE` | Second SHA-1 sur le même client (prérequis 4) |
| Plus tard | Popote | Archives invisibles au-delà de 100 fichiers | Pagination complète |
| 2026-10-12 | Catalogage Library | En production | Statut ✅ |
| 2026-10-26 | Carnet de recette, seconde relecture | Défaut suspecté : jeton révoqué gardé en cache par Play Services | Point de vigilance ⚠️, patch proposé côté Carnet, non reporté dans `src/` |
| 2026-10-26 | Carnet de recette, intégration et relecture | Variante `drive.file` et dossier visible. Défaut : tout 403 lu comme une révocation. Faille si clé debug publique déclarée au client OAuth | Section « Variante » ; patch des 403 dans `src/` et quatre tests (composant ⚠️ jusqu'à un essai sur appareil) ; avertissement « dépôt public » dans les prérequis ; Popote à prévenir pour les 403 |
| 2026-10-27 | Revue Popote `6149a81` | `DriveAppData.kt` et ses tests identiques à `src/` et `tests/`, patchs Library mis à part (24 tests contre 28), et inchangés jusqu'à la pointe `93e5d5b`. Aucun des patchs du 2026-10-26 dans Popote. Pas de `clearToken` non plus : le défaut suspecté du jeton y est aussi. Seul le dépôt manuel est validé sur appareil (`docs/todo.md` l.66-83), jamais l'envoi périodique. Pas d'épinglage TLS (`docs/todo.md` l.422-441) | Statut inchangé (✅ · ⚠️ · ⚠️). Provenance revérifiée ; vigilances « dépôt manuel seul » et « TLS » ; réserve sur l'étape 5 (Worker). Popote à prévenir : 403 (patch Library) et jeton (défaut suspecté, correctif dans le Carnet) |
| 2026-10-27 | Contre-vérification des commits Library `fa90649` et `9391ed9` contre le Carnet à `8dc4820` | Fidèles : même logique pour les 403 (messages et type `RefusDrive` adaptés), 4 tests correspondants, `avecJeton` couvre bien les cinq fonctions et `userinfo`. Moteur du Carnet inchangé jusqu'à la pointe `797fae7`. Effet de bord du patch 403 relevé : un 403 non reconnu devient transitoire. L'étape 3 d'« Intégration » oubliait les fonctions du patch | Provenance de la variante (`f198fd3`, `8dc4820`) ; étape 3 complétée ; vigilance « le patch 403 change le comportement » (non constaté) |
| 2026-10-27 | Carnet de recette `8dc4820`, compléments de la variante | `RefusDrive` et `estPanneReseau`, `echecGoogle`, `Autorisation.Echec(reseau)`, espaces insécables : 4 tests (l.233, 242, 261, 279) et 1 test NBSP (l.302), JVM seulement. « Code 8 » jamais observé. « Le déplacement du dossier ne retire pas l'accès » n'est qu'un commentaire de code | Section « Variante » complétée, rien reporté dans `src/` (aucun retour réel) ; phrase sur le déplacement nuancée |
| 2026-10-27 | Revue InnovAdapt-CRM `d2c2f6e` (branche `claude/android-rust-mobile`) | Réimplémentations indépendantes, aucun code repris. Pièges communs : jeton gardé en cache, 404 traité en succès d'écriture (absent ici), mode « Test » à 7 jours (doc Google, non observé), pas de durée de vie dans `AuthorizationResult` | « Voir aussi » dans la Variante ; trois vigilances ajoutées (mode « Test », 404, durée de vie du jeton), défaut du jeton complété ; statut inchangé |
| 2026-10-27 | Catalogage Library | `tests/DriveAppDataTest.kt` portait une adresse en `gmail.com` (domaine réel) | Remplacée par une adresse en `example.org` : écart volontaire avec Popote, qui garde la sienne. 28/28 tests verts hors Gradle (Kotlin 2.0.21, JUnit 4.13.2), compilés contre des bouchons Android ; aucun changement de `src/`. Tableau « Composants » et section « Réglages éprouvés » ajoutés (gabarit) |
