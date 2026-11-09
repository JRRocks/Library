# Chapitre 40 — Qualité d'une application Android et principes de navigation

> Deux référentiels officiels, utiles comme liste de contrôle avant toute publication :
> les **Core app quality guidelines** [S79] et les **Principles of navigation** [S202]. Les tests
> automatisés cités par Google (T-…) ne sont pas repris ; seules les exigences sont résumées.

## 40.1 Principe général [S79]

Les lignes directrices définissent la **qualité minimale** que toute appli doit atteindre. Elles sont à
combiner avec les lignes directrices de qualité **adaptative** (téléphones compacts, tablettes,
pliables, bureau, voiture, TV, XR ; modes fenêtrés redimensionnables). Google indique que les lignes directrices adaptatives
contribuent à un meilleur classement dans Google Play. Il est recommandé d'utiliser les **composants Material** plutôt que
les composants natifs de la plateforme, pour un rendu cohérent entre versions d'Android.

## 40.2 Expérience utilisateur [S79]

| Domaine | Exigence |
|---|---|
| Cohérence | Expérience cohérente sur tous les cas d'usage et formats |
| Cycle de vie | Mise en arrière-plan au changement d'appli, retour depuis les Récents ; pause/reprise à la mise en veille et au verrouillage |
| Orientation / pliage | Mêmes fonctions et actions dans toutes les orientations et états de pliage ; remplir la fenêtre (léger letterboxing toléré) ; transitions rapides sans bug d'affichage ni perte d'état |
| Qualité graphique | Aucune distorsion, flou ou pixellisation ; **drawables vectoriels** quand c'est possible ; graphismes adaptés à chaque taille ; pas d'aliasing sur les bords |
| Longueur de ligne | **45 à 75 caractères** (espaces compris) pour chaque langue prise en charge |
| Thème | Contenu (et contenu web) compatible **thème clair et sombre** |

## 40.3 Navigation et état [S79]

- Bouton **Retour** standard ; **aucune invite de retour personnalisée à l'écran**.
- **Geste de navigation** pour revenir et aller à l'accueil.
- **Préservation de l'état** : ne pas perdre les saisies, la progression, les menus, etc., en quittant le
  premier plan ; à la reprise depuis les Récents ou après la veille, retrouver **l'état exact**. À la
  relance depuis le lanceur : si l'usage est récent (quelques minutes), restaurer au plus près ; si
  plus ancien, restaurer ou repartir de l'accueil/état par défaut.

## 40.4 Notifications [S79]

Pertinentes pour l'appli ; **pas de promotion croisée** (interdite par le Play Store) ; **canaux de
notification** bien définis (pas un seul canal pour tout) ; bonne **priorité** ; regroupement des
notifications multiples ; délais d'expiration le cas échéant ; notifications persistantes seulement pour
des événements en cours (lecture musicale, appel). Messagerie : `MessagingStyle`, réponse directe,
raccourcis de conversation, bulles.

## 40.5 Accessibilité [S79]

- **Cibles tactiles d'au moins 48 dp** (ch. 12).
- **Contraste** : 3:1 pour grand texte et graphismes ; **4,5:1** pour petit texte (moins de 18 pt, ou
  moins de 14 pt en gras) (ch. 9, 14).
- `contentDescription` pour chaque élément d'interface, sauf `TextView`.

## 40.6 Fonctionnalités [S79]

- **Audio** : démarrer ou afficher un indicateur de préparation **en moins d'une seconde** ; demander et
  abandonner le focus audio ; gérer les demandes des autres applis ; lecture en arrière-plan via un
  service de premier plan avec notification persistante ; style `MediaStyle` ; reprise ou indication de
  pause.
- **Vidéo** : image dans l'image (PiP) ; lecture en arrière-plan.
- **Partage** : utiliser la **Sharesheet** système.
- **Services d'arrière-plan** : éviter les services longs inutiles (mauvais usages cités : maintenir une
  connexion réseau pour des notifications, une connexion Bluetooth, le GPS allumé).

## 40.7 Performance et stabilité [S79]

- **Démarrage** : rapide, ou retour visuel (indicateur de progression) au-delà de **2 secondes**.
- **Rendu** : une image toutes les 16 ms ou moins (60 i/s au moins).
- **StrictMode** : aucun avertissement rouge visible lors des tests.
- **Stabilité** : ni plantage ni blocage du fil d'interface (ANR) ; rapport de pré-lancement Google Play
  et Android Vitals après déploiement.
