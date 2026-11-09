# Licences : ce qu'on a le droit de faire

Repères pour trier. Ce n'est pas un avis juridique : dans le doute, on pose la question à l'utilisateur et on ne reprend rien.

Les obligations d'une licence jouent quand on **distribue** : un APK publié, un dépôt public. Un outil utilisé seulement chez soi n'en déclenche pas.

## Le code

| Licence | Reprendre du code dans une de ses applications | À faire |
|---|---|---|
| MIT, BSD, ISC | Oui, partout | Garder la mention de l'auteur et le texte de la licence |
| Apache-2.0 | Oui, partout | Garder la licence et le fichier `NOTICE`, signaler les fichiers modifiés |
| MPL-2.0 | Oui | Les fichiers repris restent sous MPL et leurs modifications se publient ; le reste de l'application garde sa licence |
| LGPL | Comme bibliothèque, avec précaution | Les modifications de la bibliothèque se publient ; sur Android, la condition « bibliothèque remplaçable » est difficile à tenir : demander |
| GPL-2.0, GPL-3.0 | Seulement si l'application entière passe sous GPL | Publier le code source de l'application distribuée. Une application déjà sous GPL peut reprendre du code GPL |
| AGPL-3.0 | Comme la GPL, et l'obligation vaut aussi pour un service en ligne | Publier le code même si le logiciel ne tourne que sur un serveur |
| Aucune licence | Non | Tous droits réservés par défaut : on peut lire, pas reprendre |
| « Source disponible » (BSL, SSPL, clause Commons) | Non, sauf lecture attentive | Ce ne sont pas des licences libres : usage commercial ou hébergement souvent interdits |

Trois cas qui reviennent :

- **Un outil GPL lancé à côté** (un serveur séparé, un programme appelé par le réseau) n'impose rien à l'application qui l'appelle. C'est reprendre son code qui engage.
- **Un fork** garde la licence du projet d'origine. Entre forks du même projet, le code se reprend avec la mention de son auteur.
- **Du code Apache-2.0 peut entrer dans un projet GPL-3.0**, pas l'inverse.

## Les modèles, les images, les textes

| Ce que c'est | À regarder |
|---|---|
| Poids d'un modèle | Ils ont souvent leur propre licence, différente de celle du code : « recherche seulement » ou « non commercial » sont fréquents |
| Images, icônes, textes | Creative Commons : BY (citer l'auteur), BY-SA (même licence pour ce qui en dérive), BY-NC (pas d'usage commercial), BY-ND (pas de modification) |
| Polices | OFL : utilisable et intégrable, à ne pas revendre seule |
| Données | Licence propre au jeu de données ; les conditions d'utilisation d'un site valent aussi pour ce qu'on y récupère |

« Non commercial » convient à un usage personnel. Dès qu'une application est vendue, ou sert une activité payée, il faut demander.

## À noter à chaque reprise

Le dépôt, le commit ou la version, les fichiers repris, la licence, et où sa mention figure dans l'application.
