#!/usr/bin/env bash
# Confier une tache a Antigravity (agy), avec les garde-fous du pont Claude -> Antigravity.
#
#   bash confier.sh [--lecture | --code <nom>] [--nom <nom>] [--role <texte>] [--modele <id>]
#                   [--suite <conversation>] [--accord-prive] [--projet <dossier>]
#                   [--delai <secondes>] <consigne.txt | ->
#   bash confier.sh --retirer <nom> [--projet <dossier>]
#
#   --lecture        (par defaut) Antigravity lit une copie du projet et repond ; il ne modifie rien.
#   --code <nom>     Antigravity travaille dans une copie du projet, sur la branche antigravity/<nom> :
#                    il modifie, compile et teste ; Claude relit, essaie et livre.
#   --nom <nom>      nom de la copie de lecture (par defaut : lecture), pour une suite.
#   --role <texte>   role donne a Antigravity (relecteur, codeur, architecte, second avis...).
#   --modele <id>    modele d'Antigravity (agy models) ; par defaut, celui choisi dans Antigravity.
#   --suite <id>     reprend une conversation (identifiant donne par le compte rendu), meme --nom.
#   --accord-prive   l'utilisateur a accepte de montrer ce depot prive a Antigravity (D-05).
#   --projet         dossier du projet (par defaut : le dossier courant).
#   --delai          duree maximale de la tache, en secondes (par defaut 1200).
#   consigne         fichier qui contient la tache, ou - pour la lire sur l'entree standard ;
#                    28 000 octets au plus avec les garde-fous (limite de la ligne de commande).
#   --retirer <nom>  retire la copie <nom> ; une copie de code seulement apres la fusion : git
#                    refuse s'il reste du travail non commite ou une branche non fusionnee.
#
# Garde-fous, quel que soit le profil :
#   - un depot qui n'est pas public sur GitHub n'est montre qu'avec --accord-prive ;
#   - Antigravity ne travaille jamais dans le dossier du projet mais dans une copie (git worktree,
#     ou simple copie d'un dossier hors git en lecture) ; la copie de lecture part de l'etat courant
#     (modifications non commitees comprises) et se retire apres la tache ; la copie de code part
#     du dernier commit et reste pour la relecture ;
#   - mode accept-edits donne explicitement ; jamais --dangerously-skip-permissions ;
#   - bac a sable du terminal (--sandbox) : sans reseau, ecriture seulement dans la copie ;
#   - reglages de cette seule session (garde-fous.json ; rien n'est ecrit dans les reglages
#     d'Antigravity) : push, gh, publication, suppression, reseau, web et commandes git qui
#     modifient le depot refuses ; seule la lecture git sort du bac a sable, plus la compilation
#     et les tests en profil code (compilation.json) ; une autre sortie, que le mode sans ecran ne
#     peut pas faire accepter, arrete la tache ;
#   - profil lecture : aucune ecriture de fichier par les outils d'Antigravity, aucune compilation ;
#   - environnement : transports git coupes (GIT_ALLOW_PROTOCOL) et push redirige vers une adresse
#     invalide (pushInsteadOf), gh sans jeton valable, aucune fenetre d'identification ;
#   - en tete de la tache : la date, le role, ce qui est permis et ce qui est refuse.
# Le compte rendu donne la reponse, les refus, les fichiers modifies, l'entree de journal
# proposee par Antigravity, et verifie qu'aucune branche n'a ete poussee et que le dossier du
# projet n'a pas change. Traces dans le dossier temporaire du poste (%TEMP%\antigravity).
#
# Codes de sortie : 0 tache faite ; 2 a regarder (refus, reponse vide, echec) ;
# 1 lancement impossible ou refuse. Messages sans accents pour s'afficher partout.

set -u
ici="$(cd "$(dirname "$0")" && pwd)"
profil=lecture; nom=""; projet="$PWD"; delai=1200; consigne=""
role=""; modele=""; suite=""; accord=0; retirer=""; limite=28000

