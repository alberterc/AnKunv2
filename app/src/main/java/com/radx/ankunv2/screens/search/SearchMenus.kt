package com.radx.ankunv2.screens.search

sealed class SearchMenus(var route: String) {
    object Search: SearchMenus(route = "search")
}
