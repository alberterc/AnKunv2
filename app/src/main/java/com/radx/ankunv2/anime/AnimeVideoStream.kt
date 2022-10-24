package com.radx.ankunv2.anime

import java.net.URL

object AnimeVideoStream {
    private var episodeList: MutableList<String> = mutableListOf()
    private var episodeMap: MutableMap<String, String> = mutableMapOf()

    fun getEpisodeUrlsMap(episodeID: String): Map<String, String> {
        getEpisodeUrls(episodeID = episodeID)
        return this.episodeMap
    }

    private fun getEpisodeUrls(episodeID: String) {
        val episodeUrls = Utils.animeBaseUrl + "public-api/episode.php?id=" + episodeID

        // get anime episode stream url
        // using "Direct server" or "v.vrv.co"
        val episodeDetailsJSON = URL(episodeUrls).readText()
            .replace("\\", "")
            .replace("[", "")
            .replace("]", "")
            .replace("\"{", "")
            .replace("}\"", "")

        // convert from String into List
        // [UNKNOWN, UNKNOWN, OFFICIAL SOURCES.., UNOFFICIAL SOURCES..]
        episodeList = episodeDetailsJSON
            .split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*\$)".toRegex()) as MutableList<String>
        // remove the first 2 index from list
        episodeList.removeAt(0)
        episodeList.removeAt(0)
        // remove OFFICIAL SOURCES index if null
        if (episodeList[0] == "null") {
            episodeList.removeAt(0)
        }
        // remove last index (episode num)
        val episodeNum = episodeList[episodeList.lastIndex]
        episodeList.removeAt(episodeList.lastIndex)

        // convert from List to Map
        episodeMap = episodeList
            .associate {
                val (key, value) = it.split("\":\"")
                key.replace("\"", "") to value.replace("\"", "")
            } as MutableMap<String, String>
        episodeMap["episodeNum"] = episodeNum
    }
}