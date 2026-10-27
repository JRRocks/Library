# Compiler, tester et livrer une app Android depuis une session cloud

| Champ | Valeur |
|---|---|
| **Identifiant** | `android-en-session-cloud` (fiche méthode : pas de `src/`) |
| **Statut** | ✅ **compiler et tester en session** (SDK installé dans le conteneur) : consigné dans quatre dépôts, rejoué le 2026-10-27. ✅ **APK de CI signé par une clé de debug fixe, installé sur un vrai téléphone** (ToolMap, 2026-10-03). 🟡 dépôt de l'APK dans `release/` et pré-version `debug-<sha7>` : APK bien produits et déposés, installation depuis ces emplacements non consignée. Détail dans « Composants » |
| **Plateforme** | Session claude.ai/code (Linux, sans KVM, sortie HTTPS par un proxy) ; CI GitHub Actions `ubuntu-latest` |
| **Dépendances** | `commandlinetools-linux` (Google), `bash`, `curl`, `unzip`, JDK 17 ou 21, Gradle du projet |
| **Provenance** | ToolMap (privé), branche `sauvegarde/p16-2026-10-16` @ `eee8c6b` · SuivieMAT (privé), branche `claude/remove-scanlist-history-a5bqzh` @ `f8f3eaf` · Suivie-Popote-Cie (privé) @ `6149a81` · [Carnet-de-recette](https://github.com/JRRocks/Carnet-de-recette) (public) @ `8dc4820` · Activity-track (privé) @ `855773c` · Android-Suntrack (privé) @ `e5c94b4` · InnovAdapt-CRM (privé), branche `claude/android-rust-mobile` @ `d2c2f6e` |
| **Utilisé dans** | ToolMap (hook SessionStart), SuivieMAT, Popote Compagnie, Carnet de recette ; tests Rust, Node et Python lancés en session lors des revues d'InnovAdapt et d'Activity-track |
| **Fichiers d'origine** | ToolMap : `.claude/hooks/session-start.sh`, `.claude/settings.json`, `scripts/install-android-sdk.sh`, `scripts/gradle-init/cloud-mirror.init.gradle`, `docs/DECISIONS.md` (ADR-009, 045, 046, 047), `.github/workflows/ci.yml`, `docs/specs/release.md` § 1. SuivieMAT : `consignes/outils.md` § 6 bis, 6 ter et 8, `.github/workflows/{build-debug-apk,nettoyage-actions}.yml`, `app/build.gradle.kts` (`apk.phoneOnly`), `release/LISEZMOI.md`. Carnet : `outils/sdk-android.sh`, `CLAUDE.md` § « Compiler et tester », `.github/workflows/{version,apercu}.yml`, `docs/publication.md`. Popote : `docs/decisions.md` (section v131), `CLAUDE.md` |
| **Dernière mise à jour de la fiche** | 2026-10-27 |

## À quoi ça sert

Travailler sur une app Android depuis un téléphone ou un navigateur, sans poste : compiler,
lancer les tests, produire un APK et le faire arriver sur le téléphone. Une session cloud n'a
ni SDK Android ni émulateur (pas de `/dev/kvm`). Les CLAUDE.md de ScanList et SuivieMAT disent
encore qu'un build y est « généralement pas exécutable » : c'est dépassé, SuivieMAT compile
lui-même en session depuis le 2026-09-24 (`outils.md` § 6 ter), avec le SDK installé comme
ci-dessous. Pour voir un écran sans émulateur, voir
[`captures-compose-robolectric.md`](captures-compose-robolectric.md).

## Composants

| Composant | Statut | Preuve |
|---|---|---|
| SDK dans le conteneur, tests et `assembleDebug` en session | ✅ | ToolMap ADR-009 (depuis le 2026-10-01) ; SuivieMAT `outils.md` § 6 ter (vérifié le 2026-09-24) et § 8 (APK 1.1 à 1.5 des 26 et 27/10, puis 1.6 à `5eb5198`) ; Popote v131 (suite complète en 716 s) ; Carnet `594ffef`, `f198fd3` ; rejoué le 2026-10-27 (historique) |
| Miroir Maven Central contre les 429 | ✅ | ToolMap, SuivieMAT ; 429 encore constaté le 2026-10-27 sur `repo.maven.apache.org`, miroir joignable |
| APK de CI, clé de debug fixe, installé sur téléphone | ✅ | ToolMap `docs/JOURNAL.md`, session 3 suite 19 (2026-10-03) : « l'utilisateur a installé l'APK de `fa96a1a` », rapport du téléphone reçu |
| Dépôt de l'APK dans `release/` de la branche | 🟡 | SuivieMAT : APK déposés par la CI depuis le 2026-09-25, puis par la session (1.1 à 1.5) ; chaque entrée du journal dit « non vérifié sur téléphone » |
| Pré-version GitHub `debug-<sha7>` | 🟡 | ToolMap : APK livré par la pré-version quand le quota a refusé l'artefact (run 38274900901, `docs/JOURNAL.md` l. 1038) ; installation depuis la pré-version non consignée |
| Tests Kotlin purs sans Gradle | 🟡 | Outil de revue (2026-10-27) : 101 tests purs de SuivieMAT, 13 de `astro-soleil-lune` |

## 1. Installer le SDK

Trois façons, du plus simple au plus durable. Toutes écrivent `local.properties`
(`sdk.dir=…`, ignoré par git) et gardent les versions alignées sur `compileSdk`.

- **À la main, dans le scratchpad** (SuivieMAT `outils.md` § 6 ter). Rien n'entre dans le
  dépôt ; tout disparaît avec la session.

  ```bash
  SDK=<scratchpad>/sdk; mkdir -p $SDK/cmdline-tools && cd <scratchpad>
  curl -fsSL -o outils.zip https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip
  unzip -q outils.zip -d $SDK/cmdline-tools && mv $SDK/cmdline-tools/cmdline-tools $SDK/cmdline-tools/latest
  { yes || true; } | $SDK/cmdline-tools/latest/bin/sdkmanager --sdk_root=$SDK --licenses >/dev/null
  $SDK/cmdline-tools/latest/bin/sdkmanager --sdk_root=$SDK "platforms;android-35" "build-tools;35.0.0"
  export ANDROID_HOME=$SDK; echo "sdk.dir=$SDK" > <projet>/local.properties
  ```

  Ce bloc a été rejoué tel quel le 2026-10-27 (20 s, plateforme 35 et build-tools 35.0.0).

- **Par un script versionné et idempotent** (Carnet `outils/sdk-android.sh`). Sous
  `set -o pipefail`, écrire `{ yes || true; } | sdkmanager …` : `yes` meurt de SIGPIPE (141)
  quand `sdkmanager` a fini, ce qui arrêterait le script juste après l'installation. Le même
  script précharge le pot Robolectric (§ 2).
- **Par un hook SessionStart** (ToolMap, ADR-009). `.claude/settings.json` déclare
  `.claude/hooks/session-start.sh`, qui ne fait rien hors cloud (`CLAUDE_CODE_REMOTE`
  différent de `true`), copie le script d'init du miroir dans `~/.gradle/init.d/`, appelle
  `scripts/install-android-sdk.sh` (aussi utilisé par la CI), écrit `local.properties`,
  exporte `ANDROID_HOME` par `$CLAUDE_ENV_FILE` et chauffe le cache (`./gradlew --version`).
  Sans le hook : le lancer à la main avec `CLAUDE_CODE_REMOTE=true`.

Versions vues : plateforme 35 et build-tools 35.0.0 (Popote, Carnet), 36 et 35.0.0
(SuivieMAT), 37 et 36.0.0 (ToolMap, compileSdk 37). AGP installe lui-même `platform-tools`
s'il manque, une fois les licences acceptées (constaté le 2026-10-27).

## 2. Maven Central répond 429

Depuis la sortie partagée des sessions, `repo.maven.apache.org` répond « 429 Too Many
Requests » aux rafales, et Gradle désactive alors le dépôt : le build échoue (ToolMap ADR-009).
Les réessais de Gradle ne suffisent pas. Parade : un script d'init **hors du dépôt**, qui
pointe Maven Central vers le miroir de Google. Les fichiers du projet et la CI n'en dépendent
pas. Variante ToolMap (`scripts/gradle-init/cloud-mirror.init.gradle`, commentaires retirés),
à poser dans `~/.gradle/init.d/` ou à passer par `--init-script` :

```groovy
def mirror = 'https://maven-central.storage-download.googleapis.com/maven2'
def redirect = { RepositoryHandler repos ->
    repos.configureEach { repo ->
        if (repo instanceof MavenArtifactRepository && repo.url.toString().startsWith('https://repo.maven.apache.org/maven2')) {
            repo.url = mirror
        }
    }
}
beforeSettings { settings ->
    redirect(settings.pluginManagement.repositories)
    redirect(settings.dependencyResolutionManagement.repositories)
}
```

Variante SuivieMAT : ajouter le miroir en tête avec `content { excludeGroupByRegex("androidx.*")
; excludeGroupByRegex("com\\.android.*") }`, ces groupes restant sur `google()`.

- Premier build en `--max-workers=1` : moins de téléchargements en parallèle (Carnet `CLAUDE.md`).
- Le miroir peut avoir quelques heures de retard sur une version toute neuve (ADR-009).
- **Robolectric télécharge son pot `android-all-instrumented` lui-même**, hors de Gradle : le
  miroir ne le couvre pas. Le précharger dans `~/.m2/repository` avec des pauses entre les
  essais (Carnet), ou le déposer dans un dossier et lancer les tests avec
  `robolectric.offline=true` et `robolectric.dependency.dir=<dossier>` (2026-10-27).

## 3. Ordres de grandeur

| Mesure | Durée | Source |
|---|---|---|
| Popote v131, suite complète (`:core` 903 tests, `:app-terrain` 305) | 716 s | `docs/decisions.md` l. 3795 |
| ToolMap, build à froid | ≈ 2 min | ADR-009 |
| SuivieMAT, premier passage puis `clean testDebugUnitTest assembleDebug` | quelques minutes, puis moins de 2 min | `outils.md` § 6 ter |
| SDK (plateforme 35, build-tools 35.0.0) installé dans le scratchpad | 22 s | 2026-10-27 |
| Projet Compose minimal, premier `testDebugUnitTest` sous Robolectric (dépendances par le miroir), puis relance | 3 min 54 s à froid, puis 34 s | 2026-10-27 |
| `cargo test -p crm-core --locked` (79 tests), à froid | 1 min 39 à 1 min 57 | revue InnovAdapt, 2026-10-27 |
| `node --test tests/*.test.mjs` (54 tests) | 1,1 à 1,4 s | revue InnovAdapt |
| `pytest` (86 réussis, 5 sautés) | ≈ 6 s | revue Activity-track |

Une commande de plus de 2 minutes se lance en arrière-plan, sortie dans un fichier de log
(ToolMap `CLAUDE.md`).

## 4. Tests hors Android, sans Gradle

- **Kotlin pur** : la distribution Gradle installée dans le conteneur (`/opt/gradle-<version>/lib`)
  contient `kotlin-compiler-embeddable`, `kotlin-stdlib`, `junit-4.13.2` et `hamcrest-core`.
  Le compilateur demande aussi, du même dossier, `kotlin-reflect`, `kotlin-script-runtime`,
  `kotlin-daemon-embeddable`, `trove4j`, `annotations` et `kotlinx-coroutines-core-jvm`.
  Compiler avec `java -cp <jars du compilateur> org.jetbrains.kotlin.cli.jvm.K2JVMCompiler
  -no-stdlib -no-reflect -cp <stdlib:junit:hamcrest> -d <sortie> <sources et tests>`, puis
  lancer `java -cp <stdlib:junit:hamcrest:sortie> org.junit.runner.JUnitCore <Classe>`.
  Environ 10 à 15 s. Seulement pour du code sans `android.*` ni bibliothèque absente du
  dossier `lib`.
  Le script utilisé pour vérifier la Library le 2026-10-27, cité dans les historiques sous le
  nom `ktest.sh` (vider `JAVA_TOOL_OPTIONS` évite le bruit des options du proxy) :

  ```bash
  #!/usr/bin/env bash
  # Usage : ktest.sh <sortie> <Classe.De.Test[,Autre]> fichier1.kt fichier2.kt ...
  set -euo pipefail
  L=/opt/gradle-8.14.3/lib            # adapter à la version installée
  OUT=$1; shift; CLASSES=$1; shift
  CP="$L/kotlin-stdlib-2.0.21.jar:$L/junit-4.13.2.jar:$L/hamcrest-core-1.3.jar"
  CCP="$L/kotlin-compiler-embeddable-2.0.21.jar:$L/kotlin-stdlib-2.0.21.jar:$L/kotlin-script-runtime-2.0.21.jar:$L/kotlin-reflect-2.0.21.jar:$L/kotlin-daemon-embeddable-2.0.21.jar:$(ls $L/trove4j*.jar | head -1):$L/annotations-24.0.1.jar:$(ls $L/kotlinx-coroutines-core-jvm*.jar | head -1)"
  rm -rf "$OUT"; mkdir -p "$OUT"
  JAVA_TOOL_OPTIONS= java -cp "$CCP" org.jetbrains.kotlin.cli.jvm.K2JVMCompiler -no-stdlib -no-reflect -cp "$CP" -d "$OUT" "$@"
  JAVA_TOOL_OPTIONS= java -cp "$CP:$OUT" org.junit.runner.JUnitCore ${CLASSES//,/ }
  ```

  Un test qui importe `kotlin.test` demande en plus `kotlin-test` et `kotlin-test-junit` (absents
  du dossier `lib`, à télécharger depuis Maven Central à la même version). Un fichier qui importe
  quelques classes Android peut être compilé contre de petits bouchons Java qui ne font que lever
  une exception, pour exécuter ses fonctions pures : c'est ainsi qu'ont tourné les tests de
  `google-drive-appdata` et de `maj-auto-github` (avec `kotlinx-serialization` 1.6.2 du même
  dossier, plus ancien que celui des apps), résultat valable pour les fonctions pures seulement.
- **Rust** : `CARGO_TARGET_DIR=<scratchpad> cargo test -p <crate> --locked`, crates
  téléchargées par le proxy. Une interface Slint demande en plus des bibliothèques système
  (fontconfig, xkbcommon, wayland, x11) : non lancée en session.
- **Node** : `node --test`. Un HTML d'un seul fichier se teste en extrayant ses fonctions de
  premier niveau dans un `vm` (InnovAdapt `tests/helpers.mjs`).
- **Python** : venv dans le scratchpad, versions épinglées, `python -m pytest -p
  no:cacheprovider`, puis supprimer les `__pycache__` de la copie de travail.

## 5. Livrer l'APK au téléphone

Un APK de debug dépasse vite les 30 Mio qu'une session peut envoyer en pièce jointe (SuivieMAT :
55 Mio, 38 Mio en arm64 seul). Popote donne un lien plutôt que le fichier (`CLAUDE.md`).

