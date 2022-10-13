package com.radx.ankunv2

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
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
import com.radx.ankunv2.ui.theme.AnKunv2Theme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AnKunApp()
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

@Preview(
    name = "Light Mode",
    showBackground = true
)
@Preview(
    name = "Dark Mode",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true
)
@Composable
fun PrevBottomNavBar() {
    AnKunApp()
}