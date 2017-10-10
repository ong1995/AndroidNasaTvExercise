package co.blackfintech.nasatv.activities

import android.os.Bundle
import co.blackfintech.nasatv.R
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.util.Log
import kotlinx.android.synthetic.main.activity_stream.*

/**
 * Created by User on 10/10/2017.
 */
class StreamActivity : AppCompatActivity() {

    companion object {
        var STREAM_LINK: String = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stream)

        println(STREAM_LINK)
        bindView()
    }

    private fun bindView() {
        nasaStreamVideo.setZOrderOnTop(false);
        nasaStreamVideo.setOnPreparedListener { mp ->
            mp.start()
        }
        val videoUrl: Uri = Uri.parse(intent.getStringExtra(STREAM_LINK))
        nasaStreamVideo.setVideoURI(videoUrl)
    }
}