package com.radx.ankunv2.intro

sealed class IntroMenus(var route: String) {
    object SignIn: IntroMenus(route = "sign_in")
    object SignUp: IntroMenus(route = "sign_up")
}