- **Dans `release/` de la branche** (SuivieMAT) : l'APK et un `.txt` (commit source, date,
  SHA-256 du fichier). Il se télécharge depuis la page GitHub du fichier, sur le téléphone. En
  CI : `permissions: contents: write`, `paths-ignore: release/**` ; un push fait avec
  `GITHUB_TOKEN` ne relance pas de run. Chaque APK reste dans l'historique git, d'où
  `-Papk.phoneOnly` (`abiFilters` arm64 seul) ; un tel APK ne s'installe pas sur un émulateur x86_64.
- **Pré-version GitHub `debug-<sha7>`** (ToolMap `ci.yml`, ADR-045 et 046) : les fichiers de
  release ne comptent pas dans le quota d'artefacts. Seule la dernière est gardée ; l'envoi en
  artefact devient non bloquant, et une étape finale échoue seulement si ni l'artefact ni la
  pré-version ne portent d'APK.
- **Aperçu signé de la clé release** (Popote, Carnet) : voir la fiche
  [`maj-auto-github`](../../moteurs/maj-auto-github/README.md).
- **Une seule clé de debug** pour le poste, la CI et les sessions : chaque APK de test
  s'installe par-dessus le précédent sans perdre les données. Sans elle, chaque runner signe
  avec une clé jetable et chaque mise à jour impose une désinstallation (SuivieMAT § 6 bis,
  ToolMap ADR-009). Changer de clé impose une désinstallation unique. Cette clé ne signe jamais
  un APK distribué, **ne se copie jamais dans la Library**, et son empreinte ne se déclare
  jamais à un client OAuth quand le dépôt est public (fiche
  [`google-drive-appdata`](../../moteurs/google-drive-appdata/README.md)). **Dépôt public** :
  une clé de debug versionnée devient publique, et n'importe qui peut signer un APK du même
  paquet qui s'installe par-dessus une version signée par elle, avec accès à ses données
  (ToolMap ADR-047). D'où le choix de ToolMap : l'APK de CI est signé par la clé de
  publication dès que ses secrets existent, la clé de debug restant le repli.

