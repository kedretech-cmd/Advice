package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AdviceCategory
import com.example.data.model.DailyAdviceEntity
import com.example.ui.theme.BorderGray
import com.example.ui.theme.GrayTextSecondary
import com.example.ui.theme.LightGreenBackground
import com.example.ui.theme.PrimaryGreen
import com.example.ui.theme.PureWhite

@Composable
fun AdviceCard(
    item: DailyAdviceEntity,
    fontScale: Float,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    onToggleAction: () -> Unit,
    onShare: () -> Unit,
    userReaction: String? = null,
    onQuickLike: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val category = AdviceCategory.fromKey(item.categoryKey)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("advice_card_${item.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = PureWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = BorderStroke(1.dp, BorderGray)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Category header and Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = LightGreenBackground,
                    border = BorderStroke(1.dp, PrimaryGreen.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(PrimaryGreen),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = category.icon,
                                contentDescription = null,
                                tint = PureWhite,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = category.amharicName,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryGreen
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Quick Like button
                    IconButton(
                        onClick = onQuickLike,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("quick_like_${item.id}")
                    ) {
                        Icon(
                            imageVector = if (userReaction != null) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "React to advice",
                            tint = if (userReaction != null) Color.Red else GrayTextSecondary
                        )
                    }

                    // Favorite button
                    IconButton(
                        onClick = onToggleFavorite,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("favorite_button_${item.id}")
                    ) {
                        Icon(
                            imageVector = if (item.isFavorite) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = if (item.isFavorite) "Remove from favorites" else "Add to favorites",
                            tint = if (item.isFavorite) PrimaryGreen else GrayTextSecondary
                        )
                    }

                    // Share button
                    IconButton(
                        onClick = onShare,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("share_button_${item.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share advice",
                            tint = GrayTextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Title in bold black
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = (18 * fontScale).sp
                ),
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Advice Body Preview in readable black/charcoal
            Text(
                text = item.advice,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = (15 * fontScale).sp,
                    lineHeight = (23 * fontScale).sp
                ),
                color = Color.Black.copy(alpha = 0.85f),
                maxLines = 4,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Practical Daily Action in Light Green Box with Border
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onToggleAction)
                    .testTag("action_toggle_${item.id}"),
                shape = RoundedCornerShape(12.dp),
                color = if (item.isActionCompleted) LightGreenBackground else PureWhite,
                border = BorderStroke(
                    width = 1.dp,
                    color = if (item.isActionCompleted) PrimaryGreen else BorderGray
                )
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (item.isActionCompleted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                        contentDescription = "Mark action completed",
                        tint = if (item.isActionCompleted) PrimaryGreen else GrayTextSecondary,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.TaskAlt,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = PrimaryGreen
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "የዕለቱ ተግባር",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryGreen
                            )
                            if (item.isActionCompleted) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "✓ ተከናውኗል",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryGreen
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = item.dailyAction,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = (13 * fontScale).sp
                            ),
                            fontWeight = FontWeight.Medium,
                            color = Color.Black,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}
