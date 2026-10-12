package fr.popote.core.sauvegarde

import fr.popote.core.sauvegarde.DriveAppData.FichierDrive
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Le transport Drive a besoin d'un compte Google et du réseau — aucun des deux n'est
 * disponible ici. Ce fichier couvre les parts **sans I/O** : la construction du corps
 * multipart, la lecture de la réponse de liste, le nommage et la rotation. Ce sont elles
 * qui décident ce qui s'écrit et surtout ce qui **s'efface** ; c'est là que se joue le
 * risque, pas dans l'appel HTTP.
 */
class DriveAppDataTest {

    /**
     * Le téléchargement d'une archive rend compte de ses octets au fil de la lecture — c'est
     * ce qui fait avancer la barre d'une restauration, comme celle d'une mise à jour.
     */
    @Test
    fun `la lecture d un telechargement rend compte des octets recus`() {
        val contenu = ByteArray(150_000) { (it % 251).toByte() }
        val releves = mutableListOf<Pair<Long, Long>>()

        val lu = DriveAppData.lireAvecProgression(contenu.inputStream(), contenu.size.toLong()) { recus, total ->
            releves += recus to total
        }

        assertTrue(lu.contentEquals(contenu))
        assertEquals(0L to 150_000L, releves.first())
        assertEquals(150_000L to 150_000L, releves.last())
        assertTrue("les octets ne reculent jamais", releves.zipWithNext().all { (a, b) -> b.first >= a.first })
    }

