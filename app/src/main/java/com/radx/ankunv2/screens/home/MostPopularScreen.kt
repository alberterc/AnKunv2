package com.radx.ankunv2.screens.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.radx.ankunv2.AnimeDetailsScreenNav
import com.radx.ankunv2.Utils.readFavoriteIds
import com.radx.ankunv2.Utils.toast
import com.radx.ankunv2.anime.AnimeDetails
import com.radx.ankunv2.anime.AnimeSearch
import com.radx.ankunv2.screens.AnimeDetailsScreen
import com.radx.ankunv2.ui.theme.BrightGrey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun MostPopularScreenNavigationHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = HomeMenus.PopularWeek.route
    ) {
        composable(
            route = "${HomeMenus.PopularWeek.route}/{popularType}/{page}",
            arguments = listOf(
                navArgument("popularType") {
                    type = NavType.StringType
                },
                navArgument("page") {
                    type = NavType.StringType
                })
        ) {
            MostPopularScreen(navController, it.arguments!!.getString("popularType")!!, it.arguments!!.getString("page")!!)
        }
        composable(
            route = "${AnimeDetailsScreenNav.AnimeDetails.route}/{animeId}",
            arguments = listOf(navArgument("animeId") {
                type = NavType.StringType
            })
        ) {
            AnimeDetailsScreen(it.arguments!!.getString("animeId")!!)
        }
    }
}

private var animeIDs = mutableListOf<String>()

@Suppress("UNCHECKED_CAST")
@Composable
fun MostPopularScreen(navController: NavHostController, popularType: String, page: String) {
    val context = LocalContext.current

    // get favorite id from firestore
    animeIDs = readFavoriteIds(context)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp, 0.dp, 16.dp, 16.dp)
    ) {
        var mostPopularItemsState by remember { mutableStateOf(listOf(listOf(""))) }
        var currentPage by remember { mutableStateOf(page) }
        LaunchedEffect(Unit) {
            getMostPopularDetailedList(page = currentPage, sort = popularType)
            mostPopularItemsState = mostPopularItems
        }

        LaunchedEffect(currentPage) {
            launch {
                snapshotFlow { currentPage }
                    .apply {
                        getMostPopularDetailedList(page = currentPage, sort = popularType)
                        mostPopularItemsState = mostPopularItems
                    }
            }
        }

        // top app bar
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(PaddingValues(bottom = 16.dp))
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = null,
                modifier = Modifier
                    .padding(0.dp, 0.dp, 16.dp, 0.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = rememberRipple(color = MaterialTheme.colorScheme.secondary),
                        onClick = { navController.popBackStack() }
                    )
            )
            if (popularType == "popular-week") {
                Text(
                    text = "Most Popular This Week",
                    fontSize = 22.sp
                )
            }
            else {
                Text(
                    text = "Most Popular This Year",
                    fontSize = 22.sp
                )
            }
        }

        // next and previous page
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(0.dp, 0.dp, 0.dp, 16.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            FilledTonalButton(
                enabled = currentPage != "1",
                modifier = Modifier.padding(0.dp, 0.dp, 40.dp, 0.dp),
                content = { Text(text = "Prev") },
                onClick = {
                    currentPage = if (popularType == "popular-week") {
                        (Integer.parseInt(currentPage) - 1).toString()
//                        navController.navigate("${HomeMenus.PopularWeek.route}/$popularType/$currentPage")
                    } else {
                        (Integer.parseInt(currentPage) - 1).toString()
//                        navController.navigate("${HomeMenus.PopularWeek.route}/$popularType/$currentPage")
                    }
                }
            )
            Text(
                text = currentPage,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )
            FilledTonalButton(
                enabled = true,
                modifier = Modifier.padding(40.dp, 0.dp, 0.dp, 0.dp),
                content = { Text(text = "Next") },
                onClick = {
                    currentPage = if (popularType == "popular-week") {
                        (Integer.parseInt(currentPage) + 1).toString()
//                        navController.navigate("${HomeMenus.PopularYear.route}/$popularType/$currentPage")
                    } else {
                        (Integer.parseInt(currentPage) + 1).toString()
//                        navController.navigate("${HomeMenus.PopularYear.route}/$popularType/$currentPage")
                    }
                }
            )
        }

        // anime list
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .padding(0.dp, 0.dp, 0.dp, 16.dp)
        ) {
            items(mostPopularItemsState) {item ->
                if (mostPopularItemsState.size != 1) {
                    AnimeMostPopularDetailedCardItem(item, navController)
                }
            }
        }
    }
}

