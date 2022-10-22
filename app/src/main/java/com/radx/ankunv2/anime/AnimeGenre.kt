package com.radx.ankunv2.anime

object AnimeGenre {
    private var allGenreList: List<String> = listOf(
        "Action", "Adventure", "Cars", "Comedy",
        "Dementia", "Demons", "Drama", "Ecchi",
        "Fantasy", "Game", "Harem", "Historical",
        "Horror", "Josei", "Kids", "Magic",
        "Martial Arts", "Mecha", "Military", "Music",
        "Mystery", "Parody", "Police", "Psychological",
        "Romance", "Samurai", "School", "Sci-Fi",
        "Seinen", "Shoujo", "Shoujo Ai", "Shounen",
        "Shounen Ai", "Slice of Life", "Space", "Sports",
        "Super Power", "Supernatural", "Thriller", "Vampire",
        "Yaoi", "Yuri"
    )

    fun getAllGenderList(): List<String> {
        return this.allGenreList
    }
}