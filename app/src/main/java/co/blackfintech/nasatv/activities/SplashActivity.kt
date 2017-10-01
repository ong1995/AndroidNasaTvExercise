package co.blackfintech.nasatv.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import co.blackfintech.nasatv.R
import org.jetbrains.anko.startActivity

////////////////////////////////////////////////////////////////////////////////////////////////////
class SplashActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {

    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_splash)

    Handler().postDelayed({

      startActivity<LoadingActivity>()
      finish()
    }, 1500)
  }
}
////////////////////////////////////////////////////////////////////////////////////////////////////
