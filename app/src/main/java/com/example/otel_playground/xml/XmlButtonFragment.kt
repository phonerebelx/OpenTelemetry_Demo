package com.example.otel_playground.xml

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import com.example.otel_playground.OtelApplication
import com.example.otel_playground.R


class XmlButtonFragment : Fragment(R.layout.fragment_xml_button) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val button = view.findViewById<ImageButton>(R.id.xmlButton)

        button.setOnClickListener {
            Log.i("OTEL_XML", "XML button clicked")

            // FEATURE 1 (MANUAL): DOMAIN EVENT
//            OtelApplication
//                .eventBuilder("xml.fragment", "xml_button.clicked")
//                .setAttribute("source", "xml")
//                .setAttribute("screen", "MainActivity")
//                .emit()
        }
    }
}

