package com.oryx.geoop

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query


@Dao
interface AssignmentDao {

    @Query("SELECT * FROM assignment LIMIT 1")
    fun getAnyUser(): List<Assignment>

    @Query("SELECT * FROM assignment")
    fun getAll(): LiveData<List<Assignment>>

    @Query("SELECT * FROM assignment WHERE status != :status ORDER BY datePicker, timePicker ASC")
    fun getOpened(status: String = "Completed"): LiveData<List<Assignment>>

    @Query("SELECT * FROM assignment WHERE status = :status ORDER BY lastUpdate DESC")
    fun getCompleted(status: String = "Completed"): LiveData<List<Assignment>>

//    @Query("SELECT * FROM assignment")
//    fun getCompleted(): LiveData<List<Assignment>>

    @Query("SELECT * FROM assignment WHERE tid IN (:userIds)")
    fun loadAllByIds(userIds: IntArray): List<Assignment>

    @Query("SELECT * FROM assignment WHERE task LIKE :task AND " +
            "status LIKE :status LIMIT 1")
    fun findByName(task: String, status: String): Assignment

    @Query("UPDATE assignment SET status = :status WHERE tid = :id")
    fun updateById(id: Int, status: String)

    @Query("UPDATE assignment SET statusNotes = :statusNote WHERE tid = :id")
    fun updateStatusNoteById(id: Int, statusNote: String)

    @Query("UPDATE assignment SET lastUpdate = :lastUpdate WHERE tid = :id")
    fun updateLastUpdateById(id: Int, lastUpdate: String)

    @Insert
    fun insertAll(vararg assignment: Assignment)

    @Delete
    fun delete(assignment: Assignment)
}