valeur() { case "${2-}" in ''|--*) echo "L'option $1 attend une valeur."; exit 1;; esac; }
while [ $# -gt 0 ]; do
  case "$1" in
    --lecture) profil=lecture; shift;;
    --code) valeur "$@"; profil=code; nom="$2"; shift 2;;
    --nom) valeur "$@"; nom="$2"; shift 2;;
    --role) valeur "$@"; role="$2"; shift 2;;
    --modele) valeur "$@"; modele="$2"; shift 2;;
    --suite) valeur "$@"; suite="$2"; shift 2;;
    --accord-prive) accord=1; shift;;
    --retirer) valeur "$@"; retirer="$2"; shift 2;;
    --projet) valeur "$@"; projet="$2"; shift 2;;
    --delai) valeur "$@"; delai="$2"; shift 2;;
    -h|--help) sed -n '2,/^$/p' "$0" | sed 's/^# \{0,1\}//'; exit 0;;
    -) consigne="-"; shift;;
    -*) echo "Option inconnue : $1"; exit 1;;
    *) consigne="$1"; shift;;
  esac
done

winpath() { if command -v cygpath >/dev/null 2>&1; then cygpath -w "$1"; else printf '%s' "$1"; fi; }
nom_valide() { case "$1" in ''|-*|*[!a-z0-9-]*) return 1;; esac; return 0; }
empreinte() { printf '%s' "$1" | cksum | awk '{ printf "%06x", $1 % 16777216 }'; }

projet="$(cd "$projet" 2>/dev/null && pwd)" || { echo "Dossier du projet introuvable."; exit 1; }
racine="$(git -C "$projet" rev-parse --show-toplevel 2>/dev/null || true)"
sous_dossier="$(git -C "$projet" rev-parse --show-prefix 2>/dev/null || true)"
base="${TMPDIR:-/tmp}/antigravity"
# Les copies portent le nom du projet et une empreinte de son chemin : deux projets de meme nom
# ne partagent pas leurs copies.
origine="${racine:-$projet}"
prefixe_copie="$base/copies/$(basename "$origine")-$(empreinte "$origine")"
[ -n "$racine" ] && git -C "$racine" worktree prune 2>/dev/null

# --- 0. Retrait d'une copie ----------------------------------------------------
if [ -n "$retirer" ]; then
  nom_valide "$retirer" || { echo "Nom invalide : $retirer"; exit 1; }
  copie="$prefixe_copie-$retirer"; branche="antigravity/$retirer"; reste=0; trouve=0
  if [ -e "$copie" ]; then
    trouve=1
    if [ -z "$racine" ]; then
      rm -rf -- "$copie" && echo "Copie de lecture retiree : $(winpath "$copie")"
    elif git -C "$copie" symbolic-ref -q HEAD >/dev/null 2>&1; then
      if git -C "$racine" worktree remove "$copie" 2>/dev/null; then echo "Copie retiree : $(winpath "$copie")"
      else echo "Copie gardee, elle a du travail non commite : $(winpath "$copie")"; reste=1; fi
    elif git -C "$racine" worktree remove --force "$copie" 2>/dev/null; then
      echo "Copie de lecture retiree : $(winpath "$copie")"
    else echo "Copie impossible a retirer : $(winpath "$copie")"; reste=1; fi
  fi
  if [ -n "$racine" ] && git -C "$racine" show-ref --verify --quiet "refs/heads/$branche"; then
    trouve=1
    if [ "$reste" -eq 0 ] && git -C "$racine" branch -d "$branche" >/dev/null 2>&1; then
      echo "Branche $branche retiree (deja fusionnee)."
    else
      echo "Branche $branche gardee : pas fusionnee, ou copie gardee. La supprimer seulement avec l'accord de l'utilisateur."
      reste=1
    fi
  fi
  [ "$trouve" -eq 1 ] || { echo "Rien a retirer : ni copie $retirer ni branche $branche pour ce projet."; exit 1; }
  [ "$reste" -eq 0 ] && exit 0
  exit 2
