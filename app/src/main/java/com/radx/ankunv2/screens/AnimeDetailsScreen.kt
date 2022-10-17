package com.radx.ankunv2.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.radx.ankunv2.R
import com.radx.ankunv2.ui.theme.BrightGrey
import com.radx.ankunv2.ui.theme.Transparent
import com.radx.ankunv2.ui.theme.White

@Preview(showBackground = true)
@Composable
fun AnimeDetailsScreen() {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val maxHeight = this.maxHeight

        val topHeight: Dp = maxHeight * 1/3
        val bottomHeight: Dp = maxHeight * 2/3 - 40.dp
        val centerHeight = 200.dp
        val centerPaddingBottom = bottomHeight - centerHeight/2

        Top(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .height(topHeight)
        )

        Bottom(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .height(bottomHeight - 110.dp)
        )

        Center(
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, bottom = centerPaddingBottom)
                .fillMaxWidth()
                .height(centerHeight)
                .align(Alignment.BottomCenter)
        )
    }
}

// large thumbnail
@Composable
fun Top(modifier: Modifier) {
    Column(modifier.background(Color.Transparent)) {
        Card(
            modifier = Modifier
                .height(250.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(0.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.ic_launcher_background),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxHeight(),
                contentScale = ContentScale.Crop
            )
        }
    }
}

// small thumbnail and title, etc
@Composable
fun Center(modifier: Modifier) {
    val favoriteState by remember { mutableStateOf(false) }

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
                    painter = painterResource(R.drawable.ic_launcher_foreground),
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
                    text = "Anime title",
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
                            text = "Status",
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
                            text = "Season",
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
fun Bottom(modifier: Modifier) {
    val genreItemsState by remember { mutableStateOf(listOf("")) }
    val episodeItemsState by remember { mutableStateOf(listOf("")) }

    Column(
        modifier
            .background(Color.Transparent)
            .padding(16.dp, 0.dp, 16.dp, 0.dp)
            .fillMaxSize()
    ) {
        // genre
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 100.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .padding(0.dp, 0.dp, 0.dp, 8.dp)
        ) {
            items(genreItemsState) { item ->
                // each genre
                GenreCardItem()
            }
        }

        // description
        Text(
            text = "description",
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
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 80.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(64.dp)
        ) {
            items(episodeItemsState) { item ->
                // each episode card
                EpisodeCardItem()
            }
        }
    }
}

@Composable
fun GenreCardItem() {
    OutlinedCard(
        colors = CardDefaults
            .outlinedCardColors(
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
            ),
        border = BorderStroke(1.dp, White),
        shape = RoundedCornerShape(100.dp),
        modifier = Modifier
            .wrapContentHeight()
            .wrapContentWidth()
    ) {
        Text(
            text = "Action",
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