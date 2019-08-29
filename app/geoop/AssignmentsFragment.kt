package com.oryx.geoop

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_assignments.view.*

class AssignmentsFragment : Fragment() {
    var listener: OnFragmentInteractionListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val layout = inflater.inflate(R.layout.fragment_assignments, container, false)

        layoutManager = LinearLayoutManager(requireContext())
       // layoutManager = LinearLayoutManager(container!!.context) // activity as Context)
        recyclerView = layout.openList
        recyclerView.layoutManager = layoutManager
       // adapter = AppAdapter(container!!.context, assignmentsList)
        val adapter = AssignmentsAdapter(requireContext())
        recyclerView.adapter = adapter

    //    appViewModel = ViewModelProviders.of(this).get(AppViewModel::class.java)

     //   appViewModel.getAssignmentsById().observe(this, Observer { assignments ->
     //       printAll(assignments) // You can pass assignments object to your AssignmentsAdapter
     //   })

        appViewModel.openedAssignments.observe(this, Observer { assignments ->
            // Update the cached copy of the words in the adapter.
            assignments?.let { it ->
                println(it)
                assignmentsOpenedList.clear()
                adapter.notifyDataSetChanged()
                it.forEach {
                    val insertIndex = assignmentsOpenedList.size
                    assignmentsOpenedList.add(insertIndex, it)
                    adapter.notifyItemInserted(insertIndex)
                }
            }
        })
        return layout
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {
        @JvmStatic
        fun newInstance() = AssignmentsFragment()
    }

    fun printAll(strings: List<Assignment>) {
        for(s in strings) print("$s ")
        println()
    }
}