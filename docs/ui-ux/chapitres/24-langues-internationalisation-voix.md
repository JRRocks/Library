# Chapitre 24 — Langues, internationalisation et interfaces vocales

## 24.1 Internationalisation (i18n) et localisation (l10n) [S160]

- **Internationalisation** : concevoir une application pour qu'elle puisse être adaptée à différentes
  langues et régions **sans modification d'ingénierie**. **Localisation** : adapter une application
  déjà internationalisée à une région ou une langue (traduction + éléments propres à la région).
  L'internationalisation se fait idéalement **une fois**, avant la localisation (qui se répète par
  région).
- Les abréviations **i18n** et **l10n** viennent du nombre de lettres entre la première et la dernière
  (18 et 10) ; usage forgé chez Digital Equipment Corporation dans les années 1970-1980.

### Ce qui varie d'une région à l'autre [S160]

| Domaine | Exemples |
|---|---|
| **Texte** | Traduction (le poste le plus long) ; **longueurs différentes** et **tailles de caractères différentes** (latin vs caractères chinois) → des mises en page qui marchent dans une langue peuvent échouer dans une autre ; dialecte, registre |
| **Nombres** | **Séparateur décimal** et **groupement des chiffres** |
| **Dates et heures** | Formats, calendriers différents (islamique, japonais) |
| **Autres conventions** | Formats de téléphone, d'adresse postale, codes postaux ; **monnaie** (symbole, position, codes ISO 4217) ; système de mesure ; formats de papier |
| **Réglementation** | Conformités légales et normes techniques locales |
| **Services tiers** | Cartes, météo, paiements pas toujours disponibles partout |

Les différences très régulières s'automatisent via le **CLDR** (*Common Locale Data Repository*) du
Consortium Unicode, utilisé par Windows, macOS, Debian, Google, la Fondation Wikimedia [S160].
Les systèmes modernes utilisent **Unicode**. Une méthode de test : la **pseudo-localisation**
(vérifier qu'un logiciel est prêt pour la localisation) [S160].

### Règles de conception « i18n-ready »
1. **Aucun texte en dur** dans le code : tout passe par des ressources (sur Android : `strings.xml`).
2. **Prévoir l'allongement** : conteneurs de texte extensibles (pas de hauteur fixe — cf. WCAG 1.4.12).
3. **Formater avec les API de la plateforme** (nombres, dates, devises) selon la *locale*.
4. **Pas de texte dans les images.**
5. **Pluriels et accords** gérés par le mécanisme de la plateforme (pas de concaténation).
6. **Déclarer la langue** du contenu (WCAG 3.1.1, 3.1.2) pour que les lecteurs d'écran prononcent
   correctement.
7. **Icônes universelles** (enveloppe plutôt que boîte aux lettres) [S147].

## 24.2 Écritures de droite à gauche (RTL)

NN/g a observé en 2017 que, dans les langues écrites de droite à gauche comme l'**arabe**, le motif
de lecture en F est **inversé** (miroir) [S35]. Conséquences de conception (synthèse) : en RTL, la
**mise en page entière se reflète** (navigation, icônes directionnelles comme « retour » et
« suivant », alignements, progressions), mais **pas tout** (chiffres, logos, lecteur multimédia,
icônes non directionnelles). Sur Android, on déclare la prise en charge RTL et on utilise les
attributs `start`/`end` plutôt que `left`/`right` (usage standard de la plateforme).

## 24.3 Interfaces vocales (VUI) [S161]

Une interface vocale permet une interaction parlée avec les machines : reconnaissance vocale pour
comprendre les commandes, synthèse vocale pour répondre. Intérêt : **mains libres, yeux libres**.

Points de conception [S161] :
- **Fiabilité d'abord** : des performances irrégulières entraînent moins d'engagement et des retours
  négatifs.
- **Pluridisciplinarité** : informatique, linguistique, facteurs humains (psychologie).
- **Plus le VUI colle au modèle mental** de la tâche, plus il est facile et efficace.
- **Grand public** : simplicité et beaucoup d'aide pour un premier appel. **Utilisateurs experts**
  (techniciens de terrain) : productivité, **moins d'invites**, dialogues « à initiative mixte »
  (plusieurs informations dans un seul énoncé, dans n'importe quel ordre).
- **La parole convient aux transactions rapides et routinières** (changer le statut d'un ordre de
  travail, saisir un temps) ; les demandes **complexes** réussissent moins avec le grand public ;
  certaines situations exigent un agent humain.
- Méthode de prototypage adaptée : le **« magicien d'Oz »** (chap. 18, [S124]).

## 24.4 Check-list
- [ ] Zéro chaîne en dur ; ressources par langue.
- [ ] Mise en page testée avec textes plus longs (pseudo-localisation).
- [ ] Dates, nombres, devises formatés par la locale.
- [ ] Langue déclarée ; RTL prévu si nécessaire.
- [ ] Alternative non vocale à toute fonction vocale (et inversement).

---
*Sources du chapitre : S35, S124, S147, S160–S161.*
