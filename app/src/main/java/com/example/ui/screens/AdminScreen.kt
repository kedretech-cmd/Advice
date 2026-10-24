package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PersonRemove
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.firebase.FirestoreUser
import com.example.data.model.AdviceCategory
import com.example.data.model.DailyAdviceEntity
import com.example.ui.theme.BorderGray
import com.example.ui.theme.GrayTextMuted
import com.example.ui.theme.GrayTextSecondary
import com.example.ui.theme.LightGreenBackground
import com.example.ui.theme.PrimaryGreen
import com.example.ui.theme.PureWhite
import com.example.ui.viewmodel.AdviceViewModel
import com.example.ui.viewmodel.GenerationUiState

@Composable
fun AdminScreen(
    viewModel: AdviceViewModel,
    modifier: Modifier = Modifier
) {
    val todayList by viewModel.todayAdviceList.collectAsState()
    val allUsers by viewModel.allUsersList.collectAsState()
    val generationState by viewModel.generationState.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }
    var customTopic by remember { mutableStateOf("") }
    var editingItem by remember { mutableStateOf<DailyAdviceEntity?>(null) }

    editingItem?.let { item ->
        EditAdviceDialog(
            item = item,
            onDismiss = { editingItem = null },
            onSave = { updated ->
                viewModel.updateAdvice(updated)
                editingItem = null
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(PureWhite)
            .testTag("admin_screen")
    ) {
        // Top Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = PureWhite,
            shadowElevation = 0.5.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(LightGreenBackground),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = null,
                            tint = PrimaryGreen,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "የአስተዳደር ዳሽቦርድ (Admin Dashboard)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Text(
                            text = "AI ማመንጫ • የይዘት አስተዳደር • ተጠቃሚዎች",
                            style = MaterialTheme.typography.bodySmall,
                            color = GrayTextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = PureWhite,
                    contentColor = PrimaryGreen,
                    indicator = { tabPositions ->
                        Box(
                            Modifier
                                .tabIndicatorOffset(tabPositions[selectedTab])
                                .height(3.dp)
                                .background(PrimaryGreen)
                        )
                    }
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("ዳሽቦርድ & AI", fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("ምክሮች (${todayList.size})", fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = { Text("ተጠቃሚዎች (${allUsers.size})", fontWeight = FontWeight.Bold) }
                    )
                }
            }
        }

        when (selectedTab) {
            0 -> {
                // Dashboard & AI Generation
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Quick Stats Cards
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            StatCard(
                                title = "የዛሬ ምክሮች",
                                value = "${todayList.size}",
                                subtitle = "የታተሙ",
                                modifier = Modifier.weight(1f)
                            )
                            StatCard(
                                title = "ተጠቃሚዎች",
                                value = "${allUsers.size.coerceAtLeast(1)}",
                                subtitle = "የተመዘገቡ",
                                modifier = Modifier.weight(1f)
                            )
                            StatCard(
                                title = "AI ሁኔታ",
                                value = if (viewModel.isGeminiKeyReady) "ዝግጁ" else "ነባሪ",
                                subtitle = "gemini-3.5",
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    // AI API Status Card
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = PureWhite),
                            border = BorderStroke(1.dp, BorderGray)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Key,
                                            contentDescription = null,
                                            tint = PrimaryGreen,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Google Gemini 3.5 Flash",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.Black
                                        )
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = LightGreenBackground
                                    ) {
                                        Text(
                                            text = if (viewModel.isGeminiKeyReady) "የተገናኘ ✓" else "በአማራጭ ዝግጁ",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = PrimaryGreen,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = "ሞዴል: gemini-3.5-flash • ቋንቋ: አማርኛ (10 የተለያዩ ዘርፎች)",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = GrayTextSecondary
                                )
                            }
                        }
                    }

                    // AI Generation Box
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = PureWhite),
                            border = BorderStroke(1.dp, BorderGray)
                        ) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = PrimaryGreen,
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "የዕለት 10 ምክሮች ማመንጫ",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = "የዛሬን 10 ምክሮች በGemini AI ያፍልቁ ወይም ለየት ያለ የትኩረት ርዕስ ያስገቡ።",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = GrayTextSecondary
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                OutlinedTextField(
                                    value = customTopic,
                                    onValueChange = { customTopic = it },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("custom_topic_input"),
                                    label = { Text("ልዩ የትኩረት ርዕስ (አማራጭ)") },
                                    placeholder = { Text("ለምሳሌ: ለወጣቶች የንግድ ሥራ፣ የፈተና ዝግጅት...") },
                                    singleLine = true,
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = PrimaryGreen,
                                        unfocusedBorderColor = BorderGray,
                                        focusedContainerColor = PureWhite,
                                        unfocusedContainerColor = PureWhite
                                    )
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                val isLoading = generationState is GenerationUiState.Loading

                                Button(
                                    onClick = {
                                        viewModel.generateNewAdviceWithGemini(
                                            customTopic.ifBlank { null }
                                        )
                                    },
                                    enabled = !isLoading,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp)
                                        .testTag("generate_advice_button"),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
                                ) {
                                    if (isLoading) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(18.dp),
                                            strokeWidth = 2.dp,
                                            color = PureWhite
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("ምክሮችን በማመንጨት ላይ...", color = PureWhite)
                                    } else {
                                        Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp), tint = PureWhite)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("አዲስ 10 ምክሮች አፍልቅ (Generate)", fontWeight = FontWeight.Bold, color = PureWhite)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            1 -> {
                // Advice Management
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(todayList, key = { "admin_adv_${it.id}" }) { item ->
                        val category = AdviceCategory.fromKey(item.categoryKey)

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = PureWhite),
                            border = BorderStroke(1.dp, BorderGray)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(LightGreenBackground),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = category.icon,
                                        contentDescription = null,
                                        tint = PrimaryGreen,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = item.title,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black
                                    )
                                    Text(
                                        text = "${category.amharicName} • ${item.dailyAction}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = GrayTextSecondary,
                                        maxLines = 1
                                    )
                                }

                                IconButton(onClick = { editingItem = item }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = PrimaryGreen)
                                }

                                IconButton(onClick = { viewModel.deleteAdvice(item) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                                }
                            }
                        }
                    }
                }
            }

            2 -> {
                // User Management
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(allUsers, key = { "user_${it.uid}" }) { user ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = PureWhite),
                            border = BorderStroke(1.dp, BorderGray)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(LightGreenBackground),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = (user.fullName.firstOrNull() ?: 'U').uppercase(),
                                        fontWeight = FontWeight.Bold,
                                        color = PrimaryGreen
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = user.fullName,
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.Black
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = if (user.role == "super_admin") LightGreenBackground else BorderGray
                                        ) {
                                            Text(
                                                text = user.role,
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = if (user.role == "super_admin") PrimaryGreen else GrayTextSecondary,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }

                                    Text(
                                        text = user.email,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = GrayTextSecondary
                                    )
                                }

                                if (user.role != "super_admin") {
                                    IconButton(
                                        onClick = {
                                            val newRole = if (user.role == "admin") "user" else "admin"
                                            viewModel.updateUserRole(user.uid, newRole)
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Shield,
                                            contentDescription = "Toggle admin role",
                                            tint = if (user.role == "admin") PrimaryGreen else GrayTextMuted
                                        )
                                    }

                                    IconButton(
                                        onClick = { viewModel.toggleUserSuspension(user) }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.PersonRemove,
                                            contentDescription = "Toggle suspension",
                                            tint = if (user.status == "suspended") Color.Red else GrayTextMuted
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = PureWhite),
        border = BorderStroke(1.dp, BorderGray)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = title, style = MaterialTheme.typography.labelSmall, color = GrayTextSecondary)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = PrimaryGreen)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = subtitle, style = MaterialTheme.typography.labelSmall, color = GrayTextMuted)
        }
    }
}

