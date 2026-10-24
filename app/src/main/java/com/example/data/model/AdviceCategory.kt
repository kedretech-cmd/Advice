package com.example.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Timer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

enum class AdviceCategory(
    val key: String,
    val amharicName: String,
    val englishName: String,
    val icon: ImageVector,
    val color: Color,
    val gradientEnd: Color
) {
    MOTIVATION(
        key = "motivation",
        amharicName = "ተነሳሽነት",
        englishName = "Motivation",
        icon = Icons.Default.Bolt,
        color = Color(0xFFE65100), // Vibrant amber/orange
        gradientEnd = Color(0xFFFF9800)
    ),
    EDUCATION(
        key = "education",
        amharicName = "ትምህርትና እውቀት",
        englishName = "Education",
        icon = Icons.Default.School,
        color = Color(0xFF1565C0), // Deep blue
        gradientEnd = Color(0xFF42A5F5)
    ),
    BUSINESS(
        key = "business",
        amharicName = "ንግድና ሥራ ፈጠራ",
        englishName = "Business",
        icon = Icons.AutoMirrored.Filled.TrendingUp,
        color = Color(0xFF1B5E20), // Ethiopian Forest green
        gradientEnd = Color(0xFF43A047)
    ),
    TECHNOLOGY(
        key = "technology",
        amharicName = "ቴክኖሎጂና ዲጂታል",
        englishName = "Technology",
        icon = Icons.Default.Devices,
        color = Color(0xFF00838F), // Cyan / Teal
        gradientEnd = Color(0xFF26C6DA)
    ),
    PERSONAL_DEV(
        key = "personal_dev",
        amharicName = "የግል ዕድገት",
        englishName = "Self Development",
        icon = Icons.Default.Psychology,
        color = Color(0xFF6A1B9A), // Royal purple
        gradientEnd = Color(0xFFAB47BC)
    ),
    RELATIONSHIPS(
        key = "relationships",
        amharicName = "ማህበራዊ ግንኙነት",
        englishName = "Relationships",
        icon = Icons.Default.People,
        color = Color(0xFFAD1457), // Rose
        gradientEnd = Color(0xFFEC407A)
    ),
    LIFE_WISDOM(
        key = "life_wisdom",
        amharicName = "የሕይወት ጥበብ",
        englishName = "Life Wisdom",
        icon = Icons.Default.Lightbulb,
        color = Color(0xFFF57F17), // Golden sun
        gradientEnd = Color(0xFFFFD54F)
    ),
    PRODUCTIVITY(
        key = "productivity",
        amharicName = "የጊዜ አጠቃቀም",
        englishName = "Productivity",
        icon = Icons.Default.Timer,
        color = Color(0xFF0277BD), // Sky blue
        gradientEnd = Color(0xFF29B6F6)
    ),
    HEALTHY_HABITS(
        key = "healthy_habits",
        amharicName = "ጤናማ ልማዶች",
        englishName = "Healthy Habits",
        icon = Icons.Default.Favorite,
        color = Color(0xFFC62828), // Crimson health
        gradientEnd = Color(0xFFEF5350)
    ),
    FINANCIAL(
        key = "financial",
        amharicName = "የፋይናንስ አስተዳደር",
        englishName = "Financial Discipline",
        icon = Icons.Default.AccountBalanceWallet,
        color = Color(0xFF2E7D32), // Emerald
        gradientEnd = Color(0xFF66BB6A)
    );

    companion object {
        fun fromKey(key: String): AdviceCategory {
            return entries.firstOrNull {
                it.key.equals(key, ignoreCase = true) ||
                it.amharicName.equals(key, ignoreCase = true) ||
                it.englishName.equals(key, ignoreCase = true)
            } ?: MOTIVATION
        }
    }
}
