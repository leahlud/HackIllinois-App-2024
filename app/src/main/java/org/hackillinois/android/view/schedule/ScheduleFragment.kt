package org.hackillinois.android.view.schedule

import android.os.Bundle
import com.google.android.material.tabs.TabLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_schedule.view.*
import org.hackillinois.android.R
import org.hackillinois.android.viewmodel.ScheduleViewModel

class ScheduleFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_schedule, container, false)

        val sectionsPagerAdapter = SectionsPagerAdapter(childFragmentManager)

        view.scheduleContainer.adapter = sectionsPagerAdapter
        view.scheduleContainer.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(view.tabs))
        view.tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(view.scheduleContainer))

        val scheduleViewModel = ScheduleViewModel()

        val time = System.currentTimeMillis()

        view.scheduleContainer.currentItem = when {
            time < scheduleViewModel.fridayEnd -> 0
            time < scheduleViewModel.saturdayEnd -> 1
            time < scheduleViewModel.sundayEnd -> 2
            else -> 0
        }

        return view
    }

    inner class SectionsPagerAdapter constructor(fm: FragmentManager) : FragmentPagerAdapter(fm) {
        override fun getItem(position: Int) = DayFragment.newInstance(position)
        override fun getCount() = 3
    }
}