package com.example.mynlpapiapplication.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mynlpapiapplication.R
import com.example.mynlpapiapplication.data.OpenAISummarizerResponse
import com.example.mynlpapiapplication.databinding.ResponseItemBinding

class ResponseRecyclerAdapter(private var context: Context) :
    RecyclerView.Adapter<ResponseRecyclerAdapter.ResponseItemViewHolder>() {

    private var responseList: MutableList<OpenAISummarizerResponse> = mutableListOf()

    inner class ResponseItemViewHolder(var binding: ResponseItemBinding) :
        RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResponseItemViewHolder {
        val binding =
            ResponseItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ResponseItemViewHolder(binding)
    }

    override fun getItemCount(): Int = responseList.size

    override fun onBindViewHolder(holder: ResponseItemViewHolder, position: Int) {
        holder.binding.apply {
            val response = responseList[position]
            this.urlText.text =
                context.resources.getString(R.string.response_item_url_title).format(
                    response.requestString?.trim(),
                    response.maxTokens,
                    response.temperature
                )
            this.turnaroundText.text = context.getString(R.string.request_turnaround).format(response.getSendDateString(),response.getTurnaroundTime())
            response.usage?.let {
                this.promtTokens.text = it.prompt_tokens.toString()
                this.completionTokens.text = it.completion_tokens.toString()
                this.totalTokens.text = it.total_tokens.toString()
            }
            response.choices?.get(0)?.let {
                this.summaryText.text = it.responseText.trim()
            }
            responseList[position].error?.let {
                this.summaryText.text = it.toString()
            }
        }
    }

    fun setResponseList(l: MutableList<OpenAISummarizerResponse>) {
        responseList = l
        notifyDataSetChanged()
    }

    fun addToList(response: OpenAISummarizerResponse) {
        responseList.add(0, response)
        notifyDataSetChanged()
    }
}