fi

# --- 1. Ce qu'il faut pour lancer ---------------------------------------------
agy="$(command -v agy 2>/dev/null || true)"
if [ -z "$agy" ] && [ -n "${LOCALAPPDATA:-}" ] && command -v cygpath >/dev/null 2>&1; then
  c="$(cygpath -u "$LOCALAPPDATA")/agy/bin/agy.exe"
  [ -x "$c" ] && agy="$c"
fi
[ -n "$agy" ] || { echo "Antigravity (agy) introuvable sur ce poste."; exit 1; }
for f in garde-fous.json compilation.json; do
  [ -f "$ici/$f" ] || { echo "$f introuvable a cote du script."; exit 1; }
done
case "$delai" in ''|*[!0-9]*) echo "Delai invalide : $delai"; exit 1;; esac
[ "$profil" = lecture ] && [ -z "$nom" ] && nom=lecture
nom_valide "$nom" || { echo "Nom invalide (minuscules, chiffres, tirets) : $nom"; exit 1; }
case "$modele" in *[!a-z0-9.-]*) echo "Modele invalide : $modele (voir agy models)"; exit 1;; esac
case "$suite" in *[!0-9a-f-]*) echo "Conversation invalide : $suite"; exit 1;; esac
role="$(printf '%s' "$role" | tr '\r\n' '  ')"
[ "$profil" = code ] && [ -z "$racine" ] && { echo "Le profil code demande un depot git."; exit 1; }
if [ -n "$racine" ] && ! git -C "$racine" rev-parse --verify -q HEAD >/dev/null; then
  echo "Le depot n'a encore aucun commit : rien a copier."; exit 1
fi

if [ "$consigne" = "-" ]; then texte="$(cat)"
elif [ -n "$consigne" ] && [ -f "$consigne" ]; then texte="$(cat "$consigne")"
else echo "Donne la tache : un fichier, ou - pour l'entree standard."; exit 1; fi
[ -n "$(printf '%s' "$texte" | tr -d '[:space:]')" ] || { echo "La consigne est vide."; exit 1; }

# D-05 : un depot public se montre librement ; un autre, seulement avec l'accord de l'utilisateur.
visibilite="inconnue"
if [ -n "$racine" ] && command -v gh >/dev/null 2>&1; then
  url="$(git -C "$racine" remote get-url origin 2>/dev/null || true)"
  depot="$(printf '%s' "$url" | sed -n -E 's#^(https://|git@|ssh://git@)github\.com[:/]([^/]+/[^/]+)$#\2#p' | sed 's/\.git$//')"
  if [ -n "$depot" ]; then
    v="$(GH_PROMPT_DISABLED=1 gh repo view "$depot" --json visibility --jq .visibility 2>/dev/null || true)"
    [ -n "$v" ] && visibilite="$v"
  fi
fi
if [ "$visibilite" != PUBLIC ] && [ "$accord" -ne 1 ]; then
  echo "Depot prive ou de visibilite inconnue ($visibilite) : il ne se montre a Antigravity qu'avec"
  echo "l'accord de l'utilisateur pour ce projet (D-05). Avec son accord, relance avec --accord-prive."
  exit 1
fi

# --- 2. La tache, garde-fous en tete -------------------------------------------
if [ "$profil" = code ]; then
  branche="antigravity/$nom"
  permis="Ce dossier est une copie du projet, sur la branche $branche. Tu peux lire et modifier ses fichiers, et lancer les commandes de compilation et de test du projet : Claude relira ton travail et le livrera."
  bac_sortie="n'en sors que pour compiler ou tester le projet"
else
  branche=""
  permis="Tu lis ce dossier sans rien y modifier ni compiler : ta reponse ecrite est le resultat attendu."
  bac_sortie="n'en sors pas"
