package com.farzin.batterymanager.helper

import android.content.Context
import android.content.SharedPreferences

class SPManager {


    companion object{
        private var sharedPreferences:SharedPreferences? = null
        private var editor:SharedPreferences.Editor? = null
        private var flag = "SP_BOOLEAN"
        private var isServiceOn = "isServiceOn"

        fun isServiceOn (context: Context):Boolean{
            sharedPreferences = context.getSharedPreferences(flag,Context.MODE_PRIVATE)
            return sharedPreferences?.getBoolean(isServiceOn,false)!!
        }

        fun setServiceState (context: Context, isOn:Boolean){
            sharedPreferences = context.getSharedPreferences(flag,Context.MODE_PRIVATE)
            editor = sharedPreferences?.edit()
            editor?.putBoolean(isServiceOn,isOn)
            editor?.apply()
        }

    }



}