package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.MainNavTab
import com.example.ui.theme.BorderGray
import com.example.ui.theme.EthiopianGold
import com.example.ui.theme.EthiopianGreenPrimary
import com.example.ui.theme.EthiopianRed
import com.example.ui.theme.GrayTextSecondary
import com.example.ui.theme.LightGreenBackground
import com.example.ui.theme.PrimaryGreen
import com.example.ui.theme.PureWhite
import com.example.ui.viewmodel.AdviceViewModel

@Composable
fun AppNavigationDrawerSheet(
    viewModel: AdviceViewModel,
    currentTab: MainNavTab,
    isAdminScreenOpen: Boolean,
    onSelectTab: (MainNavTab) -> Unit,
    onOpenAdminScreen: () -> Unit,
    onCloseDrawer: () -> Unit,
    onShowAuthDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val todayList by viewModel.todayAdviceList.collectAsState()
    val favoritesList by viewModel.favoritesList.collectAsState()
    val fontScale by viewModel.fontScale.collectAsState()
    val isAdmin = viewModel.isAdmin

    val completedCount = todayList.count { it.isActionCompleted }
    val totalCount = todayList.size

    ModalDrawerSheet(
        modifier = modifier
            .widthIn(max = 330.dp)
            .fillMaxHeight()
            .testTag("app_navigation_drawer"),
        drawerContainerColor = PureWhite,
        drawerShape = RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
        ) {
            // Ethiopian Flag Accent Bar (Green, Gold, Red)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(Color(0xFF16A34A))
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(Color(0xFFFBBF24))
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(Color(0xFFDC2626))
                )
            }

            // Drawer Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(LightGreenBackground)
                    .padding(horizontal = 20.dp, vertical = 20.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(PureWhite),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.WbSunny,
                            contentDescription = "የዕለት ምክር",
                            tint = PrimaryGreen,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "የዕለት ምክር",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = PrimaryGreen,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "በ Kedre AI የተዘጋጀ",
                                style = MaterialTheme.typography.labelSmall,
                                color = PrimaryGreen,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // User Profile Summary Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = PureWhite),
                    border = BorderStroke(1.dp, BorderGray)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(PrimaryGreen.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                if (currentUser != null) {
                                    Text(
                                        text = (currentUser?.displayName?.firstOrNull()
                                            ?: currentUser?.email?.firstOrNull() ?: 'U').uppercase(),
                                        fontWeight = FontWeight.Bold,
                                        color = PrimaryGreen,
                                        fontSize = 15.sp
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        tint = PrimaryGreen,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Column {
                                val name = currentUser?.displayName?.ifBlank { null }
                                    ?: currentUser?.email?.substringBefore("@")
                                    ?: "እንግዳ ተጠቃሚ"
                                Text(
                                    text = name,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black,
                                    maxLines = 1
                                )

                                if (isAdmin) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = EthiopianGold.copy(alpha = 0.2f),
                                        modifier = Modifier.padding(top = 2.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Security,
                                                contentDescription = null,
                                                tint = Color(0xFFB45309),
                                                modifier = Modifier.size(10.dp)
                                            )
                                            Spacer(modifier = Modifier.width(3.dp))
                                            Text(
                                                text = "ዋና አስተዳዳሪ",
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFFB45309),
                                                fontSize = 10.sp
                                            )
                                        }
                                    }
                                } else if (currentUser != null) {
                                    Text(
                                        text = currentUser?.email ?: "ተጠቃሚ",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = GrayTextSecondary,
                                        fontSize = 11.sp,
                                        maxLines = 1
                                    )
                                } else {
                                    Text(
                                        text = "ያልገቡ • ይግቡ",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = PrimaryGreen,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }

                        IconButton(
                            onClick = {
                                onCloseDrawer()
                                onShowAuthDialog()
                            },
                            modifier = Modifier.size(32.dp).testTag("drawer_auth_button")
                        ) {
                            Icon(
                                imageVector = if (currentUser != null) Icons.AutoMirrored.Filled.Logout else Icons.AutoMirrored.Filled.Login,
                                contentDescription = if (currentUser != null) "Logout" else "Sign In",
                                tint = PrimaryGreen,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Navigation Items Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
            ) {
                Text(
                    text = "ዋና ምናሌ",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = GrayTextSecondary,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )

                // 1. Home
                NavigationDrawerItem(
                    label = { Text("ዋና ገጽ (Home)", fontWeight = FontWeight.Medium) },
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    badge = {
                        if (totalCount > 0) {
                            Badge(containerColor = PrimaryGreen.copy(alpha = 0.15f), contentColor = PrimaryGreen) {
                                Text("$totalCount ምክሮች", fontWeight = FontWeight.Bold)
                            }
                        }
                    },
                    selected = currentTab == MainNavTab.HOME && !isAdminScreenOpen,
                    onClick = {
                        onSelectTab(MainNavTab.HOME)
                        onCloseDrawer()
                    },
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = LightGreenBackground,
                        selectedIconColor = PrimaryGreen,
                        selectedTextColor = PrimaryGreen,
                        unselectedTextColor = Color.Black
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.padding(vertical = 2.dp).testTag("drawer_item_home")
                )

                // 2. Categories
                NavigationDrawerItem(
                    label = { Text("ዘርፎች (Categories)", fontWeight = FontWeight.Medium) },
                    icon = { Icon(Icons.Default.Category, contentDescription = null) },
                    badge = {
                        Badge(containerColor = BorderGray.copy(alpha = 0.6f), contentColor = Color.Black) {
                            Text("10 ዘርፎች")
                        }
                    },
                    selected = currentTab == MainNavTab.CATEGORIES && !isAdminScreenOpen,
                    onClick = {
                        onSelectTab(MainNavTab.CATEGORIES)
                        onCloseDrawer()
                    },
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = LightGreenBackground,
                        selectedIconColor = PrimaryGreen,
                        selectedTextColor = PrimaryGreen,
                        unselectedTextColor = Color.Black
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.padding(vertical = 2.dp).testTag("drawer_item_categories")
                )

                // 3. Daily Actions
                NavigationDrawerItem(
                    label = { Text("ዕለታዊ ተግባራት (Daily Actions)", fontWeight = FontWeight.Medium) },
                    icon = { Icon(Icons.Default.TaskAlt, contentDescription = null) },
                    badge = {
                        if (totalCount > 0) {
                            val badgeColor = if (completedCount == totalCount) PrimaryGreen else EthiopianGold
                            Badge(containerColor = badgeColor.copy(alpha = 0.2f), contentColor = Color.Black) {
                                Text("$completedCount/$totalCount")
                            }
                        }
                    },
                    selected = currentTab == MainNavTab.ACTIONS && !isAdminScreenOpen,
                    onClick = {
                        onSelectTab(MainNavTab.ACTIONS)
                        onCloseDrawer()
                    },
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = LightGreenBackground,
                        selectedIconColor = PrimaryGreen,
                        selectedTextColor = PrimaryGreen,
                        unselectedTextColor = Color.Black
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.padding(vertical = 2.dp).testTag("drawer_item_actions")
                )

                // 4. Favorites
                NavigationDrawerItem(
                    label = { Text("የተወደዱ (Favorites)", fontWeight = FontWeight.Medium) },
                    icon = { Icon(Icons.Default.Bookmark, contentDescription = null) },
                    badge = {
                        if (favoritesList.isNotEmpty()) {
                            Badge(containerColor = EthiopianGold.copy(alpha = 0.25f), contentColor = Color(0xFFB45309)) {
                                Text("${favoritesList.size}")
                            }
                        }
                    },
                    selected = currentTab == MainNavTab.FAVORITES && !isAdminScreenOpen,
                    onClick = {
                        onSelectTab(MainNavTab.FAVORITES)
                        onCloseDrawer()
                    },
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = LightGreenBackground,
                        selectedIconColor = PrimaryGreen,
                        selectedTextColor = PrimaryGreen,
                        unselectedTextColor = Color.Black
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.padding(vertical = 2.dp).testTag("drawer_item_favorites")
                )

                // 5. User Dashboard
                NavigationDrawerItem(
                    label = { Text("ዳሽቦርድ (User Dashboard)", fontWeight = FontWeight.Medium) },
                    icon = { Icon(Icons.Default.Dashboard, contentDescription = null) },
                    selected = currentTab == MainNavTab.DASHBOARD && !isAdminScreenOpen,
                    onClick = {
                        onSelectTab(MainNavTab.DASHBOARD)
                        onCloseDrawer()
                    },
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = LightGreenBackground,
                        selectedIconColor = PrimaryGreen,
                        selectedTextColor = PrimaryGreen,
                        unselectedTextColor = Color.Black
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.padding(vertical = 2.dp).testTag("drawer_item_user_dashboard")
                )

                // 6. Admin Dashboard - CRITICAL: Visible ONLY to verified Admin (kedretech@gmail.com)
                if (isAdmin) {
                    Spacer(modifier = Modifier.height(4.dp))
                    NavigationDrawerItem(
                        label = {
                            Text(
                                "የአስተዳደር ዳሽቦርድ (Admin)",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF15803D)
                            )
                        },
                        icon = {
                            Icon(
                                Icons.Default.AdminPanelSettings,
                                contentDescription = "Admin Dashboard",
                                tint = Color(0xFF15803D)
                            )
                        },
                        badge = {
                            Badge(
                                containerColor = Color(0xFFFEF3C7),
                                contentColor = Color(0xFF92400E)
                            ) {
                                Text("አስተዳዳሪ ብቻ", fontWeight = FontWeight.Bold)
                            }
                        },
                        selected = isAdminScreenOpen,
                        onClick = {
                            onOpenAdminScreen()
                            onCloseDrawer()
                        },
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = Color(0xFFDCFCE7),
                            selectedIconColor = PrimaryGreen,
                            selectedTextColor = PrimaryGreen,
                            unselectedTextColor = Color(0xFF15803D)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.padding(vertical = 2.dp).testTag("drawer_item_admin_dashboard")
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = BorderGray)
            Spacer(modifier = Modifier.height(12.dp))

            // Kedre AI Engine Info Card
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = LightGreenBackground,
                    border = BorderStroke(1.dp, PrimaryGreen.copy(alpha = 0.25f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(PureWhite),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = PrimaryGreen,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Kedre AI Engine",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryGreen
                            )
                            Text(
                                text = "በየቀኑ 5:00 ጥዋት 10 አዳዲስ ምክሮችን ያዘጋጃል",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.DarkGray,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Font Size Quick Switcher
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.FormatSize,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "የፅሁፍ መጠን",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Black
                        )
                    }

                    Row {
                        OutlinedButton(
                            onClick = { viewModel.decreaseFontScale() },
                            modifier = Modifier.height(32.dp).testTag("drawer_decrease_font"),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp)
                        ) {
                            Text("A-", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Button(
                            onClick = { viewModel.increaseFontScale() },
                            modifier = Modifier.height(32.dp).testTag("drawer_increase_font"),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp)
                        ) {
                            Text("A+", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(16.dp))

            // Footer
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "የዕለት ምክር • Daily Advice v1.0",
                    style = MaterialTheme.typography.labelSmall,
                    color = GrayTextSecondary,
                    fontSize = 11.sp
                )
                Text(
                    text = "ኢትዮጵያ • Powered by Kedre AI",
                    style = MaterialTheme.typography.labelSmall,
                    color = PrimaryGreen,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 10.sp
                )
            }
        }
    }
}
