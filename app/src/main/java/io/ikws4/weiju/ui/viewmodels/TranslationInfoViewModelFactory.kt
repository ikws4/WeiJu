package io.ikws4.weiju.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.ikws4.weiju.data.TranslationInfoRepository

@Suppress("UNCHECKED_CAST")
class TranslationInfoViewModelFactory(private val translationInfoRepository: TranslationInfoRepository, private val pkgName: String) :
    ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return TranslationInfoViewModel(translationInfoRepository,pkgName) as T
    }
}