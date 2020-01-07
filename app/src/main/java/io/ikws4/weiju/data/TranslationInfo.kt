package io.ikws4.weiju.data

import android.content.ContentValues
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "translation_info")
data class TranslationInfo(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val pkgName: String,
    val query: String,
    val from: String,
    val to: String,
    val result: String
) {
    companion object {
        fun fromContentValues(values: ContentValues) = TranslationInfo(
            pkgName = values.getAsString("pkgName"),
            query = values.getAsString("query"),
            from = values.getAsString("from"),
            to = values.getAsString("to"),
            result = values.getAsString("result")
        )
    }
}
