package io.ikws4.weiju.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ikws4.weiju.data.UserRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class UserViewModel(private val userRepository: UserRepository) : ViewModel() {
    val user = userRepository.getUser()

    @ExperimentalCoroutinesApi
    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }

    fun increaseFreeSogouApiAmount(amount: Int) = viewModelScope.launch {
        userRepository.increaseFreeSogouApiAmount(amount)
    }
}