    @Test
    fun `le corps multipart porte les metadonnees Drive attendues`() {
        val enveloppe = byteArrayOf(1, 2, 3, 4, 5)

        val (frontiere, corps) = DriveAppData.construireCorpsMultipart("popote-test.enc", enveloppe)
        val texte = String(corps, Charsets.ISO_8859_1)

        assertTrue(texte.contains("--$frontiere"))
        assertTrue(texte.contains(""""name":"popote-test.enc""""))
        assertTrue(texte.contains(""""parents":["appDataFolder"]"""))
        assertTrue(texte.contains("Content-Type: application/octet-stream"))
        assertTrue("le corps doit se refermer sur la frontière", texte.trimEnd().endsWith("--$frontiere--"))
    }

    /**
     * Drive refuse qu'un remplacement rejoue `parents` — et le fichier est déjà dans le bon
     * dossier de toute façon.
     */
    @Test
    fun `un remplacement ne rejoue pas le dossier parent`() {
        val (_, corps) = DriveAppData.construireCorpsMultipart(
            "popote-courant.enc",
            byteArrayOf(1),
            creation = false,
        )

        val texte = String(corps, Charsets.ISO_8859_1)
        assertTrue(texte.contains(""""name":"popote-courant.enc""""))
        assertFalse("un PATCH ne doit pas porter parents", texte.contains("parents"))
    }

    @Test
    fun `l enveloppe binaire n est ni tronquee ni alteree dans le corps`() {
        val enveloppe = ByteArray(64) { it.toByte() }

        val (_, corps) = DriveAppData.construireCorpsMultipart("popote-test.enc", enveloppe)

        assertTrue("l'enveloppe doit apparaître intacte dans le corps", indexOfSousTableau(corps, enveloppe) >= 0)
    }

    @Test
    fun `la reponse de liste se lit en fichiers`() {
        val reponse = """
            {"files":[
              {"id":"1a","name":"popote-courant.enc"},
              {"id":"2b","name":"popote-2026-08-26.enc"}
            ]}
        """.trimIndent()

        assertEquals(
            listOf(
                FichierDrive("1a", "popote-courant.enc"),
                FichierDrive("2b", "popote-2026-08-26.enc"),
            ),
            DriveAppData.lireListe(reponse),
        )
    }

    /** Un dossier applicatif neuf ne renvoie pas de tableau `files` du tout. */
    @Test
    fun `un dossier vide se lit comme une liste vide`() {
        assertEquals(emptyList<FichierDrive>(), DriveAppData.lireListe("""{"files":[]}"""))
        assertEquals(emptyList<FichierDrive>(), DriveAppData.lireListe("{}"))
    }

    /**
     * Le dossier applicatif porte aussi une photo par passation : il dépasse les cent
     * fichiers d'une page bien avant d'être extravagant. La version d'avant ignorait
     * `nextPageToken`, et le reste — le trousseau compris — disparaissait sans un mot.
     */
    @Test
    fun `une reponse paginee annonce sa page suivante`() {
        val reponse = """
            {"nextPageToken":"suite~42","files":[{"id":"1a","name":"popote-courant.enc"}]}
        """.trimIndent()

        assertEquals("suite~42", DriveAppData.lireJetonPage(reponse))
        assertEquals(listOf(FichierDrive("1a", "popote-courant.enc")), DriveAppData.lireListe(reponse))
    }

    /** Dernière page : Drive omet la clé. Une clé vide n'est pas un jeton valable non plus. */
    @Test
    fun `la derniere page n annonce aucune suite`() {
        assertNull(DriveAppData.lireJetonPage("""{"files":[{"id":"1a","name":"x.enc"}]}"""))
        assertNull(DriveAppData.lireJetonPage("{}"))
        assertNull(DriveAppData.lireJetonPage("""{"nextPageToken":"","files":[]}"""))
    }

    @Test
    fun `la reponse userinfo se lit en adresse`() {
        val reponse = """{"sub":"1234567890","email":"popotier@gmail.com","email_verified":true}"""

        assertEquals("popotier@gmail.com", DriveAppData.lireEmail(reponse))
    }

    @Test
    fun `une reponse userinfo sans email ne rend rien`() {
        assertEquals(null, DriveAppData.lireEmail("""{"sub":"1234567890"}"""))
    }

    @Test
    fun `l espace gestionnaire a ses propres noms, qui ne se confondent pas avec ceux du popotier`() {
        val gestion = DriveAppData.Espace.GESTION
        assertEquals("gestion-courant.enc", gestion.nomCourant)
        assertEquals(DriveAppData.NOM_COURANT, DriveAppData.Espace.POPOTE.nomCourant)
        assertEquals("gestion-2026-08-26.enc", DriveAppData.nomQuotidien(LocalDate.of(2026, 8, 26), gestion))
        assertTrue(gestion.estQuotidienne("gestion-2026-08-26.enc"))
        assertFalse(gestion.estQuotidienne("popote-2026-08-26.enc"))
        assertFalse(DriveAppData.Espace.POPOTE.estQuotidienne("gestion-2026-08-26.enc"))
        assertEquals(gestion, DriveAppData.Espace.duMode(fr.popote.core.db.ParametresEntity.MODE_GESTIONNAIRE))
        assertEquals(DriveAppData.Espace.POPOTE, DriveAppData.Espace.duMode(null))
    }

    @Test
    fun `le nom quotidien porte la date ISO`() {
        assertEquals("popote-2026-08-26.enc", DriveAppData.nomQuotidien(LocalDate.of(2026, 8, 26)))
        assertEquals("popote-2026-01-01.enc", DriveAppData.nomQuotidien(LocalDate.of(2026, 1, 1)))
    }

    @Test
    fun `le nom d une photo aplatit son chemin relatif`() {
        assertEquals(
            "popote-photo-Passation_Images_signature-123.jpg.enc",
            DriveAppData.nomPhoto("Passation_Images/signature-123.jpg"),
        )
    }

    /** Le nom d'une photo ne doit jamais se faire prendre pour une quotidienne. */
    @Test
    fun `le nom d une photo echappe a la rotation des quotidiennes`() {
        val fichiers = listOf(fichier(DriveAppData.nomPhoto("Passation_Images/signature-123.jpg")))

        assertEquals(emptyList<FichierDrive>(), DriveAppData.aSupprimer(fichiers))
    }

    @Test
    fun `la rotation garde les sept plus recentes`() {
        val fichiers = (1..12).map { fichier("popote-2026-08-%02d.enc".format(it)) }

        val aEffacer = DriveAppData.aSupprimer(fichiers).map { it.nom }

        assertEquals(5, aEffacer.size)
        assertEquals("popote-2026-08-01.enc", aEffacer.min())
        assertEquals("popote-2026-08-05.enc", aEffacer.max())
    }

    @Test
    fun `en dessous de sept la rotation n efface rien`() {
        val fichiers = (1..7).map { fichier("popote-2026-08-0$it.enc") }

        assertEquals(emptyList<FichierDrive>(), DriveAppData.aSupprimer(fichiers))
    }

    /**
     * Une horloge d'appareil mal réglée peut avoir écrit une quotidienne datée dans le
     * futur. Triée normalement, elle resterait toujours en tête — aucune date réelle ne la
     * dépasse jamais — et volerait pour de bon une place aux archives réellement récentes
     * sans jamais s'effacer elle-même. Elle doit donc s'effacer, horloge en moins ou pas.
     */
    @Test
    fun `une quotidienne datee dans le futur s efface au lieu de monopoliser une place`() {
        val fichiers = (1..6).map { fichier("popote-2026-08-%02d.enc".format(it)) } +
            fichier("popote-2099-01-01.enc")

        val aEffacer = DriveAppData.aSupprimer(fichiers, aujourdHui = LocalDate.of(2026, 8, 6)).map { it.nom }

        assertEquals(listOf("popote-2099-01-01.enc"), aEffacer)
    }

    /** Sans horloge en avance, une quotidienne du jour même reste une gardée normale. */
    @Test
    fun `une quotidienne datee du jour meme n est pas consideree future`() {
        val fichiers = (1..7).map { fichier("popote-2026-08-%02d.enc".format(it)) }

        val aEffacer = DriveAppData.aSupprimer(fichiers, aujourdHui = LocalDate.of(2026, 8, 7))

        assertEquals(emptyList<FichierDrive>(), aEffacer)
    }

    /**
     * Le garde-fou qui compte : la rotation ne touche ni au courant, ni au trousseau, ni à
     * quoi que ce soit qui ne porte pas un nom de quotidienne. Effacer le trousseau
     * rendrait toutes les archives illisibles sur un appareil neuf.
     */
    @Test
    fun `la rotation epargne le courant, le trousseau et tout fichier etranger`() {
        val fichiers = (1..12).map { fichier("popote-2026-08-%02d.enc".format(it) ) } + listOf(
            fichier(DriveAppData.NOM_COURANT),
            fichier(DriveAppData.NOM_TROUSSEAU),
            fichier("popote-vieux-format.enc"),
            fichier("note.txt"),
        )

        val aEffacer = DriveAppData.aSupprimer(fichiers).map { it.nom }

        assertFalse(aEffacer.contains(DriveAppData.NOM_COURANT))
        assertFalse(aEffacer.contains(DriveAppData.NOM_TROUSSEAU))
        assertFalse(aEffacer.contains("popote-vieux-format.enc"))
        assertFalse(aEffacer.contains("note.txt"))
        assertTrue(aEffacer.all { it.matches(Regex("""popote-\d{4}-\d{2}-\d{2}\.enc""")) })
    }

    private fun fichier(nom: String) = FichierDrive(id = "id-$nom", nom = nom)

    // ── Socles et points (consigne/phase-17-sauvegarde-continue.md) ────────────

    @Test
    fun `la liste lit aussi la date et la taille de chaque fichier`() {
        val reponse = """{"files":[{"id":"a","name":"popote-socle-20260921T091512Z.enc",""" +
            """"modifiedTime":"2026-09-21T09:15:14.120Z","size":"1432000"}]}"""

        val fichier = DriveAppData.lireListe(reponse).single()

        assertEquals(Instant.parse("2026-09-21T09:15:14.120Z"), fichier.modifie)
        assertEquals(1_432_000L, fichier.taille)
    }

    @Test
    fun `socle et point se nomment par leur instant, et le point nomme son socle`() {
        val espace = DriveAppData.Espace.POPOTE
        val socle = espace.nomSocle(Instant.parse("2026-09-21T09:15:12Z"))
        val point = espace.nomPoint(Instant.parse("2026-09-26T10:57:12Z"), socle)

        assertEquals("popote-socle-20260921T091512Z.enc", socle)
        assertEquals("popote-point-20260926T105712Z-20260921T091512Z.enc", point)
        val lu = espace.archive(FichierDrive("id", point)) as DriveAppData.Archive.Point
        assertEquals(socle, lu.socle)
        assertEquals(Instant.parse("2026-09-26T10:57:12Z"), lu.instant)
        assertNull("un espace ne lit jamais les archives de l'autre", DriveAppData.Espace.GESTION.archive(FichierDrive("id", point)))
        assertFalse("un socle n'est pas une quotidienne", espace.estQuotidienne(socle))
        assertNull(espace.archive(FichierDrive("id", DriveAppData.nomTrousseau("0123456789abcdef"))))
        assertNull(espace.archive(FichierDrive("id", DriveAppData.nomPhoto("Passation_Images/a.jpg"))))
    }

    /** Tous les points des dernières 24 h, puis un par heure, puis un par jour. */
    @Test
    fun `les points s eclaircissent avec l age`() {
        val espace = DriveAppData.Espace.POPOTE
        val debut = Instant.parse("2026-09-01T06:00:00Z")
        val socle = espace.nomSocle(debut)
        val dernier = Instant.parse("2026-09-26T12:00:00Z")
        // Un point toutes les vingt minutes pendant vingt-cinq jours, le dernier à `dernier`.
        val points = generateSequence(debut) { it.plusSeconds(1200) }
            .takeWhile { !it.isAfter(dernier) }
            .map { FichierDrive("id-$it", espace.nomPoint(it, socle)) }
            .toList()

        val efface = DriveAppData.aPurger(points + FichierDrive("s", socle), espace, ZoneOffset.UTC)
            .mapTo(HashSet()) { it.nom }
        val restants = points.filterNot { it.nom in efface }
            .map { (espace.archive(it) as DriveAppData.Archive.Point).instant }
        fun age(instant: Instant) = java.time.Duration.between(instant, dernier)

        val jourPlein = restants.filter { age(it) <= DriveAppData.FENETRE_POINTS_TOUS }
        assertEquals("trois par heure sur les dernières 24 h, bornes comprises", 73, jourPlein.size)
        val semaine = restants.filter {
            age(it) > DriveAppData.FENETRE_POINTS_TOUS && age(it) <= DriveAppData.FENETRE_POINTS_HORAIRES
        }
        val heures = semaine.map { it.atZone(ZoneOffset.UTC).let { z -> z.toLocalDate() to z.hour } }
        assertEquals("un par heure au-delà", heures.distinct().size, semaine.size)
        assertTrue(semaine.size in 140..145)
        val anciens = restants.filter { age(it) > DriveAppData.FENETRE_POINTS_HORAIRES }
        assertEquals("un par jour au-delà d'une semaine", anciens.map { it.atZone(ZoneOffset.UTC).toLocalDate() }.distinct().size, anciens.size)
        assertEquals(19, anciens.size)
        assertTrue("le socle ne part pas", socle !in efface)
    }

    @Test
    fun `un doublon part, le plus recent reste`() {
        val ancien = FichierDrive("a", "popote-2026-09-26.enc", modifie = Instant.parse("2026-09-26T08:00:00Z"))
        val recent = FichierDrive("b", "popote-2026-09-26.enc", modifie = Instant.parse("2026-09-26T21:00:00Z"))

        assertEquals(listOf(ancien), DriveAppData.aPurger(listOf(recent, ancien), DriveAppData.Espace.POPOTE))
    }

    @Test
    fun `la purge ne touche ni trousseau, ni photo, ni fichier etranger, ni l autre espace`() {
        val intouchables = listOf(
            FichierDrive("1", DriveAppData.NOM_TROUSSEAU),
            FichierDrive("2", DriveAppData.nomTrousseau("0123456789abcdef")),
            FichierDrive("3", DriveAppData.nomPhoto("Passation_Images/a.jpg")),
            FichierDrive("4", "note.txt"),
        ) + (1..9).map { FichierDrive("g$it", DriveAppData.Espace.GESTION.nomSocle(Instant.ofEpochSecond(it * 1_000_000L))) }

        assertEquals(emptyList<FichierDrive>(), DriveAppData.aPurger(intouchables, DriveAppData.Espace.POPOTE))
    }

    private fun indexOfSousTableau(haystack: ByteArray, needle: ByteArray): Int {
        outer@ for (i in 0..haystack.size - needle.size) {
            for (j in needle.indices) {
                if (haystack[i + j] != needle[j]) continue@outer
            }
            return i
        }
        return -1
    }
}
