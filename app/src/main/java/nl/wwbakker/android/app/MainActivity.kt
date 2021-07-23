package nl.wwbakker.android.app

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import nl.wwbakker.android.app.usercontrol.SensorControl


class MainActivity : AppCompatActivity() {
    lateinit var glView : MyView
    lateinit var mControlsView : View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)

        glView = MyView(this, lifecycle)
        setContentView(glView)
        mControlsView = window.decorView

        val uiOptions =
            (View.SYSTEM_UI_FLAG_LOW_PROFILE
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)

//        window.insetsController?.hide(WindowInsets.Type.statusBars())
//        window.insetsController?.hide(WindowInsets.Type.systemBars())
//        window.insetsController?.

        mControlsView.systemUiVisibility = uiOptions


    }


}