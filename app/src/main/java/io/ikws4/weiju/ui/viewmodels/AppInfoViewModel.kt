package io.ikws4.weiju.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ikws4.weiju.data.AppInfo
import io.ikws4.weiju.data.AppInfoRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class AppInfoViewModel(private val appInfoRepository: AppInfoRepository) : ViewModel() {
    val appInfos = appInfoRepository.getAll()

    @ExperimentalCoroutinesApi
    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }

    /**
     * 标记为是否被选择 isSelect
     */
    fun update(appInfo: AppInfo) = viewModelScope.launch {
        appInfoRepository.update(appInfo)
    }
}