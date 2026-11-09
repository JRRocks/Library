---
name: livraison
description: Terminer un changement par quelque chose que l'utilisateur peut installer ou ouvrir depuis son téléphone - APK Android, page web, exécutable - avec un numéro de version nouveau, une compilation vérifiée et le lien ou le fichier dans le message final. À utiliser à la fin de tout changement qui touche une application, avant de pousser, et quand l'utilisateur dit « donne-moi l'APK », « où est l'APK », « compile », « livre », « nouvelle version », « publie » ou « je peux tester ? ».
---

# Livrer

L'utilisateur ne lit pas le code : il essaie l'application sur son téléphone. Un changement n'existe pour lui que lorsqu'il peut l'installer. Il n'a jamais à demander le fichier, ni à le chercher.

Les consignes du projet disent **par quel canal** livrer. Ce skill donne l'ordre des opérations et les règles qui valent partout.

## 1. Vérifier avant de livrer

- Lance la commande de vérification du projet (tests, lint, compilation). Rien ne part si elle échoue.
- Si la session ne peut pas compiler (pas de SDK, pas de réseau), dis-le et laisse la CI compiler. Ne présente jamais comme compilé ce qui ne l'a pas été.

## 2. Monter la version

- Chaque livraison porte une version nouvelle (`versionCode` et `versionName`, ou le fichier de version du projet). Jamais deux fichiers avec le même numéro, même pour un changement minuscule.
- Si le projet monte sa version tout seul à la compilation, ne la change pas à la main.
- Committe le fichier de version avec le code.

## 3. Compiler et attendre le résultat

- **Par la CI** : pousse, puis attends la fin de l'exécution du commit poussé (outils GitHub Actions, sans boucle d'attente). Rouge : lis le journal de la tâche en échec, corrige, revérifie, repousse, jusqu'au vert.
- **En local** (PC) : compile, puis range l'APK dans le dossier Drive des APK sous le nom `<NomApp>.<version>.apk`.
- Trois échecs qui ne viennent pas du code, à dire tels quels plutôt que de relancer :
  - exécution refusée pour une raison de facturation ou de quota de minutes ;
  - exécution qui reste « en attente » alors que la CI tourne sur le PC de l'utilisateur : son exécuteur n'est pas lancé ;
  - téléversement de l'artefact refusé pour cause de quota.

## 4. Remettre le fichier ou le lien

Par le canal du projet. Les quatre qui existent :

| Canal | Ce que tu donnes |
|---|---|
| Artefact ou pré-version de la CI | le lien de téléchargement |
| APK déposé dans la branche par la CI | le lien de la page du fichier, et si l'APK correspond déjà au dernier commit ou si la CI compile encore |
| Fichier envoyé dans la conversation | le fichier, avec une ligne qui nomme le changement |
| Dossier Drive des APK (PC) | le chemin du fichier |

Sans consigne du projet : donne un lien qui s'ouvre depuis un téléphone connecté à GitHub.

Ne renvoie pas un fichier déjà remis plus tôt dans la conversation.

Un APK dépasse vite la taille qu'une session cloud peut envoyer en pièce jointe : dans ce cas, un lien. Les montages déjà éprouvés (APK déposé dans la branche, pré-version, clé commune) sont décrits dans la Library : `~/.claude/library/docs/methodes/android-en-session-cloud.md`, § 5.

## 5. Ne jamais casser l'installation

- L'APK doit s'installer **par-dessus** la version précédente. Il faut pour cela la même clé de signature et le même identifiant d'application, toujours. Sinon Android impose de désinstaller, et les données partent avec.
- Ne change donc jamais la clé, l'`applicationId` ni le type de compilation livré sans l'avoir dit à l'utilisateur et obtenu son accord.
- N'installe jamais une version de debug sur un téléphone qui porte la version en service : la signature diffère.
- Dans un dépôt public, une clé de debug versionnée est publique : n'importe qui peut signer un fichier qui s'installe par-dessus. Elle ne signe jamais une application distribuée.
- Un APK de debug non réduit peut être bloqué au téléchargement par un antivirus en ligne. Quand le projet livre une version réduite (R8), essaie-la avant de la remettre : la réduction peut casser ce que le debug ne montre pas.

## 6. Application utilisée par d'autres personnes

- Tout reste privé par défaut : dépôt privé, version d'essai privée.
- La publication n'a lieu que sur un feu vert explicite, donné pour cette version-là. Un accord ne vaut pas pour la version suivante. Dans le doute : ne publie pas, demande.
- Avant de publier, vérifie que la version publiée est bien celle que l'utilisateur a essayée.
- Mise à jour intégrée et publication en deux temps (essai privé, puis public) : le moteur `maj-auto-github` de la Library les fournit.
- Avant une première distribution : clé de signature de publication créée et sauvegardée hors du dépôt, identifiant d'application définitif, licences des projets repris respectées (nom, icône, identifiant, mise à jour intégrée).

## 7. En cas d'échec

Ne remets rien de cassé en silence. Dis ce que montre le journal de la tâche en échec, en une ou deux lignes, et ce que tu fais ensuite.

## 8. Le message final

- le nom de l'application et sa version ;
- le lien ou le fichier ;
- ce qui a changé, en mots de tous les jours ;
- ce qu'il doit essayer sur son téléphone, et ce que tu n'as pas pu vérifier ;
- l'état des sauvegardes quand le projet le demande (miroir Drive fait, ou à faire au retour sur le PC).

Une session sans changement de code donne quand même le lien de la dernière version livrée, quand le projet le prévoit.
