package com.radx.ankunv2.anime

import com.radx.ankunv2.Utils
import com.radx.ankunv2.screens.search.animeList
import org.jsoup.Jsoup
import java.net.URL

object AnimeDetails {
    private var animeDetailsMap: MutableMap<String, String> = mutableMapOf()
    private var animeEpisodesList: List<List<String>> = mutableListOf()
    private var animeGenreList: List<String> = listOf("")

    fun getAnimeDetailsForSeveralIDs(animeIDs: List<String>): List<Map<String, String>> {
        val animeList = mutableListOf<Map<String, String>>()
        try {
            animeIDs.forEach { animeID ->
                animeDetailsResponse(animeID = animeID)
                val animeDetails = mapOf(
                    "title" to this.animeDetailsMap["title"]!!,
                    "id" to animeID,
                    "small thumbnail" to this.animeDetailsMap["small thumbnail"]!!,
                    "type" to this.animeDetailsMap["type"]!!,
                    "episode count" to animeDetailsMap["episode count"]!!
                )
                animeList.add(animeDetails)
            }
        } catch (ignored: IndexOutOfBoundsException) {}
        return animeList
    }

    fun getAnimeDetailsList(animeID: String): Map<String, String> {
        try {
            animeDetailsResponse(animeID = animeID)
        } catch (ignored: IndexOutOfBoundsException) {}
        return this.animeDetailsMap
    }

    fun getAnimeEpisodesList(animeID: String): List<List<String>> {
        try {
            animeEpisodesResponse(animeID = animeID)
        } catch (ignored: IndexOutOfBoundsException) {}
        return this.animeEpisodesList
    }

    fun getAnimeGenreList(animeID: String): List<String> {
        try {
            animeGenreResponse(animeID = animeID)
        } catch (ignored: IndexOutOfBoundsException) {}
        return this.animeGenreList
    }

    private fun animeDetailsResponse(animeID: String) {
        val detailsUrl = Utils.animeBaseUrl + animeID

        val webPage = Jsoup.connect(detailsUrl).get()
        val detailsHTML = webPage.select("div.infox")

        animeDetailsMap["title"] = detailsHTML.select("h1.entry-title")[0].text().trim()
        animeDetailsMap["description"] = detailsHTML.select("div.desc")[0].text().trim()
        detailsHTML.select("div.spe").select("span").eachText().forEach { item ->
            if (item.substringBefore(":").trim() == "Status") {
                animeDetailsMap["status"] = item.substringAfter(":").trim()
            }
            else if (item.substringBefore(":").trim() == "Episodes") {
                animeDetailsMap["episode count"] = item.substringAfter(":").trim()
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

    private fun animeEpisodesResponse(animeID: String) {
        val episodesUrl = Utils.animeBaseUrl + "public-api/episodes.php?id=${animeID}"

        // get anime episodes data from JSON response
        // retrieves JSON response in String data type
        val animeEpisodesJSONStr = URL(episodesUrl).readText()
            .replace("\\", "")
            .replace("[", "")
            .replace("]", "")
        // convert from String into List of List
        // [[UNKNOWN, EPISODE ID, EPISODE NUMBER, EPISODE RELEASE TIME]]
        animeEpisodesList = animeEpisodesJSONStr
            .split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*\$)".toRegex())
            .chunked(4)
    }

    private fun animeGenreResponse(animeID: String) {
        val detailsUrl = Utils.animeBaseUrl + animeID

        val webPage = Jsoup.connect(detailsUrl).get()
        animeGenreList = webPage.select("div.infox").select("div.genxed").select("span")[0].select("a").eachText()
    }
}