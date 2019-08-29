package com.oryx.geoop

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.oryx.geoop.AppDatabase.Companion.INSTANCE
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AppDatabaseCallback(private val scope: CoroutineScope) : RoomDatabase.Callback() {
    override fun onOpen(db: SupportSQLiteDatabase) {
        super.onOpen(db)
        INSTANCE?.let { database ->
            scope.launch(Dispatchers.IO) {
                populateDatabase(database.assignmentDao())
            }
        }
    }

    private suspend fun populateDatabase(assignmentDao: AssignmentDao) {
        assignmentDao.getAll()
        /*  assignmentDao.deleteAll()

          var word = Word("Hello")
          wordDao.insert(word)
          word = Word("World!")
          wordDao.insert(word)
          */
    }

}
