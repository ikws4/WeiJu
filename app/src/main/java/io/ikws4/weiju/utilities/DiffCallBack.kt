package io.ikws4.weiju.utilities

import androidx.recyclerview.widget.DiffUtil
import io.ikws4.weiju.data.AppInfo
import io.ikws4.weiju.data.TranslationInfo

class AppInfoDiffCallBack : DiffUtil.ItemCallback<AppInfo>() {
    override fun areItemsTheSame(oldItem: AppInfo, newItem: AppInfo): Boolean =
        oldItem.pkgName == newItem.pkgName

    override fun areContentsTheSame(oldItem: AppInfo, newItem: AppInfo): Boolean =
        oldItem == oldItem
}

class TranslationInfoDiffCallBack : DiffUtil.ItemCallback<TranslationInfo>() {
    override fun areItemsTheSame(oldItem: TranslationInfo, newItem: TranslationInfo): Boolean =
        oldItem.query == newItem.query

    override fun areContentsTheSame(oldItem: TranslationInfo, newItem: TranslationInfo): Boolean =
        oldItem == newItem
}