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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.kapture.kapture.navigation.Navigation
import com.kapture.kapture.navigation.NavigationIndex
import com.kapture.kapture.navigation.Screen
import com.kapture.kapture.ui.screens.ArchiveScreen
import com.kapture.kapture.ui.screens.HomeScreen

@Composable
fun BottomNavigationBar() {

    var selectedItem by remember {
        mutableStateOf(NavigationIndex.HOME_SCREEN)
    }

    val navController = rememberNavController()

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
    ) {
            paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(paddingValues),
        ) {
            composable(Screen.Home.route) {
                HomeScreen()
            }
            composable(Screen.Archive.route) {
                ArchiveScreen()
            }
        }
    }
}