## 6. Pièges de CI GitHub Actions

| Piège | Symptôme | Remède | Source |
|---|---|---|---|
| `android-actions/setup-android@v3` | « Failed to find package 'tools' », sortie 1 avant toute étape Gradle | Retirer l'action : le runner a déjà un SDK (`$ANDROID_SDK_ROOT/cmdline-tools/*/bin/sdkmanager`), ou l'installer par son propre script | ScanList et SuivieMAT `d90e340` (2026-09-15) ; Android-Suntrack, runs 38064480807, 38075724619, 38075805926 (octobre) |
| Quota d'artefacts | 0,5 Go pour **tout le compte**, compté en Go × heures, recalculé toutes les 6 à 12 h : « Artifact storage quota has been hit » | Rétention de 1 à 3 jours, ne pas doubler une release par un artefact, purge à chaque push, pré-version | ToolMap `release.md` § 1 ; SuivieMAT `nettoyage-actions.yml` (workflow réutilisable, 🔬 candidat) ; InnovAdapt `02dd5cd` |
| Envoi annexe avant la compilation | `Upload test report` (`if: always()`) bute sur le quota, le job échoue, « Build debug APK » est sauté | `continue-on-error: true` sur les envois annexes ; compiler avant de publier les rapports | Activity-track `android.yml`, run 38150625547 |
| Minutes ou facturation | Run refusé sans machine (`runner_id` 0, aucune étape, aucun journal), ou « recent account payments have failed or your spending limit needs to be increased » | Compiler en session ; régler la facturation du compte | SuivieMAT `outils.md` § 6 bis (2026-10-26) ; ToolMap `docs/JOURNAL.md` l. 64 (CI arrêtée depuis le 2026-10-16) |
| Push de balise depuis une session | HTTP 403, pas transitoire | Le workflow pose la balise avec son jeton | Popote `CLAUDE.md` ; Carnet `version.yml` l. 108-122 |
| GraphQL GitHub en session | `gh release list` refusé (403) | API REST : `gh api repos/<owner>/<repo>/releases` | Revue Popote, 2026-10-27 |
| `workflow_dispatch`, `schedule` | Absents tant que le workflow n'est pas sur la branche par défaut | Fusionner le workflow dans `main` | Carnet `publication.md` l. 102-103 ; SuivieMAT § 6 bis |
| Version laissée en brouillon | Lien mort derrière un run vert | `gh release edit <balise> --draft=false` | Carnet `apercu.yml` (vécu sur Popote) |
| Secrets de signature | Mot de passe altéré par `$`, `"` ou `` ` `` quand `${{ secrets.* }}` est collé dans le script | Passer par `env`, vérifier le `.jks` décodé par `keytool` tout de suite, `base64 -d --ignore-garbage` | InnovAdapt `c5aba61` ; voir aussi `maj-auto-github` (`Properties.load`) |
| Tests jamais lancés | Une CI qui ne fait qu'`assembleDebug` laisse passer une régression | `testDebugUnitTest` avant `assembleDebug` : a attrapé une vraie régression au premier passage | SuivieMAT `outils.md` § 6 bis |

## 7. Revue d'un dépôt depuis une session

- **Clone superficiel** : `git log A..B` y montre de fausses divergences. Vu trois fois le
  2026-10-27 : une « lignée orpheline » d'InnovAdapt qui descendait en fait de `main`, une
  branche d'Android-Suntrack « à 5 commits » déjà fusionnée, 17 commits au lieu de 4 dans
  Activity-track. Vérifier `git rev-parse --is-shallow-repository`, lire les vrais parents
  (`git cat-file -p <fusion>`), cloner sans `--depth` ou `git fetch --unshallow`.
- **Diagnostic de CI sans SDK** : liste des runs et des jobs, puis la fin du journal du job
  en échec (outils GitHub de la session) : la cause exacte, sans rien compiler.

## 8. Candidats (🔬, une ligne chacun)

- **Démarrer la vraie app sous Robolectric** : ToolMap `app/src/test/kotlin/fr/toolmap/AppStartupTest.kt`
  (SDK 26 et 35) lance la vraie `Application` (graphe Hilt complet) et `MainActivity` jusqu'à
  la carte, méthodes natives neutralisées par `instrumentedPackages`. Il échouait sur le code
  qui plantait au lancement sur le téléphone (ordre d'initialisation de MapLibre, `806ffb3`).
- **Contrôle statique des regex pour ICU** : ToolMap `core/src/test/kotlin/fr/toolmap/core/RegexPortabilityTest.kt`
  relit les littéraux passés à `Regex(…)`, `Pattern.compile(…)` et `.toRegex()` et refuse ce
  qu'ICU (le moteur d'Android) peut refuser alors que le JDK l'accepte : `}` nue, `--` dans une
  classe. Né d'un plantage réel sur téléphone (`c620a38`). Contrôle de grammaire, pas ICU ; ne
  lit que le premier littéral d'un appel.

## Historique des retours d'usage

<!-- Ne jamais réécrire ni supprimer une ligne : on en ajoute. La plus ancienne en haut. -->

| Date | Source | Retour | Effet sur la fiche |
|---|---|---|---|
| 2026-09-15 | ScanList, SuivieMAT | `setup-android` cassé, remplacé ; premiers tests en CI, une régression réelle attrapée (`d90e340`) | Pièges de CI |
| 2026-09-24 | SuivieMAT | Compilation et tests en session cloud, SDK dans le scratchpad, miroir Maven | Composant SDK ✅ |
| 2026-10-01 | ToolMap | Hook SessionStart, miroir par script d'init, clé de debug fixe (ADR-009) | Composant SDK ✅ |
| 2026-10-03 | ToolMap | APK de CI installé sur le téléphone de l'utilisateur, rapport du téléphone reçu | Livraison par la CI ✅ |
| 2026-10-12 | ToolMap | Quota d'artefacts épuisé : APK livré par la pré-version `debug-<sha7>`, garde de livraison (ADR-045, 046) | Pré-version 🟡 |
| 2026-10-26 | Popote, SuivieMAT, Carnet | Suite complète Popote en session (716 s) ; APK 1.1 à 1.5 de SuivieMAT compilés en session et déposés dans `release/` faute de minutes ; script SDK du Carnet | Ordres de grandeur |
| 2026-10-27 | Catalogage Library | Fiche créée à partir des revues du jour. Rejoué ici, dans le scratchpad : SDK en 22 s ; 429 constaté sur `repo.maven.apache.org`, miroir joignable ; projet Compose minimal compilé et testé sous Robolectric avec le script d'init ci-dessus et Gradle 8.14.3 du conteneur (`BUILD SUCCESSFUL` en 3 min 54 s à froid, 4 PNG produits) ; tests purs sans Gradle : `astro-soleil-lune`, 13 tests verts en 15 s. Contre-vérification : le bloc du § 1 écrivait `local.properties` dans le scratchpad au lieu du projet, corrigé puis rejoué (20 s) ; raison de l'ADR-047 ajoutée au § 5 ; tests `astro-soleil-lune` relancés, 13 verts en 9 s | — |
