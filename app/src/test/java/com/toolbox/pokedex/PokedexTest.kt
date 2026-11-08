package com.toolbox.pokedex

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PokedexTest {

    @Test
    fun testPokemonModelTotalStats() {
        val bulbasaur = Pokemon(
            id = 1,
            name = "Bulbasaur",
            types = listOf("Grass", "Poison"),
            hp = 45,
            atk = 49,
            def = 49,
            spAtk = 65,
            spDef = 65,
            spd = 45,
            species = "Seed Pokémon",
            description = "A strange seed was planted on its back at birth.",
            height = "0.7 m",
            weight = "6.9 kg",
        )

        assertEquals(318, bulbasaur.totalStats)
        assertEquals(2, bulbasaur.types.size)
        assertEquals(PokemonType.Grass.color, bulbasaur.primaryTypeColor)
    }

    @Test
    fun testPokemonTypeFromString() {
        assertEquals(PokemonType.Fire, PokemonType.fromString("Fire"))
        assertEquals(PokemonType.Water, PokemonType.fromString("water"))
        assertEquals(PokemonType.Electric, PokemonType.fromString("ELECTRIC"))
        assertEquals(null, PokemonType.fromString("NonExistentType"))
    }
}
