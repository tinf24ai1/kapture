package com.kapture.kapture.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Archive
import androidx.compose.material.icons.rounded.Home
import androidx.compose.ui.graphics.vector.ImageVector

// Screens for Navigation
sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Archive : Screen("archive")
}

// Data class for Navigation Item
data class NavigationItem(
    val name: String,
    val icon: ImageVector,
    val route: String,
)

// Enum class for Navigation Index
enum class NavigationIndex(val index: Int) {
    HOME_SCREEN(0),
    ARCHIVE_SCREEN(1);

    companion object Convert {
        fun fromIndex(index: Int): NavigationIndex {
            return entries.firstOrNull { it.index == index } ?: HOME_SCREEN
        }
    }
}

// Object for managing Navigation Items
object Navigation {

    private val items = listOf(
        NavigationItem(
            name = "Home",
            icon = Icons.Rounded.Home,
            route = Screen.Home.route
        ),
        NavigationItem(
            name = "Archive",
            icon = Icons.Rounded.Archive,
            route = Screen.Archive.route
        ),
    )

    fun navigationItems(): List<NavigationItem> {
        return this.items
    }
}
