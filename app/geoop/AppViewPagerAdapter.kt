package com.oryx.geoop

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter


class AppViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> AssignmentsFragment()
            1 -> CompletedFragment()
            else -> InfoFragment()
        }
    }


    override fun getItemCount(): Int {
        return 3
    }
}