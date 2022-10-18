package com.radx.ankunv2.screens

import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.radx.ankunv2.R
import com.radx.ankunv2.anime.AnimeDetails
import com.radx.ankunv2.ui.theme.BrightGrey
import com.radx.ankunv2.ui.theme.Grey
import com.radx.ankunv2.ui.theme.Transparent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun AnimeDetailsScreen(animeID: String) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val maxHeight = this.maxHeight
        val topHeight: Dp = maxHeight * 1/3
        val bottomHeight: Dp = maxHeight * 2/3 - 40.dp
        val centerHeight = 200.dp
        val centerPaddingBottom = bottomHeight - centerHeight/2

        var genreItemsState by remember { mutableStateOf(listOf("")) }
        var animeDetailsState by remember { mutableStateOf(mapOf(
            "title" to "",
            "description" to "",
            "status" to "",
            "type" to "",
            "season" to "",
            "small thumbnail" to "",
            "large thumbnail" to ""
        )) }
        LaunchedEffect(Unit) {
            getGenreItemsList(animeID = animeID)
            genreItemsState = genreItems
            getAnimeDetailsMap(animeID = animeID)
            animeDetailsState = animeDetails
        }

        Top(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .height(topHeight),
            animeDetailsState
        )

        Bottom(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .height(bottomHeight - 110.dp),
            genreItemsState,
            animeDetailsState
        )

        Center(
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, bottom = centerPaddingBottom)
                .fillMaxWidth()
                .height(centerHeight)
                .align(Alignment.BottomCenter),
            animeDetailsState
        )
    }
}

// large thumbnail
@Composable
fun Top(modifier: Modifier, animeDetailsState: Map<String, String>) {
    val largeThumbnail = if (animeDetailsState["large thumbnail"] == null) "" else animeDetailsState["large thumbnail"]!!
    Log.e("THUMB", largeThumbnail)

    Column(modifier.background(Color.Transparent)) {
        Card(
            modifier = Modifier
                .height(250.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(0.dp)
        ) {
            Image(
                painter = rememberAsyncImagePainter(largeThumbnail),
                contentDescription = null,
                modifier = Modifier.fillMaxHeight(),
                contentScale = ContentScale.Crop
            )
        }
    }
}

// small thumbnail and title, etc
@Composable
fun Center(modifier: Modifier, animeDetailsState: Map<String, String>) {
    val favoriteState by remember { mutableStateOf(false) }

    val thumbnail = if (animeDetailsState["small thumbnail"] == null) "" else animeDetailsState["small thumbnail"]!!
    val title = if (animeDetailsState["title"] == null) "" else animeDetailsState["title"]!!
    val status = if (animeDetailsState["status"] == null) "" else animeDetailsState["status"]!!
    val season = if (animeDetailsState["season"] == null) "" else animeDetailsState["season"]!!

    Column(
        modifier.background(Color.Transparent)
            .padding(0.dp, 0.dp, 0.dp, 0.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // small thumbnail
            Card(
                modifier = Modifier
                    .height(200.dp)
                    .width(150.dp)
            ) {
                Image(
                    painter = rememberAsyncImagePainter(thumbnail),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.FillBounds
                )
            }

            // title, status, episodes, season, favorite button
            Column(
                modifier = Modifier
                    .padding(12.dp, 40.dp, 0.dp, 0.dp)
            ) {
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .padding(0.dp, 0.dp, 0.dp, 10.dp)
                )
                Row {
                    Column(
                        modifier = Modifier
                            .padding(0.dp, 10.dp, 0.dp, 0.dp)
                    ) {
                        Text(
                            text = status,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier
                                .padding(0.dp, 0.dp, 0.dp, 8.dp)
                        )
                        Text(
                            text = "Episodes",
                            fontSize = 12.sp,
                            color = BrightGrey,
                            modifier = Modifier
                                .padding(0.dp, 0.dp, 0.dp, 0.dp)
                        )
                        Text(
                            text = season,
                            fontSize = 12.sp,
                            color = BrightGrey,
                            modifier = Modifier
                                .padding(0.dp, 0.dp, 0.dp, 0.dp)
                        )
                    }
                    Column(
                        modifier = Modifier
                            .padding(19.dp, 23.dp, 0.dp, 0.dp)
                    ) {
//                        val rating: Float by remember { mutableStateOf(0f) }

                        if (!favoriteState) {
                            Button(
                                enabled =  true,
                                onClick = { },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onSecondaryContainer),
                                modifier = Modifier
                                    .width(119.dp),
                                content = {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .padding(0.dp, 3.dp, 0.dp, 3.dp)
                                            .fillMaxWidth()
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = null,
                                            modifier = Modifier
                                                .width(12.dp)
                                                .height(12.dp)
                                        )
                                        Text(
                                            text = "Favorites",
                                            fontSize = 12.sp,
                                            modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 1.dp)
                                        )
                                    }
                                }
                            )
                        }
                        else {
                            OutlinedButton(
                                enabled =  true,
                                onClick = { },
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                                ),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSecondaryContainer),
                                content = {
                                    Text(
                                        text = "Favorited",
                                        fontSize = 12.sp,
                                        modifier = Modifier
                                            .padding(0.dp, 0.dp, 0.dp, 0.dp),
                                    )
                                }
                            )
                        }

                        // rating bar lib
