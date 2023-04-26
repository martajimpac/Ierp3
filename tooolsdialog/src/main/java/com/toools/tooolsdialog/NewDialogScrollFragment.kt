package com.toools.tooolsdialog

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.graphics.fonts.Font
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.toools.tooolsdialog.databinding.DialogBaseBinding
import com.toools.tooolsdialog.databinding.DialogBaseScrollBinding

class NewDialogScrollFragment(private val imgDefault : Int?, private val  backgroundColor : Int?,
                        private val iconThinColor : Int?, private val iconBackgroundColor : Int?, private val textColor : Int?,
                        private val buttonColor : Int?, private val titleFont: Typeface?, private val descriptionFont: Typeface?,
                        private val buttonFont: Typeface?, private val buttonTextColor: Int?): DialogFragment() {

    private var act: Activity? = null
    private lateinit var binding: DialogBaseScrollBinding

    private var title: Any? = null
    private var text: Any? = null
    private var icon: Int? = null
    private var completion1: (() -> Unit)? = null
    private var completion2: (() -> Unit)? = null
    private var completion3: (() -> Unit)? = null
    private var button1: Any? = null
    private var button2: Any? = null
    private var button3: Any? = null
    private var typeDialog = TypeDialog.one

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogBaseScrollBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpView()
    }

    override fun onStart() {
        super.onStart()
        val dialog: Dialog? = dialog
        if (dialog != null) {
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window?.setLayout(width, height)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity)
            act = context
    }

    private fun setUpView() {
        binding.apply {
            act?.let { context ->
                oneButtonCardView.visibility = View.GONE
                twoButtonsConstraintLayout.visibility = View.GONE
                threeButtonsConstraintLayout.visibility = View.GONE

                //dependiendo del tipo mostrar uno u otro
                when (typeDialog) {
                    TypeDialog.one -> {
                        oneButtonCardView.visibility = View.VISIBLE
                    }
                    TypeDialog.two -> {
                        twoButtonsConstraintLayout.visibility = View.VISIBLE
                    }
                    TypeDialog.threee -> {
                        threeButtonsConstraintLayout.visibility = View.VISIBLE
                    }
                }

                //titulo
                (title as? String)?.let {
                    tituloTextView.text = it
                }
                (title as? Int)?.let {
                    tituloTextView.text = context.getString(it)
                }

                titleFont?.let { font ->
                    tituloTextView.typeface = font
                }

                //Description
                (text as? String)?.let {
                    descripcionTextView.text = it
                }
                (text as? Int)?.let {
                    descripcionTextView.text = context.getString(it)
                }

                descriptionFont?.let { font ->
                    descripcionTextView.typeface = font
                }


                textColor?.let { color ->
                    tituloTextView.setTextColor(ContextCompat.getColor(context, color))
                    descripcionTextView.setTextColor(ContextCompat.getColor(context, color))
                    oneButtonTextView.setTextColor(ContextCompat.getColor(context, color))
                    twoButtonSuccessTextView.setTextColor(ContextCompat.getColor(context, color))
                    twoButtonCancelTextView.setTextColor(ContextCompat.getColor(context, color))
                    threeButtonTopTextView.setTextColor(ContextCompat.getColor(context, color))
                    threeButtonMiddleTextView.setTextColor(ContextCompat.getColor(context, color))
                    threeButtonBottomTextView.setTextColor(ContextCompat.getColor(context, color))
                }

                buttonColor?.let {color ->
                    oneButtonCardView.setCardBackgroundColor(ContextCompat.getColor(context, color))
                    twoButtonCancelCardView.setCardBackgroundColor(ContextCompat.getColor(context, color))
                    twoButtonSuccessCardView.setCardBackgroundColor(ContextCompat.getColor(context, color))
                    threeButtonBottomCardView.setCardBackgroundColor(ContextCompat.getColor(context, color))
                    threeButtonTopCardView.setCardBackgroundColor(ContextCompat.getColor(context, color))
                    threeButtonMiddleCardView.setCardBackgroundColor(ContextCompat.getColor(context, color))
                }

                buttonFont?.let { font ->
                    oneButtonTextView.typeface = font
                    twoButtonSuccessTextView.typeface = font
                    twoButtonCancelTextView.typeface = font
                    threeButtonTopTextView.typeface = font
                    threeButtonMiddleTextView.typeface = font
                    threeButtonBottomTextView.typeface = font
                }

                buttonTextColor?.let { textColor ->
                    oneButtonTextView.setTextColor(ContextCompat.getColor(context, textColor))
                    twoButtonSuccessTextView.setTextColor(ContextCompat.getColor(context, textColor))
                    twoButtonCancelTextView.setTextColor(ContextCompat.getColor(context, textColor))
                    threeButtonTopTextView.setTextColor(ContextCompat.getColor(context, textColor))
                    threeButtonMiddleTextView.setTextColor(ContextCompat.getColor(context, textColor))
                    threeButtonBottomTextView.setTextColor(ContextCompat.getColor(context, textColor))
                }

                //background
                backgroundColor?.let { color ->
                    baseCardView.setCardBackgroundColor(ContextCompat.getColor(context, color))
                }

                //iconBackgroundColor
                iconBackgroundColor?.let { color ->
                    iconCardView.setCardBackgroundColor(ContextCompat.getColor(context, color))
                }

                //iconImage
                icon?.let {imagen ->
                    Glide.with(context.baseContext).load(imagen).into(dialogImageView)
                } ?: run{
                    imgDefault?.let { imagen ->
                        Glide.with(context.baseContext).load(imagen).into(dialogImageView)
                    }
                }

                iconThinColor?.let { color ->
                    dialogImageView.setColorFilter(ContextCompat.getColor(context, color), android.graphics.PorterDuff.Mode.SRC_IN)
                }

                //button1
                (button1 as? String)?.let {
                    oneButtonTextView.text = it
                    twoButtonSuccessTextView.text = it
                    threeButtonTopTextView.text = it
                }
                (button1 as? Int)?.let {
                    oneButtonTextView.text = context.getString(it)
                    twoButtonSuccessTextView.text = context.getString(it)
                    threeButtonTopTextView.text = context.getString(it)
                }

                //button2
                (button2 as? String)?.let {
                    twoButtonCancelTextView.text = it
                    threeButtonMiddleTextView.text = it
                }
                (button2 as? Int)?.let {
                    twoButtonCancelTextView.text = context.getString(it)
                    threeButtonMiddleTextView.text = context.getString(it)
                }

                //button3
                (button3 as? String)?.let {
                    threeButtonBottomTextView.text = it
                }
                (button3 as? Int)?.let {
                    threeButtonBottomTextView.text = context.getString(it)
                }

                //completion1
                oneButtonCardView.setOnClickListener {
                    completion1?.let { it1 -> it1() }
                    dismiss()
                }
                twoButtonSuccessCardView.setOnClickListener {
                    completion1?.let { it1 -> it1() }
                    dismiss()
                }
                threeButtonTopCardView.setOnClickListener {
                    completion1?.let { it1 -> it1() }
                    dismiss()
                }

                //completion2
                twoButtonCancelCardView.setOnClickListener {
                    completion2?.let { it1 -> it1() }
                    dismiss()
                }
                threeButtonMiddleCardView.setOnClickListener {
                    completion2?.let { it1 -> it1() }
                    dismiss()
                }

                //completion3
                threeButtonBottomCardView.setOnClickListener {
                    completion3?.let { it1 -> it1() }
                    dismiss()
                }
            }
        }
    }

    fun showOneButtons(title: Any? = null, text: Any, icon: Int? = imgDefault, button1: Any, completion: (() -> Unit)? = null) {
        typeDialog = TypeDialog.one
        loadData(title = title, text = text, icon = icon, button1 = button1, completion1 = completion)
    }

    fun showTwoButtons(title: Any? = null, text: Any, icon: Int? = imgDefault, button1: Any, completion1: (() -> Unit)? = null, button2: Any?, completion2: (() -> Unit)? = null) {
        typeDialog = TypeDialog.two
        loadData(title = title, text = text, icon = icon, button1 = button1, completion1 = completion1, button2 = button2, completion2 = completion2)
    }

    fun showThreeButtons(title: Any? = null, text: Any, icon: Int? = imgDefault, button1: Any, completion1: (() -> Unit)? = null, button2: Any?, completion2: (() -> Unit)? = null, button3: Any?, completion3: (() -> Unit)? = null) {
        typeDialog = TypeDialog.threee
        loadData(title = title, text = text, icon = icon, button1 = button1, completion1 = completion1, button2 = button2, completion2 = completion2, button3 = button3, completion3 = completion3)
    }

    fun loadData(title: Any? = null, text: Any, icon: Int? = imgDefault, button1: Any, completion1: (() -> Unit)? = null, button2: Any? = null, completion2: (() -> Unit)? = null, button3: Any? = null, completion3: (() -> Unit)? = null) {
        this.title = title
        this.text = text
        this.icon = icon
        this.button1 = button1
        this.completion1 = completion1
        this.button2 = button2
        this.completion2 = completion2
        this.button3 = button3
        this.completion3 = completion3
    }
}