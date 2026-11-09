#!/usr/bin/env bash
# Releve d'un depot public sans passer par l'API de GitHub : existence, licence,
# dernier commit, trois dernieres etiquettes classees PAR DATE.
#
# Usage : releve.sh proprietaire/depot [proprietaire/depot ...]
#         releve.sh https://codeberg.org/proprietaire/depot
#
# Rien du depot n'est execute : clone superficiel sans les fichiers, puis lecture du
# seul fichier de licence. Les clones vont dans un dossier temporaire, efface a la fin.
# Ce releve ne remplace pas la lecture de la page du projet : il dit seulement si le
# depot existe, sous quelle licence, et s'il vit encore.
set -u
[ $# -gt 0 ] || { echo "Usage : releve.sh proprietaire/depot [...]" >&2; exit 2; }
command -v git >/dev/null || { echo "git est introuvable" >&2; exit 2; }

TMP="$(mktemp -d "${TMPDIR:-/tmp}/releve.XXXXXX")" || exit 1
trap 'rm -rf "$TMP"' EXIT
export GIT_TERMINAL_PROMPT=0   # un depot introuvable ne doit pas demander d'identifiant

# "timeout" manque sur certains postes : on s'en passe alors.
limite() { if command -v timeout >/dev/null; then timeout "$@"; else shift; "$@"; fi; }

licence() {  # $1 = clone ; ecrit le nom court de la licence, ou le debut du texte a lire
  local d="$1" f txt nom
  f=$(git -C "$d" ls-tree --name-only HEAD 2>/dev/null | grep -i -E '^(licen[cs]e|copying|unlicense)' | head -1)
  [ -n "$f" ] || { echo "AUCUN FICHIER DE LICENCE a la racine"; return; }
  txt=$(limite 60 git -C "$d" show "HEAD:$f" 2>/dev/null | head -c 2500 | tr '\n' ' ' | tr -s ' ')
  case "$txt" in
    *"GNU AFFERO"*|*"GNU Affero"*) nom="AGPL";;
    *"GNU LESSER"*|*"GNU Lesser"*) nom="LGPL";;
    *"GNU GENERAL PUBLIC LICENSE"*"Version 3"*) nom="GPL-3.0";;
    *"GNU GENERAL PUBLIC LICENSE"*"Version 2"*) nom="GPL-2.0";;
    *"Apache License"*) nom="Apache-2.0";;
    *"Mozilla Public License"*) nom="MPL-2.0";;
    *"MIT License"*|*"Permission is hereby granted, free of charge"*) nom="MIT";;
    *"Redistribution and use in source and binary forms"*) nom="BSD";;
    *"This is free and unencumbered software"*) nom="Unlicense";;
    *) nom="A LIRE : $(printf '%s' "$txt" | cut -c1-80)";;
  esac
  echo "$nom ($f)"
}

n=0
for r in "$@"; do
  case "$r" in
    -*) echo "$r | ignore (ce n'est pas un depot)"; continue;;
    http://*|https://*) url="${r%/}"; url="${url%.git}.git";;
    */*) url="https://github.com/${r%.git}.git";;
    *) echo "$r | ignore (attendu : proprietaire/depot)"; continue;;
  esac
  n=$((n + 1)); d="$TMP/$n"
  if ! limite 120 git clone --quiet --depth 1 --filter=blob:none --no-checkout "$url" "$d" 2>/dev/null; then
    echo "$r | INTROUVABLE ou inaccessible"; continue
  fi
  # Les etiquettes se classent par date : un depot qui a change la forme de ses numeros
  # ("v0.8.7" puis "0.20.2") trompe un tri par nom.
  limite 200 git -C "$d" fetch --quiet --depth 1 --filter=tree:0 origin 'refs/tags/*:refs/tags/*' 2>/dev/null
  etiquettes=$(git -C "$d" for-each-ref --sort=-creatordate --count=3 \
    --format='%(refname:short) (%(creatordate:short))' refs/tags 2>/dev/null | tr '\n' ';' | sed 's/;$//; s/;/ ; /g')
  echo "$r | dernier commit $(git -C "$d" log -1 --format=%cs 2>/dev/null) | licence : $(licence "$d") | etiquettes : ${etiquettes:-aucune}"
done
