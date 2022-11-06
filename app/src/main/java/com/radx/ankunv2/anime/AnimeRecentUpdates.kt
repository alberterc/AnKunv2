package com.radx.ankunv2.anime

import com.radx.ankunv2.Utils
import java.net.URL

object AnimeRecentUpdates {
    private var recentUpdatesList: List<List<String>> = mutableListOf()

    fun getRecentUpdatesList(mode: String = "sub", page: String = "1") : List<List<String>> {
        getRecentUpdates(mode = mode, page = page)
        return this.recentUpdatesList
    }

    private fun getRecentUpdates(page: String = "1", mode: String = "sub") {
        val recentUpdatesUrl = "/public-api/index.php?" +
                "page=" + page +
                "&mode=" + mode

        // get anime latest list data from JSON response
        // retrieves JSON response in String data type
        val animeRecentUpdatesJSONStr = URL(Utils.animeBaseUrl + recentUpdatesUrl).readText()
            .replace("\\", "")
            .replace("[", "")
            .replace("]", "")
        // convert from String into List of List
        // [[Anime Title, Anime ID, UNKNOWN, TOTAL EP, ANIME THUMBNAIL, LAST EP RELEASE TIME]]
        recentUpdatesList = animeRecentUpdatesJSONStr
            .split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*\$)".toRegex())
            .chunked(6)
    }
}