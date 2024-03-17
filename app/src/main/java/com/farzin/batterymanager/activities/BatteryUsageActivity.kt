package com.farzin.batterymanager.activities

import android.icu.lang.UCharacter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.farzin.batterymanager.R
import com.farzin.batterymanager.adapter.BatteryAdapter
import com.farzin.batterymanager.databinding.ActivityBatteryUsageBinding
import com.farzin.batterymanager.model.BatteryModel
import com.farzin.batterymanager.utils.BatteryUsage

class BatteryUsageActivity : AppCompatActivity() {

    private lateinit var  binding:ActivityBatteryUsageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBatteryUsageBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val batteryUsage = BatteryUsage(this)


        var batteryPercentageArray: MutableList<BatteryModel> = ArrayList()
        for (item in batteryUsage.getUsageStats()) {

            if (item.totalTimeInForeground > 0) {
                var bm = BatteryModel()
                bm.packageName = item.packageName
                bm.percentUsage =
                    (item.totalTimeInForeground.toFloat() / batteryUsage.getTotalTime()
                        .toFloat() * 100).toInt()

                batteryPercentageArray += bm
            }

        }







        binding.rvUsage.adapter = BatteryAdapter(this,batteryPercentageArray,batteryUsage.getTotalTime())
        binding.rvUsage.setHasFixedSize(true)
        binding.rvUsage.layoutManager = LinearLayoutManager(this,RecyclerView.VERTICAL,false)
        binding.rvUsage.addItemDecoration(DividerItemDecoration(this,RecyclerView.VERTICAL))
    }
}