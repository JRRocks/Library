# Voir un écran Compose en PNG sans émulateur (Robolectric)

| Champ | Valeur |
|---|---|
| **Identifiant** | `captures-compose-robolectric` (fiche méthode : pas de `src/`, le code de capture vit dans les tests de chaque app) |
| **Statut** | ✅ **méthode de base** : rendu « constaté fidèle par le propriétaire » le 2026-09-29 dans Popote (largeur de téléphone fixée par `qualifiers`, sous-menus atteints en tapant les vrais boutons). 🟡 **extensions du Carnet de recette** (thème sombre, police 130 % et 200 %, 412 dp, paysage) : jamais comparées à un écran réel, le Carnet n'a jamais tourné sur un téléphone. 🟡 **plusieurs tailles dans une classe** (Popote, paysage et tablette) : rendus jamais comparés à un écran réel. 🟡 artefact `captures` en CI : configuré, aucun run vérifié n'est consigné |
| **Plateforme** | Android, Jetpack Compose ; tests unitaires JVM (`testDebugUnitTest`), JDK 17 ou 21 |
| **Dépendances** | Robolectric 4.13 ; Compose BOM 2024.10.01 (`ui-test-junit4` en test, `ui-test-manifest` en `debugImplementation`) ; JUnit 4.13.2 ; `androidx.test:core` 1.6.1. Versions communes à Popote et au Carnet : AGP 8.8.0, Kotlin 2.0.21, compileSdk 35 |
| **Provenance** | Origine : [JRRocks/Suivie-Popote-Cie](https://github.com/JRRocks/Suivie-Popote-Cie) (privé) @ `6149a81` (2026-10-26). Code de référence public : [JRRocks/Carnet-de-recette](https://github.com/JRRocks/Carnet-de-recette) @ `8dc4820` (2026-10-26). Variante non Android : [JRRocks/InnovAdapt-CRM](https://github.com/JRRocks/InnovAdapt-CRM) (privé), branche `claude/android-rust-mobile` @ `d2c2f6e` |
| **Utilisé dans** | Popote Compagnie (26 classes `Captures*Test`) · Carnet de recette (3 classes, 4 tests) |
| **Fichiers d'origine** | Popote : `docs/decisions.md` l. 134-160, 2852-2929 et 3692-3722, `app-terrain/src/test/kotlin/fr/popote/terrain/capture/` (gabarit `CapturesOngletsTest.kt`, plusieurs tailles `CapturesComptoirTaillesTest.kt` et `CapturesPaysageTest.kt`, données fictives `DonneesExemple.kt`), `app-terrain/build.gradle.kts` l. 104-122 et 141-148, `EcransTest.kt` l. 102-109. Carnet : `app/src/test/kotlin/fr/jrrocks/carnetderecette/capture/{CapturesOngletsTest,CapturesLargeTest,CapturesPaysageTest}.kt`, `app/build.gradle.kts` l. 80-95 et 135-140, `outils/sdk-android.sh`, `.github/workflows/{tests,apercu}.yml` |
| **Dernière mise à jour de la fiche** | 2026-10-27 |

## À quoi ça sert

Une session cloud (claude.ai/code) n'a pas de KVM, donc pas d'émulateur. Un test Robolectric
vert dit que l'écran ne plante pas, pas à quoi il ressemble. La méthode dessine l'écran réel
(vrais composables, vrai thème, vraie police) dans un `Bitmap`, l'écrit en PNG sous
`app/build/captures/`, et on le regarde avec `Read` avant de publier un APK. Elle ne remplace
ni le geste tactile réel ni l'essai sur l'APK, qui confirme encore un écran neuf (Popote,
`docs/decisions.md` l. 156-158).

## Comment ça marche

- `@GraphicsMode(GraphicsMode.Mode.NATIVE)` : Robolectric dessine avec le moteur graphique
  natif d'Android. `@Config(sdk = [34], qualifiers = "w360dp-h800dp-xxhdpi")` fixe la taille
  et la densité de l'écran simulé.
- Le cadre est posé une fois (`setContent`) dans un `Box` marqué `testTag("capture")`.
  Changer un état observé (thème, police, écran affiché) recompose sans refaire le cadre.
- `capturer` dessine `window.decorView` dans un `Bitmap`, le recadre sur les bornes du nœud
  `capture` et l'écrit en PNG. Les `assertExists` font de chaque capture un test de fumée.
- Les messages affichés sont produits par le vrai code, pas recopiés dans le test (Carnet
  `3ec1388`) : la capture montre ce que l'app affichera.

## Variantes

| Variante | Comment | Source | Statut |
|---|---|---|---|
| 360 dp, sous-menus par les vrais boutons | `qualifiers = "w360dp-h800dp-xxhdpi"`, `taper` puis `attendre` | Popote `CapturesOngletsTest`, Carnet | ✅ |
| 412 dp ; paysage | `"w412dp-h915dp-xxhdpi"` ; `"w800dp-h360dp-land-xxhdpi"` | Carnet `CapturesLargeTest` (`f5f8baf`), `CapturesPaysageTest` (`8dc4820`) | 🟡 |
| Plusieurs tailles dans une classe (téléphone couché, tablette) | fenêtre plus grande que toutes (`"w1480dp-h924dp-hdpi"`), zone `Modifier.size(largeur.dp, hauteur.dp)` marquée `capture`. Ne vaut que si la mise en page lit la largeur de son parent (`BoxWithConstraints`), pas celle de l'appareil | Popote `CapturesComptoirTaillesTest`, `CapturesPaysageTest` (versions 96 et 97, `docs/decisions.md` l. 2852-2929) : mises en page choisies sur ces rendus par le porteur du projet (l. 2888-2892) | 🟡 |
| Thème sombre ; police 130 % et 200 % | état `sombre` passé au thème ; `LocalDensity provides Density(densite.density, fontScale)` | Carnet `CapturesOngletsTest` (`f5f8baf`) | 🟡 |
| Page PDF | `PdfDocument` ne fait que fournir un `Canvas` : la même fonction de dessin vise un `Bitmap`. Pagination et compression hors champ | Popote `docs/decisions.md` l. 134-140 | 🟡 |
| Artefact CI `captures` | `actions/upload-artifact@v4` du dossier : Popote en lancement manuel seulement, rétention 1 j (« le plus gros artefact ») ; Carnet à chaque run, 7 j | Popote `tests.yml` l. 55-69 (« à confirmer au premier run réel », `decisions.md` l. 159-160) ; Carnet `tests.yml`, `apercu.yml` | 🟡 |
| Hors Android : Slint | rendu logiciel hors écran (`MinimalSoftwareWindow`) écrit en PNG, données fictives posées par `examples/seed.rs` | InnovAdapt `crates/crm-app/examples/snapshot.rs` | 🟡 (jamais lancé ici) |

## Mise en place

1. Dépendances de `app/build.gradle.kts` :

   ```kotlin
   testImplementation(platform("androidx.compose:compose-bom:2024.10.01"))
   testImplementation("junit:junit:4.13.2")
   testImplementation("org.robolectric:robolectric:4.13")
   testImplementation("androidx.test:core:1.6.1")
   testImplementation("androidx.compose.ui:ui-test-junit4")
   debugImplementation("androidx.compose.ui:ui-test-manifest")
   ```

2. Dans `android { }` : `testOptions { unitTests { isIncludeAndroidResources = true } }`.
   `ui-test-manifest` n'existe qu'en debug : désactiver les tests unitaires de la variante
   release (`androidComponents { beforeVariants(selector().withBuildType("release")) {
   it.enableUnitTest = false } }`, Popote et Carnet).
3. `tasks.withType<Test>().configureEach { forkEvery = 1 }` (Popote ; voir les pièges). Le
   Carnet, avec 4 tests de capture, s'en passe encore.
4. Copier le gabarit, remplacer `ThemeApp`, `EcranAccueil` et `EtatAccueil` par ceux de
   l'app, puis `./gradlew :app:testDebugUnitTest --tests '*Captures*'`.

## Gabarit (écrit pour cette fiche)

Inspiré du Carnet (code public) et des aides `afficher`, `attendre`, `taper`, `capturer` de
Popote, qui les recopie classe par classe (aides `private`). Exécuté tel quel le 2026-10-27
dans une session cloud (historique).

```kotlin
package com.exemple.app.capture

import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.ComposeTimeoutException
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.printToString
import androidx.compose.ui.unit.Density
import com.exemple.app.EcranAccueil
import com.exemple.app.EtatAccueil
import com.exemple.app.ThemeApp
import java.io.File
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/** Ecrans reels en PNG sous app/build/captures/. Donnees fictives seulement : un PNG se partage. */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [34], qualifiers = "w360dp-h800dp-xxhdpi")
class CapturesAccueilTest {

    @get:Rule val compose = createAndroidComposeRule<ComponentActivity>()

    private var sombre by mutableStateOf(false)
    private var police by mutableFloatStateOf(1f)
    private var ecran by mutableIntStateOf(0)
    private var contenu by mutableStateOf<(@Composable () -> Unit)?>(null)

    private val exemple = EtatAccueil(titre = "Courses de la semaine", courses = listOf("Farine", "Oeufs", "Lait"))

    private fun cadre() {
        compose.setContent {
            val densite = LocalDensity.current
            CompositionLocalProvider(LocalDensity provides Density(densite.density, police)) {
                ThemeApp(sombre = sombre) {
                    // key(ecran) : chaque afficher() repart d'un ecran neuf, sans etat garde.
                    key(ecran) {
                        Box(Modifier.fillMaxSize().testTag("capture")) { contenu?.invoke() }
                    }
                }
            }
        }
    }

    private fun afficher(bloc: @Composable () -> Unit) {
        contenu = bloc
        ecran++
        compose.waitForIdle()
    }

    private fun attendre(texte: String) {
        try {
            compose.waitUntil(15_000) { compose.onAllNodesWithText(texte).fetchSemanticsNodes().isNotEmpty() }
        } catch (e: ComposeTimeoutException) {
            // Un echec de CI ne montre que ce message : on y joint l'ecran a l'echeance.
            throw AssertionError("'$texte' absent apres 15 s. Ecran :\n" + compose.onRoot().printToString(), e)
        }
    }

    /** Action semantique plutot que performClick(), qui echoue hors de la zone visible. */
    private fun taper(texte: String) {
        compose.onNodeWithText(texte).performSemanticsAction(SemanticsActions.OnClick)
        compose.waitForIdle()
    }

    private fun capturer(nom: String) {
        compose.waitForIdle()
        val zone = compose.onNodeWithTag("capture").fetchSemanticsNode().boundsInWindow
        val vue = compose.activity.window.decorView
        val plein = Bitmap.createBitmap(vue.width, vue.height, Bitmap.Config.ARGB_8888)
        vue.draw(Canvas(plein))
        val rogne = Bitmap.createBitmap(
            plein,
            zone.left.toInt(),
            zone.top.toInt(),
            zone.width.toInt(),
            zone.height.toInt().coerceAtMost(plein.height - zone.top.toInt()),
        )
        File("build/captures").mkdirs()
        File("build/captures/$nom.png").outputStream().use { rogne.compress(Bitmap.CompressFormat.PNG, 100, it) }
    }

    @Test
    fun `accueil puis reglages en clair en sombre et a 200 pour cent`() {
        cadre()

        afficher { EcranAccueil(exemple) }
        attendre("Courses de la semaine")
        capturer("01-accueil")

        taper("Réglages")
        attendre("Thème")
        capturer("02-reglages")

        sombre = true
        capturer("03-reglages-sombre")

        sombre = false
        police = 2f
        afficher { EcranAccueil(exemple) }
        compose.onNodeWithText("Courses de la semaine").assertExists()
        capturer("04-accueil-police-200")
    }
}
```

## Points de vigilance et limites connues

- **Ne jamais publier un PNG rendu sur des données réelles.** Dans Popote, 19 classes de
  captures sur 26 chargent le seed réel des membres (`importerSeedReel`) : leurs PNG montrent
  de vrais noms. Construire des données fictives, sur le modèle de `DonneesExemple.kt`
  (Popote : « aucun nom réel », déterministe) ou du compte `foyer@example.org` du Carnet.
- **Photos absentes** : dans Popote, les photos d'articles ne se chargent pas (tuiles grises).
  On juge structure, espacements et couleurs. Ce qui s'affiche vient du test : un stock négatif
  peut venir du test, pas de l'app (Popote l. 152-155). Nuance : `CapturesPaysageTest`
  (l. 134-136) attend 400 ms après chaque `afficher`, parce que les photos se chargent hors du
  fil principal et que la première image d'un écran les montre encore en gris. Une tuile grise
  peut donc venir d'une capture trop rapide.
- **Instabilité en suite complète** (Popote l. 3692-3722) : une CI à deux CPU a vu 186 échecs
  en cascade sur 301 tests. Trois parades, qui confinent sans expliquer la cause :
  `forkEvery = 1` (`60d3b08`) ; annuler le `viewModelScope` avant `db.close()` (`a926f77`),
  sinon une coroutine restée en file lit une base fermée pendant le test suivant ; joindre
  l'arbre sémantique au message d'une attente expirée (`af0a78b`, qui ne compile qu'avec
  `1fcd83c`). Des attentes de 15 s expirent encore parfois, sans cause établie.
- **`performClick()`** exige que le centre du nœud tombe dans la fenêtre simulée, sinon
  « Failed to inject touch input » : appeler l'action sémantique (`EcransTest.kt` l. 102-109).
- **Nom de test accentué** : Kotlin génère une classe nommée d'après le test, qui ne s'écrit
  pas (`InvalidPathException`, Popote `430a9a8`). Noms de test sans accent.
- **`assertExists` et `assertDoesNotExist`** sont des méthodes de `SemanticsNodeInteraction`,
  pas des fonctions à importer : un tel import casse la compilation (Popote `3f0e67c`).
- **Pot `android-all-instrumented`** (`14-robolectric-10818077-i6` pour Robolectric 4.13 et
  SDK 34) : Robolectric le télécharge lui-même au premier test, hors de Gradle, et Maven
  Central répond souvent 429 depuis une session. Le précharger (Carnet `outils/sdk-android.sh`)
  ou le déposer dans un dossier et passer `robolectric.offline=true` et
  `robolectric.dependency.dir=<dossier>` aux tests (2026-10-27). Voir
  [`android-en-session-cloud.md`](android-en-session-cloud.md).
- **Version d'Android simulée** : Popote et le Carnet restent en SDK 34 avec Robolectric 4.13.
  ToolMap (Robolectric 4.17) a vu le SDK 36 échouer dans Robolectric lui-même sous JDK 21 et
  reste en 35 (`docs/JOURNAL.md`, session 3 suite 17).
- **Police agrandie** : à 200 %, les grands textes grossissent moins que les petits (mise à
  l'échelle non linéaire, celle d'Android 14) : constaté sur les PNG du 2026-10-27, jamais
  comparé à un téléphone.
- **Quota d'artefacts** : publier les captures en CI consomme le quota de 0,5 Go partagé par
  tout le compte GitHub. Popote ne les publie qu'à la demande, gardées un jour.

## Historique des retours d'usage

<!-- Ne jamais réécrire ni supprimer une ligne : on en ajoute. La plus ancienne en haut. -->

| Date | Source | Retour | Effet sur la fiche |
|---|---|---|---|
| 2026-09-29 | Popote | Méthode « constatée fidèle par le propriétaire » (`docs/decisions.md` l. 141-151) : écrans réels à une largeur de téléphone, sous-menus atteints en tapant les boutons | Méthode de base ✅ |
| 2026-10-12 | Popote, CI | Cascade d'échecs Robolectric en suite complète ; `forkEvery = 1`, annulation du `viewModelScope`, arbre sémantique joint (`60d3b08`, `a926f77`, `af0a78b`) ; noms de test sans accent (`430a9a8`) ; imports inexistants (`3f0e67c`) | Pièges |
| 2026-10-26 | Carnet de recette | Captures dès le lot 1 (`594ffef`), messages tirés du vrai code (`3ec1388`), thème sombre, police 130 et 200 %, 412 dp (`f5f8baf`), paysage (`8dc4820`) ; aucun essai sur téléphone | Extensions 🟡 |
| 2026-10-27 | Catalogage Library | Fiche créée. Gabarit exécuté dans une session cloud sur un projet minimal (AGP 8.8.0, Kotlin 2.0.21, Robolectric 4.13, Gradle 8.14.3, JDK 21, pot Robolectric hors ligne) : `BUILD SUCCESSFUL`, 1 test, 0 échec, 4 PNG de 1080 × 2232 px (clair, réglages atteints par `taper`, sombre, police 200 %), relus à l'œil. Ce n'est pas une comparaison avec un téléphone. Contre-vérification : prénom fictif de l'exemple remplacé par un titre neutre, gabarit relancé tel qu'il est ici (`BUILD SUCCESSFUL` en 36 s, 1 test, 0 échec, 4 PNG) ; variante Popote « plusieurs tailles » ajoutée | Gabarit vérifié en session ; statuts inchangés |
