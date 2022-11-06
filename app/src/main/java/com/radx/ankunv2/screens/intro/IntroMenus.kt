package com.radx.ankunv2.screens.intro

sealed class IntroMenus(var route: String) {
    object SignIn: IntroMenus(route = "sign_in")
    object SignUp: IntroMenus(route = "sign_up")
    object MainApp: IntroMenus(route = "main_app")
}
