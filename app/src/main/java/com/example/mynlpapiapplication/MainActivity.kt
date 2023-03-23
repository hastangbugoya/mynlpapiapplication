package com.example.mynlpapiapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
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
    private val loadSize = arrayOf<String>("50", "100", "150", "200")

    private val temperatureArray = Array<String>(10) {
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
                            this@MainActivity,
                            myAPIKey.getAPIKey()!!,
                            binding.url.text.toString(),
                            maxTokens,
                            temperature,
                            Dispatchers.IO
                        )
                    binding.loadingImage.visibility = View.GONE
                    binding.summaryText.text =
                        result.choices?.get(0)?.text ?: result.error?.toString()
                                ?: getString(R.string.unknown_error)
                    result.usage?.let {
                        binding.promtTokens.text = it.prompt_tokens.toString()
                        binding.completionTokens.text = it.completion_tokens.toString()
                        binding.totalTokens.text = it.total_tokens.toString()
                    }
                    if (result.code == 401) {
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

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {

        outState.putString("url",binding.url.text.toString())
        outState.putString("prompt_token",binding.promptTokensLabel.text.toString())
        outState.putString("completion_tokens",binding.completionTokens.text.toString())
        outState.putString("total_tokens",binding.totalTokens.text.toString())
        outState.putString("summary",binding.summaryText.text.toString())
        outState.putInt("max_tokens", maxTokens)
        outState.putDouble("temperature", temperature)
        super.onSaveInstanceState(outState, outPersistentState)
    }

    override fun onRestoreInstanceState(
        savedInstanceState: Bundle?,
        persistentState: PersistableBundle?
    ) {
        binding.url.setText(savedInstanceState?.getString("url"))
        binding.promtTokens.setText(savedInstanceState?.getString("prompt_tokens"))
        binding.completionTokens.setText(savedInstanceState?.getString("completion_tokens"))
        binding.totalTokens.setText(savedInstanceState?.getString("summary"))
        maxTokens = savedInstanceState?.getInt("max_tokens") ?: 50
        binding.maxTokensSpinner.setSelection((maxTokens/50) - 1)
        temperature = savedInstanceState?.getDouble("temperature") ?: 0.5
        binding.temperatureSpinner.setSelection((temperature * 10).toInt() - 1)
        super.onRestoreInstanceState(savedInstanceState, persistentState)
    }

    private fun askForAPIKey() {
        val editText = EditText(this)
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.enter_apikey_prompt))
            .setView(editText)
            .setPositiveButton(getString(R.string.ok)) { _, _ ->
                val inputString = editText.text.toString()
                if (inputString.isNotEmpty()) {
                    myAPIKey.setAPIKey(inputString)
                    myAPIKey.saveAPIInfo()
                }
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .create().show()
    }

    override fun lockupButton() {
        binding.summarizeButton.isClickable = false
    }

    override fun releaseButton() {
        binding.summarizeButton.isClickable = true
    }
}