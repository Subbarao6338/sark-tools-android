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
    val abilities: List<String> = emptyList(),
) {
    val totalStats: Int get() = hp + atk + def + spAtk + spDef + spd

    val primaryTypeColor: Color
        get() = PokemonType.fromString(types.firstOrNull() ?: "")?.color ?: Color(0xFF90A4AE)

    val genderClassification: PokemonGenderClassification
        get() = PokemonGenderClassification.forPokemon(name)

    val typeMatchups: PokemonTypeMatchups
        get() = PokemonTypeMatchups.calculate(types)
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
    val category: String = "",
    val introduced: String = "",
    val abilities: List<String> = emptyList(),
    val moves: List<String> = emptyList(),
    val evolutionPrev: String? = null,
    val evolutionNext: List<String> = emptyList(),
    val heldItems: List<String> = emptyList(),
    val gamesAppeared: List<String> = emptyList(),
    val locations: List<String> = emptyList(),
    val costumes: List<String> = emptyList(),
) {
    val totalStats: Int get() = hp + atk + def + spAtk + spDef + spd

    val primaryTypeColor: Color
        get() = PokemonType.fromString(types.firstOrNull() ?: "")?.color ?: Color(0xFF90A4AE)

    val genderClassification: PokemonGenderClassification
        get() = PokemonGenderClassification.forPokemon(name)

    val typeMatchups: PokemonTypeMatchups
        get() = PokemonTypeMatchups.calculate(types)
}

data class TypeMultiplier(
    val typeName: String,
    val multiplier: Float,
)

data class PokemonTypeMatchups(
    val weaknesses: List<TypeMultiplier>, // 2x or 4x
    val resistances: List<TypeMultiplier>, // 0.5x or 0.25x
    val immunities: List<String>, // 0x
) {
    companion object {
        // Attack type -> Defender type -> multiplier
        private val TYPE_CHART = mapOf(
            "Normal" to mapOf("Rock" to 0.5f, "Ghost" to 0.0f, "Steel" to 0.5f),
            "Fire" to mapOf("Fire" to 0.5f, "Water" to 0.5f, "Grass" to 2.0f, "Ice" to 2.0f, "Bug" to 2.0f, "Rock" to 0.5f, "Dragon" to 0.5f, "Steel" to 2.0f),
            "Water" to mapOf("Fire" to 2.0f, "Water" to 0.5f, "Grass" to 0.5f, "Ground" to 2.0f, "Rock" to 2.0f, "Dragon" to 0.5f),
            "Electric" to mapOf("Water" to 2.0f, "Electric" to 0.5f, "Grass" to 0.5f, "Ground" to 0.0f, "Flying" to 2.0f, "Dragon" to 0.5f),
            "Grass" to mapOf("Fire" to 0.5f, "Water" to 2.0f, "Grass" to 0.5f, "Poison" to 0.5f, "Ground" to 2.0f, "Flying" to 0.5f, "Bug" to 0.5f, "Rock" to 2.0f, "Dragon" to 0.5f, "Steel" to 0.5f),
            "Ice" to mapOf("Fire" to 0.5f, "Water" to 0.5f, "Grass" to 2.0f, "Ice" to 0.5f, "Ground" to 2.0f, "Flying" to 2.0f, "Dragon" to 2.0f, "Steel" to 0.5f),
            "Fighting" to mapOf("Normal" to 2.0f, "Ice" to 2.0f, "Poison" to 0.5f, "Flying" to 0.5f, "Psychic" to 0.5f, "Bug" to 0.5f, "Rock" to 2.0f, "Ghost" to 0.0f, "Dark" to 2.0f, "Steel" to 2.0f, "Fairy" to 0.5f),
            "Poison" to mapOf("Grass" to 2.0f, "Poison" to 0.5f, "Ground" to 0.5f, "Rock" to 0.5f, "Ghost" to 0.5f, "Steel" to 0.0f, "Fairy" to 2.0f),
            "Ground" to mapOf("Fire" to 2.0f, "Electric" to 2.0f, "Grass" to 0.5f, "Poison" to 2.0f, "Flying" to 0.0f, "Bug" to 0.5f, "Rock" to 2.0f, "Steel" to 2.0f),
            "Flying" to mapOf("Electric" to 0.5f, "Grass" to 2.0f, "Fighting" to 2.0f, "Bug" to 2.0f, "Rock" to 0.5f, "Steel" to 0.5f),
            "Psychic" to mapOf("Fighting" to 2.0f, "Poison" to 2.0f, "Psychic" to 0.5f, "Dark" to 0.0f, "Steel" to 0.5f),
            "Bug" to mapOf("Fire" to 0.5f, "Grass" to 2.0f, "Fighting" to 0.5f, "Poison" to 0.5f, "Flying" to 0.5f, "Psychic" to 2.0f, "Ghost" to 0.5f, "Dark" to 2.0f, "Steel" to 0.5f, "Fairy" to 0.5f),
            "Rock" to mapOf("Fire" to 2.0f, "Ice" to 2.0f, "Fighting" to 0.5f, "Ground" to 0.5f, "Flying" to 2.0f, "Bug" to 2.0f, "Steel" to 0.5f),
            "Ghost" to mapOf("Normal" to 0.0f, "Psychic" to 2.0f, "Ghost" to 2.0f, "Dark" to 0.5f),
            "Dragon" to mapOf("Dragon" to 2.0f, "Steel" to 0.5f, "Fairy" to 0.0f),
            "Dark" to mapOf("Fighting" to 0.5f, "Psychic" to 2.0f, "Ghost" to 2.0f, "Dark" to 0.5f, "Fairy" to 0.5f),
            "Steel" to mapOf("Fire" to 0.5f, "Water" to 0.5f, "Electric" to 0.5f, "Ice" to 2.0f, "Rock" to 2.0f, "Steel" to 0.5f, "Fairy" to 2.0f),
            "Fairy" to mapOf("Fire" to 0.5f, "Fighting" to 2.0f, "Poison" to 0.5f, "Dragon" to 2.0f, "Dark" to 2.0f, "Steel" to 0.5f)
        )

        fun calculate(defendingTypes: List<String>): PokemonTypeMatchups {
            val weaknesses = mutableListOf<TypeMultiplier>()
            val resistances = mutableListOf<TypeMultiplier>()
            val immunities = mutableListOf<String>()

            PokemonType.entries.forEach { attackType ->
                var mult = 1.0f
                defendingTypes.forEach { defType ->
                    val factor = TYPE_CHART[attackType.typeName]?.get(defType) ?: 1.0f
                    mult *= factor
                }

                when {
                    mult == 0.0f -> immunities.add(attackType.typeName)
                    mult > 1.0f -> weaknesses.add(TypeMultiplier(attackType.typeName, mult))
                    mult in 0.01f..0.99f -> resistances.add(TypeMultiplier(attackType.typeName, mult))
                }
            }

            weaknesses.sortByDescending { it.multiplier }
            resistances.sortBy { it.multiplier }

            return PokemonTypeMatchups(weaknesses, resistances, immunities)
        }
    }
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
