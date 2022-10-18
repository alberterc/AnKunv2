package com.radx.ankunv2.screens.home

sealed class HomeMenus(var route: String) {
    object Home: HomeMenus("home")
    object PopularWeek: HomeMenus("popular_week")
    object PopularYear: HomeMenus("popular_year")
    object RecentUpdatesSub: HomeMenus("recent_updates_sub")
    object RecentUpdatesDub: HomeMenus("recent_updates_dub")
}
