# Calcul astronomique hors-ligne : lever et coucher du soleil, phase et lever de la lune

| Champ | Valeur |
|---|---|
| **Identifiant** | `astro-soleil-lune` |
| **Statut** | 🟡 **Testé.** 13 tests JVM, exécutés ici le 2026-10-27 (13/13). 8 comparent à des **éphémérides publiées** (Paris, New York, hémisphère sud, Svalbard en jour et en nuit polaires, phases lunaires) ; les autres vérifient la cohérence interne. Il tourne dans le widget Sun Track, mais aucun retour terrain n'est consigné. Deux défauts du calcul de la lune (transit de la veille, jour du changement d'heure) ont été **corrigés dans la Library** le 2026-10-27 ; l'app source ne les a pas encore corrigés |
| **Plateforme** | **Kotlin pur** (JVM, Android ou autre), `java.time` uniquement. Aucune dépendance, aucun réseau |
| **Provenance** | [JRRocks/Android-Suntrack](https://github.com/JRRocks/Android-Suntrack) (privé), commit `e5c94b4` (2026-10-10), inchangé jusqu'à `2cb08aa` (branche `meteogram-weather-icons`, 2026-10-11). `MoonCalculator.kt` et `AstroTest.kt` modifiés par la Library le 2026-10-27 (voir l'historique) |
| **Utilisé dans** | Sun Track (widget Android) |
| **Fichiers d'origine** | `app/src/main/java/com/jeanrocka/suntrack/astro/{SolarCalculator,MoonCalculator,AstroTimes}.kt`, `app/src/test/.../AstroTest.kt` ; test d'altitude repris de `app/src/test/.../SkyPaletteTest.kt` |
| **Dernière mise à jour de la fiche** | 2026-10-27 |

## À quoi ça sert

Pour une date, une latitude, une longitude et un fuseau, on obtient sans réseau ni quota d'API :

- **Soleil** (`SolarCalculator.calculate`) : lever, coucher, **midi solaire**, durée du jour,
  déclinaison, drapeaux **jour polaire / nuit polaire**. Algorithme **NOAA**. Les tests
  tolèrent 4 à 6 min d'écart avec les éphémérides publiées : la précision « à la minute »
  souvent annoncée pour la NOAA n'est pas vérifiée ici.
- **Lune** (`MoonCalculator.calculate`) : phase (0 à 1), **fraction éclairée**, nom de phase
  (8 catégories), lever, coucher et **transit**. Théorie lunaire tronquée, à quelques minutes
  d'arc près ; lever et coucher par **échantillonnage de l'altitude topocentrique toutes les
  10 min**, puis interpolation (robuste les jours où la lune frôle l'horizon). Lever, coucher
  et transit ne sont rendus que s'ils tombent **dans le jour civil local** ; sinon `null`.
- `SolarCalculator.altitudeDeg(solarMinutes, latitude, declinationDeg)` : altitude du soleil
  en fonction du **temps solaire vrai** en minutes (720 = midi solaire) et de la déclinaison du
  jour, sans réfraction. Ce n'est pas « un instant » : la conversion d'un instant en temps
  solaire vit dans l'app (`render/SkyPalette.sunAltitude`, non catalogué).

## API

```kotlin
val sun: SunTimes = SolarCalculator.calculate(LocalDate.now(), 48.8566, 2.3522, ZoneId.of("Europe/Paris"))
// sun.sunrise / sun.sunset : ZonedDateTime? (null en jour ou nuit polaire → tester sun.polarDay / sun.polarNight)

val moon: MoonTimes = MoonCalculator.calculate(LocalDate.now(), 48.8566, 2.3522, ZoneId.of("Europe/Paris"))
// moon.phase (0 = nouvelle, 0.5 = pleine), moon.illumination, moon.phaseName, moon.waxing, moon.moonrise/moonset/transit
MoonCalculator.phaseAt(Instant.now()); MoonCalculator.illuminationAt(Instant.now())
```

## Composants

| Fichier (`src/`) | Rôle | Dépend d'Android ? | Tests (`tests/AstroTest.kt`) | Statut |
|---|---|---|---|---|
| `SolarCalculator.kt` | Lever, coucher, midi solaire, jour et nuit polaires (NOAA) ; `altitudeDeg` | Non | 6 tests contre éphémérides ou cohérence, 1 test d'altitude (ajout Library) | 🟡 |
| `MoonCalculator.kt` | Phase, illumination, lever, coucher, transit | Non | 3 tests de phase contre valeurs publiées ; lever et coucher : cohérence seulement ; transit et jour du changement d'heure : 2 tests (ajouts Library) | 🟡 (patché le 2026-10-27, voir vigilance) |
| `AstroTimes.kt` | Types `SunTimes`, `MoonTimes`, `MoonPhase` | Non | indirects | 🟡 |

## Intégration

Copiez `src/*.kt` (package `com.jeanrocka.suntrack.astro`, à renommer) et `tests/AstroTest.kt`.
Sur Android en minSdk 26, `java.time` est natif ; en dessous, il faut le désucrage.

`SunTimes.declinationDeg` est un paramètre de constructeur **sans valeur par défaut** (ajouté en
`942dd34`) : du code de test qui fabrique des `SunTimes` à la main doit le fournir.

Les tests purs se lancent sans Gradle ni Android (méthode : `docs/methodes/android-en-session-cloud.md`).

## Réglages éprouvés

Aucun : le pas d'échantillonnage de la lune (10 min), la marge de 2 h autour du jour et
l'altitude de l'horizon lunaire (0,125°) sont des choix de l'app, jamais ajustés après un retour
terrain.

## Points de vigilance

- **Piège classique évité** : le jour julien est calculé à partir de la date UTC qui
  **contient le midi local**. Le calculer depuis la date locale fait glisser d'un jour dans
  les fuseaux éloignés de Greenwich (« coucher d'hier » à l'est de la ligne de changement
  de date).
- Deux passes pour le midi solaire : on évalue les termes orbitaux **au** midi approché,
  comme le tableur de la NOAA.
- La précision se dégrade près des pôles : fiez-vous aux drapeaux polaires.
- `illumination` n'est **pas** linéaire en `phase`.
- **Époque de la théorie lunaire** : `daysSinceJ2000` soustrait `10956.0` (époque de
  Schlyter, 1999-12-31 0 h TU), **pas** `10957.5` (J2000.0). La première version de l'app avait
  `10957.5` ; ses effets : l'anomalie moyenne prend environ 19,6° de retard (phase et
  illumination fausses) et, surtout, `topocentricAltitude` tire l'heure TU de la partie
  fractionnaire du nombre de jours, donc le temps sidéral était faux de 180° : lever, coucher
  et transit décalés d'environ 12 h. Les 3 tests de phase l'ont attrapé (Android-Suntrack,
  run `31263914491`, corrigé en `de36c8a`) ; le test « moon rises and sets » est passé malgré
  l'erreur.
- **Couverture faible côté lune** : lever, coucher et transit ne sont comparés à **aucune**
  éphéméride publiée (seule leur présence est vérifiée). `waxing`, `phaseName` et
  `SunTimes.declinationDeg` ne sont vérifiés nulle part. Un test de lever de lune contre une
  valeur publiée (IMCCE ou équivalent) reste à écrire.
- **Défauts corrigés dans la Library le 2026-10-27** (l'app source les porte encore) :
  1. *Transit de la veille.* La recherche prenait le premier maximum de toute la fenêtre
     élargie (de 22 h la veille à 2 h le lendemain) : sur 26 à 29 jours par an (Paris, Sydney,
     New York, 2024), elle rendait le transit de la veille au soir, et jamais `null`,
     contrairement à la KDoc. Correctif : seuls les maxima dont l'heure affinée tombe dans le
     jour local comptent ; la marge ne sert plus qu'à fournir des voisins.
  2. *Jour du changement d'heure.* Le balayage durait toujours 24 h : le jour de 23 h, il
     débordait sur le lendemain et pouvait rendre un lever ou un coucher d'après minuit
     (New York, 2023-03-12 : lever du 13 à 0 h 44) ; le jour de 25 h, la dernière heure était
     ignorée. Correctif : la longueur réelle du jour local fixe le nombre d'échantillons.
  Dans Sun Track, le transit ne sert plus que de repli quand le lever de la lune manque
  (placement par `MoonPlacement` depuis la v1.5).
- Une transcription JavaScript du même calcul (NOAA + Schlyter, époque corrigée) existe dans
  la maquette `preview/sun_track_dynamic.html` de l'app : piste pour une app web, sans test.

## Briques voisines dans le même dépôt (candidats, non extraits)

Voir l'INDEX : météo Open-Meteo sans clé (`WeatherRepository`), icônes météo à paliers
(`WeatherTiers`), localisation sans Play Services (`LocationResolver`), rafraîchissement de
widget à la minute sans réveil (`TickScheduler`, `RefreshWorker`).

## Historique des retours d'usage

| Date | Source | Retour | Effet sur la fiche |
|---|---|---|---|
| 2026-10-12 | Catalogage Library | Tests contre éphémérides OK, pas de retour terrain | Statut 🟡 |
| 2026-10-27 | Revue Android-Suntrack et Sun-tracking | `src/` et `tests/` identiques à `e5c94b4` et à `2cb08aa`. APK de la release v1.5 publié (0 téléchargement), v1.7 commité ; seule vérification consignée : émulateur. Aucun retour sur la justesse des heures. CI de l'app rouge depuis octobre à l'étape `setup-android` : les tests n'y ont plus tourné depuis `de36c8a`. Sun-tracking (`2aec84a`) est une republication de l'état initial avec le bug d'époque (CI : 3 tests de phase sur 10 en échec) : rien à extraire | Statut 🟡 inchangé ; vigilances « époque » et « couverture » ajoutées |
| 2026-10-27 | Library (patch, § 5.C) | Défaut du transit de la veille (signalé par une contre-revue, reproduit en Kotlin : 26 à 29 jours par an) et défaut du jour de 23 h (New York 2023-03-12), tous deux contraires à la KDoc. Deux tests ajoutés, en échec avant le correctif ; correctif dans `MoonCalculator.calculate` ; test d'altitude repris de `SkyPaletteTest`. 13/13 tests verts (Kotlin 2.0.21, JUnit 4.13.2, sans Gradle) | `src/MoonCalculator.kt` et `tests/AstroTest.kt` modifiés ; Android-Suntrack à prévenir |