fi
ligne_role=""; [ -n "$role" ] && ligne_role="
Ton role dans cette tache : $role."
invite="Nous sommes le $(date +%Y-%m-%d). Claude Code te confie cette tache (pont Claude -> Antigravity).$ligne_role

Garde-fous de cette tache :
- $permis
- Reste dans ce dossier : ne lis et n'ecris rien en dehors.
- Pour lire, chercher et modifier des fichiers, prefere tes outils de fichiers au terminal. Lance tes commandes dans le bac a sable ; $bac_sortie.
- Tu ne pousses, ne publies et ne supprimes rien : pas de git push, pas de gh, pas de publication ni de deploiement, pas de suppression de fichier ou de branche, pas d'envoi sur le reseau. Si la tache le demande, ne le fais pas : ecris-le dans ta reponse, Claude s'en chargera.
- Ne lance aucune commande git qui modifie le depot (add, commit, checkout, switch, merge, rebase, reset, stash, branch, tag, remote, config) : Claude s'en charge.
- Si une commande est refusee, ne cherche pas a la contourner : dis-le dans ta reponse en citant le message recu.
- Ne modifie pas le journal du projet. Termine ta reponse par l'entree que tu y proposes, au format de ce journal s'il existe (lis ses dernieres entrees), avec la mention Agent : Antigravity (tache confiee par Claude), entre une ligne --- entree de journal --- et une ligne --- fin ---. Claude la verifiera et la posera.
- Termine toujours par une reponse ecrite : ce que tu as fait, ce que tu n'as pas pu faire.

Tache :
$texte"
taille="$(printf '%s' "$invite" | wc -c | tr -d ' ')"
if [ "$taille" -gt "$limite" ]; then
  echo "Consigne trop longue pour la ligne de commande de Windows : $taille octets avec les garde-fous, $limite au plus."
  echo "Raccourcis-la : Antigravity lit lui-meme les fichiers et l'historique du projet (git diff, git log)."
  exit 1
fi

# --- 3. Copie de travail et reglages -------------------------------------------
run="$base/$(date +%Y%m%d-%H%M%S)-$$-$nom"
mkdir -p "$run" "$base/copies" || { echo "Impossible de creer $run"; exit 1; }
printf '%s\n' "$invite" > "$run/consigne.txt"
copie="$prefixe_copie-$nom"; verrou="$copie.verrou"; note=""; neuve=0
# Une seule tache a la fois par copie : le verrou porte le numero du script qui l'utilise.
if [ -f "$verrou" ] && kill -0 "$(cat "$verrou" 2>/dev/null)" 2>/dev/null; then
  echo "Une autre tache utilise deja la copie $nom : attends-la, ou choisis un autre --nom."; exit 1
