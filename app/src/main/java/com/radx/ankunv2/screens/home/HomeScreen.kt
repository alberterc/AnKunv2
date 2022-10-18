package com.radx.ankunv2.screens.home

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil.compose.rememberAsyncImagePainter
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.calculateCurrentOffsetForPage
import com.google.accompanist.pager.rememberPagerState
import com.radx.ankunv2.anime.*
import com.radx.ankunv2.screens.AnimeDetailsScreen
import com.radx.ankunv2.ui.theme.Transparent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlin.math.absoluteValue

@Composable
fun HomeNavigationHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = HomeMenus.Home.route
    ) {
        composable(route = HomeMenus.Home.route) { MainScreen(navController) }
        composable(
            route = "${HomeMenus.PopularWeek.route}/{popularType}/{page}",
            arguments = listOf(
                navArgument("popularType") {
                    type = NavType.StringType
                },
                navArgument("page") {
                    type = NavType.StringType
                })
        ) { MostPopularScreen(
            navController,
            it.arguments!!.getString("popularType")!!,
            it.arguments!!.getString("page")!!
        ) }
        composable(
            route = "${HomeMenus.PopularYear.route}/{popularType}/{page}",
            arguments = listOf(
                navArgument("popularType") {
                    type = NavType.StringType
                },
                navArgument("page") {
                    type = NavType.StringType
                })
        ) { MostPopularScreen(
            navController,
            it.arguments!!.getString("popularType")!!,
            it.arguments!!.getString("page")!!
        ) }
        composable(
            route = "${HomeMenus.RecentUpdatesSub.route}/{mode}/{page}",
            arguments = listOf(
                navArgument("mode") {
                    type = NavType.StringType
                },
                navArgument("page") {
                    type = NavType.StringType
                })
        ) {
            RecentUpdatesScreen(
                navController,
                it.arguments!!.getString("mode")!!,
                it.arguments!!.getString("page")!!
            )
        }
        composable(
            route = "${HomeMenus.RecentUpdatesDub.route}/{mode}/{page}",
            arguments = listOf(
                navArgument("mode") {
                    type = NavType.StringType
                },
                navArgument("page") {
                    type = NavType.StringType
                })
        ) {
            RecentUpdatesScreen(
                navController,
                it.arguments!!.getString("mode")!!,
                it.arguments!!.getString("page")!!
            )
        }
        composable(
            route = "${AnimeDetailsScreenNav.AnimeDetails.route}/{animeId}",
            arguments = listOf(navArgument("animeId") {
                type = NavType.StringType
            })
        ) { AnimeDetailsScreen(it.arguments!!.getString("animeId")!!) }
    }
}

