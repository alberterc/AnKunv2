@file:OptIn(ExperimentalMaterial3Api::class)

package com.radx.ankunv2

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
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
import androidx.compose.ui.text.AnnotatedString
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