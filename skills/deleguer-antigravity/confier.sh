#!/usr/bin/env bash
# Confier une tache a Antigravity (agy), avec les garde-fous du pont Claude -> Antigravity.
#
#   bash confier.sh [--lecture | --code <nom>] [--projet <dossier>] [--delai <secondes>] <consigne.txt | ->
#
#   --lecture     (par defaut) Antigravity lit une copie du projet et repond ; il ne modifie rien.
#   --code <nom>  Antigravity travaille dans une copie du projet (git worktree), sur la branche
#                 antigravity/<nom> : il modifie et compile ; Claude relit, essaie et livre.
#   --projet      dossier du projet (par defaut : le dossier courant).
#   --delai       duree maximale de la tache, en secondes (par defaut 1200).
#   consigne      fichier qui contient la tache, ou - pour la lire sur l'entree standard.
#
# Garde-fous, quel que soit le profil :
#   - Antigravity ne travaille jamais dans le dossier du projet : il recoit une copie (git
#     worktree) ; la copie de lecture part de l'etat courant du projet, modifications non
#     commitees comprises ; la copie de code part du dernier commit ;
#   - mode accept-edits donne explicitement ; jamais --dangerously-skip-permissions ;
#   - bac a sable du terminal (--sandbox) : sans reseau, ecriture seulement dans la copie ;
#   - reglages de cette seule session (garde-fous.json ; rien n'est ecrit dans les reglages
#     d'Antigravity) : push, gh, publication, suppression, reseau, web et commandes git qui
#     modifient le depot refuses ; seules la lecture git, la compilation et les tests peuvent
#     sortir du bac a sable ; une autre sortie, que le mode sans ecran ne peut pas faire
#     accepter, arrete la tache ;
#   - profil lecture : aucune ecriture de fichier par les outils d'Antigravity ;
#   - environnement : un git push lance par un script echoue (pushInsteadOf vers une adresse
#     invalide), gh n'a pas de jeton valable, aucune fenetre d'identification ne s'ouvre ;
#   - en tete de la tache : la date, ce qui est permis et ce qui est refuse.
# Le compte rendu donne la reponse, les refus, les fichiers modifies, et verifie qu'aucune
# branche distante n'a bouge et que le dossier du projet n'a pas change.
#
# Codes de sortie : 0 tache faite ; 2 a regarder (refus, reponse vide, echec) ;
# 1 lancement impossible. Messages sans accents pour s'afficher partout.

set -u
ici="$(cd "$(dirname "$0")" && pwd)"
profil=lecture; nom=""; projet="$PWD"; delai=1200; consigne=""

while [ $# -gt 0 ]; do
  case "$1" in
    --lecture) profil=lecture; shift;;
    --code) profil=code; nom="${2:-}"; shift 2 || shift;;
    --projet) projet="${2:-}"; shift 2 || shift;;
    --delai) delai="${2:-}"; shift 2 || shift;;
    -h|--help) sed -n '2,34p' "$0"; exit 0;;
    -) consigne="-"; shift;;
    -*) echo "Option inconnue : $1"; exit 1;;
    *) consigne="$1"; shift;;
  esac
done

winpath() { if command -v cygpath >/dev/null 2>&1; then cygpath -w "$1"; else printf '%s' "$1"; fi; }

# --- 1. Ce qu'il faut pour lancer ---------------------------------------------
agy="$(command -v agy 2>/dev/null || true)"
if [ -z "$agy" ] && [ -n "${LOCALAPPDATA:-}" ] && command -v cygpath >/dev/null 2>&1; then
  c="$(cygpath -u "$LOCALAPPDATA")/agy/bin/agy.exe"
  [ -x "$c" ] && agy="$c"
fi
[ -n "$agy" ] || { echo "Antigravity (agy) introuvable sur ce poste."; exit 1; }
[ -f "$ici/garde-fous.json" ] || { echo "garde-fous.json introuvable a cote du script."; exit 1; }
case "$delai" in ''|*[!0-9]*) echo "Delai invalide : $delai"; exit 1;; esac

