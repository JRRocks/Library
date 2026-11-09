#!/bin/bash
# Library JRRocks : regles communes, skills et catalogue des moteurs,
# poses dans chaque nouvelle session cloud. Mode d'emploi : installation/README.md.
# Version 2026-11-09.3 (changer ce numero force la reinstallation dans les nouvelles sessions)
DOSSIER="${HOME:-/root}/.claude/library"
mkdir -p "$(dirname "$DOSSIER")" && rm -rf "$DOSSIER"
git clone --quiet --depth 1 "${LIBRARY_DEPOT:-https://github.com/JRRocks/Library.git}" "$DOSSIER" \
  && bash "$DOSSIER/installation/installer.sh" || true
