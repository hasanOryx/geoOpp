package com.oryx.geoop

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import kotlinx.coroutines.CoroutineScope

@Database(entities = [Assignment::class], version = 1)
//@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun assignmentDao(): AssignmentDao

    companion object {
        @Volatile
        var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context,
                        scope: CoroutineScope
        ): AppDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "database-name"
                ).addCallback(AppDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}