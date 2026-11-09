# Fiches méthode

Une fiche méthode range un savoir-faire d'outillage déjà mis en œuvre dans une app JRRocks : comment
compiler, tester, voir ou livrer. Ce ne sont pas des moteurs : pas de `src/` à copier, le code
reste dans les tests ou les scripts de chaque app, et la fiche cite d'où il vient.

Mêmes règles que les fiches moteurs ([`AGENTS.md`](../../AGENTS.md)) : statut honnête en tête,
provenance (dépôt, commit, fichier) pour chaque affirmation, aucun secret ni donnée personnelle,
et un « Historique des retours d'usage » où l'on ajoute des lignes sans jamais en effacer.

| Fiche | À quoi ça sert | Statut |
|---|---|---|
| [`captures-compose-robolectric.md`](captures-compose-robolectric.md) | Voir un écran Compose réel en PNG depuis une session cloud, sans émulateur : Robolectric en rendu natif, gabarit de test prêt à copier, pièges de stabilité | ✅ méthode de base (Popote, rendu constaté fidèle le 2026-09-29) · 🟡 extensions du Carnet (sombre, police 130 et 200 %, 412 dp, paysage) et de Popote (plusieurs tailles dans une classe) |
| [`android-en-session-cloud.md`](android-en-session-cloud.md) | Compiler, tester et livrer une app Android depuis claude.ai/code : SDK dans le conteneur ou par hook, miroir Maven contre les 429, tests purs sans Gradle, Rust et Node, livraison d'un APK de plus de 30 Mio, pièges de CI GitHub Actions | ✅ compilation et tests en session, APK de CI installé sur téléphone · 🟡 livraison par `release/` et par pré-version |

Dernière mise à jour : 2026-10-27
