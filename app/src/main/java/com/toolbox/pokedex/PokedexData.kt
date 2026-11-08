package com.toolbox.pokedex

import android.content.Context
import androidx.compose.ui.graphics.Color
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

enum class PokemonType(val typeName: String, val color: Color) {
    Normal("Normal", Color(0xFFA8A77A)),
    Fire("Fire", Color(0xFFEE8130)),
    Water("Water", Color(0xFF6390F0)),
    Electric("Electric", Color(0xFFF7D02C)),
    Grass("Grass", Color(0xFF7AC74C)),
    Ice("Ice", Color(0xFF96D9D6)),
    Fighting("Fighting", Color(0xFFC22E28)),
    Poison("Poison", Color(0xFFA33EA1)),
    Ground("Ground", Color(0xFFE2BF65)),
    Flying("Flying", Color(0xFFA98FF3)),
    Psychic("Psychic", Color(0xFFF95587)),
    Bug("Bug", Color(0xFFA6B91A)),
    Rock("Rock", Color(0xFFB6A136)),
    Ghost("Ghost", Color(0xFF735797)),
    Dragon("Dragon", Color(0xFF6F35FC)),
    Dark("Dark", Color(0xFF705746)),
    Steel("Steel", Color(0xFFB7B7CE)),
    Fairy("Fairy", Color(0xFFD685AD));

    companion object {
        fun fromString(name: String): PokemonType? {
            return entries.find { it.typeName.equals(name, ignoreCase = true) }
        }
    }
}

@Serializable

enum class PokemonGenderClassification(
    val label: String,
    val ratioDescription: String,
    val canBeMale: Boolean,
    val canBeFemale: Boolean,
    val isGenderless: Boolean,
) {
    MaleAndFemaleEqual("Dual Gender", "50% ♂ / 50% ♀", canBeMale = true, canBeFemale = true, isGenderless = false),
    StarterRatio("Starter Gender", "87.5% ♂ / 12.5% ♀", canBeMale = true, canBeFemale = true, isGenderless = false),
    FemalePredominant("Mostly Female", "25% ♂ / 75% ♀", canBeMale = true, canBeFemale = true, isGenderless = false),
    MalePredominant("Mostly Male", "75% ♂ / 25% ♀", canBeMale = true, canBeFemale = true, isGenderless = false),
    FemaleOnly("Female Only ♀", "100% Female", canBeMale = false, canBeFemale = true, isGenderless = false),
    MaleOnly("Male Only ♂", "100% Male", canBeMale = true, canBeFemale = false, isGenderless = false),
    Genderless("Genderless ⚲", "Gender Unknown / Genderless", canBeMale = false, canBeFemale = false, isGenderless = true);

    companion object {
        private val GENDERLESS_NAMES = setOf(
            "magnemite", "magneton", "voltorb", "electrode", "staryu", "starmie", "porygon",
            "ditto", "articuno", "zapdos", "moltres", "mewtwo", "mew", "unown", "porygon2",
            "shedinja", "lunatone", "solrock", "baltoy", "claydol", "beldum", "metang", "metagross",
            "kyogre", "groudon", "rayquaza", "jirachi", "deoxys", "bronzor", "bronzong", "magnezone",
            "porygon-z", "rotom", "uxie", "mesprit", "azelf", "dialga", "palkia", "giratina",
            "phione", "manaphy", "darkrai", "shaymin", "arceus", "victini", "klink", "klang",
            "klinklang", "cryogonal", "golett", "golurk", "cobalion", "terrakion", "virizion",
            "reshiram", "zekrom", "kyurem", "keldeo", "meloetta", "genesect", "carbink",
            "diancie", "hoopa", "volcanion", "type: null", "silvally", "minior", "dhelmise",
            "tapu koko", "tapu lele", "tapu bulu", "tapu fini", "cosmog", "cosmoem", "solgaleo",
            "lunala", "nihilego", "buzzwole", "pheromosa", "xurkitree", "celesteela", "kartana",
            "guzzlord", "necrozma", "magearna", "marshadow", "poipole", "naganadel", "stakataka",
            "blacephalon", "zeraora", "meltan", "melmetal", "sinistea", "polteageist", "falinks",
            "pincurchin", "zacian", "zamazenta", "eternatus", "kubfu", "urshifu", "zarude",
            "regieleki", "regidrago", "glastrier", "spectrier", "calyrex", "tandemaus", "maushold",
            "poltchageist", "sinistcha", "gimmighoul", "gholdengo", "wo-chien", "chien-pao",
            "ting-lu", "chi-yu", "roaring moon", "iron valiant", "koraidon", "miraidon",
            "walking wake", "iron leaves", "gouging fire", "raging bolt", "iron boulder",
            "iron crown", "terapagos", "pecharunt"
        )

        private val FEMALE_ONLY_NAMES = setOf(
            "nidoran♀", "nidorina", "nidoqueen", "chansey", "kangaskhan", "jynx",
            "smoochum", "miltank", "blissey", "illumise", "latias", "wormadam",
            "vespiquen", "froslass", "petilil", "lilligant", "vullaby", "mandibuzz",
            "flabébé", "floette", "florges", "salazzle", "bounsweet", "steenee",
            "tsareena", "hatenna", "hattrem", "hatterene", "milcery", "alcremie",
            "tinkatink", "tinkatuff", "tinkaton", "enamorus", "ogerpon"
        )

        private val MALE_ONLY_NAMES = setOf(
            "nidoran♂", "nidorino", "nidoking", "hitmonlee", "hitmonchan", "tauros",
            "tyrogue", "hitmontop", "volbeat", "latios", "mothim", "gallade",
            "thundurus", "tornadus", "landorus", "braviary", "rufflet", "impidimp",
            "morgrem", "grimmsnarl", "okidogi", "munkidori", "fezandipiti"
        )

        private val STARTER_OR_RARE_MALE_NAMES = setOf(
            "bulbasaur", "ivysaur", "venusaur", "charmander", "charmeleon", "charizard",
            "squirtle", "wartortle", "blastoise", "chikorita", "bayleef", "meganium",
            "cyndaquil", "quilava", "typhlosion", "totodile", "croconaw", "feraligatr",
            "treecko", "grovyle", "sceptile", "torchic", "combusken", "blaziken",
            "mudkip", "marshtomp", "swampert", "turtwig", "grotle", "torterra",
            "chimchar", "monferno", "infernape", "piplup", "prinplup", "empoleon",
            "snivy", "servine", "serperior", "tepig", "pignite", "emboar",
            "oshawott", "dewott", "samurott", "chespin", "quilladin", "chesnaught",
            "fennekin", "braixen", "delphox", "froakie", "frogadier", "greninja",
            "rowlet", "dartrix", "decidueye", "litten", "torracat", "incineroar",
            "popplio", "brionne", "primarina", "grookey", "thwackey", "rillaboom",
            "scorbunny", "raboot", "cinderace", "sobble", "drizzile", "inteleon",
            "sprigatito", "floragato", "meowscarada", "fuecoco", "crocalor", "skeledirge",
            "quaxly", "quaxwell", "quaquaval", "eevee", "vaporeon", "jolteon", "flareon",
            "espeon", "umbreon", "leafeon", "glaceon", "sylveon", "togepi", "togetic", "togekiss",
            "snorlax", "munchlax", "riolu", "lucario", "zorua", "zoroark", "aerodactyl",
            "omanyte", "omastar", "kabuto", "kabutops", "lileep", "cradily", "anorith", "armaldo",
            "cranidos", "rampardos", "shieldon", "bastiodon", "tirtouga", "carracosta",
            "archen", "archeops", "tyrunt", "tyrantrum", "amaura", "aurorus"
        )

        private val FEMALE_PREDOMINANT_NAMES = setOf(
            "clefairy", "clefable", "cleffa", "vulpix", "ninetales", "jigglypuff",
            "wigglytuff", "igglybuff", "snubbull", "granbull", "skitty", "delcatty",
            "chimecho", "chingling", "lopunny", "buneary", "gothita", "gothorita", "gothitelle"
        )

        private val MALE_PREDOMINANT_NAMES = setOf(
            "growlithe", "arcanine", "abra", "kadabra", "alakazam", "machop",
            "machoke", "machamp", "electabuzz", "magmar", "elekid", "magby",
            "makuhita", "hariyama", "timburr", "gurdurr", "conkeldurr"
        )

        fun forPokemon(pokemonName: String): PokemonGenderClassification {
            val key = pokemonName.trim().lowercase()
            return when {
                GENDERLESS_NAMES.any { key.contains(it) } -> Genderless
                FEMALE_ONLY_NAMES.any { key.contains(it) } -> FemaleOnly
                MALE_ONLY_NAMES.any { key.contains(it) } -> MaleOnly
                STARTER_OR_RARE_MALE_NAMES.any { key.contains(it) } -> StarterRatio
                FEMALE_PREDOMINANT_NAMES.any { key.contains(it) } -> FemalePredominant
                MALE_PREDOMINANT_NAMES.any { key.contains(it) } -> MalePredominant
                else -> MaleAndFemaleEqual
            }
        }
    }
}

