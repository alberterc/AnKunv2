package com.radx.ankunv2.screens.home

sealed class Screens(var route: String) {
    object Home: Screens("home")
    object PopularWeek: Screens("popular_week")
    object PopularYear: Screens("popular_year")
    object RecentUpdatesSub: Screens("recent_updates_sub")
    object RecentUpdatesDub: Screens("recent_updates_dub")
}
