package com.radx.ankunv2.anime

import com.radx.ankunv2.Utils
import java.net.URL

object AnimeSlider {
    private var sliderList: List<List<String>> = mutableListOf()

    fun getSliderResultList(): List<List<String>> {
        getSliderResult()
        return this.sliderList
    }

    private fun getSliderResult() {
        val sliderUrl = "public-api/slider.php"

        // get anime search result data from JSON response
        // retrieves JSON response in String data type
        val animeSliderListJSONStr = URL(Utils.animeBaseUrl + sliderUrl).readText()
            .replace("\\", "")
            .replace("[", "")
            .replace("]", "")
        // convert from String into List of List
        // [[Anime Title, Anime ID, ANIME THUMBNAIL, SUB OR DUB (sub=0, dub=1]]
        sliderList = animeSliderListJSONStr
            .split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*\$)".toRegex())
            .chunked(3)
    }
}