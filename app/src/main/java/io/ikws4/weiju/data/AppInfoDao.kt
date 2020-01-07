package io.ikws4.weiju.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface AppInfoDao {
    @Query("SELECT * FROM app_infos WHERE isDelete = 0 ORDER BY name")
    fun getAll(): LiveData<List<AppInfo>>

    @Query("SELECT * FROM app_infos WHERE pkgName = :pkgName")
    fun getByPkgName(pkgName: String):AppInfo?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(appInfo: AppInfo)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(appInfos: List<AppInfo>)

    @Update
    fun update(appInfo: AppInfo)

    @Query("UPDATE app_infos SET isDelete = 1 WHERE pkgName = :pkgName")
    fun remove(pkgName: String)
}