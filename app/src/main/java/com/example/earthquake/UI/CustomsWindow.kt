package com.example.earthquake.UI

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.example.earthquake.R
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter
import com.google.android.gms.maps.model.Marker

class CustomsWindow(private val context: Context) : InfoWindowAdapter {
    private val view: View
    private val layoutInflater: LayoutInflater
    override fun getInfoWindow(marker: Marker): View? {
        return null
    }

    override fun getInfoContents(marker: Marker): View {
        val title = view.findViewById<View>(R.id.winTitle) as TextView
        title.text = marker.title
        val magnitude = view.findViewById<View>(R.id.magnitude) as TextView
        magnitude.text = marker.snippet
        return view
    }

    init {
        layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        view = layoutInflater.inflate(R.layout.custom_info_window, null)
    }
}