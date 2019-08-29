package com.oryx.geoop

import android.content.Context
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData

class AppRepository(private val assignmentDao: AssignmentDao) {

    val allAssignments: LiveData<List<Assignment>> = assignmentDao.getAll()
    val openedAssignments: LiveData<List<Assignment>> = assignmentDao.getOpened()
    val completedAssignments: LiveData<List<Assignment>> = assignmentDao.getCompleted()

    @WorkerThread
    fun insert(assignment: Assignment) {
        assignmentDao.insertAll(assignment)
    }

    @WorkerThread
    fun loadAllByIds(userIds: IntArray): List<Assignment> {
        return assignmentDao.loadAllByIds(userIds)
    }

    @WorkerThread
    fun updateLastUpdateById(context: Context, id: Int, lastUpdate: String) {
        assignmentDao.updateLastUpdateById(id, lastUpdate)
    }

    @WorkerThread
    fun updateStatusNoteById(context: Context, id: Int, statusNote: String) {
        assignmentDao.updateStatusNoteById(id, statusNote)
    }
    @WorkerThread
    fun update(context: Context, id: Int, status: String) {
        assignmentDao.updateById(id, status)
        //assignmentsList.filter { it.id == id }.forEach { it.status = status }
        //println(assignmentsList)
      //  assignmentsList.replaceAll {  } .filter { it.id == id}   .forEach(status=status)
/*
      //  var x = Timestamp(System.currentTimeMillis())
      //  var z = DateTimeFormatter.ISO_INSTANT.format(Instant.now())
        var trx_id = DateTimeFormatter
            .ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS")
            .withZone(ZoneOffset.UTC)
            .format(Instant.now())
        var sql = "sql it as"

        val trx = SQLModel(trx_id, sql, false)
        trxList.add(trx)

        val jsonStr =
            "{\"trx_id\": \"${trx.trx_id}\", \"sql\": \"${trx.sql}\", \"updated\": ${trx.updated}}"
        val jObj = JSONObject(jsonStr)

        if (mWebSocketClient.isOpen) {
            mWebSocketClient.send(jObj.toString())
        } else {
            try {
                client.connectWebSocket(context)
                SystemClock.sleep(500)
                mWebSocketClient.send(jObj.toString())
            } catch (e : Exception){
                Looper.prepare() // to be able to make toast
                Toast.makeText(context, "not connected $e", Toast.LENGTH_LONG).show()
                Looper.loop()
            }
        }
*/
       // val jsonStr = "{\"action\": \"update\",\"unique_id\": $id, \"name\": \"$name\"}}"
       // val jObj = JSONObject(jsonStr)
       // mWebSocketClient.send(jObj.toString())
    }
}
