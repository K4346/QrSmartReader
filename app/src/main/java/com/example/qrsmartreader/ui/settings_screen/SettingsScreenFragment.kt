package com.example.qrsmartreader.ui.settings_screen

import android.R
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.RadioButton
import androidx.core.text.isDigitsOnly
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.qrsmartreader.domain.entities.CameraRecognitionType
import com.example.qrsmartreader.domain.entities.ProcessorRecognitionType
import com.example.qrsmartreader.databinding.FragmentSettingsScreenBinding


class SettingsScreenFragment : Fragment() {

    private var _binding: FragmentSettingsScreenBinding? = null

    private val binding get() = _binding!!

    private val viewModel: SettingsScreenViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsScreenBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        initListeners()
    }

    private fun initViews() {
        binding.fpsSwitch.isChecked = viewModel.getFpsCounterVisible()

        setRadioGroup()

        binding.etMaxResults.setText(viewModel.getMaxResultsCount().toString())

        binding.etMaxResults.addTextChangedListener {

            if (it.toString().isBlank()) {
                viewModel.changeMaxResults(0)
            } else
                if (it.toString().isDigitsOnly()) {
                    viewModel.changeMaxResults(it.toString().toInt())
                }
        }
        initSpinner()

    }

    private fun initSpinner() {
        binding.modelVersionSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedItem = parent.getItemAtPosition(position).toString()
                viewModel.changeModelName(selectedItem)
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
        initSpinnerAdapter()
    }

    private fun initSpinnerAdapter() {
        val adapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_item, viewModel.getFilesFromAssetsFolder())
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        binding.modelVersionSpinner.adapter = adapter
    }

    private fun setRadioGroup() {
        when (viewModel.getProcessor()) {
            ProcessorRecognitionType.Cpu -> binding.cpuMode.isChecked = true
            ProcessorRecognitionType.Gpu -> binding.gpuMode.isChecked = true
        }
        when (viewModel.getRecognitionType()) {
            CameraRecognitionType.Segment -> binding.segmentationMode.isChecked = true
            CameraRecognitionType.Pose -> binding.keyPointsDetectionMode.isChecked = true
        }
    }

    private fun initListeners() {
        binding.clearHistoryButton.setOnClickListener {
            viewModel.clearHistory()
        }
        binding.fpsSwitch.setOnCheckedChangeListener { compoundButton, flag ->
            viewModel.changeFpsCounterVisible(flag)
        }
        binding.rgProcessorType.setOnCheckedChangeListener { radioGroup, i ->
            val processor =
                if (binding.gpuMode.isChecked) ProcessorRecognitionType.Gpu
                else ProcessorRecognitionType.Cpu
            viewModel.changeProcessorType(processor)
        }
        binding.rgRecognitionType.setOnCheckedChangeListener { radioGroup, i ->
            val type =
                if (binding.keyPointsDetectionMode.isChecked) CameraRecognitionType.Pose
                else CameraRecognitionType.Segment
            viewModel.changeRecognitionType(type)
            initSpinnerAdapter()
        }
    }
}