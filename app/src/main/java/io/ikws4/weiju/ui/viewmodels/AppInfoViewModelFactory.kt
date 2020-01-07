package io.ikws4.weiju.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.ikws4.weiju.data.AppInfoRepository

class AppInfoViewModelFactory(private val appInfoRepository: AppInfoRepository) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return AppInfoViewModel(appInfoRepository) as T
    }
}