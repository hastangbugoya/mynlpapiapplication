package com.example.mynlpapiapplication.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mynlpapiapplication.R
import com.example.mynlpapiapplication.data.OpenAISummarizerResponse
import com.example.mynlpapiapplication.data.ResponseDisplayType
import com.example.mynlpapiapplication.databinding.NormalResponseItemBinding
import com.example.mynlpapiapplication.databinding.TopResponseItemBinding

class ResponseRecyclerAdapter(private var context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var responseList: MutableList<OpenAISummarizerResponse> = mutableListOf()

    inner class NormalResponseItemViewHolder(var binding: NormalResponseItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    inner class TopResponseItemViewHolder(var binding: TopResponseItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when(viewType) {
            ResponseDisplayType.TOP.value -> {
                TopResponseItemViewHolder(TopResponseItemBinding.inflate(inflater,parent,false))
            }
            else -> {
                NormalResponseItemViewHolder(NormalResponseItemBinding.inflate(inflater, parent, false))
            }
        }
    }

    override fun getItemCount(): Int = responseList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {
            is TopResponseItemViewHolder -> {
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
                        this.promptTokens.text = it.prompt_tokens.toString()
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
            else -> {
                (holder as NormalResponseItemViewHolder).binding.apply {
                    val response = responseList[position]
                    this.urlText.text =
                        context.resources.getString(R.string.response_item_url_title).format(
                            response.requestString?.trim(),
                            response.maxTokens,
                            response.temperature
                        )
                    this.turnaroundText.text = context.getString(R.string.request_turnaround).format(response.getSendDateString(),response.getTurnaroundTime())
                    response.usage?.let {
                        this.promptTokens.text = it.prompt_tokens.toString()
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
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            ResponseDisplayType.TOP.value
        } else {
            ResponseDisplayType.NORMAL.value
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