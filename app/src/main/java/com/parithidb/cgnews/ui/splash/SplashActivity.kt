package com.parithidb.cgnews.ui.splash

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.MediaController
import androidx.appcompat.app.AppCompatActivity
import com.parithidb.cgnews.ui.home.MainActivity
import com.parithidb.cgnews.R
import com.parithidb.cgnews.ui.login.LoginActivity
import com.parithidb.cgnews.databinding.ActivitySplashBinding
import com.parithidb.cgnews.util.SharedPrefHelper

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private lateinit var sharedPref: SharedPrefHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPref = SharedPrefHelper(this)
        // Play the raw resource
        val videoUri = Uri.parse("android.resource://${packageName}/${R.raw.splash_video}")
        binding.videoView.setVideoURI(videoUri)
        binding.videoView.setMediaController(null)
        binding.videoView.start()

        // in case video cannot play (device weirdness), fallback after small delay:
        binding.videoView.postDelayed({ if (!isFinishing) routeAfterSplash() }, 250)
    }

    private fun routeAfterSplash() {
        val userMail = sharedPref.getUserEmail()
        if (userMail != null) {
            // User signed in -> go to main/home
            startActivity(Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
        } else {
            // Not signed in -> go to login
            startActivity(Intent(this, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
        }
        finish()
    }
}
