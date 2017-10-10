package co.blackfintech.nasatv.activities

import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.animation.AccelerateInterpolator
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.DecelerateInterpolator
import co.blackfintech.nasatv.R
import co.blackfintech.nasatv.apiclient.ChannelService
import co.blackfintech.nasatv.apiclient.entities.Channel
import co.blackfintech.nasatv.apiclient.entities.ChannelResponse
import co.blackfintech.nasatv.apiclient.events.ApiRequestEvent
import co.blackfintech.nasatv.apiclient.events.ApiResponseEvent
import kotlinx.android.synthetic.main.activity_loading.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.toast
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


////////////////////////////////////////////////////////////////////////////////////////////////////
class LoadingActivity : AppCompatActivity() {

    private var connectionStatus = ConnectionStatus.CONNECTING
//    6540154 10414700
    private val nasaChannelId = "6540154"
    private var channel: Channel? = Channel()

    private val retrofit by lazy {
        Retrofit.Builder().baseUrl("https://api.ustream.tv/").addConverterFactory(GsonConverterFactory.create()).build()
    }

    // region Lifecycle
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading)
        bindViews()
    }

    override fun onStart() {

        super.onStart()
        EventBus.getDefault().register(this@LoadingActivity)
        fetchDataFromApi()
    }

    override fun onResume() {

        super.onResume()
        initBackgroundVideo()
        initTapListener()
    }

    override fun onStop() {

        super.onStop()
        EventBus.getDefault().unregister(this@LoadingActivity)
    }
    //endregion

    private fun bindViews() {

        instructionsText.startAnimation(fadeAnimator())
    }

    private fun fetchDataFromApi() {

        EventBus.getDefault().post(ApiRequestEvent())
    }

    private fun initBackgroundVideo() {

//        loopVideo.setZOrderOnTop(false)
//        loopVideo.setOnPreparedListener { mp ->
//            mp.isLooping = true
//            mp.start()
//        }
//        val videoUri = Uri.parse("android.resource://$packageName/${R.raw.earth}")
//        loopVideo.setVideoURI(videoUri)
    }

    private fun initTapListener() {

        backgroundView.setOnClickListener {

            when (connectionStatus) {

                ConnectionStatus.CONNECTING -> {

                    toast("App is communicating with NASA servers, please wait.")
                }

                ConnectionStatus.OFF_AIR -> {

                    toast("NASA Channels are currently offline.\nTry again later.")
                }

                ConnectionStatus.LIVE -> {
                    toast("NASA Channels is live")
                    println(channel?.stream?.hls)
                    startActivity(intentFor<StreamActivity>(StreamActivity.STREAM_LINK to channel?.stream?.hls))
                    finish()
                }
            }
        }
    }

    private fun fadeAnimator(): AnimationSet {

        val fadeIn = AlphaAnimation(0f, 1f)
        fadeIn.interpolator = DecelerateInterpolator()
        fadeIn.duration = 300

        val fadeOut = AlphaAnimation(1f, 0f)
        fadeOut.interpolator = AccelerateInterpolator()
        fadeOut.startOffset = 2000
        fadeOut.duration = 300

        val animation = AnimationSet(false)
        animation.addAnimation(fadeIn)
        animation.addAnimation(fadeOut)
        animation.repeatCount = Animation.INFINITE

        return animation
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    fun onApiRequestEvent(request: ApiRequestEvent) {

        val channelService = retrofit.create(ChannelService::class.java)
        val channelCall = channelService.getChannels(nasaChannelId)
        val response: Response<ChannelResponse>?
        response = channelCall.execute()
        EventBus.getDefault().post(ApiResponseEvent(response.body()))

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onApiResponseEvent(response: ApiResponseEvent) {
        channel = response.channelResponse?.channel
        when (channel?.status) {
            "live" -> {
                println("live")
//                statusImage.setColorFilter(R.color.colorLive)
                statusImage.setColorFilter(ContextCompat.getColor(this, R.color.colorLive))
                statusText.setText(R.string.status_live)
                connectionStatus = ConnectionStatus.LIVE
            }

            "offair" -> {
                println("offair")
                statusImage.setColorFilter(ContextCompat.getColor(this, R.color.colorOffAir))
                statusText.setText(R.string.status_off_air)
                connectionStatus = ConnectionStatus.OFF_AIR
            }

            else -> {
                println("else")
            }
        }
    }
}

////////////////////////////////////////////////////////////////////////////////////////////////////
enum class ConnectionStatus {

    CONNECTING,
    OFF_AIR,
    LIVE
}
////////////////////////////////////////////////////////////////////////////////////////////////////
