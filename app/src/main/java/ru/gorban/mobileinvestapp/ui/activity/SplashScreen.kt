package ru.gorban.mobileinvestapp.ui.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import ru.gorban.mobileinvestapp.MainActivity
import ru.gorban.mobileinvestapp.databinding.ActivitySplashScreenBinding

class SplashScreen : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreenBinding
    private val SPLASH_TIME_OUT = 1000L


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        Handler(Looper.getMainLooper()).postDelayed(
            {
                startActivity(Intent(this@SplashScreen, MainActivity::class.java))
                finish()
            }, SPLASH_TIME_OUT
        )
    }
}