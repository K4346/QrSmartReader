package com.example.qrsmartreader.ui.gallery_detection

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.example.qrsmartreader.R
import com.example.qrsmartreader.databinding.FragmentGalleryDetectionBinding
import com.example.qrsmartreader.ui.result_screen.ResultScreenFragment

class GalleryDetectionFragment : Fragment() {

    private var _binding: FragmentGalleryDetectionBinding? = null

    private val binding get() = _binding!!

    private val viewModel: GalleryDetectionViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGalleryDetectionBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.qrPointView.init(viewModel.aiResult.image, viewModel.aiResult.points)

        setListeners()
        setObservers()
    }

    private fun setObservers() {
        viewModel.recognisedQrSLE.observe(viewLifecycleOwner) { result ->
            if (result.isNotEmpty()) {
                Navigation.findNavController(requireView())
                    .navigate(
                        R.id.action_galleryDetectionFragment_to_resultScreenFragment,
                        bundleOf(ResultScreenFragment.QR_DETECTION_RESULT to result)
                    )
            } else {
                Toast.makeText(requireContext(),
                    getString(R.string.not_worked_recgnition),Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setListeners() {
        binding.changeButton.setOnClickListener {
            val newRationPoints = binding.qrPointView.getRatioPoints()
            val newQrPhoto = viewModel.getNewBitmap(newRationPoints)
            binding.ivCheck.setImageBitmap(newQrPhoto)

            newQrPhoto?.let { qr -> viewModel.decodeQR(qr) }
        }
    }


}