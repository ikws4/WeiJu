package io.ikws4.weiju.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ikws4.weiju.data.TranslationInfo
import io.ikws4.weiju.data.TranslationInfoRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class TranslationInfoViewModel(private val translationInfoRepository: TranslationInfoRepository, pkgName: String) : ViewModel() {
    val translationInfos = translationInfoRepository.getByPkgName(pkgName)

    @ExperimentalCoroutinesApi
    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }

    fun update(info: TranslationInfo) = viewModelScope.launch {
        translationInfoRepository.update(info)
    }
}