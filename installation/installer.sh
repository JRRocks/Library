#!/usr/bin/env bash
# Installe la Library dans le dossier de configuration de Claude Code (~/.claude) :
# les regles communes (SOCLE.md) et les skills (skills/).
#
#   bash installation/installer.sh            installe ou met a jour
#   bash installation/installer.sh --retirer  retire tout ce que la Library a pose
#
# Le meme script sert en session cloud (Linux) et sur un PC Windows (Git Bash).
# Il ne touche qu'a ce qu'il a lui-meme installe :
#   - dans ~/.claude/CLAUDE.md : seulement le bloc entre les deux reperes LIBRARY ;
#   - dans ~/.claude/skills/   : seulement les dossiers qui portent le fichier .library.
# Les skills ranges dans skills/_propositions/ ne sont pas installes.
# Les messages sont sans accents pour s'afficher partout.

set -u

SRC="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
DEST="${CLAUDE_CONFIG_DIR:-${HOME:-/root}/.claude}"
SOCLE="$SRC/SOCLE.md"
MEMOIRE="$DEST/CLAUDE.md"
MARQUE=".library"
DEBUT="<!-- LIBRARY:DEBUT - bloc pose par la Library (installation/installer.sh). Ne pas modifier ici. -->"
FIN="<!-- LIBRARY:FIN -->"

if [ "${1:-}" = "--retirer" ]; then
  for ancien in "$DEST/skills"/*/; do
    [ -f "${ancien}${MARQUE}" ] && rm -rf "$ancien"
  done
  if [ -f "$MEMOIRE" ] && [ "$(grep -c '^<!-- LIBRARY:DEBUT' "$MEMOIRE" || true)" = "1" ] \
     && [ "$(grep -c '^<!-- LIBRARY:FIN' "$MEMOIRE" || true)" = "1" ]; then
    reste="$(mktemp)"
    awk '
      /^<!-- LIBRARY:DEBUT/ { dans = 1; next }
      /^<!-- LIBRARY:FIN/   { dans = 0; next }
      !dans { print }
    ' "$MEMOIRE" > "$reste"
    if grep -q '[^[:space:]]' "$reste"; then cat "$reste" > "$MEMOIRE"; else rm -f "$MEMOIRE"; fi
    rm -f "$reste"
  fi
  echo "Library retiree de $DEST (skills et regles communes)."
  exit 0
fi

if [ ! -f "$SOCLE" ]; then
  echo "Library : $SOCLE introuvable, rien n'est installe." >&2
  exit 1
fi

mkdir -p "$DEST/skills" || exit 1

# --- 1. Skills -------------------------------------------------------------
# On retire d'abord ce qu'une installation precedente avait pose : un skill
# supprime de la Library disparait ainsi du poste.
for ancien in "$DEST/skills"/*/; do
  [ -f "${ancien}${MARQUE}" ] && rm -rf "$ancien"
done

poses=0
ignores=""
for dossier in "$SRC/skills"/*/; do
  [ -f "${dossier}SKILL.md" ] || continue
  nom="$(basename "$dossier")"
  cible="$DEST/skills/$nom"
  if [ -e "$cible" ]; then
    # Un skill du meme nom existe deja et ne vient pas de la Library : on le garde.
    ignores="$ignores $nom"
    continue
  fi
  cp -R "$dossier" "$cible" && : > "$cible/$MARQUE" && poses=$((poses + 1))
done

# --- 2. Regles communes ----------------------------------------------------
version="$(git -C "$SRC" log -1 --format='%cs %h' 2>/dev/null || true)"
[ -n "$version" ] || version="inconnue"

bloc="$(mktemp)"
{
  printf '%s\n' "$DEBUT"
  cat "$SOCLE"
  printf '\nVersion de la Library installee : %s (%s skill(s)).\n' "$version" "$poses"
  printf '%s\n' "$FIN"
} > "$bloc"

nouveau="$(mktemp)"
if [ -f "$MEMOIRE" ]; then
  debuts="$(grep -c '^<!-- LIBRARY:DEBUT' "$MEMOIRE" || true)"
  fins="$(grep -c '^<!-- LIBRARY:FIN' "$MEMOIRE" || true)"
  ligne_debut="$(grep -n '^<!-- LIBRARY:DEBUT' "$MEMOIRE" | head -n 1 | cut -d: -f1)"
  ligne_fin="$(grep -n '^<!-- LIBRARY:FIN' "$MEMOIRE" | head -n 1 | cut -d: -f1)"
  if [ "$debuts" = "1" ] && [ "$fins" = "1" ] && [ "$ligne_debut" -lt "$ligne_fin" ]; then
    # Bloc deja present : on le remplace, le reste du fichier est garde tel quel.
    awk '
      /^<!-- LIBRARY:DEBUT/ { dans = 1; next }
      /^<!-- LIBRARY:FIN/   { dans = 0; next }
      !dans { print }
    ' "$MEMOIRE" > "$nouveau"
  else
    # Fichier personnel sans bloc, ou reperes abimes : copie de secours, puis on
    # garde tout le texte et on ne retire que les lignes de repere orphelines.
    [ -f "$MEMOIRE.avant-library" ] || cp "$MEMOIRE" "$MEMOIRE.avant-library"
    grep -v -e '^<!-- LIBRARY:DEBUT' -e '^<!-- LIBRARY:FIN' "$MEMOIRE" > "$nouveau" || true
  fi
  # Une ligne vide entre le contenu personnel et le bloc, sans en empiler.
  if [ -s "$nouveau" ] && [ -n "$(tail -n 1 "$nouveau")" ]; then
    printf '\n' >> "$nouveau"
  fi
fi
cat "$bloc" >> "$nouveau"
cat "$nouveau" > "$MEMOIRE"
rm -f "$bloc" "$nouveau"

echo "Library installee dans $DEST : $poses skill(s), regles communes version $version."
[ -z "$ignores" ] || echo "Skills gardes tels quels (meme nom, pas poses par la Library) :$ignores"
exit 0
