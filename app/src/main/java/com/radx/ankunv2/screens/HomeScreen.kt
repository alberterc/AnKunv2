package com.radx.ankunv2.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Card
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import coil.compose.rememberAsyncImagePainter
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.calculateCurrentOffsetForPage
import com.google.accompanist.pager.rememberPagerState
import com.radx.ankunv2.anime.AnimeSlider
import com.radx.ankunv2.ui.theme.Transparent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
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

        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Most Popular This Week",
                modifier = Modifier.align(Alignment.TopStart)
            )
            ClickableText(
                text = AnnotatedString("See all"),
                modifier = Modifier.align(Alignment.TopEnd),
                onClick = { }
            )
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
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