# Grille par domaine

Pour chaque domaine : ce qui est le plus efficace et le plus robuste aujourd'hui, quand rester, quand changer, et les pièges. Ce sont des repères : la mesure faite dans le projet passe avant.

Les noms d'outils et leurs versions bougent vite. Avant de proposer un outil précis, relis sa documentation à jour.

## Interface

| | |
|---|---|
| **Le plus efficace** | L'outil de la plateforme : Kotlin et Jetpack Compose sur Android, TypeScript et son cadre sur le web. Il apporte ce qu'on ne voit pas : accessibilité, clavier, thème, sélecteurs du système, mises à jour suivies |
| **Rester** | Presque toujours |
| **Changer** | Quand la même interface doit tourner sur Android et sur Windows sans être écrite deux fois : Compose Multiplatform (on reste en Kotlin), ou une interface Rust (Slint) quand le moteur est déjà en Rust |
| **Pièges** | Une interface qui n'est pas celle du système doit refaire à la main ce qu'il donne : sélecteur de fichiers, ouverture du navigateur, partage, clavier, retour arrière |

## Graphisme et rendu

| | |
|---|---|
| **Le plus efficace** | Dessin courant : le canevas du système. Cartes, 3D, gros volumes : un moteur natif existant, piloté depuis Kotlin. Effets au pixel : un shader, exécuté par la carte graphique. Rendu identique sur plusieurs plateformes : Rust avec une bibliothèque graphique portable |
| **Rester** | Tant que l'affichage tient sa cadence. Se mesure en images lentes, pas à l'œil |
| **Changer** | Calcul fait pixel par pixel sur le processeur : shader ou code natif. Géométrie lourde recalculée à chaque image : la calculer une fois, dans le moteur |
| **Pièges** | Traverser la frontière entre langages à chaque image ou pour chaque point |

## Moteur de calcul

Géométrie, itinéraires, statistiques, tri et recherche sur de gros volumes, génération de documents, compression.

| | |
|---|---|
| **Le plus efficace** | Rust : pas de ramasse-miettes, donc des temps réguliers et une mémoire maîtrisée ; calcul en parallèle sans accès concurrent involontaire ; le même moteur pour Android, Windows, le web (WebAssembly) et un serveur. C ou C++ seulement pour reprendre une bibliothèque qui existe déjà |
| **Rester** | Logique petite, appelée rarement, déjà rangée dans un module Kotlin pur qui se teste sans appareil |
| **Changer** | Opération longue sur des données réalistes (des centaines de millisecondes et plus), pauses visibles, mémoire qui gonfle, moteur à partager entre applications |
| **Pièges** | Une frontière trop fine (mille petits appels). Deux copies du même calcul qui finissent par diverger |

## Fichiers et formats

Lecture et écriture de traces, de tableurs, de CSV, de PDF, d'archives, de formats maison.

| | |
|---|---|
| **Le plus efficace** | Rust pour analyser un fichier venu de l'extérieur : une entrée mal formée ne peut pas corrompre la mémoire, et les erreurs font partie des types : un résultat ne s'utilise pas sans avoir traité le cas d'échec |
| **Rester** | Petits fichiers écrits par l'application elle-même |
| **Changer** | Fichiers d'origine inconnue, fichiers volumineux, ou même format lu par plusieurs applications |
| **Pièges** | Les expressions régulières : la machine de test et Android n'ont pas le même moteur, et une formule acceptée par l'une a déjà fait planter l'autre. Un moteur embarqué avec l'application répond pareil partout |

## Base de données

| | |
|---|---|
| **Le plus efficace** | Le langage efficace, ici, c'est SQL. SQLite fait le travail, et il est écrit en C. Contraintes, index et transactions portent les règles : une règle portée par la base ne peut pas être contournée par le code. Côté Kotlin, Room vérifie les requêtes à la compilation |
| **Rester** | Presque toujours : on corrige le schéma, l'index ou la transaction, pas le langage |
| **Changer la couche d'accès** | Quand le moteur qui écrit dans la base est en Rust, ou partagé entre plateformes : une seule couche d'accès et une seule suite de migrations |
| **Pièges** | Deux langages qui écrivent dans la même base sans propriétaire unique. Des migrations tenues en double |

## Réseau et synchronisation

| | |
|---|---|
| **Le plus efficace** | Transport : la bibliothèque de la plateforme, qui gère certificats, proxy et changements de réseau. Protocole, fusion, file d'attente, formats d'échange : Rust quand la logique est délicate (états, conflits, chiffrement) ou partagée avec un serveur, écrite sans entrée ni sortie pour se tester sans réseau |
| **Rester** | Appels simples à une API |
| **Changer** | Synchronisation entre appareils, protocole maison, échanges de pair à pair |
| **Pièges** | Refaire en natif ce que le système fait mieux : état de la connexion, tâches de fond, reprise après coupure |