@Serializable
data class PokemonForm(
    val name: String,
    val types: List<String>,
    val hp: Int,
    val atk: Int,
    val def: Int,
    val spAtk: Int,
    val spDef: Int,
    val spd: Int,
    val isMega: Boolean = false,
    val isRegional: Boolean = false,
    val isGigantamax: Boolean = false,
    val isLegendsZa: Boolean = false,
) {
    val totalStats: Int get() = hp + atk + def + spAtk + spDef + spd

    val primaryTypeColor: Color
        get() = PokemonType.fromString(types.firstOrNull() ?: "")?.color ?: Color(0xFF90A4AE)

    val genderClassification: PokemonGenderClassification
        get() = PokemonGenderClassification.forPokemon(name)
}

@Serializable
data class Pokemon(
    val id: Int,
    val name: String,
    val types: List<String>,
    val hp: Int,
    val atk: Int,
    val def: Int,
    val spAtk: Int,
    val spDef: Int,
    val spd: Int,
    val species: String = "",
    val description: String = "",
    val height: String = "",
    val weight: String = "",
    val color: String = "",
    val hasGenderDiff: Boolean = false,
    val genderDiffDesc: String = "",
    val generation: Int = 1,
    val isLegendsZa: Boolean = false,
    val forms: List<PokemonForm> = emptyList(),
) {
    val totalStats: Int get() = hp + atk + def + spAtk + spDef + spd

    val primaryTypeColor: Color
        get() = PokemonType.fromString(types.firstOrNull() ?: "")?.color ?: Color(0xFF90A4AE)

    val genderClassification: PokemonGenderClassification
        get() = PokemonGenderClassification.forPokemon(name)
}

object PokedexRepository {
    private var cachedPokemon: List<Pokemon>? = null

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    fun getAllPokemon(context: Context): List<Pokemon> {
        cachedPokemon?.let { return it }

        val list = try {
            val jsonText = context.assets.open("pokedex.json").bufferedReader().use { it.readText() }
            json.decodeFromString<List<Pokemon>>(jsonText)
        } catch (e: Exception) {
            emptyList()
        }

        cachedPokemon = list
        return list
    }

    fun getPokemonById(context: Context, id: Int): Pokemon? {
        return getAllPokemon(context).find { it.id == id }
    }
}
