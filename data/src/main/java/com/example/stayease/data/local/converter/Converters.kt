package com.example.stayease.data.local.converter

import androidx.room.TypeConverter
import com.example.stayease.domain.model.Review
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

object Converters {

    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val reviewsListType = Types.newParameterizedType(List::class.java, Review::class.java)
    private val reviewsJsonAdapter = moshi.adapter<List<Review>>(reviewsListType)

    @TypeConverter
    @JvmStatic
    fun fromStringList(value: String?): List<String> {
        return value?.split(',')
            ?.map { it.trim() }
            ?.filter { it.isNotEmpty() }
            ?: emptyList()
    }

    @TypeConverter
    @JvmStatic
    fun toStringList(value: List<String>?): String {
        return value?.joinToString(",") ?: ""
    }

    @TypeConverter
    @JvmStatic
    fun fromReviewList(json: String?): List<Review> {
        return if (json == null) emptyList() else reviewsJsonAdapter.fromJson(json) ?: emptyList()
    }

    @TypeConverter
    @JvmStatic
    fun toReviewList(reviews: List<Review>?): String {
        return reviewsJsonAdapter.toJson(reviews ?: emptyList())
    }
}
