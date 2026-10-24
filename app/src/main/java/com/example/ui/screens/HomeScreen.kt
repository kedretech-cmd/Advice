package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DailyAdviceEntity
import com.example.ui.components.AdviceCard
import com.example.ui.components.AuthDialog
import com.example.ui.components.CategoryFilterRow
import com.example.ui.components.DailyProgressCard
import com.example.ui.components.EthiopianHeaderBanner
import com.example.ui.components.SocialShareDialog
import com.example.ui.theme.BorderGray
import com.example.ui.theme.GrayTextSecondary
import com.example.ui.theme.LightGreenBackground
import com.example.ui.theme.PrimaryGreen
import com.example.ui.theme.PureWhite
import com.example.ui.viewmodel.AdviceViewModel
import com.example.ui.viewmodel.GenerationUiState

@Composable
fun HomeScreen(
    viewModel: AdviceViewModel,
    onNavigateToDetail: (DailyAdviceEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val adviceList by viewModel.filteredTodayAdvice.collectAsState()
    val allTodayList by viewModel.todayAdviceList.collectAsState()
    val selectedCategoryKey by viewModel.selectedCategoryKey.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val fontScale by viewModel.fontScale.collectAsState()
    val generationState by viewModel.generationState.collectAsState()

    val currentUser by viewModel.currentUser.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val userReactions by viewModel.userReactions.collectAsState()

    var shareItem by remember { mutableStateOf<DailyAdviceEntity?>(null) }
    var isSearchExpanded by remember { mutableStateOf(false) }
    var showAuthDialog by remember { mutableStateOf(false) }

    val completedCount = allTodayList.count { it.isActionCompleted }
    val totalCount = allTodayList.size

    shareItem?.let { item ->
        SocialShareDialog(
            item = item,
            onDismiss = { shareItem = null }
        )
    }

    if (showAuthDialog) {
        AuthDialog(
            userProfile = userProfile,
            onDismiss = { showAuthDialog = false },
            onAuthSuccess = {
                showAuthDialog = false
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(PureWhite)
            .testTag("home_screen")
    ) {
        // Top Action Bar
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = PureWhite,
            shadowElevation = 0.5.dp
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "የዕለቱ 10 ምክሮች",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // User Profile / Sign In
                        IconButton(
                            onClick = { showAuthDialog = true },
                            modifier = Modifier.testTag("user_profile_button")
                        ) {
                            if (currentUser != null) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(LightGreenBackground),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = (currentUser?.displayName?.firstOrNull() ?: currentUser?.email?.firstOrNull() ?: 'U').uppercase(),
                                        fontWeight = FontWeight.Bold,
                                        color = PrimaryGreen,
                                        fontSize = 12.sp
                                    )
                                }
                            } else {
                                Icon(
                                    imageVector = Icons.Default.AccountCircle,
                                    contentDescription = "Sign in",
                                    tint = PrimaryGreen
                                )
                            }
                        }

                        // Search Toggle
                        IconButton(
                            onClick = { isSearchExpanded = !isSearchExpanded },
                            modifier = Modifier.testTag("search_toggle_button")
                        ) {
                            Icon(
                                imageVector = if (isSearchExpanded) Icons.Default.Close else Icons.Default.Search,
                                contentDescription = "Search advice",
                                tint = Color.Black
                            )
                        }

                        // Font scale toggle
                        IconButton(
                            onClick = {
                                if (fontScale >= 1.25f) viewModel.decreaseFontScale()
                                else viewModel.increaseFontScale()
                            },
                            modifier = Modifier.testTag("font_scale_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.FormatSize,
                                contentDescription = "Change font size",
                                tint = Color.Black
                            )
                        }

                        // Refresh
                        IconButton(
                            onClick = { viewModel.checkAndInitializeTodayAdvice() },
                            modifier = Modifier.testTag("refresh_today_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Refresh today advice",
                                tint = PrimaryGreen
                            )
                        }
                    }
                }

                // Expandable Search Field
                AnimatedVisibility(visible = isSearchExpanded) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.setSearchQuery(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                            .testTag("search_input_field"),
                        placeholder = { Text("በአማርኛ ወይም በእንግሊዝኛ ይፈልጉ...") },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = null, tint = PrimaryGreen)
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                    Icon(Icons.Default.Close, contentDescription = "Clear search")
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryGreen,
                            unfocusedBorderColor = BorderGray,
                            focusedContainerColor = PureWhite,
                            unfocusedContainerColor = PureWhite
                        )
                    )
                }
            }
        }

        // AI Generation feedback banner
        when (val state = generationState) {
            is GenerationUiState.Loading -> {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(10.dp),
                    color = LightGreenBackground,
                    border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryGreen.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp,
                            color = PrimaryGreen
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = state.message,
                            style = MaterialTheme.typography.bodySmall,
                            color = PrimaryGreen,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
            is GenerationUiState.Error -> {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFFEF2F2),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFCA5A5))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = state.error,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFB91C1C),
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = { viewModel.resetGenerationState() }) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Dismiss",
                                modifier = Modifier.size(16.dp),
                                tint = Color(0xFFB91C1C)
                            )
                        }
                    }
                }
            }
            else -> {}
        }

        // Main Feed
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("advice_list"),
            contentPadding = PaddingValues(bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header Hero Banner
            item {
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    EthiopianHeaderBanner(ethiopianDateStr = viewModel.todayEthiopianDate)
                }
            }

            // Daily Practical Actions Progress Card
            if (totalCount > 0) {
                item {
                    Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                        DailyProgressCard(
                            completedCount = completedCount,
                            totalCount = totalCount
                        )
                    }
                }
            }

            // Category Filter Chips
            item {
                CategoryFilterRow(
                    selectedCategoryKey = selectedCategoryKey,
                    onCategorySelected = { viewModel.setCategoryFilter(it) }
                )
            }

            // Empty state if search or filter yielded 0
            if (adviceList.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = PrimaryGreen
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (searchQuery.isNotBlank()) "በዚህ ፍለጋ ምንም ምክር አልተገኘም" else "ምንም ምክር አልተገኘም",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "እባክዎ ሌላ ቃል ይፈልጉ ወይም አዲስ ምክር ያፍልቁ",
                            style = MaterialTheme.typography.bodyMedium,
                            color = GrayTextSecondary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                items(
                    items = adviceList,
                    key = { it.id }
                ) { item ->
                    Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                        AdviceCard(
                            item = item,
                            fontScale = fontScale,
                            onClick = { onNavigateToDetail(item) },
                            onToggleFavorite = { viewModel.toggleFavorite(item.id, item.isFavorite) },
                            onToggleAction = { viewModel.toggleActionCompleted(item.id, item.isActionCompleted) },
                            onShare = { shareItem = item },
                            userReaction = userReactions[item.id],
                            onQuickLike = {
                                if (currentUser == null) {
                                    showAuthDialog = true
                                } else {
                                    viewModel.toggleReaction(item.id, "like")
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
