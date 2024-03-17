package com.farzin.batterymanager.activities

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.farzin.batterymanager.databinding.ActivitySplashScreenBinding
import com.farzin.batterymanager.helper.SPManager
import java.util.*
import kotlin.concurrent.timerTask

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)




        val array = arrayOf(
            "Make your Battery Powerful",
            "Make your Battery Safe",
            "Make your Battery Faster",
            "Manage your Phone Battery",
            "Notify When Fully Charged",
            "(:"
        )

        var i = 1
        for (i in 1..6) {
            helperTextGenerator((i * 1000).toLong(), array[i-1])
        }

        Timer().schedule(timerTask {
            startActivity(Intent(applicationContext, MainActivity::class.java))
            finish()
        }, 7000)
    }

    private fun helperTextGenerator(delay: Long, text: String) {
        Timer().schedule(timerTask {
            runOnUiThread(timerTask {
                binding.helpTxt.text = text
            })
        }, delay)
    }
}