package com.flowmate.data

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromLongList(value: List<Long>?): String? {
        return value?.joinToString(",")
    }

    @TypeConverter
    fun toLongList(value: String?): List<Long> {
        return value?.split(",")?.mapNotNull { it.toLongOrNull() } ?: emptyList()
    }
}
