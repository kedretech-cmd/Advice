package com.example.data.firebase

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class FirestoreUser(
    @DocumentId val uid: String = "",
    val fullName: String = "",
    val email: String = "",
    val photoURL: String = "",
    val role: String = "user", // "user", "admin", "super_admin"
    val status: String = "active", // "active", "suspended"
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class FirestoreAdvice(
    @DocumentId val id: String = "",
    val title: String = "",
    val advice: String = "",
    val explanation: String = "",
    val dailyAction: String = "",
    val category: String = "",
    val categoryKey: String = "",
    val imageUrl: String = "",
    val imagePrompt: String = "",
    val publicationDate: String = "", // YYYY-MM-DD
    val published: Boolean = true,
    val createdBy: String = "system",
    val reactionCount: Long = 0L,
    val commentCount: Long = 0L,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class FirestoreReaction(
    @DocumentId val id: String = "",
    val adviceId: String = "",
    val userId: String = "",
    val reactionType: String = "like", // "like", "love", "helpful", "inspirational"
    @ServerTimestamp val createdAt: Timestamp? = null
)

@IgnoreExtraProperties
data class FirestoreComment(
    @DocumentId val id: String = "",
    val adviceId: String = "",
    val userId: String = "",
    val userName: String = "",
    val text: String = "",
    val status: String = "visible", // "visible", "hidden"
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class FirestoreFavorite(
    @DocumentId val id: String = "",
    val adviceId: String = "",
    val userId: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null
)
