package com.radx.ankunv2.screens.season

sealed class SeasonMenus(var route: String) {
    object Season: SeasonMenus(route = "season")
}
