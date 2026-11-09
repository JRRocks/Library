# Chapitre 35 — Authentification, mots de passe et murs de connexion

> La connexion est un des rares moments où **sécurité et usabilité** se heurtent frontalement. Les
> règles ci-dessous (NN/g, W3C) permettent de ne sacrifier ni l'une ni l'autre.

## 35.1 Créer un mot de passe : trois façons de faciliter (NN/g, 2015) [S198]

Constat : créer un mot de passe est « presque exactement la même expérience qu'il y a vingt ans » ;
les **gestionnaires de mots de passe** et la **biométrie** sont les exceptions notables.

### 1. Montrer les règles
Sans règles visibles à l'avance, l'utilisateur joue à un jeu dont **le règlement est caché**
(séquence d'erreurs successives : majuscule, chiffre, caractère spécial, pas d'espace, pas de
séquence…, jusqu'à l'abandon). **Solution** : énoncer les exigences et les **garder visibles tant que
le champ est actif** ; un simple lien vers les exigences n'est pas suffisant ; **ne pas les mettre dans
le libellé** ni en placeholder. Réduire les restrictions techniques donne moins de texte et de règles à
traiter.

### 2. Montrer la saisie
Avec des exigences complexes, l'utilisateur **invente à la volée** une chaîne conforme — qui n'est plus
« son » mot de passe — et masquée, ce qui charge la mémoire à court terme et **cache les fautes de
frappe** (plus nombreuses sur petits écrans). **Solution** : permettre de **démasquer** :
- **Ordinateur** : masqué par défaut, avec une case **« Afficher le mot de passe »** ;
- **Mobile/tablette** : **affiché par défaut**, avec un contrôle « Masquer » (plus facile de se
  protéger des regards, et on crée moins souvent des comptes très sensibles sur téléphone).

### 3. Montrer la force
Des règles de composition détaillées donnent des mots de passe **faciles à deviner** (on se conforme
sans vraiment sécuriser). **Un indicateur de force motive** à en créer de meilleurs (étude Egelman
et al., 2013) ; atteindre la barre pleine procure une satisfaction (« gamification pour le bien »). Un
**indicateur de progression vers la conformité** (liste de critères cochés) n'est **pas** un indicateur
de force.

### Plaider pour des règles plus simples
Les équipes de sécurité imposent souvent des règles strictes ; or une étude de Carnegie Mellon (Shay
et al., 2014) montre que « certaines combinaisons d'exigences [de longueur] étaient à la fois **plus
fortes et plus utilisables** que la politique complexe traditionnelle ». Les utilisateurs de mots de
passe complexes les stockent souvent de façon peu sûre (post-it, tiroir, fichier). Pour les équipes
produit : les murs de connexion et exigences complexes **font fuir** (conversions plus faibles).

## 35.2 Les murs de connexion (login walls) [S199]

Un mur de connexion exige de se connecter ou de s'inscrire **avant** de continuer. NN/g : « Nous avons
rarement vu des utilisateurs plus agacés ». Coût d'interaction élevé : se souvenir de ses identifiants ou
créer un compte.

| Situation | Verdict |
|---|---|
| **Application très personnelle** (e-mail, banque) | Justifié (protection contre les intrus) |
| **Site visité rarement** | Nuisance |
| **Mur à l'arrivée** (avant tout contenu) | **À éviter** (déconseillé depuis 1999) : l'utilisateur ne peut pas estimer la valeur du service ; il lui est **« à deux touches » de supprimer l'app** ; « il n'y aura peut-être pas de prochaine fois » |
| **Aperçu ou visite guidée** | Mieux que rien, mais vague ; l'utilisateur « devine bas » |
| **Connexion tardive** (au moment de l'achat/de l'enregistrement) | **Recommandée** : le contenu d'abord, la connexion quand elle est nécessaire (ex. oDesk vs Task Rabbit ; Gilt) |

Principe sous-jacent : **réciprocité** — donner de la valeur avant de demander quoi que ce soit
[S199].

## 35.3 Les exigences d'authentification accessible (WCAG 2.2) [S92]

- **3.3.8 Authentification accessible (minimum)**, niveau AA : la connexion ne doit pas exiger de
  **mémoriser, résoudre un casse-tête ou retaper un code**, sauf alternative ou aide (**gestionnaire de
  mots de passe**, **collage autorisé**).
- **3.3.9 (renforcé)**, AAA : pas de test cognitif, même reconnaître des images fournies par
  l'utilisateur.
- **3.3.7 Pas de ressaisie** : ne pas redemander une information déjà fournie dans le même processus.
- Attributs `autocomplete` appropriés (`username`, `current-password`, `new-password`, `email`) pour que
  les gestionnaires fonctionnent [S133].

## 35.4 Application aux apps Android hors ligne (cas fréquent des apps JRRocks)

1. **Pas de compte** quand il n'est pas nécessaire (app 100 % hors ligne, comme ScanList) : pas de mur.
2. Si un compte est utile (sauvegarde Drive, synchronisation) : **le demander au moment du besoin**
   (« Sauvegarder sur Google Drive »), pas au premier lancement ; permettre de **continuer sans**.
3. Utiliser les **API d'identité de la plateforme** (Google Identity, *Credential Manager*, biométrie)
   plutôt que des mots de passe maison (voir la fiche `google-drive-appdata` de la Library).
4. **Jamais de secret ni de donnée personnelle** dans le code ou dans le dépôt public (règle 1 de
   `AGENTS.md`).
5. Afficher clairement **ce qui est stocké, où, et comment l'effacer**.

## 35.5 Check-list
- [ ] Règles de mot de passe visibles pendant la saisie ; option d'affichage.
- [ ] Indicateur de force plutôt que liste d'obligations arbitraires ; règles simplifiées si possible.
- [ ] Pas de mur de connexion avant la valeur ; connexion tardive.
- [ ] Gestionnaire de mots de passe et collage autorisés ; `autocomplete` correct.
- [ ] Alternative à tout test cognitif ; pas de ressaisie.

---
*Sources du chapitre : S92, S133, S198, S199.*
