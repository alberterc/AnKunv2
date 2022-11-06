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
import com.radx.ankunv2.Utils
import com.radx.ankunv2.Utils.toast
import com.radx.ankunv2.anime.*
import com.radx.ankunv2.screens.AnimeDetailsScreen
import com.radx.ankunv2.ui.theme.BrightGrey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun RecentUpdatesNavigationHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = HomeMenus.RecentUpdatesSub.route
    ) {
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
            RecentUpdatesScreen(navController, it.arguments!!.getString("mode")!!, it.arguments!!.getString("page")!!)
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

@Composable
fun RecentUpdatesScreen(navController: NavHostController, isDub: String, page: String) {
    val context = LocalContext.current

    // get favorite id from firestore
    animeIDs = Utils.readFavoriteIds(context)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp, 0.dp, 16.dp, 16.dp)
    ) {
        var recentUpdatesItemsState by remember { mutableStateOf(listOf(listOf(""))) }
        var currentPage by remember { mutableStateOf(page) }
        LaunchedEffect(Unit) {
            getRecentUpdatesDetailedList(mode = isDub, page = currentPage)
            recentUpdatesItemsState = recentUpdatesItems
        }
        
        LaunchedEffect(currentPage) {
            launch {
                snapshotFlow { currentPage }
                    .apply {
                        getRecentUpdatesDetailedList(mode = isDub, page = currentPage)
                        recentUpdatesItemsState = recentUpdatesItems
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
            if (isDub == "sub") {
                Text(
                    text = "Recent Updates (Sub)",
                    fontSize = 22.sp
                )
            }
            else {
                Text(
                    text = "Recent Updates (Dub)",
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
                    currentPage = if (isDub == "sub") {
                        (Integer.parseInt(currentPage) - 1).toString()
//                        navController.navigate("${HomeMenus.RecentUpdatesSub.route}/$isDub/$currentPage")
                    } else {
                        (Integer.parseInt(currentPage) - 1).toString()
//                        navController.navigate("${HomeMenus.RecentUpdatesSub.route}/$isDub/$currentPage")
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
                    currentPage = if (isDub == "sub") {
                        (Integer.parseInt(currentPage) + 1).toString()
//                        navController.navigate("${HomeMenus.RecentUpdatesDub.route}/$isDub/$currentPage")
                    } else {
                        (Integer.parseInt(currentPage) + 1).toString()
//                        navController.navigate("${HomeMenus.RecentUpdatesDub.route}/$isDub/$currentPage")
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
            items(recentUpdatesItemsState) {item ->
                if (recentUpdatesItemsState.size != 1) {
                    AnimeRecentUpdatesDetailedCardItem(item, navController, isDub)
                }
            }
        }
    }
}

@Composable
fun AnimeRecentUpdatesDetailedCardItem(anime: List<String>, navController: NavHostController, isDub: String) {
    // [[Anime Title, Anime ID, UNKNOWN, TOTAL EP, ANIME THUMBNAIL, LAST EP RELEASE TIME]]
    val title = anime[0].replace("\"", "")
    val id = anime[1]
    val totalEp = anime[3]
    val thumbnail = anime[4].replace("\"", "")
    val lastReleaseTime: String

    val seconds = Utils.getTimeFromEpoch(anime[5]) / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24
    // simple string formatting for episode release time
    if (days > 1) {
        lastReleaseTime = "$days days ago"
    }
    else if (days > 0) {
        lastReleaseTime = "$days day ago"
    }
    else if (hours > 1) {
        lastReleaseTime = "$hours hrs. ago"
    }
    else if (hours > 0) {
        lastReleaseTime = "$hours hr. ago"
    }
    else if (minutes > 1) {
        lastReleaseTime = "$minutes mins. ago"
    }
    else if (minutes > 0) {
        lastReleaseTime = "$minutes min. ago"
    }
    else if (seconds > 1) {
        lastReleaseTime = "$seconds secs. ago"
    }
    else if (seconds > 0) {
        lastReleaseTime = "$seconds sec. ago"
    }
    else {
        lastReleaseTime = "Just Released"
    }

    var favoriteState by remember { mutableStateOf(false) }
    var genreItemsState by remember { mutableStateOf(listOf("")) }

    val firebaseAuth = Firebase.auth
    val firebaseDatabase = Firebase.firestore
    val context = LocalContext.current

    favoriteState = animeIDs.contains(id)

    LaunchedEffect(Unit) {
        getGenreRecentItemsList(id)
        genreItemsState = genreRecentItems
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
                if (isDub == "dub") {
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
            Text(
                text = lastReleaseTime,
                fontSize = 14.sp,
                modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 0.dp)
            )
            Text(
                text = "Episode " + totalEp,
                fontSize = 14.sp,
                modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 0.dp)
            )
            Column(
                modifier = Modifier
                    .padding(0.dp, 8.dp, 0.dp, 10.dp)
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

var genreRecentItems = listOf("")
suspend fun getGenreRecentItemsList(animeID: String) = withContext(Dispatchers.IO) {
    fillGenreRecentItemsList(animeID = animeID)
}
fun fillGenreRecentItemsList(animeID: String) {
    genreRecentItems = AnimeDetails.getAnimeGenreList(animeID = animeID)
}

var recentUpdatesItems = listOf(listOf(""))
suspend fun getRecentUpdatesDetailedList(mode: String = "sub", page: String = "1") = withContext(Dispatchers.IO) {
    fillRecentUpdatesDetailedList(mode = mode, page = page)
}
fun fillRecentUpdatesDetailedList(mode: String = "sub", page: String = "1") {
    recentUpdatesItems = AnimeRecentUpdates.getRecentUpdatesList(mode = mode, page = page)
}