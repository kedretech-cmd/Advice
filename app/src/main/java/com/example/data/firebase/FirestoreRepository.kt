package com.example.data.firebase

import android.content.Context
import com.example.R
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class FirestoreRepository(private val db: FirebaseFirestore) {

    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private val auth = Firebase.auth

    val currentUserId: String?
        get() = auth.currentUser?.uid

    val currentUserEmail: String?
        get() = auth.currentUser?.email

    fun isCurrentUserAdmin(): Boolean {
        val email = currentUserEmail?.lowercase()
        return email == "kedretech@gmail.com"
    }

    // 1. User Profiles
    suspend fun ensureUserProfile(firebaseUser: FirebaseUser): FirestoreUser {
        val docRef = db.collection("users").document(firebaseUser.uid)
        val snapshot = docRef.get().await()

        val role = if (firebaseUser.email.equals("kedretech@gmail.com", ignoreCase = true)) {
            "super_admin"
        } else {
            "user"
        }

        if (snapshot.exists()) {
            val existing = snapshot.toObject(FirestoreUser::class.java)!!
            if (role == "super_admin" && existing.role != "super_admin") {
                docRef.update("role", "super_admin").await()
                return existing.copy(role = "super_admin")
            }
            return existing
        }

        val newUser = FirestoreUser(
            uid = firebaseUser.uid,
            fullName = firebaseUser.displayName ?: firebaseUser.email?.substringBefore("@") ?: "ተጠቃሚ",
            email = firebaseUser.email ?: "",
            photoURL = firebaseUser.photoUrl?.toString() ?: "",
            role = role,
            status = "active"
        )

        val payload = mapOf(
            "uid" to newUser.uid,
            "fullName" to newUser.fullName,
            "email" to newUser.email,
            "photoURL" to newUser.photoURL,
            "role" to newUser.role,
            "status" to newUser.status,
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )

        docRef.set(payload).await()
        return newUser
    }

    suspend fun getUserProfile(uid: String): FirestoreUser? {
        val snapshot = db.collection("users").document(uid).get().await()
        return snapshot.toObject(FirestoreUser::class.java)
    }

    fun getAllUsersFlow(): Flow<List<FirestoreUser>> {
        return db.collection("users")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .snapshots()
            .map { snap -> snap.toObjects(FirestoreUser::class.java) }
            .catch { emit(emptyList()) }
    }

    suspend fun updateUserRole(uid: String, newRole: String) {
        db.collection("users").document(uid).update(
            "role", newRole,
            "updatedAt", FieldValue.serverTimestamp()
        ).await()
    }

    suspend fun updateUserStatus(uid: String, newStatus: String) {
        db.collection("users").document(uid).update(
            "status", newStatus,
            "updatedAt", FieldValue.serverTimestamp()
        ).await()
    }

    // 2. Advice Posts
    fun getAdviceForDateFlow(date: String): Flow<List<FirestoreAdvice>> {
        return db.collection("advice")
            .whereEqualTo("publicationDate", date)
            .whereEqualTo("published", true)
            .snapshots()
            .map { snap -> snap.toObjects(FirestoreAdvice::class.java) }
            .catch { emit(emptyList()) }
    }

    fun getAllAdviceFlow(): Flow<List<FirestoreAdvice>> {
        return db.collection("advice")
            .orderBy("publicationDate", Query.Direction.DESCENDING)
            .snapshots()
            .map { snap -> snap.toObjects(FirestoreAdvice::class.java) }
            .catch { emit(emptyList()) }
    }

    suspend fun saveAdviceList(adviceList: List<FirestoreAdvice>) {
        val batch = db.batch()
        for (item in adviceList) {
            val docRef = db.collection("advice").document(item.id)
            val payload = mapOf(
                "id" to item.id,
                "title" to item.title,
                "advice" to item.advice,
                "explanation" to item.explanation,
                "dailyAction" to item.dailyAction,
                "category" to item.category,
                "categoryKey" to item.categoryKey,
                "imageUrl" to item.imageUrl,
                "imagePrompt" to item.imagePrompt,
                "publicationDate" to item.publicationDate,
                "published" to item.published,
                "createdBy" to (auth.currentUser?.uid ?: "system"),
                "reactionCount" to item.reactionCount,
                "commentCount" to item.commentCount,
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            batch.set(docRef, payload)
        }
        batch.commit().await()
    }

    suspend fun updateAdvice(advice: FirestoreAdvice) {
        val docRef = db.collection("advice").document(advice.id)
        docRef.update(
            "title", advice.title,
            "advice", advice.advice,
            "explanation", advice.explanation,
            "dailyAction", advice.dailyAction,
            "category", advice.category,
            "published", advice.published,
            "updatedAt", FieldValue.serverTimestamp()
        ).await()
    }

    suspend fun deleteAdvice(adviceId: String) {
        db.collection("advice").document(adviceId).delete().await()
    }

    // 3. Reactions (Like, Love, Helpful, Inspirational)
    fun observeReactionsForAdvice(adviceId: String): Flow<List<FirestoreReaction>> {
        return db.collection("reactions")
            .whereEqualTo("adviceId", adviceId)
            .snapshots()
            .map { snap -> snap.toObjects(FirestoreReaction::class.java) }
            .catch { emit(emptyList()) }
    }

    suspend fun setReaction(adviceId: String, userId: String, reactionType: String) {
        val docId = "${adviceId}_${userId}"
        val docRef = db.collection("reactions").document(docId)
        val payload = mapOf(
            "id" to docId,
            "adviceId" to adviceId,
            "userId" to userId,
            "reactionType" to reactionType,
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )
        docRef.set(payload).await()
    }

    suspend fun removeReaction(adviceId: String, userId: String) {
        val docId = "${adviceId}_${userId}"
        db.collection("reactions").document(docId).delete().await()
    }

    // 4. Comments
    fun observeCommentsForAdvice(adviceId: String): Flow<List<FirestoreComment>> {
        return db.collection("comments")
            .whereEqualTo("adviceId", adviceId)
            .orderBy("createdAt", Query.Direction.ASCENDING)
            .snapshots()
            .map { snap -> snap.toObjects(FirestoreComment::class.java) }
            .catch { emit(emptyList()) }
    }

    suspend fun addComment(adviceId: String, userId: String, userName: String, text: String) {
        val commentRef = db.collection("comments").document()
        val payload = mapOf(
            "id" to commentRef.id,
            "adviceId" to adviceId,
            "userId" to userId,
            "userName" to userName,
            "text" to text,
            "status" to "visible",
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )
        commentRef.set(payload).await()
    }

    suspend fun deleteComment(commentId: String) {
        db.collection("comments").document(commentId).delete().await()
    }

    // 5. Favorites
    fun observeUserFavorites(userId: String): Flow<List<FirestoreFavorite>> {
        return db.collection("favorites")
            .whereEqualTo("userId", userId)
            .snapshots()
            .map { snap -> snap.toObjects(FirestoreFavorite::class.java) }
            .catch { emit(emptyList()) }
    }

    suspend fun addFavorite(adviceId: String, userId: String) {
        val docId = "${adviceId}_${userId}"
        val payload = mapOf(
            "id" to docId,
            "adviceId" to adviceId,
            "userId" to userId,
            "createdAt" to FieldValue.serverTimestamp()
        )
        db.collection("favorites").document(docId).set(payload).await()
    }

    suspend fun removeFavorite(adviceId: String, userId: String) {
        val docId = "${adviceId}_${userId}"
        db.collection("favorites").document(docId).delete().await()
    }
}
