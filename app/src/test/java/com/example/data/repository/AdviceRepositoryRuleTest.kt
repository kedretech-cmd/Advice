package com.example.data.repository

import com.example.base.FirestoreEmulatorTestBase
import com.example.data.firebase.FirestoreAdvice
import com.example.data.firebase.FirestoreRepository
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import java.util.UUID

class AdviceRepositoryRuleTest : FirestoreEmulatorTestBase() {

    @Test
    fun unauthenticatedUser_canReadPublishedAdvice() = runBlocking {
        // Unauthenticated reading of published advice should succeed
        auth.signOut()
        val docId = "pub_${UUID.randomUUID()}"
        signInTestUser(ADMIN_EMAIL)
        val adminRepo = FirestoreRepository(firestore)
        adminRepo.saveAdviceList(
            listOf(
                FirestoreAdvice(
                    id = docId,
                    title = "የዛሬ ምክር",
                    advice = "ጠቃሚ የሕይወት ምክር እና እውቀት።",
                    dailyAction = "ተግባር ፈጽሙ",
                    category = "ተነሳሽነት",
                    categoryKey = "motivation",
                    publicationDate = "2026-10-24",
                    published = true
                )
            )
        )

        auth.signOut()
        val snapshot = withTimeout(DEFAULT_TIMEOUT_MS) {
            firestore.collection("advice").document(docId).get().await()
        }
        assertTrue(snapshot.exists())
        assertEquals("የዛሬ ምክር", snapshot.getString("title"))
    }

    @Test
    fun nonAdminUser_cannotCreateAdvice() = runBlocking {
        signInTestUser(USER_EMAIL)
        val userRepo = FirestoreRepository(firestore)
        try {
            withTimeout(DEFAULT_TIMEOUT_MS) {
                userRepo.saveAdviceList(
                    listOf(
                        FirestoreAdvice(
                            id = "unauth_${UUID.randomUUID()}",
                            title = "Unauth Advice",
                            advice = "Some advice",
                            dailyAction = "Action",
                            category = "ተነሳሽነት",
                            published = true,
                            publicationDate = "2026-10-24"
                        )
                    )
                )
            }
            fail("Expected PERMISSION_DENIED for non-admin advice creation")
        } catch (e: Exception) {
            val firestoreException = e.unwrapFirestoreException()
            assertNotNull(firestoreException)
            assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, firestoreException?.code)
        }
    }

    private companion object {
        const val ADMIN_EMAIL = "kedretech@gmail.com"
        const val USER_EMAIL = "user@test.com"
        const val DEFAULT_TIMEOUT_MS = 5000L
    }
}