@Composable
fun AnimeMostPopularDetailedCardItem(anime: List<String>, navController: NavHostController) {
    // [[Anime Title, Anime ID, ANIME THUMBNAIL, SUB OR DUB (sub=0, dub=1]]
    var favoriteState by remember { mutableStateOf(false) }
    val title = anime[0].replace("\"", "")
    val id = anime[1]
    val thumbnail = anime[2].replace("\"", "")
    val isDub = anime[3]

    val firebaseAuth = Firebase.auth
    val firebaseDatabase = Firebase.firestore
    val context = LocalContext.current

    var genreItemsState by remember { mutableStateOf(listOf("")) }

    favoriteState = animeIDs.contains(id)

    LaunchedEffect(Unit) {
        getGenreItemsList(id)
        genreItemsState = genreItems
    }

    val genre = genreItemsState.toString()
        .replace("[", "")
        .replace("]", "")

    Row(
        modifier = Modifier
            .clickable(
                enabled = true,
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(color = MaterialTheme.colorScheme.secondary),
                onClick = { navController.navigate("${AnimeDetailsScreenNav.AnimeDetails.route}/$id") }
            )
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // thumbnail
        Card(
            modifier = Modifier
                .height(240.dp)
                .width(170.dp),
            shape = RoundedCornerShape(18.dp)
        ) {
            Box {
                Image(
                    painter = rememberAsyncImagePainter(thumbnail),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.FillBounds
                )
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
            }
        }

        // text title, genre, favorite button
        Column(
            modifier = Modifier
                .padding(16.dp, 0.dp, 0.dp, 0.dp)
        ) {
            Text(
                text = title,
                fontSize = 20.sp,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(0.dp, 0.dp, 0.dp, 10.dp)
            )
            Column(
                modifier = Modifier
                    .padding(0.dp, 0.dp, 0.dp, 48.dp)
            ) {
                Text(
                    text = genre,
                    fontSize = 14.sp,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    color = BrightGrey,
                    modifier = Modifier
                        .padding(0.dp, 0.dp, 0.dp, 0.dp)
                )
            }
            if (!favoriteState) {
                Button(
                    enabled =  true,
                    onClick = {
                        // add new anime id
                        animeIDs.add(id)

                        val data = hashMapOf(
                            "animeIDs" to animeIDs
                        )

                        // overwrite anime id data in firestore
                        firebaseDatabase.collection("users")
                            .document(firebaseAuth.currentUser!!.uid)
                            .set(data)
                            .addOnSuccessListener { favoriteState = !favoriteState }
                            .addOnFailureListener { toast(context, "Failed to add anime.") }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onSecondaryContainer),
                    modifier = Modifier
                        .width(135.dp),
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
                                    .width(16.dp)
                                    .height(16.dp)
                            )
                            Text(
                                text = "Favorites",
                                fontSize = 14.sp,
                                modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 1.dp)
                            )
                        }
                    }
                )
            }
            else {
                OutlinedButton(
                    enabled =  true,
                    onClick = {
                        animeIDs.remove(id)
                        val data = hashMapOf(
                            "animeIDs" to animeIDs
                        )

                        // overwrite anime id data in firestore
                        firebaseDatabase.collection("users")
                            .document(firebaseAuth.currentUser!!.uid)
                            .set(data)
                            .addOnSuccessListener { favoriteState = !favoriteState }
                            .addOnFailureListener { toast(context, "Failed to add anime.") }
                    },
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSecondaryContainer),
                    content = {
                        Text(
                            text = "Favorited",
                            fontSize = 14.sp,
                            modifier = Modifier
                                .padding(0.dp, 0.dp, 0.dp, 0.dp),
                        )
                    }
                )
            }
        }
    }
}

var genreItems = listOf("")
suspend fun getGenreItemsList(animeID: String) = withContext(Dispatchers.IO) {
    fillGenreItemsList(animeID = animeID)
}
fun fillGenreItemsList(animeID: String) {
    genreItems = AnimeDetails.getAnimeGenreList(animeID = animeID)
}

var mostPopularItems = listOf(listOf(""))
suspend fun getMostPopularDetailedList(sort: String = "popular-week", page: String = "1") = withContext(Dispatchers.IO) {
    fillMostPopularDetailedList(sort = sort, page = page)
}
fun fillMostPopularDetailedList(sort: String = "popular-week", page: String = "1") {
    mostPopularItems = AnimeSearch.getSearchResultList(sort = sort, dub = "", page = page)
}