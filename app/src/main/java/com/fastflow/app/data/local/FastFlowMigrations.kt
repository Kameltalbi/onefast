package com.fastflow.app.data.local

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object FastFlowMigrations {
    val MIGRATION_6_7 = object : Migration(6, 7) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `water_entries` (
                    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `timestamp` INTEGER NOT NULL,
                    `amountMl` INTEGER NOT NULL
                )
                """.trimIndent()
            )
        }
    }
}
