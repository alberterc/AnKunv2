package com.radx.ankunv2.anime

import org.jsoup.Jsoup

object AnimeDetails {
    private var animeDetailsMap: MutableMap<String, String> = mutableMapOf()
    private var animeGenreList: List<String> = listOf("")

    fun getAnimeDetailsList(animeID: String): Map<String, String> {
        getAnimeDetails(animeID = animeID)
        return this.animeDetailsMap
    }

    fun getAnimeGenreList(animeID: String): List<String> {
        getAnimeGenre(animeID = animeID)
        return this.animeGenreList
    }

    private fun getAnimeDetails(animeID: String) {
        val detailsUrl = Utils.animeBaseUrl + animeID

        val webPage = Jsoup.connect(detailsUrl).get()
        val detailsHTML = webPage.select("div.infox")

        animeDetailsMap["title"] = detailsHTML.select("h1.entry-title")[0].text().trim()
        animeDetailsMap["description"] = detailsHTML.select("div.desc")[0].text().trim()
        detailsHTML.select("div.spe").select("span").eachText().forEach { item ->
            if (item.substringBefore(":").trim() == "Status") {
                animeDetailsMap["status"] = item.substringAfter(":").trim()
            }
            else if (item.substringBefore(":").trim() == "Type") {
                animeDetailsMap["type"] = item.substringAfter(":").trim()
            }
            else if (item.substringBefore(":").trim() == "Season") {
                animeDetailsMap["season"] = item.substringAfter(":").trim()
            }
        }
        animeDetailsMap["small thumbnail"] = webPage.select("div.thumbook").select("img")[0].absUrl("src")
        animeDetailsMap["large thumbnail"] = webPage.select("div.bigcover").select("div").attr("style")
            .substringAfter("\'", "")
            .replace("\'", "")
            .replace(")", "")
            .replace(";", "")
            .trim()
    }

    private fun getAnimeGenre(animeID: String) {
        val detailsUrl = Utils.animeBaseUrl + animeID

        val webPage = Jsoup.connect(detailsUrl).get()
        animeGenreList = webPage.select("div.infox").select("div.genxed").select("span")[0].select("a").eachText()
    }
}