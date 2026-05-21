package com.fastflow.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fastflow.app.domain.model.WaterEntry

@Entity(tableName = "water_entries")
data class WaterEntryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val timestamp: Long,
    val amountMl: Int
) {
    fun toDomain(): WaterEntry = WaterEntry(id = id, timestamp = timestamp, amountMl = amountMl)

    companion object {
        fun fromDomain(entry: WaterEntry): WaterEntryEntity =
            WaterEntryEntity(id = entry.id, timestamp = entry.timestamp, amountMl = entry.amountMl)
    }
}
