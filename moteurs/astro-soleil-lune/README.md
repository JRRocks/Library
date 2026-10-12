# Calcul astronomique hors-ligne : lever et coucher du soleil, phase et lever de la lune

| Champ | Valeur |
|---|---|
| **Identifiant** | `astro-soleil-lune` |
| **Statut** | 🟡 **Testé.** 10 tests JVM comparés aux **éphémérides publiées** (Paris, New York, hémisphère sud, Svalbard en jour et en nuit polaires, phases lunaires). Il tourne dans le widget Sun Track, mais aucun retour terrain n'est consigné |
| **Plateforme** | **Kotlin pur** (JVM, Android ou autre), `java.time` uniquement. Aucune dépendance, aucun réseau |
| **Provenance** | [JRRocks/Android-Suntrack](https://github.com/JRRocks/Android-Suntrack) (privé), commit `e5c94b4` (2026-10-10) |
| **Utilisé dans** | Sun Track (widget Android) |
| **Fichiers d'origine** | `app/src/main/java/com/jeanrocka/suntrack/astro/{SolarCalculator,MoonCalculator,AstroTimes}.kt`, `app/src/test/.../AstroTest.kt` |
| **Dernière mise à jour de la fiche** | 2026-10-12 |

## À quoi ça sert

Pour une date, une latitude, une longitude et un fuseau, on obtient sans réseau ni quota d'API :

- **Soleil** (`SolarCalculator.calculate`) : lever, coucher, **midi solaire**, durée du jour,
  déclinaison, drapeaux **jour polaire / nuit polaire**. Algorithme **NOAA**, précision
  inférieure à la minute aux latitudes tempérées.
- **Lune** (`MoonCalculator.calculate`) : phase (0 à 1), **fraction éclairée**, nom de phase
  (8 catégories), lever, coucher et **transit**. Théorie lunaire tronquée, à quelques minutes
  d'arc près ; lever et coucher par **échantillonnage de l'altitude topocentrique toutes les
  10 min**, puis interpolation (robuste les jours où la lune frôle l'horizon).
- `SolarCalculator.altitudeDeg(...)` : altitude du soleil à un instant donné.

## API

```kotlin
val sun: SunTimes = SolarCalculator.calculate(LocalDate.now(), 48.8566, 2.3522, ZoneId.of("Europe/Paris"))
// sun.sunrise / sun.sunset : ZonedDateTime? (null en jour ou nuit polaire → tester sun.polarDay / sun.polarNight)

val moon: MoonTimes = MoonCalculator.calculate(LocalDate.now(), 48.8566, 2.3522, ZoneId.of("Europe/Paris"))
// moon.phase (0 = nouvelle, 0.5 = pleine), moon.illumination, moon.phaseName, moon.waxing, moon.moonrise/moonset/transit
MoonCalculator.phaseAt(Instant.now()); MoonCalculator.illuminationAt(Instant.now())
```

## Intégration

Copiez `src/*.kt` (package `com.jeanrocka.suntrack.astro`, à renommer) et `tests/AstroTest.kt`.
Sur Android en minSdk 26, `java.time` est natif ; en dessous, il faut le désucrage.

## Points de vigilance

- **Piège classique évité** : le jour julien est calculé à partir de la date UTC qui
  **contient le midi local**. Le calculer depuis la date locale fait glisser d'un jour dans
  les fuseaux éloignés de Greenwich (« coucher d'hier » à l'est de la ligne de changement
  de date).
- Deux passes pour le midi solaire : on évalue les termes orbitaux **au** midi approché,
  comme le tableur de la NOAA.
- La précision se dégrade près des pôles : fiez-vous aux drapeaux polaires.
- `illumination` n'est **pas** linéaire en `phase`.

## Briques voisines dans le même dépôt (candidats, non extraits)

Voir l'INDEX : météo Open-Meteo sans clé (`WeatherRepository`), localisation sans Play
Services (`LocationResolver`), rafraîchissement de widget à la minute sans réveil
(`TickScheduler`, `RefreshWorker`).

## Historique des retours d'usage

| Date | Source | Retour | Effet sur la fiche |
|---|---|---|---|
| 2026-10-12 | Catalogage Library | Tests contre éphémérides OK, pas de retour terrain | Statut 🟡 |
