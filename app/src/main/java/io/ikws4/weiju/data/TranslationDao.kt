package io.ikws4.weiju.data

import android.database.Cursor
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface TranslationDao {
    @Query("SELECT * FROM translation_info WHERE pkgName = :pkgName")
    fun getByPkgName(pkgName: String): LiveData<List<TranslationInfo>> // 查找特定包名的翻译数据，用于翻译编辑器

    @Query("SELECT result FROM translation_info WHERE `query` = :query AND pkgName = :pkgName AND `from` = :from AND `to` = :to")
    fun get(query: String, pkgName: String, from: String, to: String): Cursor

    @Insert
    fun insert(info: TranslationInfo): Long

    @Update
    fun update(info: TranslationInfo) // 更新翻译结果（修正）
}