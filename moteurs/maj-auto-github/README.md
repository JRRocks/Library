# Mise à jour automatique d'un APK depuis GitHub Releases (hors Play Store)

| Champ | Valeur |
|---|---|
| **Identifiant** | `maj-auto-github` |
| **Statut** | ✅ **Éprouvé.** En production depuis la v13.22 de Popote Compagnie : des dizaines de versions publiées (balises `v13.22` à `v13.125` sur le dépôt public), reçues par les téléphones des clients sans câble ni Play Store. Parties pures couvertes par `MiseAJourTest`. ⚠️ `publicationDuLancement` : annulation avalée, **patch proposé le 2026-10-26** (test JVM qui échoue avant et passe après), non vérifié sur appareil, pas encore reporté dans Popote (voir les points de vigilance) |
| **Plateforme** | Android natif, Kotlin, minSdk 26, et GitHub Actions |
| **Dépendances** | `kotlinx-serialization-json` (accès `JsonObject`, sans plugin), `androidx.core` (`FileProvider`, `PackageInfoCompat`), coroutines |
| **Provenance** | [JRRocks/Suivie-Popote-Cie](https://github.com/JRRocks/Suivie-Popote-Cie) (privé), commit `08c211a` (2026-10-12). Dépôt public des versions : [JRRocks/Popote-Compagnie](https://github.com/JRRocks/Popote-Compagnie) |
| **Utilisé dans** | Popote Compagnie · Carnet de recette (intégré le 2026-10-26, variante « dépôt de code public », aucune version publiée pour l'instant) |
| **Fichiers d'origine** | `app-terrain/src/main/kotlin/fr/popote/terrain/maj/MiseAJour.kt`, `ui/MiseAJour.kt` (boîte de dialogue), `.github/workflows/version.yml`, `docs/publication.md` |
| **Dernière mise à jour de la fiche** | 2026-10-26 |

## À quoi ça sert

Une app distribuée **hors Play Store** (usage interne, petite structure) se met à jour toute
seule. À chaque lancement :

1. elle interroge **anonymement** `https://api.github.com/repos/<owner>/<repo>/releases/latest` ;
2. elle compare le `versionCode` de l'APK publié à celui qui est installé ;
3. s'il est **strictement** supérieur, elle propose la mise à jour, télécharge l'APK dans
   son stockage privé et le passe à l'installateur système.

## Architecture : deux dépôts

- **Dépôt privé** : le code (et éventuellement des données sensibles).
- **Dépôt public « versions »** : **uniquement des APK compilés**. L'app le lit **sans
  jeton**. Un jeton embarqué dans un APK distribué n'est pas un secret, et il n'existe pas
  de portée GitHub « lire seulement les releases » d'un dépôt privé.
- La CI publie dans le dépôt public avec un **PAT fine-grained** limité à `contents: write`
  sur ce seul dépôt. Même s'il fuit, il n'ouvre pas le code.

## Variante : le dépôt de code est déjà public (Carnet de recette)

Quand le code n'a rien de privé, le second dépôt n'a plus de raison d'être :

- les APK sont publiés dans les **Releases du dépôt de code** lui-même ;
- `GITHUB_TOKEN` suffit (`permissions: contents: write`) : **aucun jeton personnel** ;
- les aperçus sont des **pré-versions** du même dépôt (`apercu-<versionName>`), que
  `/releases/latest` ignore : l'app ne les propose jamais. Ils sont produits par un workflow
  séparé, `apercu.yml`, à chaque push qui touche l'app sur une branche de travail ;
  `version.yml` ne sert plus qu'à publier ;
- un aperçu est **toujours signé de la clé release**. Dans un dépôt public, la clé debug
  versionnée est publique : un APK de debug ne doit pas pouvoir se connecter à un service
  qui reconnaît l'app par sa signature (Google, voir la fiche `google-drive-appdata`). Sans
  les secrets, `apercu.yml` lance les tests et ne publie rien.

Code de référence : [JRRocks/Carnet-de-recette](https://github.com/JRRocks/Carnet-de-recette)
(public), `.github/workflows/apercu.yml` et `version.yml`, `docs/publication.md`.

## Conventions qui font marcher le tout

| Règle | Pourquoi |
|---|---|
| L'APK publié s'appelle **`<prefixe>-<versionName>-<versionCode>.apk`**, nom fabriqué par la CI à partir de `build.gradle.kts` | L'API GitHub ne connaît pas le `versionCode`. Le nom de balise et le `versionName` (ex. `13.20-sumup-location`) ne s'ordonnent pas |
| Un APK sans ce suffixe est **ignoré** | Mieux vaut ne rien proposer qu'une version qu'on ne sait pas dater |
| Comparaison **strictement** supérieure | Ne jamais faire régresser un téléphone en service |
| L'APK doit être **signé de la même clé** que l'installé ; la CI **échoue** sur un APK `-unsigned` | Sinon Android refuse le remplacement et il faudrait désinstaller, donc perdre les données |
| Une seule requête **par processus**, silencieuse en cas d'échec | Pas de message d'erreur pour une mise à jour non demandée ; « Plus tard » tient jusqu'au prochain lancement |
| Téléchargement en `.part`, renommé à la fin | Un fichier interrompu n'est jamais pris pour un APK complet |
| En-tête `User-Agent` **obligatoire** | GitHub répond 403 aux requêtes sans `User-Agent`, même anonymes |
| 404 sur `/releases/latest` | Normal tant que rien n'a été publié, ce n'est pas une panne |

## Intégration dans une nouvelle app

1. **Manifeste** :
   ```xml
   <uses-permission android:name="android.permission.INTERNET"/>
   <uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES"/>
   <provider android:name="androidx.core.content.FileProvider"
             android:authorities="${applicationId}.fileprovider"
             android:exported="false" android:grantUriPermissions="true">
       <meta-data android:name="android.support.FILE_PROVIDER_PATHS" android:resource="@xml/file_paths"/>
   </provider>
   ```
   et `res/xml/file_paths.xml` : `<files-path name="maj" path="maj/" />`.
2. Copiez `src/MiseAJour.kt`, changez le package, puis adaptez `DEPOT`, la regex `NOM_APK`
   (préfixe) et le `User-Agent`.
3. **Au lancement** (ex. `LaunchedEffect(Unit)` de l'écran d'accueil) :
   ```kotlin
   val publication = MiseAJour.publicationDuLancement(ctx)   // null = rien à proposer
   // boîte de dialogue : versionNom, notes, taille → « Mettre à jour » / « Plus tard »
   if (!MiseAJour.peutInstaller(ctx)) ctx.startActivity(MiseAJour.ecranSourcesInconnues(ctx))
   val apk = MiseAJour.telecharger(ctx, publication) { fraction -> progression = fraction }
   MiseAJour.installer(ctx, apk)
   ```
   Depuis Android 8, « sources inconnues » est une autorisation **par app**, qu'on accorde
   seulement depuis l'écran de réglages ouvert par `ecranSourcesInconnues`.
4. **CI** : adaptez `src/version.yml`. Il lit `versionName`/`versionCode` dans
   `build.gradle.kts`, restaure la clé release depuis les secrets
   (`SIGNATURE_KEYSTORE_BASE64`, `SIGNATURE_STORE_PASSWORD`, `SIGNATURE_KEY_ALIAS`,
   `SIGNATURE_KEY_PASSWORD`), lance les tests, compile, renomme l'APK, pose la balise
   `v<versionName>` et publie sur le dépôt public avec le secret `JETON_DEPOT_VERSIONS`.
   Pour finir, il efface la clé du runner (`if: always()`). Le secret `JETON_SIGNALEMENT`
   est propre à Popote : retirez-le.
5. **Mode aperçu** (fortement conseillé si l'app est en production) : `workflow_dispatch`
   avec `apercu: true` produit le **même APK signé** en pré-version `apercu-<versionName>`
   du dépôt **privé**, sans rien publier aux clients. On le teste, puis on publie sur feu
   vert explicite.

## API exposée (`MiseAJour`)

| Membre | Rôle |
|---|---|
| `publicationDuLancement(ctx): Publication?` | Interroge une fois par processus et rend la version à proposer, ou `null` |
| `versionInstallee(ctx): Long` | `versionCode` en cours |
| `telecharger(ctx, publication, onProgression)` | Télécharge en `.part` avec progression par paliers et rend le `File` |
| `peutInstaller(ctx)` / `ecranSourcesInconnues(ctx)` | Autorisation d'installer |
| `installer(ctx, fichier)` | `ACTION_VIEW` sur une `content://` du FileProvider |
| `lirePublication(json)`, `aProposer(pub, installee)`, `versionCodeDuNom(nom)` (`internal`) | Parties pures, testées |

## Points de vigilance

- **⚠️ Annulation avalée par `publicationDuLancement` (patch proposé le 2026-10-26).**
  Symptôme : si l'écran qui interroge est quitté ou recréé (rotation, thème) pendant la
  requête GitHub, plus aucune mise à jour n'est proposée jusqu'à la mort du processus. Cause :
  `runCatching` attrape aussi la `CancellationException`, et `interrogee` était posé avant la
  requête. Correctif dans `src/` : `try`/`catch` qui relance l'annulation, `interrogee` posé
  après la réponse. Couvert par `MiseAJourTest` (le test échoue sur l'ancien code). Relevé
  par une relecture du Carnet de recette ; **Popote porte encore le défaut**.
- Dans Popote, la boîte de dialogue garde aussi son état dans des `remember` : après « Plus
  tard », une rotation la fait réapparaître. Le Carnet met cet état dans un ViewModel
  (`AccueilViewModel`), ce qui règle aussi les téléchargements perdus à la rotation.

- **Rotation de la clé release = rupture de la chaîne** : les téléphones existants ne
  pourront plus installer. Gardez la clé (et sa sauvegarde) comme un bien précieux.
- Le dépôt de versions étant public, n'y mettez **aucune donnée** (seed vide, aucun nom).
- Pousser une balise depuis une session cloud Claude peut être bloqué par la politique
  réseau (HTTP 403). Faites poser la balise par le workflow lui-même, avec le jeton de l'Action.

## Historique des retours d'usage

| Date | Source | Retour | Effet sur la fiche |
|---|---|---|---|
| v13.22 | Popote | Mise en service (motivée par les correctifs SumUp à livrer sans câble) | — |
| v13.22 → v13.125 | Popote, production | Chaîne utilisée à chaque version | Statut ✅ |
| 2026-10-12 | Catalogage Library | — | Entrée en bibliothèque |
| 2026-10-26 | Carnet de recette, intégration et relecture | Défaut : annulation avalée par `publicationDuLancement`. Variante « dépôt de code public » mise en place | Patch dans `src/` et deux tests ; composant ⚠️ jusqu'à un essai sur appareil ; section « Variante » ; Popote à prévenir |