@Composable
fun HomeScreen() {
    val navController = rememberNavController()
    HomeNavigationHost(navController = navController)
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun MainScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        var sliderItemsState by remember { mutableStateOf(listOf(listOf(""))) }
        val pagerState = rememberPagerState()
        LaunchedEffect(Unit) {
            getSliderList()
            sliderItemsState = sliderItems
        }

        // Slider section
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll( rememberScrollState() )
        ) {
            // Most Popular This Week section
            Column(
                modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 10.dp)
            ) {
                var mostPopularWeekItemsState by remember { mutableStateOf(listOf(listOf(""))) }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp, 24.dp, 16.dp, 24.dp)
                ) {
                    LaunchedEffect(Unit) {
                        getMostPopularResultList(sort = "popular-week")
                        mostPopularWeekItemsState = mostPopularWeekList
                    }

                    Text(
                        text = "Most Popular This Week",
                        modifier = Modifier.align(Alignment.TopStart),
                        fontSize = 16.sp
                    )
                    Text(
                        text = "See all",
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        fontSize = 16.sp,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = rememberRipple(color = MaterialTheme.colorScheme.secondary),
                                onClick = { navController.navigate("${HomeMenus.PopularWeek.route}/popular-week/1") }
                            )
                    )
                }

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .padding(16.dp, 0.dp, 0.dp, 0.dp)
                ) {
                    items(mostPopularWeekItemsState) { item ->
                        if (mostPopularWeekItemsState.size != 1) {
                            AnimeMostPopularCardItem(item, navController)
                        }
                    }
                }
            }

            // Recent Updates Sub section
            Column(
                modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 10.dp)
            ) {
                var recentUpdatesSubItemsState by remember { mutableStateOf(listOf(listOf(""))) }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp, 24.dp, 16.dp, 24.dp)
                ) {
                    LaunchedEffect(Unit) {
                        getRecentUpdatesResultList()
                        recentUpdatesSubItemsState = recentUpdatesSubList
                    }

                    Text(
                        text = "Recent Updates (Sub)",
                        modifier = Modifier.align(Alignment.TopStart),
                        fontSize = 16.sp
                    )
                    Text(
                        text = "See all",
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        fontSize = 16.sp,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = rememberRipple(color = MaterialTheme.colorScheme.secondary),
                                onClick = { navController.navigate("${HomeMenus.RecentUpdatesSub.route}/sub/1") }
                            )
                    )
                }

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .padding(16.dp, 0.dp, 0.dp, 0.dp)
                ) {
                    items(recentUpdatesSubItemsState) { item ->
                        if (recentUpdatesSubItemsState.size != 1) {
                            AnimeRecentUpdatesCardItem(anime = item, isDub = "0", navController = navController)
                        }
                    }
                }
            }

            // Recent Updates Dub section
            Column(
                modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 10.dp)
            ) {
                var recentUpdatesDubItemsState by remember { mutableStateOf(listOf(listOf(""))) }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp, 24.dp, 16.dp, 24.dp)
                ) {
                    LaunchedEffect(Unit) {
                        getRecentUpdatesResultList(mode = "dub")
                        recentUpdatesDubItemsState = recentUpdatesDubList
                    }

                    Text(
                        text = "Recent Updates (Dub)",
                        modifier = Modifier.align(Alignment.TopStart),
                        fontSize = 16.sp
                    )
                    Text(
                        text = "See all",
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        fontSize = 16.sp,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = rememberRipple(color = MaterialTheme.colorScheme.secondary),
                                onClick = { navController.navigate("${HomeMenus.RecentUpdatesDub.route}/dub/1") }
                            )
                    )
                }

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .padding(16.dp, 0.dp, 0.dp, 0.dp)
                ) {
                    items(recentUpdatesDubItemsState) { item ->
                        if (recentUpdatesDubItemsState.size != 1) {
                            AnimeRecentUpdatesCardItem(anime = item, isDub = "1", navController = navController)
                        }
                    }
                }
            }

            // Most Popular This Year section
            Column(
                modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 32.dp)
            ) {
                var mostPopularYearItemsState by remember { mutableStateOf(listOf(listOf(""))) }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp, 24.dp, 16.dp, 24.dp)
                ) {
                    LaunchedEffect(Unit) {
                        getMostPopularResultList(sort = "popular-year")
                        mostPopularYearItemsState = mostPopularYearList
                    }

                    Text(
                        text = "Most Popular This Year",
                        modifier = Modifier.align(Alignment.TopStart),
                        fontSize = 16.sp
                    )
                    Text(
                        text = "See all",
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        fontSize = 16.sp,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = rememberRipple(color = MaterialTheme.colorScheme.secondary),
                                onClick = { navController.navigate("${HomeMenus.PopularYear.route}/popular-year/1") }
                            )
                    )
                }

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .padding(16.dp, 0.dp, 0.dp, 0.dp)
                ) {
                    items(mostPopularYearItemsState) { item ->
                        if (mostPopularYearItemsState.size != 1) {
                            AnimeMostPopularCardItem(item, navController)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimeMostPopularCardItem(anime: List<String>, navController: NavHostController) {
    // [[Anime Title, Anime ID, ANIME THUMBNAIL, SUB OR DUB (sub=0, dub=1]]
    val title = anime[0].replace("\"", "")
    val id = anime[1]
    val thumbnailUrl = anime[2].replace("\"", "")

    Card(
        modifier = Modifier
            .height(180.dp)
            .width(120.dp),
        shape = RoundedCornerShape(18.dp),
        onClick = { navController.navigate("${AnimeDetailsScreenNav.AnimeDetails.route}/$id") }
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
                Text(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(8.dp, 0.dp, 16.dp, 15.dp),
                    text = title,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimeRecentUpdatesCardItem(anime: List<String>, isDub: String = "0", navController: NavHostController) {
    // [[Anime Title, Anime ID, UNKNOWN, TOTAL EP, ANIME THUMBNAIL, LAST EP RELEASE TIME]]
    val title = anime[0].replace("\"", "")
    val id = anime[1]
    val thumbnailUrl = anime[4].replace("\"", "")

    Card(
        modifier = Modifier
            .height(180.dp)
            .width(120.dp),
        shape = RoundedCornerShape(18.dp),
        onClick = { navController.navigate("${AnimeDetailsScreenNav.AnimeDetails.route}/$id") }
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
                        .padding(8.dp, 0.dp, 16.dp, 15.dp),
                    text = title,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp
                )
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
            .height(250.dp),
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

// lazy row (anime list) variables
var recentUpdatesSubList = listOf(listOf(""))
var recentUpdatesDubList = listOf(listOf(""))
suspend fun getRecentUpdatesResultList(mode: String = "sub") = withContext(Dispatchers.IO) {
    fillRecentUpdatesList(mode = mode)
}
fun fillRecentUpdatesList(mode: String = "sub") {
    if (mode == "sub") {
        recentUpdatesSubList = AnimeRecentUpdates.getRecentUpdatesList(mode = mode).subList(0, 5)
    }
    else {
        recentUpdatesDubList = AnimeRecentUpdates.getRecentUpdatesList(mode = mode).subList(0, 5)
    }
}

// lazy row (anime list) variables
var mostPopularWeekList = listOf(listOf(""))
var mostPopularYearList = listOf(listOf(""))
suspend fun getMostPopularResultList(sort: String = "popular-week") = withContext(Dispatchers.IO) {
    fillMostPopularList(sort = sort)
}
fun fillMostPopularList(sort: String = "popular-week") {
    if (sort == "popular-week") {
        mostPopularWeekList = AnimeSearch.getSearchResultList(sort = sort, dub = "0").subList(0, 5)
    }
    else {
        mostPopularYearList = AnimeSearch.getSearchResultList(sort = sort, dub = "0").subList(0, 5)
    }
}