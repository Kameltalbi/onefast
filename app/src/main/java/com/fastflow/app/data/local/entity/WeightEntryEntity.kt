package com.fastflow.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.fastflow.app.domain.model.WeightEntry
import com.fastflow.app.domain.model.WeightUnit

@Entity(tableName = "weight_entries")
data class WeightEntryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val timestamp: Long,
    val weight: Float,
    val unit: String = WeightUnit.KG.name,
    val waistCm: Float? = null
) {
    fun toDomain(): WeightEntry {
        return WeightEntry(
            id = id,
            timestamp = timestamp,
            weight = weight,
            unit = WeightUnit.valueOf(unit),
            waistCm = waistCm
        )
    }

    companion object {
        fun fromDomain(entry: WeightEntry): WeightEntryEntity {
            return WeightEntryEntity(
                id = entry.id,
                timestamp = entry.timestamp,
                weight = entry.weight,
                unit = entry.unit.name,
                waistCm = entry.waistCm
            )
        }
    }
}
