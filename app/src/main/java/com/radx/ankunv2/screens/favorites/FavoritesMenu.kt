package com.radx.ankunv2.screens.favorites

sealed class FavoritesMenu(var route: String) {
    object Favorites: FavoritesMenu(route = "favorites")
}
