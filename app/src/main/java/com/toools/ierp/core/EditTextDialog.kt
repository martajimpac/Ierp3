package com.toools.ierp.core

import android.app.Activity
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.input
import com.toools.ierp.R

class EditTextDialog {

    companion object {

        private var mInstance = EditTextDialog()

        @Synchronized
        fun getInstance(): EditTextDialog {
            return mInstance
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