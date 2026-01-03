package com.kapture.kapture.ui.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.kapture.kapture.ai.AIViewModel
import com.kapture.kapture.navigation.Navigation
import com.kapture.kapture.navigation.NavigationIndex
import com.kapture.kapture.navigation.Screen
import com.kapture.kapture.storage.ItemModel
import com.kapture.kapture.storage.LocalStorage
import com.kapture.kapture.ui.screens.ArchiveScreen
import com.kapture.kapture.ui.screens.HomeScreen
import com.kapture.kapture.storage.ItemList
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlinx.datetime.*
import kotlin.random.Random

// Bottom Navigation Bar with Home and Archive screens
@Composable
fun BottomNavigationBar(
    itemList: ItemList,
    aiViewModel: AIViewModel
) {

    // Function to calculate a random release date within a specified range
    val releaseDate: (startPlus: Int, endPlus: Int) -> LocalDate = { startPlus, endPlus ->
        val baseDate = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val start = baseDate.plus(startPlus, DateTimeUnit.DAY).toEpochDays()
        val end = baseDate.plus(endPlus, DateTimeUnit.DAY).toEpochDays()
        val randomDay = Random.nextInt(start, end + 1)
        LocalDate.fromEpochDays(randomDay)
    }

    // Restore archived items from LocalStorage
    var list = mutableStateListOf<ItemModel>()
    if (LocalStorage.isset("archiveList")) {
        list = (LocalStorage.load<MutableList<ItemModel>>("archiveList"))?.toMutableStateList() ?: list
    }

    val archiveList by remember {
        mutableStateOf(list)
    }

    val addToArchiveList: (ItemModel) -> Unit = { item ->
        archiveList.add(item)
        LocalStorage.save<MutableList<ItemModel>>("archiveList", archiveList.toMutableList())
    }
    val rmFromArchiveList: (ItemModel) -> Unit = { item ->
        archiveList.remove(item)
        LocalStorage.save<MutableList<ItemModel>>("archiveList", archiveList.toMutableList())
    }

    // Navigation controller to manage navigation between screens
    val navController = rememberNavController()

    var selectedItem by remember {
        mutableStateOf(NavigationIndex.HOME_SCREEN)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                Navigation.navigationItems().forEachIndexed {
                    index, item ->
                        NavigationBarItem(
                            selected = (index == selectedItem.index),
                            label = {
                                Text(item.name)
                            },
                            icon = {
                                Icon(item.icon, item.name)
                            },
                            onClick = {
                                selectedItem = NavigationIndex.fromIndex(index)
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(paddingValues),
        ) {
            composable(Screen.Home.route) {
                HomeScreen(itemList, addToArchiveList, releaseDate, aiViewModel)
            }
            composable(Screen.Archive.route) {
                ArchiveScreen(archiveList, rmFromArchiveList)
            }
        }
    }
}
