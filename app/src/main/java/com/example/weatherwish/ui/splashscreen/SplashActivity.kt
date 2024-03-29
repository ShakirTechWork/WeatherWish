package com.example.weatherwish.ui.splashscreen

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.weatherwish.Application
import com.example.weatherwish.MainActivity
import com.example.weatherwish.R
import com.example.weatherwish.exceptionHandler.ExceptionHandler
import com.example.weatherwish.firebase.FirebaseResponse
import com.example.weatherwish.ui.signIn.SignInActivity
import com.example.weatherwish.utils.Utils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class SplashActivity : AppCompatActivity() {

    private lateinit var splashViewModel: SplashViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val repository = (application as Application).appRepository

        splashViewModel =
            ViewModelProvider(this, SplashViewModelFactory(repository))[SplashViewModel::class.java]

        splashViewModel.currentLoggedInUserLiveData.observe(this) {
            when (it) {
                is FirebaseResponse.Success -> {
                    if ((it.data != null) && it.data) {
                        Utils.printDebugLog("Currently_LoggedIn_User: Success (user is already logged in)")
                        navigate("MainActivity")
                    } else {
                        Utils.printDebugLog("Currently_LoggedIn_User: no user found")
                        navigate("SignInActivity")
                    }
                }

                is FirebaseResponse.Failure -> {
                    Utils.printErrorLog("Currently_LoggedIn_User: Failure ${it.exception}")
                    ExceptionHandler.handleException(this@SplashActivity, it.exception!!)
                }

                is FirebaseResponse.Loading -> {
                    Utils.printDebugLog("Currently_LoggedIn_User: Loading")
                }

            }
        }

    }

    private fun navigate(nextScreen: String) {
        lifecycleScope.launch {
            delay(1000) // 2 seconds delay for splash screen
            Utils.printDebugLog("Navigating_to: ${nextScreen}")
            if (nextScreen == "SignInActivity") {
                val intent = Intent(this@SplashActivity, SignInActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                val intent = Intent(this@SplashActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}