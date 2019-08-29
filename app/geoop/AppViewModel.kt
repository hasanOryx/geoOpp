package com.oryx.geoop

import android.app.Application
import android.content.Context
import androidx.annotation.WorkerThread
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class AppViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: AppRepository
    val allAssignments: LiveData<List<Assignment>>
    val openedAssignments: LiveData<List<Assignment>>
    val completedAssignments: LiveData<List<Assignment>>
    private val assignmentsByIds = MutableLiveData<List<Assignment>>()

    init {
        val assignmentDao = AppDatabase.getDatabase(application, viewModelScope).assignmentDao()
        repository = AppRepository(assignmentDao)
        allAssignments = repository.allAssignments
        openedAssignments = repository.openedAssignments
        completedAssignments = repository.completedAssignments
    }

    fun insert(assignment: Assignment) = viewModelScope.launch(Dispatchers.IO) {
        println("User inserted: $assignment")
        repository.insert(assignment)
    }

    fun update(context: Context, id: Int, status: String) = viewModelScope.launch(Dispatchers.IO) {
        // println("User inserted: $user")
        repository.update(context, id, status)
    }

    fun updateStatusNoteById(context: Context, id: Int, statusNote: String) = viewModelScope.launch(Dispatchers.IO) {
        // println("User inserted: $user")
        repository.updateStatusNoteById(context, id, statusNote)
    }

    fun updateLastUpdateById(context: Context, id: Int, lastUpdate: String) = viewModelScope.launch(Dispatchers.IO) {
        // println("User inserted: $user")
        repository.updateLastUpdateById(context, id, lastUpdate)
    }

    @WorkerThread
    fun loadAllByIds(userIds: IntArray) = viewModelScope.launch(Dispatchers.IO) {
        assignmentsByIds.postValue(repository.loadAllByIds(userIds))
    }

    fun getAssignmentsById(): MutableLiveData<List<Assignment>> {
        return assignmentsByIds
    }
}
