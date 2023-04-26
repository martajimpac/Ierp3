package com.toools.ierp.core

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Build
import android.text.Html
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import java.util.*

fun Fragment.hideKeyboard() {

    view?.let {

        activity?.hideKeyboard(it)
    }
}

fun Activity.hideKeyboard() {

    if (currentFocus == null) View(this) else currentFocus?.let { hideKeyboard(it) }
}

fun Context.hideKeyboard(view: View) {

    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

val Context.prefs: SharedPreferences
    get() = getSharedPreferences(packageName, Context.MODE_PRIVATE)

fun Context.getString(name: String): String {
    return resources.getString(resources.getIdentifier(name, "string", packageName))
}

@Suppress("DEPRECATION")
fun String.htmlFormatted(): String {

    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
        Html.fromHtml(this.replace("&amp;","&"), Html.FROM_HTML_MODE_LEGACY).toString()
    else
        Html.fromHtml(this.replace("&amp;","&")).toString()
}

fun String.capitalizeEachWord(): String {

    return this.split(' ').joinToString(" ") {

        if (!it.contains(".") && !it.contains("'"))
            it.lowercase(Locale.ROOT)
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
        else
            it.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
    }
}

fun String.normalizeCommas(): String {

    return this.replace(",",", ").replace(",  ",", ")
}

fun ConstraintSet.match(view: View, parentView: View) {

    this.connect(view.id, ConstraintSet.TOP, parentView.id, ConstraintSet.TOP)
    this.connect(view.id, ConstraintSet.BOTTOM, parentView.id, ConstraintSet.BOTTOM)
    this.connect(view.id, ConstraintSet.START, parentView.id, ConstraintSet.START)
    this.connect(view.id, ConstraintSet.END, parentView.id, ConstraintSet.END)
}

class doAsync(val handler: () -> Unit) : AsyncTask<Void, Void, Void>() {

    override fun doInBackground(vararg params: Void?): Void? {
        handler()
        return null
    }
}

fun View.showKeyboard() {
    this.requestFocus()
    val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
}

fun View.hideKeyboard() {
    val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
}