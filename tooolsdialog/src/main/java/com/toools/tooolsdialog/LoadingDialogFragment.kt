package com.toools.tooolsdialog

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.toools.tooolsdialog.databinding.DialogLoadingBinding


class LoadingDialogFragment(private val imgDefault : Int?, private val  backgroundColor : Int?,
                            private val iconThinColor : Int?, private val iconBackgroundColor : Int?,
                            private val textColor : Int?, private val titleFont: Typeface?, private val isLoadingBlack: Boolean?): DialogFragment() {

    private var act: Activity? = null
    private lateinit var binding: DialogLoadingBinding

    private var title: Any? = null

    override fun onCreateView(
        inflater: android.view.LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DialogLoadingBinding.inflate(inflater, container, false)
        return binding.root
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpView()
    }

    fun setUpView() {
        binding.apply {
            act?.let { context ->
                (title as? String)?.let {
                    tituloTextView.text = it
                }
                (title as? Int)?.let {
                    tituloTextView.text = context.getString(it)
                }

                titleFont?.let { font ->
                    tituloTextView.typeface = font
                }

                textColor?.let { color ->
                    tituloTextView.setTextColor(ContextCompat.getColor(context, color))
                }

                //loading
                if (isLoadingBlack == true) {
                    lottieAnimationView.setAnimation(R.raw.loading_black)
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
                imgDefault?.let { imagen ->
                    Glide.with(context.baseContext).load(imagen).into(dialogImageView)

                }
                iconThinColor?.let { color ->
                    dialogImageView.setColorFilter(ContextCompat.getColor(context, color), android.graphics.PorterDuff.Mode.SRC_IN)
                }
            }
        }
    }

    fun setTitle(title: Any?) {
        title?.let {
            this.title = title
        }
    }
}