package com.radx.ankunv2.anime

import com.radx.ankunv2.Utils
import org.jsoup.Jsoup

object AnimeSeasons {
    private const val baseUrl = Utils.animeBaseUrl + "seasons"
    private var seasons: List<String> = emptyList()

    fun getSeasonsList(): List<String> {
        getSeasons()
        return this.seasons
    }

    private fun getSeasons() {
        val webPage = Jsoup.connect(baseUrl).get()
        seasons = webPage.select("ul.taxindex").select("span.name").eachText()
    }
}