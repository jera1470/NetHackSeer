package com.example.nethackseer.ui.homescreen

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class FeaturedPage(
    val id: String,
    val name: String,
    val summary: String,
    val type: String //"Monster", "Item" as examples, for later use as well for filtering
)

class HomeViewModel : ViewModel() {
    // again, hardcoded for now, later will be fetched from a local database
    private val allFeaturedPages = listOf(
        FeaturedPage(
            id = "lichen",
            name = "lichen",
            summary = "A type of slow-moving, plant-like fungus. " +
                    "It is weak and can be killed easily. Its only attack is to stick on you.",
            type = "Monster of the Day"
        ),
        FeaturedPage(
            id = "ring of conflict",
            name = "ring of conflict",
            summary = "A powerful magical ring that causes monsters to fight each other.",
            type = "Item of the Day"
        ),
        FeaturedPage(
            id = "goblin",
            name = "goblin",
            summary = "A small and weak humanoid monster, often found " +
                    "in the early levels of the dungeon.",
            type = "Monster of the Day"
        ),
        FeaturedPage(
            id = "magic lamp",
            name = "magic lamp",
            summary = "A rare item that contains a djinni for the chance to get a wish!",
            type = "Item of the Day"
        )
    )

    // gotta love stateflow, makes my life easier
    // stateflow is used to store the current state of the UI in this case
    private val _pageOfTheDay = MutableStateFlow<FeaturedPage?>(null)
    val pageOfTheDay: StateFlow<FeaturedPage?> = _pageOfTheDay

    init {
        pickRandomPage()
    }

    private fun pickRandomPage() {
        _pageOfTheDay.value = allFeaturedPages.random()
    }
}