fi
echo $$ > "$verrou"; trap 'rm -f "$verrou"' EXIT
# En cas d'echec avant le lancement, la copie qui vient d'etre faite repart.
abandon() {
  [ "$neuve" -eq 1 ] || return 0
  if [ -z "$racine" ]; then rm -rf -- "$copie"
  else
    git -C "$racine" worktree remove --force "$copie" 2>/dev/null
    [ -n "$branche" ] && git -C "$racine" branch -d "$branche" >/dev/null 2>&1
  fi
  return 0
}
if [ "$profil" = code ]; then
  if [ -n "$suite" ]; then
    [ -d "$copie" ] || { echo "Pas de copie $(winpath "$copie") a reprendre."; exit 1; }
  else
    if git -C "$racine" show-ref --verify --quiet "refs/heads/$branche"; then
      echo "La branche $branche existe deja : choisis un autre nom, ou --suite."; exit 1
    fi
    [ -e "$copie" ] && { echo "Le dossier $(winpath "$copie") existe deja : --retirer $nom, ou un autre nom."; exit 1; }
    git -C "$racine" worktree add -q -b "$branche" "$copie" HEAD \
      || { echo "Copie de travail impossible (git worktree)."; exit 1; }
    neuve=1
    [ -n "$(git -C "$racine" status --porcelain)" ] \
      && note="le projet a des modifications non commitees ; elles ne sont pas dans la copie d'Antigravity."
  fi
  # Reglages du profil code : la compilation et les tests s'ajoutent aux commandes permises.
  awk -v f="$ici/compilation.json" '
    { print }
    /"allow": \[/ { while ((getline l < f) > 0) { if (l !~ /"command\(/) continue
      sub(/^[[:space:]]*/, "", l); sub(/[[:space:]]*,?[[:space:]]*$/, ",", l); print "      " l } }
  ' "$ici/garde-fous.json" > "$run/reglages.json"
  attendu='"command(gradlew)"'
else
  if [ -e "$copie" ]; then
    if [ -n "$racine" ] && [ -z "$(git -C "$copie" status --porcelain 2>/dev/null)" ] \
      && git -C "$racine" worktree remove --force "$copie" 2>/dev/null; then :
    else echo "Une copie de lecture $nom existe deja, peut-etre modifiee : regarde-la, puis --retirer $nom. $(winpath "$copie")"; exit 1; fi
  fi
  if [ -n "$racine" ]; then
    instant="$(git -C "$racine" stash create 2>/dev/null || true)"
    git -C "$racine" worktree add -q --detach "$copie" "${instant:-HEAD}" \
      || { echo "Copie de lecture impossible (git worktree)."; exit 1; }
    [ -n "$(git -C "$racine" ls-files --others --exclude-standard | head -n 1)" ] \
      && note="les fichiers jamais ajoutes a git ne sont pas dans la copie d'Antigravity."
  else
    ko="$(du -sk "$projet" 2>/dev/null | cut -f1)"
    [ "${ko:-0}" -le 1048576 ] \
      || { echo "Dossier trop gros pour une copie de lecture ($((ko / 1024)) Mio, 1024 au plus) : confie un sous-dossier."; exit 1; }
    cp -R "$projet" "$copie" || { rm -rf -- "$copie"; echo "Copie de lecture impossible."; exit 1; }
    note="pas un depot git : Antigravity lit une simple copie du dossier."
  fi
  neuve=1
  sed 's/"deny": \[/"deny": [ "write_file(*)",/' "$ici/garde-fous.json" > "$run/reglages.json"
  attendu='"write_file(*)"'
fi
reglages_lisibles=1
grep -qF "$attendu" "$run/reglages.json" || reglages_lisibles=0
if command -v powershell.exe >/dev/null 2>&1; then
  REGLAGES="$(winpath "$run/reglages.json")" MSYS2_ARG_CONV_EXCL='*' powershell.exe -NoProfile -NonInteractive -Command \
    'try { $null = [IO.File]::ReadAllText($env:REGLAGES) | ConvertFrom-Json; exit 0 } catch { exit 1 }' >/dev/null 2>&1 \
    || reglages_lisibles=0
fi
[ "$reglages_lisibles" -eq 1 ] || { echo "Reglages impossibles a preparer : $(winpath "$run/reglages.json")"; abandon; exit 1; }
travail="$copie/$sous_dossier"
[ -d "$travail" ] || { echo "Le dossier $sous_dossier n'est pas dans la copie (pas suivi par git) : lance depuis la racine du projet."; abandon; exit 1; }

# --- 4. Lancement --------------------------------------------------------------
etat() { [ -n "$racine" ] && git -C "$racine" status --porcelain 2>/dev/null; }
distants() { [ -n "$racine" ] && git -C "$racine" for-each-ref --format='%(refname) %(objectname)' refs/remotes 2>/dev/null; }
# Fichiers du projet lui-meme (suivis, ou nouveaux non ignores) ecrits depuis le lancement.
ecrits_projet() {
  if [ -n "$racine" ]; then
    git -C "$racine" ls-files -c -o --exclude-standard -z 2>/dev/null \
      | while IFS= read -r -d '' f; do [ "$racine/$f" -nt "$repere" ] && printf '%s\n' "$f"; done
  else
    (cd "$projet" && find . -newer "$repere" -type f 2>/dev/null | sed 's#^\./##')
  fi | head -n 10
}
etat_avant="$(etat)"; distants_avant="$(distants)"
repere="$run/repere"; : > "$repere"
options=()
[ -n "$modele" ] && options+=(--model "$modele")
[ -n "$suite" ] && options+=(--conversation "$suite")

(
  cd "$travail" || exit 1
  export GIT_TERMINAL_PROMPT=0 GCM_INTERACTIVE=never GH_PROMPT_DISABLED=1 GIT_ALLOW_PROTOCOL=aucun
  export GH_TOKEN=garde-fou-pont-antigravity GITHUB_TOKEN=garde-fou-pont-antigravity
  i=0
  for prefixe in https:// http:// ssh:// git:// file:// git@; do
    export "GIT_CONFIG_KEY_$i=url.nopush://garde-fou/.pushInsteadOf" "GIT_CONFIG_VALUE_$i=$prefixe"
    i=$((i + 1))
  done
  export GIT_CONFIG_COUNT=$i
  MSYS2_ARG_CONV_EXCL='*' "$agy" -p "$invite" ${options[@]+"${options[@]}"} --output-format json \
    --mode accept-edits --sandbox --settings "$(winpath "$run/reglages.json")" \
    --log-file "$(winpath "$run/agy.log")" --print-timeout "${delai}s" \
    > "$run/sortie.json" 2> "$run/erreurs.txt"
)
code_agy=$?
etat_apres="$(etat)"; distants_apres="$(distants)"; ecrits="$(ecrits_projet)"

# --- 5. Lecture du resultat ----------------------------------------------------
champs=""
if command -v powershell.exe >/dev/null 2>&1; then
  champs="$(SORTIE_JSON="$(winpath "$run/sortie.json")" MSYS2_ARG_CONV_EXCL='*' powershell.exe -NoProfile -NonInteractive -Command '
[Console]::OutputEncoding = [Text.Encoding]::UTF8
try {
  $j = [IO.File]::ReadAllText($env:SORTIE_JSON, [Text.Encoding]::UTF8) | ConvertFrom-Json
  if (-not $j) { throw "vide" }
  "statut=$($j.status)"
  "conversation=$($j.conversation_id)"
  "duree=$([math]::Round([double]$j.duration_seconds))"
  "jetons=$($j.usage.total_tokens)"
  if ($j.error) { "erreur=$($j.error | ConvertTo-Json -Compress -Depth 5)" }
  foreach ($d in @($j.denied_actions)) { if ($d) { "refus=$($d.action) ($($d.display_name))" } }
  "--reponse--"
  $j.response
} catch { "illisible=1" }
' 2>/dev/null | tr -d '\r')"
fi
cle() { printf '%s\n' "$champs" | sed -n '/^--reponse--$/q;p' | sed -n "s/^$1=//p"; }
statut="$(cle statut)"; conversation="$(cle conversation)"; duree="$(cle duree)"
jetons="$(cle jetons)"; erreur="$(cle erreur)"; refus_arret="$(cle refus)"
reponse="$(printf '%s\n' "$champs" | sed -n '/^--reponse--$/,$p' | sed '1d')"
[ -n "$champs" ] && [ -z "$(cle illisible)" ] || reponse="$(cat "$run/sortie.json" 2>/dev/null)"
# Sans sortie (delai depasse...), l'identifiant de la conversation se lit dans le journal d'agy.
[ -n "$conversation" ] || conversation="$(grep -o 'Created conversation [0-9a-f-]*' "$run/agy.log" 2>/dev/null | tail -n 1 | sed 's/.* //')"
[ -n "$conversation" ] || conversation="$suite"

# Entree de journal proposee : le bloc entre --- entree de journal --- et --- fin ---, seulement
# si les deux lignes sont la ; sinon la reponse reste entiere.
bloc_journal() {
  printf '%s\n' "$reponse" | awk -v garder="$1" '
    { ligne[NR] = $0; l = tolower($0)
      if (!d && l ~ /^[[:space:]`*>]*-+[[:space:]]*entr..?e de journal[[:space:]]*-+[[:space:]`*]*$/) d = NR
      else if (d && !f && l ~ /^[[:space:]`*>]*-+[[:space:]]*fin[[:space:]]*-+[[:space:]`*]*$/) f = NR }
    END {
      for (i = 1; i <= NR; i++) {
        if (!f) { if (garder == "dehors") print ligne[i]; continue }
        if (garder == "dedans" && i > d && i < f) print ligne[i]
        if (garder == "dehors" && (i < d || i > f)) print ligne[i]
      }
    }'
}
entree="$(bloc_journal dedans)"
[ -n "$(printf '%s' "$entree" | tr -d '[:space:]')" ] && { printf '%s\n' "$entree" > "$run/journal.md"; reponse="$(bloc_journal dehors)"; }

# Refus : tache arretee sur une action non permise (denied_actions, message sur stderr), motif
# donne par les controles d'Antigravity (journal d'agy), refus d'une regle cite dans la reponse.
refus="$(
  printf '%s\n' "$refus_arret" | sed '/^$/d; s/^/tache arretee sur une action non permise : /'
  grep -o 'Monitor [a-z_]* FINISHED: triggered=true, reason="[^"]*"' "$run/agy.log" 2>/dev/null \
    | grep -v agy_tool_safety | sed 's/.*reason="\(.*\)"$/controle d Antigravity : \1/' | cut -c1-400
  printf '%s\n' "$reponse" | grep -i 'user-configured deny rule' \
    | sed 's/^[[:space:]]*/cite dans la reponse : /' | cut -c1-400
)"
avis_stderr="$(sed '/^[[:space:]]*$/d' "$run/erreurs.txt" 2>/dev/null | cut -c1-400 | head -n 5)"
satures="$(grep -c 'No capacity available' "$run/agy.log" 2>/dev/null || true)"
if grep -q 'enabling terminal sandbox' "$run/agy.log" 2>/dev/null; then bac="actif"
else bac="non confirme par le journal d'agy : sans bac a sable, toute commande hors de la courte liste permise arrete la tache"; fi
if [ -n "$racine" ]; then modifs="$(git -C "$copie" status --short 2>/dev/null)"
else modifs="$(cd "$copie" && find . -newer "$repere" -type f 2>/dev/null | sed 's#^\./#ecrit : #')"; fi
compil=""
[ "$profil" = code ] && compil="$(printf '%s\n' "$modifs" | sed 's/^...//' | grep -E '(^|/)(build\.gradle(\.kts)?|settings\.gradle(\.kts)?|gradle\.properties|gradlew(\.bat)?|gradle/wrapper/[^/]*|package(-lock)?\.json|Cargo\.toml|build\.rs|[^/]*\.(csproj|props|targets)|pyproject\.toml|setup\.(py|cfg)|conftest\.py|pytest\.ini|tox\.ini|Makefile)$')"
# Une branche distante qui bouge vient d'un fetch (Claude) ou d'un push : le reflog le dit.
pousse=""
if [ "$distants_avant" != "$distants_apres" ]; then
  for ref in $(printf '%s\n' "$distants_apres" | grep -vxF -f <(printf '%s\n' "$distants_avant") | cut -d' ' -f1); do
    git -C "$racine" reflog show -n 1 --format=%gs "$ref" 2>/dev/null | grep -q 'update by push' && pousse="$pousse $ref"
  done
fi
change_projet=""
if [ "$etat_avant" != "$etat_apres" ] || [ -n "$ecrits" ]; then
  change_projet="$( { printf '%s\n' "$etat_apres" | grep -vxF -f <(printf '%s\n' "$etat_avant") | sed 's/^...//'
    printf '%s\n' "$ecrits"; } | sed '/^$/d' | sort -u | head -n 10)"
  [ -n "$change_projet" ] || change_projet="(un fichier modifie est redevenu comme avant)"
