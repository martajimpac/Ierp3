package com.toools.eurogames.utils

import android.content.Context
import com.google.gson.Gson
import com.toools.ierp.core.prefs
import com.toools.ierp.data.ConstantHelper
import com.toools.ierp.data.model.LoginResponse
import javax.inject.Inject

class SessionManager @Inject constructor(context: Context){

    /*
    private var prefs = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE)

    companion object{
        const val FirabaseToken = "LaLigaToken"
    }

    fun saveAuthToken(token: String) {
        val editor = prefs.edit()
        editor.putString(ConstantHelper, token)
        editor.apply()
    }

    fun fetchAuthToken(): String? {
        return prefs.getString(LaLigaToken, null)
    }*/
}