@Composable
fun EditAdviceDialog(
    item: DailyAdviceEntity,
    onDismiss: () -> Unit,
    onSave: (DailyAdviceEntity) -> Unit
) {
    var title by remember { mutableStateOf(item.title) }
    var advice by remember { mutableStateOf(item.advice) }
    var explanation by remember { mutableStateOf(item.explanation) }
    var dailyAction by remember { mutableStateOf(item.dailyAction) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = PureWhite,
            modifier = Modifier.fillMaxWidth(),
            border = BorderStroke(1.dp, BorderGray)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "ምክሩን ማስተካከል (Edit Advice)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("አርዕስት") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = advice,
                    onValueChange = { advice = it },
                    label = { Text("ሙሉ ምክር") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 4,
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = dailyAction,
                    onValueChange = { dailyAction = it },
                    label = { Text("የዕለቱ ተግባር") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = explanation,
                    onValueChange = { explanation = it },
                    label = { Text("ማብራሪያ") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(onClick = onDismiss) {
                        Text("ይቅር", color = Color.Black)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            onSave(
                                item.copy(
                                    title = title,
                                    advice = advice,
                                    explanation = explanation,
                                    dailyAction = dailyAction
                                )
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
                    ) {
                        Text("አስቀምጥ", color = PureWhite)
                    }
                }
            }
        }
    }
}