if [ "$consigne" = "-" ]; then texte="$(cat)"
elif [ -n "$consigne" ] && [ -f "$consigne" ]; then texte="$(cat "$consigne")"
else echo "Donne la tache : un fichier, ou - pour l'entree standard."; exit 1; fi
[ -n "$(printf '%s' "$texte" | tr -d '[:space:]')" ] || { echo "La consigne est vide."; exit 1; }

projet="$(cd "$projet" 2>/dev/null && pwd)" || { echo "Dossier du projet introuvable."; exit 1; }
racine="$(git -C "$projet" rev-parse --show-toplevel 2>/dev/null || true)"
sous_dossier="$(git -C "$projet" rev-parse --show-prefix 2>/dev/null || true)"
base="${TMPDIR:-/tmp}/antigravity"
run="$base/$(date +%Y%m%d-%H%M%S)-$$-${nom:-lecture}"
mkdir -p "$run" "$base/copies" || { echo "Impossible de creer $run"; exit 1; }

# --- 2. Copie de travail et reglages -------------------------------------------
note=""; branche=""; copie=""
if [ "$profil" = code ]; then
  [ -n "$racine" ] || { echo "Le profil code demande un depot git."; exit 1; }
  case "$nom" in ''|-*|*[!a-z0-9-]*) echo "Nom invalide (minuscules, chiffres, tirets) : $nom"; exit 1;; esac
  branche="antigravity/$nom"
  copie="$base/copies/$(basename "$racine")-$nom"
  if git -C "$racine" show-ref --verify --quiet "refs/heads/$branche"; then
    echo "La branche $branche existe deja : choisis un autre nom."; exit 1
  fi
  [ -e "$copie" ] && { echo "Le dossier $copie existe deja."; exit 1; }
  git -C "$racine" worktree add -q -b "$branche" "$copie" HEAD \
    || { echo "Copie de travail impossible (git worktree)."; exit 1; }
  [ -n "$(git -C "$racine" status --porcelain)" ] \
    && note="le projet a des modifications non commitees ; elles ne sont pas dans la copie d'Antigravity."
  permis="Ce dossier est une copie du projet, sur la branche $branche. Tu peux lire et modifier ses fichiers, et lancer les commandes de compilation et de test du projet : Claude relira ton travail et le livrera."
  cp "$ici/garde-fous.json" "$run/reglages.json"
else
  if [ -n "$racine" ]; then
    instant="$(git -C "$racine" stash create 2>/dev/null || true)"
    copie="$base/copies/$(basename "$racine")-lecture-$$"
    git -C "$racine" worktree add -q --detach "$copie" "${instant:-HEAD}" \
      || { echo "Copie de lecture impossible (git worktree)."; exit 1; }
    [ -n "$(git -C "$racine" ls-files --others --exclude-standard | head -n 1)" ] \
      && note="les fichiers jamais ajoutes a git ne sont pas dans la copie d'Antigravity."
  else
    note="pas un depot git : Antigravity lit le dossier du projet lui-meme."
  fi
  permis="Tu lis ce dossier sans rien y modifier : ta reponse ecrite est le resultat attendu."
  sed 's/"deny": \[/"deny": [ "write_file(*)",/' "$ici/garde-fous.json" > "$run/reglages.json"
  grep -q '"write_file(\*)"' "$run/reglages.json" || { echo "Reglages de lecture impossibles a preparer."; exit 1; }
fi
if [ -n "$copie" ]; then travail="$copie/$sous_dossier"; else travail="$projet"; fi

# --- 3. La tache, garde-fous en tete -------------------------------------------
invite="Nous sommes le $(date +%Y-%m-%d). Claude Code te confie cette tache (pont Claude -> Antigravity).

Garde-fous de cette tache :
- $permis
- Pour lire, chercher et modifier des fichiers, prefere tes outils de fichiers au terminal. Lance tes commandes dans le bac a sable ; n'en sors que pour compiler ou tester le projet.
- Tu ne pousses, ne publies et ne supprimes rien : pas de git push, pas de gh, pas de publication ni de deploiement, pas de suppression de fichier ou de branche, pas d'envoi sur le reseau. Si la tache le demande, ne le fais pas : ecris-le dans ta reponse, Claude s'en chargera.
- Ne lance aucune commande git qui modifie le depot (add, commit, checkout, switch, merge, rebase, reset, stash, branch, tag, remote, config) : Claude s'en charge.
- Si une commande est refusee, ne cherche pas a la contourner : dis-le dans ta reponse en citant le message recu.
- Termine toujours par une reponse ecrite : ce que tu as fait, ce que tu n'as pas pu faire.

