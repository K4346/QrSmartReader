package com.example.qrsmartreader.ui.result_screen

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Bundle
import android.text.util.Linkify
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.example.qrsmartreader.R
import com.example.qrsmartreader.domain.entities.CameraRecognitionType
import com.example.qrsmartreader.databinding.FragmentResultScreenBinding
import com.example.qrsmartreader.ui.adapters.ResultHistoryAdapter
import com.example.qrsmartreader.ui.camera_detection_screen.CameraDetectionFragment.Companion.CAMERA_RECOGNITION_TYPE

class ResultScreenFragment : Fragment() {

    private var _binding: FragmentResultScreenBinding? = null

    private val binding get() = _binding!!

    private val viewModel: ResultScreenViewModel by viewModels()


    private lateinit var adapter: ResultHistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentResultScreenBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAdapters()

        initResultViews()

        initObservers()

        initListeners()



    }

    private fun initAdapters() {
        adapter = ResultHistoryAdapter()
        binding.rvHistory.adapter = adapter
    }

    private fun initObservers() {
        viewModel.getHistory().observe(viewLifecycleOwner){
            viewModel.prepareResultsIfNeeded(it)
            adapter.list = it

            if (adapter.list.isNotEmpty()){
                showHistoryPart()
            }
        }
        viewModel.recognisedQrSLE.observe(viewLifecycleOwner) { result ->
            Log.i("kpop222", result)
//todo
            showResultText(result.ifEmpty { "Ничего не найдено" })

            if (result.isNullOrBlank()) {
                viewModel.scanAiQr()
            }
        }
        viewModel.aiRecognisedQrSLE.observe(viewLifecycleOwner) {
            if (it.result.isNullOrEmpty()) {
                viewModel.setAiResult(it)
                Navigation.findNavController(requireView())
                    .navigate(R.id.action_resultScreenFragment_to_galleryDetectionFragment)
            } else {
                Toast.makeText(requireContext(), it.result[0], Toast.LENGTH_LONG).show()
                showResultText(it.result[0])
            }
        }
    }

    private fun showHistoryPart() {
        binding.separatorHistory.isVisible = true
        binding.tvHistoryLabel.isVisible = true
    }

    private fun initListeners() {
        binding.galleryButton.setOnClickListener {
            openGalleryForImage()
        }
        binding.cameraButton.setOnClickListener {
            openCameraScreen()
        }
        binding.settingsButton.setOnClickListener {
            openSettingsScreen()
        }
//        todo
//        binding.cameraPoseButton.setOnClickListener {
//            openCameraScreen(CameraRecognitionType.Pose)
//        }
    }

    private fun openSettingsScreen() {
        Navigation.findNavController(requireView())
            .navigate(
                R.id.action_resultScreenFragment_to_settingsScreenFragment
            )

    }

    private fun openCameraScreen() {
        Navigation.findNavController(requireView())
            .navigate(R.id.action_resultScreenFragment_to_cameraDetectionFragment)
    }

    private fun openGalleryForImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, GALLERY_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val photoUri = data?.data
        if (resultCode == Activity.RESULT_OK && requestCode == GALLERY_REQUEST && photoUri != null) {
            viewModel.sourceBitmap =

                    //   ImageDecoder.decodeBitmap(ImageDecoder.createSource(requireContext().contentResolver, photoUri))
                ImageDecoder.decodeBitmap(
                    ImageDecoder.createSource(
                        requireContext().contentResolver,
                        photoUri
                    )
                ).copy(
                    Bitmap.Config.ARGB_8888, true
                )

            viewModel.scanQr()
        }
    }


    private fun initResultViews() {
        val qrText = arguments?.getString(QR_DETECTION_RESULT)
        if (!qrText.isNullOrEmpty()) {
            showResultText(qrText)
        }
    }

    private fun showResultText(text: String) {
        binding.separatorResult.isVisible = true
        binding.tvResultLabel.isVisible = true
        binding.tvResult.isVisible = true
        binding.tvResult.text = text
        Linkify.addLinks(binding.tvResult, Linkify.WEB_URLS)
    }

    companion object {
        private const val GALLERY_REQUEST = 100

        const val QR_AI_RESULT = "QR_AI_RESULT"
        const val QR_TEXT_RESULT = "QR_TEXT_RESULT"

        const val QR_DETECTION_RESULT = "QR_DETECTION_RESULT"
    }

}