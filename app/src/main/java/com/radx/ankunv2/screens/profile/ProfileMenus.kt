package com.radx.ankunv2.screens.profile

sealed class ProfileMenus(var route: String) {
    object Profile: ProfileMenus(route = "profile")
}
