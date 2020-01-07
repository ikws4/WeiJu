package io.ikws4.weiju.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_infos")
data class AppInfo(
    @PrimaryKey val pkgName: String,
    val name: String,
    val iconPath: String,
    val isSystemApp: Boolean,
    val isSelect: Boolean = false,
    val isDelete: Boolean = false
)