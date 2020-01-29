package com.dreamstudio.goodbye_kobe

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings

class MainActivity : AppCompatActivity() {

    private lateinit var remoteConfig : FirebaseRemoteConfig
    private lateinit var imageView: ImageView
    private lateinit var textView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textView = findViewById(R.id.link)
        imageView = findViewById(R.id.image)
        remoteConfig = FirebaseRemoteConfig.getInstance()

        setupRemoteConfig()
        setupHyperlink()
    }

    private fun setupRemoteConfig() {
        remoteConfig.setConfigSettingsAsync(
            FirebaseRemoteConfigSettings.Builder().setFetchTimeoutInSeconds(3600L).build()
        )

        //override debug mode fetch time to be 0
        remoteConfig.info.configSettings.toBuilder().minimumFetchIntervalInSeconds = 0
        val fetch = remoteConfig.fetch()

        //if first time fetch successfully, do not thing to UI
        fetch.addOnCompleteListener {
            if (it.isSuccessful) {
                remoteConfig.activate()
            }
        }

        //use cached remote data to set up ui, no UI changes till next clean launch after app is killed
        if (remoteConfig.getBoolean("image_config")) {
            imageView.setImageResource(R.drawable.kobe_2)
        } else{
            imageView.setImageResource(R.drawable.kobe_1)
        }
    }

    private fun setupHyperlink() {
        val text =
            "<a href='http://kvbff.org/about/'> The Kobe and Vanessa Bryant Family Foundation </a>"
        textView.text = Html.fromHtml(text)

        textView.setOnClickListener {
            FirebaseAnalytics.getInstance(this).logEvent("link_clicked", null)
            val browserIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse("http://kvbff.org/about/"))
            startActivity(browserIntent)
        }
    }
}
