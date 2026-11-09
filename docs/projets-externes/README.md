# Projets externes repérés

Registre des projets déjà publiés, sur GitHub ou ailleurs, qui pourraient servir à mes applications. Il est tenu par le skill [`recherche-github`](../../skills/recherche-github/SKILL.md) : avant de chercher, on regarde ici ; après avoir cherché, on note ici.

| Champ | Valeur |
|---|---|
| **Statut** | 🔬 Chaque ligne est vérifiée sur le dépôt du projet à la date indiquée : existence, licence, dernière activité. **Vérifié ne veut pas dire essayé** : aucun de ces projets n'a encore été essayé ni intégré |
| **Dernière revue** | 2026-11-09, toutes applications |

États : `à essayer` · `essayé` (date, verdict) · `intégré` (où, version) · `écarté` (raison).

Licences : voir [`licences.md`](../../skills/recherche-github/licences.md). GPL et AGPL engagent quand on **reprend le code** ; un outil lancé à côté n'engage pas.

## Mihon (fork personnel, Apache-2.0)

| Besoin | Projet | Ce que ça apporte | Comment l'essayer | Licence | Activité | Intégration | État |
|---|---|---|---|---|---|---|---|
| Colorisation plus juste des pages | [vikast908/ColorComic](https://github.com/vikast908/ColorComic) | Autour du même modèle que le mien (manga-colorization-v2) : une couleur par objet au lieu d'un voile uniforme (découpage de la page, reconnaissance des zones, palette imposée au modèle), cohérence des couleurs d'une page à l'autre, détection des pages ratées, agrandissement Real-ESRGAN. Mode « référence » (MangaNinja) à partir d'une page déjà en couleur | Sur le PC à carte NVIDIA : cloner, installer, lancer `python app.py`, déposer un PDF. Environ 750 Mo de modèles au premier lancement, 6 Go de plus pour le mode référence | Code MIT. Parties reprises : manga-colorization-v2 sans licence, MangaNinja en CC BY-NC (non commercial) | v2.0.0, dernier commit 2026-07-10 | Moyenne : c'est une application web qui traite un PDF entier, pas un service page par page. Ses modules `core/` se reprennent dans mon serveur de colorisation comme troisième moteur | à essayer |
| Fonctions déjà faites dans un fork voisin | [komikku-app/komikku](https://github.com/komikku-app/komikku) | Suggestions liées à la série, catégories cachées, thème tiré de la couverture, ajout en lot, fil d'actualité toutes sources, synchronisation à double sens avec les sites de suivi, fusion de séries. Reprend aussi tout TachiyomiSY | Installer son APK depuis les Releases, à côté de mon fork | Apache-2.0, même lignée | v1.14.1, actif en 2026-11 | Faible à moyenne par fonction : même base de code, même licence | à essayer |
| Traduire une page | [zyddnys/manga-image-translator](https://github.com/zyddnys/manga-image-translator) | Traduit en un geste le texte d'une page : repère le texte, le traduit et le remet dans l'image | Démo en ligne : <https://touhou.ai/imgtrans/> ; ou image Docker sur le PC | GPL-3.0 : sans effet s'il tourne comme service séparé | Actif en 2026-10 | Moyenne : un second service à côté du serveur de colorisation | à essayer |
| Agrandir et nettoyer une image sur le téléphone | [tumuyan/RealSR-NCNN-Android](https://github.com/tumuyan/RealSR-NCNN-Android) | Real-ESRGAN, Real-CUGAN et waifu2x sur la carte graphique du téléphone | APK dans les Releases | MIT ; chaque modèle a sa licence | Dernier commit 2026-05-30 | Forte : binaires natifs et modèles à embarquer | à essayer |

## SuivieMAT et ScanList

| Besoin | Projet | Ce que ça apporte | Comment l'essayer | Licence | Activité | Intégration | État |
|---|---|---|---|---|---|---|---|
| Lire les codes empilés que le lecteur actuel ignore | [zxing-cpp/zxing-cpp](https://github.com/zxing-cpp/zxing-cpp) | Lit DataBar empilé et étendu empilé, MicroPDF417, Micro QR, rMQR, en plus des formats courants. Enveloppe Android fournie | Installer [Binary Eye](https://f-droid.org/en/packages/de.markusfisch.android.binaryeye/) ([markusfisch/BinaryEye](https://github.com/markusfisch/BinaryEye), bâti sur zxing-cpp) et viser un code empilé : s'il le lit, la bibliothèque le lira | Apache-2.0 (Binary Eye : MIT) | v3.1.1, actif en 2026-11 | Moyenne : un second analyseur d'image à côté de ML Kit, bibliothèque native | à essayer |
| OCR des marquages gravés ou peu contrastés | [RapidAI/RapidOCR](https://github.com/RapidAI/RapidOCR) | Modèles PaddleOCR convertis pour tourner partout, hors ligne | Démo en ligne, avec une photo du marquage : <https://huggingface.co/spaces/RapidAI/RapidOCRv3> | Apache-2.0 | Actif en 2026-11 | Forte : moteur et modèles à embarquer | à essayer |
| OCR de repli sans service Google | [adaptech-cz/Tesseract4Android](https://github.com/adaptech-cz/Tesseract4Android) | Tesseract empaqueté pour Android | Pas d'application de démonstration : à essayer dans une version de test | Apache-2.0 | 4.9.0, dernier commit 2026-02 | Moyenne | à essayer |
| Export tableur au lieu du CSV | [dhatim/fastexcel](https://github.com/dhatim/fastexcel) | Écrit des `.xlsx` sans la lourde Apache POI. Supprime du même coup la question du séparateur et des formules injectées du CSV | Bibliothèque : pas d'essai sans intégration | Apache-2.0 | v0.8.7, actif en 2026-10 | Faible à moyenne. Fonctionnement sur Android à vérifier | à essayer |

## Scam Stop (dérivé d'un projet GPL-3.0)

| Besoin | Projet | Ce que ça apporte | Comment l'essayer | Licence | Activité | Intégration | État |
|---|---|---|---|---|---|---|---|
| Bloquer le démarchage sans bloquer tous les inconnus | [Saracroche](https://codeberg.org/cbouvat/saracroche-android) (déménagé de GitHub vers Codeberg) | Bloque les appels et SMS indésirables à partir d'une base de numéros tenue à jour, sans collecter de données d'appel | [Play Store](https://play.google.com/store/apps/details?id=com.cbouvat.android.saracroche) ou [F-Droid](https://f-droid.org/en/packages/com.cbouvat.android.saracroche/) | GPL-3.0 : code reprenable dans Scam Stop | Dernier commit 2026-11-02 | Faible à moyenne : sa base de numéros et un mode « démarchage seulement » à ajouter | à essayer |
| Règles plus fines | [aj3423/SpamBlocker](https://github.com/aj3423/SpamBlocker) | Règles par contact, groupe, préfixe de contact, et filtrage des SMS, sans remplacer l'application Téléphone | [F-Droid](https://f-droid.org/packages/spam.blocker) ou Play Store | MIT | v5.20, actif en 2026-11 | Moyenne : idées et règles à reprendre une par une | à essayer |

## ToolMap

| Besoin | Projet | Ce que ça apporte | Comment l'essayer | Licence | Activité | Intégration | État |
|---|---|---|---|---|---|---|---|
| Itinéraires sans installer une seconde application | [abrensch/brouter](https://github.com/abrensch/brouter) | Le moteur que j'appelle déjà existe en modules (`brouter-core`) : il peut s'embarquer au lieu de passer par l'application BRouter | Rien de plus à essayer : même moteur, même résultat | MIT | v1.7.10, actif en 2026-11 | Moyenne : données et profils à gérer dans ToolMap | à essayer |
| Une carte hors ligne en un seul fichier | [protomaps/PMTiles](https://github.com/protomaps/PMTiles) | Format d'archive de tuiles en un fichier. MapLibre Native Android le lit déjà (`pmtiles://`), d'après son journal des changements | Ouvrir un fichier `.pmtiles` dans une version de test | BSD | Dernier commit 2026-09 | Faible : la bibliothèque de carte de ToolMap le lit déjà | à essayer |
| Partage de position sans réseau | [meshtastic/Meshtastic-Android](https://github.com/meshtastic/Meshtastic-Android) | Positions et messages de groupe par radio LoRa, sans téléphonie ni internet. Demande un petit boîtier radio par personne | Application sur [F-Droid](https://f-droid.org/packages/com.geeksville.mesh/), Play Store ou dans les Releases, avec deux boîtiers | GPL-3.0 | v2.8.4, actif en 2026-11 | Forte, et matériel à acheter. À voir d'abord comme outil à côté | à essayer |
| Vérifier mes grilles MGRS | [ngageoint/mgrs-java](https://github.com/ngageoint/mgrs-java) | Conversions et lignes de grille de l'agence américaine de cartographie : une référence pour contrôler mes calculs | Bibliothèque : à utiliser dans un test | MIT | 2.1.3, dernier commit 2024-04 : stable, peu suivi | Faible, comme référence de test seulement | à essayer |

## Sun Track

| Besoin | Projet | Ce que ça apporte | Comment l'essayer | Licence | Activité | Intégration | État |
|---|---|---|---|---|---|---|---|
| Calculs du soleil et de la lune sans défaut | [cosinekitty/astronomy](https://github.com/cosinekitty/astronomy) | Moteur d'astronomie éprouvé, avec une version Kotlin : levers, couchers, phases, positions. Mes calculs maison ont déjà eu deux défauts sur la lune | Bibliothèque : comparer ses résultats aux miens dans un test, sur un an de dates | MIT | v2.1.19, dernier commit 2025-01 : stable | Faible : une dépendance, ou seulement une référence de test | à essayer |
| Autre référence de calcul | [shred/commons-suncalc](https://github.com/shred/commons-suncalc) | Levers, couchers et phases en Java | Idem | Apache-2.0 | v3.11, dernier commit 2025-02 | Faible | à essayer |
| Icônes météo | [basmilius/weather-icons](https://github.com/basmilius/weather-icons) | Jeu d'icônes météo libres | Aperçu sur la page du projet | MIT | v2.0.0, dernier commit 2026-04 | Faible | à essayer |

## Épreuves (Learn-UI-UX)

| Besoin | Projet | Ce que ça apporte | Comment l'essayer | Licence | Activité | Intégration | État |
|---|---|---|---|---|---|---|---|
| Faire réviser au meilleur moment | [open-spaced-repetition/ts-fsrs](https://github.com/open-spaced-repetition/ts-fsrs) | Le planificateur FSRS, en TypeScript : il remplace les boîtes de Leitner et SM-2 par un calcul ajusté à la mémoire de chacun | Bac à sable en ligne : <https://open-spaced-repetition.github.io/ts-fsrs/playground> | MIT | Actif en 2026-11 | Moyenne : le moteur de révision est isolé, l'état des cartes est à convertir, chaque réponse se note sur quatre niveaux | à essayer |
| Contrôler l'accessibilité automatiquement | [dequelabs/axe-core-npm](https://github.com/dequelabs/axe-core-npm) (`@axe-core/playwright`), moteur [dequelabs/axe-core](https://github.com/dequelabs/axe-core) | Un appel de plus dans mes contrôles Playwright existants. Ne trouve qu'une partie des défauts, et suit WCAG, pas le RGAA | Extension de navigateur axe DevTools, sur l'application ouverte | MPL-2.0 | v4.14.0, actif en 2026-11 | Faible | à essayer |
| Leçons RGAA à partir du texte officiel | [DISIC/accessibilite.numerique.gouv.fr](https://github.com/DISIC/accessibilite.numerique.gouv.fr) | Le RGAA officiel en fichiers de données (`RGAA/criteres.json`, `glossaire.json`, `methodologies.json`), en français | Ouvrir le dossier `RGAA/` du dépôt | Contenus : Licence Ouverte 2.0 (citer la source) ; code : MIT. Marque de l'État et police Marianne exclues | Dernier commit 2026-08-18 | Moyenne : critères à transformer en leçons et en exercices | à essayer |

## Coach d'entraînement (Activity-track)

**À savoir** : intervals.icu a annoncé le 2026-10-09 qu'il devra probablement bloquer bientôt l'accès aux données Garmin par son API (« It is likely that we will have to block access to Garmin data via the API soon », [fil officiel](https://forum.intervals.icu/t/api-access-to-intervals-icu/609)), sans date. Ma chaîne actuelle passe par là. Garmin ouvre de son côté une API personnelle officielle, en lecture seule.

| Besoin | Projet | Ce que ça apporte | Comment l'essayer | Licence | Activité | Intégration | État |
|---|---|---|---|---|---|---|---|
| Lire mes données Garmin par la voie officielle | [garmin/connect-personal-api-python](https://github.com/garmin/connect-personal-api-python) | Client publié par Garmin pour son API personnelle : sommeil, variabilité cardiaque, stress, résumés quotidiens, activités. Une clé personnelle révocable, pas de mot de passe | Créer une clé dans Garmin Connect, puis la commande `gc-personal` du paquet | Apache-2.0 | v1.0.0, 2026-10-21 : tout récent | Moyenne : remplace l'export vers intervals.icu puis Drive. Python 3.12 au moins | à essayer |
| Laisser Claude lire Garmin sans fichiers intermédiaires | [Mason-Levyy/garmin-personal-mcp](https://github.com/Mason-Levyy/garmin-personal-mcp) | Serveur MCP en lecture seule sur cette API officielle | Sur le PC : `uvx garmin-personal-mcp`, avec la clé personnelle | MIT | v0.3.0, 2026-11-03 : projet de quelques semaines, non fait par Garmin | Faible sur PC ; à étudier pour la tâche de nuit dans le cloud. Code à relire avant usage | à essayer |
| Laisser Claude lire intervals.icu | [hhopke/intervals-icu-mcp](https://github.com/hhopke/intervals-icu-mcp) | Serveur MCP : activités, bien-être, forme et fatigue déjà calculées, calendrier (73 outils) | Sur le PC : `uvx intervals-icu-mcp`, avec la clé d'API intervals.icu | MIT | v5.9.1, 2026-11-08 | Faible. Dépend de l'accès aux données Garmin chez intervals.icu (voir plus haut) | à essayer |
| Montre vers téléphone sans le nuage Garmin | [Gadgetbridge](https://codeberg.org/Freeyourgadget/Gadgetbridge) (Codeberg) | Synchronise des montres Garmin récentes directement avec le téléphone et garde les fichiers d'activité sur place | [F-Droid](https://f-droid.org/en/packages/nodomain.freeyourgadget.gadgetbridge/) | AGPL-3.0 : à utiliser à côté, sans reprendre son code | Actif en 2026-11 | Forte. Prise en charge Garmin encore expérimentale | à essayer |

## CRM

**À savoir** : depuis le 2026-09-01, toute entreprise doit pouvoir **recevoir** des factures électroniques ; les petites et micro-entreprises ont jusqu'au 2027-09-01 pour les **émettre**. Elles passent par une plateforme agréée, et un PDF ordinaire envoyé par mail n'est plus une facture conforme. Source : [impots.gouv.fr](https://www.impots.gouv.fr/professionnel/je-decouvre-la-facturation-electronique), lu le 2026-11-09. Fabriquer un fichier Factur-X ne suffit donc pas : il faut aussi choisir une plateforme.

| Besoin | Projet | Ce que ça apporte | Comment l'essayer | Licence | Activité | Intégration | État |
|---|---|---|---|---|---|---|---|
| Fabriquer une facture Factur-X | [akretion/factur-x](https://github.com/akretion/factur-x) | Assemble un PDF Factur-X à partir d'un PDF et de ses données, vérifie le fichier | `pip3 install factur-x`, puis ses commandes | BSD | 7.9, actif en 2026-11 | Moyenne : Python, à côté du noyau Rust | à essayer |
| Vérifier une facture Factur-X | [ZUGFeRD/mustangproject](https://github.com/ZUGFeRD/mustangproject) | Valide, assemble, extrait | Outil Java en ligne de commande | Apache-2.0 | Actif en 2026-11 | Faible, comme outil de contrôle | à essayer |
| Ne rien écrire : un outil déjà complet | [Dolibarr/dolibarr](https://github.com/Dolibarr/dolibarr) | Clients, contacts, agenda, tâches, factures, comptabilité, dans un logiciel libre très répandu en France | Démo en ligne : <https://demo.dolibarr.org/public/demo/> | GPL-3.0 | Actif en 2026-11 | Forte : un serveur à héberger, les données à reprendre, plus de synchronisation chiffrée par Drive | à essayer |
| Fusion des modifications entre appareils | [loro-dev/loro](https://github.com/loro-dev/loro), [automerge/automerge](https://github.com/automerge/automerge) | Bibliothèques Rust éprouvées pour fusionner sans conflit, à la place de l'algorithme maison | Bibliothèques | MIT | Actifs en 2026-11 | Forte : toutes les fiches à remodeler, l'existant à migrer ; chiffrement et dépôt sur Drive restent à écrire | à essayer |

## Carnet de recette

| Besoin | Projet | Ce que ça apporte | Comment l'essayer | Licence | Activité | Intégration | État |
|---|---|---|---|---|---|---|---|
| Récupérer la recette déjà structurée | [hhursev/recipe-scrapers](https://github.com/hhursev/recipe-scrapers) | Lit ingrédients, étapes et temps sur la page web d'une recette. HelloFresh fait partie des sites pris en charge | En Python, sur une adresse de recette | MIT | 15.13.0, actif en 2026-11 | Moyenne : bibliothèque Python, logique à porter ou service à côté. Conditions d'utilisation du site à respecter | à essayer |
| Commande vocale hors ligne | [k2-fsa/sherpa-onnx](https://github.com/k2-fsa/sherpa-onnx) | Reconnaissance vocale et détection de mots-clés sur le téléphone, sans réseau | APK de démonstration : <https://k2-fsa.github.io/sherpa/onnx/android/apk.html> (reconnaissance), <https://k2-fsa.github.io/sherpa/onnx/kws/apk.html> (mots-clés) | Apache-2.0 ; chaque modèle a sa licence | Actif en 2026-11 | Moyenne : modèle français à choisir et à embarquer | à essayer |
| Format ouvert de recettes | [cooklang/cooklang-rs](https://github.com/cooklang/cooklang-rs) | Un format texte de recette et son analyseur, pour ne pas enfermer les recettes dans l'application | Bibliothèque | MIT | v0.19.0, actif en 2026-11 | Moyenne | à essayer |

## Bibliothèque de skills

Un skill est une consigne donnée à Claude : celui d'un tiers se relit en entier avant d'être installé, et après chaque mise à jour.

| Besoin | Projet | Ce que ça apporte | Comment l'essayer | Licence | Activité | Intégration | État |
|---|---|---|---|---|---|---|---|
| Skills déjà écrits par d'autres | [obra/superpowers](https://github.com/obra/superpowers), [anthropics/skills](https://github.com/anthropics/skills), [anthropics/claude-plugins-official](https://github.com/anthropics/claude-plugins-official), [pbakaus/impeccable](https://github.com/pbakaus/impeccable) (déjà utilisé pour mes refontes) | Des procédures prêtes : cadrage, plans, tests, débogage, design | Lire le skill, puis l'installer dans une session d'essai | MIT ; licence par skill ; Apache-2.0 ; Apache-2.0 | Actifs en 2026-11 | Faible à moyenne : peut doublonner ou contredire mes règles | à essayer |
| Contrôler un skill avant de l'installer | [cisco-ai-defense/skill-scanner](https://github.com/cisco-ai-defense/skill-scanner) | Cherche les consignes cachées, les fuites de données et le code dangereux dans un dossier de skill. Contrôle « au mieux » : un résultat vide ne prouve rien | `pip install cisco-ai-skill-scanner`, puis `skill-scanner scan <dossier>` | Apache-2.0 | Actif en 2026-11 | Faible | à essayer |

## Écartés

| Projet | Raison |
|---|---|
| [jenetics/jpx](https://github.com/jenetics/jpx) (lecture GPX) | Demande Java 25 et des classes XML absentes d'Android |
| [ali-vilab/MangaNinjia](https://github.com/ali-vilab/MangaNinjia), repris seul | CC BY-NC, 6 Go de modèles, carte graphique de 6 Go : à n'essayer qu'à travers ColorComic |
| [qweasdd/manga-colorization-v2](https://github.com/qweasdd/manga-colorization-v2), pour une redistribution | Aucune licence dans le dépôt : utilisable chez soi, pas redistribuable |
| [robertknight/ocrs](https://github.com/robertknight/ocrs) (OCR en Rust) | Ne reconnaît pour l'instant que l'alphabet latin de base (anglais) ; projet jeune |
| [cyberjunky/python-garminconnect](https://github.com/cyberjunky/python-garminconnect) et [tcgoetz/GarminDB](https://github.com/tcgoetz/GarminDB) | Passent par les adresses du site Garmin Connect, pas par une interface officielle : peuvent casser ou faire bloquer le compte. Garmin publie maintenant une API personnelle officielle |
| [mvilanova/intervals-mcp-server](https://github.com/mvilanova/intervals-mcp-server) | Dépassé par `hhopke/intervals-icu-mcp` : moins d'outils, plus de commit depuis 2026-08, GPL-3.0 |
| [deptofdefense/AndroidTacticalAssaultKit-CIV](https://github.com/deptofdefense/AndroidTacticalAssaultKit-CIV) | Dépôt public sans commit depuis 2024-10 ; GPL-3.0. L'application reste une référence à regarder, pas du code à reprendre |
