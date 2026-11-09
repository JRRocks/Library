#!/usr/bin/env bash
# Controle "rien de prive" avant un commit ou un push dans un depot public.
#
#   bash controle.sh              -> ce qui est pret a etre committe (git add)
#   bash controle.sh origin/main  -> tout ce qui partirait au push (commits locaux + pret a committer)
#
# Sort avec le code 1 des qu'un point est a regarder. Un signalement n'est pas une
# preuve : c'est a la session de juger (une licence contient des adresses, par exemple).
# Messages sans accents pour s'afficher partout.

set -u
base="${1:-}"
alertes=0
signale() { alertes=$((alertes + 1)); printf '  ! %s\n' "$1"; }

git rev-parse --git-dir >/dev/null 2>&1 || { echo "Pas dans un depot git."; exit 2; }

# --- 1. Identite des commits -------------------------------------------------
echo "1. Identite"
moi="$(git config user.email || true)"
case "$moi" in
  *noreply*) ;;
  "") signale "aucune adresse reglee pour les commits (git config user.email)";;
  *) signale "les prochains commits porteraient une adresse personnelle (git config user.email)";;
esac
if [ -n "$base" ]; then
  git log --format='%h %ae %ce' "$base..HEAD" 2>/dev/null | while read -r h a c; do
    case "$a$c" in *noreply*noreply*) ;; *) echo "$h";; esac
  done > "${TMPDIR:-/tmp}/controle-auteurs.$$"
  if [ -s "${TMPDIR:-/tmp}/controle-auteurs.$$" ]; then
    signale "commits a pousser signes d'une adresse non noreply : $(tr '\n' ' ' < "${TMPDIR:-/tmp}/controle-auteurs.$$")"
  fi
  rm -f "${TMPDIR:-/tmp}/controle-auteurs.$$"
fi

# --- 2. Contenu ajoute ---------------------------------------------------------
echo "2. Contenu ajoute"
if [ -n "$base" ]; then
  diff_cmd=(git diff --no-color -U0 "$base")
  noms_cmd=(git diff --name-only --diff-filter=AM "$base")
  msg="$(git log --format='%h %s%n%b' "$base..HEAD" 2>/dev/null)"
else
  diff_cmd=(git diff --cached --no-color -U0)
  noms_cmd=(git diff --cached --name-only --diff-filter=AM)
  msg=""
fi

cherche() { # $1 = libelle, $2 = expression reguliere etendue, $3 = lignes a ignorer (optionnel)
  local trouve
  trouve="$("${diff_cmd[@]}" | awk '
      /^\+\+\+ / { fichier = substr($0, 7); next }
      /^@@/      { split($3, p, ","); ligne = substr(p[1], 2) + 0; next }
      /^\+/      { print fichier ":" ligne ": " substr($0, 2); ligne++ }
    ' | grep -E -i -- "$2" | { if [ -n "${3:-}" ]; then grep -E -i -v -- "$3"; else cat; fi; } | cut -c1-160 | head -n 15)"
  if [ -n "$trouve" ]; then
    signale "$1"
    printf '%s\n' "$trouve" | sed 's/^/      /'
  fi
}

cherche "adresse e-mail" \
  '[A-Za-z0-9._%+-]+@[A-Za-z0-9-]+(\.[A-Za-z0-9-]+)*\.[a-z]{2,}' \
  'noreply|@example\.|@exemple\.|@domaine\.|@users\.noreply|@[0-9]+x\.(png|webp)|@(string|dimen|color|drawable|style|id|android|mipmap)'
cherche "chemin du poste avec un nom d'utilisateur" \
  '(C:[\\/]+Users[\\/]+|/Users/|/home/)[A-Za-z0-9._-]+' \
  '<utilisateur>|/home/user|/home/runner|Users[\\/]+(Public|Default)'
cherche "cle, jeton ou mot de passe" \
  'BEGIN [A-Z ]*PRIVATE KEY|ghp_[A-Za-z0-9]{20,}|github_pat_[A-Za-z0-9_]{20,}|AIza[0-9A-Za-z_-]{30,}|sk-[A-Za-z0-9_-]{20,}|xox[baprs]-[A-Za-z0-9-]{10,}|(api[_-]?key|secret|token|password|passwd|mot[_ ]de[_ ]passe)[A-Za-z_]*["'"'"']?[ ]*[:=][ ]*("[^" ]{8,}"|'"'"'[^'"'"' ]{8,}'"'"'|[A-Za-z0-9/+_-]*[0-9][A-Za-z0-9/+_-]{8,})'
cherche "lien prive (invitation, Drive)" \
  '[a-z]+://join\?|drive\.google\.com/|docs\.google\.com/'
cherche "coordonnees GPS precises" \
  '-?[0-9]{1,2}\.[0-9]{5,}[ ,;]+-?[0-9]{1,3}\.[0-9]{5,}'

# --- 3. Fichiers qui ne se committent pas, images a nettoyer -------------------
echo "3. Fichiers"
fichiers="$("${noms_cmd[@]}")"
interdits="$(printf '%s\n' "$fichiers" | grep -E -i '(^|/)(\.env(\..*)?|local\.properties|keystore\.properties|google-services\.json)$|\.(jks|keystore|p12|pem|apk|aab)$' | grep -v -E '(^|/)debug\.keystore$|\.env\.example$' || true)"
[ -z "$interdits" ] || signale "fichiers qui ne devraient pas etre dans le depot : $(printf '%s' "$interdits" | tr '\n' ' ')"
images="$(printf '%s\n' "$fichiers" | grep -E -i '\.(jpe?g|png|webp|heic)$' || true)"
if [ -n "$images" ]; then
  suspectes=""
  while IFS= read -r image; do
    [ -f "$image" ] || continue
    # EXIF, donnees de l'appareil ou bloc de fin Samsung : traces d'une photo ou capture du telephone.
    if LC_ALL=C grep -a -q -E 'Exif|GPSLatitude|SEFT|SEFH|samsung|SM-[A-Z][0-9]{3}' "$image"; then
      suspectes="$suspectes $image"
    fi
  done <<< "$images"
  [ -z "$suspectes" ] || signale "images qui portent des metadonnees (a retirer si elles viennent du telephone) :$suspectes"
fi

# --- 4. Messages de commit -----------------------------------------------------
if [ -n "$msg" ]; then
  echo "4. Messages de commit"
  dans_msg="$(printf '%s\n' "$msg" | grep -E -i '[A-Za-z0-9._%+-]+@[A-Za-z0-9-]+\.[a-z]{2,}|C:[\\/]+Users[\\/]+[A-Za-z0-9._-]+' | grep -v -i -E 'noreply|<utilisateur>' | cut -c1-160 | head -n 5 || true)"
  if [ -n "$dans_msg" ]; then
    signale "adresse ou chemin du poste dans un message de commit"
    printf '%s\n' "$dans_msg" | sed 's/^/      /'
  fi
fi

if [ "$alertes" -eq 0 ]; then
  echo "Rien a signaler."
  exit 0
fi
echo "$alertes point(s) a regarder avant de committer ou de pousser."
exit 1
