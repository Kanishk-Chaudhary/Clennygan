package com.KanishkChaudhary.clennygan.app.Activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.KanishkChaudhary.clennygan.app.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login2.*
import kotlinx.android.synthetic.main.activity_login2.emailET
import kotlinx.android.synthetic.main.activity_login2.passwordET
import kotlinx.android.synthetic.main.activity_signupactivity.*

class LoginActivity : AppCompatActivity() {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firebaseAuthListener = FirebaseAuth.AuthStateListener {
        val user = firebaseAuth.currentUser
        if (user != null) {
            startActivity(MainActivity.newIntent(this))
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login2)
    }

    override fun onStart() {
        super.onStart()
        firebaseAuth.addAuthStateListener(firebaseAuthListener)
    }

    override fun onStop() {
        super.onStop()
        firebaseAuth.removeAuthStateListener(firebaseAuthListener)
    }


    fun onLogin(v:View)
    {
        if(!emailET.text.toString().isNullOrEmpty()&&!passwordET.text.toString().isNullOrEmpty())
        {
            firebaseAuth.signInWithEmailAndPassword(emailET.text.toString(),passwordET.text.toString()).addOnCompleteListener{ task ->
                if(!task.isSuccessful)
                {
                    Toast.makeText(this,"Login Error ${task.exception?.localizedMessage}",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }




    companion object {

        fun newIntent(context: Context?) = Intent(context,LoginActivity::class.java)
    }
}