## Sécurité et chiffrement

| | |
|---|---|
| **Le plus efficace** | Jamais d'algorithme maison. La garde des clés reste au coffre du système, donc en Kotlin. Chiffrement, signatures, chaînes de hachage : une bibliothèque auditée, en Rust si le moteur l'est, sinon celle de la plateforme |
| **Changer** | La même logique de chiffrement doit tourner sur plusieurs plateformes |
| **Pièges** | Une clé qui sort du coffre pour traverser la frontière. Deux écritures du même chiffrement à garder compatibles |

## Image, caméra, reconnaissance

| | |
|---|---|
| **Le plus efficace** | Capture : l'API du système. Reconnaissance de texte et de codes : des bibliothèques déjà natives. La logique autour (anti-rebond, consensus de lectures) est petite |
| **Rester** | Dans le cas courant : le travail lourd est déjà fait en natif |
| **Changer** | Traitement maison image par image (filtre, recadrage, conversion) écrit en Kotlin : code natif ou carte graphique |
| **Pièges** | Copier chaque image d'un côté à l'autre de la frontière |

## Intégration au système

Services, notifications, widgets, autorisations, NFC, téléphonie, tâches planifiées, mise à jour de l'application.

Le langage de la plateforme, toujours. Rien à gagner ailleurs.

## Serveur

| | |
|---|---|
| **Le plus efficace** | Petit service personnel : le langage où la logique existe déjà. Service qui doit tenir la charge, ou qui partage le moteur de l'application : Rust |
| **Pièges** | Réécrire un serveur qui n'a pas de problème de charge |

## Outils, scripts, compilation automatique

Bash, PowerShell, Python : on reste. La robustesse y vient des tests et des contrôles, pas du langage.

## Les ponts entre langages

| Pont | Comment | À savoir |
|---|---|---|
| Kotlin et Rust | Une bibliothèque native par architecture, chargée par l'application. Liaisons écrites à la main (JNI) ou générées à partir du Rust (UniFFI). Compilation croisée lancée par Gradle (`cargo-ndk`) | Demande le NDK Android : c'est la compilation automatique qui produit la bibliothèque |
| Web et Rust | WebAssembly | Pour le calcul, pas pour l'interface |
| Python et Rust | Module natif | |
| Tout en Rust | Moteur et interface (Slint), avec une coque minimale par plateforme | L'interface se rend en image hors écran : la mise en page se vérifie sans appareil |
| Kotlin Multiplatform | Le même Kotlin sur plusieurs plateformes, sans frontière sur Android | À préférer quand le but est le partage, pas la vitesse de calcul |

Règles d'un bon pont :

- peu d'appels, et gros ;
- des types simples à la frontière ;
- les erreurs sont traduites d'un côté à l'autre : un plantage ne traverse jamais ;
- un seul propriétaire par donnée ;
- le moteur ne connaît pas l'interface.

À vérifier dans la documentation du moment, avant de proposer : les outils de la chaîne de compilation, les architectures à livrer, et les exigences d'Android sur les bibliothèques natives (alignement mémoire sur 16 Ko pour les applications qui visent les versions récentes).

## Ce qu'une session peut vérifier sans téléphone

- Un moteur Rust se compile et se teste dans une session cloud sans rien installer (`cargo test`, constaté le 2026-11-09).
- Un module Kotlin pur se teste de la même façon, par Gradle.
- La bibliothèque Android et le pont lui-même ne se vérifient que dans l'application : compilation automatique, puis téléphone.

## D'où viennent ces repères

| Repère | Source |
|---|---|
| Démarrage passé de 1,8 s à 0,4 s par une version réduite et non debuggable | SuivieMAT, `consignes/outils.md` § 7 (2026-10-28) |
| Expression régulière acceptée par la machine de test et refusée par Android | ToolMap, `CLAUDE.md` (plantage du 2026-10-03, `RegexPortabilityTest`) |
| Règles métier portées par la base plutôt que par le code | ScanList et SuivieMAT, `CLAUDE.md` |
| Moteur et interface Rust avec une coque Android, rendu hors écran | InnovAdapt-CRM, branche `claude/android-rust-mobile`, `RUST.md` : `cargo ndk`, pont JNI, 82 tests ; d'après ce fichier, le premier lancement sur Android restait à valider |
| Bibliothèques natives et pages de 16 Ko | [developer.android.com/guide/practices/page-sizes](https://developer.android.com/guide/practices/page-sizes) |
| Liaisons Kotlin générées à partir du Rust | [docs.rs/crate/uniffi](https://docs.rs/crate/uniffi/latest) |
