package io.ikws4.weiju.utilities

import android.content.Context
import io.ikws4.weiju.data.AppDatabase
import io.ikws4.weiju.data.AppInfoRepository
import io.ikws4.weiju.data.TranslationInfoRepository
import io.ikws4.weiju.data.UserRepository
import io.ikws4.weiju.ui.viewmodels.AppInfoViewModelFactory
import io.ikws4.weiju.ui.viewmodels.TranslationInfoViewModelFactory
import io.ikws4.weiju.ui.viewmodels.UserViewModelFactory

object InjectorUtils {

    private fun getAppInfoRepository(context: Context): AppInfoRepository {
        val appInfoDao = AppDatabase.getInstance(context.applicationContext).appInfoDao()
        return AppInfoRepository.getInstance(appInfoDao)
    }

    private fun getTranslationRepository(context: Context): TranslationInfoRepository {
        val translationDao = AppDatabase.getInstance(context.applicationContext).translationInfoDao()
        return TranslationInfoRepository(translationDao)
    }

    private fun getUserRepository(context: Context): UserRepository {
        val userDao = AppDatabase.getInstance(context.applicationContext).userDao()
        return UserRepository.getInstance(userDao)
    }

    fun provideAppInfoViewModelFactory(context: Context): AppInfoViewModelFactory {
        val repository = getAppInfoRepository(context)
        return AppInfoViewModelFactory(repository)
    }

    fun provideTranslationInfoViewModelFactory(context: Context, pkgName: String): TranslationInfoViewModelFactory {
        val repository = getTranslationRepository(context)
        return TranslationInfoViewModelFactory(repository, pkgName)
    }

    fun provideUserViewModelFactory(context: Context): UserViewModelFactory {
        val repository = getUserRepository(context)
        return UserViewModelFactory(repository)
    }
}