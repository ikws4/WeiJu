package io.ikws4.weiju.data

import androidx.annotation.Keep

@Keep
data class VariableModel(
    val model: String,
    val brand: String,
    val device: String,
    val productName: String,
    val androidRelease: String,
    val longitude: String,
    val latitude: String,
    val imei: String,
    val imsi: String
)