package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.InitialSeedData
import com.example.util.EthiopianDateHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read app_name string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("የዕለት ምክር", appName)
    }

    @Test
    fun `verify initial seed data provides exactly 10 posts`() {
        val seeds = InitialSeedData.getInitialTenAdvice("2026-10-24")
        assertEquals(10, seeds.size)
        seeds.forEach { advice ->
            assertTrue(advice.title.isNotBlank())
            assertTrue(advice.advice.isNotBlank())
            assertTrue(advice.dailyAction.isNotBlank())
        }
    }

    @Test
    fun `verify ethiopian date conversion produces valid format`() {
        val ethDate = EthiopianDateHelper.getTodayEthiopianString()
        assertTrue(ethDate.contains("ዓ.ም"))
    }
}
