package com.example.android.cmsc436final.adapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.android.cmsc436final.ui.tourOverview.InfoTab
import com.example.android.cmsc436final.ui.tourOverview.MapTab

class TabsAdapter (fm: FragmentManager) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment = when (position) {
        0 -> MapTab.newInstance()
        //1 -> InfoTab.newInstance()
        else -> MapTab.newInstance()
    }

    override fun getPageTitle(position: Int): CharSequence = when (position) {
        0 -> "Map"
        1 -> "More Info"
        else -> ""
    }

    override fun getCount(): Int = 2
}
