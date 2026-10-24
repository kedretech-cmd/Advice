package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.firebase.FirestoreAdvice
import com.example.data.firebase.FirestoreComment
import com.example.data.firebase.FirestoreReaction
import com.example.data.firebase.FirestoreRepository
import com.example.data.firebase.FirestoreUser
import com.example.data.local.AppDatabase
import com.example.data.model.DailyAdviceEntity
import com.example.data.remote.GeminiClient
import com.example.data.repository.AdviceRepository
import com.example.util.EthiopianDateHelper
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface GenerationUiState {
    object Idle : GenerationUiState
    data class Loading(val message: String) : GenerationUiState
    data class Success(val count: Int, val message: String) : GenerationUiState
    data class Error(val error: String) : GenerationUiState
}

class AdviceViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getInstance(application)
    private val localRepository = AdviceRepository(database.adviceDao())
    val firestoreRepository = FirestoreRepository(application)

    val todayEthiopianDate: String = EthiopianDateHelper.getTodayEthiopianString()
    val todayIsoDate: String = EthiopianDateHelper.getTodayIsoDate()

    val isGeminiKeyReady: Boolean = GeminiClient.isApiKeyConfigured()

    // Auth State
    private val _currentUser = MutableStateFlow<FirebaseUser?>(Firebase.auth.currentUser)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser.asStateFlow()

    private val _userProfile = MutableStateFlow<FirestoreUser?>(null)
    val userProfile: StateFlow<FirestoreUser?> = _userProfile.asStateFlow()

    // Filter states
    private val _selectedCategoryKey = MutableStateFlow<String?>(null)
    val selectedCategoryKey: StateFlow<String?> = _selectedCategoryKey.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Text scale for Amharic readability
    private val _fontScale = MutableStateFlow(1.05f)
    val fontScale: StateFlow<Float> = _fontScale.asStateFlow()

    // Generation state
    private val _generationState = MutableStateFlow<GenerationUiState>(GenerationUiState.Idle)
    val generationState: StateFlow<GenerationUiState> = _generationState.asStateFlow()

    // Raw today's advice from database
    val todayAdviceList: StateFlow<List<DailyAdviceEntity>> = localRepository.getTodayAdvice()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Favorites
    val favoritesList: StateFlow<List<DailyAdviceEntity>> = localRepository.getFavorites()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Filtered today's advice based on category & search query
    val filteredTodayAdvice: StateFlow<List<DailyAdviceEntity>> = combine(
        todayAdviceList,
        _selectedCategoryKey,
        _searchQuery
    ) { list, catKey, query ->
        list.filter { item ->
            val matchesCategory = catKey == null || item.categoryKey.equals(catKey, ignoreCase = true)
            val matchesQuery = query.isBlank() ||
                item.title.contains(query, ignoreCase = true) ||
                item.advice.contains(query, ignoreCase = true) ||
                item.dailyAction.contains(query, ignoreCase = true) ||
                item.category.contains(query, ignoreCase = true)
            matchesCategory && matchesQuery
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // All advice for search tab
    val allAdviceList: StateFlow<List<DailyAdviceEntity>> = localRepository.getAllAdvice()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // All users for Admin panel
    val allUsersList: StateFlow<List<FirestoreUser>> = firestoreRepository.getAllUsersFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Active reactions cache: adviceId -> Map(reactionType -> count)
    private val _reactionsMap = MutableStateFlow<Map<String, Map<String, Int>>>(emptyMap())
    val reactionsMap: StateFlow<Map<String, Map<String, Int>>> = _reactionsMap.asStateFlow()

    // User's own reactions: adviceId -> reactionType
    private val _userReactions = MutableStateFlow<Map<String, String>>(emptyMap())
    val userReactions: StateFlow<Map<String, String>> = _userReactions.asStateFlow()

    // Comments cache: adviceId -> List<FirestoreComment>
    private val _commentsMap = MutableStateFlow<Map<String, List<FirestoreComment>>>(emptyMap())
    val commentsMap: StateFlow<Map<String, List<FirestoreComment>>> = _commentsMap.asStateFlow()

    private val authStateListener = FirebaseAuth.AuthStateListener { auth ->
        _currentUser.value = auth.currentUser
        auth.currentUser?.let { user ->
            viewModelScope.launch {
                try {
                    val profile = firestoreRepository.ensureUserProfile(user)
                    _userProfile.value = profile
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } ?: run {
            _userProfile.value = null
        }
    }

    init {
        Firebase.auth.addAuthStateListener(authStateListener)
        checkAndInitializeTodayAdvice()
    }

    override fun onCleared() {
        super.onCleared()
        Firebase.auth.removeAuthStateListener(authStateListener)
    }

    val isAdmin: Boolean
        get() {
            val email = currentUser.value?.email?.lowercase()
            return email == "kedretech@gmail.com" || userProfile.value?.role == "admin" || userProfile.value?.role == "super_admin"
        }

    val isSuperAdmin: Boolean
        get() {
            val email = currentUser.value?.email?.lowercase()
            return email == "kedretech@gmail.com" || userProfile.value?.role == "super_admin"
        }

    fun checkAndInitializeTodayAdvice() {
        viewModelScope.launch {
            _generationState.value = GenerationUiState.Loading("የዕለቱን ምክሮች በማዘጋጀት ላይ...")
            val result = localRepository.ensureTodayAdviceAvailable()
            if (result.isSuccess) {
                val list = result.getOrNull().orEmpty()
                _generationState.value = GenerationUiState.Success(
                    list.size,
                    "የዛሬ 10 ምክሮች በተሳካ ሁኔታ ተዘጋጅተዋል!"
                )
                // Sync to Firestore in background
                syncAdviceToFirestore(list)
            } else {
                _generationState.value = GenerationUiState.Error(
                    result.exceptionOrNull()?.localizedMessage ?: "ያልታወቀ ስህተት አጋጥሟል"
                )
            }
        }
    }

    private fun syncAdviceToFirestore(list: List<DailyAdviceEntity>) {
        viewModelScope.launch {
            try {
                val firestoreList = list.map {
                    FirestoreAdvice(
                        id = it.id,
                        title = it.title,
                        advice = it.advice,
                        explanation = it.explanation,
                        dailyAction = it.dailyAction,
                        category = it.category,
                        categoryKey = it.categoryKey,
                        imageUrl = it.imageUrl ?: "",
                        imagePrompt = it.imagePrompt,
                        publicationDate = it.publicationDate,
                        published = true
                    )
                }
                firestoreRepository.saveAdviceList(firestoreList)
            } catch (e: Exception) {
                // Non-blocking sync error
                e.printStackTrace()
            }
        }
    }

    fun generateNewAdviceWithGemini(customTopic: String? = null) {
        viewModelScope.launch {
            _generationState.value = GenerationUiState.Loading(
                if (customTopic.isNullOrBlank()) "አዲስ 10 የዕለት ምክሮችን ከGemini AI በማፍለቅ ላይ..."
                else "በርዕስ '$customTopic' ላይ ምክር በማፍለቅ ላይ..."
            )
            val result = localRepository.forceGenerateTodayAdvice(customTopic)
            if (result.isSuccess) {
                val list = result.getOrNull().orEmpty()
                _generationState.value = GenerationUiState.Success(
                    list.size,
                    "አዳዲስ 10 ምክሮች በተሳካ ሁኔታ ተፈጥረዋል!"
                )
                syncAdviceToFirestore(list)
            } else {
                _generationState.value = GenerationUiState.Error(
                    result.exceptionOrNull()?.localizedMessage ?: "የምክር ማመንጨት አልተሳካም። እባክዎ እንደገና ይሞክሩ።"
                )
            }
        }
    }

    // Reaction handling with optimistic UI
    fun toggleReaction(adviceId: String, reactionType: String) {
        val uid = currentUser.value?.uid ?: return
        val currentReaction = _userReactions.value[adviceId]

        viewModelScope.launch {
            try {
                if (currentReaction == reactionType) {
                    // Remove reaction
                    _userReactions.value = _userReactions.value - adviceId
                    updateReactionCountLocally(adviceId, reactionType, -1)
                    firestoreRepository.removeReaction(adviceId, uid)
                } else {
                    // Add or switch reaction
                    if (currentReaction != null) {
                        updateReactionCountLocally(adviceId, currentReaction, -1)
                    }
                    _userReactions.value = _userReactions.value + (adviceId to reactionType)
                    updateReactionCountLocally(adviceId, reactionType, 1)
                    firestoreRepository.setReaction(adviceId, uid, reactionType)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun updateReactionCountLocally(adviceId: String, reactionType: String, delta: Int) {
        val currentMap = _reactionsMap.value[adviceId] ?: emptyMap()
        val currentCount = currentMap[reactionType] ?: 0
        val newCount = (currentCount + delta).coerceAtLeast(0)
        val updatedMap = currentMap + (reactionType to newCount)
        _reactionsMap.value = _reactionsMap.value + (adviceId to updatedMap)
    }

    // Comment handling
    fun loadCommentsForAdvice(adviceId: String) {
        viewModelScope.launch {
            firestoreRepository.observeCommentsForAdvice(adviceId).collect { comments ->
                _commentsMap.value = _commentsMap.value + (adviceId to comments)
            }
        }
    }

    fun addComment(adviceId: String, text: String) {
        val user = currentUser.value ?: return
        val userName = userProfile.value?.fullName ?: user.displayName ?: "ተጠቃሚ"

        viewModelScope.launch {
            try {
                firestoreRepository.addComment(adviceId, user.uid, userName, text)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun deleteComment(adviceId: String, commentId: String) {
        viewModelScope.launch {
            try {
                firestoreRepository.deleteComment(commentId)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Admin user management
    fun updateUserRole(uid: String, newRole: String) {
        viewModelScope.launch {
            firestoreRepository.updateUserRole(uid, newRole)
        }
    }

    fun toggleUserSuspension(user: FirestoreUser) {
        val newStatus = if (user.status == "active") "suspended" else "active"
        viewModelScope.launch {
            firestoreRepository.updateUserStatus(user.uid, newStatus)
        }
    }

    fun setCategoryFilter(categoryKey: String?) {
        _selectedCategoryKey.value = categoryKey
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleFavorite(id: String, currentStatus: Boolean) {
        viewModelScope.launch {
            localRepository.toggleFavorite(id, currentStatus)
            currentUser.value?.uid?.let { uid ->
                if (!currentStatus) {
                    firestoreRepository.addFavorite(id, uid)
                } else {
                    firestoreRepository.removeFavorite(id, uid)
                }
            }
        }
    }

    fun toggleActionCompleted(id: String, currentStatus: Boolean) {
        viewModelScope.launch {
            localRepository.toggleActionCompleted(id, currentStatus)
        }
    }

    fun increaseFontScale() {
        if (_fontScale.value < 1.4f) {
            _fontScale.value = (_fontScale.value + 0.1f).coerceAtMost(1.4f)
        }
    }

    fun decreaseFontScale() {
        if (_fontScale.value > 0.85f) {
            _fontScale.value = (_fontScale.value - 0.1f).coerceAtLeast(0.85f)
        }
    }

    fun resetGenerationState() {
        _generationState.value = GenerationUiState.Idle
    }

    fun updateAdvice(advice: DailyAdviceEntity) {
        viewModelScope.launch {
            localRepository.updateAdvice(advice)
            try {
                firestoreRepository.updateAdvice(
                    FirestoreAdvice(
                        id = advice.id,
                        title = advice.title,
                        advice = advice.advice,
                        explanation = advice.explanation,
                        dailyAction = advice.dailyAction,
                        category = advice.category,
                        categoryKey = advice.categoryKey,
                        publicationDate = advice.publicationDate,
                        published = true
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun deleteAdvice(advice: DailyAdviceEntity) {
        viewModelScope.launch {
            localRepository.deleteAdvice(advice)
            try {
                firestoreRepository.deleteAdvice(advice.id)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
