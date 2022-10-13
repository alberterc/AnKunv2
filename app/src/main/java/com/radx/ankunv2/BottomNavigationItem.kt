package com.radx.ankunv2

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavigationItem(
    var route: String,
    var icon: ImageVector,
    var label: String
) {
    object Home: BottomNavigationItem("home", Icons.Outlined.Home, "Home")
    object Season: BottomNavigationItem("season", Icons.Outlined.Info, "Season")
    object Search: BottomNavigationItem("search", Icons.Outlined.Search, "Search")
    object Favorites: BottomNavigationItem("favorites", Icons.Outlined.FavoriteBorder, "Favorites")
    object Profile: BottomNavigationItem("profile", Icons.Outlined.Person, "Profile")
}
