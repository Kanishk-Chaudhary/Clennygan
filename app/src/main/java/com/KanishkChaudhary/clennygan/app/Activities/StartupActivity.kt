package com.KanishkChaudhary.clennygan.app.Activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.KanishkChaudhary.clennygan.app.R

class StartupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_startup)
    }

    fun onLogin(v:View)
    {
       startActivity(LoginActivity.newIntent(this))
    }

    fun onSignUp(v:View)
    {
        startActivity(Signupactivity.newIntent(this))
    }

    companion object {

        fun newIntent(context: Context?) = Intent(context,StartupActivity::class.java)
    }
}