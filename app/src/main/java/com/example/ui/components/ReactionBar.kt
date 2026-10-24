package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BorderGray
import com.example.ui.theme.LightGreenBackground
import com.example.ui.theme.PrimaryGreen
import com.example.ui.theme.PureWhite

data class ReactionOption(
    val type: String,
    val emoji: String,
    val labelAm: String
)

val REACTION_OPTIONS = listOf(
    ReactionOption("like", "👍", "ላይክ"),
    ReactionOption("love", "❤️", "ፍቅር"),
    ReactionOption("helpful", "💡", "አስተማሪ"),
    ReactionOption("inspirational", "✨", "አነቃቂ")
)

@Composable
fun ReactionBar(
    currentReaction: String?,
    reactionCounts: Map<String, Int>,
    onSelectReaction: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .testTag("reaction_bar"),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        REACTION_OPTIONS.forEach { opt ->
            val isSelected = currentReaction == opt.type
            val count = reactionCounts[opt.type] ?: 0

            val backgroundColor by animateColorAsState(
                targetValue = if (isSelected) LightGreenBackground else PureWhite,
                animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                label = "reaction_bg"
            )

            val borderColor by animateColorAsState(
                targetValue = if (isSelected) PrimaryGreen else BorderGray,
                animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                label = "reaction_border"
            )

            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onSelectReaction(opt.type) }
                    .testTag("reaction_btn_${opt.type}"),
                shape = RoundedCornerShape(12.dp),
                color = backgroundColor,
                border = BorderStroke(1.dp, borderColor)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = opt.emoji,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (count > 0) "$count" else opt.labelAm,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) PrimaryGreen else Color.Black
                    )
                }
            }
        }
    }
}
