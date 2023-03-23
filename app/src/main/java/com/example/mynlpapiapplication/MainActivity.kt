package com.example.mynlpapiapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.example.mynlpapiapplication.core.APIKey
import com.example.mynlpapiapplication.core.MySharedPreference
import com.example.mynlpapiapplication.databinding.ActivityMainBinding
import com.example.mynlpapiapplication.network.OpenAISummarizer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), OpenAISummarizer.UIUpdater {
    private var maxTokens: Int = 50
    private var temperature: Double = 0.5
    private val myAPIKey: APIKey by lazy {
        APIKey(mySharedPreference)
    }
    private val mySharedPreference: MySharedPreference by lazy {
        MySharedPreference(this)
    }

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(LayoutInflater.from(this))
    }

    private val openAISummarizer = OpenAISummarizer()
    val loadSize = arrayOf<String>("50", "100", "200")

    val temperatureArray = Array<String>(10) {
        "%.1f".format((it + 1) / 10.0)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myAPIKey.getAPIInfo()
        if (BuildConfig.DEBUG) {
            myAPIKey.setAPIKey(BuildConfig.API_KEY)
            myAPIKey.saveAPIInfo()
        }
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

        binding.temperatureSpinner.adapter = ArrayAdapter(
            this,
            R.layout.shrink_spinner_item,
            temperatureArray
        )
            .apply {
                setDropDownViewResource(
                    android.R.layout.simple_spinner_dropdown_item
                )
            }
        binding.temperatureSpinner.setSelection(4)

        binding.summarizeButton.setOnClickListener {
            if (myAPIKey.isNullorEmpty()) {
                askForAPIKey()
            }
            if (!myAPIKey.isNullorEmpty() && !binding.url.text.isNullOrEmpty()) {
                CoroutineScope(Dispatchers.Main).launch {
                    binding.loadingImage.visibility = View.VISIBLE
                    val result =
                        openAISummarizer.summarizeUrl(
                            myAPIKey.getAPIKey()!!,
                            binding.url.text.toString(),
                            maxTokens,
                            temperature,
                            Dispatchers.IO
                        )
                    binding.loadingImage.visibility = View.GONE
                    binding.summaryText.text =
                        result.choices?.get(0)?.text ?: result.error?.toString() ?: "Unknown error"
                    result.usage?.let {
                        binding.promtTokens.text = it.prompt_tokens.toString()
                        binding.completionTokens.text = it.completion_tokens.toString()
                        binding.totalTokens.text = it.total_tokens.toString()
                    }
                    if (result.code.equals(401)) {
                        myAPIKey.resetKey()
                        myAPIKey.saveAPIInfo()
                        Log.d(
                            "Meow",
                            "Code: ${result.code} myAPIKey on Fail key:${myAPIKey.getAPIKey()}"
                        )
                        askForAPIKey()
                    }
                }
            }
        }

        binding.maxTokensSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
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
        binding.temperatureSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    // May or may not affect return length - just setting AI creative freedom
                    // higher value gives more leeway
                    temperature = parent.getItemAtPosition(position).toString().toDouble()
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    TODO("Something")
                }
            }

    }

    override fun onDestroy() {
        super.onDestroy()
    }

    fun askForAPIKey() {
        val editText = EditText(this)
        AlertDialog.Builder(this)
            .setTitle("Enter your OpenAI API key")
            .setView(editText)
            .setPositiveButton("OK") { _, _ ->
                val inputString = editText.text.toString()
                if (!inputString.isNullOrEmpty()) {
                    myAPIKey.setAPIKey(inputString)
                    myAPIKey.saveAPIInfo()
                }
            }
            .setNegativeButton("Cancel", null)
            .create().show()
    }

    override fun lockupButton() {
        binding.summarizeButton.isClickable = false
    }

    override fun releaseButton() {
        binding.summarizeButton.isClickable = true
    }
}