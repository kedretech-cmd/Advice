# የዕለት ምክር | AI-Powered Amharic Daily Advice App
> **Daily Wisdom • Daily Growth (ዕለታዊ ጥበብ • ዕለታዊ ዕድገት)**

A modern, production-grade Android application developed in Kotlin and Jetpack Compose that automatically delivers 10 inspiring, educational, and practical pieces of daily advice in the Amharic language (አማርኛ), powered by Google Gemini AI and persistent local database caching.

---

## 🌟 Key Features

1. **10 Daily Unique Amharic Advice Posts (የዕለቱ 10 ምክሮች)**:
   - Automatically prepared for each day covering 10 distinct life wisdom domains:
     - **ተነሳሽነት (Motivation)**
     - **ትምህርትና እውቀት (Education & Learning)**
     - **ንግድና ሥራ ፈጠራ (Business & Entrepreneurship)**
     - **ቴክኖሎጂና ዲጂታል ክህሎት (Technology & Digital)**
     - **የግል ዕድገት (Personal Development)**
     - **ማህበራዊ ግንኙነት (Relationships & Communication)**
     - **የሕይወት ጥበብ (Life Wisdom & Philosophy)**
     - **የጊዜ አጠቃቀም (Time Management & Productivity)**
     - **ጤናማ ልማዶች (Healthy Habits & Wellness)**
     - **የፋይናንስ አስተዳደር (Financial Responsibility)**
   - Each post contains:
     - Engaging Amharic title
     - 50–100 Amharic words of actionable wisdom
     - Practical daily action step (**የዕለቱ ተግባር**)
     - Concise explanation (**ማብራሪያ**)
     - Category badge with custom color and icon
     - Ethiopian calendar date (ባሕረ ሐሳብ) + Gregorian date

2. **Google Gemini API Integration**:
   - Integrated with Gemini 3.5 Flash (`gemini-3.5-flash`) via structured JSON output.
   - Enforces natural, culturally respectful Amharic grammar and authentic Ethiopian context.
   - Secure API key management via AI Studio Secrets Panel (`BuildConfig.GEMINI_API_KEY`).
   - Built-in fallback so the app always functions seamlessly with pre-seeded wisdom even before an API key is entered.

3. **Persistent Local Database (Room)**:
   - All advice posts and publication dates are stored in SQLite via Room Database.
   - Prevents redundant regeneration per day.
   - Offline access: Read saved advice anywhere, anytime without internet connection.
   - Habit tracking: Check off completed daily action steps with saved progress.
   - Favorites collection: Bookmark favorite wisdom posts.

4. **Ethiopian Calendar Integration (የኢትዮጵያ ዘመን አቆጣጠር)**:
   - Native Julian Day Number (JDN) conversion algorithm to accurately compute the Ethiopian year, month (መስከረም እስከ ጳጉሜ), and day.
   - Example: *ጥቅምት 14 ቀን 2017 ዓ.ም*.

5. **Ethiopian Design System & Typography**:
   - Authentic Ethiopian Emerald Green (`#1B5E20`), Warm Gold (`#FFB300`), and clean cream backgrounds.
   - Readability controls: Font scaling toggle for easy reading of Ge'ez typography.
   - Adaptive launcher icon with the golden open book of wisdom and rising sun motif.
   - Full Light and Dark theme support.

6. **Social Sharing**:
   - One-tap share to Telegram, WhatsApp, and Facebook.
   - Copy Amharic text to clipboard.
   - Formatted social quote card preview.

7. **Admin & Custom Topic Generator**:
   - Manually trigger Gemini AI advice generation.
   - Enter custom prompts (e.g. advice tailored for university students, new entrepreneurs, or morning routines).
   - Edit or delete individual advice items.

---

## 🛠 Tech Stack

- **Platform:** Android (API 24+)
- **Language:** Kotlin
- **UI Framework:** Jetpack Compose with Material Design 3 (M3)
- **Architecture:** MVVM + Clean Architecture + Repository Pattern
- **Local Persistence:** AndroidX Room Database + Coroutines Flow
- **Networking:** OkHttp (60s timeouts) & Retrofit
- **AI Model:** Google Gemini 3.5 Flash (`gemini-3.5-flash`)
- **Testing:** Robolectric & JUnit 4

---

## 🚀 How to Run & Configure

1. **Configure Gemini API Key (Optional but Recommended)**:
   - Open the **Secrets panel in AI Studio**.
   - Add your Gemini API key under `GEMINI_API_KEY`.
   - The key is securely injected into `BuildConfig.GEMINI_API_KEY` at build time.
2. **Build and Test**:
   - Run compilation: `compile_applet`
   - Run unit/Robolectric tests: `gradle :app:testDebugUnitTest`
