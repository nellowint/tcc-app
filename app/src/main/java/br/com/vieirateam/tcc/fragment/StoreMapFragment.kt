package br.com.vieirateam.tcc.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.vieirateam.tcc.R
import br.com.vieirateam.tcc.model.Store
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class StoreMapFragment : Fragment(), OnMapReadyCallback {

    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private lateinit var mMapView: MapView
    private lateinit var store: Store

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mMapView.onSaveInstanceState(outState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_store_map, container, false)

        val store = arguments?.getSerializable("store") as Store
        latitude = store.latitude.toDouble()
        longitude = store.longitude.toDouble()
        this.store = store

        mMapView = view?.findViewById(R.id.mapView) as MapView
        mMapView.onCreate(savedInstanceState)
        mMapView.getMapAsync(this)

        return view
    }

    override fun onResume() {
        super.onResume()
        mMapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mMapView.onPause()
    }

    override fun onStart() {
        super.onStart()
        mMapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        mMapView.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mMapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mMapView.onLowMemory()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        val newLatLng = LatLng(latitude, longitude)

        googleMap.addMarker(MarkerOptions().position(newLatLng)
                .title(getString(R.string.app_name) + " " + store.name).snippet(store.address))?.showInfoWindow()
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newLatLng, 17F))
        googleMap.uiSettings?.isZoomControlsEnabled = true
        googleMap.uiSettings?.isMyLocationButtonEnabled = true
        googleMap.uiSettings?.isZoomGesturesEnabled = true
    }
}
