package com.toools.ierp.core

import android.app.Activity
import android.graphics.PorterDuff
import android.util.Log
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.afollestad.materialdialogs.input.input
import com.toools.ierp.R
import java.lang.Exception

class DialogHelper {

    companion object {

        private var mInstance = DialogHelper()

        @Synchronized
        fun getInstance(): DialogHelper {

            return mInstance
        }
    }

    private var loadingDialog: MaterialDialog? = null
    fun showLoadingAlert(activity: Activity, title: Any? = R.string.loading, show: Boolean) {

        if (loadingDialog == null)
            loadingDialog = MaterialDialog(activity)

        if (show) {

            loadingDialog?.show {

                cancelable(false)
                cancelOnTouchOutside(false)
                (title as? String)?.let { tit ->

                    title(text = tit)
                } ?: (title as? Int)?.let { tit ->

                    title(tit)
                }
                try {
                    customView(R.layout.dialog_loading, scrollable = false)
                }catch (e : Exception){
                    Log.e("error dialog: ", e.toString())
                }
            }
        } else {

            loadingDialog?.dismiss()
            loadingDialog = null
        }
    }

    fun showOKAlert(activity: Activity, title: Any? = null, text: Any, icon: Int = R.drawable.ic_toools, completion: (() -> Unit)? = null) {

        showThreeButtonsAlert(activity, title, text, icon, R.string.ok, completion, null, null, null, null)
    }

    fun showTwoButtonsAlert(activity: Activity, title: Any? = null, text: Any, icon: Int = R.drawable.ic_toools, button1: Any, completion1: (() -> Unit)? = null, button2: Any?, completion2: (() -> Unit)? = null) {

        showThreeButtonsAlert(activity, title, text, icon, button1, completion1, button2, completion2, null, null)
    }

    fun showThreeButtonsAlert(activity: Activity, title: Any? = null, text: Any, icon: Int = R.drawable.ic_toools, button1: Any, completion1: (() -> Unit)? = null, button2: Any?, completion2: (() -> Unit)? = null, button3: Any?, completion3: (() -> Unit)? = null) {

        MaterialDialog(activity).show {

            cancelable(false)
            cancelOnTouchOutside(false)
            (title as? String)?.let { tit ->

                title(text = tit)
            } ?: (title as? Int)?.let { tit ->

                title(tit)
            }
            (text as? String)?.let { txt ->

                message(text = txt)
            } ?: (text as? Int)?.let { txt ->

                message(txt)
            }

            AppCompatResources.getDrawable(activity, icon)?.let { drawable ->

                val draw = drawable.mutate()
                draw.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(ContextCompat.getColor(context, R.color.colorPrimary), BlendModeCompat.SRC_ATOP)
                icon(drawable = draw)
            } ?: icon(icon)

            (button1 as? String)?.let { txt ->

                positiveButton(text = txt)
            } ?: (button1 as? Int)?.let { txt ->

                positiveButton(txt)
            }
            (button2 as? String)?.let { txt ->

                negativeButton(text = txt)
            } ?: (button2 as? Int)?.let { txt ->

                negativeButton(txt)
            }
            (button3 as? String)?.let { txt ->

                @Suppress("DEPRECATION")
                neutralButton(text = txt)
            } ?: (button3 as? Int)?.let { txt ->

                @Suppress("DEPRECATION")
                neutralButton(txt)
            }
            completion1?.let { completion ->

                positiveButton {

                    completion()
                }
            }
            completion2?.let { completion ->

                negativeButton {

                    completion()
                }
            }
            completion3?.let { completion ->

                @Suppress("DEPRECATION")
                neutralButton{

                    completion()
                }
            }
        }
    }

    fun showEditTextAlert(activity: Activity, title: Any, text: Any, icon: Int?, button1: Any = R.string.next, completion: EditTextDialogListener, button2: Any = R.string.cancel, hint: Any, initialValue: String? = null) {

        MaterialDialog(activity).show {

            cancelable(false)
            cancelOnTouchOutside(false)
            (title as? String)?.let { tit ->

                title(text = tit)
            } ?: (title as? Int)?.let { tit ->

                title(tit)
            }
            (text as? String)?.let { txt ->

                message(text = txt)
            } ?: (text as? Int)?.let { txt ->

                message(txt)
            }
            icon?.let { ic ->

                icon(ic)
            }
            (button1 as? String)?.let { txt ->

                positiveButton(text = txt)
            } ?: (button1 as? Int)?.let { txt ->

                positiveButton(txt)
            }
            (button2 as? String)?.let { txt ->

                negativeButton(text = txt)
            } ?: (button2 as? Int)?.let { txt ->

                negativeButton(txt)
            }
            (hint as? String)?.let {

                input(hint = it, waitForPositiveButton = true, prefill = initialValue ?: "") { _, text ->

                    completion.editTextDialogDismissed(text.toString())
                }
            } ?: (hint as? Int)?.let {

                input(hintRes = it, waitForPositiveButton = true, prefill = initialValue ?: "") { _, text ->

                    completion.editTextDialogDismissed(text.toString())
                }
            } ?: kotlin.run {

                input(waitForPositiveButton = true, prefill = initialValue ?: "") { _, text ->

                    completion.editTextDialogDismissed(text.toString())
                }
            }
        }
    }
}

interface EditTextDialogListener {
    fun editTextDialogDismissed(value: String)
}