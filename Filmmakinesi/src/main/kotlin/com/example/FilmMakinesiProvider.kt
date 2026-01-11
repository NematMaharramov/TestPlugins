package com.example

import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.utils.*

class FilmMakinesiProvider : MainAPI() {
    override var mainUrl = "https://filmmakinesi.to" 
    override var name = "Film Makinesi"
    override val supportedTypes = setOf(TvType.Movie, TvType.TvSeries)

    override var lang = "tr"

    override val hasMainPage = true

    override suspend fun getMainPage(page: Int, request: MainPageRequest): HomePageResponse? {
        val document = app.get(mainUrl).document
        val items = document.select(".film-list .film-item").mapNotNull {
            val title = it.selectFirst(".title")?.text() ?: return@mapNotNull null
            val href = it.selectFirst("a")?.attr("href") ?: return@mapNotNull null
            val poster = it.selectFirst("img")?.attr("data-src") ?: it.selectFirst("img")?.attr("src")
            
            newMovieSearchResponse(title, href, TvType.Movie) {
                this.posterUrl = poster
            }
        }
        return newHomePageResponse("Recent Movies", items)
    }

    override suspend fun search(query: String): List<SearchResponse> {
        val document = app.get("$mainUrl/?s=$query").document
        return document.select(".film-list .film-item").mapNotNull {
            val title = it.selectFirst(".title")?.text() ?: return@mapNotNull null
            val href = it.selectFirst("a")?.attr("href") ?: return@mapNotNull null
            val poster = it.selectFirst("img")?.attr("data-src") ?: it.selectFirst("img")?.attr("src")

            newMovieSearchResponse(title, href, TvType.Movie) {
                this.posterUrl = poster
            }
        }
    }
}
