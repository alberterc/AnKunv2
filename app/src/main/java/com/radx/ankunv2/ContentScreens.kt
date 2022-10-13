@file:OptIn(ExperimentalMaterial3Api::class)

package com.radx.ankunv2

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.radx.ankunv2.anime.AnimeSeasons
import com.radx.ankunv2.ui.theme.*

@Composable
fun HomeScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Home Screen",
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun SeasonScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp, 0.dp, 16.dp, 0.dp)
    ) {
        // dropdown menu (anime seasons) variables
        var dropdownShowMenu by remember { mutableStateOf(false) }
        val dropdownItems = AnimeSeasons.getSeasonsList().subList(0, 10) // longer list causes fps drops
        var dropdownSelectedItem by remember { mutableStateOf(dropdownItems[0]) }

        // lazy column (anime list) variables
        val columnItems = listOf("A", "B", "C")

        Text(
            text = "Seasons",
            fontSize = 22.sp,
            modifier = Modifier
                .padding(PaddingValues(bottom = 16.dp))
        )

        MaterialTheme(
            shapes = MaterialTheme.shapes.copy(extraSmall = RoundedCornerShape(30.dp))
        ) {
            ExposedDropdownMenuBox(
                expanded = dropdownShowMenu,
                onExpandedChange = { dropdownShowMenu = !dropdownShowMenu }
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                        .height(56.dp),
                    readOnly = true,
                    value = dropdownSelectedItem,
                    onValueChange = { },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = dropdownShowMenu
                        )
                    },
                    colors = ExposedDropdownMenuDefaults.textFieldColors(
                        containerColor = Transparent,
                        focusedIndicatorColor = Grey,
                        unfocusedIndicatorColor = Grey,
                        focusedTrailingIconColor = Grey,
                        unfocusedTrailingIconColor = Grey
                    )
                )
                ExposedDropdownMenu(
                    expanded = dropdownShowMenu,
                    onDismissRequest = { dropdownShowMenu = false },
                    modifier = Modifier
                        .requiredSizeIn(maxHeight = 330.dp)
                ) {
                    dropdownItems.forEach { item ->
                        DropdownMenuItem(
                            onClick = {
                                dropdownSelectedItem = item
                                dropdownShowMenu = false
                            },
                            text = {
                                Text(
                                    text = item
                                )
                            },
                            // contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                        )
                    }
                }
            }
        }

        LazyColumn {
            items(
                count = columnItems.size,
                itemContent = {
                    AnimeSeasonListItem(anime = it)
                }
            )
        }
    }
}

@Composable
fun AnimeSeasonListItem(anime: Int) {
    Row {
        Column {
            Text(
                text = anime.toString()
            )
        }
    }
}

@Composable
fun SearchScreen() {
    Column {
        Text(
            text = "Search Screen",
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
fun FavoritesScreen() {
    Column {
        Text(
            text = "Favorites Screen",
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
fun ProfileScreen() {
    Column {
        Text(
            text = "Profile Screen",
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewScreen() {
    AnKunv2Theme {
        SeasonScreen()
    }
}

//@Preview(showBackground = true)
//@Composable
//fun PreviewHomeScreen() {
//    AnKunv2Theme {
//        HomeScreen()
//    }
//}
//
//@Preview(showBackground = true)
//@Composable
//fun PreviewSeasonScreen() {
//    AnKunv2Theme {
//        SeasonScreen()
//    }
//}
//
//@Preview(showBackground = true)
//@Composable
//fun PreviewSearchScreen() {
//    AnKunv2Theme {
//        SearchScreen()
//    }
//}
//
//@Preview(showBackground = true)
//@Composable
//fun PreviewFavoritesScreen() {
//    AnKunv2Theme {
//        FavoritesScreen()
//    }
//}
//
//@Preview(showBackground = true)
//@Composable
//fun PreviewProfileScreen() {
//    AnKunv2Theme {
//        ProfileScreen()
//    }
//}