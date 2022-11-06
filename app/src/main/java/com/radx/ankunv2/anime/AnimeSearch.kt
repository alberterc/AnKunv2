package com.radx.ankunv2.anime

import com.radx.ankunv2.Utils
import java.net.URL

object AnimeSearch {
    private var searchResultList: List<List<String>> = mutableListOf()

    fun getSearchResultList(
        search: String = "", season: String = "", genres: String = "",
        dub: String = "", airing: String = "", sort: String = "popular-week",
        page: String = "1"
    ): List<List<String>> {
        getSearchResult(
            search = search, season = season, genres = genres,
            dub = dub, airing = airing, sort = sort,
            page = page
        )
        return this.searchResultList
    }

    private fun getSearchResult(
        search: String = "", season: String = "", genres: String = "",
        dub: String = "", airing: String = "", sort: String = "popular-week",
        page: String = "1"
    ) {
         val searchUrl =
            "/public-api" +
                    "/search.php?search_text=" + search +
                    "&season=" + season +
                    "&genres=" + genres +
                    "&dub=" + dub +
                    "&airing=" + airing +
                    "&sort=" + sort +
                    "&page=" + page

        // get anime search result data from JSON response
        // retrieves JSON response in String data type
        val animeSearchListJSONStr = URL(Utils.animeBaseUrl + searchUrl).readText()
            .replace("\\", "")
            .replace("[", "")
            .replace("]", "")
        // convert from String into List of List
        // [[Anime Title, Anime ID, ANIME THUMBNAIL, SUB OR DUB (sub=0, dub=1]]
        searchResultList = animeSearchListJSONStr
            .split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*\$)".toRegex())
            .chunked(4)
    }
}