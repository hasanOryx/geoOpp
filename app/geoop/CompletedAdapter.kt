package com.oryx.geoop

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.recycler_completed.view.*

class CompletedAdapter(
    private val context: Context,
    assignmentsList: MutableList<Assignment>
) :
    RecyclerView.Adapter<CompletedAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.recycler_completed, parent, false))
    }

    override fun getItemCount(): Int {
      //  return assignmentsList.filter { it.status == "Completed" }.size
         return assignmentsCompletedList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.apply {
            taskName.apply {
                text = assignmentsCompletedList[position].task
                //   setOnClickListener {
                //       Toast.makeText(context, chaptersList[position], Toast.LENGTH_LONG).show()
                //   }
            }
            taskDate.text = assignmentsCompletedList[position].datePicker
            taskTime.text = assignmentsCompletedList[position].timePicker
            taskStatus.text = assignmentsCompletedList[position].status
            taskCompletion.text = assignmentsCompletedList[position].lastUpdate
        }

        holder.itemView.setOnClickListener {
            Toast.makeText(context, position.toString(), Toast.LENGTH_LONG).show()
            val intent = Intent(context, AnotherActivity::class.java)
            intent.putExtra("text", assignmentsCompletedList[position].details)

            intent.putExtra("lon", assignmentsCompletedList[position].lon.toString())
            intent.putExtra("lat", assignmentsCompletedList[position].lat.toString())
            intent.putExtra("task_id", assignmentsCompletedList[position].tid.toString())
            intent.putExtra("status", assignmentsCompletedList[position].status)
            intent.putExtra("statusNote", assignmentsCompletedList[position].statusNotes)
            intent.putExtra("mobile", assignmentsCompletedList[position].mobile)
            intent.putExtra("customer", assignmentsCompletedList[position].customer)

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
        val taskCompletion: TextView = view.completionDate
    }
}