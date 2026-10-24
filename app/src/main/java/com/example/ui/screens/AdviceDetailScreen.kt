package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AdviceCategory
import com.example.data.model.DailyAdviceEntity
import com.example.ui.components.AuthDialog
import com.example.ui.components.CommentSection
import com.example.ui.components.ReactionBar
import com.example.ui.components.SocialShareDialog
import com.example.ui.theme.BorderGray
import com.example.ui.theme.GrayTextSecondary
import com.example.ui.theme.LightGreenBackground
import com.example.ui.theme.PrimaryGreen
import com.example.ui.theme.PureWhite
import com.example.ui.viewmodel.AdviceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdviceDetailScreen(
    item: DailyAdviceEntity,
    viewModel: AdviceViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler(onBack = onBack)

    val fontScale by viewModel.fontScale.collectAsState()
    val todayList by viewModel.todayAdviceList.collectAsState()
    val currentItem = todayList.firstOrNull { it.id == item.id } ?: item
    val category = AdviceCategory.fromKey(currentItem.categoryKey)

    val currentUser by viewModel.currentUser.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val userReactions by viewModel.userReactions.collectAsState()
    val reactionsMap by viewModel.reactionsMap.collectAsState()
    val commentsMap by viewModel.commentsMap.collectAsState()

    var showShareDialog by remember { mutableStateOf(false) }
    var showAuthDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(currentItem.id) {
        viewModel.loadCommentsForAdvice(currentItem.id)
    }

    if (showShareDialog) {
        SocialShareDialog(
            item = currentItem,
            onDismiss = { showShareDialog = false }
        )
    }

    if (showAuthDialog) {
        AuthDialog(
            userProfile = userProfile,
            onDismiss = { showAuthDialog = false },
            onAuthSuccess = { showAuthDialog = false }
        )
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("advice_detail_screen"),
        containerColor = PureWhite,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = category.amharicName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("detail_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.Black
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            if (fontScale >= 1.25f) viewModel.decreaseFontScale()
                            else viewModel.increaseFontScale()
                        },
                        modifier = Modifier.testTag("detail_font_scale_button")
                    ) {
                        Icon(Icons.Default.FormatSize, contentDescription = "Font size", tint = Color.Black)
                    }
                    IconButton(
                        onClick = { viewModel.toggleFavorite(currentItem.id, currentItem.isFavorite) },
                        modifier = Modifier.testTag("detail_favorite_button")
                    ) {
                        Icon(
                            imageVector = if (currentItem.isFavorite) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Favorite",
                            tint = if (currentItem.isFavorite) PrimaryGreen else Color.Black
                        )
                    }
                    IconButton(
                        onClick = { showShareDialog = true },
                        modifier = Modifier.testTag("detail_share_button")
                    ) {
                        Icon(Icons.Default.Share, contentDescription = "Share", tint = Color.Black)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PureWhite
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Hero Card in Pure White with Green Border
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("detail_hero_banner"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = PureWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                border = BorderStroke(1.dp, BorderGray)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(LightGreenBackground),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = category.icon,
                                    contentDescription = null,
                                    tint = PrimaryGreen,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = category.amharicName,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryGreen
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = LightGreenBackground,
                            border = BorderStroke(1.dp, PrimaryGreen.copy(alpha = 0.2f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CalendarToday,
                                    contentDescription = null,
                                    tint = PrimaryGreen,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = currentItem.ethiopianDate.ifBlank { currentItem.publicationDate },
                                    style = MaterialTheme.typography.labelSmall,
                                    color = PrimaryGreen,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = currentItem.title,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontSize = (22 * fontScale).sp,
                            lineHeight = (30 * fontScale).sp
                        ),
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Advice Body
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = PureWhite),
                border = BorderStroke(1.dp, BorderGray)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.FormatQuote,
                            contentDescription = null,
                            tint = PrimaryGreen,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "ሙሉ ምክር",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryGreen
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = currentItem.advice,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = (17 * fontScale).sp,
                            lineHeight = (28 * fontScale).sp
                        ),
                        color = Color.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Practical Daily Action Box
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("detail_action_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (currentItem.isActionCompleted) LightGreenBackground else PureWhite
                ),
                border = BorderStroke(
                    width = 1.dp,
                    color = if (currentItem.isActionCompleted) PrimaryGreen else BorderGray
                )
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.TaskAlt,
                                contentDescription = null,
                                tint = PrimaryGreen,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "የዕለቱ ተግባር (Action Step)",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryGreen
                            )
                        }

                        if (currentItem.isActionCompleted) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = PrimaryGreen
                            ) {
                                Text(
                                    text = "ተከናውኗል ✓",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = PureWhite,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = currentItem.dailyAction,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = (16 * fontScale).sp,
                            lineHeight = (24 * fontScale).sp
                        ),
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = { viewModel.toggleActionCompleted(currentItem.id, currentItem.isActionCompleted) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("detail_action_toggle_btn"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
                    ) {
                        Icon(
                            imageVector = if (currentItem.isActionCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (currentItem.isActionCompleted) "ተግባሩን እንደተጠናቀቀ ምልክት አድርገዋል" else "ተግባሩን አጠናቅቄያለሁ ብለው ምልክት ያድርጉ"
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Interactive Reaction Bar
            Text(
                text = "የእርስዎ ስሜት (Reactions)",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            ReactionBar(
                currentReaction = userReactions[currentItem.id],
                reactionCounts = reactionsMap[currentItem.id] ?: emptyMap(),
                onSelectReaction = { reactionType ->
                    if (currentUser == null) {
                        showAuthDialog = true
                    } else {
                        viewModel.toggleReaction(currentItem.id, reactionType)
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Explanation Section
            if (currentItem.explanation.isNotBlank()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = PureWhite),
                    border = BorderStroke(1.dp, BorderGray)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = PrimaryGreen,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "ማብራሪያ",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = currentItem.explanation,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = (15 * fontScale).sp,
                                lineHeight = (24 * fontScale).sp
                            ),
                            color = GrayTextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Social Sharing Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { showShareDialog = true },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("ምክሩን አጋራ")
                }

                OutlinedButton(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("የዕለት ምክር", "${currentItem.title}\n\n${currentItem.advice}\n\nየዕለቱ ተግባር: ${currentItem.dailyAction}")
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "ምክሩ ተገልብጧል!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, BorderGray)
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color.Black)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("ጽሑፉን ቅዳ", color = Color.Black)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Comments Section
            CommentSection(
                comments = commentsMap[currentItem.id] ?: emptyList(),
                currentUserId = currentUser?.uid,
                isAdmin = viewModel.isAdmin,
                onAddComment = { text -> viewModel.addComment(currentItem.id, text) },
                onDeleteComment = { commentId -> viewModel.deleteComment(currentItem.id, commentId) },
                onRequireLogin = { showAuthDialog = true }
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
