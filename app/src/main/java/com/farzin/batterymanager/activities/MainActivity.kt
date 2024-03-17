package com.farzin.batterymanager.activities

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.BatteryManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import com.farzin.batterymanager.R
import com.farzin.batterymanager.databinding.ActivityMainBinding
import com.farzin.batterymanager.helper.SPManager
import com.farzin.batterymanager.model.BatteryModel
import com.farzin.batterymanager.service.BatteryNotifications
import com.farzin.batterymanager.utils.BatteryUsage

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)



        binding.menu.setOnClickListener{
            binding.drawer.openDrawer(GravityCompat.END)
        }

        binding.icLayout.txtAppUsage.setOnClickListener{
            startActivity(Intent(this@MainActivity,BatteryUsageActivity::class.java))
            binding.drawer.closeDrawer(GravityCompat.END)
        }



        registerReceiver(batteryInfoReceiever, IntentFilter(Intent.ACTION_BATTERY_CHANGED))


        if (SPManager.isServiceOn(this@MainActivity) == true){
            binding.icLayout.switchCompat.isChecked = true
            binding.icLayout.switchTxt.text = "service is on"
            startService()
        }else{
            binding.icLayout.switchCompat.isChecked = false
            binding.icLayout.switchTxt.text = "service is off"
            stopService()
        }

        binding.icLayout.switchCompat.setOnCheckedChangeListener { switch, isChecked ->
            SPManager.setServiceState(applicationContext,isChecked)

            if (isChecked == true){
                startService()
                binding.icLayout.switchTxt.text = "service is on"
                Toast.makeText(applicationContext,"service is on",Toast.LENGTH_SHORT).show()
            }else{
                stopService()
                binding.icLayout.switchTxt.text = "service is off"
                Toast.makeText(applicationContext,"service is off",Toast.LENGTH_SHORT).show()
            }
        }


    }

    private var batteryInfoReceiever: BroadcastReceiver = object : BroadcastReceiver() {
        @RequiresApi(Build.VERSION_CODES.M)
        @SuppressLint("SetTextI18n")
        override fun onReceive(context: Context, intent: Intent) {
            var battery = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0)

            if (intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0) == 0) {
                binding.txtPlug.text = getString(R.string.plugged_out)
            } else {
                binding.txtPlug.text = getString(R.string.plugged_in)
            }

            binding.txtVoltage.text = (intent.getIntExtra(
                BatteryManager.EXTRA_VOLTAGE,
                0
            ) / 1000).toString() + " volt" //تبدیل به وولت

            binding.txtTemp.text = (intent.getIntExtra(
                BatteryManager.EXTRA_TEMPERATURE,
                0
            ) / 10).toString() + " °C"//تبدیل به سانتیگراد

            binding.txtTech.text = intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY)



            binding.circularProgressBar.progressMax = 100F

            if (battery >= 80){
                binding.circularProgressBar.progressBarColor = Color.GREEN
                binding.txtCharge.setTextColor(Color.GREEN)
                binding.bolt.setColorFilter(Color.GREEN)
            } else if (battery < 80 && battery>=40){
                binding.circularProgressBar.progressBarColor = getColor(R.color.teal_200)
                binding.txtCharge.setTextColor(getColor(R.color.teal_200))
                binding.bolt.setColorFilter(getColor(R.color.teal_200))
            } else if(battery < 40 && battery >= 10){
                binding.circularProgressBar.progressBarColor = Color.YELLOW
                binding.txtCharge.setTextColor(Color.YELLOW)
                binding.bolt.setColorFilter(Color.YELLOW)
            }else if (battery < 10 && battery >= 0){
                binding.circularProgressBar.progressBarColor = Color.RED
                binding.txtCharge.setTextColor(Color.RED)
                binding.bolt.setColorFilter(Color.RED)
            }

            binding.circularProgressBar.setProgressWithAnimation(battery.toFloat())
            binding.txtCharge.text = battery.toString() + "%"

            var batteryHealth = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, 0)

            when(batteryHealth){
                BatteryManager.BATTERY_HEALTH_DEAD->{
                    binding.txtBatteryHeath.text = "Your battery is dead,);"
                    binding.txtBatteryHeath.setTextColor(Color.WHITE)
                    binding.imgHealth.setImageResource(R.drawable.dead)
                }

                BatteryManager.BATTERY_HEALTH_COLD->{
                    binding.txtBatteryHeath.text = "Your battery is cold, It's OK!"
                    binding.txtBatteryHeath.setTextColor(Color.CYAN)
                    binding.imgHealth.setImageResource(R.drawable.cold)
                }

                BatteryManager.BATTERY_HEALTH_GOOD->{
                    binding.txtBatteryHeath.text = "your battery health is good, take care"
                    binding.txtBatteryHeath.setTextColor(Color.GREEN)
                    binding.imgHealth.setImageResource(R.drawable.good)
                }

                BatteryManager.BATTERY_HEALTH_OVERHEAT->{
                    binding.txtBatteryHeath.text = "Your battery is overheat, stop working with your phone"
                    binding.txtBatteryHeath.setTextColor(Color.RED)
                    binding.imgHealth.setImageResource(R.drawable.overheat)
                }

                BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE->{
                    binding.txtBatteryHeath.text = "Your battery is over voltage,battery health in danger"
                    binding.txtBatteryHeath.setTextColor(Color.YELLOW)
                    binding.imgHealth.setImageResource(R.drawable.warning)
                }

                else->{
                    binding.txtBatteryHeath.text = "Battery health unknown"
                    binding.txtBatteryHeath.setTextColor(Color.WHITE)
                    binding.imgHealth.setImageResource(R.drawable.unknown)
                }
            }

        }

    }

    private fun startService(){
        val intent = Intent(this,BatteryNotifications::class.java)
        ContextCompat.startForegroundService(this,intent)
    }

    private fun stopService(){
        val intent = Intent(this,BatteryNotifications::class.java)
        stopService(intent)
    }


    override fun onBackPressed() {
        binding.drawer.closeDrawer(GravityCompat.END)
    }
}