package com.dreamstudio.goodbye_kobe

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings

class MainActivity : AppCompatActivity() {

    private lateinit var remoteConfig : FirebaseRemoteConfig

    override fun onCreate(savedInstanceState: Bundle?) {
        FirebaseApp.initializeApp(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val image = findViewById<ImageView>(R.id.image)
        val textView = findViewById<TextView>(R.id.link)
        remoteConfig = FirebaseRemoteConfig.getInstance()

        setupRemoteConfig(image)
        setupHyperlink(textView)
    }

    private fun setupRemoteConfig(image: ImageView) {
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
            image.setImageResource(R.drawable.kobe_2)
        }
    }

    private fun setupHyperlink(view: View) {
        val textView = view.findViewById<TextView>(R.id.link)
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
