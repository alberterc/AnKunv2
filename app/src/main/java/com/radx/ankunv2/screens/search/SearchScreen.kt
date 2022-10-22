package com.radx.ankunv2.screens.search

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.google.accompanist.flowlayout.FlowRow
import com.radx.ankunv2.anime.AnimeDetailsScreenNav
import com.radx.ankunv2.anime.AnimeGenre
import com.radx.ankunv2.anime.AnimeSearch
import com.radx.ankunv2.anime.AnimeSeasons
import com.radx.ankunv2.ui.theme.BrightGrey
import com.radx.ankunv2.ui.theme.Grey
import com.radx.ankunv2.ui.theme.PurpleGrey40
import com.radx.ankunv2.ui.theme.Transparent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun SearchNavigationHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = SearchMenus.Search.route
    ) {
        composable(route = SearchMenus.Search.route) { SearchMainScreen(navController) }
    }
}

@Composable
fun SearchScreen() {
    val navController = rememberNavController()
    SearchNavigationHost(navController)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchMainScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp, 0.dp, 16.dp, 0.dp)
    ) {
        var searchTextInput by remember { mutableStateOf(TextFieldValue("")) }
        var seasonInput by remember { mutableStateOf("") }
        var genresInput by remember { mutableStateOf(listOf("")) }
        var typeInput by remember { mutableStateOf("") }
        var airingInput by remember { mutableStateOf("") }
        var sortInput by remember { mutableStateOf("popular-week") }
        var currentPage by remember { mutableStateOf("1") }

        var animeListState by remember { mutableStateOf(listOf(listOf(""))) }

        val coroutineScope = rememberCoroutineScope()

        Text(
            text = "Search",
            fontSize = 22.sp,
            modifier = Modifier
                .padding(PaddingValues(bottom = 16.dp))
        )

        // search input text and button
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // outlined text field
            MaterialTheme(
                shapes = MaterialTheme.shapes.copy(extraSmall = RoundedCornerShape(30.dp))
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .height(60.dp),
                    value = searchTextInput,
                    label = { Text("Search") },
                    onValueChange = { newText ->
                        searchTextInput = newText
                    },
                    placeholder = { Text(text = "Anime Title...") },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        containerColor = Transparent,
                        focusedTrailingIconColor = Grey,
                        unfocusedTrailingIconColor = Grey,
                        unfocusedLabelColor = Grey,
                        focusedLabelColor = Grey,
                        placeholderColor = BrightGrey
                    )
                )
            }
            // start search button
            Button(
                content = { Icon(Icons.Default.Search, contentDescription = null) },
                onClick = {
                    coroutineScope.launch {
                        getAnimeList(
                            search = searchTextInput.text,
                            season = seasonInput,
                            genres = "",
                            dub = typeInput,
                            airing = airingInput,
                            sort = sortInput,
                            page = "1"
                        )
                        animeListState = animeList
                        // Log.e("ANIME", animeList.toString())
                    }
                },
                shape = CircleShape,
                contentPadding = PaddingValues(0.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onSecondaryContainer),
                modifier = Modifier
                    .size(45.dp)
            )
        }

        // filters
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(0.dp, 10.dp, 0.dp, 0.dp)
                .fillMaxWidth()
        ) {
            var visible by remember { mutableStateOf(false) } // change to false to not show the filter settings on load

            Column(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = rememberRipple(color = MaterialTheme.colorScheme.secondary),
                        onClick = { visible = !visible }
                    )
            ) {
                Text(
                    text = "Filters",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .padding(0.dp, 0.dp, 0.dp, 0.dp)
                        .align(Alignment.CenterHorizontally)
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                )
            }

            // filter settings
            AnimatedVisibility(visible = visible) {
                Column {
                    // type: all, sub, dub, chinese
                    Column {
                        var selectedAll by remember { mutableStateOf(true) }
                        var selectedSub by remember { mutableStateOf(false) }
                        var selectedDub by remember { mutableStateOf(false) }
                        var selectedChinese by remember { mutableStateOf(false) }

                        typeInput =
                            if (selectedAll) {
                                ""
                            } else if (selectedSub) {
                                "0"
                            } else if(selectedDub) {
                                "1"
                            } else {
                                "2"
                            }

                        Text(
                            text = "Type",
                            modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 8.dp)
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // all
                            FilterChip(
                                selected = selectedAll,
                                onClick = {
                                    if (selectedDub || selectedChinese || selectedSub) {
                                        selectedAll = !selectedAll
                                        selectedDub = false
                                        selectedChinese = false
                                        selectedSub = false
                                    }
                                },
                                label = { Text(text = "All") }
                            )
                            // sub
                            FilterChip(
                                selected = selectedSub,
                                onClick = {
                                    if (selectedDub || selectedChinese || selectedAll) {
                                        selectedSub = !selectedSub
                                        selectedDub = false
                                        selectedChinese = false
                                        selectedAll = false
                                    }
                                },
                                label = { Text(text = "Sub") }
                            )
                            // dub
                            FilterChip(
                                selected = selectedDub,
                                onClick = {
                                    if (selectedSub || selectedChinese || selectedAll) {
                                        selectedDub = !selectedDub
                                        selectedSub = false
                                        selectedChinese = false
                                        selectedAll = false
                                    }
                                },
                                label = { Text(text = "Dub") }
                            )
                            // chinese
                            FilterChip(
                                selected = selectedChinese,
                                onClick = {
                                    if (selectedSub || selectedDub || selectedAll) {
                                        selectedChinese = !selectedChinese
                                        selectedSub = false
                                        selectedDub = false
                                        selectedAll = false
                                    }
                                },
                                label = { Text(text = "Chinese") }
                            )
                        }
                    }
                    Divider()

                    // status: all, ongoing, completed
                    Column(
                        modifier = Modifier.padding(0.dp, 8.dp, 0.dp, 0.dp)
                    ) {
                        var selectedAll by remember { mutableStateOf(true) }
                        var selectedOngoing by remember { mutableStateOf(false) }
                        var selectedCompleted by remember { mutableStateOf(false) }

                        Text(
                            text = "Status",
                            modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 8.dp)
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // all
                            FilterChip(
                                selected = selectedAll,
                                onClick = {
                                    if (selectedOngoing || selectedCompleted) {
                                        selectedAll = !selectedAll
                                        selectedOngoing = false
                                        selectedCompleted = false
                                    }
                                },
                                label = { Text(text = "All") }
                            )
                            // ongoing
                            FilterChip(
                                selected = selectedOngoing,
                                onClick = {
                                    if (selectedAll || selectedCompleted) {
                                        selectedAll = false
                                        selectedOngoing = !selectedOngoing
                                        selectedCompleted = false
                                    }
                                },
                                label = { Text(text = "Ongoing") }
                            )
                            // completed
                            FilterChip(
                                selected = selectedCompleted,
                                onClick = {
                                    if (selectedAll || selectedOngoing) {
                                        selectedAll = false
                                        selectedOngoing = false
                                        selectedCompleted = !selectedCompleted
                                    }
                                },
                                label = { Text(text = "Completed") }
                            )
                        }
                    }
                    Divider()

                    // sort: popular-week, popular-year, A-Z, Z-A, ranking
                    Column(
                        modifier = Modifier.padding(0.dp, 8.dp, 0.dp, 0.dp)
                    ) {
                        var selectedWeek by remember { mutableStateOf(true) }
                        var selectedYear by remember { mutableStateOf(false) }
                        var selectedAZ by remember { mutableStateOf(false) }
                        var selectedZA by remember { mutableStateOf(false) }
                        var selectedRank by remember { mutableStateOf(false) }

                        Text(
                            text = "Sort",
                            modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 8.dp)
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // popular-week
                            FilterChip(
                                selected = selectedWeek,
                                onClick = {
                                    if (selectedYear || selectedAZ || selectedZA || selectedRank) {
                                        selectedWeek = !selectedWeek
                                        selectedYear = false
                                        selectedAZ = false
                                        selectedZA = false
                                        selectedRank = false
                                    }
                                },
                                label = { Text(text = "Popular (Week)") }
                            )

                            // popular-year
                            FilterChip(
                                selected = selectedYear,
                                onClick = {
                                    if (selectedWeek || selectedAZ || selectedZA || selectedRank) {
                                        selectedWeek = false
                                        selectedYear = !selectedYear
                                        selectedAZ = false
                                        selectedZA = false
                                        selectedRank = false
                                    }
                                },
                                label = { Text(text = "Popular (Year)") }
                            )
                        }
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // a-z
                            FilterChip(
                                selected = selectedAZ,
                                onClick = {
                                    if (selectedYear || selectedWeek || selectedZA || selectedRank) {
                                        selectedWeek = false
                                        selectedYear = false
                                        selectedAZ = !selectedAZ
                                        selectedZA = false
                                        selectedRank = false
                                    }
                                },
                                label = { Text(text = "A-Z") }
                            )

                            // z-a
                            FilterChip(
                                selected = selectedZA,
                                onClick = {
                                    if (selectedYear || selectedWeek || selectedAZ || selectedRank) {
                                        selectedWeek = false
                                        selectedYear = false
                                        selectedAZ = false
                                        selectedZA = !selectedZA
                                        selectedRank = false
                                    }
                                },
                                label = { Text(text = "Z-A") }
                            )

                            // ranking
                            FilterChip(
                                selected = selectedRank,
                                onClick = {
                                    if (selectedYear || selectedWeek || selectedAZ || selectedZA) {
                                        selectedWeek = false
                                        selectedYear = false
                                        selectedAZ = false
                                        selectedZA = false
                                        selectedRank = !selectedRank
                                    }
                                },
                                label = { Text(text = "Ranking") }
                            )
                        }
                    }
                    Divider()

                    // season:
                    Column(
                        modifier = Modifier.padding(0.dp, 8.dp, 0.dp, 0.dp)
                    ) {
                        var dropdownShowMenu by remember { mutableStateOf(false) }
                        var dropdownSelectedItem by remember { mutableStateOf("") }

                        LaunchedEffect(Unit) {
                            getSeasonList()
                        }

                        Text(
                            text = "Seasons",
                            modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 8.dp)
                        )

                        ExposedDropdownMenuBox(
                            expanded = dropdownShowMenu,
                            onExpandedChange = { dropdownShowMenu = !dropdownShowMenu },
                            modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 16.dp)
                        ) {
                            OutlinedTextField(
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth()
                                    .height(60.dp),
                                readOnly = true,
                                value = dropdownSelectedItem,
                                onValueChange = { },
                                label = { },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(
                                        expanded = dropdownShowMenu
                                    )
                                },
                                placeholder = { Text("None") },
                                colors = ExposedDropdownMenuDefaults.textFieldColors(
                                    containerColor = Transparent,
                                    focusedIndicatorColor = Grey,
                                    unfocusedIndicatorColor = Grey,
                                    focusedTrailingIconColor = Grey,
                                    unfocusedTrailingIconColor = Grey,
                                    placeholderColor = BrightGrey,
                                    unfocusedLabelColor = PurpleGrey40,
                                    focusedLabelColor = PurpleGrey40
                                )
                            )
                            ExposedDropdownMenu(
                                expanded = dropdownShowMenu,
                                onDismissRequest = { dropdownShowMenu = false },
                                modifier = Modifier.requiredSizeIn(maxHeight = 330.dp)
                            ) {
                                dropdownItems.forEach { item ->
                                    DropdownMenuItem(
                                        enabled = true,
                                        onClick = {
                                            dropdownSelectedItem = item
                                            dropdownShowMenu = false
                                        },
                                        text = { Text(text = item) },
                                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                                    )
                                }
                            }
                        }
                    }
                    Divider()

                    // genre:
                    Column(
                        modifier = Modifier.padding(0.dp, 8.dp, 0.dp, 0.dp)
                    ) {
                        var dropdownShowMenu by remember { mutableStateOf(false) }
                        var dropdownSelectedItem by remember { mutableStateOf("") }
                        var selectedGenres = remember { mutableStateListOf<String>() }

                        Text(
                            text = "Genres",
                            modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 8.dp)
                        )
                        ExposedDropdownMenuBox(
                            expanded = dropdownShowMenu,
                            onExpandedChange = { dropdownShowMenu = !dropdownShowMenu },
                            modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 0.dp)
                        ) {
                            OutlinedTextField(
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth()
                                    .height(60.dp),
                                readOnly = true,
                                value = dropdownSelectedItem,
                                onValueChange = { },
                                label = { },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(
                                        expanded = dropdownShowMenu
                                    )
                                },
                                placeholder = { Text("None") },
                                colors = ExposedDropdownMenuDefaults.textFieldColors(
                                    containerColor = Transparent,
                                    focusedIndicatorColor = Grey,
                                    unfocusedIndicatorColor = Grey,
                                    focusedTrailingIconColor = Grey,
                                    unfocusedTrailingIconColor = Grey,
                                    placeholderColor = BrightGrey,
                                    unfocusedLabelColor = PurpleGrey40,
                                    focusedLabelColor = PurpleGrey40
                                )
                            )
                            ExposedDropdownMenu(
                                expanded = dropdownShowMenu,
                                onDismissRequest = { dropdownShowMenu = false },
                                modifier = Modifier.requiredSizeIn(maxHeight = 330.dp)
                            ) {
                                allGenreList.forEach { item ->
                                    DropdownMenuItem(
                                        enabled = true,
                                        onClick = {
                                            dropdownSelectedItem = item
                                            selectedGenres.add(item)
                                            dropdownShowMenu = false
                                        },
                                        text = { Text(text = item) },
                                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                                    )
                                }
                            }
                        }
                        // selected genres
                        FlowRow(
                            mainAxisSpacing = 8.dp,
                            crossAxisSpacing = 8.dp
                        ) {
                            selectedGenres.forEach { item ->
                                if (selectedGenres.size > 0) {
                                    InputChip(
                                        selected = false,
                                        onClick = {
                                            selectedGenres.remove(item)
                                        },
                                        label = { Text(text = item) },
                                        colors = InputChipDefaults.inputChipColors(
                                            labelColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                        ),
                                        trailingIcon = {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = null,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        },
                                        modifier = Modifier.align(Alignment.CenterHorizontally)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // next and previous page
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(0.dp, 16.dp, 0.dp, 16.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            FilledTonalButton(
                enabled = currentPage != "1",
                modifier = Modifier.padding(0.dp, 0.dp, 40.dp, 0.dp),
                content = { Text(text = "Prev") },
                onClick = {
                    currentPage = (Integer.parseInt(currentPage) - 1).toString()
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
                    currentPage = (Integer.parseInt(currentPage) + 1).toString()
                }
            )
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 32.dp)
        ) {
            items(animeListState) { item->
                if (animeListState.size != 1) {
                    AnimeSearchListCard(item)
                }
            }
        }

        LaunchedEffect(currentPage) {
            launch {
                snapshotFlow { currentPage }
                    .apply {
                        try {
                            getAnimeList(
                                search = searchTextInput.text,
                                season = seasonInput,
                                genres = "",
                                dub = typeInput,
                                airing = airingInput,
                                sort = sortInput,
                                page = currentPage
                            )
                            animeListState = animeList
                        } catch (ignored: IndexOutOfBoundsException) {}
                    }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimeSearchListCard(anime: List<String>) {
    // [[Anime Title, Anime ID, ANIME THUMBNAIL, SUB OR DUB (sub=0, dub=1]]
    val title = anime[0].replace("\"", "")
    val id = anime[1]
    val thumbnailUrl = anime[2].replace("\"", "")
    val isDub = anime[3]

    Card(
        modifier = Modifier
            .height(220.dp)
            .width(160.dp),
        shape = RoundedCornerShape(18.dp),
        onClick = { //navController.navigate("${AnimeDetailsScreenNav.AnimeDetails.route}/$id")
            }
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

var dropdownItems = listOf("")
suspend fun getSeasonList() = withContext(Dispatchers.IO) {
    fillSeasonList()
}
fun fillSeasonList() {
    dropdownItems = AnimeSeasons.getSeasonsList().subList(0, 10)
}

var allGenreList = AnimeGenre.getAllGenderList()

var animeList = listOf(listOf(""))
suspend fun getAnimeList(search: String = "",
                         season: String = "",
                         genres: String = "",
                         dub: String = "",
                         airing: String = "",
                         sort: String = "popular-week",
                         page: String = "1")
= withContext(Dispatchers.IO) {
    fillAnimeList(search, season, genres, dub, airing, sort, page)
}
fun fillAnimeList(search: String, season: String, genres: String, dub: String, airing: String, sort: String, page: String) {
    animeList = AnimeSearch.getSearchResultList(
        search = search,
        season = season,
        genres = genres,
        dub = dub,
        airing = airing,
        sort = sort,
        page = page
    )
}