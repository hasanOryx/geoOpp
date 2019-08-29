package com.oryx.geoop

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.recycler_assignments.view.*
import android.content.Intent



var trxList: ArrayList<SQLModel> = ArrayList() // ArrayList<SQLModel> = ArrayList()

class AssignmentsAdapter(private val context: Context) :
    RecyclerView.Adapter<AssignmentsAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.recycler_assignments, parent, false))
    }

    override fun getItemCount(): Int {
       // return assignmentsList.filterNot { it.status == "Completed" }.size
        return assignmentsOpenedList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.apply {
            taskName.apply {
                text = assignmentsOpenedList[position].task
             //   setOnClickListener {
             //       Toast.makeText(context, chaptersList[position], Toast.LENGTH_LONG).show()
             //   }
            }
            taskDate.text = assignmentsOpenedList[position].datePicker
            taskTime.text = assignmentsOpenedList[position].timePicker
            taskStatus.text = assignmentsOpenedList[position].status
            latestUpdate.text = assignmentsOpenedList[position].lastUpdate
        }

        holder.itemView.setOnClickListener {
           // Toast.makeText(context, position.toString(), Toast.LENGTH_LONG).show()
            val intent = Intent(context, AnotherActivity::class.java)
            intent.putExtra("text", assignmentsOpenedList[position].details)

            intent.putExtra("lon", assignmentsOpenedList[position].lon.toString())
            intent.putExtra("lat", assignmentsOpenedList[position].lat.toString())
            intent.putExtra("task_id", assignmentsOpenedList[position].tid.toString())
            intent.putExtra("status", assignmentsOpenedList[position].status)
            intent.putExtra("statusNote", assignmentsOpenedList[position].statusNotes)
            intent.putExtra("mobile", assignmentsOpenedList[position].mobile.toString())
            intent.putExtra("customer", assignmentsOpenedList[position].customer)

            context.startActivity(intent)
        }

 //       holder.checkBox.setOnCheckedChangeListener { _, b ->
 //           Toast.makeText(context, b.toString(), Toast.LENGTH_LONG).show()
 //           appViewModel.update(context, position+1, "Dana and Karam and Yara")
 //       }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val taskName: TextView = view.taskName
        val taskDate: TextView = view.taskDate
        val taskTime: TextView = view.taskTime
        val taskStatus: TextView = view.taskStatus
        val latestUpdate: TextView = view.latestUpdate
    }
}