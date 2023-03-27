package com.toools.ierp.ui.base

/*
import android.content.Context
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.toools.ierp.R

abstract class BaseFragment: Fragment() {

    override fun onResume() {

        super.onResume()

    }

    private var lottieLoadingContainerView: RelativeLayout? = null
    fun showLottieLoading(context: Context, containerView: ViewGroup, show: Boolean = true) {

        if (show && lottieLoadingContainerView == null) {

            lottieLoadingContainerView = RelativeLayout(context)
            lottieLoadingContainerView?.setBackgroundColor(ContextCompat.getColor(context, R.color.transBlack))
            lottieLoadingContainerView?.isClickable = true
            lottieLoadingContainerView?.isFocusable = true

            val lottieLoadingView = LottieAnimationView(context)
            lottieLoadingView.setAnimation(R.raw.loading)
            lottieLoadingView.speed = 2.5f
            lottieLoadingView.repeatCount = 9999

            val params = RelativeLayout.LayoutParams(context.resources.getDimensionPixelSize(R.dimen.lottie_loading_size), context.resources.getDimensionPixelSize(R.dimen.lottie_loading_size))
            params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE)

            lottieLoadingContainerView?.addView(lottieLoadingView)

            lottieLoadingView.layoutParams = params

            containerView.addView(lottieLoadingContainerView)

            when (containerView) {

                is ConstraintLayout -> lottieLoadingContainerView?.layoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT)
                is RelativeLayout -> lottieLoadingContainerView?.layoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT)
                else -> throw IllegalArgumentException("containerView no es un ConstraintLayout ni RelativeLayout!!!!")
            }

            lottieLoadingView.playAnimation()
        } else if (!show && lottieLoadingContainerView != null) {

            lottieLoadingContainerView?.let { lottieContainerView ->

                lottieContainerView.removeAllViews()
                containerView.removeView(lottieContainerView)
                lottieLoadingContainerView = null
            }
        }
    }
}*/