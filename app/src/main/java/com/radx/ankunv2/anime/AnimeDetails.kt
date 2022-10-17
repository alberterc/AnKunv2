package com.radx.ankunv2.anime

import android.util.Log
import it.skrape.core.htmlDocument
import it.skrape.fetcher.HttpFetcher
import it.skrape.fetcher.response
import it.skrape.fetcher.skrape
import it.skrape.selects.attribute
import it.skrape.selects.eachHref
import it.skrape.selects.eachText
import it.skrape.selects.html5.*
import it.skrape.selects.text
import javax.net.ssl.SSLException

object AnimeDetails {
    private var animeDetailsMap: MutableMap<String, String> = mutableMapOf()
    private var animeGenreList: List<String> = listOf("")

    fun getAnimeDetailsList(animeID: String): Map<String, Any> {
        getAnimeDetails(animeID = animeID)
        return this.animeDetailsMap
    }

    fun getAnimeGenreList(animeID: String): List<String> {
        getAnimeGenre(animeID = animeID)
        return this.animeGenreList
    }

    private fun getAnimeDetails(animeID: String) {
        val detailsUrl = Utils.animeBaseUrl + animeID

        skrape(HttpFetcher) {
            request {
                url = detailsUrl
            }

            // anime details
            response {
                htmlDocument {
                    div {
                        withClass = "infox"
                        findFirst {
                            // title
                            h1 {
                                withClass = "entry-title"
                                findFirst {
                                    animeDetailsMap["title"] = text.trim()
                                }
                            }

                            // description
                            div {
                                withClass = "desc"
                                findFirst {
                                    animeDetailsMap["description"] = text.trim()
                                }
                            }

                            // check if anime details is available
                            div {
                                withClass = "spe"
                                findFirst {
                                    span {
                                        findAll {
                                            eachText.forEach { item ->
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
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // small thumbnail
                    div {
                        withClass = "thumbook"
                        findFirst {
                            img {
                                findFirst {
                                    animeDetailsMap["small thumbnail"] = attribute("src").trim()
                                }
                            }
                        }
                    }

                    // large thumbnail
                    div {
                        withClass = "bigcover"
                        findFirst {
                            div {
                                findFirst {
                                    Log.e("THUMB", attribute(
                                        "style='background-image: url\\()[^\\)]+'"
                                    ))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun getAnimeGenre(animeID: String) {
        val detailsUrl = Utils.animeBaseUrl + animeID

        skrape(HttpFetcher) {
            request {
                url = detailsUrl
            }

            // anime details
            try {
                response {
                    htmlDocument {
                        div {
                            withClass = "infox"
                            findFirst {
                                // genre
                                div {
                                    withClass = "genxed"
                                    findFirst {
                                        span {
                                            findFirst {
                                                a {
                                                    findAll {
                                                        animeGenreList = eachText
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (ignored: SSLException) {}
        }
    }
}