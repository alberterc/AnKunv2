package com.radx.ankunv2.screens.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.radx.ankunv2.anime.AnimeSeasons
import com.radx.ankunv2.ui.theme.BrightGrey
import com.radx.ankunv2.ui.theme.Grey
import com.radx.ankunv2.ui.theme.PurpleGrey40
import com.radx.ankunv2.ui.theme.Transparent
import kotlinx.coroutines.Dispatchers
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
        var currentPage by remember { mutableStateOf("1") }

        Text(
            text = "Search",
            fontSize = 22.sp,
            modifier = Modifier
                .padding(PaddingValues(bottom = 16.dp))
        )

        // outlined text field
        MaterialTheme(
            shapes = MaterialTheme.shapes.copy(extraSmall = RoundedCornerShape(30.dp))
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
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

        // filters
        Column(
            modifier = Modifier
                .padding(0.dp, 10.dp, 0.dp, 0.dp)
                .fillMaxWidth()
        ) {
            var visible by remember { mutableStateOf(true) } // change to false to not show the filter settings on load

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

            // filter settings
            AnimatedVisibility(visible = visible) {
                Column {
                    // type: all, sub, dub, chinese
                    Column {
                        var selectedAll by remember { mutableStateOf(true) }
                        var selectedSub by remember { mutableStateOf(false) }
                        var selectedDub by remember { mutableStateOf(false) }
                        var selectedChinese by remember { mutableStateOf(false) }

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
                        var dropdownSelectedItem by remember { mutableStateOf(dropdownItems[0]) }

                        LaunchedEffect(Unit) {
                            getSeasonList()
                            dropdownSelectedItem = dropdownItems[0]
                        }

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
                                label = { Text("Seasons") },
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

                    // genre:
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
        ) {}
    }
}

var dropdownItems = listOf("")
suspend fun getSeasonList() = withContext(Dispatchers.IO) {
    fillSeasonList()
}
fun fillSeasonList() {
    dropdownItems = AnimeSeasons.getSeasonsList().subList(0, 10)
}