fi

# --- 6. Compte rendu -----------------------------------------------------------
a_regarder=0
[ "$code_agy" -eq 0 ] && [ "$statut" = SUCCESS ] || a_regarder=1
[ -n "$(printf '%s' "$reponse" | tr -d '[:space:]')" ] || a_regarder=1
[ -n "$(printf '%s' "$refus$avis_stderr" | tr -d '[:space:]')" ] && a_regarder=1
[ -n "$pousse$change_projet" ] && a_regarder=1
[ "$profil" = lecture ] && [ -n "$modifs" ] && a_regarder=1

{
  echo "Antigravity : ${statut:-inconnu} (${duree:-?} s, ${jetons:-?} jetons, code $code_agy)"
  echo "Modele : ${modele:-celui choisi dans Antigravity}${role:+ ; role : $role}"
  if [ "$profil" = code ]; then echo "Profil code : branche $branche, copie $(winpath "$travail")"
  else echo "Profil lecture : $(winpath "$travail")"; fi
  echo "Depot : $visibilite$([ "$accord" -eq 1 ] && echo ', montre avec l accord de l utilisateur')"
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
  else echo "  aucun signale"; fi
  echo
  echo "Fichiers modifies dans la copie :"
  if [ -n "$modifs" ]; then printf '%s\n' "$modifs" | sed 's/^/  /'; else echo "  aucun"; fi
  [ "$profil" = lecture ] && [ -n "$modifs" ] && echo "  ATTENTION : profil lecture, la copie ne devait pas changer"
  if [ -n "$compil" ]; then
    echo "  Note : fichiers de compilation ou de test modifies ; s'il a compile, ils ont tourne hors du bac a sable :"
    printf '%s\n' "$compil" | sed 's/^/    /'
  fi
  echo
  echo "Entree de journal proposee (a verifier, puis a poser en haut du journal du projet) :"
  if [ -f "$run/journal.md" ]; then sed 's/^/  /' "$run/journal.md"; else echo "  aucune : a ecrire par Claude"; fi
  echo
  echo "Verifications :"
  if [ -n "$racine" ]; then
    if [ -n "$pousse" ]; then echo "  ATTENTION : push vers$pousse pendant la tache"
    elif [ "$distants_avant" != "$distants_apres" ]; then echo "  branches distantes mises a jour par un fetch pendant la tache (pas par un push)"
    else echo "  branches distantes inchangees"; fi
  fi
  if [ -n "$change_projet" ]; then
    echo "  ATTENTION : le dossier du projet a change pendant la tache ; si ce n'est pas Claude, Antigravity a ecrit hors de sa copie :"
    printf '%s\n' "$change_projet" | sed 's/^/    /'
  else echo "  dossier du projet inchange"; fi
  if [ "$profil" = lecture ]; then
    if [ -z "$modifs" ]; then
      if [ -n "$racine" ]; then git -C "$racine" worktree remove --force "$copie" 2>/dev/null && echo "  copie de lecture retiree"
      else rm -rf -- "$copie" && echo "  copie de lecture retiree"; fi
    else echo "  copie de lecture gardee pour la regarder ; ensuite : --retirer $nom"; fi
  else echo "  copie gardee pour la relecture ; apres la fusion : --retirer $nom"; fi
  echo
  if [ -n "$conversation" ]; then
    if [ "$profil" = code ]; then echo "Suite : --code $nom --suite $conversation --projet $projet"
    else echo "Suite : --lecture --nom $nom --suite $conversation --projet $projet"; fi
  fi
  echo "Traces : $(winpath "$run")"
} | tee "$run/rapport.txt"

[ "$a_regarder" -eq 0 ] && exit 0
exit 2