Tache :
$texte"
printf '%s\n' "$invite" > "$run/consigne.txt"

# --- 4. Lancement --------------------------------------------------------------
etat() { [ -n "$racine" ] && git -C "$racine" status --porcelain 2>/dev/null; }
distants() { [ -n "$racine" ] && git -C "$racine" for-each-ref --format='%(refname) %(objectname)' refs/remotes 2>/dev/null; }
etat_avant="$(etat)"; distants_avant="$(distants)"

(
  cd "$travail" || exit 1
  export GIT_TERMINAL_PROMPT=0 GCM_INTERACTIVE=never GH_PROMPT_DISABLED=1
  export GH_TOKEN=garde-fou-pont-antigravity GITHUB_TOKEN=garde-fou-pont-antigravity
  i=0
  for prefixe in https:// http:// ssh:// git:// file:// git@; do
    export "GIT_CONFIG_KEY_$i=url.nopush://garde-fou/.pushInsteadOf" "GIT_CONFIG_VALUE_$i=$prefixe"
    i=$((i + 1))
  done
  export GIT_CONFIG_COUNT=$i
  MSYS2_ARG_CONV_EXCL='*' "$agy" -p "$invite" --output-format json --mode accept-edits --sandbox \
    --settings "$(winpath "$run/reglages.json")" --log-file "$(winpath "$run/agy.log")" \
    --print-timeout "${delai}s" > "$run/sortie.json" 2> "$run/erreurs.txt"
)
code_agy=$?
etat_apres="$(etat)"; distants_apres="$(distants)"

# --- 5. Lecture du resultat ----------------------------------------------------
champs=""
if command -v powershell.exe >/dev/null 2>&1; then
  champs="$(SORTIE_JSON="$(winpath "$run/sortie.json")" MSYS2_ARG_CONV_EXCL='*' powershell.exe -NoProfile -NonInteractive -Command '
[Console]::OutputEncoding = [Text.Encoding]::UTF8
try { $j = [IO.File]::ReadAllText($env:SORTIE_JSON, [Text.Encoding]::UTF8) | ConvertFrom-Json } catch { "illisible=1"; exit }
if (-not $j) { "illisible=1"; exit }
"statut=$($j.status)"
"conversation=$($j.conversation_id)"
"duree=$([math]::Round([double]$j.duration_seconds))"
"jetons=$($j.usage.total_tokens)"
if ($j.error) { "erreur=$($j.error | ConvertTo-Json -Compress -Depth 5)" }
foreach ($d in @($j.denied_actions)) { if ($d) { "refus=$($d.action) ($($d.display_name))" } }
"--reponse--"
$j.response
' 2>/dev/null | tr -d '\r')"
fi
cle() { printf '%s\n' "$champs" | sed -n '/^--reponse--$/q;p' | sed -n "s/^$1=//p"; }
statut="$(cle statut)"; conversation="$(cle conversation)"; duree="$(cle duree)"
jetons="$(cle jetons)"; erreur="$(cle erreur)"; refus_arret="$(cle refus)"
reponse="$(printf '%s\n' "$champs" | sed -n '/^--reponse--$/,$p' | sed '1d')"
[ -n "$champs" ] && [ -z "$(cle illisible)" ] || reponse="$(cat "$run/sortie.json" 2>/dev/null)"

