package com.fastflow.app.presentation.community

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fastflow.app.domain.model.*
import com.fastflow.app.domain.repository.CommunityFeedFilter
import com.fastflow.app.domain.repository.CommunityRepository
import com.fastflow.app.domain.usecase.stats.GetUserStatsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommunityViewModel @Inject constructor(
    private val communityRepository: CommunityRepository,
    private val getUserStatsUseCase: GetUserStatsUseCase
) : ViewModel() {

    private val _filter = MutableStateFlow(CommunityFeedFilter.ALL)
    private val _group = MutableStateFlow<CommunityGroup?>(null)
    private val _uiState = MutableStateFlow(CommunityUiState())
    val uiState: StateFlow<CommunityUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch { communityRepository.ensureSeeded() }

        viewModelScope.launch {
            communityRepository.observeProfile().collect { profile ->
                _uiState.update { it.copy(profile = profile) }
            }
        }

        viewModelScope.launch {
            combine(_filter, _group) { filter, group -> filter to group }
                .flatMapLatest { (filter, _) ->
                    communityRepository.observeFeed(filter)
                }
                .collect { posts ->
                    val group = _group.value
                    val filtered = if (group == null) posts else posts.filter { it.group == group }
                    _uiState.update { it.copy(posts = filtered, isLoading = false) }
                }
        }
    }

    fun setFilter(filter: CommunityFeedFilter) {
        _filter.value = filter
    }

    fun setGroup(group: CommunityGroup?) {
        _group.value = group
    }

    fun showComposeDialog() {
        _uiState.update { it.copy(showComposeDialog = true) }
    }

    fun hideComposeDialog() {
        _uiState.update { it.copy(showComposeDialog = false) }
    }

    fun showProfileDialog() {
        _uiState.update { it.copy(showProfileDialog = true) }
    }

    fun hideProfileDialog() {
        _uiState.update { it.copy(showProfileDialog = false) }
    }

    fun saveProfile(displayName: String, shareAnonymously: Boolean) {
        viewModelScope.launch {
            communityRepository.updateProfile(displayName, shareAnonymously)
            hideProfileDialog()
        }
    }

    fun publishPost(type: CommunityPostType, group: CommunityGroup, content: String) {
        viewModelScope.launch {
            communityRepository.createPost(type, group, content)
                .onSuccess {
                    hideComposeDialog()
                    _uiState.update { it.copy(error = null) }
                }
                .onFailure { e -> _uiState.update { it.copy(error = e.message) } }
        }
    }

    fun shareProgress(group: CommunityGroup = CommunityGroup.GENERAL) {
        viewModelScope.launch {
            val stats = getUserStatsUseCase().first()
            communityRepository.shareProgress(stats, group)
                .onSuccess { _uiState.update { it.copy(error = null) } }
                .onFailure { e -> _uiState.update { it.copy(error = e.message) } }
        }
    }

    fun hidePost(postId: Int) {
        viewModelScope.launch {
            communityRepository.hidePost(postId)
        }
    }

    fun reportPost(postId: Int) {
        viewModelScope.launch {
            communityRepository.reportPost(postId)
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}

data class CommunityUiState(
    val profile: CommunityProfile = CommunityProfile("", "", false, false),
    val posts: List<CommunityPost> = emptyList(),
    val isLoading: Boolean = true,
    val showComposeDialog: Boolean = false,
    val showProfileDialog: Boolean = false,
    val error: String? = null
)
