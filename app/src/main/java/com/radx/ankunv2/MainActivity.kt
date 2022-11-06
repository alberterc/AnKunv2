package com.radx.ankunv2

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.radx.ankunv2.screens.home.HomeScreen
import com.radx.ankunv2.screens.profile.ProfileScreen
import com.radx.ankunv2.screens.search.SearchScreen
import com.radx.ankunv2.screens.season.SeasonScreen
import com.radx.ankunv2.intro.IntroActivity
import com.radx.ankunv2.screens.favorites.FavoritesScreen
import com.radx.ankunv2.ui.theme.AnKunv2Theme


class MainActivity : ComponentActivity() {
    private val firebaseAuth = Firebase.auth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            setContent {
                AnKunApp()
            }
        }
        else {
            startActivity(
                Intent(applicationContext, IntroActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            )
        }
    }
}

@Composable
fun NavigationHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = BottomNavigationItem.Home.route
    ) {
        composable(BottomNavigationItem.Home.route) { HomeScreen() }
        composable(BottomNavigationItem.Season.route) { SeasonScreen() }
        composable(BottomNavigationItem.Search.route) { SearchScreen() }
        composable(BottomNavigationItem.Favorites.route) { FavoritesScreen() }
        composable(BottomNavigationItem.Profile.route) { ProfileScreen() }
    }
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        BottomNavigationItem.Home,
        BottomNavigationItem.Season,
        BottomNavigationItem.Search,
        BottomNavigationItem.Favorites,
        BottomNavigationItem.Profile
    )

    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEachIndexed { _, item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                alwaysShowLabel = true,
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        // Pop up to the start destination of the graph to
                        // avoid building up a large stack of destinations
                        // on the back stack as users select items
                        navController.graph.startDestinationRoute?.let {route ->
                            popUpTo(route) {
                                saveState = true
                            }
                        }

                        // Avoid multiple copies of the same destination when
                        // reselecting the same item
                        launchSingleTop = true

                        // Restore state when reselecting a previously selected item
                        restoreState = true
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnKunApp() {
    val navController = rememberNavController()

    AnKunv2Theme {
        Scaffold(
            bottomBar = { BottomNavigationBar(navController) },
            content = { padding ->
                Box(modifier = Modifier.padding(padding)) {
                    NavigationHost(navController = navController)
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PrevAnKunApp() {
    AnKunApp()
}