# Refus : tache arretee sur une action non permise (denied_actions, message sur stderr), motif
# donne par les controles d'Antigravity (journal), refus d'une regle cite dans la reponse.
refus="$(
  printf '%s\n' "$refus_arret" | sed '/^$/d; s/^/tache arretee sur une action non permise : /'
  grep -o 'Monitor [a-z_]* FINISHED: triggered=true, reason="[^"]*"' "$run/agy.log" 2>/dev/null \
    | grep -v agy_tool_safety | sed 's/.*reason="\(.*\)"$/controle d Antigravity : \1/' | cut -c1-400
  printf '%s\n' "$reponse" | grep -i -E 'deny rule|permission denied|permission check failed' \
    | sed 's/^[[:space:]]*/cite dans la reponse : /' | cut -c1-400
)"
avis_stderr="$(sed '/^[[:space:]]*$/d' "$run/erreurs.txt" 2>/dev/null | cut -c1-400 | head -n 5)"
satures="$(grep -c 'No capacity available' "$run/agy.log" 2>/dev/null || true)"
if grep -q 'enabling terminal sandbox' "$run/agy.log" 2>/dev/null; then bac="actif"
else bac="INDISPONIBLE sur ce poste : toute commande hors de la courte liste permise arrete la tache"; fi
modifs=""; [ -n "$copie" ] && modifs="$(git -C "$copie" status --short 2>/dev/null)"

# --- 6. Compte rendu -----------------------------------------------------------
a_regarder=0
[ "$code_agy" -eq 0 ] && [ "$statut" = SUCCESS ] || a_regarder=1
[ -n "$(printf '%s' "$reponse" | tr -d '[:space:]')" ] || a_regarder=1
[ -n "$(printf '%s' "$refus$avis_stderr" | tr -d '[:space:]')" ] && a_regarder=1
[ "$distants_avant" = "$distants_apres" ] || a_regarder=1
[ "$etat_avant" = "$etat_apres" ] || a_regarder=1
[ "$profil" = lecture ] && [ -n "$modifs" ] && a_regarder=1

{
  echo "Antigravity : ${statut:-inconnu} (${duree:-?} s, ${jetons:-?} jetons, code $code_agy)"
  [ -n "$conversation" ] && echo "Conversation : $conversation"
  if [ "$profil" = code ]; then echo "Profil code : branche $branche, copie $(winpath "$travail")"
  else echo "Profil lecture : $(winpath "$travail")"; fi
  echo "Bac a sable : $bac"
  [ -n "$note" ] && echo "Note : $note"
  [ "${satures:-0}" -gt 0 ] 2>/dev/null \
    && echo "Note : modele sature chez Google, $satures nouvelle(s) tentative(s) ; d'ou la duree."
  [ -n "$erreur" ] && echo "Erreur : $erreur"
  echo
  echo "Reponse :"
  if [ -n "$(printf '%s' "$reponse" | tr -d '[:space:]')" ]; then printf '%s\n' "$reponse" | sed 's/^/  /'
  else echo "  (vide : la tache s'est arretee sans reponse)"; fi
  echo
  echo "Refus des garde-fous :"
  if [ -n "$(printf '%s' "$refus$avis_stderr" | tr -d '[:space:]')" ]; then
    printf '%s\n' "$refus" | sed '/^[[:space:]]*$/d; s/^/  - /'
    [ -n "$avis_stderr" ] && printf '%s\n' "$avis_stderr" | sed 's/^/  - message d Antigravity : /'
  else echo "  aucun : Antigravity n'a tente aucune action interdite"; fi
  if [ -n "$copie" ]; then
    echo
    echo "Fichiers modifies dans la copie :"
    if [ -n "$modifs" ]; then printf '%s\n' "$modifs" | sed 's/^/  /'; else echo "  aucun"; fi
    [ "$profil" = lecture ] && [ -n "$modifs" ] && echo "  ATTENTION : profil lecture, la copie ne devait pas changer"
  fi
  echo
  echo "Verifications :"
  if [ -z "$racine" ]; then echo "  pas un depot git : branches et dossier non verifies"
  else
    if [ "$distants_avant" = "$distants_apres" ]; then echo "  branches distantes inchangees"
    else echo "  ATTENTION : une branche distante a bouge pendant la tache"; fi
    if [ "$etat_avant" = "$etat_apres" ]; then echo "  dossier du projet inchange"
    else echo "  ATTENTION : le dossier du projet a change pendant la tache"; fi
  fi
  if [ "$profil" = lecture ] && [ -n "$copie" ] && [ -z "$modifs" ]; then
    git -C "$racine" worktree remove "$copie" 2>/dev/null && echo "  copie de lecture retiree"
  fi
  echo
  echo "Traces : $(winpath "$run")"
} | tee "$run/rapport.txt"

[ "$a_regarder" -eq 0 ] && exit 0
exit 2
