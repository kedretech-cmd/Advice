package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import com.example.data.model.DailyAdviceEntity
import com.example.ui.screens.AdminScreen
import com.example.ui.screens.AdviceDetailScreen
import com.example.ui.screens.CategoriesScreen
import com.example.ui.screens.DailyActionsScreen
import com.example.ui.screens.FavoritesScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.theme.DailyAdviceTheme
import com.example.ui.theme.EthiopianGreenPrimary
import com.example.ui.viewmodel.AdviceViewModel

enum class MainNavTab(val amharicTitle: String, val icon: ImageVector, val tag: String) {
    HOME("ዋና ገጽ", Icons.Default.Home, "nav_tab_home"),
    CATEGORIES("ዘርፎች", Icons.Default.Category, "nav_tab_categories"),
    ACTIONS("ተግባራት", Icons.Default.TaskAlt, "nav_tab_actions"),
    FAVORITES("የተወደዱ", Icons.Default.Bookmark, "nav_tab_favorites"),
    ADMIN("አስተዳደር", Icons.Default.AdminPanelSettings, "nav_tab_admin")
}

class MainActivity : ComponentActivity() {

    private val adviceViewModel: AdviceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DailyAdviceTheme {
                MainAppContent(viewModel = adviceViewModel)
            }
        }
    }
}

@Composable
fun MainAppContent(viewModel: AdviceViewModel) {
    var currentTab by remember { mutableStateOf(MainNavTab.HOME) }
    var selectedDetailItem by remember { mutableStateOf<DailyAdviceEntity?>(null) }

    // If detail screen is open, back handler pops detail
    if (selectedDetailItem != null) {
        AdviceDetailScreen(
            item = selectedDetailItem!!,
            viewModel = viewModel,
            onBack = { selectedDetailItem = null }
        )
    } else {
        // If on secondary tab, back button returns to Home tab
        if (currentTab != MainNavTab.HOME) {
            BackHandler {
                currentTab = MainNavTab.HOME
            }
        }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                NavigationBar(
                    modifier = Modifier.testTag("bottom_nav_bar")
                ) {
                    MainNavTab.entries.forEach { tab ->
                        val selected = currentTab == tab
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                if (currentTab != tab) {
                                    currentTab = tab
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = tab.icon,
                                    contentDescription = tab.amharicTitle
                                )
                            },
                            label = {
                                Text(text = tab.amharicTitle)
                            },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = EthiopianGreenPrimary.copy(alpha = 0.2f),
                                selectedIconColor = EthiopianGreenPrimary,
                                selectedTextColor = EthiopianGreenPrimary
                            ),
                            modifier = Modifier.testTag(tab.tag)
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (currentTab) {
                    MainNavTab.HOME -> {
                        HomeScreen(
                            viewModel = viewModel,
                            onNavigateToDetail = { selectedDetailItem = it }
                        )
                    }
                    MainNavTab.CATEGORIES -> {
                        CategoriesScreen(
                            viewModel = viewModel,
                            onCategoryClick = { category ->
                                viewModel.setCategoryFilter(category.key)
                                currentTab = MainNavTab.HOME
                            }
                        )
                    }
                    MainNavTab.ACTIONS -> {
                        DailyActionsScreen(
                            viewModel = viewModel,
                            onNavigateToDetail = { selectedDetailItem = it }
                        )
                    }
                    MainNavTab.FAVORITES -> {
                        FavoritesScreen(
                            viewModel = viewModel,
                            onNavigateToDetail = { selectedDetailItem = it }
                        )
                    }
                    MainNavTab.ADMIN -> {
                        AdminScreen(
                            viewModel = viewModel
                        )
                    }
                }
            }
        }
    }
}
