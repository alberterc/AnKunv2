package com.radx.ankunv2

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.google.accompanist.systemuicontroller.SystemUiController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.radx.ankunv2.ui.theme.AnKunv2Theme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Hide the status bar.
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        // Remember that you should never show the action bar if the
        // status bar is hidden, so hide that too if necessary.
        actionBar?.hide()

        setContent {
            AnKunApp()
        }
    }
}

@Composable
fun BottomNavigationBar() {
    var selectedItem by remember { mutableStateOf(0) }
    val items = listOf(
        BottomNavigationItem.Home,
        BottomNavigationItem.Season,
        BottomNavigationItem.Search,
        BottomNavigationItem.Favorites,
        BottomNavigationItem.Profile
    )

    NavigationBar {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                alwaysShowLabel = true,
                selected = selectedItem == index,
                onClick = { selectedItem = index }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnKunApp() {
//    val systemUIController: SystemUiController = rememberSystemUiController()
//    systemUIController.isStatusBarVisible = false

    AnKunv2Theme {
        Scaffold(
            bottomBar = { BottomNavigationBar() },
            content = { padding ->
                Text(
                    text = "TES",
                    modifier = Modifier.padding(padding)
                )
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
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
    AnKunv2Theme {
        Scaffold(
            bottomBar = { BottomNavigationBar() },
            content = { padding ->
                Text(
                    text = "TES",
                    modifier = Modifier.padding(padding)
                )
            }
        )
    }
}