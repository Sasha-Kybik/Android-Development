package com.example.criminalintent

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import java.io.File

class ZoomDialogFragment: DialogFragment() {

    private lateinit var imageZoom: ImageView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.photo_zoom, container, false)

        imageZoom = view.findViewById(R.id.image_zoom) as ImageView
        val image = arguments?.getSerializable("image") as File

        imageZoom.setImageBitmap(getScaledBitmap(image.path, requireActivity()))

        return view
    }

    companion object {
        fun photoZoom(pic: File): ZoomDialogFragment {
            val image = Bundle().apply {
                putSerializable("image", pic)
            }

            return ZoomDialogFragment().apply {
                arguments = image
            }
        }
    }
}
