package com.example.android.cmsc436final.adapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.android.cmsc436final.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


class TabsCollectionFragment : Fragment() {
    // When requested, this adapter returns a TabsCollectionAdapterclass,
    // representing an object in the collection.
//    private lateinit var demoCollectionAdapter: DemoCollectionAdapter
    private lateinit var viewPager: ViewPager2

    // main view
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        return inflater.inflate(R.layout.fragment_tour_overview, container, false)
//    }

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        demoCollectionAdapter = DemoCollectionAdapter(this)
//        viewPager = view.findViewById(R.id.pager)
//        viewPager.adapter = demoCollectionAdapter

    // CREATES THE TAB TITLES
//        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//            val tabLayout = view.findViewById<TabLayout>(R.id.tab_layout)
//            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
////                tab.text = "OBJECT ${(position + 1)}"
//                if (position == 0) {
//                    tab.text = "MAP"
//                } else {
//                    tab.text = "MORE INFO"
//                }
//            }.attach()
//        }
//    }

    // ADAPTER
//    class DemoCollectionAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
//
//        override fun getItemCount(): Int = 100
//
//        override fun createFragment(position: Int): Fragment {
//            // Return a NEW fragment instance in createFragment(int)
//            val fragment = DemoObjectFragment()
//            fragment.arguments = Bundle().apply {
//                // Our object is just an integer :-P
//                putInt(ARG_OBJECT, position + 1)
//            }
//            return fragment
//        }
//    }
//
//    private const val ARG_OBJECT = "object"

// Instances of this class are fragments representing a single
//// object in our collection.
//class DemoObjectFragment : Fragment() {
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        arguments?.takeIf { it.containsKey(ARG_OBJECT) }?.apply {
//            if (getInt(ARG_OBJECT) == 0) {
//                return inflater.inflate(R.layout.tour_overview_map_tab, container, false)
//            } else {
//                return inflater.inflate(R.layout.tour_overview_info_tab, container, false)
//            }
//        }
//        return inflater.inflate(R.layout.tour_overview_map_tab, container, false)
//    }
//}

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        arguments?.takeIf { it.containsKey(ARG_OBJECT) }?.apply {
//            val textView: TextView = view.findViewById(android.R.id.text1)
//            textView.text = getInt(ARG_OBJECT).toString()
//        }
//    }
}