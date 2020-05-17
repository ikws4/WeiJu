package io.ikws4.weiju.data

import android.content.Context
import android.widget.EditText
import androidx.annotation.Keep
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.commonsware.cwac.saferoom.SafeHelperFactory
import io.ikws4.weiju.utilities.DATABASE_NAME
import io.ikws4.weiju.utilities.WEIJU_PKG_NAME
import io.ikws4.weiju.worker.SeedAppDatabaseWorker

@Database(entities = [AppInfo::class, TranslationInfo::class, User::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun appInfoDao(): AppInfoDao

    abstract fun translationInfoDao(): TranslationDao

    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE translation_info (id INTEGER NOT NULL, pkgName TEXT NOT NULL, `query` TEXT NOT NULL, `from` TEXT NOT NULL, `to` TEXT NOT NULL, result TEXT NOT NULL, PRIMARY KEY(id))")
                database.execSQL("CREATE TABLE user (freeSogouApiAmount INTEGER NOT NULL)")
            }
        }

        @Keep
        private fun buildDatabase(context: Context): AppDatabase {
            val editText = EditText(context)
            editText.setText(WEIJU_PKG_NAME)
            val safeHelperFactory = SafeHelperFactory.fromUser(editText.text)

            return Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
                .openHelperFactory(safeHelperFactory)
                .addMigrations(MIGRATION_1_2)
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        val result = OneTimeWorkRequestBuilder<SeedAppDatabaseWorker>()
                            .addTag(SeedAppDatabaseWorker.TAG)
                            .build()
                        WorkManager.getInstance(context).enqueue(result)
                    }
                }).build()
        }
    }
}

