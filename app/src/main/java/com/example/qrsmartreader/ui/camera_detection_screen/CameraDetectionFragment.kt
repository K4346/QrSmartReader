package com.example.qrsmartreader.ui.camera_detection_screen

import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.SurfaceHolder
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.AdapterView
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.example.qrsmartreader.R
import com.example.qrsmartreader.Yolov8Ncnn
import com.example.qrsmartreader.Yolov8NcnnInterface
import com.example.qrsmartreader.Yolov8NcnnPose
import com.example.qrsmartreader.domain.entities.CameraRecognitionType
import com.example.qrsmartreader.domain.entities.ProcessorRecognitionType
import com.example.qrsmartreader.databinding.FragmentCameraDetectionBinding
import com.example.qrsmartreader.ui.result_screen.ResultScreenFragment.Companion.QR_DETECTION_RESULT

class CameraDetectionFragment : Fragment(), SurfaceHolder.Callback {

    private lateinit var binding: FragmentCameraDetectionBinding


    private val viewModel: CameraDetectionViewModel by viewModels()


    private val REQUEST_CAMERA = 100

    private lateinit var yolov8ncnn: Yolov8NcnnInterface

    private var current_model = 0

    private val handler: Handler = Handler(Looper.getMainLooper())


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCameraDetectionBinding.inflate(inflater, container, false)
        val view = binding.root

        yolov8ncnn = when (viewModel.modelType){
            CameraRecognitionType.Segment -> Yolov8Ncnn()
            CameraRecognitionType.Pose -> Yolov8NcnnPose()
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        binding.cameraview.holder.setFormat(PixelFormat.RGBA_8888)
        binding.cameraview.holder.addCallback(this)


        scanQRCodeRunnable.run()

        initListeners()

        binding.spinnerModel.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                arg0: AdapterView<*>?,
                arg1: View?,А
                position: Int,
                id: Long
            ) {
                if (arg1 == null) return
                if (position != current_model) {
                    current_model = position
                    reload()
                }
            }

            override fun onNothingSelected(arg0: AdapterView<*>?) {}
        }

        reload()
    }

    private fun initListeners() {
        viewModel.recognisedQrSLE.observe(viewLifecycleOwner) { result ->
//            todo
            if (result.isNotEmpty()) {
                Log.i("kpop", result)
//                todo разобрать мб
                yolov8ncnn.clearQR()
                Navigation.findNavController(requireView())
                    .navigate(
                        R.id.action_cameraDetectionFragment_to_resultScreenFragment,
                        bundleOf(QR_DETECTION_RESULT to result)
                    )
                binding.spinnerModel.onItemSelectedListener = null
                yolov8ncnn.sendQRDataToCpp()?.let {
                    viewModel.currentImage = it
                }
            }
        }
    }

    private fun reload() {
        val ret_init = yolov8ncnn.loadModel(requireActivity().assets, current_model,
            when (viewModel.processorType){
                ProcessorRecognitionType.Cpu -> 0
                ProcessorRecognitionType.Gpu -> 1
            }
        )
        if (!ret_init) {
            Log.e("MainActivity", "yolov8ncnn loadModel failed")
        }
    }

    override fun surfaceCreated(p0: SurfaceHolder) {}

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        yolov8ncnn.setOutputWindow(holder.surface)
    }

    override fun surfaceDestroyed(p0: SurfaceHolder) {}

    override fun onResume() {
        super.onResume()
        if (checkSelfPermission(
                requireContext(),
                android.Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_DENIED
        ) {
            requestPermissions(arrayOf<String>(android.Manifest.permission.CAMERA), REQUEST_CAMERA)
        }
        yolov8ncnn.openCamera(1, viewModel.isFpsCounting)
        scanQRCodeRunnable.run()
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacksAndMessages(null)
        yolov8ncnn.closeCamera()
    }

    private val scanQRCodeRunnable: Runnable = object : Runnable {
        override fun run() {
            val qwerty = yolov8ncnn.sendQRDataToCpp()
            val height = yolov8ncnn.qr_h()
            val width = yolov8ncnn.qr_w()
            if (qwerty != null && qwerty.isNotEmpty()
                && height != 0 && width != 0
                && !viewModel.currentImage.contentEquals(qwerty)
            ) {
                viewModel.currentImage = qwerty
                Log.i("kpoper", "WTF ${height}")
                readQRCode(qwerty, height, width)
            }
            handler.postDelayed(this, 300) // Повторяем через 0.1 секунду
        }
    }

    fun readQRCode(byteArray: IntArray?, h: Int, w: Int) {

        // Преобразование массива байтов в объект Bitmap
        val bitmap = Bitmap.createBitmap(byteArray!!, w, h, Bitmap.Config.ARGB_8888)
        binding.img.setImageBitmap(bitmap)
        viewModel.decodeQr(bitmap)
    }

    companion object {
        const val CAMERA_RECOGNITION_TYPE = "CameraRecognitionType"
    }

}