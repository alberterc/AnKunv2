package com.radx.ankunv2.screens.favorites

import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.radx.ankunv2.anime.AnimeDetails
import com.radx.ankunv2.AnimeDetailsScreenNav
import com.radx.ankunv2.Utils.readFavoriteIds
import com.radx.ankunv2.Utils.toast
import com.radx.ankunv2.screens.AnimeDetailsScreen
import com.radx.ankunv2.ui.theme.Transparent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun FavoritesNavigationHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = FavoritesMenu.Favorites.route
    ) {
        composable(route = FavoritesMenu.Favorites.route) { FavoritesMainScreen(navController) }
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

@Composable
fun FavoritesScreen() {
    val navController = rememberNavController()
    FavoritesNavigationHost(navController)
}

@Composable
fun FavoritesMainScreen(navController: NavHostController) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp, 0.dp, 16.dp, 0.dp)
    ) {
        var animeListState by remember { mutableStateOf(listOf(mapOf("" to ""))) }
        var animeIDsState by remember { mutableStateOf(listOf("")) }

        // get favorite id from firestore
        val firebaseAuth = Firebase.auth
        val firebaseDatabase = Firebase.firestore

        // read all favorite anime ids
        val animeIDs = mutableListOf<String>()
        firebaseDatabase.collection("users")
            .document(firebaseAuth.currentUser!!.uid)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    try {
                        val ids: List<String> = document.data!!.values.toMutableList()[0] as List<String>
                        // add all existing anime id
                        ids.forEach {
                            animeIDs.add(it)
                        }
                    } catch (_: IndexOutOfBoundsException) {}
                }
                else {
                    toast(context, "No Docs")
                }
                animeIDsState = animeIDs
            }
            .addOnFailureListener {
                toast(context, "Failed to retrieve data.")
            }

        LaunchedEffect(animeIDsState) {
            getAnimeList(animeIDs = animeIDsState)
            animeListState = animeList
        }

        // top app bar
        Text(
            text = "Favorites",
            fontSize = 22.sp,
            modifier = Modifier
                .padding(PaddingValues(bottom = 16.dp))
        )

        // anime list
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .padding(0.dp, 0.dp, 0.dp, 16.dp)
        ) {
            items(animeListState) { item ->
                if (animeListState.size != 1) {
                    AnimeFavoritesCard(item, navController)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimeFavoritesCard(anime: Map<String, String>, navController: NavHostController) {
    // [[Anime Title, Anime ID, ANIME THUMBNAIL, SUB OR DUB (sub=0, dub=1]]
    val title = anime["title"]!!.replace("\"", "")
    val id = anime["id"]
    val thumbnailUrl = anime["small thumbnail"]!!.replace("\"", "")
    val isDub = anime["type"]

    Card(
        modifier = Modifier
            .height(220.dp)
            .width(160.dp),
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

private var animeList = listOf<Map<String, String>>()
private var animeDetails = listOf<Map<String, String>>()
suspend fun getAnimeList(animeIDs: List<String>) = withContext(Dispatchers.IO) {
    fillAnimeList(animeIDs = animeIDs)
}

fun fillAnimeList(animeIDs: List<String>) {
    if (animeIDs.isNotEmpty()) {
        animeDetails = AnimeDetails.getAnimeDetailsForSeveralIDs(animeIDs)
        animeList = animeDetails
    }
}
