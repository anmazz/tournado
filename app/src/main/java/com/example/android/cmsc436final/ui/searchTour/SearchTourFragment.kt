package com.example.android.cmsc436final.ui.searchTour

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.algolia.instantsearch.core.connection.ConnectionHandler
import com.algolia.instantsearch.helper.android.list.autoScrollToStart
import com.algolia.instantsearch.helper.android.searchbox.SearchBoxViewAppCompat
import com.algolia.instantsearch.helper.android.searchbox.connectView
import com.algolia.instantsearch.helper.android.stats.StatsTextView
import com.algolia.instantsearch.helper.stats.StatsPresenterImpl
import com.algolia.instantsearch.helper.stats.connectView
import com.example.android.cmsc436final.MainActivity
import com.example.android.cmsc436final.R
import kotlinx.android.synthetic.main.fragment_search_tour.*


class SearchTourFragment: Fragment() {

    private val connection = ConnectionHandler()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_search_tour, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewModel = ViewModelProviders.of(requireActivity())[SearchTourViewModel::class.java]
        val searchBoxView = SearchBoxViewAppCompat(searchView)
        val statsView = StatsTextView(stats)

        viewModel.tours.observe(this, Observer { hits -> viewModel.adapterTour.submitList(hits) })

        connection += viewModel.searchBox.connectView(searchBoxView)
        connection += viewModel.stats.connectView(statsView, StatsPresenterImpl())

        productList.let {
            it.itemAnimator = null
            it.adapter = viewModel.adapterTour
            it.layoutManager = LinearLayoutManager(requireContext())
            it.autoScrollToStart(viewModel.adapterTour)
        }

       // filters.setOnClickListener {
         //   (requireActivity() as MainActivity).showFacetFragment()
//        }
//        filters.setOnClickListener {
//            activity!!.supportFragmentManager
//                .beginTransaction()
//                .add(R.id.container, FacetFragment())
//                .addToBackStack("facet")
//                .commit()
//        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        connection.disconnect()
    }
}
//
//class SearchTourFragment : Fragment(), OnMapReadyCallback {
//    private lateinit var mSharedViewModel: SharedViewModel
//    private lateinit var mGoogleMap: GoogleMap
//    private lateinit var searchBar: EditText
//    private lateinit var listOfResults: ListView
//    private val db = FirebaseFirestore.getInstance()
//
//    //algoriastuff
//    val client = ClientSearch(ApplicationID("MLWVY1AHOC"), APIKey("84275e27b9ffaecb0207751b4b2349c6"), LogLevel.ALL)
//    val index = client.initIndex(IndexName("tours"))
//    val list = ArrayList<String>()
//
//    private var lat : Double = 0.0
//    private var long : Double = 0.0
//
//    companion object {
//        private lateinit var mapView: MapView
//        private const val TAG = "SearchTourFragment"
//    }
//
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        val root = inflater.inflate(R.layout.fragment_search_tour, container, false)
//        //populating list with tours
//        val tourRef = db.collection("tours")
//            .get()
//            .addOnSuccessListener { result ->
//                for (document in result) {
//                    list.add(document.getString("name")!!)
//                    //list.add(document.getString("tags")!!)
//                    //Log.d(TAG, "${document.id} => ${document.data}")
//                }
//                val arrayAdapter = ArrayAdapter<String>(context!!,android.R.layout.simple_list_item_1,list)
//                listOfResults.setAdapter(arrayAdapter)
//
//            }
//            .addOnFailureListener { exception ->
//                Log.d(TAG, "Error getting documents: ", exception)
//            }
//
//        val adapter = ArrayAdapter<String>(context!!,android.R.layout.simple_list_item_1,list)
//
//
//
//
//        searchBar.doAfterTextChanged { editable ->
//            //val query = com.algolia.search.model.search.Query(editable.toString())
//            val query = com.algolia.search.saas.Query(editable.toString())
//                .setAttributesToRetrieve("name")
//                .setHitsPerPage(50)
//            GlobalScope.launch{
//                index.search(query as com.algolia.search.model.search.Query)
//
//            }
//
//
//
//        }
//
//
//
//        Log.i(TAG, "In onCreateView")
//
//
//        if (savedInstanceState != null) {
//            // Restore last state for checked position.
//            lat = savedInstanceState.getDouble("lat")
//            long = savedInstanceState.getDouble("long")
//        }
//        return root
//    }
//    fun AppCompatActivity.configureToolbar(toolbar: Toolbar) {
//        setSupportActionBar(toolbar)
//        supportActionBar?.let {
//            it.title = intent.extras?.getString(KeyName)
//            it.setDisplayHomeAsUpEnabled(true)
//
//        }
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        mapView = view.findViewById(R.id.map) as MapView
//        mapView.onCreate(savedInstanceState)
//        mapView.onResume()
//        mapView.getMapAsync(this)
//        Log.i(TAG, "In onViewCreated")
//        super.onViewCreated(view, savedInstanceState)
//    }
//
//
//    override fun onActivityCreated(savedInstanceState: Bundle?) {
//        super.onActivityCreated(savedInstanceState)
//        Log.i(TAG, "In onActivityCreated")
//        mSharedViewModel = ViewModelProviders.of(this).get(SharedViewModel::class.java)
//    }
//
//    override fun onStart() {
//        super.onStart()
//        startLocationUpdates()
//    }
//
//
//    override fun onMapReady(googleMap: GoogleMap) {
//        mGoogleMap = googleMap
//
//    }
//
//
//    private fun startLocationUpdates() {
//        mSharedViewModel.getLocationData().observe(this, Observer {
//            lat = it.latitude
//            long = it.longitude
//            moveCamera(lat, long)
//        })
//    }
//
//
//    private fun moveCamera(latitude : Double, longitude: Double){
//        mGoogleMap.clear()
//        mGoogleMap.addMarker(MarkerOptions().position(LatLng(latitude, longitude)).title("Current Location"))
//        mGoogleMap.animateCamera(
//            CameraUpdateFactory.newLatLngZoom(
//                LatLng(
//                    latitude,
//                    longitude
//                ), 16.0f
//            )
//        )
//    }
//
//    override fun onSaveInstanceState(outState: Bundle) {
//        super.onSaveInstanceState(outState)
//        outState.putDouble("lat", lat)
//        outState.putDouble("long", long)
//    }
//
//
//
//}