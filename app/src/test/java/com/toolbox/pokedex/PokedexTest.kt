package com.toolbox.pokedex

import com.toolbox.dashboard.ToolCategory
import com.toolbox.dashboard.allTools
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

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
            color = "Green",
            generation = 1,
        )

        assertEquals(318, bulbasaur.totalStats)
        assertEquals(2, bulbasaur.types.size)
        assertEquals(PokemonType.Grass.color, bulbasaur.primaryTypeColor)
        assertEquals(1, bulbasaur.generation)
        assertEquals("Green", bulbasaur.color)
    }

    @Test
    fun testPokemonTypeFromString() {
        assertEquals(PokemonType.Fire, PokemonType.fromString("Fire"))
        assertEquals(PokemonType.Water, PokemonType.fromString("water"))
        assertEquals(PokemonType.Electric, PokemonType.fromString("ELECTRIC"))
        assertEquals(null, PokemonType.fromString("NonExistentType"))
    }

    @Test
    fun testPokemonFormsAndLegendsZa() {
        val megaZygarde = PokemonForm(
            name = "Mega Zygarde",
            types = listOf("Dragon", "Ground"),
            hp = 216,
            atk = 120,
            def = 141,
            spAtk = 111,
            spDef = 115,
            spd = 105,
            isMega = true,
            isLegendsZa = true,
        )

        assertEquals(808, megaZygarde.totalStats)
        assertTrue(megaZygarde.isMega)
        assertTrue(megaZygarde.isLegendsZa)
        assertEquals(PokemonType.Dragon.color, megaZygarde.primaryTypeColor)

        val floetteEternal = PokemonForm(
            name = "Floette Eternal Flower",
            types = listOf("Fairy"),
            hp = 74,
            atk = 65,
            def = 67,
            spAtk = 125,
            spDef = 128,
            spd = 92,
            isLegendsZa = true,
        )
        assertEquals(551, floetteEternal.totalStats)
        assertTrue(floetteEternal.isLegendsZa)
    }

    @Test
    fun testGenderDifferencesModel() {
        val pikachu = Pokemon(
            id = 25,
            name = "Pikachu",
            types = listOf("Electric"),
            hp = 35,
            atk = 55,
            def = 40,
            spAtk = 50,
            spDef = 50,
            spd = 90,
            hasGenderDiff = true,
            genderDiffDesc = "Female's tail ends in a heart shape.",
            color = "Yellow",
            generation = 1,
        )

        assertTrue(pikachu.hasGenderDiff)
        assertTrue(pikachu.genderDiffDesc.contains("heart shape"))
        assertEquals("Yellow", pikachu.color)
    }

    @Test
    fun testPokedexJsonFileIntegrity() {
        val jsonFile = File("src/main/assets/pokedex.json")
        val altFile = File("app/src/main/assets/pokedex.json")
        val target = if (jsonFile.exists()) jsonFile else altFile
        assertTrue("pokedex.json must exist", target.exists())

        val jsonStr = target.readText()
        val jsonParser = Json {
            ignoreUnknownKeys = true
            isLenient = true
        }

        val list = jsonParser.decodeFromString<List<Pokemon>>(jsonStr)
        assertTrue("Dataset must contain at least 1025 entries for complete Gen 9", list.size >= 1025)

        val first = list.first()
        assertEquals(1, first.id)
        assertEquals("Bulbasaur", first.name)

        val last = list.find { it.id == 1025 }
        assertNotNull("Pecharunt #1025 must exist", last)
        assertEquals("Pecharunt", last?.name)
        assertEquals(9, last?.generation)

        // Check Legends: Z-A Mega Evolutions exist in forms
        val zygarde = list.find { it.id == 718 }
        assertNotNull(zygarde)
        assertTrue(zygarde!!.forms.any { it.name == "Mega Zygarde" && it.isLegendsZa })

        val meganium = list.find { it.id == 154 }
        assertNotNull(meganium)
        assertTrue(meganium!!.forms.any { it.name == "Mega Meganium" && it.isLegendsZa })

        val feraligatr = list.find { it.id == 160 }
        assertNotNull(feraligatr)
        assertTrue(feraligatr!!.forms.any { it.name == "Mega Feraligatr" && it.isLegendsZa })

        val emboar = list.find { it.id == 500 }
        assertNotNull(emboar)
        assertTrue(emboar!!.forms.any { it.name == "Mega Emboar" && it.isLegendsZa })

        // Check gender differences count
        val genderDiffCount = list.count { it.hasGenderDiff }
        assertTrue("Should contain 100+ species with gender differences", genderDiffCount >= 100)

        // Check unique colors
        val uniqueColors = list.map { it.color }.distinct()
        assertTrue("Should cover multiple colors", uniqueColors.size >= 8)
    }

    @Test
    fun testToolCategoryReorganization() {
        val educationTools = allTools.filter { it.category == ToolCategory.Education }
        val gamesTools = allTools.filter { it.category == ToolCategory.Games }
        val otherTools = allTools.filter { it.category == ToolCategory.Others }

        assertTrue("Education category must contain education tools", educationTools.isNotEmpty())
        assertTrue("Games category must contain games tools", gamesTools.isNotEmpty())

        assertTrue(educationTools.any { it.id == "education_hub" })
        assertTrue(educationTools.any { it.id == "formula_reference" })
        assertTrue(gamesTools.any { it.id == "pokedex" })
        assertTrue(gamesTools.any { it.id == "random_generator" })

        // Others should no longer have miscellaneous misplaced tools
        assertFalse(otherTools.any { it.id == "pokedex" })
        assertFalse(otherTools.any { it.id == "education_hub" })
        assertFalse(otherTools.any { it.id == "heart_rate" })
        assertFalse(otherTools.any { it.id == "paint_calculator" })
    }
    @Test
    fun testPokemonGenderClassification() {
        assertEquals(PokemonGenderClassification.Genderless, PokemonGenderClassification.forPokemon("Mewtwo"))
        assertEquals(PokemonGenderClassification.Genderless, PokemonGenderClassification.forPokemon("Magnemite"))
        assertEquals(PokemonGenderClassification.FemaleOnly, PokemonGenderClassification.forPokemon("Chansey"))
        assertEquals(PokemonGenderClassification.MaleOnly, PokemonGenderClassification.forPokemon("Tauros"))
        assertEquals(PokemonGenderClassification.StarterRatio, PokemonGenderClassification.forPokemon("Bulbasaur"))
        assertEquals(PokemonGenderClassification.MaleAndFemaleEqual, PokemonGenderClassification.forPokemon("Pikachu"))

        // Form gender classification
        val megaGallade = PokemonForm(
            name = "Mega Gallade",
            types = listOf("Psychic", "Fighting"),
            hp = 68,
            atk = 165,
            def = 95,
            spAtk = 65,
            spDef = 115,
            spd = 110,
            isMega = true
        )
        assertEquals(PokemonGenderClassification.MaleOnly, megaGallade.genderClassification)
    }

    @Test
    fun testSeparateEducationAndGamesTools() {
        val periodicTable = allTools.find { it.id == "periodic_table" }
        assertNotNull(periodicTable)
        assertEquals(ToolCategory.Education, periodicTable?.category)

        val chemistryGames = allTools.find { it.id == "chemistry_games" }
        assertNotNull(chemistryGames)
        assertEquals(ToolCategory.Games, chemistryGames?.category)

        val scienceLabs = allTools.find { it.id == "science_labs" }
        assertNotNull(scienceLabs)
        assertEquals(ToolCategory.Education, scienceLabs?.category)

        val brainGames = allTools.find { it.id == "brain_games" }
        assertNotNull(brainGames)
        assertEquals(ToolCategory.Games, brainGames?.category)
    }
}
