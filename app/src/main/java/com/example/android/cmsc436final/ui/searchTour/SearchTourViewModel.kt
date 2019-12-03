package com.example.android.cmsc436final.ui.searchTour

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.algolia.instantsearch.core.connection.ConnectionHandler
import com.algolia.instantsearch.core.selectable.list.SelectionMode
import com.algolia.instantsearch.helper.android.filter.facet.FacetListAdapter
import com.algolia.instantsearch.helper.android.filter.state.connectPagedList
import com.algolia.instantsearch.helper.android.list.SearcherSingleIndexDataSource
import com.algolia.instantsearch.helper.android.searchbox.SearchBoxConnectorPagedList
import com.algolia.instantsearch.helper.filter.facet.*
import com.algolia.instantsearch.helper.filter.state.FilterState
import com.algolia.instantsearch.helper.searcher.SearcherSingleIndex
import com.algolia.instantsearch.helper.searcher.connectFilterState
import com.algolia.instantsearch.helper.stats.StatsConnector
import com.algolia.search.client.ClientSearch
import com.algolia.search.model.APIKey
import com.algolia.search.model.ApplicationID
import androidx.paging.PagedListAdapter
import com.algolia.search.model.Attribute
import com.algolia.search.model.IndexName
import com.example.android.cmsc436final.model.Tour
import io.ktor.client.features.logging.LogLevel


class SearchTourViewModel: ViewModel() {

        val client = ClientSearch(ApplicationID("MLWVY1AHOC"), APIKey("84275e27b9ffaecb0207751b4b2349c6"), LogLevel.ALL)
        val index = client.initIndex(IndexName("tours"))
        val searcher = SearcherSingleIndex(index)
        val dataSourceFactory = SearcherSingleIndexDataSource.Factory(searcher) { hit ->
            TourDataOrganization(
                hit.json.getPrimitive("name").content,
                hit.json.getPrimitive("tourID").content,
                hit.json.getObjectOrNull("_highlightResult")

            )
        }
        val pagedListConfig = PagedList.Config.Builder().setPageSize(50).setEnablePlaceholders(false).build()
        val tours: LiveData<PagedList<TourDataOrganization>> = LivePagedListBuilder(dataSourceFactory, pagedListConfig).build()
        val searchBox = SearchBoxConnectorPagedList(searcher, listOf(tours))
        val adapterTour = SearchTourAdapter(/*parentFragment*/)
        val stats = StatsConnector(searcher)



    val filterState = FilterState()
    val facetList = FacetListConnector(
        searcher = searcher,
        filterState = filterState,
        attribute = Attribute("tags"),
        selectionMode = SelectionMode.Single
    )
    val facetPresenter = FacetListPresenterImpl(
        sortBy = listOf(FacetSortCriterion.CountDescending, FacetSortCriterion.IsRefined),
        limit = 100
    )

    val adapterFacet = FacetListAdapter(FacetViewHolderImpl.Factory)
    val connection = ConnectionHandler()

    init {
        connection += searchBox
        connection += stats
        connection += facetList
        connection += searcher.connectFilterState(filterState)
        connection += facetList.connectView(adapterFacet, facetPresenter)
        connection += filterState.connectPagedList(tours)
    }

    override fun onCleared() {
        super.onCleared()
        searcher.cancel()
        connection.disconnect()
    }

}