package com.toools.tooolsdialog

import android.app.Activity
import android.graphics.PorterDuff
import android.graphics.Typeface
import android.graphics.fonts.Font
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView

//Esto es la clase con el enlace simbolico.

class DialogHelper {

    var imgDefault : Int? = null
    var isIniciado : Boolean = false

    var backgroundColor : Int? = null
    var iconThinColor : Int? = null
    var iconBackgroundColor : Int? = null
    var textColor : Int? = null
    var buttonColor: Int? = null
    var buttonTextColor: Int? = null

    var titleFont: Typeface? = null
    var descriptionFont: Typeface? = null
    var buttonFont: Typeface? = null

    var isLoadingBlack: Boolean? = false

    companion object {

        private var mInstance = DialogHelper()

        @Synchronized
        fun getInstance(): DialogHelper {
            return mInstance
        }
    }

    fun initDefaultValues(imgDefault : Int, backgroundColor : Int, iconThinColor : Int?, iconBackgroundColor : Int, textColor : Int, buttonColor : Int,
                          titleFont: Typeface? = null, descriptionFont: Typeface? = null, buttonFont: Typeface? = null, isLoadingBlack: Boolean? = false, buttonTextColor : Int? = null){
        this.imgDefault = imgDefault
        this.backgroundColor = backgroundColor
        this.iconThinColor = iconThinColor
        this.iconBackgroundColor = iconBackgroundColor
        this.textColor = textColor
        this.buttonColor = buttonColor

        this.titleFont = titleFont
        this.descriptionFont = descriptionFont
        this.buttonFont = buttonFont
        this.buttonTextColor = buttonTextColor

        this.isLoadingBlack = isLoadingBlack

        this.isIniciado = true
    }

    private var loadingDialog: LoadingDialogFragment? = null
    private var isLoadingShow = false
    fun showLoadingAlert(activity: AppCompatActivity, title: Any? = R.string.loading, show: Boolean) {

        if (loadingDialog == null) {
            loadingDialog = LoadingDialogFragment(
                imgDefault,
                backgroundColor,
                iconThinColor,
                iconBackgroundColor,
                textColor,
                titleFont,
                isLoadingBlack
            )
        }

        if (show) {
            if (!isLoadingShow) {
                if (loadingDialog?.isVisible == true) {
                    loadingDialog?.dismiss()
                }
                loadingDialog?.setTitle(title)
                loadingDialog?.show(activity.supportFragmentManager, "loadingFragment")
                isLoadingShow = true
            }
        } else {

            if (activity.supportFragmentManager.findFragmentByTag("loadingFragment") != null) {
                loadingDialog?.dismiss()
            }
            isLoadingShow = false
            loadingDialog = null
        }

    }

    fun showOKAlert(activity: AppCompatActivity, title: Any? = null, text: Any, icon: Int? = imgDefault, button1: Any = R.string.ok, completion: (() -> Unit)? = null) {
        val dialog = NewDialogFragment(imgDefault, backgroundColor, iconThinColor, iconBackgroundColor, textColor, buttonColor, titleFont, descriptionFont, buttonFont, buttonTextColor)
        dialog.showOneButtons(title = title, text = text, icon = icon, button1 = button1, completion = completion)
        dialog.show(activity.supportFragmentManager, "oneButtonsAlert")
    }

    fun showOKScrollAlert(activity: AppCompatActivity, title: Any? = null, text: Any, icon: Int? = imgDefault, button1: Any = R.string.ok, completion: (() -> Unit)? = null) {
        val dialog = NewDialogScrollFragment(imgDefault, backgroundColor, iconThinColor, iconBackgroundColor, textColor, buttonColor, titleFont, descriptionFont, buttonFont, buttonTextColor)
        dialog.showOneButtons(title = title, text = text, icon = icon, button1 = button1, completion = completion)
        dialog.show(activity.supportFragmentManager, "oneButtonsAlert")
    }

    fun showTwoButtonsAlert(activity: AppCompatActivity, title: Any? = null, text: Any, icon: Int? = imgDefault, button1: Any, completion1: (() -> Unit)? = null, button2: Any?, completion2: (() -> Unit)? = null) {
        val dialog = NewDialogFragment(imgDefault, backgroundColor, iconThinColor, iconBackgroundColor, textColor, buttonColor, titleFont, descriptionFont, buttonFont, buttonTextColor)
        dialog.showTwoButtons(title = title, text = text, icon = icon, button1 = button1, completion1 = completion1, button2 = button2, completion2 = completion2)
        dialog.show(activity.supportFragmentManager, "threeButtonsAlert")
    }

    fun showThreeButtonsAlert(activity: AppCompatActivity, title: Any? = null, text: Any, icon: Int? = imgDefault, button1: Any, completion1: (() -> Unit)? = null, button2: Any?, completion2: (() -> Unit)? = null, button3: Any?, completion3: (() -> Unit)? = null) {
        val dialog = NewDialogFragment(imgDefault, backgroundColor, iconThinColor, iconBackgroundColor, textColor, buttonColor, titleFont, descriptionFont, buttonFont, buttonTextColor)
        dialog.showThreeButtons(title = title, text = text, icon = icon, button1 = button1, completion1 = completion1, button2 = button2, completion2 = completion2, button3 = button3, completion3 = completion3)
        dialog.show(activity.supportFragmentManager, "threeButtonsAlert")
    }

}