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
