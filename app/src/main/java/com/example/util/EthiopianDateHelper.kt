package com.example.util

import java.util.Calendar

/**
 * Helper to convert Gregorian calendar date to Ethiopian Calendar (ባሕረ ሐሳብ).
 * Ethiopian year has 12 months of 30 days each, plus Pagume of 5 or 6 days.
 */
object EthiopianDateHelper {

    private val AMHARIC_MONTHS = listOf(
        "መስከረም",  // 1. Meskerem
        "ጥቅምት",    // 2. Tikimt
        "ኅዳር",     // 3. Hidar
        "ታኅሣሥ",    // 4. Tahsas
        "ጥር",      // 5. Tir
        "የካቲት",    // 6. Yakatit
        "መጋቢት",    // 7. Megabit
        "ሚያዝያ",    // 8. Miyazya
        "ግንቦት",    // 9. Ginbot
        "ሰኔ",      // 10. Sene
        "ሐምሌ",     // 11. Hamle
        "ነሐሴ",     // 12. Nehase
        "ጳጉሜ"      // 13. Pagume
    )

    private val AMHARIC_DAYS = listOf(
        "እሑድ",    // Sunday
        "ሰኞ",     // Monday
        "ማክሰኞ",  // Tuesday
        "ረቡዕ",    // Wednesday
        "ሐሙስ",   // Thursday
        "ዓርብ",    // Friday
        "ቅዳሜ"    // Saturday
    )

    data class EthiopianDate(
        val year: Int,
        val month: Int, // 1 to 13
        val day: Int,
        val monthNameAmharic: String,
        val dayOfWeekAmharic: String
    ) {
        fun formatFullAmharic(): String {
            return "$dayOfWeekAmharic፣ $monthNameAmharic $day ቀን $year ዓ.ም"
        }

        fun formatShortAmharic(): String {
            return "$monthNameAmharic $day፣ $year ዓ.ም"
        }
    }

    /**
     * Converts a Gregorian Calendar instance to Ethiopian Date.
     */
    fun toEthiopianDate(calendar: Calendar = Calendar.getInstance()): EthiopianDate {
        val gy = calendar.get(Calendar.YEAR)
        val gm = calendar.get(Calendar.MONTH) + 1
        val gd = calendar.get(Calendar.DAY_OF_MONTH)
        val dow = calendar.get(Calendar.DAY_OF_WEEK) // 1 = Sunday, 7 = Saturday

        val amharicDayOfWeek = AMHARIC_DAYS[(dow - 1).coerceIn(0, 6)]

        // JDN algorithm for Gregorian to Ethiopian conversion
        val a = (14 - gm) / 12
        val y = gy + 4800 - a
        val m = gm + 12 * a - 3
        val jdn = gd + (153 * m + 2) / 5 + 365 * y + y / 4 - y / 100 + y / 400 - 32045

        // Ethiopian epoch in Julian Day Number is 1723856 (Aug 29, 8 AD)
        val r = (jdn - 1723856) % 1461
        val n = (r % 365) + 365 * (r / 1460)

        val ethYear = 4 * ((jdn - 1723856) / 1461) + (r / 365) - (r / 1460)
        val ethMonth = (n / 30) + 1
        val ethDay = (n % 30) + 1

        val monthIndex = (ethMonth - 1).coerceIn(0, AMHARIC_MONTHS.size - 1)
        val amharicMonthName = AMHARIC_MONTHS[monthIndex]

        return EthiopianDate(
            year = ethYear,
            month = ethMonth,
            day = ethDay,
            monthNameAmharic = amharicMonthName,
            dayOfWeekAmharic = amharicDayOfWeek
        )
    }

    /**
     * Returns today's formatted string: e.g. "ቅዳሜ፣ ጥቅምት 14 ቀን 2017 ዓ.ም"
     */
    fun getTodayEthiopianString(): String {
        return toEthiopianDate().formatFullAmharic()
    }

    /**
     * Today's ISO date string (YYYY-MM-DD) for database keying
     */
    fun getTodayIsoDate(): String {
        val cal = Calendar.getInstance()
        val y = cal.get(Calendar.YEAR)
        val m = cal.get(Calendar.MONTH) + 1
        val d = cal.get(Calendar.DAY_OF_MONTH)
        return "%04d-%02d-%02d".format(y, m, d)
    }
}
