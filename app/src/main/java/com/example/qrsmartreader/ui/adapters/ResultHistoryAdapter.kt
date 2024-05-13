package com.example.qrsmartreader.ui.adapters

import android.text.util.Linkify
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.qrsmartreader.data.entities.QrResultEntity
import com.example.qrsmartreader.databinding.ItemResultBinding
import java.time.format.DateTimeFormatter

class ResultHistoryAdapter : RecyclerView.Adapter<ResultHistoryAdapter.ResultHistoryHolder>() {

    var list: List<QrResultEntity> = arrayListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ResultHistoryAdapter.ResultHistoryHolder {
        val binding = ItemResultBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ResultHistoryHolder(binding)
    }

    override fun onBindViewHolder(holder: ResultHistoryAdapter.ResultHistoryHolder, position: Int) {
        val item = list[position]

        holder.resultText.text = item.text
        Linkify.addLinks(holder.resultText, Linkify.WEB_URLS)
        holder.resultDate.text = item.date
    }

    override fun getItemCount() = list.size
    inner class ResultHistoryHolder(binding: ItemResultBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val resultText = binding.tvResultText
        val resultDate = binding.tvResultDate
    }

}