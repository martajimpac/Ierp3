package com.toools.ierp.data.model

import com.google.gson.annotations.SerializedName
import java.util.*
import kotlin.collections.ArrayList

data class ISpotClientDataResponse(val status: String?, val error: String?, @SerializedName("attributes") val menuItems: List<ISpotClientDataMenuItem>) {

    fun isOK(): Boolean {

        return status?.lowercase(Locale.ROOT) == "ok"
    }

    fun isLive(): Boolean {
        return menuItems.isNotEmpty() && menuItems.any { item -> item.type == "2" }
    }

    fun lateralMenuItems(): List<ISpotClientDataMenuItem> {

        val items = ArrayList<ISpotClientDataMenuItem>()
        menuItems.let {

            for (item in menuItems) {

                if (item.type == "1" || item.type == "3")
                    items.add(item)
            }
        }
        return items
    }
}

data class ISpotClientDataMenuItem(val type: String, var name: String, val value: String?) {

    var header = false
    var bottomVisible = true
}