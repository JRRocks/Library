# Library
La liste de tout les moteurs que j'utilise pour pouvoir les réutiliser sur d'autres app

## Colorisation IA de manga

- **manga-colorization-v2** — https://github.com/qweasdd/manga-colorization-v2
  - Colorisation automatique de pages noir et blanc, gratuit et local (GPU NVIDIA ≈1 s/page, CPU ≈15-20 s/page, ou Google Colab gratuit).
  - Serveur HTTP réutilisable (`GET /health`, `POST /colorize`) : `tools/colorizer-server` dans [Mihon-edited-](https://github.com/JRRocks/Mihon-edited-).
  - Astuce : ne garder que les couleurs du modèle et les poser sous le trait d'origine (canal L en Lab) pour garder la page nette.
