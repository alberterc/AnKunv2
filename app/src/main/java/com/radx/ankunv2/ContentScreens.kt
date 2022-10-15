@file:OptIn(ExperimentalMaterial3Api::class)

package com.radx.ankunv2

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.Card
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import coil.compose.rememberAsyncImagePainter
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.calculateCurrentOffsetForPage
import com.google.accompanist.pager.rememberPagerState
import com.radx.ankunv2.anime.AnimeSearch
import com.radx.ankunv2.anime.AnimeSeasons
import com.radx.ankunv2.anime.AnimeSlider
import com.radx.ankunv2.ui.theme.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlin.math.absoluteValue

@OptIn(ExperimentalPagerApi::class)
@Composable
fun HomeScreen() {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        var sliderItemsState by remember { mutableStateOf(listOf(listOf(""))) }
        val pagerState = rememberPagerState()

        LaunchedEffect(true) {
            getSliderList()
            sliderItemsState = sliderItems
        }

        if (sliderItemsState.size != 1) {
            HorizontalPager(
                count = sliderItemsState.size,
                state = pagerState,
                userScrollEnabled = true
            ) { page ->
                // [[ANIME ID, ANIME TITLE, ANIME THUMBNAIL]]
                val title = sliderItemsState[page][1].replace("\"", "")
                val thumbnailUrl = sliderItemsState[page][2].replace("\"", "")
                // Calculate the absolute offset for the current page from the
                // scroll position. We use the absolute value which allows us to mirror
                // any effects for both directions
                val pageOffset = calculateCurrentOffsetForPage(page).absoluteValue

                HomeSliderCard(title, thumbnailUrl, pageOffset)
            }

            LaunchedEffect(pagerState.currentPage) {
                delay(5000)
                var newPage = pagerState.currentPage + 1
                if (newPage > sliderItemsState.lastIndex) newPage = 0
                pagerState.animateScrollToPage(newPage)
            }
        }
    }
}

@Composable
fun HomeSliderCard(title: String, thumbnailUrl: String, pageOffset: Float) {
    Card(
        modifier = Modifier
            .graphicsLayer {
                // We animate the scaleX + scaleY, between 85% and 100%
                lerp(
                    start = 0.85f,
                    stop = 1f,
                    fraction = 1f - pageOffset.coerceIn(0f, 1f)
                ).also { scale ->
                    scaleX = scale
                    scaleY = scale
                }

                // We animate the alpha, between 50% and 100%
                alpha = lerp(
                    start = 0.5f,
                    stop = 1f,
                    fraction = 1f - pageOffset.coerceIn(0f, 1f)
                )
            }
            .fillMaxWidth()
            .height(330.dp),
        shape = RoundedCornerShape(0.dp)
    ) {
        Box {
            Image(
                painter = rememberAsyncImagePainter(thumbnailUrl),
                contentDescription = null,
                modifier = Modifier.fillMaxHeight(),
                contentScale = ContentScale.Crop
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
                            endY = 1150f
                        )
                    )
            ) {
                Text(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(8.dp, 0.dp, 8.dp, 15.dp),
                    text = title,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

// dropdown menu (anime seasons) variables
var sliderItems = listOf(listOf(""))
suspend fun getSliderList() = withContext(Dispatchers.IO) {
    fillSliderList()
}
fun fillSliderList() {
    sliderItems = AnimeSlider.getSliderResultList()
}

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