package com.example.splash

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.Path
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.view.animation.AnticipateInterpolator
import android.window.SplashScreenView
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.animation.doOnEnd
import androidx.core.os.BuildCompat
import com.example.otpviewer.InitViewModel


class InitActivity : AppCompatActivity() {

    private val handler = Handler(Looper.getMainLooper())
    private val viewModel: InitViewModel by viewModels()
    private val jumpRunnable = { goToMainScreen() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        /*//region old way to launch activity
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
        //endregion*/

        customizeSplashScreenExit()
        keepSplashScreenLonger()
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacksAndMessages(null)
    }

    private fun goToMainScreenDelayed() {
        handler.postDelayed(jumpRunnable, 1500)
    }

    private fun goToMainScreen() {
        Intent(this, MainActivity::class.java).also { startActivity(it) }
        finish()
    }

    private fun keepSplashScreenLonger() {
        val content: View = findViewById(android.R.id.content)
        content.viewTreeObserver.addOnPreDrawListener(
            object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    return if (viewModel.isDataReady()) {
                        content.viewTreeObserver.removeOnPreDrawListener(this)
                        goToMainScreenDelayed()
                        true
                    } else {
                        false
                    }
                }
            }
        )
    }


    private fun customizeSplashScreenExit() {
        if (!BuildCompat.isAtLeastS()) {
            return
        }

        splashScreen.setOnExitAnimationListener { splashScreenView ->

            /** Exit immediately **/
//            splashScreenView.remove()

            /**  Standard exit animator**/
//            sleep(1000)
//            splashScreenView.remove()

            /**  Customize exit animator **/
            showSplashExitAnimator(splashScreenView)
//            showSplashIconExitAnimator(splashScreenView)
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun showSplashExitAnimator(splashScreenView: SplashScreenView) {

        // Create your custom animation set.
        val slideUp = ObjectAnimator.ofFloat(
            splashScreenView,
            View.TRANSLATION_Y,
            0f,
            -splashScreenView.height.toFloat()
        )
        val slideLeft = ObjectAnimator.ofFloat(
            splashScreenView,
            View.TRANSLATION_X,
            0f,
            -splashScreenView.width.toFloat()
        )

        val scaleXOut = ObjectAnimator.ofFloat(
            splashScreenView,
            View.SCALE_X,
            1.0f,
            0f
        )

        val path = Path()
        path.moveTo(1.0f, 1.0f)
        path.lineTo(0f, 0f)
        val scaleOut = ObjectAnimator.ofFloat(
            splashScreenView,
            View.SCALE_X,
            View.SCALE_Y,
            path
        )

        val alphaOut = ObjectAnimator.ofFloat(
            splashScreenView,
            View.ALPHA,
            1f,
            0f
        )

        val animatorSet = AnimatorSet()
        animatorSet.duration = resources.getInteger(R.integer.splash_exit_total_duration).toLong()
        animatorSet.interpolator = AnticipateInterpolator()

        animatorSet.playTogether(scaleOut)
        animatorSet.playTogether(slideUp)
        animatorSet.playTogether(slideUp, scaleXOut)
        animatorSet.playTogether(slideUp, scaleOut)
        animatorSet.playTogether(slideUp, slideLeft)
        animatorSet.playTogether(slideUp, slideLeft, scaleOut)
        animatorSet.playTogether(slideUp, slideLeft, scaleOut, alphaOut)

        animatorSet.doOnEnd {
            Log.d("Splash", "SplashScreen#remove when animator done")
            // splashScreenView.setBackgroundColor(android.graphics.Color.BLUE)
            splashScreenView.remove()
        }
        animatorSet.start()
    }



}