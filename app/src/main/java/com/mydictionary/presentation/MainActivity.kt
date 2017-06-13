package com.mydictionary.presentation

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.mydictionary.R
import com.mydictionary.data.entity.WordInfo
import com.mydictionary.data.repository.WordsRepository
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val app = DictionaryApp.getInstance(this)
        app.repository.getTodayWord(Calendar.getInstance().time, object : WordsRepository.WordSourceListener<WordInfo>{
            override fun onSuccess(wordInfo: WordInfo?) {
                Toast.makeText(this@MainActivity, wordInfo?.definitions?.get(0)?.definition, Toast.LENGTH_LONG).show()
            }
            override fun onError(error: String?) {
                Toast.makeText(this@MainActivity, error, Toast.LENGTH_LONG).show()
            }
        })
    }
}
