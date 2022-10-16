package com.radx.ankunv2.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.radx.ankunv2.anime.AnimeSearch
import com.radx.ankunv2.anime.AnimeSeasons
import com.radx.ankunv2.ui.theme.Grey
import com.radx.ankunv2.ui.theme.PurpleGrey40
import com.radx.ankunv2.ui.theme.Transparent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeasonScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp, 0.dp, 16.dp, 0.dp)
    ) {
        var dropdownShowMenu by remember { mutableStateOf(false) }
        var dropdownSelectedItem by remember { mutableStateOf(dropdownItems[0]) }
        var columnItemsState by remember { mutableStateOf(listOf(listOf(""))) }
        LaunchedEffect(true) {
            getSeasonList() // get season list in background
            dropdownSelectedItem = dropdownItems[0]
        }

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
                onExpandedChange = { dropdownShowMenu = !dropdownShowMenu },
                modifier = Modifier
                    .padding(0.dp, 0.dp, 0.dp, 16.dp)
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                        .height(60.dp),
                    readOnly = true,
                    value = dropdownSelectedItem,
                    onValueChange = { },
                    label = { Text("Seasons") },
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
                        unfocusedTrailingIconColor = Grey,
                        unfocusedLabelColor = PurpleGrey40,
                        focusedLabelColor = PurpleGrey40
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
                            enabled = true,
                            onClick = {
                                dropdownSelectedItem = item
                                dropdownShowMenu = false
                            },
                            text = {
                                Text(
                                    text = item
                                )
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                        )
                    }
                }
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 160.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items(columnItemsState) {item ->
                if (columnItemsState.size != 1) {
                    AnimeSeasonListCardItem(item)
                }
            }
        }

        LaunchedEffect(dropdownShowMenu) {
            launch {
                snapshotFlow { dropdownSelectedItem }
                    .apply {
                        try {
                            getSearchResultList(season = dropdownSelectedItem)
                            columnItemsState = columnItems
                        } catch (ignored: IndexOutOfBoundsException) {}
                    }
            }
        }
    }
}

@Composable
fun AnimeSeasonListCardItem(anime: List<String>) {
    // [[Anime Title, Anime ID, ANIME THUMBNAIL, SUB OR DUB (sub=0, dub=1]]
    val title = anime[0].replace("\"", "")
    val id = anime[1]
    val thumbnailUrl = anime[2].replace("\"", "")
    val isDub = anime[3]

    Card(
        modifier = Modifier
            .height(250.dp)
            .width(160.dp),
        shape = RoundedCornerShape(18.dp)
    ) {
        Box {
            Image(
                painter = rememberAsyncImagePainter(thumbnailUrl),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.FillBounds
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Transparent,
                                Color.Black
                            ),
                            startY = 0f,
                            endY = 1000f
                        )
                    )
            ) {
                if (isDub == "1") {
                    Card(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(0.dp, 8.dp, 8.dp, 0.dp),
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Text(
                            modifier = Modifier
                                .background(color = MaterialTheme.colorScheme.onSecondaryContainer)
                                .padding(5.dp, 2.dp, 5.dp, 2.dp),
                            text = "DUB",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Text(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(8.dp, 0.dp, 8.dp, 15.dp),
                    text = title,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

// dropdown menu (anime seasons) variables
var dropdownItems = listOf("")
suspend fun getSeasonList() = withContext(Dispatchers.IO) {
    fillSeasonList()
}
fun fillSeasonList() {
    dropdownItems = AnimeSeasons.getSeasonsList().subList(0, 10) // longer list may causes fps drops
}

// lazy column (anime list) variables
var columnItems = listOf(listOf(""))
suspend fun getSearchResultList(
    search: String = "", season: String = "", genres: String = "",
    dub: String = "", airing: String = "", sort: String = "popular-week",
    page: String = "1"
) = withContext(Dispatchers.IO) {
    fillSearchResultList(season = season)
}
fun fillSearchResultList(
    search: String = "", season: String = "", genres: String = "",
    dub: String = "", airing: String = "", sort: String = "popular-week",
    page: String = "1"
) {
    columnItems = AnimeSearch.getSearchResultList(season = season)
}