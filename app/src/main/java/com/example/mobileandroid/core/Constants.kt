package com.example.mobileandroid.core

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences

class Constants {
    var context: Context? = null
    var sharedPref: SharedPreferences? = null
    var sharedPrefEditor: SharedPreferences.Editor? = null
    fun configSessionUtils(context: Context) {
        this.context = context
        sharedPref = context.getSharedPreferences("AppPreferences", Activity.MODE_PRIVATE)
        sharedPrefEditor = sharedPref?.edit()
    }

    fun storeValueString(key: String?, value: String?) {
        sharedPrefEditor!!.putString(key, value)
        sharedPrefEditor!!.commit()
    }

    fun fetchValueString(key: String?): String? {
        return sharedPref!!.getString(key, null)
    }

    fun deleteValueString(key: String?) {
        sharedPref!!.edit().remove(key).apply()
    }

    companion object {
        var _instance: Constants? = null
        fun instance(context: Context): Constants? {
            if (_instance == null) {
                _instance = Constants()
                _instance!!.configSessionUtils(context)
            }
            return _instance
        }

        fun instance(): Constants? {
            return _instance
        }
    }
}