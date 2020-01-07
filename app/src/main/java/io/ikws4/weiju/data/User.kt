package io.ikws4.weiju.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class User(
    @PrimaryKey
    val freeSogouApiAmount: Int
)