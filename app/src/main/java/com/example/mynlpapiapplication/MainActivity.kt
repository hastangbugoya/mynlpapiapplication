package com.example.mynlpapiapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.example.mynlpapiapplication.databinding.ActivityMainBinding
import com.example.mynlpapiapplication.network.OpenAISummarizer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), OpenAISummarizer.UIUpdater {
    private var maxTokens: Int = 50
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(LayoutInflater.from(this))
    }
    private val openAISummarizer = OpenAISummarizer()
    val loadSize = arrayOf<String>("50", "100", "200")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        openAISummarizer.setUIUpdater(this)
        binding.maxTokensSpinner.adapter = ArrayAdapter(
            this,
            R.layout.shrink_spinner_item,
            loadSize
        )
            .apply {
                setDropDownViewResource(
                    android.R.layout.simple_spinner_dropdown_item
                )
            }

        binding.summarizeButton.setOnClickListener {
            if (!binding.url.text.isNullOrEmpty()) {
                CoroutineScope(Dispatchers.Main).launch {
                    val result =
                        openAISummarizer.summarizeUrl(binding.url.text.toString(), maxTokens)
                    binding.summaryText.text = result.choices?.get(0)?.text ?: result.error?.toString() ?: ""
                }
            }
        }

        binding.maxTokensSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                // May or may not affect return length - just setting maximum
                // return length may be affected when we set temperature
                maxTokens = Integer.parseInt(parent.getItemAtPosition(position).toString())
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                TODO("Something")
            }
        }
    }

    override fun lockupButton() {
       binding.summarizeButton.isClickable = false
    }

    override fun releaseButton() {
        binding.summarizeButton.isClickable = true
    }
}