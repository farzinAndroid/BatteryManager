package com.farzin.batterymanager.adapter

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.farzin.batterymanager.databinding.RvItemBinding
import com.farzin.batterymanager.model.BatteryModel
import java.util.ArrayList
import kotlin.math.roundToInt

class BatteryAdapter(private val context: Context, private val batteryList: MutableList<BatteryModel>, private val totalTime:Long) : RecyclerView.Adapter<BatteryAdapter.MyViewHolder>() {

    var finalBatteryList:MutableList<BatteryModel> = ArrayList()

    init {
        finalBatteryList = sortedList(batteryList)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val layoutInflater = LayoutInflater.from(context)
        val  binding:RvItemBinding = RvItemBinding.inflate(layoutInflater,parent,false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {


        holder.binding.txtPercentage.text = finalBatteryList.get(position).percentUsage.toString() + "%"

        holder.binding.txtTime.text = finalBatteryList.get(position).timeUsage

        holder.binding.progressBar.progress = finalBatteryList[position].percentUsage

        holder.binding.txtAppName.text = getAppName(finalBatteryList[position].packageName.toString())

        holder.binding.appImage.setImageDrawable(getAppIcon(finalBatteryList[position].packageName.toString()))



    }

    override fun getItemCount(): Int {
       return finalBatteryList.size
    }

    private fun sortedList(batteryPercentageArray:MutableList<BatteryModel>) : MutableList<BatteryModel>{

        val finalList:MutableList<BatteryModel> = ArrayList()

        val sortedList = batteryPercentageArray
            .groupBy { it.packageName }
            .mapValues { entry -> entry.value.sumBy { it.percentUsage } }.toList()
            .sortedWith(compareBy { it.second }).reversed()


        for (item in sortedList) {
            val bm = BatteryModel()
            val timePerApp =
                item.second.toFloat() / 100 * totalTime.toFloat() / 1000 / 60
            val hour = timePerApp / 60
            val min = timePerApp % 60
            bm.packageName = item.first
            bm.percentUsage = item.second
            bm.timeUsage = "${hour.roundToInt()} hour ${min.roundToInt()} minutes"
            finalList += bm
        }
        return finalList
    }

    private fun getAppName(packageName:String):String{

        val pm = context.applicationContext.packageManager
        val ai :ApplicationInfo? = try {
            pm.getApplicationInfo(packageName,0)
        }catch (e:PackageManager.NameNotFoundException){
            null
        }
        return (if (ai != null) pm.getApplicationLabel(ai).toString() else "(unknown)") //as String
    }

    private fun getAppIcon(packageName: String):Drawable?{

        var icon:Drawable? = null

        try {
            icon = context.applicationContext.packageManager.getApplicationIcon(packageName)
        }catch (e:PackageManager.NameNotFoundException){
            e.printStackTrace()
        }
        return icon
    }











    class MyViewHolder(val binding: RvItemBinding) : RecyclerView.ViewHolder(binding.root) {

    }


}