//                        RatingBar(
//                        )
                    }
                }
            }
        }
    }
}

// anime details
@Composable
fun Bottom(modifier: Modifier, genreItemsState: List<String>, animeDetailsState: Map<String, String>) {
    val episodeItemsState by remember { mutableStateOf(listOf("")) }

    val description = if (animeDetailsState["description"] == null) "" else animeDetailsState["description"]!!

    Column(
        modifier
            .verticalScroll(rememberScrollState())
            .background(Color.Transparent)
            .padding(16.dp, 8.dp, 16.dp, 0.dp)
            .fillMaxSize()
    ) {
        // genre
        LazyRow (
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .padding(0.dp, 0.dp, 0.dp, 16.dp)
        ) {
            items(genreItemsState) { item ->
                // each genre
                if (genreItemsState.size != 1) {
                    GenreCardItem(item)
                }
            }
        }

        // description
        Text(
            text = description,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )

        // episodes section
        // episode header
        Text(
            text = "Episodes",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
                .padding(0.dp, 24.dp, 0.dp, 16.dp)
        )

        // episode list
        LazyRow(
            modifier = Modifier
                .padding(0.dp, 0.dp, 0.dp, 16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items(episodeItemsState) { item ->
                // each episode card
                EpisodeCardItem()
            }
        }
    }
}

@Composable
fun GenreCardItem(genre: String) {
    OutlinedCard(
        colors = CardDefaults
            .outlinedCardColors(
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                containerColor = Transparent
            ),
        border = BorderStroke(1.dp, Grey),
        shape = RoundedCornerShape(100.dp),
        modifier = Modifier
            .wrapContentHeight()
            .wrapContentWidth()
    ) {
        Text(
            text = genre,
            fontSize = 12.sp,
            modifier = Modifier
                .padding(8.dp, 3.dp, 8.dp, 3.dp)
                .align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
fun EpisodeCardItem() {
    FilledTonalButton(
        enabled =  true,
        onClick = { },
        modifier = Modifier
            .width(130.dp),
        content = {
            Text(
                text = "Episode 1",
                fontSize = 14.sp,
                modifier = Modifier
                    .padding(0.dp, 0.dp, 0.dp, 0.dp),
            )
        }
    )
}

var genreItems = listOf("")
suspend fun getGenreItemsList(animeID: String) = withContext(Dispatchers.IO) {
    fillGenreItemsList(animeID = animeID)
}
fun fillGenreItemsList(animeID: String) {
    genreItems = AnimeDetails.getAnimeGenreList(animeID = animeID)
}

var animeDetails = mapOf(
    "title" to "",
    "description" to "",
    "status" to "",
    "type" to "",
    "season" to "",
    "small thumbnail" to "",
    "large thumbnail" to ""
)
suspend fun getAnimeDetailsMap(animeID: String) = withContext(Dispatchers.IO) {
    fillAnimeDetailsMap(animeID = animeID)
}
fun fillAnimeDetailsMap(animeID: String) {
    animeDetails = AnimeDetails.getAnimeDetailsList(animeID = animeID)
}