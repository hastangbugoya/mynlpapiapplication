package com.example.mynlpapiapplication

import android.R
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.example.mynlpapiapplication.databinding.ActivityMainBinding
import com.example.mynlpapiapplication.network.OpenAISummarizer
import com.example.mynlpapiapplication.network.SummarizeBot
import com.example.mynlpapiapplication.network.SummarizeBotClient

class MainActivity : AppCompatActivity() {
    private var summarySize : Int = 10
    private val binding : ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(LayoutInflater.from(this))
    }
    private val summarizeBotClient = SummarizeBotClient()
    private val summarizeBot = SummarizeBot("XXXXX")
    private val openAISummarizer = OpenAISummarizer()
    val loadSize = arrayOf<String>("10", "20", "30", "40", "50", "60", "70")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.summarySize.adapter = ArrayAdapter(
            this,
            com.example.mynlpapiapplication.R.layout.shrink_spinner_item,
            loadSize)
            .apply {
                setDropDownViewResource(
                    android.R.layout.simple_spinner_dropdown_item
                )
            }

        binding.summarizeButton.setOnClickListener {
            if (!binding.url.text.isNullOrEmpty()) {
                openAISummarizer.summarizeUrl(binding.url.text.toString(),200) {
                    binding.summaryText.text = it.toString()
                }
            }
        }

        binding.summarySize.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                summarySize = Integer.parseInt(parent.getItemAtPosition(position).toString())
                // do something with the selected item
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // do nothing
            }
        }
    }
}