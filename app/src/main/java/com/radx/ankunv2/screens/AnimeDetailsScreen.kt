package com.radx.ankunv2.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil.compose.rememberAsyncImagePainter
import com.google.accompanist.flowlayout.FlowRow
import com.radx.ankunv2.anime.AnimeDetails
import com.radx.ankunv2.anime.AnimeDetailsScreenNav
import com.radx.ankunv2.anime.Utils
import com.radx.ankunv2.screens.videoplayer.AnimeVideoScreen
import com.radx.ankunv2.ui.theme.BrightGrey
import com.radx.ankunv2.ui.theme.Grey
import com.radx.ankunv2.ui.theme.Transparent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun AnimeDetailsNavigationHost(animeID: String, navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = AnimeDetailsScreenNav.AnimeDetails.route
    ) {
        composable(route = AnimeDetailsScreenNav.AnimeDetails.route) { MainScreen(animeID = animeID, navController = navController) }
        composable(
            route = "${AnimeDetailsScreenNav.AnimeEpisodeStream.route}/{episodeID}",
            arguments = listOf(navArgument("episodeID") {
                type = NavType.StringType
            })
        ) {
            AnimeVideoScreen(it.arguments!!.getString("episodeID")!!)
        }
    }
}

@Composable
fun AnimeDetailsScreen(animeID: String = "") {
    val navController = rememberNavController()
    AnimeDetailsNavigationHost(animeID = animeID, navController = navController)
}

@Composable
fun MainScreen(animeID: String, navController: NavHostController) {
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
            "episode count" to "",
            "type" to "",
            "season" to "",
            "small thumbnail" to "",
            "large thumbnail" to ""
        )) }
        var animeEpisodesItemsState by remember { mutableStateOf(listOf(listOf(""))) }

        LaunchedEffect(Unit) {
            getGenreItemsList(animeID = animeID)
            genreItemsState = genreItems
            getAnimeDetailsMap(animeID = animeID)
            animeDetailsState = animeDetails
            getAnimeEpisodeList(animeID = animeID)
            animeEpisodesItemsState = episodesList
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
                .height(bottomHeight - 75.dp),
            genreItemsState,
            animeDetailsState,
            animeEpisodesItemsState,
            navController
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

    Column(modifier.background(Color.Transparent)) {
        Card(
            modifier = Modifier
                .height(250.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(0.dp)
        ) {
            Box {
                Image(
                    painter = rememberAsyncImagePainter(largeThumbnail),
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
                                endY = 1000f
                            )
                        )
                )
            }
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
                    .height(160.dp)
                    .width(110.dp)
            ) {
                Box {
                    Image(
                        painter = rememberAsyncImagePainter(thumbnail),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.FillBounds
                    )
                    if (animeDetailsState["type"] == "Dubbed") {
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
                }
            }

            // title, status, episodes, season, favorite button
            Column(
                modifier = Modifier
                    .padding(12.dp, 0.dp, 0.dp, 0.dp)
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
                            .padding(0.dp, 0.dp, 0.dp, 0.dp)
                    ) {
                        Text(
                            text = status,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier
                                .padding(0.dp, 0.dp, 0.dp, 8.dp)
                        )
                        Text(
                            text = animeDetailsState["episode count"] + " episodes",
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
                            .padding(0.dp, 0.dp, 0.dp, 0.dp)
                    ) {
//                        val rating: Float by remember { mutableStateOf(0f) }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth(),
                            contentAlignment = Alignment.TopEnd
                        ) {
                            if (!favoriteState) {
                                Icon(
                                    imageVector = Icons.Outlined.FavoriteBorder,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = rememberRipple(color = MaterialTheme.colorScheme.secondary),
                                            onClick = { }
                                        )
                                )
                            }
                            else {
                                Icon(
                                    imageVector = Icons.Filled.Favorite,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = rememberRipple(color = MaterialTheme.colorScheme.secondary),
                                            onClick = { }
                                        )
                                )
                            }
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
fun Bottom(
    modifier: Modifier,
    genreItemsState: List<String>,
    animeDetailsState: Map<String, String>,
    animeEpisodesItemsState: List<List<String>>,
    navController: NavHostController
) {
    val description = if (animeDetailsState["description"] == null) "" else animeDetailsState["description"]!!

    Column(
        modifier
            .verticalScroll(rememberScrollState())
            .background(Color.Transparent)
            .padding(16.dp, 0.dp, 16.dp, 0.dp)
            .fillMaxSize()
    ) {
        // genre
        FlowRow(
            modifier = Modifier
                .padding(0.dp, 0.dp, 0.dp, 16.dp),
            mainAxisSpacing = 8.dp,
            crossAxisSpacing = 8.dp
        ) {
            genreItemsState.forEach { item ->
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
            items(animeEpisodesItemsState) { item ->
                // each episode card
                if (item.size != 1) {
                    EpisodeCardItem(item, animeEpisodesItemsState, navController = navController)
                }
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
fun EpisodeCardItem(episode: List<String>, animeEpisodesItemsState: List<List<String>>, navController: NavHostController) {
    // [[UNKNOWN, EPISODE ID, EPISODE NUMBER, EPISODE RELEASE TIME]]
    val id = episode[1]
    val num = episode[2]
    val releaseTime: String

    val seconds = Utils.getTimeFromEpoch(episode[3]) / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24
    // simple string formatting for episode release time
    if (days > 1) {
        releaseTime = "$days days ago"
    }
    else if (days > 0) {
        releaseTime = "$days day ago"
    }
    else if (hours > 1) {
        releaseTime = "$hours hrs. ago"
    }
    else if (hours > 0) {
        releaseTime = "$hours hr. ago"
    }
    else if (minutes > 1) {
        releaseTime = "$minutes mins. ago"
    }
    else if (minutes > 0) {
        releaseTime = "$minutes min. ago"
    }
    else if (seconds > 1) {
        releaseTime = "$seconds secs. ago"
    }
    else if (seconds > 0) {
        releaseTime = "$seconds sec. ago"
    }
    else {
        releaseTime = "Just Released"
    }

    FilledTonalButton(
        enabled =  true,
        onClick = {
            navController.navigate("${AnimeDetailsScreenNav.AnimeEpisodeStream.route}/$id")
        },
        modifier = Modifier
            .width(130.dp),
        content = {
            Column {
                Text(
                    text = "Episode " + num,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .padding(0.dp, 0.dp, 0.dp, 0.dp)
                )
                Text(
                    text = releaseTime,
                    fontSize = 12.sp,
                    color = BrightGrey
                )
            }
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
    "episode count" to "",
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

var episodesList = listOf(listOf(""))
suspend fun getAnimeEpisodeList(animeID: String) = withContext(Dispatchers.IO) {
    fillAnimeEpisodeList(animeID = animeID)
}
fun fillAnimeEpisodeList(animeID: String) {
    episodesList = AnimeDetails.getAnimeEpisodesList(animeID = animeID)
}