- **Compatibilité** : fonctionner sur la dernière version publique d'Android ; `targetSdk` et
  `compileSdk` à jour ; SDK tiers à jour ; pas d'interfaces hors SDK ; **aucune bibliothèque de débogage
  en production**.
- **Batterie** : prise en charge de Doze et App Standby.

## 40.8 Vie privée et sécurité [S79]

| Sujet | Exigence |
|---|---|
| Permissions | Minimum absolu ; position **approximative** plutôt que précise si possible |
| Sensibles | Données sensibles (SMS, journal d'appels, position) ou services payants uniquement s'ils sont liés au cœur d'usage, avec information visible ; envisager une alternative (ex. intention implicite plutôt que permission sur les contacts) |
| Moment | Demander la permission **au moment de la fonction**, pas au démarrage |
| Justification | Expliquer clairement **pourquoi** |
| Refus | **Dégradation progressive** si refus ou révocation : ne pas interdire tout l'accès |
| Stockage | Données sensibles dans le stockage interne de l'appli |
| Journaux | Aucune donnée personnelle ou sensible dans les journaux |
| Identifiants matériels | Pas d'identifiant non réinitialisable (IMEI) |
| Sauvegarde | Données, réglages et état conservés lors des sauvegardes cloud et transferts d'appareil (Auto Backup, `dataExtractionRules`) ; restauration automatique des connexions (Restore Credentials) |
| Identité | Indices d'**autofill** ; **Credential Manager** (passkeys, identité fédérée, mots de passe) ; authentification **biométrique** pour opérations financières ou documents sensibles |
| Composants | `android:exported` défini explicitement ; seuls les composants devant être appelés sont exportés ; intents explicites ; drapeau *immutable* sur les PendingIntent ; permissions personnalisées au niveau `signature` |
| Réseau | Tout le trafic chiffré ; configuration de sécurité réseau déclarée ; fournisseur de sécurité Play initialisé au démarrage |
| WebView | Ne pas utiliser `setAllowUniversalAccessFromFileURLs()` pour le contenu local ; préférer `WebViewAssetLoader` |

## 40.9 Principes de navigation Android [S202]

Ces principes valent **même sans le composant Navigation** :

1. **Destination de départ fixe** : le premier écran depuis le lanceur, et le dernier vu avant de
   retourner au lanceur avec Retour. Un assistant de configuration unique ou un écran de connexion n'est
   pas une destination de départ (ils ne sont vus que dans certains cas).
2. **L'état de navigation est une pile** (*back stack*) : la destination de départ est au fond ; naviguer
   empile, Retour dépile ; les opérations agissent sur le sommet.
3. **Haut (Up) et Retour sont identiques dans la tâche de l'appli** : Retour (barre système) remonte
   l'historique chronologique ; Up (barre d'appli) se comporte pareillement dans la tâche.
4. **Up ne quitte jamais l'appli** : absent sur la destination de départ, alors que Retour y est affiché et
   quitte l'appli. Lancée par un lien profond depuis la tâche d'une autre appli, Up ramène dans la tâche
   de l'appli (pile simulée) tandis que Retour ramène à l'autre appli.
5. **Le lien profond simule une navigation manuelle** : il remplace la pile existante de la tâche par une
   **pile synthétique réaliste** (équivalente à une navigation organique) ; pour un graphe simple, la
   destination de départ plus la destination ciblée.

Cohérence avec le chapitre 39 : le comportement de Retour est un modèle mental très ancré ; lui rester
fidèle évite les surprises.

## 40.10 Check-list rapide avant publication

- [ ] Retour/geste de retour standard, pile cohérente, état préservé.
- [ ] Cibles ≥ 48 dp, contrastes ≥ 4,5:1 / 3:1, descriptions de contenu.
- [ ] Thèmes clair et sombre ; lignes de 45–75 caractères.
- [ ] Permissions minimales, demandées en contexte, avec repli en cas de refus.
- [ ] Rien de sensible dans les journaux ni en stockage partagé ; trafic chiffré.
- [ ] Retour visuel au-delà de 2 s de démarrage ; 60 i/s ; aucun ANR.
- [ ] Aucune bibliothèque de débogage dans la version de production.

---
*Sources du chapitre : S79, S202.*
