---
name: depannage-ci
description: Diagnostiquer une compilation Android ou une CI GitHub Actions qui échoue, à partir des pannes déjà rencontrées sur ces projets et de leur parade. À utiliser quand une exécution est rouge, reste en attente ou ne démarre pas, quand l'APK n'apparaît pas, quand Gradle échoue (429, SDK absent, mémoire), quand un test passe une fois sur deux, ou quand l'émulateur ne répond pas.
---

# Dépanner une compilation ou une CI

Ces pannes ont déjà coûté des sessions. Cherche d'abord ici avant de chercher ailleurs. Si la panne n'y figure pas, trouve sa cause, puis signale-la dans le message final pour qu'elle soit ajoutée.

Le détail éprouvé (installation du SDK, miroir Maven, livraison d'un APK de plus de 30 Mio, pièges de CI avec leur source) est dans la fiche méthode de la Library : `~/.claude/library/docs/methodes/android-en-session-cloud.md`, ou le même chemin dans le dépôt `Library` s'il est joint à la session. Ce skill en donne l'essentiel et y ajoute les tests et l'émulateur.

## Règles avant tout

- **Lis le journal de la tâche en échec** avant de toucher au code. Ne devine pas.
- **Reproduis en local** quand c'est possible, corrige, revérifie, puis repousse. La CI n'est pas un compilateur d'essai : six exécutions rouges de suite ont déjà été produites ainsi.
- **Un test instable se répare**, cause trouvée. On ne le relance pas jusqu'au vert, on ne le désactive pas, on ne le saute pas.
- **Pas de commit vide** pour relancer la CI. Pas de test retiré pour obtenir du vert.
- Une panne qui ne vient pas du code se dit telle quelle à l'utilisateur, avec ce qu'il doit faire.

## L'exécution ne démarre pas, ou n'aboutit pas

| Ce que tu vois | Cause | Parade |
|---|---|---|
| Échec immédiat, aucune étape lancée, message de facturation ou de limite de dépense | Minutes Actions épuisées sur un dépôt privé | Ce n'est pas un échec de code : dis-le. Compile en local si possible. Solutions durables, à décider par l'utilisateur : dépôt public (minutes gratuites) ou exécuteur sur son PC |
| Exécution « en attente » sans fin | La CI tourne sur le PC de l'utilisateur et son exécuteur n'est pas lancé | Dis-lui de le lancer. Ne relance pas le workflow |
| Rien ne se déclenche après un push | Le workflow ne liste que certaines branches | Lance-le à la main sur la branche (`workflow_dispatch`) |
| Le lancement à la main ou la planification n'existent pas | Le workflow n'est pas sur la branche par défaut | Il doit d'abord être fusionné dans la branche par défaut |
| « Failed to find package 'tools' » avant toute étape Gradle | L'action `setup-android` est cassée | Retire-la : la machine de la CI a déjà un SDK, ou installe-le par le script du projet |
| Mot de passe de signature refusé en CI seulement | Secret collé dans le script, ou relu par un fichier de propriétés qui l'altère | Passe les secrets par l'environnement de l'étape, et vérifie la clé décodée tout de suite |
| Téléversement de l'artefact refusé | Quota de stockage des artefacts atteint | Nettoyage des anciens artefacts dans le workflow, téléversement non bloquant, APK publié aussi en pré-version |
| L'APK d'une branche a disparu | Le nettoyage d'une autre exécution ne garde que le dernier | Donne le lien de la pré-version, ou relance la compilation de la branche |
| Push d'une étiquette `v…` refusé (403) depuis une session cloud | Blocage volontaire de la session | Ne réessaie pas : c'est le workflow qui pose l'étiquette |
| Boucle d'exécutions après un dépôt d'APK par la CI | Le commit de dépôt relance la CI | `[skip ci]` dans le message de ce commit |

## Gradle et le SDK

| Ce que tu vois | Cause | Parade |
|---|---|---|
| `SDK location not found`, pas de `ANDROID_HOME` | Session cloud sans SDK | Lance le script d'installation du projet s'il existe ; sinon installe le SDK hors du dépôt et écris `local.properties` (jamais committé) |
| `429 Too Many Requests` sur Maven Central | Limite du bac à sable | Miroir Maven par un script d'initialisation Gradle hors du dépôt |
| `./gradlew: Permission denied` | Bit exécutable perdu au clone | `chmod +x gradlew` |
| `JAVA_HOME is not set` sous Windows | `java` absent du PATH | `JAVA_HOME` = le JDK fourni par Android Studio |
| Gradle tué, mémoire insuffisante | Trop de tâches en parallèle | `--max-workers=2` |
| Hôte refusé par le réseau de la session | Politique réseau de l'environnement cloud | Ne contourne pas : dis quel hôte manque. Vérifie avec la CI |
| Fins de ligne ou caractères abîmés entre Windows et le cloud | Conversion automatique de git | `core.autocrlf=false`, fins de ligne `lf` |
| Numéro de version automatique faussé | Un outil de mise en forme a modifié les sources pendant la compilation | Lance la mise en forme seule d'abord, puis la compilation |

Un téléchargement volumineux (SDK, données de test, moteur d'itinéraire) se garde en cache hors du dépôt : ne le refais pas à chaque session.

## Tests

| Ce que tu vois | Cause | Parade |
|---|---|---|
| Un test passe seul et échoue dans la suite | Coroutine ou flux de base de données encore actif à la fin d'un autre test | Arrête et attends la fin (`cancelAndJoin`) avant de fermer la base ; attends l'état complet, pas le premier état émis ; une JVM par classe en dernier recours |
| Tests Robolectric en échec dès le téléchargement (429, délai dépassé) | Robolectric télécharge ses fichiers Android à l'exécution | Cache de la CI (`actions/cache`), miroir Maven |
| Plantage au lancement sur le téléphone, tests verts | Expression régulière acceptée par la JVM, refusée par Android | Échappe toutes les accolades littérales (`\{`, `\}`) ; garde un test qui le vérifie |
| Un écran s'ouvre à la place d'un autre | Une route fixe correspond au modèle d'une autre route | Test qui vérifie qu'aucune route fixe n'est avalée par un modèle |
| Nom de test refusé sur appareil | Espaces dans le nom d'une méthode de test instrumenté | Pas d'espace dans les noms des tests sur appareil |

## Émulateur (PC)

| Ce que tu vois | Parade |
|---|---|
| Les appuis « ne font rien » | L'écran actif a changé : redécouvre son identifiant à chaque démarrage, puis `adb shell input -d <id> tap` |
| Chemin distant abîmé sous Git Bash (`secure_mkdirs() failed`) | Préfixe la commande par `MSYS_NO_PATHCONV=1` |
| La base rapatriée ne contient pas les dernières écritures | Rapatrie aussi les fichiers `-wal` et `-shm` |
| Capture d'écran corrompue | `adb exec-out`, pas `adb shell` |
| APK refusé à l'installation | APK limité à l'architecture du téléphone : utilise l'APK complet sur l'émulateur |
| `run-as` refusé | APK non debuggable : utilise la version de debug pour inspecter les données |
| Émulateur plein, ou installations perdues | Enregistre un instantané une fois l'émulateur prêt ; installe l'APK réduit |

## Ce que l'émulateur et la session cloud ne remplacent pas

GPS réel, capteurs, caméra, NFC, deux téléphones, écran éteint, batterie : seul le téléphone de l'utilisateur le montre. Une session cloud n'a pas d'émulateur. Dis-le dans le message final, point par point.
