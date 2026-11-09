---
name: revue-langages
description: Passer un projet en revue, domaine par domaine (interface et graphisme, moteur de calcul, fichiers, base de données, réseau, sécurité), pour dire si le langage utilisé est le plus efficace et le plus robuste, et proposer un changement là où il se justifie - application hybride comprise, par exemple un moteur Rust sous une interface Kotlin. À utiliser quand l'utilisateur dit « revue des langages », « est-ce le bon langage », « moteur Rust », « passe en Rust », « rends l'application plus rapide » ou « plus robuste », avant d'écrire un moteur lourd, et au démarrage d'un projet pour choisir le langage de chaque domaine. Propose, ne réécrit rien sans accord.
---

# Revue des langages, domaine par domaine

## Le principe

Une application n'a pas un langage : elle en a un par domaine. L'interface, le moteur, la base et le réseau n'ont pas les mêmes besoins. Une application hybride est donc normale, pourvu que la frontière entre ses deux côtés soit nette et peu traversée.

Deux critères seulement :

- **l'efficacité** : temps, mémoire, batterie, taille du fichier livré ;
- **la robustesse** : des familles entières de défauts rendues impossibles, et le même comportement sur la machine de test et sur le téléphone.

Le goût, la mode et l'envie d'un seul langage partout ne comptent pas. Et le langage est rarement le premier frein : avant de proposer d'en changer, écarte les causes moins chères (§ 3).

## 1. Cartographier

Pour chaque domaine présent dans le projet, relève :

- où est le code (modules, taille) et dans quel langage ;
- **ce qui fait vraiment le travail** : c'est souvent déjà une bibliothèque native (la base, la caméra, la reconnaissance, la carte), pilotée par quelques lignes de Kotlin ;
- ce qui tourne à chaque image, à chaque caractère tapé, à chaque élément d'une grande liste ;
- dans la mémoire du projet : les lenteurs, les plantages et les tests instables, rangés par domaine.

Domaines : interface, graphisme et rendu, moteur de calcul, fichiers et formats, base de données, réseau et synchronisation, sécurité, image et capteurs, intégration au système, serveur, outils. Le fichier `grille-par-domaine.md` de ce skill dit, pour chacun, ce qui est le plus efficace, quand rester et quand changer.

Sur un gros dépôt, confie ce relevé à un sous-agent.

## 2. Mesurer avant de juger

- **Efficacité** : reprends ce que le projet a déjà mesuré. Sinon mesure : la durée d'une opération type sur des données réalistes, le démarrage, la mémoire, la taille du fichier livré.
- **Robustesse** : compte les défauts passés par famille (valeur absente, accès concurrent, fichier mal formé, écart entre la machine de test et Android, plantage natif).
- Sans téléphone ni émulateur, mesure le moteur seul, et dis que les chiffres sur appareil manquent.

Pas de chiffre et pas de famille de défauts nommée : le verdict est « à mesurer », jamais « à changer ».

## 3. Écarter ce qui coûte moins qu'un changement de langage

- l'algorithme et la structure des données ; un travail lourd sur le fil de l'interface ; des objets créés par milliers dans une boucle ;
- la base : un index manquant, des écritures non groupées, un tri fait par le code au lieu de la requête ;
- la livraison : une version debuggable est lente par nature. Un démarrage est déjà passé de 1,8 s à 0,4 s en livrant une version réduite et non debuggable : le code n'y était pour rien ;
- une bibliothèque native existante et éprouvée, plutôt qu'une réécriture.

## 4. Juger chaque domaine

Quatre questions :

1. **La plateforme impose-t-elle le langage ?** Interface du système, services, capteurs : on reste.
2. **Le domaine est-il limité par le calcul et la mémoire, ou par l'attente** (disque, réseau, système) ? Seul le premier gagne à changer de langage.
3. **Ce code sert-il, ou doit-il servir, à plusieurs applications ou plateformes ?** Un moteur écrit une fois pour Android, Windows, le web et un serveur vaut un changement.
4. **Quelle famille de défauts a fait mal ici**, et un autre langage la rend-elle impossible ?

Verdict par domaine : **garder**, **garder et corriger autrement** (§ 3), **changer**, ou **à mesurer**.

Un moteur Rust se justifie quand au moins un de ces cas est avéré :

- calcul lourd ou gros volumes de données ;
- analyse de fichiers venus de l'extérieur ;
- chiffrement, protocole, synchronisation ;
- moteur partagé entre plusieurs applications ou plateformes ;
- besoin du même comportement, à l'identique, en test et sur le téléphone.

Il ne se justifie pas pour du code de liaison autour des services du système, pour une logique petite et rarement appelée, ni pour ce qui change chaque semaine avec l'interface.

## 5. Compter le coût du pont

Un second langage ajoute une frontière. Chiffre-la avant de recommander :

- **les passages** : peu nombreux et par lots (un appel pour mille éléments, jamais mille appels), car chaque passage copie des données ;
- **la chaîne de compilation** : des outils en plus dans la CI et dans chaque session, un temps de compilation plus long ;
- **le fichier livré** : une bibliothèque native par architecture, de la taille en plus, des exigences du système à respecter ;
- **les plantages** : côté natif, ils doivent rester lisibles sur le téléphone ;
- **les tests** : des deux côtés, plus un test de la frontière elle-même.

Si le gain ne couvre pas ce coût, le verdict est « garder ». Quand le but est seulement de partager du code Kotlin entre plateformes, compare avec Kotlin Multiplatform, qui n'ajoute pas de frontière sur Android.

## 6. Proposer

Un rapport court, sur le gabarit `modele-rapport.md` de ce skill :

- un tableau par domaine : aujourd'hui, constat, verdict, proposition ;
- trois changements au plus, les plus rentables d'abord, chacun avec son gain attendu, son coût, son risque et son premier pas ;
- ce qu'il ne faut pas toucher, et pourquoi ;
- ce qui n'a pas pu être vérifié.

L'utilisateur décide : des choix numérotés, ta recommandation en premier. **Rien n'est réécrit pendant la revue.**

## 7. Un changement décidé se fait par petits pas

1. Une frontière d'abord : une interface étroite devant le code actuel, sans changer de langage.
2. Les tests existants deviennent le contrat : ils doivent passer sur l'ancienne version et sur la nouvelle.
3. La nouvelle version vit à côté de l'ancienne, mesurée sur les mêmes données.
4. Bascule derrière un réglage, livraison, essai sur le téléphone.
5. L'ancienne version n'est retirée qu'après un essai réel réussi.

Chaque pas donne une version installable. Jamais de réécriture d'un bloc. La décision et les mesures vont dans la mémoire du projet ; un moteur ainsi isolé est candidat à la Library.
